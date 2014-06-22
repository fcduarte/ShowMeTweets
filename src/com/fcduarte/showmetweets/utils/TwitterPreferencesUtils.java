package com.fcduarte.showmetweets.utils;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fcduarte.showmetweets.model.User;

public class TwitterPreferencesUtils {
	
	private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String REQUEST_TOKEN_SECRET_KEY = "request_token_secret";
	private static final String REQUEST_TOKEN_KEY = "request_token";
	private static final String LOGGED_USER_KEY = "logged-user";
	
	private SharedPreferences prefs;
	
	public TwitterPreferencesUtils(Context context) {
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
		editor.putString(ACCESS_TOKEN_SECRET_KEY, accessToken);
		
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

}
