package com.baidu.push.example;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by blin on 2015/10/22.
 */
public class AsyncSend {
    private BaiduPush mBaiduPush;
    private String mMessage;
    private Handler mHandler;
    private MyAsyncTask mTask;
    private String mUserId;
    private OnSendScuessListener mListener;
    public interface OnSendScuessListener {
        void sendScuess();
    }

    public void setOnSendScuessListener(OnSendScuessListener listener) {
        this.mListener = listener;
    }
    Runnable reSend = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.e("reSend","resend msg...");
            send();//重发
        }
    };

    public AsyncSend(String jsonMsg,BaiduPush T1) {
        // TODO Auto-generated constructor stub
        mBaiduPush = T1;
        mMessage = jsonMsg;
        mHandler = new Handler();
    }

    // 发送
    public void send() {
        //TODO 需判断有没有网络
        mTask = new MyAsyncTask();
        mTask.execute();
    }


    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... message) {
            String result = "";
            result = mBaiduPush.PushtoAll(mMessage);
//				result = mBaiduPush.PushNotifyAll("Benny",mMessage);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.e("SendMsgAsyncTask", "send msg result:" + result);
            if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则100ms后重发
                mHandler.postDelayed(reSend, 100);
            } else {
                if (mListener != null)
                    mListener.sendScuess();
            }
        }
    }
}
