package com.baidu.push.example;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;


public class BaiduPush {
	public final static String mUrl = "http://api.tuisong.baidu.com/rest/3.0/";

    public final static String HTTP_METHOD_POST = "POST";
	public final static String HTTP_METHOD_GET = "GET";

	public static final String SEND_MSG_ERROR = "send_msg_error";
	
	private final static int HTTP_CONNECT_TIMEOUT = 20000;// 餈頞�園嚗�0s
	private final static int HTTP_READ_TIMEOUT = 20000;// 霂餅��航��嗆�湛�10s

	public String mHttpMethod;// 霂瑟��孵�嚗ost or Get
	public String mSecretKey;// 摰key

	/**
	 * ���賣
	 *
	 * @param http_mehtod
	 *            霂瑟��孵�
	 * @param secret_key
	 *            摰key
	 * @param api_key
	 *            摨key
	 */
	public BaiduPush(String http_mehtod, String secret_key, String api_key) {
		mHttpMethod = http_mehtod;
		mSecretKey = secret_key;
		RestApi.mApiKey = api_key;
	}

	/**
	 * url蝻��孵�
	 *
	 * @param str
	 *            ��蝻��孵�嚗��暺恕銝滾tf-8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String urlencode(String str) throws UnsupportedEncodingException {
		String rc = URLEncoder.encode(str, "utf-8");
		return rc.replace("*", "%2A");
	}

	/**
	 * 撠�蝚虫葡頧祆蝘迴son�澆�
	 *
	 * @param str
	 * @return
	 */
	public String jsonencode(String str) {
		String rc = str.replace("\\", "\\\\");
		rc = rc.replace("\"", "\\\"");
		rc = rc.replace("\'", "\\\'");
		return rc;
	}

	/**
	 * �扯�Post霂瑟���桀�����銋掩
	 *
	 * @param data
	 *            霂瑟����	 * @return
	 */
	public String PostHttpRequest(RestApi data) {

		StringBuilder sb = new StringBuilder();

		String channel = data.remove(RestApi._CHANNEL_ID);
		if (channel == null)
			channel =data._CHANNEL;
//        Log.i("BaiduPush", "mUrl:"+mUrl + channel);
		try {
            data.put(RestApi._SECRETKEY,mSecretKey);
			data.put(RestApi._TIMESTAMP,
                    Long.toString(System.currentTimeMillis() / 1000));
			data.remove(RestApi._SIGN);
            data.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
			sb.append(mHttpMethod);
            sb.append(mUrl);
			sb.append(channel);
			for (Map.Entry<String, String> i : data.entrySet()) {
				sb.append(i.getKey());
				sb.append('=');
				sb.append(i.getValue());
			}
			sb.append(mSecretKey);

			// System.out.println( "PRE: " + sb.toString() );
			// System.out.println( "UEC: " + URLEncoder.encode(sb.toString(),
			// "utf-8") );

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			// md.update( URLEncoder.encode(sb.toString(), "utf-8").getBytes()
			// );
			Log.i("BaiduPush",sb.toString());
			md.update(urlencode(sb.toString()).getBytes());
			byte[] md5 = md.digest();

			sb.setLength(0);
			for (byte b : md5)
				sb.append(String.format("%02x", b & 0xff));
			data.put(RestApi._SIGN, sb.toString());

			// System.out.println( "MD5: " + sb.toString());

			sb.setLength(0);
			for (Map.Entry<String, String> i : data.entrySet()) {
				sb.append(i.getKey());
				sb.append('=');
				// sb.append(i.getValue());
				// sb.append(URLEncoder.encode(i.getValue(), "utf-8"));
				sb.append(urlencode(i.getValue()));
				sb.append('&');
			}
			sb.setLength(sb.length() - 1);

			// System.out.println( "PST: " + sb.toString() );
			// System.out.println( mUrl + "?" + sb.toString() );

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("PostHttpRequest", "PostHttpRequest Exception:" + e.getMessage());
			return SEND_MSG_ERROR;//瘨��憭梯揖嚗���霂荔��扯���
		}

		StringBuilder response = new StringBuilder();
		HttpRequest(mUrl + channel, sb.toString(), response);
		Log.i("BaiduPush", "mUrl:" + mUrl + channel);
		Log.i("BaiduPush",sb.toString());
        Log.i("BaiduPush",response.toString());
		return response.toString();
	}

