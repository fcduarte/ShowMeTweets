package com.fcduarte.showmetweets.activities;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.dao.TweetDAO;
import com.fcduarte.showmetweets.dao.UserDAO;
import com.fcduarte.showmetweets.model.User;
import com.fcduarte.showmetweets.utils.ConnectivityUtils;
import com.fcduarte.showmetweets.utils.TwitterUtils;

public class SignInWithTwitter extends Activity {

	protected static final String TWITTER_CALLBACK_URL = "oauth://showmetweets";
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	private ImageButton mSignInTwitterButton;
	private Twitter mTwitter;
	private TwitterUtils mTwitterUtils;
	private UserDAO mUserDAO;
	private TweetDAO mTweetDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_with_twitter);
		this.getActionBar().hide();

		mSignInTwitterButton = (ImageButton) findViewById(R.id.btn_sign_in);
		mSignInTwitterButton.setOnClickListener(mSignButtonListener);

		mUserDAO = new UserDAO();
		mTweetDAO = new TweetDAO();
		
		mTwitterUtils = new TwitterUtils(this);
		mTwitter = mTwitterUtils.getTwitterClient();
		
		if (mTwitterUtils.loadAccessToken() != null) {
			new SynchronizeUserAsyncTask().execute();
			return;
		}
		
		processTwitterCallback(getIntent().getData());
	}

	private void goToMainScreen() {
		Intent intent = new Intent(this, HomeActivity.class);
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
					new SynchronizeUserAsyncTask().execute();
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
	
	private class SynchronizeUserAsyncTask extends AsyncTask<Integer, Void, Boolean> {
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
	
			if (result) {
				goToMainScreen();
			}
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			twitter4j.User remoteUser = getRemoteUser();
			User localUser = null;
			
			if (remoteUser == null) {
				localUser = mUserDAO.findByUsername(mTwitterUtils.loadLoggedUser());
				
				if (localUser == null) {
					return false;
				}
			} else {
				localUser = mUserDAO.findByUsername(remoteUser.getScreenName());
			}
			
			if (remoteUser != null && localUser == null) {
				User user = new User();
				user.buildFromRemote(remoteUser);
				user.save();
				
				localUser = user;

			}
			
			if (mTwitterUtils.userChanged(localUser)) {
				mTweetDAO.deleteAll();
				mTwitterUtils.saveLoggedUser(localUser);
			}
			
			return true;
		}

		private twitter4j.User getRemoteUser() {
			if (!ConnectivityUtils.isConnected(SignInWithTwitter.this)) {
				return null;
			}
			
	        try {
				long id = mTwitter.getId();
				return mTwitter.showUser(id);
			} catch (IllegalStateException e) {
				return null;
			} catch (TwitterException e) {
				Log.e("TwitterClient", "getRemoteUser()", e);
				
				if (!e.isCausedByNetworkIssue()) {
					mTwitterUtils.clear();
				}
				
				return null;
			}
		}
		
	}


}
