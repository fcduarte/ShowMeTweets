package com.fcduarte.showmetweets.activities;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.utils.TwitterPreferencesUtils;

public class SignInWithTwitter extends Activity {

	protected static final String TWITTER_CONSUMER_KEY = "QZhiS4PAxHk1gfM94K2iSkxpr";
	protected static final String TWITTER_CONSUMER_SECRET = "aNQg7QPrzNKtZc9RU8Fu85Gj4VFOgGu5qHSYMIK3fWeXSCffFd";
	protected static final String TWITTER_CALLBACK_URL = "oauth://showmetweets";
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	private ImageButton mSignInTwitterButton;
	private Twitter mTwitter;
	private TwitterPreferencesUtils mTwitterPreferencesUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_with_twitter);
		this.getActionBar().hide();

		mSignInTwitterButton = (ImageButton) findViewById(R.id.btn_sign_in);
		mSignInTwitterButton.setOnClickListener(mSignButtonListener);
		
		mTwitterPreferencesUtils = new TwitterPreferencesUtils(this);
		mTwitter = buildTwitter();
		
		AccessToken accessToken = mTwitterPreferencesUtils.loadAccessToken();
		
		if (accessToken != null) {
			goToMainScreen(accessToken);
		}
		
		processTwitterCallback(getIntent().getData());
	}

	private void goToMainScreen(AccessToken accessToken) {
		mTwitter.setOAuthAccessToken(accessToken);
		
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra(HomeActivity.TWITTER_CLIENT_KEY, mTwitter);
		
		startActivity(intent);
	}

	private Twitter buildTwitter() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = builder.build();

		TwitterFactory factory = new TwitterFactory(configuration);
		return factory.getInstance();
	}

	private void processTwitterCallback(Uri data) {
		if (data == null || !data.toString().startsWith(TWITTER_CALLBACK_URL)) {
			return;
		}
		
        final String verifier = data.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
        
        if (verifier == null) {
        	return;
        }
        
        Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					AccessToken accessToken = mTwitter.getOAuthAccessToken(mTwitterPreferencesUtils.loadRequestToken(), verifier);
					mTwitterPreferencesUtils.saveAccessToken(accessToken.getToken(), accessToken.getTokenSecret());
					goToMainScreen(accessToken);
				} catch (TwitterException e) {
					Log.e("SignInWithTwitter", "Error", e);
					mTwitterPreferencesUtils.clear();
				}				
			}
		});
        thread.start();
	}

	private OnClickListener mSignButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						RequestToken requestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						mTwitterPreferencesUtils.saveRequestToken(requestToken.getToken(), requestToken.getTokenSecret());
						
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						
						SignInWithTwitter.this.startActivity(intent);
					} catch (TwitterException e) {
						Log.e("SignIn", "Error requestToken", e);
						mTwitterPreferencesUtils.clear();
					}
				}
			});

			thread.start();
		}
	};

}