	/**
	 * �扯�Post霂瑟�
	 *
	 * @param url
	 *            �箇�url
	 * @param query
	 *            �漱���	 * @param out
	 *            ��典�憭�摮泵銝�	 * @return
	 */
	private int HttpRequest(String url, String query, StringBuilder out) {

		URL urlobj;
		HttpURLConnection connection = null;

		try {
			urlobj = new URL(url);
			Log.i("BaiduPush","URL:"+url);
            Log.i("BaiduPush","Query:"+query);
			connection = (HttpURLConnection) urlobj.openConnection();
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent",
                    "BCCS_SDK/3.0");
			connection
					.setRequestProperty("Content-Length", "" + query.length());
			connection.setRequestProperty("charset", "utf-8");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
			connection.setReadTimeout(HTTP_READ_TIMEOUT);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
            Log.i("BaiduPush","QueryString:"+query.toString());
			wr.writeBytes(query.toString());
			wr.flush();
			wr.close();

			// Get Response
			Log.i("BaiduPush","Get Response");
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			while ((line = rd.readLine()) != null) {
				out.append(line);
				out.append('\r');
			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("HttpRequest", "HttpRequest Exception:" + e.getMessage());
            out.append("HttpRequest Exception:" + e.getMessage());
			out.append(SEND_MSG_ERROR);//瘨��憭梯揖嚗���霂荔��扯���
		}

		if (connection != null)
			connection.disconnect();

		return 0;
	}

	//
	// REST APIs
	//
	/**
	 * �亥砭霈曉�靽⊥���具��冽銝摨列hannel��摰蝟颯�
	 *
	 * @param userid
	 *            �冽id
	 * @param channelid
	 * @return json敶Ｗ����∪�Ｗ�
	 */
	public String QueryBindlist(String userid, String channelid) {
		RestApi ra = new RestApi(RestApi.METHOD_QUERY_BIND_LIST);
		ra.put(RestApi._USER_ID, userid);
		// ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
		ra.put(RestApi._CHANNEL_ID, channelid);
		// ra.put(RestApi._START, "0");
		// ra.put(RestApi._LIMIT, "10");
		return PostHttpRequest(ra);
	}

	/**
	 * �斗霈曉����具��冽銝hannel��摰蝟餅�血��具�
	 *
	 * @param userid
	 *            �冽id
	 * @param channelid
	 * @return
	 */
	public String VerifyBind(String userid, String channelid) {
		RestApi ra = new RestApi(RestApi.METHOD_VERIFY_BIND);
		ra.put(RestApi._USER_ID, userid);
		// ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
		ra.put(RestApi._CHANNEL_ID, channelid);
		return PostHttpRequest(ra);
	}

	/**
	 * 蝏�摰�瑁挽蝵格�蝑�	 *
	 * @param tag
	 * @param userid
	 * @return
	 */
	public String SetTag(String tag, String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_SET_TAG);
		ra.put(RestApi._USER_ID, userid);
		ra.put(RestApi._TAG, tag);
		return PostHttpRequest(ra);
	}

	/**
	 * �亥砭摨����蝑�	 *
	 * @return
	 */
	public String FetchTag() {
		RestApi ra = new RestApi(RestApi.METHOD_FETCH_TAG);
		// ra.put(RestApi._NAME, "0");
		// ra.put(RestApi._START, "0");
		// ra.put(RestApi._LIMIT, "10");
		return PostHttpRequest(ra);
	}

	/**
	 * ����冽��摰�蝑�	 *
	 * @param tag
	 * @param userid
	 * @return
	 */
	public String DeleteTag(String tag, String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_DELETE_TAG);
		ra.put(RestApi._USER_ID, userid);
		ra.put(RestApi._TAG, tag);
		return PostHttpRequest(ra);
	}

	/**
	 * �亥砭���冽��蝑�	 *
	 * @param userid
	 * @return
	 */
	public String QueryUserTag(String userid) {
//        Log.i("BaiduPush", "mUrl-1:"+mUrl );
		RestApi ra = new RestApi(RestApi.METHOD_QUERY_USER_TAG);
        ra.put(RestApi._CHANNEL, "app/query_tags");
//		ra.put(RestApi._USER_ID, userid);
//        ra.put(RestApi.METHOD_QUERY_USER_TAG, "query_tags");
		return PostHttpRequest(ra);
	}
    /*
    Benny Test
     */
    public String PushtoAll(String message) {
        Log.i("BaiduPush",  message );
        RestApi ra = new RestApi(RestApi.METHOD_QUERY_USER_TAG);
        ra.put(RestApi._CHANNEL, "push/all");
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);
        ra.put(RestApi._MESSAGES, message);
        return PostHttpRequest(ra);
    }
	/**
	 * �寞channel_id�亥砭霈曉�蝐餃�嚗�1嚗�閫霈曉�嚗�2嚗c霈曉�嚗�3嚗ndriod霈曉�嚗�4嚗OS霈曉�嚗�5嚗p霈曉�嚗�	 *
	 * @param channelid
	 * @return
	 */
	public String QueryDeviceType(String channelid) {
		RestApi ra = new RestApi(RestApi.METHOD_QUERY_DEVICE_TYPE);
		ra.put(RestApi._CHANNEL_ID, channelid);
		return PostHttpRequest(ra);
	}

	// Message Push

	private final static String MSGKEY = "msgkey";

	/**
	 * 蝏�摰�瑟����	 *
	 * @param message
	 * @param userid
	 * @return
	 */
	public String PushMessage(String message, String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);
