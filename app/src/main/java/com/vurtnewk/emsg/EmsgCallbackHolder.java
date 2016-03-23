
package com.vurtnewk.emsg;

import android.content.Context;
import android.os.Handler;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

class EmsgCallbackHolder {

   private ConcurrentHashMap<String, EmsgCallBack> mCallBackArrays;
   private final int LIMITSIZE = 50;
   private final long TEXTMSGTIMEOUT = 60 * 1000;
   private Handler mHandler;

   public EmsgCallbackHolder(Context mContext, Handler mHandler) {
       this.mHandler = mHandler;

       mCallBackArrays = new ConcurrentHashMap<String, EmsgCallBack>(LIMITSIZE);
   }

   public void addtoCollections(String id, EmsgCallBack mCallBack) {
       if (mCallBackArrays.size() == LIMITSIZE) {
           mCallBack.onError(EmsgCallBack.TypeError.SOCKETERROR);
       } else {
           mCallBackArrays.put(id, mCallBack);
       }
   }

   private EmsgCallBack removeEmsgCallBack(String id) {
       return mCallBackArrays.remove(id);
   }

   public EmsgCallBack onCallBackAction(String id) {
       return removeEmsgCallBack(id);
   }

   /**
    * if the map has no callback data check action not used
    */
   public void checkOutTime() {
       if (mCallBackArrays.size() == 0)
           return;
       checkoutTimeRunning();
   }

   private void checkoutTimeRunning() {
       Iterator<String> mKeyIterator = mCallBackArrays.keySet().iterator();
       while (mKeyIterator.hasNext()) {
           String mInteger = mKeyIterator.next();
           final EmsgCallBack mEmsgCallBack = mCallBackArrays.get(mInteger);
           if (mEmsgCallBack != null) {
               if (isTimeOut(mEmsgCallBack.mCallBackTime)) {
                   mCallBackArrays.remove(mInteger);
                   mHandler.post(new Runnable() {
                       @Override
                       public void run() {
                           mEmsgCallBack.onError(EmsgCallBack.TypeError.TIMEOUT);
                       }
                   });
               }
           }
       }
   }

   private boolean isTimeOut(long timeCreated) {
       return System.currentTimeMillis() - timeCreated > TEXTMSGTIMEOUT;
   }

}
