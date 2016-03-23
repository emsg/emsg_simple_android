
package com.vurtnewk.emsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * alarmManager
 */
public class HeartBeatReceiver extends BroadcastReceiver {

    public static int counter = 0;

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        counter++;
        try {
            EmsgClient.getInstance().getHeartBeatManager().sendHeartBeat();
        } catch (Exception e) {

        }
    }

}
