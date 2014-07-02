package com.fcduarte.showmetweets.dao;

import com.activeandroid.query.Select;
import com.fcduarte.showmetweets.model.User;

public class UserDAO {

	public User findByUsername(String username) {
		return new Select().from(User.class).where("username = ?", username).executeSingle(); 
	}
}
