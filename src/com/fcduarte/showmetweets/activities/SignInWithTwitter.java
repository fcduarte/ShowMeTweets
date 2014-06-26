package com.fcduarte.showmetweets.activities;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.utils.TwitterUtils;

public class SignInWithTwitter extends Activity {

	protected static final String TWITTER_CALLBACK_URL = "oauth://showmetweets";
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	private ImageButton mSignInTwitterButton;
	private Twitter mTwitter;
	private TwitterUtils mTwitterUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_with_twitter);
		this.getActionBar().hide();

		mSignInTwitterButton = (ImageButton) findViewById(R.id.btn_sign_in);
		mSignInTwitterButton.setOnClickListener(mSignButtonListener);
		
		mTwitterUtils = new TwitterUtils(this);
		mTwitter = TwitterUtils.getTwitterClient();
		
		AccessToken accessToken = mTwitterUtils.loadAccessToken();
		
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
					AccessToken accessToken = mTwitter.getOAuthAccessToken(mTwitterUtils.loadRequestToken(), verifier);
					mTwitterUtils.saveAccessToken(accessToken.getToken(), accessToken.getTokenSecret());
					goToMainScreen(accessToken);
				} catch (TwitterException e) {
					Log.e("SignInWithTwitter", "Error", e);
					mTwitterUtils.clear();
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
						mTwitterUtils.saveRequestToken(requestToken.getToken(), requestToken.getTokenSecret());
						
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						
						SignInWithTwitter.this.startActivity(intent);
					} catch (TwitterException e) {
						Log.e("SignIn", "Error requestToken", e);
						mTwitterUtils.clear();
					}
				}
			});

			thread.start();
		}
	};

}
