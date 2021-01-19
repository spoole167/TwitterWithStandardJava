package dev.gruffwizard.tools.twitter;

import dev.gruffwizard.tools.twitter.connection.TwitterConnection;
import dev.gruffwizard.tools.twitter.connection.TwitterResponse;
import dev.gruffwizard.tools.twitter.entities.Tweet;
import dev.gruffwizard.tools.twitter.entities.User;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    private static String bearer="";




    private static long DELAY_FOLLOWERS = Math.round(15f*60f/15f*1.1f)*1000;
    private static long DELAY_RECENT_SEARCH = Math.round(15f*60f/450f*1.1f)*1000;



    public static void main(String[] args) throws Exception {

        String screenName="quarkusio";

        TwitterSession s=new TwitterSession(bearer);

        User user=s.getUser(screenName);

        List<Tweet> tweets=s.getTweets(user);

        Set<User> retweeters=s.getRetweeters(tweets);



        File f=new File("tweets-for-"+screenName+".json");
        PrintWriter pw=new PrintWriter(f);
        pw.print(tweets);
        pw.close();


    }

}
