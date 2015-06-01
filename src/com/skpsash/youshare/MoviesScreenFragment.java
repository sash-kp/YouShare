package com.skpsash.youshare;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.skpsash.youshare.adapter.AppController;
import com.skpsash.youshare.adapter.FeedItem;
import com.skpsash.youshare.adapter.FeedListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MoviesScreenFragment extends Fragment {

	private ListView listView;
	private FeedListAdapter listAdapter;
	private List<FeedItem> feedItems;
	private String URL_FEED = "http://skpsash.site50.net/get_posts_data/getJsonFromPostsForMovies.php";
	ProgressBar pb;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
		listView = (ListView) rootView.findViewById(R.id.listMovies);
		feedItems = new ArrayList<FeedItem>();
		listAdapter = new FeedListAdapter(getActivity(), feedItems);
		return rootView;
	}
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		pb = (ProgressBar) getActivity().findViewById(R.id.progressmovies);
		pb.setVisibility(View.VISIBLE);
		// We first check for cached request
				Cache cache = AppController.getInstance().getRequestQueue().getCache();
				Entry entry = cache.get(URL_FEED);
				if (entry != null) {
					// fetch the data from cache
					try {
						String data = new String(entry.data, "UTF-8");
						try {
							Log.d("Yeah","Yes");
							parseJsonFeed(new JSONObject(data));
							pb.setVisibility(View.GONE);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

				}
				
				else {
					// making fresh volley request and getting json
					JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
							URL_FEED, null, new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									VolleyLog.d("What", "Response: " + response.toString());
									if (response != null) {
										parseJsonFeed(response);
										pb.setVisibility(View.GONE);
									}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									VolleyLog.d("What Re", "Error: " + error.getMessage());
									pb.setVisibility(View.GONE);
								}
							});

					// Adding request to volley request queue
					AppController.getInstance().addToRequestQueue(jsonReq);
				}
				listView.setAdapter(listAdapter);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		pb = (ProgressBar) getActivity().findViewById(R.id.progressmovies);
		pb.setVisibility(View.VISIBLE);
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
				URL_FEED, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						VolleyLog.d("What", "Response: " + response.toString());
						if (response != null) {
							parseJsonFeed(response);
							pb.setVisibility(View.GONE);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("What Re", "Error: " + error.getMessage());
						pb.setVisibility(View.GONE);
					}
				});

		// Adding request to volley request queue
		AppController.getInstance().addToRequestQueue(jsonReq);
		listAdapter.clear();
		listAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("feed");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				FeedItem item = new FeedItem();
				item.setId(feedObj.getInt("postid"));
				item.setName(feedObj.getString("username"));

				// Image might be null sometimes
				String image = feedObj.isNull("image") ? null : feedObj
						.getString("image");
				item.setImge(image);
				item.setStatus(feedObj.getString("message"));
				item.setProfilePic(feedObj.getString("profilepicpath"));
				item.setTimeStamp(feedObj.getString("created_at"));

				// url might be null sometimes
				String feedUrl = feedObj.isNull("link") ? null : feedObj
						.getString("link");
				item.setUrl(feedUrl);

				feedItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.main, menu);
	    super.onCreateOptionsMenu(menu,inflater);
	    setHasOptionsMenu(true);
	}

}
