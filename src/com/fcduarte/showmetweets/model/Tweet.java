package com.fcduarte.showmetweets.model;

import java.io.Serializable;
import java.util.Date;

import twitter4j.MediaEntity;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "tweets")
public class Tweet extends Model implements Serializable, Comparable<Tweet> {

	private static final long serialVersionUID = -1345947692824479432L;
	
	@Column(name = "body")
	private String body;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "tweet_id")
	private Long tweetId;

	@Column(name = "users")
	private User user;
	
	@Column(name = "favorite_count")
	private int favoriteCount;
	
	@Column(name = "retweet_count")
	private int retweetCount;
	
	@Column(name = "body_media_url")
	private String bodyMediaURL;

	public Tweet() {
		super();
	}

	public Tweet(String body, Date createdAt, Long tweetId, User user, int favoriteCount,
			int retweetCount, String bodyMediaURL) {
		super();
		this.body = body;
		this.createdAt = createdAt;
		this.tweetId = tweetId;
		this.user = user;
		this.favoriteCount = favoriteCount;
		this.retweetCount = retweetCount;
		this.bodyMediaURL = bodyMediaURL;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCreatedAtFormatted() {
		return DateUtils.getRelativeTimeSpanString(
				this.getCreatedAt().getTime(), new Date().getTime(), 0,
				DateUtils.FORMAT_ABBREV_RELATIVE).toString();
	}
	
	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}
	
	public String getBodyMediaURL() {
		return bodyMediaURL;
	}

	public void setBodyMediaURL(String bodyMediaURL) {
		this.bodyMediaURL = bodyMediaURL;
	}

	@Override
	public int compareTo(Tweet another) {
		return another.getCreatedAt().compareTo(this.getCreatedAt());
	}

	public void buildFromRemote(twitter4j.Status status) {
		this.body = status.getText();
		this.createdAt = status.getCreatedAt();
		this.tweetId = status.getId();
		
		User user = new User();
		user.buildFromRemote(status.getUser());
		this.user = user;
		this.favoriteCount = status.getFavoriteCount();
		this.retweetCount = status.getRetweetCount();
		
		if (status.getMediaEntities().length > 0) {
			MediaEntity mediaEntity = status.getMediaEntities()[0];
			this.bodyMediaURL = mediaEntity.getMediaURL();
		}
	}

}
