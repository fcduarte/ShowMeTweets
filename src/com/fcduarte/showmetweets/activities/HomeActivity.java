package com.fcduarte.showmetweets.activities;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.dao.TweetDAO;
import com.fcduarte.showmetweets.dao.UserDAO;
import com.fcduarte.showmetweets.fragments.ListTweetsFragment;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;
import com.fcduarte.showmetweets.utils.TwitterUtils;

public class HomeActivity extends FragmentActivity {

	private static final int COMPOSE_NEW_TWEET = 10;
	public static final String LOGGED_USER_KEY = "logged-user";
	public static final String TWEET_KEY = "tweet";
	public static final String TWITTER_CLIENT_KEY = "twitter-client";
	
	private User mLoggedUser;
	private Twitter mTwitter;
	private UserDAO mUserDAO;
	private TweetDAO mTweetDAO;
	private TwitterUtils mTwitterUtils;
	private SwipeRefreshLayout mRefreshContainer;
	ListTweetsFragment mListTweetsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mTwitterUtils = new TwitterUtils(this);
		mRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mRefreshContainer.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		mRefreshContainer.setOnRefreshListener(refreshListener);

		mUserDAO = new UserDAO();
		mTweetDAO = new TweetDAO();
		
		mTwitter = (Twitter) getIntent().getSerializableExtra(TWITTER_CLIENT_KEY);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		mListTweetsFragment = ListTweetsFragment.newInstance(null, mTwitter, true);
		ft.replace(R.id.tweets_list_fragment, mListTweetsFragment);
		ft.commit();
		
		new SynchronizeTwitterAsyncTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == COMPOSE_NEW_TWEET) {
			Tweet tweet = (Tweet) data.getExtras().getSerializable(TWEET_KEY);
			tweet.getUser().save();
			tweet.save();
			
			mListTweetsFragment.addNewTweet(tweet);

			new SendTweetsToRemoteAsyncTask().execute();
		}
	}
	
	@Override
	public void onBackPressed() {
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    startActivity(intent);
	}

	public void onComposeNewTweetClicked(MenuItem menuItem) {
		Intent intent = new Intent(this, ComposeNewTweetActivity.class);
		intent.putExtra(LOGGED_USER_KEY, mLoggedUser);
		startActivityForResult(intent, COMPOSE_NEW_TWEET);
	}
	
	public void onShowProfileClicked(MenuItem menuItem) {
		callProfileActivity(this);
	}

	private void callProfileActivity(Context context) {
		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra(LOGGED_USER_KEY, mLoggedUser);
		intent.putExtra(TWITTER_CLIENT_KEY, mTwitter);
		startActivity(intent);
	}
	
	private User findLocalSavedUser() {
		String username = mTwitterUtils.loadLoggedUser();
		
		if (username == null) {
			return null;
		}
		
		return mUserDAO.findByUsername(username);
	}
	
	private class SynchronizeTwitterAsyncTask extends AsyncTask<Integer, Void, Void> {
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mListTweetsFragment.setUser(mLoggedUser);
			mListTweetsFragment.synchronizeTweets(1);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			twitter4j.User remoteUser = getRemoteUser();
			
			if (remoteUser == null) {
				mLoggedUser = findLocalSavedUser();
			} else {
				mLoggedUser = mUserDAO.findByUsername(remoteUser.getScreenName());
			}
			
			
			if (remoteUser != null && mLoggedUser == null) {
				User user = new User();
				user.buildFromRemote(remoteUser);
				user.save();
				
				mLoggedUser = user;

			}
			
			if (mTwitterUtils.userChanged(mLoggedUser)) {
				mTweetDAO.deleteAll();
				mTwitterUtils.saveLoggedUser(mLoggedUser);
			}
			
			return null;
		}

		private twitter4j.User getRemoteUser() {
	        try {
				long id = mTwitter.getId();
				return mTwitter.showUser(id);
			} catch (IllegalStateException e) {
				return null;
			} catch (TwitterException e) {
				Log.e("TwitterClient", "getRemoteUser()", e);
				
				if (!e.isCausedByNetworkIssue()) {
					mTwitterUtils.clear();
					
					Intent intent = new Intent(HomeActivity.this, SignInWithTwitter.class);
					HomeActivity.this.startActivity(intent);
				}
				
				return null;
			}
		}
		
	}
	
	private class SendTweetsToRemoteAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<Tweet> tweets = mTweetDAO.getAllNotSynchronized();
			
			for (Tweet tweet : tweets) {
				if (tweet.getUser().getTwitterId().equals(mLoggedUser.getTwitterId())) {
					try {
						twitter4j.Status status = mTwitter.updateStatus(tweet.getBody());
						tweet.setTweetId(status.getId());
						tweet.save();
					} catch (TwitterException e) {
						Log.e("TwitterClient", "updateStatus()", e);
					}
					
				}
			}
			return null;
		}
		
	}
	
	private OnRefreshListener refreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			mListTweetsFragment.synchronizeTweets(1);
		}
	};

}
