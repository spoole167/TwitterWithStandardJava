package dev.gruffwizard.tools.twitter.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tweet extends Entity {

    private final JSONObject data;

    private Set<String> hashtags=new HashSet<>();
    private long id;
    private long author_id;
    private long conversation_id;
    private long in_reply_to_user_id;

    private String created;
    private String source;
    private String text;
    private String geo;


    private int retweet_count;
    private int like_count;
    private int quote_count;
    private int replies_count;


    private Set<Long> retweeters;

    public Tweet(JSONObject t) {
        data=t;
        created=get(t,"created_at",created);
        id=get(t,"id",id);
        source=get(t,"source",source);
        text=get(t,"text",text);
        author_id=get(t,"author_id",author_id);
        conversation_id=get(t,"author_id",conversation_id);
        in_reply_to_user_id=get(t,"in_reply_to_user_id",in_reply_to_user_id);
        geo=get(t,"geo",geo);

        if (t.has("public_metrics")) {
            JSONObject pm = t.getJSONObject("public_metrics");
            retweet_count=get(pm,"retweet_count",retweet_count);
            like_count=get(pm,"like_count",like_count);
            quote_count=get(pm,"quote_count",quote_count);
           replies_count=get(pm,"replies_count",replies_count);
        }

        if(t.has("entities")) {
            JSONObject e = t.getJSONObject("entities");
            if(e.has("hashtags")) {
                JSONArray h = e.getJSONArray("hashtags");
                h.forEach(a -> { JSONObject o= (JSONObject) a; hashtags.add(o.getString("tag"));});
            }
        }
    }



    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append("id="+id);
       sb.append(",created="+created);
       sb.append(",rt="+retweet_count);
       sb.append(",qt="+quote_count);
       sb.append(",lk="+like_count);
       sb.append(",rp="+replies_count);
       sb.append(",tags="+hashtags);
       sb.append(",msg="+text);

        return sb.toString();
    }

    public long getID() {
        return id;
    }

    public boolean hasRetweeters() {
        return retweeters!=null;
    }

    public boolean wasRetweeted() {
        return retweet_count>0 || quote_count>0;
    }

    public void setRetweeters(Set<Long> users) {
        retweeters=users;
    }
}
