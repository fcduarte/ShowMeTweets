package com.fcduarte.showmetweets.activities;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.dao.TweetDAO;
import com.fcduarte.showmetweets.dao.UserDAO;
import com.fcduarte.showmetweets.fragments.HomeTimelineFragment;
import com.fcduarte.showmetweets.fragments.MentionsTimelineFragment;
import com.fcduarte.showmetweets.listeners.FragmentTabListener;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;
import com.fcduarte.showmetweets.utils.TwitterUtils;

public class HomeActivity extends FragmentActivity {

	private static final String MENTIONS_TIMELINE_FRAGMENT = "MentionsTimelineFragment";
	private static final String HOME_TIMELINE_FRAGMENT = "HomeTimelineFragment";
	private static final int COMPOSE_NEW_TWEET = 10;
	public static final String LOGGED_USER_KEY = "logged-user";
	public static final String TWEET_KEY = "tweet";
	
	private User mLoggedUser;
	private Twitter mTwitter;
	private TweetDAO mTweetDAO;
	private TwitterUtils mTwitterUtils;
	private HomeTimelineFragment mHomeTimelineFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mTwitterUtils = new TwitterUtils(this);
		mTwitter = mTwitterUtils.getTwitterClient();
		
		mTweetDAO = new TweetDAO();
		mLoggedUser = new UserDAO().findByUsername(mTwitterUtils.loadLoggedUser());
		
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab homeTimelineTab = actionBar
				.newTab()
				.setText("HOME")
				.setIcon(R.drawable.ic_home)
				.setTag(HOME_TIMELINE_FRAGMENT)
				.setTabListener(
						new FragmentTabListener<HomeTimelineFragment>(
								R.id.tweets_list_fragment, this, HOME_TIMELINE_FRAGMENT,
								HomeTimelineFragment.class));
		
		actionBar.addTab(homeTimelineTab);
		actionBar.selectTab(homeTimelineTab);
		
		Tab mentionsTab = actionBar
				.newTab()
				.setText("MENTIONS")
				.setIcon(R.drawable.ic_mentions)
				.setTag(MENTIONS_TIMELINE_FRAGMENT)
				.setTabListener(
						new FragmentTabListener<MentionsTimelineFragment>(
								R.id.tweets_list_fragment, this, MENTIONS_TIMELINE_FRAGMENT,
								MentionsTimelineFragment.class));

		actionBar.addTab(mentionsTab);
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
			
			mHomeTimelineFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag(HOME_TIMELINE_FRAGMENT);
			mHomeTimelineFragment.addNewTweet(tweet);

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
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		startActivity(intent);
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
	
}
