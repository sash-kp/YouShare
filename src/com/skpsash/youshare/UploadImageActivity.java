package com.skpsash.youshare;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skpsash.youshare.adapter.DatabaseHandler;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
 
public class UploadImageActivity extends Activity {
	
	static String path     = "";
    private Uri mImageCaptureUri;
    String result = "";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    String selectedPath1 = "NONE";
    private ImageView mImageView;
    static ProgressDialog progressDialog;
    Button b1,b3,backtoHome;
    TextView resText;
    HttpEntity resEntity;
    DatabaseHandler db;
    static boolean picIsSet = false;
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimage);
        StrictMode.enableDefaults();
        b1 = (Button)findViewById(R.id.setPic);
        b3 = (Button)findViewById(R.id.uploadPic);
        backtoHome = (Button) findViewById(R.id.backtoPostpage);
        mImageView = (ImageView) findViewById(R.id.viewImage);
//        Uri mImageViewUri = Uri.parse("android.resource://com.skpsash.youshare/" + R.drawable.empty_profile);
        resText = (TextView)findViewById(R.id.result);
        resText.setText("*Please Upload an image of size less than 2MB.");
        resText.setTextColor(getResources().getColor(R.color.reddish));
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoto();
            }
        });
        backtoHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	UploadImageActivity.this.startActivity(new Intent(UploadImageActivity.this,HomeScreenActivity.class));  
                return;
            }
        });
        b3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
            	if(mImageView.getDrawable() != null){
            		new AlertDialog.Builder(UploadImageActivity.this,AlertDialog.THEME_HOLO_DARK)
    		        .setTitle("Confirm Upload")
    		        .setMessage("Are You Sure You want to upload this photo as your profile pic?")
    		        .setNegativeButton("No", null)
    		        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

    		            public void onClick(DialogInterface arg0, int arg1) {
    		            	new Uploading().execute();   
    		            	picIsSet = true;
    		            }		        
    		        }).create().show();
            		
            	}
            	else{
            		Toast.makeText(getApplicationContext(), "Please set an image first.", Toast.LENGTH_LONG).show();
            	}
            }
        });
 
    }
     
     @Override
     protected void onPause() {
         // TODO Auto-generated method stub
         super.onPause();
     }
     
     @Override
    public void onBackPressed() {
    	 this.startActivity(new Intent(UploadImageActivity.this,HomeScreenActivity.class));  

    	 return;  
    }
     
     
     
     public static class MyAlertDialogFragment extends android.app.DialogFragment {
    	    	    	
    	 public MyAlertDialogFragment(){
    		 
    	 }
    	 @Override
    	 public Dialog onCreateDialog(Bundle savedInstanceState) {
    		 progressDialog = new ProgressDialog(getActivity(),AlertDialog.THEME_HOLO_DARK);
    		 progressDialog.setTitle("Upload Manager");
    		 progressDialog.setMessage("Uploading Your File. Please wait...");
        	 progressDialog.setIndeterminate(false);
        	 progressDialog.setMax(100);
        	 progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	 progressDialog.setCancelable(false);
        	 progressDialog.show();
             return progressDialog;
         	  
    	 }
   }
 
     
    public void openPhoto(){
    	  final String [] items           = new String [] {"From Camera", "From SD Card"};
          ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
          AlertDialog.Builder builder     = new AlertDialog.Builder(this);
   
          builder.setTitle("Select Image");
          builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
              public void onClick( DialogInterface dialog, int item ) {
                  if (item == 0) {
                      Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                      
                      File file        = new File(Environment.getExternalStorageDirectory(),
                                          "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                      mImageCaptureUri = Uri.fromFile(file);
   
                      try {
                    	  intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
                          intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                          intent.putExtra("return-data", true);
                          
                          startActivityForResult(intent, PICK_FROM_CAMERA);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
   
                      dialog.cancel();
                  } else {
                      Intent intent = new Intent();   
                      intent.setType("image/*");
                      intent.setAction(Intent.ACTION_GET_CONTENT);
                      startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                  }
              }
          } );
   
          final AlertDialog dialog = builder.create();
          dialog.show();
   }
    
    
 
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (resultCode != RESULT_OK){
   	    return;   
	   }
		
	   else{
       Bitmap bitmap;
       
//       BitmapFactory.Options options = new BitmapFactory.Options();

       if (requestCode == PICK_FROM_FILE) {
           mImageCaptureUri = data.getData();
           path = getRealPathFromURI(mImageCaptureUri); //from Gallery
           selectedPath1 = path;
           if (path == null)
               path = mImageCaptureUri.getPath(); //from File Manager
           	   selectedPath1 = path;           	            	
//   			   options.inSampleSize = 8;
//   			   demoBitmap  = BitmapFactory.decodeFile(path,options);
//   			   bitmap = Bitmap.createScaledBitmap(demoBitmap,640, 640, false);
   			bitmap = BitmapFactory.decodeFile(path);
           if (path != null)
//        	   options.inSampleSize = 0;
//			   demoBitmap  = BitmapFactory.decodeFile(path,options);
//			   bitmap = Bitmap.createScaledBitmap(bitmap,1024, 1024, false);
        	   selectedPath1 = path; 
			   bitmap = BitmapFactory.decodeFile(path);               
       } else {
           path    = mImageCaptureUri.getPath();
           selectedPath1 = path;
//           options.inSampleSize = 8;
//           demoBitmap  = BitmapFactory.decodeFile(path);
//		   bitmap = Bitmap.createScaledBitmap(demoBitmap,480, 480, false);
           bitmap = BitmapFactory.decodeFile(path);
           }
       if(findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/jpeg") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/pjpeg") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/png") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/gif") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/bmp")){
       mImageView.setImageBitmap(bitmap);
       scaleImage();
       }
       else{
    	   Log.d("mime",findMimeTypeOfFile(path).toString());
    	   new AlertDialog.Builder(UploadImageActivity.this,AlertDialog.THEME_HOLO_DARK)
	        .setTitle("Unsupported Format")
	        .setMessage("Sorry. Only jpeg/png/bmp/gif  formatted images are accepted.")
	        .setPositiveButton("Ok", null)
	        .create().show();
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
    
    public static String findMimeTypeOfFile(String originalPath)
    {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
        Collection<?> mimeTypes = MimeUtil.getMimeTypes(originalPath);

        if (mimeTypes.isEmpty())
        {
            /* Unknown file type, so here we just give Jesus the wheel and open it as a plain text file */
            return "text/plain";
        } else
        {
            Iterator<?> iterator = mimeTypes.iterator();
            MimeType mimeType = (MimeType) iterator.next();
            String mimetype = mimeType.getMediaType() + "/" + mimeType.getSubType();
            return mimetype;
        }
    }
    
    private void scaleImage()
    {
        // Get the ImageView and its bitmap
        ImageView view = (ImageView) findViewById(R.id.viewImage);
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions AND the desired bounding box
        if(findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/jpeg") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/pjpeg") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/png") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/gif") || findMimeTypeOfFile(path).toString().equalsIgnoreCase("image/bmp")){
        	try{
        	int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int bounding = dpToPx(250);
            Log.i("Test", "original width = " + Integer.toString(width));
            Log.i("Test", "original height = " + Integer.toString(height));
            Log.i("Test", "bounding = " + Integer.toString(bounding));
            
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
        	}
        	catch(Exception e){
            	e.printStackTrace();
            	new AlertDialog.Builder(UploadImageActivity.this,AlertDialog.THEME_HOLO_DARK)
    	        .setTitle("Invalid Image")
    	        .setMessage("This is not a valid image file.")
    	        .setPositiveButton("Ok", null)
    	        .create().show();
            }
        }	
        
        
        

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.  
        
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
    
    private class Uploading extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            android.app.FragmentManager ft = getFragmentManager();
            MyAlertDialogFragment newFragment = new MyAlertDialogFragment ();
            newFragment.show(ft, "dialog");
            
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
                    	 db = new DatabaseHandler(UploadImageActivity.this);
                    	 String SetServerString = "";
        				 File file = new File(selectedPath1);
        	             String urlString = "http://skpsash.site50.net/uploadImg_server_files/upload_media_test.php";
        	             HttpClient client = new DefaultHttpClient();
        	             HttpPost post = new HttpPost(urlString);
