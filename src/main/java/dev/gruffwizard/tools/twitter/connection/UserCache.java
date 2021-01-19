package dev.gruffwizard.tools.twitter.connection;

/*
 Class to contain current list of fetched users from Twitter
 */

import dev.gruffwizard.tools.twitter.entities.User;

import java.util.*;

public class UserCache {

    private Map<String, User> usersByName=new TreeMap<>();
    private Map<Long, User> usersByID=new HashMap<>();
    private Set<User> allusers=new HashSet<>();

    public boolean hasUser(Long rid) {
        return usersByID.containsKey(rid);
    }
    public boolean hasUser(String screenName) {
        screenName=screenName.toLowerCase();
        return usersByName.containsKey(screenName);

    }

    public User getUser(long rid) {
        return usersByID.get(rid);
    }
    public void add(User u) {

        if(allusers.contains(u)) return;

        usersByName.put(u.getName().toLowerCase(), u);
        usersByID.put(u.getID(), u);
        allusers.add(u);
    }

    public void addAll(Collection<User> users) {
        for(User u:users) {
                add(u);
        }
    }


    public User getUser(String screenName) {
        return usersByName.get(screenName);
    }

    public String[] identifyMissing(String[] screenName) {
        List<String> missing=new LinkedList<>();
        for(String s:screenName) {
            if(hasUser(s)==false) missing.add(s);
        }
        return missing.toArray(new String[0]);
    }

    public Set<User> getUsers(Collection<Long> ids) {
       return getUsers(ids.toArray(new Long[0]));
    }

    public Set<User> getUsers(Long ... ids) {
        Set<User> users=new HashSet<>();
        for(Long l:ids) {
            User u=usersByID.get(l);
            if(u!=null) users.add(u);
        }
        return users;
    }

    public Set<User> getUsers(String[] screenNames) {
        Set<User> users=new HashSet<>();
        for(String s:screenNames) {
            User u=usersByName.get(s);
            if(u!=null) users.add(u);
        }
        return users;
    }

    public Set<Long> getMissing(Long[] ids) {

        Set<Long> missingIds=new HashSet<>();
        for(Long l:ids) {
            if(hasUser(l)==false) missingIds.add(l);
        }

        return missingIds;
    }

     public UserCache getUserMap(Set<Long> req) {

       UserCache u=new UserCache();
       for(Long l:req) {
           u.add(getUser(l));
       }
       return u;
    }
}
