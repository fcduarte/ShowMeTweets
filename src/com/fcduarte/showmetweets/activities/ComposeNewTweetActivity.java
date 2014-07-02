package com.fcduarte.showmetweets.activities;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;
import com.squareup.picasso.Picasso;

public class ComposeNewTweetActivity extends Activity {
	
	private static final int MAX_CHARACTERS = 140;
	
	private ImageView mAvatarImageView;
	private TextView mNameTextView;
	private TextView mUsernameTextView;
	private EditText mTweetBody;
	private User mLoggedUser;
	private MenuItem mSendTweetMenuItem;
	private MenuItem mTotalCharactersLeftMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_new_tweet);
		
		mAvatarImageView = (ImageView) findViewById(R.id.user_avatar);
		mNameTextView = (TextView) findViewById(R.id.name);
		mUsernameTextView = (TextView) findViewById(R.id.username);
		mTweetBody = (EditText) findViewById(R.id.tweet_body);
		mTweetBody.addTextChangedListener(watcher);
		
		mLoggedUser = (User) getIntent().getSerializableExtra(HomeActivity.LOGGED_USER_KEY);
		mNameTextView.setText(mLoggedUser.getName());
		mUsernameTextView.setText(mLoggedUser.getUsernameFormatted());
		
		Picasso.with(this)
				.load(mLoggedUser.getAvatarUrl())
				.placeholder(R.drawable.user_placeholder)
				.resizeDimen(R.dimen.avatar_image_size,
						R.dimen.avatar_image_size).centerInside()
				.into(mAvatarImageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_compose_new_tweet, menu);
		mSendTweetMenuItem = menu.findItem(R.id.send_tweet);
		mTotalCharactersLeftMenuItem = menu.findItem(R.id.character_left);

		TextView tv  = new TextView(this);
		tv.setText(R.string.character_left);
		tv.setTextColor(Color.WHITE);
		tv.setPadding(0, 0, 20, 0);
		mTotalCharactersLeftMenuItem.setActionView(tv);

		return true;
	}
	
	public void onSendTweetClicked(MenuItem menuItem) {
		String tweetBody = mTweetBody.getText().toString();
		
		if (tweetBody.isEmpty()) {
			Toast.makeText(this, R.string.empty_tweet, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Tweet tweet = new Tweet(tweetBody, new Date(), null, mLoggedUser, 0, 0, null);
		
		Intent intent = new Intent();
		intent.putExtra(HomeActivity.TWEET_KEY, tweet);
		
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// no-op
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// no-op			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			int totalCharacters = s.length();
			processCharactersLeft(totalCharacters);
		}
	};
	
	private void processCharactersLeft(int totalCharacters) {
		int charactersLeft = MAX_CHARACTERS - totalCharacters;
		boolean enabled = charactersLeft >= 0 && charactersLeft < MAX_CHARACTERS;
		mSendTweetMenuItem.setEnabled(enabled);
		
		((TextView) mTotalCharactersLeftMenuItem.getActionView()).setText(String.valueOf(charactersLeft));
		((TextView) mTotalCharactersLeftMenuItem.getActionView()).setTextColor(enabled ? Color.WHITE : Color.RED);
	}

	
}
