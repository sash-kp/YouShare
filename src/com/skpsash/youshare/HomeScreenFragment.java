package com.skpsash.youshare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skpsash.youshare.adapter.AppController;
import com.skpsash.youshare.adapter.DatabaseHandler;
import com.skpsash.youshare.adapter.JSONParser;
import com.skpsash.youshare.adapter.UserFunctions;

public class HomeScreenFragment extends Fragment {
	private ImageButton img_btn;
	private Button regDetails;
	private Button changePassword;
	private Button buttonLogout,btnMakePost;
	JSONParser jsonParser;
	JSONObject json;
	static boolean network;
	static Context context;
	public HomeScreenFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);
		img_btn = (ImageButton) rootView.findViewById(R.id.imageButton1);
		regDetails = (Button) rootView.findViewById(R.id.button2);
		changePassword = (Button) rootView.findViewById(R.id.chPassword);
		buttonLogout = (Button) rootView.findViewById(R.id.btnLogout);
		btnMakePost = (Button) rootView.findViewById(R.id.makePost);
		final ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		// Loading image with placeholder and error image
//		imageLoader.get("http://skpsash.uphero.com/uploadImg_server_files/tmp_avatar_1407960614628.jpg", ImageLoader.getImageListener(
//		                img_btn, R.drawable.empty_profile, R.drawable.empty_profile));
//		context = getActivity().getApplicationContext();
		// Tag used to cancel the request
		RequestQueue queue = Volley.newRequestQueue(getActivity());
		final DatabaseHandler db = new DatabaseHandler(getActivity());
		String url = "http://skpsash.site50.net/uploadImg_server_files/getUrlOfPic.php";
		
		
        img_btn.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				 
				new Thread(new Runnable(){
				    public void run()
				    {
				    	try {			            		
							network = hasConnection();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    }).start();
				
				if(!network){
					new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK)
				    .setTitle("No Internet Connection")
				    .setMessage("No internet access.Make sure you get connected to internet.")
				     .setPositiveButton("Ok", null)
				    .create().show();
				}
				else{
					
				final String url1 = "http://skpsash.site50.net/uploadImg_server_files/getUrlOfPic.php";
				jsonParser = new JSONParser();
				new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK)
				.setTitle("Removing Profile Pic")
				.setMessage("Are You Sure You want to Remove Your Profile Pic?")
				.setNegativeButton("No", null)
				.setPositiveButton("Yes", new OnClickListener() {

				    public void onClick(DialogInterface arg0, int arg1) {
				    	new Thread(new Runnable(){
				            public void run()
				            {
				            	List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("tag", "remove"));
								params.add(new BasicNameValuePair("id",db.getUserId()));
				            	json = jsonParser.getJSONFromUrl(url1,params);
				            	try {
				            		Log.d("rowCheck",json.getString("rows"));
									if(json.getString("rows").equalsIgnoreCase("row")){
										getActivity().runOnUiThread(new Runnable() {
											  public void run() {
												  Toast.makeText(getActivity(),
											              "Your Profile Picture is removed.", Toast.LENGTH_LONG).show();
											  }
											});
										
									}
									if(json.getString("rows").equalsIgnoreCase("norow")){
										getActivity().runOnUiThread(new Runnable() {
											  public void run() {
												  Toast.makeText(getActivity(),
											              "Currently no profile pic is set for your profile.", Toast.LENGTH_LONG).show();
											  }
											});
										
										
									}
									getActivity().startActivity(getActivity().getIntent());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            }
				        }).start();
				 	
				    }		        
				}).create().show();
				
}
				return true;
			}
		});
		
        
		StringRequest postRequest = new StringRequest(Method.POST, url, 
			    new Response.Listener<String>() 
			    {
			        @Override
			        public void onResponse(String response) {
			            // response
			            Log.d("Response", response);
			            imageLoader.get(response, new ImageListener() {
	               			 
                		    @Override
                		    public void onErrorResponse(VolleyError error) {
                		        Log.e("TAG", "Image Load Error: " + error.getMessage());
                		    }
                		 
                		    @Override
                		    public void onResponse(ImageContainer response, boolean arg1) {
                		        if (response.getBitmap() != null) {
                		            // load image into imageview
                		            img_btn.setImageBitmap(response.getBitmap());		           
                		            scaleImage();
                		            
                		        	
                		        }
                		    }
                		    
                		     
                		});
			        }
			    }, 
			    new Response.ErrorListener() 
			    {
			         @Override
			         public void onErrorResponse(VolleyError error) {
			             // error
//			             Log.d("Error.Response", error.getMessage());
			       }
			    }
			) {     
			    @Override
			    protected Map<String, String> getParams() 
			    {  
			            Map<String, String>  params = new HashMap<String, String>();  
			            params.put("userId", db.getUserId());  
			            Log.d("id is ", db.getUserId());			             
			            return params;  
			    }
			};
			queue.add(postRequest);
			postRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		         

	
		
		img_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),UploadImageActivity.class);
				getActivity().startActivity(intent);
				
//				getActivity().finish();
				
			}
		});
		
				
		regDetails.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),RegisteredActivity.class);
				getActivity().startActivity(intent);
				
//				getActivity().finish();
				
			}
		});
		
		changePassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
				getActivity().startActivity(intent);
				
//				getActivity().finish();
				
			}
		});
		
		btnMakePost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),MakeAPostActivity.class);
				getActivity().startActivity(intent);
				
