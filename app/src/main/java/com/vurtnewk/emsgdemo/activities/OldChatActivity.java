package com.vurtnewk.emsgdemo.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.vurtnewk.emsg.EmsgCallBack;
import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsg.EmsgConstants;
import com.vurtnewk.emsg.beans.EmsMessage;
import com.vurtnewk.emsg.beans.MessageInfoEntity;
import com.vurtnewk.emsg.beans.MsgSessionEntity;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsg.ui.SoundMeter;
import com.vurtnewk.emsg.util.BitmapTools;
import com.vurtnewk.emsg.util.ExpandGridView;
import com.vurtnewk.emsg.util.ExpressionPagerAdapter;
import com.vurtnewk.emsg.util.ImageBase64;
import com.vurtnewk.emsg.util.ImageSizeTool;
import com.vurtnewk.emsg.util.SmileUtils;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.adapter.ChatMsgViewAdapter;
import com.vurtnewk.emsgdemo.adapter.ExpressionAdapter;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.CameraUtils;
import com.vurtnewk.emsgdemo.utils.FileUtil;
import com.vurtnewk.emsgdemo.utils.PicUtils;
import com.vurtnewk.emsgdemo.utils.VLog;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class OldChatActivity extends BaseActivity {

    //! 上传文件名称,由外部直接访问获得，判空处理
    public static String localTempImgFileName;
    //! 上传文件地址
    public static File mUploadFilePath;
    public static String uploadphotopath;
    long l;
    int picsize;
    //! 本地文件夹名称
    private static final String localTempImgDir = "emsg";
    protected static final int CAMERA_REQUEST_CODE = 26;
    protected static final int PHOTOS_REQUEST_CODE = 25;
    private List<MessageInfoEntity> mDataArrays = new ArrayList<>();
    private List<MessageInfoEntity> moremDataArrays = new ArrayList<>();
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ImageView iv_back;
    private ListView listView;
    private EditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private TextView tv_btn_press_to_speak;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private ImageView locationImgview;
    private View more;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private ChatMsgViewAdapter mAdapter;
    private Handler mHandler = new Handler();
    // 给谁发送消息
    private int voice_time;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private View rcChat_popup;
    private RelativeLayout edittext_layout;
    private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding, voice_rcd_hint_tooshort;
    private ImageView img1, sc_img1, volume;
    private LinearLayout del_re;
    private ProgressBar loadmorePB;
    private ImageView btn_picture, btn_take_picture;
    ImageView iv_info;
    private Button btnMore;
    private TextView name;
    private boolean isShosrt = false;
    private BaseApplication mApplication;
    private EmsgClient mEmsgClient;
    private Handler handler = null;
    private MessageInfoEntity entity = null;
    private final String[] accounts = {"aaa@test.com/123", "bbb@test.com/222"};
    private int flag = 1;
    private boolean btn_vocie = false;
    private long startVoiceT, endVoiceT;
    private String voiceName;
    private SoundMeter mSensor;
    private String to;
    private String jid;
    private String itsheadurl;
    private String itsname;
    private String sid;//从消息列表获取的sid
    private MessageInfoDaoImpl service;
    private UserInfo userinfo;
    private boolean isloading;
    private final int pagesize = 10;
    private boolean haveMoreData = true;
    public static final String IMAGEFILEPATH = "ImageFilePath";
    String emsgsendpath;//发送的文件夹路径
    private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    /**
     * 单聊为1，群聊为2
     */
    private String chatType = "1";
    //    ImageView mIvRightBtn;
    String messageGroupUrl = "";
    String domain;
    private Toolbar mToolbar;

    static {
        sWorkerThread.start();
    }

    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private void runOnWorkerThread(Runnable r) {
        sWorker.post(r);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mEventBus.register(this);
        mEmsgClient = BaseApplication.getInstance().getEmsgClient();
        // 创建属于主线程的handler
        handler = new Handler();
        setContentView(R.layout.activity_emsg_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //单聊-群聊
        chatType = getIntent().getStringExtra("chatType");
        if (TextUtils.isEmpty(chatType)) {
            chatType = "1";
        }
        //目标人的ID
        to = getIntent().getStringExtra("id") + "";
        EmsgClient.ISCHAT = to;
//        UserInfo user = (UserInfo) getIntent().getSerializableExtra("user");
//        if(user != null){
//            itsheadurl = user.getIcon();
//            itsname = user.getNickname();
//        }
        domain = ACache.get(this).getAsString(EmsgClient.EMSG_INFO_DOMAIN);
        userinfo = BaseApplication.getInstance().getUserInfo();
        String str[] = {userinfo.getId(), to};
        Arrays.sort(str);
        sid = str[0] + str[1];
        //jid
        jid = userinfo.getId() + "@" + domain;

        itsheadurl = getIntent().getStringExtra("headurl");
        itsname = getIntent().getStringExtra("name");
//        messageGroupUrl = getIntent().getStringExtra("messageGroupUrl");
        initView();
        //判断 activity被销毁后 有没有数据被保存下来
        if (savedInstanceState != null) {
            String savepicpath = savedInstanceState.getString(IMAGEFILEPATH);
            if (savepicpath != null) {
                mUploadFilePath = new File(savepicpath);
                if (mUploadFilePath.exists()) {
                    to = savedInstanceState.getString("to");
                    sid = savedInstanceState.getString("sid");
                    itsheadurl = savedInstanceState.getString("headurl");
                    setResult(Activity.RESULT_OK);
                } else {
                }
            }
        }
//        if (!TextUtils.isEmpty(to) && to.contains("/")) {
//            Constants.ISCHAT = to.split("/")[0];
//        } else {
//            Constants.ISCHAT = to;
//        }
        service = new MessageInfoDaoImpl(this);// 消息数据库
        registerMessageReciver();
        loadChatHistoryRecord(sid);
    }


    /**
     * initView
     */
    protected void initView() {
        userinfo = BaseApplication.getInstance().getUserInfo();
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSensor = new SoundMeter();
        btn_take_picture = (ImageView) findViewById(R.id.btn_take_picture);
        btn_picture = (ImageView) findViewById(R.id.btn_picture);
        rcChat_popup = this.findViewById(R.id.rcChat_popup);
        name = (TextView) findViewById(R.id.mToolbarTitle);
        name.setText(itsname);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        volume = (ImageView) this.findViewById(R.id.volume);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        tv_btn_press_to_speak = (TextView) findViewById(R.id.tv_btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        del_re = (LinearLayout) this.findViewById(R.id.del_re);
        voice_rcd_hint_rcding = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_rcding);
        voice_rcd_hint_loading = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_loading);
        voice_rcd_hint_tooshort = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_tooshort);
//        mIvRightBtn = (ImageView) findViewById(R.id.mIvRightBtn);
        more = findViewById(R.id.more);
        img1 = (ImageView) this.findViewById(R.id.img1);
        sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
        reslist = getExpressionRes(35);
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        iv_emoticons_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.INVISIBLE);
                iv_emoticons_checked.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                emojiIconContainer.setVisibility(View.VISIBLE);
                hideKeyboard();
            }
        });

        // 语音文字切换按钮
        iv_emoticons_checked.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(mEditTextContent.getText().toString(), "text", "", "", "");
            }
        });
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        mEditTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                // edittext_layout.setBackground(null);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images
                        .Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PHOTOS_REQUEST_CODE);
            }
        });
        btn_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 拍照获取图片
                 */
                selectPicFromCamera();
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 拍照获取图片
     */
    public void selectPicFromCamera() {
        // 先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory() + "/" +
                        localTempImgDir);
                if (!dir.exists())// 不存在则创建
                    dir.mkdirs();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                localTempImgFileName = new Long(new Date().getTime()).toString();
                mUploadFilePath = new File(dir, localTempImgFileName);
                Uri u = Uri.fromFile(mUploadFilePath);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext, "没有储存卡", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * @brief 图片处理任务
     */
    private class CompressPicTask extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... params) {
            // 如果相机拍照时候屏幕被反转，需要转换照片的角度，异步线程处理
            CameraUtils.saveBitmap(mUploadFilePath);
            return mUploadFilePath;
        }

        @Override
        protected void onPostExecute(File file) {
            final String path = file.getAbsolutePath();
            uploadphotopath = path;
            updateUserPhoto("file", null, path);
        }
    }

    private String getDate() {
        return DateFormat.format("yyyy-MM-dd kk:mm", new Date()).toString();
    }

    /**
     * 处理三星手机拍照转屏销毁activity的问题，保存需要的数据
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUploadFilePath != null) {
            outState.putString("ImageFilePath", mUploadFilePath.getAbsolutePath());
            outState.putString("to", to);
            outState.putString("sid", sid);
            outState.putString("headurl", itsheadurl);
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (more.getVisibility() == View.GONE) {
            System.out.println("more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 获得表情名称
     *
     * @param getSum
     * @return
     */
    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }


    /**
     * 按下语音录制按钮时
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
            return false;
        }
        if (btn_vocie) {
            int[] location = new int[2];
            buttonPressToSpeak.getLocationInWindow(location); // 获取在当前窗口内的绝
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int b1 = location[0] + buttonPressToSpeak.getWidth();
            int b2 = location[1] + buttonPressToSpeak.getHeight();
            int[] del_location = new int[2];
            del_re.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
                    return false;
                }
                emsgsendpath = android.os.Environment.getExternalStorageDirectory() +
                        "/yueqiu/emsg/send";
                File destDir = new File(emsgsendpath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                System.out.println("2");
                if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X && event.getY() < b2 && event.getX() < b1) {// 判断手势按下的位置是否是语音录制按钮的范围内
                    System.out.println("3");
                    tv_btn_press_to_speak.setText("正在说话");
                    rcChat_popup.setVisibility(View.VISIBLE);
                    voice_rcd_hint_loading.setVisibility(View.VISIBLE);
                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    voice_rcd_hint_tooshort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                voice_rcd_hint_loading.setVisibility(View.GONE);
                                voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    startVoiceT = System.currentTimeMillis();
                    voiceName = startVoiceT + ".amr";
                    start(voiceName);
                    flag = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成

                if (event.getY() >= del_Y && event.getY() <= del_Y + del_re.getHeight() && event
                        .getX() >= del_x && event.getX() <= del_x + del_re.getWidth()) {
                    rcChat_popup.setVisibility(View.GONE);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    tv_btn_press_to_speak.setText("按住说话");
                    stop();
                    flag = 1;
                    File file = new File(emsgsendpath + "/" + voiceName);
                    Log.d("debug", file.getAbsolutePath());
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    tv_btn_press_to_speak.setText("按住说话");
                    stop();
                    endVoiceT = System.currentTimeMillis();
                    flag = 1;
                    voice_time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (voice_time < 1) {
                        isShosrt = true;
                        voice_rcd_hint_loading.setVisibility(View.GONE);
                        voice_rcd_hint_rcding.setVisibility(View.GONE);
                        voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                voice_rcd_hint_tooshort.setVisibility(View.GONE);
                                rcChat_popup.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        return false;
                    }
                    File file = new File(emsgsendpath + "/" + voiceName);
                    if (name.getText().toString() != null) {
                        final String voicepath = file.getAbsolutePath();
                        uploadphotopath = voicepath;
                        updateAmr("file", voicepath);
                    }
                    rcChat_popup.setVisibility(View.GONE);
                }
            }
            if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
                Animation mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc2);
                img1.setVisibility(View.GONE);
                del_re.setVisibility(View.VISIBLE);
                del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
                if (event.getY() >= del_Y && event.getY() <= del_Y + del_re.getHeight() && event
                        .getX() >= del_x && event.getX() <= del_x + del_re.getWidth()) {
                    del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
                    sc_img1.startAnimation(mLitteAnimation);
                    sc_img1.startAnimation(mBigAnimation);
                }
            } else {
                img1.setVisibility(View.VISIBLE);
                del_re.setVisibility(View.GONE);
                del_re.setBackgroundResource(0);
            }
        }
        return super.onTouchEvent(event);
    }

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private static final int POLL_INTERVAL = 300;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };


    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        volume.setImageResource(R.drawable.amp1);
    }

    private void updateDisplay(double signalEMA) {
        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);
                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        btn_vocie = true;
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mEventBus.unregister(this);
        unregisterReceiver(receiver);
        EmsgClient.ISCHAT = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }


    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.vurtnewk.emsg.util.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(OldChatActivity
                                    .this, (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {
                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i,
                                                    selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete
                                                    (selectionStart - 1, selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart
                                                - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            entity = new MessageInfoEntity();
            EmsMessage message = (EmsMessage) mIntent.getParcelableExtra("message");
            if (message == null)
                return;
            //收到的是单聊
            if ("1".equals(message.getType() + "") && !message.getmAccFrom().split("@")[0].equals(to.split("@")[0])) {//并且当前不能是群聊
                return;
            }
            //
            if ("2".equals(message.getType() + "") && (message.getGid() == null || !message.getGid().equals(sid))) {
                return;
            }
            if (!chatType.equals(message.getType() + "")) {
                return;
            }
            ////fixed by lei
            int type = message.getType();
            if (type == 101 || type == 108) {
                return;
            }
            entity.setType(type + "");
            entity.setMsg_time(getDate());
            if (message.getmExtendsMap() != null) {
                entity.setMsg_type(message.getmExtendsMap().get("messageType"));
                //消息为图片的时候 和ios端约定的接受字段名 messageImageUrlId
                if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageImageUrlId"))) {
                    entity.setMsg_imageUrlId(message.getmExtendsMap().get("messageImageUrlId"));
                }

                //消息为地理位置的时候  和ios端约定的接受字段名messageGeoLat  messageGeoLng
                if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageGeoLat"))) {
                    entity.setMsg_GeoLat(message.getmExtendsMap().get("messageGeoLat"));
                }
                if (!TextUtils.isEmpty(message.getmExtendsMap().get("messageGeoLng"))) {
                    entity.setMsg_GeoLng(message.getmExtendsMap().get("messageGeoLng"));
                }

                entity.setVoice_time(message.getmExtendsMap().get("messageAudioTime"));//语音时间
            }
            entity.setHeadurl(itsheadurl);
            if (message.getmExtendsMap() != null && !TextUtils.isEmpty(message.getmExtendsMap().get("messageFromNickName"))) {
                entity.setNickname(message.getmExtendsMap().get("messageFromNickName"));
                entity.setHeadurl(message.getmExtendsMap().get("messageFromHeaderUrl"));
            } else {
                entity.setNickname(message.getmAccFrom().split("@")[0]);
            }
            entity.setMsg_state(true);
            entity.setMsg_content(message.getContent());
            mDataArrays.add(entity);//广播
            handler.post(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    listView.setSelection(listView.getCount() - 1);
                }
            });
        }

    };

    private void mDataArrays() {
        if (mDataArrays != null && mDataArrays.size() > 1) {
            String msg_time = mDataArrays.get(0).getMsg_time();
        }
    }

    private void registerMessageReciver() {
        IntentFilter mIntentFiter = new IntentFilter();
        // 注册即时消息接收广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECDATA);
        // 接收离线消息广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECOFFLINEDATA);
        // 接收消息服务开启广播即session连接成功
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_SESSONOPENED);
        // 对应的上下文对象
        registerReceiver(receiver, mIntentFiter);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
//        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams
//                .SOFT_INPUT_STATE_HIDDEN) {
//            if (getCurrentFocus() != null)
//                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);
//        }
        InputMethodManager imm = null;
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        }
        View view = getWindow().getDecorView();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void showError(String message) {
        VToast.showShortToast(mContext, message);
    }


    public void onSuccess(String requestCode, String result, int statusCode) {

    }

    public void onFailure(String requestCode, String error, int statusCode) {

    }

    /**
     * 上传到文件服务器的回调（返回文件的url地址）
     *
     * @param fileurl
     */
    public void uploadFileResult(final String fileurl) {
        File dF = new File(uploadphotopath);
        ImageSizeTool imageSizeTool = new ImageSizeTool();
        try {
            l = imageSizeTool.getFileSizes(dF);
            picsize = imageSizeTool.FormentFileSize(l);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            uploadphotopath = BitmapTools.getThumbUploadPath(uploadphotopath, 100);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        File f2 = new File(uploadphotopath);
        long l2 = 0;
        try {
            l2 = imageSizeTool.getFileSizes(f2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int picsize2 = imageSizeTool.FormentFileSize(l2);
        //str为base64编码之后的字符串
        final String str = ImageBase64.getImageBinary(uploadphotopath);
        if (dF.getAbsolutePath().contains("amr"))//判断语音
        {
            send(fileurl, "audio", fileurl, "", "");
        } else {
            send(str, "image", fileurl, "", "");
        }
    }

    public void deleteChatRecord() {
        mDataArrays.clear();
        mAdapter.notifyDataSetChanged();
    }


    public void loadMoreRecordChat(List<MessageInfoEntity> list) {
        moremDataArrays = list;
        mDataArrays.addAll(0, list);//加载历史
        mAdapter.notifyDataSetChanged();
    }

    public void loadConversationsWithRecentChat(List<MessageInfoEntity> list) {
        mDataArrays = list;
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        listView.setOnScrollListener(new ListScrollListener());
        listView.setAdapter(mAdapter);
        listView.setSelection(listView.getCount() - 1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //相机拍照
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK && mUploadFilePath != null) {
                    new CompressPicTask().execute();
                }
                break;
            //相册选取
            case PHOTOS_REQUEST_CODE:
                if (resultCode == RESULT_OK && null != data) {
                    Uri imageUri = data.getData();
                    uploadphotopath = FileUtil.getRealFilePath(mContext, imageUri);
                    updateUserPhoto("file", data, null);
                }
                break;
        }
    }

    /**
     * EMSG 聊天消息发送方法
     *
     * @param str               消息内容（text类型为文本内容，image类型为base64转码后的字符串）
     * @param messageType       “text”文本类型 “image”图片类型 “audio”语音类型 “geo”地理位置类型
     * @param messageImageUrlId 图片/文件 上传到文件服务器得到的文件URL
     * @param messageGeoLat     地理位置坐标
     * @param messageGeoLng
     */
    private void send(String str, final String messageType, final String messageImageUrlId, final
    String messageGeoLat, final String messageGeoLng) {
        if (mEmsgClient == null) {
            return;
        }
        if (TextUtils.isEmpty(jid) || mEmsgClient.isClose()) {
            showError("数据异常，请重新登录");
            return;
        }
        ////fixed by lei
//        boolean exist = service.getMsgSessionByType("101", sid);
//        if (exist) {//如果存在这条消息，就不是好友，不能继续发送消息
//            showToastShort("你们不是好友，不能使用聊天功能...");
//            return;
//        }
//        if (!NetWorkUtil.isAnyNetConnected()) {
//            showToastShort("当前网络不可用，请检查网络");
//            return;
//        }
        ////
        if (mEmsgClient != null) {
            final String contString = str;
            if (contString.length() > 0) {
                try {
                    //可扩展的消息发送内容（需和ios端统一字段）
                    final Map<String, String> mExtendMap = new HashMap<String, String>();
                    if ("1".equals(chatType)) {
                        if (userinfo != null) {
                            if (!TextUtils.isEmpty(userinfo.getNickname())) {
                                //发送消息的用户昵称
                                mExtendMap.put("messageFromNickName", userinfo.getNickname());
                            }
                            if (!TextUtils.isEmpty(userinfo.getIcon())) {
                                //发送消息的用户头像
                                mExtendMap.put("messageFromHeaderUrl", userinfo.getIcon());
                            }
                            if (!TextUtils.isEmpty(userinfo.getGender())) {
                                //发送消息的用户性别
                                mExtendMap.put("messageFromSex", userinfo.getGender());
                            }
                            if (!TextUtils.isEmpty(userinfo.getBirthday())) {
                                //发送消息的用户年龄
                                mExtendMap.put("messageFromAge", userinfo.getBirthday());
                            }
                        }
                    }
                    final String msgTo = to + "@" + domain;
                    //消息类型判断
                    if (!TextUtils.isEmpty(messageType) && "text".equals(messageType)) {
                        mExtendMap.put("messageType", messageType);//文本类型
                    } else if (!TextUtils.isEmpty(messageType) && "image".equals(messageType)) {
                        mExtendMap.put("messageType", messageType);//图片类型
                        mExtendMap.put("messageImageUrlId", messageImageUrlId);
                    } else if (!TextUtils.isEmpty(messageType) && "audio".equals(messageType)) {
                        mExtendMap.put("messageType", messageType);//语音类型
                        mExtendMap.put("messageImageUrlId", messageImageUrlId);
                        mExtendMap.put("messageAudioTime", String.valueOf(voice_time));
                    } else if (!TextUtils.isEmpty(messageType) && "geo".equals(messageType)) {
                        mExtendMap.put("messageType", messageType);//地理位置类型
                        mExtendMap.put("messageGeoLat", messageGeoLat);
                        mExtendMap.put("messageGeoLng", messageGeoLng);
                    }
                    runOnWorkerThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("1".equals(chatType)) {
                                mEmsgClient.sendMessageWithExtendMsg(msgTo, contString,
                                        EmsgClient.MsgTargetType.SINGLECHAT, new EmsgCallBack() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError(TypeError mTypeError) {
                                            }
                                        }, mExtendMap);

                            }
                            //插入成功记录到数据库
                            MessageInfoEntity msgentity = new MessageInfoEntity();
                            String str[] = {jid.split("@")[0], to.split("@")[0]};
                            Arrays.sort(str);
                            String sid = str[0] + str[1];
                            msgentity.setSid(sid);
                            msgentity.setType(chatType);//单聊
                            msgentity.setMsg_state(false);//收发消息判断：false 为我发出去的消息 true为接收到的消息
                            msgentity.setMyjid(jid);
                            msgentity.setJid(to + "@" + domain);
                            msgentity.setMynickname(userinfo.getNickname());
                            //fixed
                            msgentity.setNickname(itsname);//对方昵称
                            msgentity.setMyheadurl(userinfo.getIcon());
                            msgentity.setHeadurl(itsheadurl);//对方头像
                            if (messageType.equals("audio"))//判断语音
                            {
                                msgentity.setMsg_content(uploadphotopath);
                                msgentity.setVoice_time(String.valueOf(voice_time));
                            } else {
                                msgentity.setMsg_content(contString);
                            }

                            msgentity.setMsg_type(messageType);
                            msgentity.setMsg_readorno("1");//已读
                            msgentity.setMsg_time(getDate());
                            msgentity.setMsg_GeoLat(messageGeoLat);
                            msgentity.setMsg_GeoLng(messageGeoLng);
                            msgentity.setMsg_imageUrlId(messageImageUrlId);
                            Boolean bool = service.AddMsgInfo(msgentity);
                            String num = service.getMsgNotReadNum(msgentity.getSid()) + "";
                            MsgSessionEntity sessionEntity = new MsgSessionEntity();
                            sessionEntity.setSid(sid);
                            sessionEntity.setType(chatType);//单聊
                            sessionEntity.setMyjid(jid);
                            sessionEntity.setJid(to + "@" + domain);
                            sessionEntity.setMynickname(userinfo.getNickname());
                            sessionEntity.setNickname(itsname);//对方昵称
                            sessionEntity.setMyheadurl(userinfo.getIcon());
                            sessionEntity.setHeadurl(itsheadurl);//对方头像
                            sessionEntity.setMsg_content(contString);
                            sessionEntity.setMsg_type(messageType);
                            sessionEntity.setMsg_lasttime(getDate());
                            sessionEntity.setMsg_noread_num(num);
                            sessionEntity.setAttr("");
                            service.AddMsgSession(sessionEntity);

                            mDataArrays.add(msgentity);//发送
                            handler.post(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    listView.setSelection(listView.getCount() - 1);
                                    mEditTextContent.setText("");
                                }
                            });
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 分页加载数据（上拉刷新）
     */
    private class ListScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                        loadmorePB.setVisibility(View.VISIBLE);
                        try {
                            loadMoreHistoryRecord(sid, mDataArrays.size(), pagesize);
                        } catch (Exception e1) {
                            handler.post(new Runnable() {
                                public void run() {
                                    loadmorePB.setVisibility(View.GONE);
                                }
                            });
                            return;
                        }
                        try {

                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            handler.post(new Runnable() {
                                public void run() {
                                    loadmorePB.setVisibility(View.GONE);
                                }
                            });
                            e.printStackTrace();
                        }
                        if (moremDataArrays.size() != 0) {
                            handler.post(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    listView.setSelection(moremDataArrays.size() - 1);
                                }
                            });
                            if (moremDataArrays.size() != pagesize)
                                haveMoreData = false;
                        } else {
                            haveMoreData = false;
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                loadmorePB.setVisibility(View.GONE);
                            }
                        });
                        isloading = false;

                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
                totalItemCount) {
        }
    }

    public void loadChatHistoryRecord(String sid) {
        List<MessageInfoEntity> list = new ArrayList<MessageInfoEntity>();
        UserInfo user = BaseApplication.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(user.getId())) {
//            service.listMessageInfo(null,sid,"1");
            List<Map<String, String>> listmessageinfo = service.listMessageInfo(null, sid, "1", 0, 10);
            Boolean bool = service.updateMsgReadOrNo(sid);
            if (listmessageinfo != null) {
                if (!listmessageinfo.isEmpty()) {
                    for (int i = 0; i < listmessageinfo.size(); i++) {
                        MessageInfoEntity entity = new MessageInfoEntity();
                        entity.setMsg_content(listmessageinfo.get(i).get("msg_content"));
                        entity.setMsg_time(listmessageinfo.get(i).get("msg_time"));
//                        entity.setMyheadurl(listmessageinfo.get(i).get("myheadurl"));
//                        entity.setMynickname(listmessageinfo.get(i).get("mynickname"));
                        //todo
                        entity.setMyheadurl(user.getIcon());
                        entity.setMynickname(user.getNickname());

                        entity.setMyjid(listmessageinfo.get(i).get("myjid"));
                        entity.setHeadurl(listmessageinfo.get(i).get("headurl"));
                        entity.setJid(listmessageinfo.get(i).get("jid"));
                        entity.setMsg_type(listmessageinfo.get(i).get("msg_type"));
                        entity.setNickname(listmessageinfo.get(i).get("nickname"));
                        entity.setSid(listmessageinfo.get(i).get("sid"));
                        entity.setMsg_imageUrlId(listmessageinfo.get(i).get("msg_imageUrlId"));
                        entity.setMsg_GeoLat(listmessageinfo.get(i).get("msg_GeoLat"));
                        entity.setMsg_GeoLng(listmessageinfo.get(i).get("msg_GeoLng"));
                        entity.setVoice_time(listmessageinfo.get(i).get("voice_time"));
                        if (listmessageinfo.get(i).get("msg_state").equals("true")) {
                            entity.setMsg_state(true);
                        } else {
                            entity.setMsg_state(false);
                        }
                        list.add(entity);
                    }
                }
            }
        }
        loadConversationsWithRecentChat(list);
    }

    public void loadMoreHistoryRecord(String sid, int start, int end) {
        {

            List<MessageInfoEntity> list = new ArrayList<MessageInfoEntity>();
            UserInfo user = BaseApplication.getInstance().getUserInfo();
            if (!TextUtils.isEmpty(user.getId())) {
                List<Map<String, String>> listmessageinfo = service.listMessageInfo(null, sid, "1", start, end);
                Boolean bool = service.updateMsgReadOrNo(sid);
                if (listmessageinfo != null) {
                    if (!listmessageinfo.isEmpty()) {
                        for (int i = 0; i < listmessageinfo.size(); i++) {
                            MessageInfoEntity entity = new MessageInfoEntity();
                            entity.setMsg_content(listmessageinfo.get(i).get("msg_content"));
                            entity.setMsg_time(listmessageinfo.get(i).get("msg_time"));

//                            entity.setMyheadurl(listmessageinfo.get(i).get("myheadurl"));
//                            entity.setMynickname(listmessageinfo.get(i).get("mynickname"));

                            entity.setMyheadurl(user.getIcon());
                            entity.setMynickname(user.getNickname());

                            entity.setMyjid(listmessageinfo.get(i).get("myjid"));
                            entity.setHeadurl(listmessageinfo.get(i).get("headurl"));
                            entity.setJid(listmessageinfo.get(i).get("jid"));
                            entity.setMsg_type(listmessageinfo.get(i).get("msg_type"));
                            entity.setNickname(listmessageinfo.get(i).get("nickname"));
                            entity.setSid(listmessageinfo.get(i).get("sid"));
                            entity.setMsg_imageUrlId(listmessageinfo.get(i).get("msg_imageUrlId"));
                            entity.setMsg_GeoLat(listmessageinfo.get(i).get("msg_GeoLat"));
                            entity.setMsg_GeoLng(listmessageinfo.get(i).get("msg_GeoLng"));
                            entity.setVoice_time(listmessageinfo.get(i).get("voice_time"));
                            if (listmessageinfo.get(i).get("msg_state").equals("true")) {
                                entity.setMsg_state(true);
                            } else {
                                entity.setMsg_state(false);
                            }
                            list.add(entity);
                        }
                    }
                }
            }
            loadMoreRecordChat(list);
        }
    }

    public void updateUserPhoto(final String fields, final Intent data, final String filePath) {
        //先进行压缩
        if (data != null || !TextUtils.isEmpty(filePath)) {
            // 压缩图片
            boolean isCompress = PicUtils.compressToLocal(data, filePath, OldChatActivity.this, 600, 600, "upload.jpg");
            if (isCompress) {// 通知主线程上传已压缩文件
//                        File f = new File(Environment.getExternalStorageDirectory(), "/yueqiu/upload.jpg");
                final File f = new File(SettingsConstants.upLoadFileDir.getAbsolutePath(), "/upload.jpg");
//                        EventBus.getDefault().post(new UpdateUserImage(fields, f));
                uploadEmsgChatFile(fields, 1, f);
            } else {
                showError("压缩文件失败");
            }
//            new Thread() {
//                public void run() {
//
//
//                }
//            }.start();
        }
    }

    public void updateAmr(final String amr, final String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File f = new File(filePath);
            uploadEmsgChatFile(amr, 1, f);
//            new Thread() {
//                public void run() {
//
////                    EventBus.getDefault().post(new UpdateUserImage(amr, f));
//
//                }
//            }.start();

        }
    }

    public void uploadEmsgChatFile(String field, int type, final File... files) {
        if (files == null || field == null)
            return;
        if (files != null) {
            try {
                RequestParams uploadparams = new RequestParams();
                uploadparams.put(field, files);
                uploadparams.put("appid", "test");
                uploadparams.put("appkey", "83bf20e2b20141e098fa6b721f693163");
                if (files[0].getAbsolutePath().toString().contains("amr")) {//判断语音
                    uploadparams.put("file_type", "audio");
                } else {
                    uploadparams.put("file_type", "image");
                }
                uploadparams.setForceMultipartEntityContentType(true);
                HttpClient.postFile(UrlConstants.BASE_FILE_URL_UPLOAD, uploadparams, new HttpListener() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject o = null;
                        try {
                            o = new JSONObject(result);
                            JSONObject entity = o.optJSONObject("entity");
                            String photoid = entity.optString("id");
                            if(!TextUtils.isEmpty(photoid)){
                                uploadFileResult(UrlConstants.BASE_FILE_URL_GET + photoid);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        VLog.e(TAG, "error:" + error);
                    }

                    @Override
                    public void onStart() {
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
