/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Call_URL;
import core.DateOperations;
import core.JSONUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import reads.CountCommits;
import util.Constants;

/**
 *
 * @author mosesopenja
 */
public class Forks {
    
    public static ArrayList<String> getDatas(String project, String[] token, int ct) throws ParseException {
        int y_m_d = 20160306;
        int h_m_s = 0;
        long marchDate = 20160306;
        marchDate = (long) 20160306;
        ArrayList<String> forkDetails = new ArrayList<String>();

        JSONParser parser = new JSONParser();
        try {
            int x = 0; // control to break out of the infinite loop
            int p = 1; // Page number parameter
            int i = 0; // Commit Counter
            int count = 0;
            while (true) {

                if (ct == (token.length)) {/// the the index for the tokens array...
                    ct = 0; //// go back to the first index......
                }
                // xxxxxx Need to include the Since and Until parameters so as to return only the commits between two given tags
                String forks_url = Call_URL.callURL("https://api.github.com/repos/" + project + "/forks?page=" + p + "&until=" + Constants.cons.TODAY_DATE + "per_page=100&access_token=" + token[ct++]);

                if (JSONUtils.isValidJSON(forks_url) == false) {///

                    System.out.println(" :Invalid fork found!");
                    break;
                }
                JSONArray a = (JSONArray) parser.parse(forks_url);
                if (a.toString().equals("[]")) {
                    x = 1;//System.out.println("Empty JSON Object Returned: "+a.toString());
                    break;
                }

                count += a.size();
                if (count % 500 == 0) {
                    System.out.println("      " + count);
                }
                for (Object o : a) {
                    JSONObject jsonObject = (JSONObject) o;
                    String full_name = (String) jsonObject.get("full_name");
                    if ((String) jsonObject.get("created_at") != null) {
                        String created_at = (String) jsonObject.get("created_at");
                        List<String> c_return = CountCommits.countCommits2(full_name, created_at, Constants.cons.TODAY_DATE, token, ct);
                        double counts = Double.parseDouble(c_return.get(0));
                        String first_date = c_return.get(2);
                        String last_date = c_return.get(1);
                        ct = Integer.parseInt(c_return.get(3));
                        if (counts > 0) {
                            forkDetails.add(counts + "/" + full_name + "/" + created_at + "/" + first_date + "/" + last_date);
                        }
                    }

                }

                p++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        forkDetails.add(ct + "");
        return forkDetails;

    }
}
