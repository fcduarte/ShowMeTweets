package com.fcduarte.showmetweets.adapters;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.model.Tweet;
import com.squareup.picasso.Picasso;

public class TweetsListViewAdapter extends BaseAdapter {

	private List<Tweet> tweets;
	private Context context;

	public TweetsListViewAdapter(List<Tweet> tweets, Context context) {
		super();
		this.tweets = tweets;
		this.context = context;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
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
			view = LayoutInflater.from(context).inflate(
					R.layout.list_detail_tweet, parent, false);
			holder = new ViewHolder();
			holder.ivUserAvatar = (ImageView) view
					.findViewById(R.id.user_avatar);
			holder.tvUsername = (TextView) view.findViewById(R.id.username);
			holder.tvName = (TextView) view.findViewById(R.id.name);
			holder.tvBody = (TextView) view.findViewById(R.id.body);
			holder.tvTimeStampt = (TextView) view.findViewById(R.id.timestamp);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// Get the image URL for the current position.
		Tweet tweet = getItem(position);
		String url = tweet.getUser().getAvatarUrl();
		holder.tvUsername.setText(tweet.getUser().getUsernameFormatted());
		holder.tvName.setText(tweet.getUser().getName());
		holder.tvBody.setText(tweet.getBody());
		holder.tvTimeStampt.setText(tweet.getCreatedAtFormatted());

		// Trigger the download of the URL asynchronously into the image view.
		Picasso picasso = Picasso.with(context);
		picasso.setIndicatorsEnabled(true);
		picasso.setLoggingEnabled(true);
		
		picasso.load(url)
				.placeholder(R.drawable.user_placeholder)
				.resizeDimen(R.dimen.avatar_image_size,
						R.dimen.avatar_image_size).centerInside()
				.into(holder.ivUserAvatar);

		return view;
	}

	static class ViewHolder {
		ImageView ivUserAvatar;
		TextView tvUsername;
		TextView tvName;
		TextView tvBody;
		TextView tvTimeStampt;
	}
	
	public void addTweet(Tweet tweet) {
		if (this.tweets != null) {
			this.tweets.add(tweet);
			Collections.sort(this.tweets);
			notifyDataSetChanged();
		}
	}

}
