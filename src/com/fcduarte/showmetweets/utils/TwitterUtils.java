package com.fcduarte.showmetweets.utils;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fcduarte.showmetweets.model.User;

public class TwitterUtils {
	
	protected static final String TWITTER_CONSUMER_KEY = "QZhiS4PAxHk1gfM94K2iSkxpr";
	protected static final String TWITTER_CONSUMER_SECRET = "aNQg7QPrzNKtZc9RU8Fu85Gj4VFOgGu5qHSYMIK3fWeXSCffFd";
	private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String REQUEST_TOKEN_SECRET_KEY = "request_token_secret";
	private static final String REQUEST_TOKEN_KEY = "request_token";
	private static final String LOGGED_USER_KEY = "logged-user";
	
	private SharedPreferences prefs;
	
	public TwitterUtils(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void saveRequestToken(String requestToken, String requestTokenSecret) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(REQUEST_TOKEN_KEY, requestToken);
		editor.putString(REQUEST_TOKEN_SECRET_KEY, requestTokenSecret);

		editor.commit();
	}
	
	public RequestToken loadRequestToken() {
		String reqToken = prefs.getString(REQUEST_TOKEN_KEY, null);
		String reqTokenSecret = prefs.getString(REQUEST_TOKEN_SECRET_KEY, null);

		return new RequestToken(reqToken, reqTokenSecret);
	}
	
	public void saveAccessToken(String accessToken, String accessTokenSecret) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(ACCESS_TOKEN_KEY, accessToken);
		editor.putString(ACCESS_TOKEN_SECRET_KEY, accessTokenSecret);
		
		editor.commit();
	}
	
	public AccessToken loadAccessToken() {
		String accessToken = prefs.getString(ACCESS_TOKEN_KEY, null);
		String accessTokenSecret = prefs.getString(ACCESS_TOKEN_SECRET_KEY, null);

		if (accessToken == null || accessTokenSecret == null) {
			return null;
		}
		
		return new AccessToken(accessToken, accessTokenSecret);
	}
	
	public void clear() {
		prefs.edit().clear().commit();
	}
	
	public void saveLoggedUser(User loggedUser) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(LOGGED_USER_KEY, loggedUser.getUsername());
		
		editor.commit();
		
	}
	
	public String loadLoggedUser() {
		return prefs.getString(LOGGED_USER_KEY, null);
	}
	
	public boolean userChanged(User user) {
		String loggedUser = loadLoggedUser();
		return loggedUser == null || !user.getUsername().equals(loggedUser);
	}
	
	public Twitter getTwitterClient() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = builder.build();

		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
		
		AccessToken accessToken = loadAccessToken();
		
		if (accessToken != null) {
			twitter.setOAuthAccessToken(accessToken);
		}
		return twitter;
	}

}
