package com.baidu.push.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncSendMsg extends Activity {
String TagStr="PostMT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendtees);
        Button imageDownloaderBtn = (Button) findViewById(R.id.PMS1);
        imageDownloaderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                new ImageDownloader().execute(downloadUrl);
                String t1="http://api.tuisong.baidu.com/rest/3.0/push/all";
                String t2="apikey=mzaavB1EGEG8qGqeE5FhrLFy&device_type=3&" +
                        "msg=%7B%22title%22%3A%22hello%22%2C%22description%22%3A%22hello+world%22%7D&" +
                        "msg_type=1&secret_key=97177c7744320dd55571457fc122a418&" +
                        "sign=2ed148fe9f3f4195b75bfbb5bdcf6075&timestamp=1445838906";
                new PostTest(t1,t2).execute();
                /*new PostTest("http://192.168.91.57/cacti/index.php",
                        "login_username=admin&login_password=1234").execute();*/

            }

        });
    }
    private class PostTest extends AsyncTask<String, Void, String> {
       String mUrl,mQuery;
        @Override
        protected String doInBackground(String... params) {

            URL urlobj;
            HttpURLConnection connection = null;
            StringBuilder out=new StringBuilder();

                    Log.i(TagStr,mUrl);
            Log.i(TagStr,mQuery);

            try {
                urlobj = new URL(mUrl);
                byte[] outputBytes =mQuery.getBytes();
                connection = (HttpURLConnection) urlobj.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type",
                       "application/x-www-form-urlencoded;charset=utf-8");
                connection.setRequestProperty("User-Agent",
                        "BCCS_SDK/3.0");
                connection
                        .setRequestProperty("Content-Length", Integer.toString(mQuery.length()));

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setFixedLengthStreamingMode(
                        mQuery.getBytes().length);
                connection.connect();

                /*PrintWriter out1 = new PrintWriter(connection.getOutputStream());
                out1.print(mQuery);
                out1.close();*/

               /* OutputStream outputStream = connection.getOutputStream();
                outputStream.write(outputBytes);
*/
                /*int responseCode = connection.getResponseCode();
                Log.i(TagStr, Integer.toString(responseCode));*/
//                Log.i(TagStr, outputBytes.toString());
                // Send request
                /*DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                Log.i(TagStr, mQuery.toString());
                wr.writeBytes(mQuery);
                wr.flush();
                wr.close();*/
                OutputStream outputStream;
                outputStream = connection.getOutputStream();
                outputStream.write(mQuery.getBytes());
                outputStream.flush();
                outputStream.close();
                String urlReturned = connection.getURL().toString();
                Log.i(TagStr,urlReturned );
                int responseCode = connection.getResponseCode();
                Log.i(TagStr, Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK) {

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
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HttpRequest", "HttpRequest Exception:" + e.getMessage());
//                out.append("HttpRequest Exception:" + e.getMessage());
//                out.append("Error!");
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
