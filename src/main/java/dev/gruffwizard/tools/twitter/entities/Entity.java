package dev.gruffwizard.tools.twitter.entities;

import org.json.JSONObject;

public abstract class  Entity {


    String get(JSONObject obj, String field, String defaultValue) {
        if(obj.has(field)) return obj.getString(field);
        return defaultValue;
    }
     int get(JSONObject obj,String field,int defaultValue) {
        if(obj.has(field)) return obj.getInt(field);
        return defaultValue;
    }
    long get(JSONObject obj,String field,long defaultValue) {
        if(obj.has(field)) return obj.getLong(field);
        return defaultValue;
    }
}
