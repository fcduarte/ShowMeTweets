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
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "profile_background_url")
	private String profileBackgroundUrl;
	
	@Column(name = "followers_count")
	private int followersCount;
	
	@Column(name = "following_count")
	private int followingCount;
	
	@Column(name = "tweets_count")
	private int tweetsCount;
	
	public User() {
		super();
	}

	public User(String name, String username, String avatarUrl, Long twitterId,
			String description, String profileBackgroundUrl,
			int followersCount, int followingCount, int tweetsCount) {
		super();
		this.name = name;
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.twitterId = twitterId;
		this.description = description;
		this.profileBackgroundUrl = profileBackgroundUrl;
		this.followersCount = followersCount;
		this.followingCount = followingCount;
		this.tweetsCount = tweetsCount;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfileBackgroundUrl() {
		return profileBackgroundUrl;
	}

	public void setProfileBackgroundUrl(String profileBackgroundUrl) {
		this.profileBackgroundUrl = profileBackgroundUrl;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}
	
	public int getTweetsCount() {
		return tweetsCount;
	}

	public void setTweetsCount(int tweetsCount) {
		this.tweetsCount = tweetsCount;
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
		this.description = remoteUser.getDescription();
		this.profileBackgroundUrl = remoteUser.getProfileBannerMobileURL();
		this.followersCount = remoteUser.getFollowersCount();
		this.followingCount = remoteUser.getFriendsCount();
		this.tweetsCount = remoteUser.getStatusesCount();
	}

}
