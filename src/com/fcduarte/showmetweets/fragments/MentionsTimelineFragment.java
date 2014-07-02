package com.fcduarte.showmetweets.fragments;

import android.os.Bundle;

public class MentionsTimelineFragment extends ListTweetsFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.mTimelineType = ListTweetsFragment.TimelineType.MENTIONS;
	}

}
