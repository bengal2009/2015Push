package com.baidu.push.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostMT extends Activity {
String TagStr="PostMT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_mt);
        Button imageDownloaderBtn = (Button) findViewById(R.id.PMS);
        imageDownloaderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                new ImageDownloader().execute(downloadUrl);
                String t1="http://api.tuisong.baidu.com/rest/3.0/app/query_tags";
                String t2="apikey=mzaavB1EGEG8qGqeE5FhrLFy&device_type=3&" +
                        "secret_key=97177c7744320dd55571457fc122a418&" +
                        "sign=7dd37c1fae9172194d44636325868dbe&timestamp=1445840002";
                new PostTest(t1,t2).execute();
            }

        });
    }
    private class PostTest extends AsyncTask<String, Void, String> {
       String mUrl,mQuery;
        @Override
        protected String doInBackground(String... params) {

            URL urlobj;
            HttpURLConnection connection = null;
            StringBuilder out=null;

                    Log.i(TagStr,mUrl);
            Log.i(TagStr,mQuery);

            try {
                urlobj = new URL(mUrl);
                byte[] outputBytes =mQuery.getBytes("UTF-8");
                connection = (HttpURLConnection) urlobj.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("User-Agent",
                        "BCCS_SDK/3.0");
                connection
                        .setRequestProperty("Content-Length", Integer.toString(mQuery.length()));
                connection.setRequestProperty("charset", "utf-8");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(outputBytes);

                int responseCode = connection.getResponseCode();
                Log.i(TagStr, Integer.toString(responseCode));
                Log.i(TagStr, outputBytes.toString());
                // Send request
                /*DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(mQuery.toString());
                wr.flush();
                wr.close();*/

                /*if(responseCode == HttpURLConnection.HTTP_OK) {

                    // Get Response
                    Log.i("BaiduPush", "Get Response");
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;

                    while ((line = rd.readLine()) != null) {
                        out.append(line);
                        out.append('\r');
                    }
                    Log.i("BaiduPush", out.toString());
                    rd.close();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HttpRequest", "HttpRequest Exception:" + e.getMessage());
//                out.append("HttpRequest Exception:" + e.getMessage());
//                out.append("Error!");//瘨��憭梯揖嚗���霂荔��扯���
            }

            if (connection != null)
                connection.disconnect();
            return null;
             }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        private PostTest(String UrlStr,String QueryStr)
        {
            mUrl=UrlStr;
            mQuery=QueryStr;

        }
    }
}
