package com.skpsash.youshare.adapter;

public class FeedItem {
	private int postId;
	private String username, message, image, profilepicpath, link,unique_user_id,category;
	String created_at;

	public FeedItem() {
	}

	public FeedItem(int id, String name, String image, String status,
			String profilePic, String timeStamp, String url,String user_id,String category) {
		super();
		this.postId = id;
		this.username = name;
		this.image = image;
		this.message = status;
		this.profilepicpath = profilePic;
		this.created_at = timeStamp;
		this.link = url;
		this.unique_user_id = user_id;
		this.category = category;
	}

	public int getId() {
		return postId;
	}

	public void setId(int id) {
		this.postId = id;
	}
	
	
	public String getUserId() {
		return unique_user_id;
	}

	public void setUserId(String user_id) {
		this.unique_user_id = user_id;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return username;
	}

	public void setName(String name) {
		this.username = name;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getStatus() {
		return message;
	}

	public void setStatus(String status) {
		this.message = status;
	}

	public String getProfilePic() {
		return profilepicpath;
	}

	public void setProfilePic(String profilePic) {
		this.profilepicpath = profilePic;
	}

	public String getTimeStamp() {
		return created_at;
	}

	public void setTimeStamp(String string) {
		this.created_at = string;
	}

	public String getUrl() {
		return link;
	}

	public void setUrl(String url) {
		this.link = url;
	}
}
