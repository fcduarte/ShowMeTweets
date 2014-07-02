package com.fcduarte.showmetweets.fragments;

import android.os.Bundle;

public class HomeTimelineFragment extends ListTweetsFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.mTimelineType = ListTweetsFragment.TimelineType.HOME;
	}
	
}
