/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import com.opm.mahsa.msr.CreateFiles;
import core.Call_URL;
import core.JSONUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.Constants;

/**
 *
 * @author mosesopenja
 */
public class CheckTravis {
    /**
     * 
     * @param project
     * @param shaa
     * @param ct
     * @return
     * @throws Exception 
     */
    public static String checkTravis(String project, int ct) throws Exception {

        int flag = 0;
        if (ct == (Constants.getToken().length)) {/// the the index for the tokens array...
            ct = 0; //// go back to the first index......
        }
        String jsonString = "";
        jsonString = Call_URL.callURL("https://api.github.com/repos/" + project + "/contents?access_token=" + Constants.getToken()[ct++]);
        if (!jsonString.equals("Error")) {
            JSONParser parser = new JSONParser();
            if (JSONUtils.isValidJSON(jsonString) == true) {
                JSONArray jsonArray = (JSONArray) parser.parse(jsonString);
                if (jsonArray.toString().equals("[]")) {
                    /// Break out of the loop, when empty array is found!
                    //break;
                }
                for (Object jsonObj : jsonArray) {
                    JSONObject jsonObject = (JSONObject) jsonObj;
                    String name = (String) jsonObject.get("name");
                    String download_url = (String) jsonObject.get("download_url");
                    if (name.contains("travis.yml")) {
                        flag = 1;
                        //break;
                    }
                    
                }
            }
        }

        return flag + "/" + ct;

    }
}
