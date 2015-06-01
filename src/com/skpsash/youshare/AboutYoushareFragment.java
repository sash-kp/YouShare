package com.skpsash.youshare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutYoushareFragment extends Fragment {
	TextView tv,tv1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		 
	        
	        
		tv = (TextView) getActivity().findViewById(R.id.htmlText);
		
		
		return rootView;
	}
	 @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		SpannableString content = new SpannableString("Get Familiar With YouShare");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv1 = (TextView) getActivity().findViewById(R.id.aboutYoushareHead);
		tv1.setText(content);
		tv = (TextView) getActivity().findViewById(R.id.htmlText);
		tv.setText(Html.fromHtml("<html><body>" +
				"<p align=\"justify\">If you spend money/time on useful stuffs,you will definitely gain more than you spend.<b> Sharing is gaining</b>. <b>YouShare</b> is developed mainly to serve the purpose of <b>Sharing/Gaining</b> of useful information/facts/knowledge in a well organised way.</p>"
				+"<p align=\"justify\">Using <b>YouShare</b> you can make your own posts(can contain any link/photo) and assign a category"
				+" to your posts.For example, if you are posting something about any movie, you can assign \"Movies\" category to your post. Once you have added a post successfully, your post will be visible(along with your username and profile pic) to all other registered YouShare members. All the posts made on YouShare will category filtered.That means the movie post made by you can be visible in \"Movies\" category zone only. It will not be visible in \"Just Fun\" category zone. Once you register, it will be easy for you to explore what features <b>YouShare</b> provide.</p>"
				+"<p align=\"justify\">As this is the very first version(v1.1) of YouShare, there is much scope of improvement.However your registration details like email id and YouShare password will not be disclosed to anyone in any case.The YouShare password which you enter while registering, will be stored in YouShare server database in such a manner that even the developer of the app can't access it.And that's the reason \"Forgot Password\" and \"Change Password\" features are also provided.</p>"
				+"<p align=\"justify\">Any suggestion/constructive criticism regarding the app is always welcome.Please feel free to contact the developer if you have any issue using the app.Thank you for reading.Happy YouSharing.</p>"+
		"<p align=\"justify\">P.S: Please provide your valid email address while registering.Else You won't be able to register as a confirmation mail will be sent to your provided email id only.</p>"+
		"<p align=\"justify\">App Developer<br>"+"Sashwat Kumar Padhy<br>Email : sashwat.padhy@gmail.com</p>"));
		
	}
	
}
