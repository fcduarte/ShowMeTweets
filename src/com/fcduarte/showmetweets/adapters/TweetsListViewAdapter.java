package com.fcduarte.showmetweets.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.activities.HomeActivity;
import com.fcduarte.showmetweets.activities.ProfileActivity;
import com.fcduarte.showmetweets.model.Tweet;
import com.squareup.picasso.Picasso;

public class TweetsListViewAdapter extends BaseAdapter {

	private List<Tweet> tweets;
	private Context mContext;

	public TweetsListViewAdapter(List<Tweet> tweets, Context context) {
		super();
		this.tweets = tweets;
		this.mContext = context;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.tweets.size();
	}

	@Override
	public Tweet getItem(int position) {
		return this.tweets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.detail_tweet, parent, false);
			holder = new ViewHolder();
			holder.ivUserAvatar = (ImageView) view
					.findViewById(R.id.user_avatar);
			holder.tvUsername = (TextView) view.findViewById(R.id.username);
			holder.tvName = (TextView) view.findViewById(R.id.name);
			holder.tvBody = (TextView) view.findViewById(R.id.body);
			holder.tvTimeStampt = (TextView) view.findViewById(R.id.timestamp);
			holder.tvRetweetCount = (TextView) view.findViewById(R.id.retweet_count);
			holder.tvFavoriteCount = (TextView) view.findViewById(R.id.favorite_count);
			holder.ivMediaBody = (ImageView) view.findViewById(R.id.media_body);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// Get the image URL for the current position.
		Tweet tweet = getItem(position);
		String avatarUrl = tweet.getUser().getAvatarUrl();
		String mediaBodyUrl = tweet.getBodyMediaURL();

		holder.tvUsername.setText(tweet.getUser().getUsernameFormatted());
		holder.tvName.setText(tweet.getUser().getName());
		holder.tvBody.setText(tweet.getBody());
		holder.tvTimeStampt.setText(tweet.getCreatedAtFormatted());
		holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
		holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));

		Picasso.with(mContext)
				.load(avatarUrl)
				.placeholder(R.drawable.user_placeholder)
				.resizeDimen(R.dimen.avatar_image_size,
						R.dimen.avatar_image_size).centerInside()
				.into(holder.ivUserAvatar);

		Picasso.with(mContext)
				.load(mediaBodyUrl)
				.resizeDimen(R.dimen.body_media_image_size,
						R.dimen.body_media_image_size).centerInside()
				.into(holder.ivMediaBody);
		
		holder.ivUserAvatar.setTag(Integer.valueOf(position));
		holder.ivUserAvatar.setOnClickListener(userAvatarOnClickListener);

		return view;
	}

	static class ViewHolder {
		ImageView ivUserAvatar;
		TextView tvUsername;
		TextView tvName;
		TextView tvBody;
		TextView tvTimeStampt;
		TextView tvRetweetCount;
		TextView tvFavoriteCount;
		ImageView ivMediaBody;
	}
	
	public void addTweet(Tweet tweet) {
		if (this.tweets != null) {
			this.tweets.add(tweet);
			Collections.sort(this.tweets);
		}
	}
	
	public void addTweets(List<Tweet> tweets) {
		if (this.tweets == null) {
			this.tweets = new ArrayList<>();
		}
		
		for (Tweet tweet : tweets) {
			if (!this.tweets.contains(tweet)) {
				this.tweets.add(tweet);
			}
		}
		
		Collections.sort(this.tweets);
	}
	
	private OnClickListener userAvatarOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Tweet tweet = getItem((int) view.getTag());
			
			Intent intent = new Intent(mContext, ProfileActivity.class);
			intent.putExtra(HomeActivity.LOGGED_USER_KEY, tweet.getUser());
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			mContext.startActivity(intent);
		}
	};


}
