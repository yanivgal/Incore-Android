package com.yaniv.incore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Request extends AsyncTask<Void, Integer, JSONObject> {
	public static enum RequestType {GET, POST};
	
	private RequestHandler requestHandler;
	private RequestObject requestObject;
	
	public Request(RequestObject requestObject) {
		this.requestObject = requestObject;
	}

	@Override
	protected JSONObject doInBackground(Void... vParams) {
		requestHandler = new RequestHandler(requestObject);
		return requestHandler.execute();
	}
	
	
	/*
	 * 
	 * Request Handler Class
	 * 
	 */
	
	public class RequestHandler {
		private HttpClient httpClient;
		private HttpPost httpPost;
		private HttpGet httpGet;
		private HttpEntity responseEntity;
		
		String url;
		HashMap<String, String> params;
		RequestType requestType;
		
		public RequestHandler(RequestObject requestObject) {
			this.url = requestObject.getUrl();
			this.params = requestObject.getAllParameters();
			this.requestType = requestObject.getType();
			
			httpClient = new DefaultHttpClient();
		}
		
		public JSONObject execute() {
			JSONObject jsonResponse = new JSONObject();
			
			switch (requestType) {
			case GET:
				jsonResponse = get();
				break;
			case POST:
				jsonResponse = post();
				break;
			}
			
			return jsonResponse;
		}

		public JSONObject get() {
			JSONObject jsonResponse = new JSONObject();
			
			try {
				httpGet = new HttpGet(this.url);
				
				HttpResponse response = httpClient.execute(httpGet);
				
				int responseStatus = response.getStatusLine().getStatusCode();
				if (responseStatus == HttpStatus.SC_OK) {
					responseEntity = response.getEntity();
					if (responseEntity != null) {
			            InputStream instream = responseEntity.getContent();
			            String res = streamToString(instream);
			            instream.close();
						jsonResponse = new JSONObject(res);
			        }
				} else {
					jsonResponse = printError("Can't connect to server");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return jsonResponse;
		}
		
		public JSONObject post() {
			JSONObject jsonResponse = new JSONObject();
			
			try {
				httpPost = new HttpPost(this.url);
				
				/*
				 * Add parameters request
				 */
				ArrayList<NameValuePair> parameters = 
						new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
				    parameters.add(
				    		new BasicNameValuePair(
				    				entry.getKey(), entry.getValue()
				    				)
				    		);
				}
				httpPost.setEntity(new UrlEncodedFormEntity(parameters));
				
				HttpResponse response = httpClient.execute(httpPost);
				int responseStatus = response.getStatusLine().getStatusCode();
				if (responseStatus == HttpStatus.SC_OK) {
					responseEntity = response.getEntity();
					if (responseEntity != null) {
			            InputStream instream = responseEntity.getContent();
			            String res = streamToString(instream);
			            instream.close();
						jsonResponse = new JSONObject(res);
			        }
				} else {
					jsonResponse = printError("Can't connect to server");
				}
			} catch (ClientProtocolException e) { 
				e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return jsonResponse;
		}
		
		private String streamToString(InputStream is) {
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    
		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}
		
		private JSONObject printError(String errorMessgae) {
			JSONObject jsonResponse = new JSONObject();
			try {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", errorMessgae);
			} catch (JSONException e) { }
			return jsonResponse;
		}
	}
	
	
	/*
	 * 
	 * Request Object Class
	 * 
	 */
	
	public static class RequestObject {
		private String url;
		private RequestType type;
		private HashMap<String, String> params;
		
		public RequestObject() {
			this.type = RequestType.GET;
			this.params = new HashMap<String, String>();
		}
		
		public RequestObject(String url) {
			this.url = url;
			this.type = RequestType.GET;
			this.params = new HashMap<String, String>();
		}
		
		public RequestObject(String url, RequestType type) {
			this.url = url;
			this.type = type;
			this.params = new HashMap<String, String>();
		}
		
		public RequestObject(
				String url, RequestType type, HashMap<String, String> params) {
			this.url = url;
			this.type = type;
			this.params = params;
		}
		
		public String getUrl() {
			return url;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setType(RequestType type) {
			this.type = type;
		}
		
		public RequestType getType() {
			return type;
		}
		
		public void setAllParameters(HashMap<String, String> params) {
			this.params = params;		
		}
		
		public HashMap<String, String> getAllParameters() {
			return params;
		}
		
		public void addParameter(String key, String value) {
			params.put(key, value);
		}
		
		public void removeParameter(String key) {
			params.remove(key);
		}
		
		public String getParameter(String key) {
			return params.get(key);
		}
		
		public void removeAllParameters() {
			params.clear();
		}
	}
}
