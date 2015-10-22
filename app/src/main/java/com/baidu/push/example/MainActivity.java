package com.baidu.push.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity implements View.OnClickListener,
        AsyncSend.OnSendScuessListener {
    public static final String APP_KEY ="mzaavB1EGEG8qGqeE5FhrLFy";
    public static final String SECRIT_KEY = "97177c7744320dd55571457fc122a418";
    private BaiduPush mBaiduPushServer;
    Gson mGson;
    String s1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.testa)).setOnClickListener(this);
//        Message message = new Message("1111");

                if (mBaiduPushServer == null)
                    mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                            MainActivity.SECRIT_KEY, MainActivity.APP_KEY);

AsyncSend task=new AsyncSend("{\"title\":\"hello\",\"description\":\"hello world\"}"
        ,mBaiduPushServer);
        task.setOnSendScuessListener(this);
        task.send();
//                Log.i("App", mBaiduPushServer.PushtoAll("{\"title\":\"hello\",\"description\":\"hello world\"}"));
//        Log.i("App", mBaiduPushServer.QueryUserTag("111"));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.testa:

                /*String userId = app.getUserId();
                String channelId = app.getChannelId();
                Log.i("Main", "USERID" + userId);
                Log.i("Main","USERID"+channelId);
*/
                break;

        }
    }
    @Override
    public void sendScuess() {
        Log.e("sendScuess", "发送成功");
        Toast.makeText(getApplicationContext(), "Done!",
                Toast.LENGTH_LONG).show();
    }
}
