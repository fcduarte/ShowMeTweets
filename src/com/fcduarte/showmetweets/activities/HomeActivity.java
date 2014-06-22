package com.fcduarte.showmetweets.activities;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.adapters.TweetsListViewAdapter;
import com.fcduarte.showmetweets.dao.TweetDAO;
import com.fcduarte.showmetweets.dao.UserDAO;
import com.fcduarte.showmetweets.listeners.EndlessScrollListener;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;
import com.fcduarte.showmetweets.utils.TwitterPreferencesUtils;

public class HomeActivity extends Activity {

	private static final int COMPOSE_NEW_TWEET = 10;
	public static final String LOGGED_USER_KEY = "logged-user";
	public static final String TWEET_KEY = "tweet";
	public static final String TWITTER_CLIENT_KEY = "twitter-client";
	
	private ListView mTweetsListView;
	private TextView mEmptyListTweets;
	private ProgressBar mLoadingProgressBar;
	private TweetsListViewAdapter mTweetsListViewAdapter;
	private User mLoggedUser;
	private Twitter mTwitter;
	private UserDAO mUserDAO;
	private TweetDAO mTweetDAO;
	private TwitterPreferencesUtils mTwitterPreferencesUtils;
	private SwipeRefreshLayout mRefreshContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mTweetsListView = (ListView) findViewById(R.id.tweets_list);
		mTweetsListView.setOnScrollListener(endlessScrollListener);
		mEmptyListTweets = (TextView) findViewById(R.id.empty_list_tweets);
		mLoadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
		mTweetsListViewAdapter = new TweetsListViewAdapter(new ArrayList<Tweet>(), HomeActivity.this);
		mTwitterPreferencesUtils = new TwitterPreferencesUtils(this);
		mRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mRefreshContainer.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		mRefreshContainer.setOnRefreshListener(refreshListener);

		mTweetsListView.setEmptyView(mEmptyListTweets);
		mTweetsListView.setAdapter(mTweetsListViewAdapter);

		mUserDAO = new UserDAO();
		mTweetDAO = new TweetDAO();
		
		mTwitter = (Twitter) getIntent().getSerializableExtra(TWITTER_CLIENT_KEY);
		new SynchronizeTwitterAsyncTask().execute(1);
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
			
			mTweetsListViewAdapter.addTweet(tweet);
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
	
	private class SynchronizeTwitterAsyncTask extends AsyncTask<Integer, Void, Void> {

		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mEmptyListTweets.setVisibility(View.GONE);
			mLoadingProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			List<Tweet> tweets = mTweetDAO.getAllTweets();
			mLoadingProgressBar.setVisibility(View.GONE);
			
			if (tweets.isEmpty()) {
				mEmptyListTweets.setVisibility(View.VISIBLE);
			} else {
				mTweetsListViewAdapter.setTweets(tweets);
				mTweetsListViewAdapter.notifyDataSetChanged();
			}
			
			if (mRefreshContainer.isRefreshing()) {
				mRefreshContainer.setRefreshing(false);
			}
		}

		@Override
		protected Void doInBackground(Integer... params) {
			twitter4j.User remoteUser = getRemoteUser();
			
			if (remoteUser == null) {
				mTwitterPreferencesUtils.clear();
				
				Intent intent = new Intent(HomeActivity.this, SignInWithTwitter.class);
				HomeActivity.this.startActivity(intent);
				return null;
			}
			
			mLoggedUser = mUserDAO.findByTwitterId(remoteUser.getId());
			
			if (mLoggedUser == null) {
				User user = new User();
				user.buildFromRemote(remoteUser);
				user.save();
			}
			
			if (mTwitterPreferencesUtils.userChanged(mLoggedUser)) {
				mTweetDAO.deleteAll();
				mTwitterPreferencesUtils.saveLoggedUser(mLoggedUser);
			}
			
			saveRemoteTweets(params[0]);
	        return null;
		}

		private void saveRemoteTweets(Integer currentPage) {
			try {
				List<twitter4j.Status> listStatus = mTwitter.getHomeTimeline(new Paging(currentPage));
				
				for (twitter4j.Status status : listStatus) {
					Tweet tweet = mTweetDAO.findByTwitterId(status.getId());
					
					if (tweet == null) {
						tweet = new Tweet();
						tweet.buildFromRemote(status);
						tweet.getUser().save();
						tweet.save();
					}
				}
			} catch (TwitterException e) {
			}
		}
		
		private twitter4j.User getRemoteUser() {
	        try {
				long id = mTwitter.getId();
				return mTwitter.showUser(id);
			} catch (IllegalStateException | TwitterException e) {
				Log.e("TwitterClient", "getRemoteUser()", e);
				return null;
			}
		}
		
	}
	
	private class SendTweetsToRemoteAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<Tweet> tweets = mTweetDAO.getAllTweetsNotSynchronized();
			
			for (Tweet tweet : tweets) {
				if (tweet.getUser().getTwitterId().equals(mLoggedUser.getTwitterId())) {
					try {
						mTwitter.updateStatus(tweet.getBody());
					} catch (TwitterException e) {
						Log.e("TwitterClient", "updateStatus()", e);
					}
					
				}
			}
			return null;
		}
		
	}
	
	private OnScrollListener endlessScrollListener = new EndlessScrollListener() {
		
		@Override
		public void onLoadMore(int page, int totalItemsCount) {
			Log.i("onLoadMore()", String.format("page: %s, total items: %s", page, totalItemsCount));
			new SynchronizeTwitterAsyncTask().execute(page);
		}
	};
	
	private OnRefreshListener refreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			new SynchronizeTwitterAsyncTask().execute(1);
		}
	};

}
