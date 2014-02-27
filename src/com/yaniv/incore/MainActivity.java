package com.yaniv.incore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "IncorePrefs";
	
	private static Camera camera;
	private CameraPreview cameraPreview;
	private FrameLayout cameraPreviewLayout;
	private LinearLayout clickToCapture;
	private LinearLayout confirmOrRetake;
	private ImageView aperture;
	private ProcessImage processImage;

	private LinearLayout processResultHolder;
	private ImageView processResult;
	private Bitmap capturedImage;
	private LinearLayout drawResult;
	
	private ScrollView jsonResponseHolder;
	private TextView jsonResponseTextView;
	
	private MediaPlayer mediaPlayer;
	
	private TextView minRadiusTextView;
	private TextView maxRadiusTextView;
	private TextView sensitivityTextView;
	private TextView hostTextView;
	
	private LinearLayout menuButtonWrapper;
	private LinearLayout menuContainer;
	

	private EditText minRadiusInput;
	private EditText maxRadiusInput;
	private EditText sensitivityInput;
	private EditText hostInput;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		processResultHolder = (LinearLayout) findViewById(R.id.process_result_holder);
		processResult = (ImageView) findViewById(R.id.process_result);
		drawResult = (LinearLayout) findViewById(R.id.draw_results);
		
		clickToCapture = (LinearLayout) findViewById(R.id.click_to_capture);
		confirmOrRetake = (LinearLayout) findViewById(R.id.confirm_or_retake);
		aperture = (ImageView) findViewById(R.id.aperture);
		
		jsonResponseHolder = (ScrollView) findViewById(R.id.json_response_holder);
		jsonResponseTextView = (TextView) findViewById(R.id.json_response);
		
		mediaPlayer = MediaPlayer.create(this, R.raw.camera_shutter);
		
		minRadiusInput = (EditText) findViewById(R.id.min_radius_input);
		maxRadiusInput = (EditText) findViewById(R.id.max_radius_input);
		sensitivityInput = (EditText) findViewById(R.id.sensitivity_input);
		sensitivityInput.setInputType(
				InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		hostInput = (EditText) findViewById(R.id.host_input);
		hostInput.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		final float scale = getResources().getDisplayMetrics().density;
		int width = (int) (150 * scale + 0.5f);
		hostInput.getLayoutParams().width = width;
//		hostInput.setLayoutParams(
//				new LayoutParams(50, 50)
//				);
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		minRadiusInput.setText(
				settings.getString("minRadius", Integer.toString(20))
				);
		maxRadiusInput.setText(
				settings.getString("maxRadius", Integer.toString(55))
				);
		sensitivityInput.setText(
				settings.getString("sensitivity", Float.toString((float) 0.91))
				);
		hostInput.setText(settings.getString("host", "84.108.106.16"));
		
		camera = getCameraInstance();
		if (camera != null) {
			// Create our camera preview view and set it as the content 
			// of our activity.
			
	        cameraPreview = new CameraPreview(this, camera);
	        
	        cameraPreviewLayout = 
	        		(FrameLayout) findViewById(R.id.camera_preview);
	        cameraPreviewLayout.addView(cameraPreview);
		}
		
		
		menuContainer = (LinearLayout) findViewById(R.id.menu_container);
		setMenuTextViews();
		
		minRadiusTextView = (TextView) findViewById(R.id.min_radius_textView);
		minRadiusTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				focusInput(v);
			}
		});
		
		maxRadiusTextView = (TextView) findViewById(R.id.max_radius_textView);
		maxRadiusTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				focusInput(v);
			}
		});
		
		sensitivityTextView = (TextView) findViewById(R.id.sensitivity_textView);
		sensitivityTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				focusInput(v);
			}
		});
		
		hostTextView = (TextView) findViewById(R.id.host_textView);
		hostTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				focusInput(v);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static Camera getCameraInstance() {
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    } catch (Exception e) {
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	public void clickToCapture(View v) {
		RotateAnimation anim = new RotateAnimation(0f, 350f, 40f, 40f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
		
		aperture.startAnimation(anim);
		
		takePicture();
	}
	
	public void retakePicture(View v) {
		drawResult.removeAllViews();
		
		if (camera == null) {
			camera = getCameraInstance();
			if (camera != null) {
				// Create our camera preview view and set it as the content 
				// of our activity.
				
		        cameraPreview = new CameraPreview(this, camera);
		        
		        cameraPreviewLayout = 
		        		(FrameLayout) findViewById(R.id.camera_preview);
		        cameraPreviewLayout.addView(cameraPreview);
			}
		}
		
		if (processResultHolder.getVisibility() == View.VISIBLE) {
			processResultHolder.setVisibility(View.GONE);
//			cameraPreview.bringToFront();
		} else {
			camera.startPreview();
		}
		
		camera.startPreview();
		
		confirmOrRetake.setVisibility(View.GONE);
		clickToCapture.setVisibility(View.VISIBLE);
	}
	
	public void confirmPicture(View v) {
		if (camera != null) {
			camera.stopPreview();
//			camera.release();
			camera = null;
		}
		
		JSONObject res = new JSONObject();
		
		while (!processImage.afterProcess()) { }
		
		processResultHolder.setVisibility(View.VISIBLE);
		cameraPreview.setVisibility(View.GONE);
		

		Circle c = new Circle(this);
		
		try {
			jsonResponseTextView.setText(processImage.getResponse().toString(2));
			res = processImage.getResponse();
			
			JSONArray circles = res.getJSONArray("circles");
			for (int i = 0; i < circles.length(); i++) {
				JSONObject circle = circles.getJSONObject(i);
				int x = circle.getInt("x");
				int y = circle.getInt("y");
				int radius = circle.getInt("radius");
				
				c.addCircle(x, y, radius);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		drawResult.addView(c);
		
		//jsonResponseHolder.setVisibility(View.VISIBLE);
		
	}
	
	private void takePicture() {
		camera.autoFocus(new AutoFocusCallback() {
    		@Override
    		public void onAutoFocus(boolean success, Camera camera) {
    			PictureCallback pictureCallback = new PictureCallback() {
					
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						processImage = new ProcessImage(
								data,
								minRadiusInput.getText().toString(), 
								maxRadiusInput.getText().toString(),
								sensitivityInput.getText().toString(),
								hostInput.getText().toString());
						processImage.start();
						
						Bitmap b = BitmapFactory.decodeByteArray(
								data, 0, data.length
								);
						Matrix mat = new Matrix();
					    mat.postRotate(90);
					    capturedImage = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mat, true);
						processResult.setImageBitmap(capturedImage);
						
						aperture.setAnimation(null);

						mediaPlayer.start();
						clickToCapture.setVisibility(View.GONE);
						confirmOrRetake.setVisibility(View.VISIBLE);
					}
				};
    			camera.takePicture(null, null, pictureCallback);
    		}
    	});
	}
	
	private void setMenuTextViews() {
		TextView t = (TextView) findViewById(R.id.min_radius_textView);
		t.setText(getString(R.string.min_radius));
		
		t = (TextView) findViewById(R.id.max_radius_textView);
		t.setText(getString(R.string.max_radius));
		
		t = (TextView) findViewById(R.id.sensitivity_textView);
		t.setText(getString(R.string.sensitivity));
		
		t = (TextView) findViewById(R.id.host_textView);
		t.setText(getString(R.string.host));
	}
	
	private void focusInput(View v) {
		EditText ed = (EditText) ((ViewGroup) v.getParent()).getChildAt(0);
		InputMethodManager inputMethodManager = 
				(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ed.requestFocus();
		inputMethodManager.showSoftInput(ed, InputMethodManager.SHOW_FORCED);
		
	}
	
	public void menu(View v) {
		if (menuContainer.getVisibility() == View.VISIBLE) {
			v.setBackgroundResource(R.drawable.action_bar_bg_default);
			menuContainer.setVisibility(View.GONE);
		} else if (menuContainer.getVisibility() == View.GONE) {
			v.setBackgroundResource(R.drawable.action_bar_bg_pressed);
			menuContainer.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onStop() {
		/*
		 * For the home button
		 */
		super.onStop();
		
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("minRadius", minRadiusInput.getText().toString());
		editor.putString("maxRadius", maxRadiusInput.getText().toString());
		editor.putString("sensitivity", sensitivityInput.getText().toString());
		editor.putString("host", hostInput.getText().toString());

		// Commit the edits!
		editor.commit();
	}
	
	@Override
	protected void onPause() {
		/*
		 * For back  button
		 */
		super.onPause();
		
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("minRadius", minRadiusInput.getText().toString());
		editor.putString("maxRadius", maxRadiusInput.getText().toString());
		editor.putString("sensitivity", sensitivityInput.getText().toString());
		editor.putString("host", hostInput.getText().toString());

		// Commit the edits!
		editor.commit();
	}

}
