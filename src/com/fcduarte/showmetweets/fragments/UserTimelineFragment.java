package com.fcduarte.showmetweets.fragments;

import com.fcduarte.showmetweets.model.User;

import android.os.Bundle;

public class UserTimelineFragment extends ListTweetsFragment {

	private static final String USER_KEY = "user";

	public static UserTimelineFragment newInstance(User user) {
		UserTimelineFragment fragment = new UserTimelineFragment();

		Bundle args = new Bundle();
        args.putSerializable(USER_KEY, user);
        fragment.setArguments(args);
        
        return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.mTimelineType = ListTweetsFragment.TimelineType.USER;
		super.mUser = (User) getArguments().getSerializable(USER_KEY);
	}

}
