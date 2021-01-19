package dev.gruffwizard.tools.twitter;

import dev.gruffwizard.tools.twitter.connection.UserCache;
import dev.gruffwizard.tools.twitter.connection.TwitterConnection;
import dev.gruffwizard.tools.twitter.entities.Tweet;
import dev.gruffwizard.tools.twitter.entities.User;

import java.util.*;

public class TwitterSession {

    private final TwitterConnection tc;
    private final UserCache userCache = new UserCache();
    private final Map<User,List<Tweet>> tweetCache=new HashMap<>();


    public TwitterSession(String bearer) {
        tc=new TwitterConnection(bearer);
    }

    public Set<User> getUsers(String ... screenName ) throws Exception {
        String[] missingScreenName=userCache.identifyMissing(screenName);
        if(missingScreenName.length>0) {
                Set<User> users=tc.new UserRequest().getUsers(missingScreenName);
                userCache.addAll(users);
        }
        return userCache.getUsers(screenName);


    }

    public Set<User> getUsers(Long ... ids ) throws Exception {

        Set<Long> missing=userCache.getMissing(ids);

        if(!missing.isEmpty()) {
           userCache.addAll(tc.new UserRequest().getUsers(missing));
        }
        return userCache.getUsers(ids);

    }

    public User getUser(String screenName) throws Exception {

        if(!userCache.hasUser(screenName)) {
              User user = tc.new UserRequest().getUser(screenName);
              userCache.add(user);
        }
        return userCache.getUser(screenName);
    }

    public List<Tweet> getTweets(User user) throws Exception {

        if (!tweetCache.containsKey(user)) {
            List<Tweet> results=tc.new TweetRequest().getTweets(user);
            tweetCache.put(user,results);
        }
        return tweetCache.get(user);


    }

    public Set<User> getRetweeters(List<Tweet> tweets)  {

    Set<Long> req=new HashSet<>();

    tweets.stream()
            .filter(t -> !t.hasRetweeters() && t.wasRetweeted())
            .forEach(t -> {
                Set<Long>  results;
                try {
                    results = tc.new TweetRequest().getRetweeterIDs(t.getID());
                    t.setRetweeters(results);
                    req.addAll(results);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

    return userCache.getUsers(req);

    }

}
