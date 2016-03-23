
package com.vurtnewk.emsg;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vurtnewk.emsg.EmsgCallBack.TypeError;
import com.vurtnewk.emsg.beans.DefPacket;
import com.vurtnewk.emsg.beans.DefPayload;
import com.vurtnewk.emsg.beans.DefProvider;
import com.vurtnewk.emsg.beans.EmsMessage;
import com.vurtnewk.emsg.beans.Envelope;
import com.vurtnewk.emsg.beans.IEnvelope;
import com.vurtnewk.emsg.beans.IPacket;
import com.vurtnewk.emsg.beans.IProvider;
import com.vurtnewk.emsg.beans.MessageInfoEntity;
import com.vurtnewk.emsg.beans.MsgSessionEntity;
import com.vurtnewk.emsg.beans.Pubsub;
import com.vurtnewk.emsg.client.asynctask.AbsFileServerTarget;
import com.vurtnewk.emsg.client.asynctask.IUpLoadTask;
import com.vurtnewk.emsg.client.asynctask.TaskCallBack;
import com.vurtnewk.emsg.client.asynctask.qiniu.QiNiuFileServerTarget;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsg.util.NetStateUtil;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.activities.MainActivity;
import com.vurtnewk.emsgdemo.eventbus.RefreshMsgSessionEvent;
import com.vurtnewk.emsgdemo.utils.ACache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.greenrobot.event.EventBus;

public class EmsgClient implements Define {

    public static final String EMSG_INFO_HOST = "EMSG_INFO_HOST";
    public static final String EMSG_INFO_PORT = "EMSG_INFO_PORT";
    public static final String EMSG_INFO_DOMAIN = "EMSG_INFO_DOMAIN";
    public static String ISCHAT = "";

    Vibrator vibrator;
    private BlockingQueue<String> heart_beat_ack = null;
    static MyLogger logger = new MyLogger(EmsgClient.class);
    private String jid = null;
    private String pwd = null;
    private String appKey = null;
    private volatile String heart = null;
    private Socket socket = null;
    protected InputStream reader = null;
    protected OutputStream writer = null;
    public PacketReader<DefPayload> packetReader = null;
    public PacketWriter packetWriter = null;
    protected PacketListener<DefPayload> listener = null;
    private IProvider<DefPayload> provider = null;
    private boolean auth = false; // 用于返回当前认证状态
    private boolean isClose = true; // 用于返回当前连接状态
    public volatile String reconnectSN = null;
    private Context mAppContext;
    public static EmsgClient mEmsgClient;
    public HeatBeatManager mHeartBeatManger;
    public AtomicBoolean isLogOut = new AtomicBoolean(true);// 用于主动发起的断线 不需要重练
    private EmsgCallbackHolder mCallBackHolder;
    private Handler mMainHandler;
    private AbsFileServerTarget mFileServerTarget;
    private final static Object mObject = new Object();
    private String EMSG_HOST;
    private String EMSG_PORT;
    private String notifistr;
    private String htid = null;
    private MessageInfoDaoImpl service;
    private boolean isNotify = false;


    /**
     * 用于获取EmsgClient 服务引擎对象 建议在主线程中操作
     */
    public static EmsgClient getInstance() {
        synchronized (mObject) {
            if (mEmsgClient == null) {
                mEmsgClient = new EmsgClient();
            }

        }
        return mEmsgClient;
    }

    /**
     * 初始化 emsg服务引擎相关参数
     *
     * @param mAppContext android上下文对象
     */
    public void init(Context mAppContext) {
        this.mAppContext = mAppContext;
        startBgService();
        mMainHandler = new Handler();
        mCallBackHolder = new EmsgCallbackHolder(mAppContext, mMainHandler);
        mFileServerTarget = new QiNiuFileServerTarget(mAppContext);

        service = new MessageInfoDaoImpl(mAppContext);// 消息数据库
    }

    private void startBgService() {
        try {
            mAppContext.startService(new Intent(mAppContext, EmsgService.class));
        } catch (Exception e) {
        }
    }

    public HeatBeatManager getHeartBeatManager() {
        return mHeartBeatManger;
    }

    private EmsgClient() {
        mHeartBeatManger = new HeatBeatManager();
        System.setProperty("emsg.packet.provider", DefProvider.class.getName());
        mEmsgClient = this;
        setProvider(new DefProvider());
        this.listener = new Receiver();
    }

    EmsgCallBack mAuthEmsgCallBack;

    /**
     * 登陆认证
     *
     * @param jid 用户账户
     * @param pwd 用户密码
     * @mEmsgCallBack 回调接口用于判断认证成功与否
     */
    public synchronized void auth(String jid, String pwd, EmsgCallBack mEmsgCallBack) {
        logger.info("我要登录  auth__jid=" + jid);
        if (isAuth()) {
            if (mEmsgCallBack != null)
                mEmsgCallBack.onSuccess();
            return;
        }
        if (jid != null && pwd != null) {
            this.jid = jid;
            this.pwd = pwd;
        }
        if (mEmsgCallBack != null) {
            this.mAuthEmsgCallBack = mEmsgCallBack;
        }
        try {
            initConnection();
        } catch (SocketTimeoutException e) {
            if (mEmsgCallBack != null) {
                mEmsgCallBack.onError(TypeError.TIMEOUT);
            }
        } catch (Exception e) {
            if (mEmsgCallBack != null) {
                mEmsgCallBack.onError(TypeError.SOCKETERROR);
            }
        }
    }

