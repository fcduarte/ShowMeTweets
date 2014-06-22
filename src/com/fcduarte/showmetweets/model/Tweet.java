package com.fcduarte.showmetweets.model;

import java.io.Serializable;
import java.util.Date;

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

	public Tweet() {
		super();
	}

	public Tweet(String body, Date createdAt, Long tweetId, User user) {
		super();
		this.body = body;
		this.createdAt = createdAt;
		this.tweetId = tweetId;
		this.user = user;
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
	}

}
