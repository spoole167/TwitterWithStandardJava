package dev.gruffwizard.tools.twitter.connection;

import dev.gruffwizard.tools.twitter.entities.Tweet;
import dev.gruffwizard.tools.twitter.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TwitterConnection {

    private static final long DELAY_TWEETS = Math.round(15f*60f/1500f*1.1f)* 1000L;
    private static final long DELAY_RETWEETS  = Math.round(15f*60f/300f*1.1f)* 1000L;
    private static final long DELAY_GETUSER   = Math.round(15f*60f/300f*1.1f)* 1000L;

    private HttpClient client = HttpClient.newHttpClient();
    private String bearer;


    public TwitterConnection(String bearer) {
        this.bearer = bearer;
    }


    private JSONObject fetch(long delaymsecs, String path) throws Exception {

        System.out.println("delay " + delaymsecs + " for " + path);
        Thread.sleep(delaymsecs);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.twitter.com" + path))
                .header("Authorization", "Bearer " + bearer)
                .build();

        HttpResponse<String> r = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = r.body();
        System.out.println(r.statusCode()+" === "+body);
        if (body.startsWith("{")) return new JSONObject(body);
        if (body.startsWith("[")) {
            JSONObject o = new JSONObject();
            JSONArray a = new JSONArray(body);
            o.put("data", a);
            return o;

        }

        throw new RuntimeException("unexpected data returned " + body);

    }



    public class TweetRequest {


        public Set<Long> getRetweeterIDs(long  tweetid) throws Exception {


            String url="/1.1/statuses/retweeters/ids.json?id="+tweetid+"&count=100";
            String pageUrl=url;
            final Set<Long> rts=new HashSet<>();
            String nextToken=null;
            do {
                JSONObject data=fetch(DELAY_RETWEETS,pageUrl);
                JSONArray ids=data.getJSONArray("ids");
                ids.forEach( i -> {
                    rts.add(Long.parseUnsignedLong(i.toString()));
                });
                nextToken=readNextToken(data);
                if(nextToken!=null) {
                    pageUrl=url+"&cursor="+nextToken;
                }
            } while(nextToken!=null);
            return rts;
        }

        public Set<Long> getRetweeterIDs(List<Tweet> tweets) throws Exception {

            Set<Long> ids=new HashSet<>();
            for(Tweet t:tweets) {
                    ids.addAll(getRetweeterIDs(t.getID()));
            }
            return ids;
        }

        public List<Tweet> getTweets(User user) throws Exception{

            List<Tweet> tweets=new LinkedList<>();
            long id=user.getID();
            System.out.println("getting tweets for "+user.getName()+"/"+id);
            // initial
            String  url="/2/users/"+id+"/tweets?max_results=100&tweet.fields=created_at,author_id,public_metrics,conversation_id,entities,in_reply_to_user_id,referenced_tweets,source,text,reply_settings,geo";
            String pageUrl=url;

            String nextToken=null;
            do {
                JSONObject o=fetch(DELAY_TWEETS,pageUrl);
                JSONArray a=o.getJSONArray("data");
                a.forEach( t -> {
                    JSONObject to= (JSONObject) t;
                    Tweet tweet=new Tweet(to);
                    tweets.add(tweet);

                } );
                nextToken=readNextToken(o);
                if(nextToken!=null) {
                    pageUrl=url+"&pagination_token="+nextToken;
                }
            } while(nextToken!=null);

            return tweets;
        }

    }

    private String readNextToken(JSONObject o) {
        if (o.has("meta")) {
            JSONObject meta=o.getJSONObject("meta");
            if (meta.has("next_token")) {
                return meta.getString("next_token");
            }

        }
        return null;
    }

    public class UserRequest {



        public User getUser(String screenName) throws Exception {

           Set<User> users=getUsers(screenName);
           if(users.isEmpty()) return null;
           return users.toArray(new User[0])[0];

        }

        public Set<User> getUsers(String ... screenName) throws Exception {

            JSONObject resp=fetch(DELAY_GETUSER,"/2/users/by?user.fields=created_at,description,id,location,name,public_metrics,username&usernames="+String.join(",",screenName));

            Set<User> users=new HashSet<>();

            if(resp.has("data")) {
                JSONArray data= (JSONArray) resp.get("data");
                data.forEach( u -> {
                    JSONObject jo= (JSONObject) u;
                    users.add(new User(jo));

                });
            }

            return users;
        }

        public Set<User> getUsers(Long ... screenName) throws Exception {

            StringBuilder sb=new StringBuilder();

            for(int i=0;i<screenName.length;i++) {
                if(sb.length()>0) sb.append(",");
                sb.append(screenName[i]);
            }

            JSONObject resp=fetch(DELAY_GETUSER,"/2/users?user.fields=created_at,description,id,location,name,public_metrics,username&ids="+sb.toString());

            Set<User> users=new HashSet<>();

            if(resp.has("data")) {
                JSONArray data= (JSONArray) resp.get("data");
                data.forEach( u -> {
                    JSONObject jo= (JSONObject) u;
                    users.add(new User(jo));

                });
            }

            return users;
        }

        public Set<User> getUsers(Set<Long> ids ) throws Exception {
            return getUsers(ids.toArray(new Long[0]));
        }
    }
}
