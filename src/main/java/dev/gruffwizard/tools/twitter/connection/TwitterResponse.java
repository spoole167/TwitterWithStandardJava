package dev.gruffwizard.tools.twitter.connection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;

public class TwitterResponse {

    private HttpResponse<String> resp;

    public TwitterResponse(HttpResponse<String> r) {
    this.resp=r;
    }

    public JSONObject getObject() {
        String data=resp.body();
        if(data.startsWith("{")==false) throw new RuntimeException("data is not JSON object :"+data);
        JSONObject obj= new JSONObject(data);
        if (obj.has("errors")) {
            System.out.println(obj.get("errors"));
            throw new RuntimeException("boom");
        }
        return obj;

    }

    public JSONArray getArray() {
        String data=resp.body();
        if(data.startsWith("[")==false) throw new RuntimeException("data is not JSON array :"+data);

        return new JSONArray(data);

    }
    /*

        if(body.startsWith("[")) {
            try {
                JSONArray a=new JSONArray(body);
            }
        }

       try {
           obj = new JSONObject(body);
       } catch(JSONException e) {

            System.out.println("parser error ");
           System.out.println(r.body());
           throw e;
       }
       // System.out.println(obj);

        if (obj.has("errors")) {
            System.out.println(obj.get("errors"));
            throw new RuntimeException("boom");
        }
        return obj;
    }
     */
}