//				getActivity().finish();
				
			}
		});
		
		buttonLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK)
		        .setTitle("Logout")
		        .setMessage("Are You Sure You want to Log out?")
		        .setNegativeButton("No", null)
		        .setPositiveButton("Yes", new OnClickListener() {

		            public void onClick(DialogInterface arg0, int arg1) {
		            	getActivity().getApplicationContext().getSharedPreferences("MysecurePrefs", 0).edit().clear().commit();
		                UserFunctions logout = new UserFunctions();
		                logout.logoutUser(getActivity().getApplicationContext());
		                Intent login = new Intent(getActivity().getApplicationContext(), MainActivity.class);
		                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                startActivity(login);
		                getActivity().finish();
		            }		        
	        }).create().show();
				
				
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity().getApplicationContext();
	}
	
	
	/**
	   * Checks if the device has Internet connection.
	   * 
	   * @return <code>true</code> if the phone is connected to the Internet.
	 * @throws IOException 
	   */
	  public static boolean hasConnection() throws IOException {
		  URL url = new URL("http://www.google.com/");
		  
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
	        Context.CONNECTIVITY_SERVICE);

	    NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    if (wifiNetwork != null && wifiNetwork.isConnected()) {
	    	{
	    		
	    		HttpURLConnection conn= (HttpURLConnection) url.openConnection();
	    		conn.setConnectTimeout(10000);
	    		int responseCode = conn.getResponseCode();
	    		if(responseCode == 200){
	    			
	    			return true;
	    		}
	    		else {
	    			return false;
	    		}
	    	}
	      
	    }

	    NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (mobileNetwork != null && mobileNetwork.isConnected()) {
    			
    			return true;
	    }

	    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
    			return true;
	    }

	    return false;
	  }
	
	 private void scaleImage()
	    {
	        // Get the ImageView and its bitmap
	        
	        try{
	        ImageButton btn = (ImageButton) getActivity().findViewById(R.id.imageButton1);
	        Drawable drawing = btn.getDrawable();
	        
	        if (drawing == null) {
	        	Log.d("image null","null here");
	            return; 
	            // Checking for null & return, as suggested in comments
	        }
	        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	        // Get current dimensions AND the desired bounding box
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        int bounding = dpToPx(150);
	        Log.i("Test", "original width = " + Integer.toString(width));
	        Log.i("Test", "original height = " + Integer.toString(height));
	        Log.i("Test", "bounding = " + Integer.toString(bounding));

	        // Determine how much to scale: the dimension requiring less scaling is
	        // closer to the its side. This way the image always stays inside your
	        // bounding box AND either x/y axis touches it.  
	        float xScale = ((float) bounding) / width;
	        float yScale = ((float) bounding) / height;
	        float scale = (xScale <= yScale) ? xScale : yScale;
	        Log.i("Test", "xScale = " + Float.toString(xScale));
	        Log.i("Test", "yScale = " + Float.toString(yScale));
	        Log.i("Test", "scale = " + Float.toString(scale));

	        // Create a matrix for the scaling and add the scaling data
	        Matrix matrix = new Matrix();
	        matrix.postScale(scale, scale);

	        // Create a new bitmap and convert it to a format understood by the ImageView 
	        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	        width = scaledBitmap.getWidth(); // re-use
	        height = scaledBitmap.getHeight(); // re-use
	        BitmapDrawable result = new BitmapDrawable(getActivity().getResources(),scaledBitmap);
	        Log.i("Test", "scaled width = " + Integer.toString(width));
	        Log.i("Test", "scaled height = " + Integer.toString(height));
	        Bitmap bmp = result.getBitmap();
	        BorderDrawable drawable = new BorderDrawable(getResources(), bmp);
	        btn.setImageDrawable(drawable);
	        // Apply the scaled bitmap
//	        btn.setImageDrawable(result);

	        // Now change ImageView's dimensions to match the scaled image
	        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btn.getLayoutParams(); 
	        params.width = width;
	        params.height = height;
	        btn.setLayoutParams(params);

	        Log.i("Test", "done");
	        } catch(Exception e){
	        	e.printStackTrace();
	        }
	    }
	 
	   private int dpToPx(int dp)
	    {
	        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
	        return Math.round((float)dp * density);
	    }
	   
	   public class BorderDrawable extends BitmapDrawable {

		    private static final int BORDER_WIDTH = 9;
		    private final int[] GRADIENT_COLORS = {Color.GRAY,Color.BLUE, Color.GREEN, Color.RED};
		    Paint borderPaint;

		    public BorderDrawable(Resources res, Bitmap bitmap) {
		        super(res, bitmap);
		        this.borderPaint = new Paint();
		        borderPaint.setStrokeWidth(BORDER_WIDTH);
		        borderPaint.setStyle(Paint.Style.STROKE);

		        // set border gradient
		        Shader shader = new LinearGradient(0, 0, 0, getBounds().bottom, GRADIENT_COLORS, null,  Shader.TileMode.CLAMP);
		        borderPaint.setShader(shader);

		        // or the border color
		        // borderPaint.setColor(Color.GREEN);

		    }

		    @Override
		    public void draw(Canvas canvas) {
		        super.draw(canvas);
		        // draw
		        canvas.drawRect(getBounds(), borderPaint);
		    }
		}
	   

}