    private void setAppKey() throws NameNotFoundException {
        String mPackageName = mAppContext.getPackageName();
        ApplicationInfo mAppInfo = mAppContext.getPackageManager()
                .getApplicationInfo(mPackageName, PackageManager.GET_META_DATA);
        this.appKey = mAppInfo.metaData.getString("myMsg");
    }

    void setPacketListener(PacketListener<DefPayload> listener) {
        // this.listener = listener;
    }

    private void initConnection() throws IOException,
            InterruptedException {
        EMSG_HOST = ACache.get(mAppContext).getAsString(EMSG_INFO_HOST);
        EMSG_PORT = ACache.get(mAppContext).getAsString(EMSG_INFO_PORT);

        if (!TextUtils.isEmpty(EMSG_HOST) && !TextUtils.isEmpty(EMSG_PORT)) {
            this.socket = new Socket(EMSG_HOST, Integer.parseInt(EMSG_PORT));
            reconnectSN = null;
            isClose = false;
            isLogOut.set(false);
            initReaderAndWriter();
            openSession();
            mHeartBeatManger.heartbeatMonitor();
            if (mEmsStateCallBack != null) {
                mEmsStateCallBack.onEmsgOpenedListener();
            }
        }
    }

    private void openSession() throws InterruptedException {
        JsonObject j = new JsonObject();
        JsonObject envelope = new JsonObject();
        envelope.addProperty("id", UUID.randomUUID().toString());
        envelope.addProperty("type", MSG_TYPE_OPEN_SESSION);
        envelope.addProperty("jid", this.jid);
        envelope.addProperty("pwd", this.pwd);
        envelope.addProperty("appkey", this.appKey);
        j.add("envelope", envelope);
        String open_session_packet = j.toString();
        logger.info("open_session ::> " + open_session_packet);
        packetWriter.write(open_session_packet);
    }

    /**
     * 关闭Ems服务服务引擎
     */
    public void closeClient() {
        isLogOut.set(true);
        jid = null;
        shutdown("closeClient");
        stopEmsService();
    }

    public void reconnection(String reconnectSN) {
        this.reconnectSN = reconnectSN;
        try {
            logger.debug("reconnect_do_at_" + reconnectSN);
            loop("do", reconnectSN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initReaderAndWriter() throws IOException {
        reader = socket.getInputStream();
        writer = socket.getOutputStream();
        this.heart = UUID.randomUUID().toString();
        packetReader = new PacketReader<>(this);
        packetWriter = new PacketWriter(this.heart);
        heart_beat_ack = new ArrayBlockingQueue<>(2, true);
        new IOListener(this.heart);
        mHeartBeatManger.setHeartId(this.heart);
    }

    /**
     * 判断当前是否已经认证成功
     *
     * @return false当前未认证
     */
    public boolean isAuth() {
        return auth;
    }

    class IOListener extends PacketDecoder {
        Thread readThread = null;
        Thread writeThread = null;
        String _heart = null;

        IOListener(String _heart) {
            listenerRead();
            listenerWriter();
            this._heart = _heart;
        }

        void listenerRead() {
            readThread = new Thread() {
                public void run() {
                    int len = 0;
                    try {
                        byte[] buff = new byte[1024];
                        List<Byte> part = new ArrayList<Byte>();
                        while ((len = reader.read(buff)) != -1 && len != 0) {// 当远程流断开时，会返 0
                            logger.info("[" + _heart.equals(heart) + "][reader]" + "IOListener_heart=" + _heart + " ; EmsgClient_heart=" + heart);
                            List<Byte> list = parseBinaryList(buff, len);
                            List<String> packetList = new ArrayList<String>();
                            List<Byte> new_part = new ArrayList<Byte>();
                            splitByteArray(list, END_TAG, packetList, new_part,
                                    part);
                            for (int i = 0; i < packetList.size(); i++) {
                                String packet = packetList.get(i);
                                // dispach heart beat and message
                                if (HEART_BEAT.equals(packet)) {
                                    // 心跳单独处理
                                    logger.info("EMSG <=== HEART_BEAT" + packet);
                                    heart_beat_ack.poll();
                                } else if (SERVER_KILL.equals(packet)) {
                                    shutdown("READ_SERVER_KILL");
                                    isLogOut.set(true);
                                    runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mEmsStateCallBack.onAnotherClientLogin();
                                        }
                                    });

                                } else {
                                    packetReader.recv(packet);
                                }
                                part.clear();
                            }
                            if (new_part != null && new_part.size() > 0) {
                                for (byte pb : new_part) {
                                    part.add(pb);
                                }
                            }
                        }

                        if (!_heart.equals(heart)) {
                            return;
                        } else if (reader == null) {
                            return;
                        } else {
                            throw new Exception("EMSG_RETOME_SOCKET_CLOSED__READER__LEN=" + len);
                        }
                    } catch (Exception e) {
//                        logger.error("IOListener_read_error", e);
                        if (reader != null && !isLogOut.get())
                            logger.info("listenerRead_last_len=" + len);
                        reconnection("listenerRead");
                    }
                    logger.info("啊啊啊我死啦 [ " + Thread.currentThread().getName() + " ]");
                }
            };
            readThread.setName("IOListener__read__" + new Date());
            readThread.setDaemon(true);
            readThread.start();
        }

        void listenerWriter() {
            writeThread = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            if (packetWriter == null) return;
                            String msg = packetWriter.take(_heart);
                            if (KILL.equals(msg)) {
                                break;
                            }
                            if (!msg.endsWith(END_TAG)) {
                                msg = msg + END_TAG;
                            }
                            logger.debug("IOListener_writer socket_is_close="
                                    + isClose + " send_message ==> " + msg);
                            writer.write(msg.getBytes());
                            writer.flush();
                        }
                    } catch (Exception e) {
                        logger.error("IOListener_write_error", e);
                        reconnection("listenerWriter");
                    }

