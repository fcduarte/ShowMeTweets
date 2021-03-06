ShowMeTweets
============

A simple Twitter client app for Android which retrives and posts tweets of a user and also shows any user profile.

Time spent: 30 hours spent in total

### Notes:

The project uses open-source libraries such as:

- Twitter4j: handles the autenthication process and requests to Twitter API
- Picaso: image lazy loading and cache
- ActiveAndroid: SQLite database handling, ORM mapping and easy queries 

## Iteration 1

### Completed user stories:

* [x] Required: User can sign in to Twitter using OAuth login
* [x] Required: User can view the tweets from their home timeline
* [x] Required: User should be able to see the username, name, body and timestamp for each tweet
* [x] Required: User should be displayed the relative timestamp for a tweet "8m", "7h"
* [x] Required: User can view more tweets as they scroll with infinite pagination
* [x] Optional: Links in tweets are clickable and will launch the web browser (see autolink)
* [x] Required: User can compose a new tweet
* [x] Required: User can click a “Compose” icon in the Action Bar on the top right
* [x] Required: User can then enter a new tweet and post this to twitter
* [x] Required: User is taken back to home timeline with new tweet visible in timeline
* [x] Optional: User can see a counter with total number of characters left for tweet
* [x] Required: Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
* [x] Optional: Tweets are persisted into sqlite and can be displayed from the local DB


### Walkthrough of all user stories:

![ShowMeTweets](https://cloud.githubusercontent.com/assets/1507064/3376255/1a6b42c4-fbd3-11e3-9b62-7c1b5aedb161.gif)

![Tweet images](https://cloud.githubusercontent.com/assets/1507064/3394504/537a75e8-fce2-11e3-9fb6-85f79a589c68.gif)

## Iteration 2

### Completed user stories:

* [x] Required: User can navigate to view their own profile
* [x] Required: User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] Required: User can click on the profile image in any tweet to see another user's profile.
* [x] Required: User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
* [x] Required: Profile view should include that user's timeline
* [x] Required: User can switch between Timeline and Mention views using tabs.
* [x] Required: User can view their home timeline tweets.
* [x] Required: User can view the recent mentions of their username.
* [x] Required: User can scroll to bottom of either of these lists and new tweets will load ("infinite scroll")
* [x] Advanced: Robust error handling, check if internet is available, handle error cases, network failures
* [x] Advanced: When a network request is sent, user sees an indeterminate progress indicator
* [x] Bonus: User can see embedded image media within the tweet detail view
 
### Walkthrough of all user stories:

![ShowMeTweets](https://cloud.githubusercontent.com/assets/1507064/3451470/d6464d58-0197-11e4-831e-9bcaad50945b.gif)
