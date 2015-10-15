package com.baidu.push.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    public static final String APP_KEY ="mzaavB1EGEG8qGqeE5FhrLFy";
    public static final String SECRIT_KEY = "97177c7744320dd55571457fc122a418";
    private BaiduPush mBaiduPushServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.testa)).setOnClickListener(this);
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                    MainActivity.SECRIT_KEY, MainActivity.APP_KEY);
        Log.i("App", MainActivity.SECRIT_KEY);
        Log.i("App1", MainActivity.SECRIT_KEY);

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
}