                    logger.info("啊啊啊我死啦 [ " + Thread.currentThread().getName() + " ]");
                }
            };
            writeThread.setName("IOListener__writer__" + new Date());
            writeThread.setDaemon(true);
            writeThread.start();
        }
    }

    private void send(IPacket<DefPayload> packet, EmsgCallBack mEmsgCallBack)
            throws InterruptedException {
        if (isClose && mEmsgCallBack != null) {
            mEmsgCallBack.onError(TypeError.SESSIONCLOSED);
            return;
        }
        if (packet.getEnvelope().getFrom() == null) {
            packet.getEnvelope().setFrom(this.jid);
        }
        String id = UUID.randomUUID().toString();
        packet.getEnvelope().setId(id);
        mEmsgCallBack.mCallBackTime = System.currentTimeMillis();
        mCallBackHolder.addtoCollections(id, mEmsgCallBack);
        String encode_message = getProvider().encode(packet);
        packetWriter.write(encode_message);
    }

    protected void send(String message) throws InterruptedException {
        if (packetWriter == null) return;
        packetWriter.write(message);
    }

    void shutdown(String foo) {
        logger.info("shutdown____" + foo);
        //mHeartBeatManger.stopSchduleHeartBeat();
        try {
            isClose = true;
            auth = false;
            if (packetReader != null) {
                packetReader.kill();
                packetReader = null;
            }
            if (packetWriter != null) {
                packetWriter.kill();
                packetWriter = null;
            }
            heart_beat_ack = null;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            logger.info("shutdown...");

            if (mEmsStateCallBack != null) {
                mEmsStateCallBack.onEmsgClosedListener();
            }

        } catch (Exception e1) {
        }
    }

    private int getHeartBeat() {
        return heartBeat;
    }

    public String getJid() {
        return jid;
    }

    IProvider<DefPayload> getProvider() {
        return provider;
    }

    void setProvider(IProvider<DefPayload> provider) {
        this.provider = provider;
    }

    /**
     * 判断当和服务器是否处于连接状态
     *
     * @return false 和服务器已经连接
     */
    public boolean isClose() {
        return isClose;
    }

    void setAuth(boolean auth) {
        this.auth = auth;
    }

    void startEmsService() {
        Intent mIntent = new Intent(mAppContext, EmsgService.class);
        mAppContext.startService(mIntent);
    }

    void stopEmsService() {
        Intent mIntent = new Intent(mAppContext, EmsgService.class);
        mAppContext.stopService(mIntent);
    }

    private static int reconnect_counter = 0;

    //必须保证同一时刻，只能有一个重连线程在工作
    static Lock reconnect_lock = new ReentrantLock();

    private synchronized void loop(final String cmd, final String reconnectSN) {
        logger.info("LOOP__拿到锁__开始执行:" + cmd);
        Thread loopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (reconnect_lock.tryLock()) {
                    shutdown(reconnectSN);
                    try {
                        //String cmd = null;
                        logger.debug("reconnect__1__cmd=" + cmd);
                        while (true) {
                            try {
                                logger.info("等 3 秒再开始重连，等待该死的线程死去");
                                Thread.sleep(3000);
                                //cmd = loop_queue.take();
                                if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
                                    break;
                                }
                                if (Define.KILL.equals(cmd)) {
                                    logger.info("reconnect_thread_shutdown");
                                    break;
                                }
                                if ("do".equals(cmd)) {
                                    ++reconnect_counter;
                                    logger.info("do_loop");
                                    initConnection();
                                    Thread.sleep(1000);
                                }
                                break;
                            } catch (Exception e) {
                                logger.debug("reconnect__2__cmd=" + cmd + " ; err=" + e.getMessage());
                                logger.error("重连异常", e);
                                try {
                                    int sleep = 0;
                                    if (reconnect_counter < 10) {
                                        sleep = 5000;
                                    } else if (reconnect_counter > 10 && reconnect_counter <= 20) {
                                        sleep = 10000;

                                    } else {
                                        sleep = getHeartBeat();
                                    }
                                    logger.debug("沉睡 " + sleep + " , 等待下次执行");
                                    Thread.sleep(sleep);
                                    //loop_queue.put("do");
                                    //loop();
                                } catch (InterruptedException e1) {
                                    logger.error("重连睡觉异常", e1);
                                }
                            }
                        }
                    } finally {
                        reconnect_lock.unlock();
                        logger.info("LOOP__执行完毕，无论如何释放锁...");
                    }
                } else {
                    logger.info("reconnection__没有拿到锁，不能执行重连");
                }
            }
        });
        loopThread.setName("EmsgClient_reconnect_loop__" + new Date());
        loopThread.start();


    }

    /**
     * manager the heartbeat use AlarmManager to setRepeat sendHeat data
     */
    class HeatBeatManager {
        private HeartBeatReceiver mHeartBeatReceiver;
        private PendingIntent mPendingIntent;
        private String _heart = null;

        public HeatBeatManager() {
        }

        public void setHeartId(String heart) {
            _heart = heart;
        }

        public void heartbeatMonitor() {
            final String _htid = "IOListener__heart_beat__" + new Date();
            htid = _htid;
            Thread heartbeatThread = new Thread() {
                public void run() {
                    //final String _lock = new String(_heart);
                    try {
                        //等待 alarmManager 执行结果
                        //Thread.sleep(getHeartBeat() * 2 + 3000);
                        //logger.info("如果 执行心跳超过两次，则证明一切正常，否则启动心跳线程");
                        //logger.info("alarmManager_heart_beat_counter=" + HeartBeatReceiver.counter);
                        if (HeartBeatReceiver.counter > 1) {
                            logger.info("alarmManager_success");
                            return;
                        } else {
                            HeartBeatReceiver.counter = 0;
                            stopSchduleHeartBeat();
                            logger.info("alarmManager_error::start_heart_beat_thread:[_heart" + _heart + "======heart" + heart + "]");
                            while (true) {
                                if (!htid.equals(_htid)) {
                                    break;
                                }
                                //logger.info("heart_beat_lock : " + _lock + " ; _heart=" + _heart);
//                                if (!_lock.equals(_heart)) {
//                                    logger.info("stop_for_restart_heart_beat_thread:[_lock" + _lock + "======heart" + heart + "]");
//                                    return;
//                                }
                                if (isAuth()) {
                                    getHeartBeatManager().sendHeartBeat();
                                }
                                if (isClose) {
                                    logger.info("[" + heart_beat_ack.size() + "] is_closed ~~~ ");
                                    return;
                                }
                                Thread.sleep(getHeartBeat());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("心跳异常", e);
                        //logger.info("heart_beat_error:" + e.getMessage() + " ; _lock=" + _lock);
                        //reconnection("heart_beat");
                    }
                    logger.info("啊啊啊我死啦 [ " + Thread.currentThread().getName() + " ] " + (htid.equals(_htid)));
                }
            };
            heartbeatThread.setName(_htid);
            heartbeatThread.setDaemon(true);
            heartbeatThread.start();
        }

//        @SuppressLint("NewApi")
//        public void schduleNextHeartbeat1() {
//            if (isLogOut.get())
//                return;
//            try {
//                AlarmManager mAlarmManager = (AlarmManager) mAppContext
//                        .getSystemService(Context.ALARM_SERVICE);
//                if (mHeartBeatReceiver == null) {
//                    mHeartBeatReceiver = new HeartBeatReceiver();
//                    mAppContext.registerReceiver(mHeartBeatReceiver,new IntentFilter("com.emsg.client"));
//                }
//                if (mPendingIntent == null) {
//                    Intent mIntent = new Intent("com.emsg.client");
//                    mPendingIntent = PendingIntent.getBroadcast(mAppContext, 0,
//                            mIntent, 0);
//                }
//                final long mCurrentTimeMin = System.currentTimeMillis();
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCurrentTimeMin, getHeartBeat
//                        (), mPendingIntent);
//
//            } catch (Exception e) {
//                logger.info("定时器===Exception" + e.toString());
//            }
//        }

        public void stopSchduleHeartBeat() {
            try {
                AlarmManager mAlarmManager = (AlarmManager) mAppContext
                        .getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.cancel(mPendingIntent);
                mAppContext.unregisterReceiver(mHeartBeatReceiver);
                mHeartBeatReceiver = null;
            } catch (Exception e) {
            }
        }

        public void sendHeartBeat() throws Exception {
            startBgService();
            try {
                if (isAuth()) {
                    heart_beat_ack.add("1");
                    send(HEART_BEAT);
                    mCallBackHolder.checkOutTime();
                }
                if (isClose) {
                    return;
                }
            } catch (Exception e) {
                reconnection("heart_beat");
                throw e;
            }
        }
    }

    private void runOnMainThread(Runnable mRunable) {
        if (mRunable != null)
            mMainHandler.post(mRunable);
    }

    private void runCallBackError(final EmsgCallBack mEmsgCallBack,
                                  final TypeError message) {
        if (mEmsgCallBack == null)
            return;
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                mEmsgCallBack.onError(message);
            }
        });
    }

    private void runCallBackSuccess(final EmsgCallBack mEmsgCallBack) {
        if (mEmsgCallBack == null)
            return;
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                mEmsgCallBack.onSuccess();
            }
        });
    }

    /**
     * the Reciver to reciver the data from emsg-server
     */
    public class Receiver implements PacketListener<DefPayload> {

        IProvider<DefPayload> provider = new DefProvider();

        @Override
        public void mediaPacket(IPacket<DefPayload> arg0) {
        }

        @Override
        public void processPacket(IPacket<DefPayload> packet) {
            Intent intent = new Intent();
            try {
                IEnvelope mEnveloper = packet.getEnvelope();
                int envolpeType = mEnveloper.getType();
                if (envolpeType == IEnvelope.TYPE_MESSAGE_SERVER) {// target
                    if (mEnveloper.getFrom().equals("server_ack")) {
                        String id = mEnveloper.getId();
                        runCallBackSuccess(mCallBackHolder.onCallBackAction(id));
                    }
                } else {
                    EmsMessage message = insertMessage(packet);
                    //将会话消息存到本地数据库
                    boolean b = insertSqliteMessage(message);
                    if (b) {
                        intent.setAction(EmsgConstants.MSG_ACTION_RECDATA);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", message);
                        intent.putExtras(bundle);
                        mAppContext.sendBroadcast(intent);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private EmsMessage insertMessage(IPacket<DefPayload> packet)
                throws Exception {
            String spacket = provider.encode(packet);
            IEnvelope envelope = packet.getEnvelope();
            EmsMessage message = new EmsMessage();
            message.setMid(envelope.getId());
            message.setmAccFrom(envelope.getFrom());
            message.setmAccTo(envelope.getTo());
            message.setGid(envelope.getGid());
            message.setType(envelope.getType());

//            message.setCt(System.currentTimeMillis());
            if (envelope.getCt() != null) {
                message.setCt(Long.parseLong(envelope.getCt()));
            }

            String contentType = EmsgConstants.MSG_TYPE_FILETEXT;
            if (packet.getPayload() != null) {
                Map<String, String> mExtendMap = packet.getPayload().getAttrs();
                message.setmExtendsMap(new HashMap<String, String>(mExtendMap));
                String mRecContentType = mExtendMap.get("Content-type");
                if (!TextUtils.isEmpty(mRecContentType))
                    contentType = mRecContentType;
                message.setContentType(contentType);
                message.setContentLength(packet.getPayload().getAttrs()
                        .get("Content-length"));
                String content = packet.getPayload().getContent();
                if (contentType.equals(EmsgConstants.MSG_TYPE_FILEIMG)) {
                    message.setContent(mFileServerTarget
                            .getImageUrlPath(content));
                } else if (contentType.equals(EmsgConstants.MSG_TYPE_FILEAUDIO)) {
                    message.setContent(mFileServerTarget
                            .getAudioUrlPath(content));
                } else {
                    message.setContent(content);
                }

            } else {
                message.setContent(spacket);
            }
            return message;
        }

        @Override
        public void sessionPacket(IPacket<DefPayload> packet) {
            if (packet.getEnvelope().getType() == 0) {

                if ("ok".equals(packet.getEntity().getResult())) {
                    if (mAuthEmsgCallBack != null) {
                        runCallBackSuccess(mAuthEmsgCallBack);
                        mAuthEmsgCallBack = null;
                    }
                } else {
                    if (mAuthEmsgCallBack != null)
                        runCallBackError(mAuthEmsgCallBack, TypeError.AUTHERROR);
                }
            }
        }

        @Override
        public void offlinePacket(List<IPacket<DefPayload>> packets) {
            try {
                for (IPacket<DefPayload> packet : packets) {
                    EmsMessage message = insertMessage(packet);
                    //将会话消息存到本地数据库

                    boolean b = insertSqliteMessage(message);
                    if (b) {
                        Intent intent = new Intent();
                        intent.setAction(EmsgConstants.MSG_ACTION_RECOFFLINEDATA);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", message);
                        intent.putExtras(bundle);
                        mAppContext.sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void pubsubPacket(Pubsub pubsub) {
        }
    }

    /**
     * 发送普通文本消息
     *
     * @param msgTo       消息发送给对方的账户
     * @param content     文本内容
     * @param mTargetType 消息类型枚举 SINGLECHAT 单聊，GROUPCHAT群聊
     * @param mCallBack   用于发送成功与否的回调
     */
    public void sendMessage(String msgTo, String content,
                            MsgTargetType mTargetType, EmsgCallBack mCallBack) {
        if (msgTo == null || mCallBack == null) {
            return;
        }
        if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
            runCallBackError(mCallBack, TypeError.NETERROR);
            return;
        }
        int type = (mTargetType == MsgTargetType.SINGLECHAT ? 1 : 2);
        IPacket<DefPayload> packet = new DefPacket(msgTo, content, type);

        try {
            send(packet, mCallBack);
        } catch (Exception e) {
            runCallBackError(mCallBack, TypeError.SOCKETERROR);
        }
    }

    public void sendMessageWithExtendMsg(String msgTo, String content,
                                         MsgTargetType mTargetType, EmsgCallBack mCallBack,
                                         Map<String, String> mExtendMap) {

        if (msgTo == null || mCallBack == null) {
            return;
        }
        if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
            runCallBackError(mCallBack, TypeError.NETERROR);
            return;
        }
        int type = (mTargetType == MsgTargetType.SINGLECHAT ? 1 : 2);

        IPacket<DefPayload> packet = new DefPacket(msgTo, content, type, 1,
                mExtendMap);
//        if(2 == type){
//            packet.getEnvelope().setGid(msgTo);
//        }
        try {
            send(packet, mCallBack);
        } catch (Exception e) {
            runCallBackError(mCallBack, TypeError.SOCKETERROR);
        }

    }

    public void sendMessageWithExtendMsgByGroup(String msgTo, String content,
                                                MsgTargetType mTargetType, EmsgCallBack mCallBack,
                                                Map<String, String> mExtendMap) {

        if (msgTo == null || mCallBack == null) {
            return;
        }
        if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
            runCallBackError(mCallBack, TypeError.NETERROR);
            return;
        }
        int type = (mTargetType == MsgTargetType.SINGLECHAT ? 1 : 2);

//        IPacket<DefPayload> packet = new DefPacket(msgTo, content, type, 1,
//                mExtendMap);


        IEnvelope envelope = new Envelope();
        envelope.setGid(msgTo);
        envelope.setType(2);
        envelope.setId(UUID.randomUUID().toString());

        DefPayload defPayload = new DefPayload();
        defPayload.setContent(content);
        defPayload.setAttrs(mExtendMap);
        IPacket<DefPayload> packet = new DefPacket(envelope, defPayload);

        try {
            send(packet, mCallBack);
        } catch (Exception e) {
            runCallBackError(mCallBack, TypeError.SOCKETERROR);
        }

    }

    /**
     * 使用sdk默认文件服务器进行音频文件的发送
     *
     * @param uri         图片文件对应的Uri
     * @param voiceDuring 音频文件时长
     *                    发送给对方的账号
     * @param mDataMap    用于消息扩展 (无则传null)
     * @param mTargetType 消息类型枚举 SINGLECHAT 单聊，GROUPCHAT群聊
     * @param mCallBack   用于消息发送成功与否的回调
     */
    public void sendAudioMessage(Uri uri, final int voiceDuring,
                                 final String msgTo, final Map<String, String> mDataMap,
                                 final MsgTargetType mTargetType, final EmsgCallBack mCallBack) {
        if (mCallBack == null) {
            return;
        }
        if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
            runCallBackError(mCallBack, TypeError.NETERROR);
            return;
        }
        IUpLoadTask task = mFileServerTarget.getUpLoadTask();
        task.upload(uri, new TaskCallBack() {

            @Override
            public void onSuccess(String message) {
                sendAudioTextMessage(mDataMap, voiceDuring, msgTo, mTargetType,
                        mCallBack, message);
            }

            @Override
            public void onFailure() {
                runCallBackError(mCallBack, TypeError.FILEUPLOADERROR);
            }
        });
    }

    /**
     * 使用sdk默认文件服务器进行图片文件的发送
     *
     * @param uri         图片文件对应的Uri
     * @param msgTo       发送给对方的账号
     * @param mDataMap    用于消息扩展 (无则传null)
     * @param mTargetType 消息类型枚举 SINGLECHAT 单聊，GROUPCHAT群聊
     * @param mCallBack   用于发送成功与否的回调
     */
    public void sendImageMessage(Uri uri, final String msgTo,
                                 final Map<String, String> mDataMap,
                                 final MsgTargetType mTargetType, final EmsgCallBack mCallBack) {

        if (mCallBack == null) {
            return;
        }
        if (!NetStateUtil.isNetWorkAlive(mAppContext)) {
            runCallBackError(mCallBack, TypeError.NETERROR);
            return;
        }
        IUpLoadTask task = mFileServerTarget.getUpLoadTask();
        task.upload(uri, new TaskCallBack() {

            @Override
            public void onSuccess(String message) {
                sendImageTextMessage(mDataMap, msgTo, mTargetType, mCallBack,
                        message);
            }

            @Override
            public void onFailure() {
                runCallBackError(mCallBack, TypeError.FILEUPLOADERROR);
            }
        });
    }

    /**
     * 自备文件服务器时发送语音相关信息
     *
     * @param mDataMap    用于消息扩展 (无则传null)
     * @param voiceDuring 音频文件时长
     * @param msgTo       发送给对方的账号
     * @param mTargetType 消息类型枚举 SINGLECHAT 单聊，GROUPCHAT群聊
     * @param mCallBack   用于消息发送成功与否的回调
     * @param content     发送的音频在文件服务器的相关信息用于对方接收到消息后的对音频文件的 下载
     */
    public void sendAudioTextMessage(Map<String, String> mDataMap,
                                     int voiceDuring, String msgTo, MsgTargetType mTargetType,
                                     EmsgCallBack mCallBack, String content) {
        Map<String, String> mExtendMap = null;
        if (mDataMap == null) {
            mExtendMap = new HashMap<String, String>();
        } else {
            mExtendMap = mDataMap;
        }
        mExtendMap.put("Content-type", EmsgConstants.MSG_TYPE_FILEAUDIO);
        mExtendMap.put("Content-length", String.valueOf(voiceDuring));
        String to = msgTo;
        int type = (mTargetType == MsgTargetType.SINGLECHAT ? 1 : 2);
        IPacket<DefPayload> packet = new DefPacket(to, content, type, 1,
                mExtendMap);
        try {
            send(packet, mCallBack);
        } catch (Exception e) {
            runCallBackError(mCallBack, TypeError.SOCKETERROR);
        }
    }


    /**
     * 自备文件服务器时发送图片相关信息
     *
     * @param mDataMap    用于消息扩展 (无则传null)
     * @param msgTo       发送给对方的账号
     * @param mTargetType 消息类型枚举 SINGLECHAT 单聊，GROUPCHAT群聊
     * @param mCallBack   用于消息发送成功与否的回调
     * @param content     发送的图片在文件服务器的相关信息用于对方接收到消息后的对图片文件的 下载
     */
    public void sendImageTextMessage(Map<String, String> mDataMap,
                                     String msgTo, MsgTargetType mTargetType, EmsgCallBack mCallBack,
                                     String content) {

        Map<String, String> mExtendMap = null;
        if (mDataMap == null) {
            mExtendMap = new HashMap<>();
        } else {
            mExtendMap = mDataMap;
        }
        mExtendMap.put("Content-type", EmsgConstants.MSG_TYPE_FILEIMG);
        String to = msgTo;
        int type = (mTargetType == MsgTargetType.SINGLECHAT ? 1 : 2);
        IPacket<DefPayload> packet = new DefPacket(to, content, type, 1,
                mExtendMap);
        try {
            send(packet, mCallBack);
        } catch (Exception e) {
            runCallBackError(mCallBack, TypeError.SOCKETERROR);
        }
    }

    public enum MsgTargetType {
        SINGLECHAT, GROUPCHAT;
    }

    EmsStateCallBack mEmsStateCallBack;

    /**
     * 设置对ems服务连接状态的监控
     *
     * @param mEmsClosedCallback 服务连接状态接口
     */
    public void setEmsStCallBack(EmsStateCallBack mEmsClosedCallback) {
        this.mEmsStateCallBack = mEmsClosedCallback;
    }

    /**
     * 服务连接状态接口(待扩展)  其他客户端登陆时连接断开提示
     */
    public interface EmsStateCallBack {
        void onAnotherClientLogin();

        void onEmsgClosedListener();

        void onEmsgOpenedListener();

    }

    /**
     * 会话消息 插入 本地数据库
     * 用来存储聊天消息等数据记录
     */
    private boolean insertSqliteMessage(EmsMessage message) {
        String from = message.getmAccFrom().split("@")[0];
        String str[] = {message.getmAccTo().split("@")[0], from};
        Arrays.sort(str);
        String sid = str[0] + str[1];

        //插入成功记录到数据库
        //构造聊天记录实体类
        MessageInfoEntity messageEntity = new MessageInfoEntity();
        messageEntity.setId(message.getMid());
        messageEntity.setSid(sid);
        messageEntity.setType(message.getType() + "");
        messageEntity.setMsg_state(true);//接受
        messageEntity.setJid(message.getmAccFrom());
        messageEntity.setMyjid(message.getmAccTo());
        messageEntity.setMsg_content(message.getContent());
        if (message.getmExtendsMap() != null) {
            messageEntity.setNickname(message.getmExtendsMap().get("messageFromNickName"));
            messageEntity.setMynickname(message.getmExtendsMap().get("messageToNickName"));
            messageEntity.setHeadurl(message.getmExtendsMap().get("messageFromHeaderUrl"));
            messageEntity.setMyheadurl(message.getmExtendsMap().get("messageToHeaderUrl"));
            messageEntity.setAttr(new Gson().toJson(message.getmExtendsMap()));

            //TODO 定义不太好
            if (!TextUtils.isEmpty(message.getmExtendsMap().get("action"))) {
                messageEntity.setType("100");//100为通知
            }
        }


        if (message.getType() == 1 && message.getmExtendsMap() != null && "delete".equals(message.getmExtendsMap().get("action"))) {
            service.DeleteMsgSession(null, sid);
            service.DeleteMsgInfo(null, sid);
            EventBus.getDefault().post(new RefreshMsgSessionEvent());
            return false;
        }

        /**
         * 判断当前界面是否处于聊天界面
         */
        if (TextUtils.equals(ISCHAT, message.getmAccFrom().split("@")[0])) {
            messageEntity.setMsg_readorno("1");//聊天界面设置当前消息为已读状态
        } else {
            messageEntity.setMsg_readorno("0");//非聊天界面设置当前消息未读
        }

        //设置当前时间会接受消息时间
        messageEntity.setMsg_time(getDate());

//        //消息为图片的时候 和ios端约定的接受字段名 messageImageUrlId
        if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageImageUrlId"))) {
            messageEntity.setMsg_imageUrlId(message.getmExtendsMap().get("messageImageUrlId"));
        }
        //消息为地理位置的时候  和ios端约定的接受字段名messageGeoLat  messageGeoLng
        if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageGeoLat"))) {
            messageEntity.setMsg_GeoLat(message.getmExtendsMap().get("messageGeoLat"));
        }
        if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageGeoLng"))) {
            messageEntity.setMsg_GeoLng(message.getmExtendsMap().get("messaqrgeGeoLng"));
        }
        messageEntity.setMsg_type(message.getmExtendsMap().get("messageType"));
        //将聊天记录插入数据库聊天记录表

        Boolean bool = service.AddMsgInfo(messageEntity);


        if (!bool) {
            return false;
        }
        //根据sid获取当前会话未读消息数目
        String num = service.getMsgNotReadNum(sid) + "";
        //构造消息会话列表实体类
        MsgSessionEntity sessionEntity = new MsgSessionEntity();
        sessionEntity.setSid(sid);
        sessionEntity.setJid(message.getmAccFrom());
        sessionEntity.setMyjid(message.getmAccTo());
        sessionEntity.setType(message.getType() + "");
        sessionEntity.setMsg_content(message.getContent());
        if (message.getmExtendsMap() != null) {
            sessionEntity.setAttr(new Gson().toJson(message.getmExtendsMap()));
            sessionEntity.setMsg_type(message.getmExtendsMap().get("messageType"));
            sessionEntity.setNickname(message.getmExtendsMap().get("messageFromNickName"));
            sessionEntity.setMynickname(message.getmExtendsMap().get("messageToNickName"));
            sessionEntity.setHeadurl(message.getmExtendsMap().get("messageFromHeaderUrl"));
            sessionEntity.setMyheadurl(message.getmExtendsMap().get("messageToHeaderUrl"));
            //TODO 定义不太好
            if (!TextUtils.isEmpty(message.getmExtendsMap().get("action"))) {
                sessionEntity.setType("100");//100为通知
            }
        }
        sessionEntity.setMsg_lasttime(getDate());
        //设置该会话的未读消息数目
        sessionEntity.setMsg_noread_num(num);
        //插入消息会话记录到会话记录表
        service.AddMsgSession(sessionEntity);
        //收到消息推送到通知栏
        /**
         * 判断当前界面是否处于聊天界面(不处于聊天界面则发送消息到通知栏)
         */

        if (!TextUtils.equals(ISCHAT, message.getmAccFrom().split("@")[0])) {//这里控制是否推送通知...待完善
            NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.mipmap.ic_launcher,
                    "您有一条新消息", System.currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Time t = new Time();
            t.setToNow(); //
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;
            int second = t.second;
            vibrator = (Vibrator) mAppContext.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400, 100, 400}; //
            vibrator.vibrate(pattern, -1);
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            notification.ledARGB = 0xff00ff00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            //设置通知栏点击 跳转的界面
            Intent notificationIntent = new Intent(mAppContext, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent contentIntent = PendingIntent.getActivity(mAppContext, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notifistr = message.getContent();
            if ("image".equals(message.getmExtendsMap().get("messageType"))) {
                notifistr = "[图片]";
            } else if ("audio".equals(message.getmExtendsMap().get("messageType"))) {
                notifistr = "[语音]";
            }
            if (TextUtils.isEmpty(messageEntity.getNickname())) {
                notification.setLatestEventInfo(mAppContext, message.getmAccFrom().split("@")[0], notifistr,
                        contentIntent);
            } else {
                notification.setLatestEventInfo(mAppContext, messageEntity.getNickname(), notifistr,
                        contentIntent);
            }
            notificationManager.notify(0, notification);
        }
        return true;
    }

    private String getDate() {
        return DateFormat.format("yyyy-MM-dd kk:mm", new Date()).toString();
    }


}
