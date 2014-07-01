package com.fcduarte.showmetweets.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fcduarte.showmetweets.R;
import com.fcduarte.showmetweets.model.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class ProfileActivity extends Activity {

	private User mUser;
	private RelativeLayout rlProfileDetails;
	private ImageView ivUserAvatar;
	private TextView tvProfileUsername;
	private TextView tvProfileName;
	private TextView tvProfileDescription;
	private TextView tvProfileTweetsCount;
	private TextView tvProfileFollowersCount;
	private TextView tvProfileFollowingCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		rlProfileDetails = (RelativeLayout) findViewById(R.id.profile_details);
		ivUserAvatar = (ImageView) findViewById(R.id.profile_avatar_url);
		tvProfileUsername = (TextView) findViewById(R.id.profile_username);
		tvProfileName = (TextView) findViewById(R.id.profile_name);
		tvProfileDescription = (TextView) findViewById(R.id.profile_description);
		tvProfileTweetsCount = (TextView) findViewById(R.id.profile_tweets_count);
		tvProfileFollowersCount = (TextView) findViewById(R.id.profile_followers_count);
		tvProfileFollowingCount = (TextView) findViewById(R.id.profile_following_count);

		mUser = (User) getIntent().getSerializableExtra(
				HomeActivity.LOGGED_USER_KEY);

		Picasso.with(this)
				.load(mUser.getProfileBackgroundUrl())
				.resizeDimen(R.dimen.body_media_image_size,
						R.dimen.body_media_image_size).centerInside()
				.into(backgroundImageTarget);

		Picasso.with(this)
				.load(mUser.getAvatarUrl())
				.resizeDimen(R.dimen.avatar_image_size,
						R.dimen.avatar_image_size).into(ivUserAvatar);
		
		tvProfileUsername.setText(mUser.getUsernameFormatted());
		tvProfileName.setText(mUser.getName());
		tvProfileDescription.setText(mUser.getDescription());
		tvProfileTweetsCount.setText(getString(R.string.tweets_count, mUser.getTweetsCount()));
		tvProfileFollowersCount.setText(getString(R.string.followers_count, mUser.getFollowersCount()));
		tvProfileFollowingCount.setText(getString(R.string.following_count, mUser.getFollowingCount()));
	}
	
	private Target backgroundImageTarget = new Target() {

		@Override
		public void onPrepareLoad(Drawable arg0) {
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom loadedFrom) {
			rlProfileDetails.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		
		@Override
		public void onBitmapFailed(Drawable arg0) {
		}
	};
	
}
