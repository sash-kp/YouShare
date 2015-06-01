package com.skpsash.youshare;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skpsash.youshare.adapter.DatabaseHandler;
import com.skpsash.youshare.adapter.JSONParser;

public class MakeAPostActivity extends Activity {
	int check=0;
	Spinner spinnerDropDown;
	String[] categories = {
			 "GK And Happenings","Just Fun","Awareness","Movies","Personalities","Politics","Others"	
	};
	TextView tv,selAnother;
	Button uploadPostImage,makePostNow,cancelBtn,backHome;
	ArrayList<CategoryInfo> myCategories;
	 private Uri mImageCaptureUri;
	    String result = "";
	    private static final int PICK_FROM_FILE = 2;
	    String selectedPath1 = "NONE";
	    private ImageView mImageView;
	    HttpEntity resEntity;
	    DatabaseHandler db;
	    ProgressBar pb1;
	 static EditText message,link;   
	 JSONParser jsonParser;
	 JSONObject json;
	 static String selectedCategory;
	 static String resName,newstr = null;
//	 static String userId,userName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_makeapost);
		message = (EditText) findViewById(R.id.message);
		link = (EditText) findViewById(R.id.link);
		backHome = (Button) findViewById(R.id.backHome);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		selAnother = (TextView) findViewById(R.id.selAnotherImage);
		makePostNow = (Button) findViewById(R.id.makePostNow);
		mImageView = (ImageView) findViewById(R.id.confirmImage);
		mImageView.setVisibility(View.GONE);
		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		pb1.setVisibility(View.GONE);
		tv = (TextView) findViewById(R.id.newpost);
        SpannableString content = new SpannableString("New YouShare Post");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);
        myCategories = populateList();
				spinnerDropDown =(Spinner)findViewById(R.id.spinner1);
				 CategoryAdapter myAdapter = new CategoryAdapter(this, R.layout.spinner_items, myCategories,0);

				 spinnerDropDown.setAdapter(myAdapter);
				uploadPostImage = (Button) findViewById(R.id.uploadPostImage);
				uploadPostImage.setText("Yes, I have a relevant image");
				uploadPostImage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {						
						if(!uploadPostImage.getText().equals("Upload"))
						openPhoto();
						if(mImageView.getDrawable() != null){
		            		new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
		    		        .setTitle("Confirm Upload")
		    		        .setMessage("Are You Sure You want to upload this image for this particular post?")
		    		        .setNegativeButton("No", null)
		    		        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		    		            public void onClick(DialogInterface arg0, int arg1) {
//		    		            	resName = getResourceNameFromClassByID(MakeAPostActivity.class, R.id.confirmImage);
		    		            	resName = (String) mImageView.getTag();
		    		            	if (null != resName && resName.length() > 0 )
		    		            	{
		    		            	    int endIndex = resName.lastIndexOf("/");
		    		            	    if (endIndex != -1)  
		    		            	    {
		    		            	        newstr = resName.substring(endIndex+1,resName.length() ); // do not forget to put check if(endIndex != -1)
		    		            	        Log.d("resName",newstr);
		    		            	    }
		    		            	} 
		    		            	
		    		            	mImageView.setClickable(false);
		    		            	selAnother.setVisibility(View.GONE);
		    		            	new Uploading().execute(); 
		    		            	
		    		            }		        
		    		        }).create().show();
		            		
		            	}
		            	
					}
				});
				
				backHome.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						startActivity(new Intent(MakeAPostActivity.this,HomeScreenActivity.class));
					}
				});
				
		makePostNow.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						if(selectedCategory != null && !selectedCategory.isEmpty()){
							
						if((message.getText().toString() != null && !message.getText().toString().isEmpty()) || (link.getText().toString() != null && !link.getText().toString().isEmpty()) || (mImageView.getDrawable() != null)){
							
							if(link.getText().toString() != null && !link.getText().toString().isEmpty()){
								if(URLUtil.isValidUrl(link.getText().toString())){
									new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
									.setTitle("Confirmation")
									.setMessage("Are you sure to upload this post?")
									.setNegativeButton("No", null)
									.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						 
									
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											
											new UploadingPost().execute();
										}
									    	
							            }).create().show();
								}
								
								else{
									new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
							        .setTitle("Invalid Url")
							        .setMessage("Please provide a valid iink/url.")
							        .setPositiveButton("Ok", null)
							        .create().show();
								}
							}
							
							else{
								new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
								.setTitle("Confirmation")
								.setMessage("Are you sure to upload this post?")
								.setNegativeButton("No", null)
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						 
								
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										
										new UploadingPost().execute();
									}
								    	
						            }).create().show();
							}
						
						}
						
						else{
							
							new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
					        .setTitle("Required Fields")
					        .setMessage("Please fill up at least one of the first 3 fields(post description/relevant link/relevant image).")
					        .setPositiveButton("Ok", null)
					        .create().show();
						}
						
						
					}
						else{
							new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
					        .setTitle("Category Required")
					        .setMessage("Please choose a category.It is important.")
					        .setPositiveButton("Ok", null)
					        .create().show();
						}
					}
				});
			
				
				spinnerDropDown.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
							check=check+1;
							if(check > 1){
//					
						CategoryInfo myCategory;
		                if(spinnerDropDown.getSelectedItem() != null)
		                {
		                    myCategory = (CategoryInfo) spinnerDropDown.getSelectedItem();
//		                    Toast.makeText(getBaseContext(), "You have selected Category : " + myCategory.getCategoryName(),
//									Toast.LENGTH_SHORT).show();	
		                   selectedCategory =  myCategory.getCategoryName();
		                }
					}
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
				        .setTitle("Category Required")
				        .setMessage("Please choose a category.It is important.")
				        .setPositiveButton("Ok", null)
				        .create().show();
					}
				});
	}
				public ArrayList<CategoryInfo> populateList()
			    {
			        ArrayList<CategoryInfo> myCategories = new ArrayList<CategoryInfo>();
			        myCategories.add(new CategoryInfo("Select A Category"));
			        myCategories.add(new CategoryInfo("GK And Happenings")); 
			        myCategories.add(new CategoryInfo("Just Fun"));
			        myCategories.add(new CategoryInfo("Awareness"));
			        myCategories.add(new CategoryInfo("Movies"));
			        myCategories.add(new CategoryInfo("Personalities"));
			        myCategories.add(new CategoryInfo("Politics"));
			        myCategories.add(new CategoryInfo("Others"));
			        return myCategories;
			    }
				
				private class CategoryAdapter extends ArrayAdapter<CategoryInfo>
			    {
			        private Activity context;
			        ArrayList<CategoryInfo> data = null;
			        private int hidingItemIndex = 0;
			        public CategoryAdapter(Activity context, int resource, ArrayList<CategoryInfo> data,int hidingItemIndex)
			        {
			            super(context, resource, data);
			            this.context = context;
			            this.data = data;
			            this.hidingItemIndex = hidingItemIndex;
			            
			        }

			        @Override
			        public View getView(int position, View convertView, ViewGroup parent) 
			        {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
			            return super.getView(position, convertView, parent);   
			        }

			        @Override
			        public View getDropDownView(int position, View convertView, ViewGroup parent)
			        {   // This view starts when we click the spinner.
			            View row = convertView;
			            
			            if(position == hidingItemIndex){
			            	
			            	 TextView tv = new TextView(getContext());
			            	 tv.setHeight(0);
			                 tv.setVisibility(View.GONE);
			                 row = tv;
			            }
			            
			            else{
			            	LayoutInflater inflater = context.getLayoutInflater();
			                row = inflater.inflate(R.layout.spinner_items, parent, false);
			            }
			           
//			            if(row == null)
//			            {
//			                LayoutInflater inflater = context.getLayoutInflater();
//			                row = inflater.inflate(R.layout.spinner_items, parent, false);
//			            }

			            CategoryInfo item = data.get(position);

			            if(item != null)
			            {   // Parse the data from each object and set it.
			                TextView myCategory = (TextView) row.findViewById(R.id.tv1);
			                if(myCategory != null)
			                    myCategory.setText(item.getCategoryName());

			            }

			            return row;
			        }
			    }
				
			    public void openPhoto(){
			    	Intent intent = new Intent();   
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
			    	
			   }
			 
			   public void onActivityResult(int requestCode, int resultCode, Intent data) {
				  
				   if (resultCode != RESULT_OK){
			   	    return;   
				   }
					
				   else{
			       Bitmap bitmap;
			       String path     = "";
//			       BitmapFactory.Options options = new BitmapFactory.Options();

			       if (requestCode == PICK_FROM_FILE) {
			           mImageCaptureUri = data.getData();
			           path = getRealPathFromURI(mImageCaptureUri); //from Gallery
			           selectedPath1 = path;
			           if (path == null)
			               path = mImageCaptureUri.getPath(); //from File Manager
			           	   selectedPath1 = path;           	            	
//			   			   options.inSampleSize = 8;
//			   			   demoBitmap  = BitmapFactory.decodeFile(path,options);
//			   			   bitmap = Bitmap.createScaledBitmap(demoBitmap,640, 640, false);
			   			bitmap = BitmapFactory.decodeFile(path);
			           if (path != null)
//			        	   options.inSampleSize = 0;
//						   demoBitmap  = BitmapFactory.decodeFile(path,options);
//						   bitmap = Bitmap.createScaledBitmap(bitmap,1024, 1024, false);
						   bitmap = BitmapFactory.decodeFile(path);               
			       } else {
			           path    = mImageCaptureUri.getPath();
			           selectedPath1 = path;
//			           options.inSampleSize = 8;
//			           demoBitmap  = BitmapFactory.decodeFile(path);
//					   bitmap = Bitmap.createScaledBitmap(demoBitmap,480, 480, false);
			           bitmap = BitmapFactory.decodeFile(path);
			           }
			       makePostNow.setVisibility(View.GONE);
			       mImageView.setImageBitmap(bitmap);
			       mImageView.setTag(selectedPath1);
			       scaleImage();
			       mImageView.setVisibility(View.VISIBLE);
			       uploadPostImage.setText("Upload");
			       if(mImageView.getDrawable() != null){
			    	   selAnother.setVisibility(View.VISIBLE);
							mImageView.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									openPhoto();
								}
							});
							cancelBtn.setVisibility(View.VISIBLE);
							cancelBtn.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mImageView.setImageDrawable(null);
									mImageView.setVisibility(View.GONE);
									uploadPostImage.setText("Yes, I have a relevant image");
									cancelBtn.setVisibility(View.GONE);
									selAnother.setVisibility(View.GONE);
								}
							});
							
			       }
			       
				   }
			       
			    }
			 
			    public String getRealPathFromURI(Uri uri) {
			        String[] projection = { MediaStore.Images.Media.DATA };
			        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
			        if (cursor == null){
			        	return null;
			        }
			        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			        cursor.moveToFirst();
			        return cursor.getString(column_index);
			    }
			    
			    
			    private void scaleImage()
			    {
			        // Get the ImageView and its bitmap
			        ImageView view = (ImageView) findViewById(R.id.confirmImage);
			        Drawable drawing = view.getDrawable();
			        if (drawing == null) {
			            return; // Checking for null & return, as suggested in comments
			        }
			        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

			        // Get current dimensions AND the desired bounding box
			        int width = bitmap.getWidth();
			        int height = bitmap.getHeight();
			        int bounding = dpToPx(250);
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
			        BitmapDrawable result = new BitmapDrawable(getApplication().getResources(),scaledBitmap);
			        Log.i("Test", "scaled width = " + Integer.toString(width));
			        Log.i("Test", "scaled height = " + Integer.toString(height));

			        // Apply the scaled bitmap
			        view.setImageDrawable(result);

			        // Now change ImageView's dimensions to match the scaled image
			        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams(); 
			        params.width = width;
			        params.height = height;
			        view.setLayoutParams(params);

			        Log.i("Test", "done");
//			         Get the ImageView and its bitmap
			    }

			    private int dpToPx(int dp)
			    {
			        float density = getApplicationContext().getResources().getDisplayMetrics().density;
			        return Math.round((float)dp * density);
			    }
			    
			    @Override
				protected void onSaveInstanceState(Bundle outState) {
					super.onSaveInstanceState(outState);

					outState.putString("Path", selectedPath1);
				}
			    
			    @Override
				protected void onRestoreInstanceState(Bundle savedInstanceState) {
					super.onRestoreInstanceState(savedInstanceState);

					selectedPath1 = savedInstanceState.getString("Path");
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
			    
			    private class Uploading extends AsyncTask<Void, Integer, String> {
			        @Override
			        protected void onPreExecute() {
			            super.onPreExecute();
			            pb1.setVisibility(View.VISIBLE);
			            uploadPostImage.setVisibility(View.GONE);
			            makePostNow.setVisibility(View.GONE);
			            cancelBtn.setVisibility(View.GONE);
			            backHome.setVisibility(View.GONE);
			            Toast.makeText(getApplicationContext(),"Please wait for the pic to upload.", Toast.LENGTH_LONG).show();

			        }
			        @Override
					protected String doInBackground(Void... arg0) {
			        	StrictMode.enableDefaults();
			        	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			            NetworkInfo netInfo = cm.getActiveNetworkInfo();
			            if (netInfo != null && netInfo.isConnected()) {
			                try {
			                    URL url = new URL("http://www.google.com/");
			                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			                    urlc.setConnectTimeout(15000);
			                    urlc.connect();
			                    if (urlc.getResponseCode() == 200) {
			                    	 db = new DatabaseHandler(MakeAPostActivity.this);
			                    	 String SetServerString = "";
			        				 File file = new File(selectedPath1);
			        	             String urlString = "http://skpsash.site50.net/upload_posts_images/upload_image_forposts.php";
			        	             HttpClient client = new DefaultHttpClient();
			        	             HttpPost post = new HttpPost(urlString);
//			        	             MultipartEntity reqEntity = new MultipartEntity();
			        	             MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			        	             /* setting a HttpMultipartMode */
			        	             builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			        	             /* adding an image part */
			        	             FileBody bin1 = new FileBody(file);
			        	             builder.addPart("uploadedfile1", bin1);
//			        	             reqEntity.addPart("uploadedfile1", bin1);
			        	             builder.addPart("user", new StringBody(db.getUserId(),ContentType.TEXT_PLAIN));
			        	             builder.addPart("userName", new StringBody(db.getUserName(),ContentType.TEXT_PLAIN));
			        	             builder.addPart("imageTag", new StringBody("Image is Set",ContentType.TEXT_PLAIN));
			        	             HttpEntity reqEntity = builder.build();
			        	             long totalSize = reqEntity.getContentLength();
			        	             long num;
			        	             
			        	          // Execute POST request to the given URL
			        				 HttpResponse httpResponse = null;
			        				 org.apache.http.client.ResponseHandler<String> responseHandler = new org.apache.http.impl.client.BasicResponseHandler();
			        				 SetServerString = client.execute(post, responseHandler);
			        				 if(SetServerString == "1"){
			        					 result = "This image type is incompatible with YouShare server.Please use an image of another type(Like jpg,gif,png,bmp).";
			        				 }
			        				 
			        				 else if(SetServerString == "2"){
			        					 result = "Please upload an image of size less than 2MB.";
			        				 }
			        				 
			        				 else {
			        					 float currProgress = 0;
			        				     float prevProgress = 0;
			        					 for(num=0;num<=totalSize;num++)
			            	             {
			        				     currProgress = ((float)num / (float) totalSize);

			        				     if (currProgress - prevProgress >= 0.005)
			        				     {
//			            	             publishProgress((int) ((num / (float) totalSize) * 100));
			        				     publishProgress((int) (currProgress * 100));	 
			            	             prevProgress = currProgress;
			        				     }
			            	             }
			            	             post.setEntity(reqEntity);
			        				 httpResponse = client.execute(post);
			        				 resEntity = httpResponse.getEntity();
			        	             result = EntityUtils.toString(resEntity);
			        				 }
			                    }
			                    else{
			                    	new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
			        		        .setTitle("Connection Error")
			        		        .setMessage("Connection Time Out.Please try again later.")
			        		        .setPositiveButton("Ok", null)
			        		        .create().show();
			                    }
			                } catch (MalformedURLException e1) {
			                    e1.printStackTrace();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                }
			            }
						
						return result;

				  
			        	
					}
			        
			        @Override
			        protected void onProgressUpdate(Integer... progress) {
			        	pb1.setProgress((int)progress[0]);
			        }
			        
			        @Override
			        protected void onPostExecute(String result) {
			            super.onPostExecute(result);
			            backHome.setVisibility(View.VISIBLE);
			            if(pb1.getVisibility() == View.VISIBLE)
			               pb1.setVisibility(View.GONE);
			            if(result == null){
			            	new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
					        .setTitle("Network Error")
					        .setMessage("There is some error in network.Please make sure you have internet access in your device.Please try again later if problem persists.")
					        .setPositiveButton("Ok", null)
					        .create().show();
			            }
			            makePostNow.setVisibility(View.VISIBLE);
//			            resText.setText(result);
//			            resText.setTextColor(getResources().getColor(R.color.darkblue));
			            Toast.makeText(getApplicationContext(),"Upload Complete.", Toast.LENGTH_LONG).show();
			           
			        }

					
			    }
			    
			    
			    private class UploadingPost extends AsyncTask<Void, Integer, String> {
			    	
			    	private ProgressDialog pDialog;
			    	
			        			        
			        @Override
			        protected void onPreExecute() {
			            super.onPreExecute();
			            pDialog = new ProgressDialog(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK);
			            pDialog.setTitle("Contacting Servers");
			            pDialog.setMessage("Making A New Post...");
			            pDialog.setIndeterminate(false);
			            pDialog.setCancelable(true);
			            pDialog.show();

			        }
			        @Override
					protected String doInBackground(Void... arg0) {
			        	jsonParser = new JSONParser();
			        	ConnectivityManager cm = (ConnectivityManager) MakeAPostActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			            NetworkInfo netInfo = cm.getActiveNetworkInfo();
			            if (netInfo != null && netInfo.isConnected()) {
			                try {
			                    URL url = new URL("http://www.google.com/");
			                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			                    urlc.setConnectTimeout(15000);
			                    urlc.connect();
			                    if (urlc.getResponseCode() == 200) {
			                    	 db = new DatabaseHandler(MakeAPostActivity.this);
			                    	 Log.d("message",message.getText().toString());
									 Log.d("link",link.getText().toString());
									 Log.d("category",selectedCategory);
			        	             String urlString = "http://skpsash.site50.net/upload_posts_images/upload_image_forposts.php";
			        	             List<NameValuePair> params = new ArrayList<NameValuePair>();
									 params.add(new BasicNameValuePair("noImageTag", "without image post"));
									 params.add(new BasicNameValuePair("uId",db.getUserId()));
									 params.add(new BasicNameValuePair("uName",db.getUserName()));
									 if(message.getText().toString() !=null && !message.getText().toString().isEmpty()){
										 params.add(new BasicNameValuePair("message",message.getText().toString()));
									 }
									 else{
										 params.add(new BasicNameValuePair("message",""));
									 }
									 
									 if(link.getText().toString() !=null && !link.getText().toString().isEmpty()){
										 params.add(new BasicNameValuePair("link",link.getText().toString()));
									 }
									 else{
										 params.add(new BasicNameValuePair("link",""));
									 }
									 
									 params.add(new BasicNameValuePair("category", selectedCategory));
									 
									 if(newstr != null && !newstr.isEmpty()){
										 params.add(new BasicNameValuePair("filename", newstr));
									 }
									
									 
						             json = jsonParser.getJSONFromUrl(urlString,params);
						             
//						             if(json.getString("insert").toString() != null){
//						             String json1 = json.getString("insert").toString();
//						             Log.d("insert", json1);
//						             }
//						             if(json.getString("update").toString() != null){
//						             String json2 = json.getString("update").toString();
//						             Log.d("update", json2);
//						             }
			                    }
			                    else{
			                    	new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
			        		        .setTitle("Connection Error")
			        		        .setMessage("Connection Time Out.Please try again later.")
			        		        .setPositiveButton("Ok", null)
			        		        .create().show();
			                    }
			                }
			                  catch (MalformedURLException e1) {
			                    e1.printStackTrace();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                } 
//			                    catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
			            }
						
						return result;

				  
			        	
					}
			        
			       
			        
			        @Override
			        protected void onPostExecute(String result) {
			            super.onPostExecute(result);
			            pDialog.dismiss();
			            new AlertDialog.Builder(MakeAPostActivity.this,AlertDialog.THEME_HOLO_DARK)
				        .setTitle("Post Created")
				        .setMessage("Your post is now created.")
				        .setPositiveButton("Ok", new OnClickListener() {

        		            public void onClick(DialogInterface arg0, int arg1) {
        		            	selectedCategory = null;
        		            	startActivity(getIntent());
        		            }		        
        		        })
				        .create().show();
//			            Toast.makeText(getApplicationContext(),"Post Upload Complete.", Toast.LENGTH_LONG).show();
			           
			        }

					
			    }
			    
			    
			    
}
				 
	
	
	
	
//	@Override 
//	protected void onSaveInstanceState(Bundle outState) { /* do nothing */ }