//        Log.e("MessageType",RestApi.MESSAGE_TYPE_NOTIFY);
//		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);
		ra.put(RestApi._MESSAGES, message);
		ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
		// ra.put(RestApi._MESSAGE_EXPIRES, "86400");
		// ra.put(RestApi._CHANNEL_ID, "");
		ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_USER);
		ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
		ra.put(RestApi._USER_ID, userid);
		return PostHttpRequest(ra);
	}

	/**
	 * 蝏�摰�蝑曄�瑟����	 *
	 * @param message
	 * @param tag
	 * @return
	 */
	public String PushTagMessage(String message, String tag) {
		RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);
		ra.put(RestApi._MESSAGES, message);
		ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
		// ra.put(RestApi._MESSAGE_EXPIRES, "86400");
		ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_TAG);
		// ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
		ra.put(RestApi._TAG, tag);
		return PostHttpRequest(ra);
	}

	/**
	 * 蝏���瑟����	 *
	 * @param message
	 * @return
	 */
	public String PushMessage(String message) {
		RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);
		ra.put(RestApi._MESSAGES, message);
		ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
		// ra.put(RestApi._MESSAGE_EXPIRES, "86400");
		ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
		// ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
		return PostHttpRequest(ra);
	}

	/**
	 * 蝏�摰�瑟����	 *
	 * @param title
	 * @param message
	 * @param userid
	 * @return
	 */
	public String PushNotify(String title, String message, String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);

		// notification_builder_id : default 0

		// String msg =
		// String.format("{'title':'%s','description':'%s','notification_basic_style':7}",
		// title, jsonencode(message));
		// String msg =
		// String.format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':5,'open_type':2}",
		// title, jsonencode(message));
		// String msg =
		// String.format("{'title':'%s','description':'%s','notification_builder_id':2,'notification_basic_style':7}",
		// title, jsonencode(message));

		String msg = String
				.format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':7,'open_type':2,'custom_content':{'test':'test'}}",
						title, jsonencode(message));

		// String msg =
		// String.format("{\"title\":\"%s\",\"description\":\"%s\",\"notification_basic_style\":\"7\"}",
		// title, jsonencode(message));
		// String msg =
		// String.format("{\"title\":\"%s\",\"description\":\"%s\",\"notification_builder_id\":0,\"notification_basic_style\":1,\"open_type\":2}",
		// title, jsonencode(message));

		System.out.println(msg);

		ra.put(RestApi._MESSAGES, msg);

		ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
		ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_USER);
		// ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
		ra.put(RestApi._USER_ID, userid);
		return PostHttpRequest(ra);
	}

	/**
	 * 蝏���瑟����	 *
	 * @param title
	 * @param message
	 * @return
	 */
	public String PushNotifyAll(String title, String message) {
		RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
		ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);

		String msg = String
				.format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':7,'open_type':2,'custom_content':{'test':'test'}}",
						title, jsonencode(message));

		System.out.println(msg);

		ra.put(RestApi._MESSAGES, msg);

		ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
		ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
		return PostHttpRequest(ra);
	}

	/**
	 * �亥砭���冽蝳餌瑪瘨��	 *
	 * @param userid
	 * @return
	 */
	public String FetchMessage(String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_FETCH_MESSAGE);
		ra.put(RestApi._USER_ID, userid);
		// ra.put(RestApi._START, "0");
		// ra.put(RestApi._LIMIT, "10");
		return PostHttpRequest(ra);
	}

	/**
	 * �亥砭���冽�氖蝥踵��舀
	 *
	 * @param userid
	 * @return
	 */
	public String FetchMessageCount(String userid) {
		RestApi ra = new RestApi(RestApi.METHOD_FETCH_MSG_COUNT);
		ra.put(RestApi._USER_ID, userid);
		return PostHttpRequest(ra);
	}

	/**
	 * �蝳餌瑪瘨
	 * 
	 * @param userid
	 * @param msgids
	 * @return
	 */
	public String DeleteMessage(String userid, String msgids) {
		RestApi ra = new RestApi(RestApi.METHOD_DELETE_MESSAGE);
		ra.put(RestApi._USER_ID, userid);
		ra.put(RestApi._MESSAGE_IDS, msgids);
		return PostHttpRequest(ra);
	}

}