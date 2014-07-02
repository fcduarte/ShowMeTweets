package com.fcduarte.showmetweets.fragments;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.fcduarte.showmetweets.utils.ConnectivityUtils;
import com.fcduarte.showmetweets.utils.TwitterUtils;

public class ListTweetsFragment extends Fragment {
	
	public static enum TimelineType {
		HOME,
		USER,
		MENTIONS
	}
	private ListView mTweetsListView;
	private TextView mEmptyListTweets;
	private ProgressBar mLoadingProgressBar;
	private TweetsListViewAdapter mTweetsListViewAdapter;
	protected User mUser;
	private Twitter mTwitter;
	private TweetDAO mTweetDAO;
	protected TimelineType mTimelineType;
	private TwitterUtils mTwitterUtils;
	private SwipeRefreshLayout mRefreshContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTwitterUtils = new TwitterUtils(getActivity());
		mTweetDAO = new TweetDAO();
		mUser = new UserDAO().findByUsername(mTwitterUtils.loadLoggedUser());

		mTwitter = mTwitterUtils.getTwitterClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_tweets, container, false);
		
		mTweetsListView = (ListView) view.findViewById(R.id.tweets_list);
		mTweetsListView.setOnScrollListener(endlessScrollListener);
		mEmptyListTweets = (TextView) view.findViewById(R.id.empty_list_tweets);

		mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
		mTweetsListViewAdapter = new TweetsListViewAdapter(new ArrayList<Tweet>(), view.getContext(), mTwitter);

		mTweetsListView.setAdapter(mTweetsListViewAdapter);
		
		mRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mRefreshContainer.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		mRefreshContainer.setOnRefreshListener(refreshListener);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.synchronizeTweets(1);
	}
	
	public void synchronizeTweets(int page) {
		new SynchronizeTweetsAsyncTask().execute(page);
	}

	public void addNewTweet(Tweet tweet) {
		mTweetsListViewAdapter.addTweet(tweet);
		mTweetsListViewAdapter.notifyDataSetChanged();
		mTweetsListView.setSelection(0);
	}
	
	private OnScrollListener endlessScrollListener = new EndlessScrollListener() {
		
		@Override
		public void onLoadMore(int page, int totalItemsCount) {
			Log.i("onLoadMore()", String.format("page: %s, total items: %s", page, totalItemsCount));
			new SynchronizeTweetsAsyncTask().execute(page);
		}
	};
	
	private OnRefreshListener refreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			new SynchronizeTweetsAsyncTask().execute(1);
		}
	};
	
	private class SynchronizeTweetsAsyncTask extends AsyncTask<Integer, Void, List<Tweet>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if (!mRefreshContainer.isRefreshing()) {
				mLoadingProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPostExecute(List<Tweet> result) {
			super.onPostExecute(result);
			
			if (mRefreshContainer.isRefreshing()) {
				mRefreshContainer.setRefreshing(false);
			} else {
				mLoadingProgressBar.setVisibility(View.GONE);
			}
			
			if (result.isEmpty() && mUser != null) {
				mTweetsListView.setEmptyView(mEmptyListTweets);
			} else {
				mTweetsListView.setEmptyView(null);
				mTweetsListViewAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected List<Tweet> doInBackground(Integer... params) {
			List<Tweet> tweets = new ArrayList<>();
			int currentPage = params[0];
			
			if (mUser == null) {
				return tweets;
			}
			
			if (ConnectivityUtils.isConnected(getActivity())) {
				tweets = syncRemoteTweets(mUser, currentPage, mTimelineType);
				mTweetsListViewAdapter.addTweets(tweets);
			} else {
				tweets = mTweetDAO.findByUser(mUser);
				mTweetsListViewAdapter.setTweets(tweets);
			}
			
			return tweets;
		}

		private List<Tweet> syncRemoteTweets(User user, int currentPage, TimelineType timelineType) {
			List<Tweet> tweets = new ArrayList<>();
			
			try {
				List<twitter4j.Status> listStatus = new ArrayList<>();
				
				switch (timelineType) {
				case HOME:
					listStatus = mTwitter.getHomeTimeline(new Paging(currentPage));
					break;

				case USER:
					listStatus = mTwitter.getUserTimeline(user.getTwitterId(), new Paging(currentPage));
					break;

				case MENTIONS:
					listStatus = mTwitter.getMentionsTimeline(new Paging(currentPage));
					break;

				default:
					break;
				}
				
				for (twitter4j.Status status : listStatus) {
					Tweet tweet = mTweetDAO.findByTwitterId(status.getId());
					
					if (tweet == null) {
						tweet = new Tweet();
						tweet.buildFromRemote(status);
						tweet.getUser().save();
						tweet.save();
					}
					
					tweets.add(tweet);
				}
			} catch (TwitterException e) {
			}
			
			return tweets;
		}
		
	}

}
