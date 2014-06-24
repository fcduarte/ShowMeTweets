ShowMeTweets
============

A simple Twitter client app for Android which retrives and posts tweets of a user

Time spent: 20 hours spent in total

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

### Notes:

The project uses open-source libraries such as:

- Twitter4j: handles the autenthication process and requests to Twitter API
- Picaso: image lazy loading and cache
- ActiveAndroid: SQLite database handling, ORM mapping and easy queries 

### Walkthrough of all user stories:

![ShowMeTweets]()
