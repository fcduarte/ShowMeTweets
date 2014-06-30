package com.fcduarte.showmetweets.dao;

import java.util.List;

import com.activeandroid.query.Select;
import com.fcduarte.showmetweets.model.User;

public class UserDAO {

	public User findByUsername(String username) {
		List<User> users = new Select().from(User.class).where("username = ?", username).execute(); 
		
		if (users.isEmpty()) {
			return null;
		}
		
		return users.get(0);
	}
}
