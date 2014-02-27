package com.yaniv.incore;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.widget.EditText;

import com.yaniv.incore.Request.RequestObject;
import com.yaniv.incore.Request.RequestType;

public class ProcessImage extends Thread {
	
	private byte[] data;
	private String minRadius;
	private String maxRadius;
	private String sensitivity;
	private String host;
	private boolean afterProcess;
	private JSONObject jsonRespone;
	
	public ProcessImage(
			byte[] data, 
			String minRadius,
			String maxRadius, 
			String sensitivity, 
			String host) {
		this.data = data;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.sensitivity = sensitivity;
		this.host = host;
		jsonRespone = new JSONObject();
		afterProcess = false;
	}

	@Override
	public void run() {
		Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
		
		Matrix mat = new Matrix();
	    mat.postRotate(90);
	    Bitmap myInputBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mat, true);
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    myInputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
	    byte[] byteArrayImage = baos.toByteArray(); 
	    
	    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
	    
		RequestObject requestObject = new RequestObject(
				"http://" + host + "/incore/find-circles",
				RequestType.POST
				);
		requestObject.addParameter("image64", encodedImage);
		requestObject.addParameter("minRadius", minRadius);
		requestObject.addParameter("maxRadius", maxRadius);
		requestObject.addParameter("sensitivity", sensitivity);
		
		try {
			jsonRespone = new Request(requestObject).execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		afterProcess = true;
	}
	
	public boolean afterProcess() {
		return afterProcess;
	}
	
	public JSONObject getResponse() {
		return jsonRespone;
	}

}
