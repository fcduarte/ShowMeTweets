package com.fcduarte.showmetweets.dao;

import java.util.List;

import com.activeandroid.query.Select;
import com.fcduarte.showmetweets.model.User;

public class UserDAO {

	public User findByTwitterId(Long twitterId) {
		List<User> users = new Select().from(User.class).where("twitter_id = ?", twitterId).execute(); 
		
		if (users.isEmpty()) {
			return null;
		}
		
		return users.get(0);
	}
}
