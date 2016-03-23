
package com.vurtnewk.emsg;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.vurtnewk.emsg.util.NetStateUtil;


public class EmsgService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBrodCastReciver = new EmsgNetWorkBrodCastReciver();
        registerNetworkReceiver();
        //EmsgClient.getInstance().mHeartBeatManger.heartbeatMonitor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isEmsgLoginOut())
            return START_NOT_STICKY;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isEmsgLoginOut()) {
            return;
        } else {
            EmsgClient.getInstance().startEmsService();
        }
        unRegisterNetworkReceiver();
    }

    private boolean isEmsgLoginOut() {
        return EmsgClient.getInstance().isLogOut.get();
    }

    EmsgNetWorkBrodCastReciver mBrodCastReciver;

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mBrodCastReciver, filter);
    }

    private void unRegisterNetworkReceiver() {
        this.unregisterReceiver(mBrodCastReciver);
    }

    /**
     * the BroadCast for emsg reciver monitoring the net state before use in app
     * and need login again. and in this verison we put the logic in our library
     *
     * @author john
     */
    class EmsgNetWorkBrodCastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            if (NetStateUtil.isNetWorkAlive(mContext)) {
                try {
                    if (!isEmsgLoginOut()) {
                        EmsgClient.getInstance().reconnectSN = null;
                        EmsgClient.getInstance().reconnection("EmsgNetWorkBrodCastReciver");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
