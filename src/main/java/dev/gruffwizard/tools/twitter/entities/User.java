package dev.gruffwizard.tools.twitter.entities;

import dev.gruffwizard.tools.twitter.entities.Tweet;
import org.json.JSONObject;

import java.util.List;

public class User extends Entity{
    private String screenName;
    private long id;
    private String created;
    private String url;
    private String description;
    private String location;
    private String name;
    private int followers;
    private int friends;
    private int following;
    private int listed;
    private int favorites;
    private int tweet_count;
    private List<Tweet> tweets;



    public User(JSONObject obj) {
        System.out.println(obj);

        //created_at,description,id,location,name,public_metrics,username

        screenName=get(obj,"screen_name",screenName);
        screenName=get(obj,"username",screenName);
        id=obj.getLong("id");
        created=get(obj,"created",created);
        created=get(obj,"created_at",created);
        description=get(obj,"description",description);
        location=get(obj,"location",location);
        name=get(obj,"name",name);
        if (obj.has("public_metrics")) {
            JSONObject pm=obj.getJSONObject("public_metrics");
            followers = get(obj,"followers_count",followers);
            following = get(obj,"following_count",following);
            tweet_count  = get(obj,"tweet_count",tweet_count);

        } else {
            followers = get(obj,"followers_count",followers);
            friends = get(obj,"friends_count",friends);
            listed = get(obj,"listed_count",listed);
            favorites = get(obj,"favourites_count",favorites);
            tweet_count= get(obj,"statuses_count",tweet_count);
        }
        //created_at,description,location,name,public_metrics,url,username
       }


    public String getName() {
        return screenName;
    }

    public long getID() {
        return id;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }
}
