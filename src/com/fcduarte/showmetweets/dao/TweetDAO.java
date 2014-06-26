package com.fcduarte.showmetweets.dao;

import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fcduarte.showmetweets.model.Tweet;

public class TweetDAO {

	public List<Tweet> getAllTweets() {
		return new Select().from(Tweet.class).orderBy("created_at DESC").execute();
	}

	public Tweet findByTwitterId(long id) {
		List<Tweet> tweets = new Select().from(Tweet.class).where("tweet_id = ?", id).execute(); 
		
		if (tweets.isEmpty()) {
			return null;
		}
		
		return tweets.get(0);
	}

	public List<Tweet> getAllTweetsNotSynchronized() {
		return new Select().from(Tweet.class).where("tweet_id is null").execute();
	}

	public void deleteAll() {
		new Delete().from(Tweet.class).execute();
	}
}
