package com.fcduarte.showmetweets.dao;

import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fcduarte.showmetweets.model.Tweet;
import com.fcduarte.showmetweets.model.User;

public class TweetDAO {

	public List<Tweet> findByUser(User user) {
		return new Select().from(Tweet.class).where("user = ?", user.getId()).orderBy("created_at DESC").execute();
	}

	public Tweet findByTwitterId(long id) {
		Tweet tweet = new Select().from(Tweet.class).where("tweet_id = ?", id).executeSingle(); 
		return tweet;
	}

	public List<Tweet> getAllNotSynchronized() {
		return new Select().from(Tweet.class).where("tweet_id is null").execute();
	}

	public void deleteAll() {
		new Delete().from(Tweet.class).execute();
	}
}
