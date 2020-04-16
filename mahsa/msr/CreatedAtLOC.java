/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Call_URL;
import core.JSONUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mahsa
 */
public class CreatedAtLOC {
     public static String creation(String project, String[] tokens, int ct) {
        String created = "";
        long size = 0;
        try {
            if (ct >= (tokens.length)) {/// the the index for the tokens array...
                ct = 0; //// go back to the first index......
            }
            String url = "https://api.github.com/repos/" + project + "?access_token=" + tokens[ct++];
            String forks_url = Call_URL.callURL(url);
            if (JSONUtils.isValidJSONObject(forks_url) == false) {///                             
                System.out.println(":Invalid fork found!   - " + url);
                return null;
                //break;
            }

            JSONParser parser = new JSONParser();
            JSONObject jSONObject = (JSONObject) parser.parse(forks_url);
            created = (String) jSONObject.get("created_at");
            size = (long) jSONObject.get("size");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return created+"/"+size+"/"+ct;

    }
}
