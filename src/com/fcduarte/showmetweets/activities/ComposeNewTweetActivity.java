package com.fcduarte.showmetweets.activities;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;
import com.squareup.picasso.Picasso;

public class ComposeNewTweetActivity extends Activity {
	
	private ImageView mAvatarImageView;
	private TextView mNameTextView;
	private TextView mUsernameTextView;
	private TextView mTweetBody;
	private User mLoggedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_new_tweet);
		
		mAvatarImageView = (ImageView) findViewById(R.id.user_avatar);
		mNameTextView = (TextView) findViewById(R.id.name);
		mUsernameTextView = (TextView) findViewById(R.id.username);
		mTweetBody = (TextView) findViewById(R.id.tweet_body);
		
		mLoggedUser = (User) getIntent().getSerializableExtra(HomeActivity.LOGGED_USER_KEY);
		mNameTextView.setText(mLoggedUser.getName());
		mUsernameTextView.setText(mLoggedUser.getUsernameFormatted());
		
		Picasso picasso = Picasso.with(this);
		picasso.setIndicatorsEnabled(true);
		picasso.setLoggingEnabled(true);
		
		picasso.load(mLoggedUser.getAvatarUrl())
				.placeholder(R.drawable.user_placeholder)
				.resizeDimen(R.dimen.avatar_image_size,
						R.dimen.avatar_image_size).centerInside()
				.into(mAvatarImageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_compose_new_tweet, menu);
		return true;
	}
	
	public void onSendTweetClicked(MenuItem menuItem) {
		String tweetBody = mTweetBody.getText().toString();
		
		if (tweetBody.isEmpty()) {
			Toast.makeText(this, R.string.empty_tweet, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Tweet tweet = new Tweet(tweetBody, new Date(), null, mLoggedUser);
		
		Intent intent = new Intent();
		intent.putExtra(HomeActivity.TWEET_KEY, tweet);
		
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
}
