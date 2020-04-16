/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Call_URL;
import core.Create_Excel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Constants;

/**
 *
 * @author mosesopenja
 */
public class SearchReposTravis {

    public static void main(String[] args) throws Exception {
        searchrepos();
    }

    private static void searchrepos() throws Exception {
        int ct = 0;
        int counter = 0;
        String[] tokens = Constants.getToken();
        Object[] datas = null;
        //String path_new = "New_Repos_forks/";
        String path_new = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/";
        JSONParser parser = new JSONParser();
        String url_1 = "ruby+forks:>=200+fork:false+created:<2018-10-10T00:00:00Z";

        String url_2 = "java+forks:200..600+fork:false+created:<2018-10-10T00:00:00Z";
        String url_3 = "java+forks:>=601+fork:false+created:<2018-10-10T00:00:00Z";
        String url_4 = "python+forks:200..400+fork:false+created:<2018-10-10T00:00:00Z";
        String url_5 = "python+forks:401..4000+fork:false+created:<2018-10-10T00:00:00Z";
        String url_6 = "python+forks:>=4001+fork:false+created:<2018-10-10T00:00:00Z";
        String url_7 = "node+forks:>=200+fork:false+created:<2018-10-10T00:00:00Z";
        String url_8 = "php+forks:>=200+fork:false+created:<2018-10-10T00:00:00Z";
        String url_9 = "android+forks:200..380+fork:false+created:<2018-10-10T00:00:00Z";
        String url_10 = "android+forks:>=382+fork:false+created:<2018-10-10T00:00:00Z";
        String url_11 = "docker+forks:>=200+fork:false+created:<2018-10-10T00:00:00Z";
        /**
         * String url_2 =
         * "forks:2+fork:false+created:2012-04-10T00:00:01Z..2013-02-02T00:00:00Z";
         * String url_3 =
         * "forks:2+fork:false+created:2013-02-02T00:00:01Z..2013-08-10T06:20:00Z";
         * String url_4 =
         * "forks:2+fork:false+created:2013-08-10T06:20:01Z..2014-01-29T16:20:00Z";
         * String url_5 =
         * "forks:2+fork:false+created:2014-01-29T16:20:01Z..2014-07-25T14:20:00Z";
         * String url_6 =
         * "forks:2+fork:false+created:2014-07-25T14:20:01Z..2014-12-31T10:00:00Z";
         * String url_7 =
         * "forks:2+fork:false+created:2014-12-31T10:00:01Z..2015-05-08T02:00:00Z";
         * String url_8 =
         * "forks:2+fork:false+created:2015-05-08T02:00:01Z..2015-09-15T20:00:00Z";
         * String url_9 =
         * "forks:2+fork:false+created:2015-09-15T20:00:01Z..2016-01-14T12:50:00Z";
         *
         * String url_10 =
         * "forks:2+fork:false+created:2016-01-14T12:50:01Z..2016-05-06T02:50:00Z";
         * String url_11 =
         * "forks:2+fork:false+created:2016-05-06T02:50:01Z..2016-08-30T22:50:00Z";
         * String url_12 =
         * "forks:2+fork:false+created:2016-01-14T12:50:01Z..2016-05-06T02:50:00Z";
         * String url_13 =
         * "forks:2+fork:false+created:2016-05-06T02:50:01Z..2016-08-30T22:50:00Z";
         * String url_14 =
         * "forks:2+fork:false+created:2016-08-30T22:50:01Z..2016-12-06T06:20:00Z";
         * String url_15 =
         * "forks:2+fork:false+created:2016-12-06T06:20:01Z..2017-03-28T15:20:00Z";
         * String url_16 =
         * "forks:2+fork:false+created:2017-03-28T15:20:01Z..2017-08-15T20:20:00Z";
         * String url_17 =
         * "forks:2+fork:false+created:2017-08-15T20:20:01Z..2017-12-31T00:00:00Z";
         * String url_18 = "forks:3+fork:false+created:%3C2012-12-19T23:00:00Z";
         * String url_19 =
         * "forks:3+fork:false+created:2012-12-19T23:00:01Z..2013-11-28T20:00:00Z";
         * String url_20 =
         * "forks:3+fork:false+created:2013-11-28T20:00:01Z..2014-09-30T23:00:00Z";
         * String url_21 =
         * "forks:3+fork:false+created:2014-09-30T23:00:01Z..2015-06-07T12:20:00Z";
         *
         * String url_22 =
         * "forks:3+fork:false+created:2015-06-07T12:20:01Z..2016-02-06T23:20:00Z";
         * String url_23 =
         * "forks:3+fork:false+created:2016-02-06T23:20:00Z..2016-09-13T09:20:00Z";
         * String url_24 =
         * "forks:3+fork:false+created:2016-09-13T09:20:01Z..2017-04-16T09:20:00Z";
         * String url_25 =
         * "forks:3+fork:false+created:2017-04-16T09:20:01Z..2017-12-31T00:00:00Z";
         * String url_26 = "forks:4+fork:false+created:%3C2013-07-17T10:00:00Z";
         * String url_27 =
         * "forks:4+fork:false+created:2013-07-17T10:00:01Z..2014-12-06T10:00:00Z";
         * String url_28 =
         * "forks:4+fork:false+created:2014-12-06T10:00:01Z..2016-01-15T00:00:00Z";
         * String url_29 =
         * "forks:4+fork:false+created:2016-01-15T00:00:00Z..2016-12-26T10:00:00Z";
         * String url_30 =
         * "forks:4+fork:false+created:2016-12-26T10:00:01Z..2017-12-31T00:00:00Z";
         * String url_31 = "forks:5+fork:false+created:%3C2014-02-01T10:00:00Z";
         * String url_32 =
         * "forks:5+fork:false+created:2015-10-14T10:00:01Z..2017-02-26T10:00:00Z";
         * String url_33 =
         * "forks:5+fork:false+created:2017-02-26T10:00:01Z..2017-12-31T00:00:00Z";
         *
         * String url_34 = "forks:6+fork:false+created:%3C2014-09-13T00:00:00Z";
         * String url_35 =
         * "forks:6+fork:false+created:2014-09-13T00:00:01Z..2016-09-20T00:00:00Z";
         * String url_36 =
         * "forks:6+fork:false+created:2016-09-20T00:00:01Z..2017-12-31T00:00:00Z";
         * String url_37 = "forks:7+fork:false+created:%3C2015-05-25T00:00:00Z";
         * String url_38 =
         * "forks:7+fork:false+created:2015-05-25T00:00:00Z..2017-12-31T00:00:00Z";
         * String url_39 =
         * "forks:8..12+fork:false+created:%3C2013-02-28T02:00:00Z"; String
         * url_40 =
         * "forks:8..12+fork:false+created:2013-02-28T02:00:00Z..2014-05-06T20:00:00Z";
         * String url_41 =
         * "forks:8..12+fork:false+created:2014-05-06T20:00:01Z..2015-05-09T20:00:00Z";
         * String url_42 =
         * "forks:8..12+fork:false+created:2015-05-09T20:00:01Z..2016-04-02T00:00:00Z";
         * String url_43 =
         * "forks:8..12+fork:false+created:2016-04-02T00:00:01Z..2017-01-01T15:00:00Z";
         * String url_44 =
         * "forks:8..12+fork:false+created:2017-01-01T15:00:01Z..2017-12-31T00:00:00Z";
         * String url_45 =
         * "forks:12..30+fork:false+created:%3C2012-08-01T00:00:00Z";
         *
         * String url_46 =
         * "forks:12..30+fork:false+created:2012-08-01T00:00:00Z..2013-08-05T10:00:00Z";
         * String url_47 =
         * "forks:12..30+fork:false+created:2013-08-05T10:00:01Z..2014-07-06T10:00:00Z";
         * String url_48 =
         * "forks:12..30+fork:false+created:2014-07-06T10:00:01Z..2015-03-24T10:00:00Z";
         * String url_49 =
         * "forks:12..30+fork:false+created:2015-03-24T10:00:01Z..2015-12-07T21:00:00Z";
         * String url_50 =
         * "forks:12..30+fork:false+created:2015-12-07T21:00:01Z..2016-07-21T20:00:00Z";
         * String url_51 =
         * "forks:12..30+fork:false+created:2016-07-21T20:00:01Z..2017-03-05T20:50:00Z";
         * String url_52 = "forks:12..30+fork:false+created:
         * 2017-03-05T20:50:01Z..2017-12-31T00:00:00Z"; String url_53 =
         * "forks:%3E=31+fork:false+created:%3C2012-08-13T15:00:00Z"; String
         * url_54 =
         * "forks:%3E=31+fork:false+created:2012-08-13T15:00:00Z..2013-09-22T15:00:00Z";
         * String url_55 =
         * "forks:%3E=31+fork:false+created:2013-09-22T15:00:01Z..2014-08-09T23:50:00Z";
         * String url_56 =
         * "forks:%3E=31+fork:false+created:2014-08-09T23:50:01Z..2015-04-03T23:50:00Z";
         * String url_57 =
         * "forks:%3E=31+fork:false+created:2015-04-03T23:50:01Z..2019-07-12T14:50:00Z";
         *
         * *
         */
        /**
         * String url_58 =
         * "forks:%3E=31+fork:false+created:2015-11-14T08:50:01Z..2016-07-08T08:50:00Z";
         * String url_59 =
         * "forks:%3E=31+fork:false+created:2016-07-08T08:50:01Z..2017-04-02T08:50:00Z";
         * String url_60 =
         * "forks:%3E=31+fork:false+created:2017-04-02T08:50:01Z..2017-12-31T00:00:00Z";
         * ***
         */

        String[] dates_urls = {url_1, url_2, url_3, url_4
        /**
         * , url_2, url_3, url_4, url_5, url_6, url_7, url_8, url_9, url_10,
         * url_11, url_12, url_13, url_14, url_15, url_16, url_17, url_18,
         * url_19, url_20, url_21, url_22, url_23, url_24, url_25, url_26,
         * url_27, url_28, url_29, url_30, url_31, url_32, url_33, url_34,
         * url_35, url_36, url_37, url_38, url_39, url_40, url_41, url_42,
         * url_43, url_44, url_45, url_46, url_47, url_48, url_49, url_50,
         * url_51, url_52, url_53, url_54, url_55, url_56, url_57/**, url_58,
         * url_59, url_60*
         */
        };
        List<String> projectL = new ArrayList<>();

        ArrayList< Object[]> allobj = new ArrayList<Object[]>();
        datas = new Object[]{"Repos_Name", "Names", "Created_at", "login", "Fork", "Size", "Language", "Description"};
        allobj.add(datas);

        for (int a = 0; a < dates_urls.length; a++) {
            System.out.println(a + "    Reading for: " + dates_urls[a]);

            try {

                int x = 0; // control to break out of the infinite loop
                int p = 1; // Page number parameter	
                int tot = 0;
                while (true) {

                    if (counter == tokens.length - 1) {
                        counter = 0;
                    }

                    if (p == 11) {
                        break;
                    }
                    int count_flag = 0;
                    String jsonString = Call_URL.callURL("https://api.github.com/search/repositories?q=" + dates_urls[a] + "&sort=forks&order=asc&page=" + p + "&per_page=100&access_token=" + tokens[counter++]);
                    //System.out.println(p + "=https://api.github.com/search/repositories?q=android%20app+forks:>=91+fork:false&sort=forks&order=asc&page=" + p);
                    if (jsonString.equals("Error")) {
                        break;
                    }

                    //System.out.println(jsonString);
                    String jsonData = "";
                    String line = "";

                    String inputLine;
                    JSONObject obj = (JSONObject) parser.parse(jsonString);

                    if (obj.toString().equals(null)) {
                        x = 1;//System.out.println("Empty JSON Object Returned: "+a.toString());
                        break;
                    }

                    // Loop through each item
                    JSONArray items_array = (JSONArray) obj.get("items");
                    long total_count = (long) obj.get("total_count");
                    tot += items_array.size();

                    System.out.println("     " + p + "   " + tot);

                    if (items_array.size() == 0) {
                        break;
                    }
                    //for (Object jsonObj : JSONArray) {
                    for (int i = 0; i < items_array.size(); i++) {

                        JSONObject jsonObj = (JSONObject) items_array.get(i);
                        String fullName = (String) (String) jsonObj.get("full_name");
                        //System.out.println("      " + i + " : " + fullName);
                        String repoName = (String) (String) jsonObj.get("name");
                        long size = (Long) jsonObj.get("size");
                        String created_at = (String) (String) jsonObj.get("created_at");
                        JSONObject jsonObj_owner = (JSONObject) jsonObj.get("owner");
                        String logins = (String) jsonObj_owner.get("login");
                        long forks = (Long) jsonObj.get("forks_count");
                        String lang = "";
                        if ((String) jsonObj.get("language") != null) {
                            lang = (String) jsonObj.get("language");
                        }

                        String description = "";
                        if (jsonObj.get("description") != null) {
                            description = (String) jsonObj.get("description");

                        }

                        if (!projectL.contains(fullName)) {
                            String details = CheckTravis.checkTravis(fullName, ct);
                            ct = Integer.parseInt(details.split("/")[1]);
                            int flag = Integer.parseInt(details.split("/")[0]);
                            if (flag > 0) {
                                count_flag++;
                                datas = new Object[]{fullName, repoName, created_at, logins, forks, size, lang, description};
                                allobj.add(datas);
                                projectL.add(fullName);
                            }
                        }

                    }

                    if (count_flag > 0) {
                        String file_name = "TravisRepos.xlsx";
                        Create_Excel.createExcel2(allobj, 0, path_new + file_name, "forked200");
                    }

                    p++;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