//        	             MultipartEntity reqEntity = new MultipartEntity();
        	             MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        	             /* setting a HttpMultipartMode */
        	             builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        	             /* adding an image part */
        	             FileBody bin1 = new FileBody(file);
        	             builder.addPart("uploadedfile1", bin1);
//        	             reqEntity.addPart("uploadedfile1", bin1);
        	             builder.addPart("user", new StringBody(db.getUserId(),ContentType.TEXT_PLAIN));
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
                    	new AlertDialog.Builder(UploadImageActivity.this,AlertDialog.THEME_HOLO_DARK)
        		        .setTitle("Connection Error")
        		        .setMessage("Connection Time Out.Please try again later.")
        		        .setPositiveButton("Ok", null)
        		        .create().show();
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
			
			return result;

	  
        	
		}
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
        	progressDialog.setProgress((int)progress[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressDialog.isShowing())
            progressDialog.dismiss();
            if(result == null){
            	new AlertDialog.Builder(UploadImageActivity.this,AlertDialog.THEME_HOLO_DARK)
		        .setTitle("Network Error")
		        .setMessage("There is some error in network.Please make sure you have internet access in your device.Please try again later if problem persists.")
		        .setPositiveButton("Ok", null)
		        .create().show();
            }
//            resText.setText(result);
//            resText.setTextColor(getResources().getColor(R.color.darkblue));
            Toast.makeText(getApplicationContext(),"Upload Complete.", Toast.LENGTH_LONG).show();
        }

		
    }

}   

