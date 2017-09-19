package sfdata;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.ImmutableMap;

import net.sf.json.JSONObject;

public class WebUtil //This uses the wrapper to wrap HTTPS calls. Please compare with DefaultWebUtil.java
{
	
	public static boolean logIt = false;
	
	public static List<BasicNameValuePair> getFormParams(ImmutableMap<String, String> params)
	{	
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
		for (String param : params.keySet())  formparams.add(new BasicNameValuePair(param, params.get(param)));
		return formparams;
	}
	
	public static String getResponse(List<BasicNameValuePair> formparams, String clientUrl)
	{
		return getResponse(formparams, clientUrl, "text/html");
	}
	
	public static String getResponse(List<BasicNameValuePair> formparams, String clientUrl, String acceptValue)
	{
		
		try {
			HttpPost httppost = new HttpPost(clientUrl);
			httppost.setHeader("Accept", acceptValue);
			httppost.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			
			HttpClient httpclient = new DefaultHttpClient();
			
			if (clientUrl.contains("https://")) {
				httpclient = WebClientDevWrapper.wrapClient(httpclient);
			}
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			
			if (responseEntity == null) {
				return "";
			}
			
			InputStream instream =  responseEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			instream.close();
			reader.close();
			
			
			return sb.toString();
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public static String getGetResponse(List<BasicNameValuePair> formparams, String clientUrl)
	{
		
		try {
			HttpGet httpget = new HttpGet(clientUrl);
			httpget.setHeader("Authorization", "Bearer "+ formparams.get(0).getValue());
			
			HttpClient httpclient = new DefaultHttpClient();
			
			if (clientUrl.contains("https://")) {
				httpclient = WebClientDevWrapper.wrapClient(httpclient);
			}
			
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity responseEntity = response.getEntity();
			
			if (responseEntity == null) {
				return "";
			}
			
			InputStream instream =  responseEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			instream.close();
			reader.close();
			
			
			return sb.toString();
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public static String getErrorJson(String errorMessage)
	{
		JSONObject o = new JSONObject();
		o.accumulate("status", "error");
		o.accumulate("error", errorMessage);
		return o.toString();
	}
	
	public static String generateAuthKey(int length)
	{
		String authKey = "";
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		
		int Min = 0;
		double Max = chars.length() - 1;
		
		for (int i = 0; i < length; i++) {
			int value = Min + (int)(Math.random() * ((Max - Min) + 1));
			if (value < 0 || value > chars.length()) value = 0;
			
			authKey += chars.charAt(value);
		}
		
		return authKey;
	}
	
	public static String getSuccessJson(String successMessage)
	{
		JSONObject o = new JSONObject();
		o.accumulate("status", "success");
		o.accumulate("message", successMessage);
		return o.toString();
	}		
		
}
