/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Call_URL;
import core.Create_Excel;
import core.DateOperations;
import core.JSONUtils;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Constants;

/**
 *
 * @author mahsa
 */
public class Collect_PR {

    /**
     *
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        getDatas();
    }

    /**
     *
     * @throws ParseException
     */
    public static void getDatas() throws ParseException {
        Object[] datas = null;
        String project = "activemerchant/active_merchant";
        String file = "PR-Details-active_merchant.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/Data/";
        int ct = 0;
        ArrayList< Object[]> allobj = new ArrayList<Object[]>();
        datas = new Object[]{"PR-ID", "PR-ID-Number", "PR-Size", "PR-Status", "Creation_At", "Closed_At", "Merged_At", "User-login", "User-ID","Merged-Shaa", "Hash", "PR-Title", "PR-Body"};
        allobj.add(datas);
        JSONParser parser = new JSONParser();
        try {
            int x = 0; // control to break out of the infinite loop
            int p = 1; // Page number parameter
            int i = 0; // Commit Counter
            int counts = 0;
            int index = 0;
            while (true) {
                if (ct == (Constants.getToken().length)) {/// the the index for the tokens array...
                    ct = 0; //// go back to the first index......
                }
                // xxxxxx Need to include the Since and Until parameters so as to return only the commits between two given tags
                String forks_url = Call_URL.callURL("https://api.github.com/repos/" + project + "/pulls?page=" + p + "&per_page=100&state=closed&access_token=" + Constants.getToken()[ct++]);
                if (JSONUtils.isValidJSON(forks_url) == false) {///

                    System.out.println(" :Invalid Pull found!");
                    break;
                }
                JSONArray a = (JSONArray) parser.parse(forks_url);
                if (a.toString().equals("[]")) {
                    x = 1;//System.out.println("Empty JSON Object Returned: "+a.toString());
                    break;
                }
                for (Object o : a) {

                    JSONObject jsonObject = (JSONObject) o;
                    String state = (String) jsonObject.get("state");
                    String created_at = (String) jsonObject.get("created_at");
                    String updated_at = (String) jsonObject.get("updated_at");
                    String title = (String) jsonObject.get("title");
                    String merge_commit_sha = (String) jsonObject.get("merge_commit_sha");
                    String body = (String) jsonObject.get("body");
                    String closed_at = "";
                    long id = (long) jsonObject.get("id");
                    long number = (long) jsonObject.get("number");
                    String merged_at = "";
                    if (jsonObject.get("merged_at") != null) {
                        merged_at = (String) jsonObject.get("merged_at");
                    }
                    if (state.equals("closed")) {
                        closed_at = (String) jsonObject.get("closed_at");
                    }
                    String user_login = "";
                    long user_id = 0;
                    String sha = "", full_name = "";
                    System.out.println(counts + " : " + id + " / " + closed_at + "/" + merged_at);
                    long size = 0;
                    if (jsonObject.get("user") != null) {
                        JSONObject jSONObject = (JSONObject) jsonObject.get("user");
                        user_login = (String) jSONObject.get("login");
                        user_id = (long) jSONObject.get("id");
                    }
                    if (jsonObject.get("head") != null) {
                        JSONObject headObject = (JSONObject) jsonObject.get("head");
                        sha = (String) headObject.get("sha");
                        if (headObject.get("repo") != null) {
                            JSONObject jSONObject = (JSONObject) headObject.get("repo");
                            full_name = (String) jSONObject.get("full_name");
                            size = (long) jSONObject.get("size");
                        }

                    }
                    if (DateOperations.compareDates(Constants.cons.RELEASE_TODAY_SINCE, created_at) && DateOperations.compareDates(created_at, Constants.cons.RELEASE_TODAY_UNTIL)) {
                        datas = new Object[]{id, number, size, state, created_at, closed_at, merged_at, user_login, user_id,merge_commit_sha, sha, title, body};
                        allobj.add(datas);
                    }
                    counts++;
                }
                if (counts%1000 == 0) {
                    index ++;
                }
                Create_Excel.createExcel2(allobj, 0, path + file, "pr_details");
                p++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
