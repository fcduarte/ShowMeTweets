package com.fcduarte.showmetweets.model;

import java.io.Serializable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "users")
public class User extends Model implements Serializable {

	private static final long serialVersionUID = -2164731057285656343L;

	@Column(name = "name")
	private String name;

	@Column(name = "username")
	private String username;

	@Column(name = "avatar_url")
	private String avatarUrl;
	
	@Column(name = "twitter_id")
	private Long twitterId;
	
	public User() {
		super();
	}

	public User(String name, String username, String avatarUrl, Long twitterId) {
		super();
		this.name = name;
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.twitterId = twitterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Long getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(Long twitterId) {
		this.twitterId = twitterId;
	}

	public String getUsernameFormatted() {
		if (this.username == null) {
			return null;
		}

		return String.format("@%s", this.username);
	}

	public void buildFromRemote(twitter4j.User remoteUser) {
		this.name = remoteUser.getName();
		this.username = remoteUser.getScreenName();
		this.avatarUrl = remoteUser.getProfileImageURL();
		this.twitterId = remoteUser.getId();
		
	}

}
