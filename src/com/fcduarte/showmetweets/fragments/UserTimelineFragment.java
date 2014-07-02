package com.fcduarte.showmetweets.fragments;

import com.fcduarte.showmetweets.model.User;

import android.os.Bundle;

public class UserTimelineFragment extends ListTweetsFragment {

	public static UserTimelineFragment newInstance(User user) {
		// FIXME
		UserTimelineFragment fragment = new UserTimelineFragment();
		fragment.mUser = user;
		return fragment;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.mTimelineType = ListTweetsFragment.TimelineType.USER;
	}

}
