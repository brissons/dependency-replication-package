/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import com.opm.mahsa.msr.updated.ExtractFileContent;
import core.Call_URL;
import core.Create_Excel;
import core.DateOperations;
import core.File_Details;
import core.JSONUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import picks.Pick_GeneralNext;
import picks.Pick_GeneralNumeric;
import reads.Commits;
import reads.Creation_Date;
import util.Constants;

/**
 *
 * @author mahsa
 */
public class Fork_ForkDetails {

    public static void main(String[] args) throws Exception {
        getDatas();
    }

    public static void getDatas() throws ParseException, Exception {

        String file = "UNiq_Commit_Message_Forks_0222.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/00newRepo/";
        String DownloadPath = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/downloads/";
        
        String path_new = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/00newRepo/";
        
        int y_m_d = 20160306;
        int h_m_s = 0;
        long marchDate = 20160306;
        marchDate = (long) 20160306;
        ArrayList<String> forkDetails = new ArrayList<String>();

        //int index = 0;
        JSONParser parser = new JSONParser();
        String[] FILES = {file};
        for (int a = 0; a < FILES.length; a++) {
            int numbers = File_Details.getWorksheets(path + FILES[a]);
            int count = 63;
            while (count < numbers) {

                int flag = 0;
                String project = File_Details.setProjectName(path + FILES[a], count, "A2");
                String mCreation = File_Details.setProjectName(path + FILES[a], count, "C2");
                List<String> nameList = Pick_GeneralNext.pick(path + FILES[a], count, 1, 2);
                List<String> createList = Pick_GeneralNext.pick(path + FILES[a], count, 2, 2);
                List<String> firstList = Pick_GeneralNext.pick(path + FILES[a], count, 3, 2);
                List<String> lastList = Pick_GeneralNext.pick(path + FILES[a], count, 4, 2);

                List<Double> commitAfter = Pick_GeneralNumeric.pick_3(path + FILES[a], count, 6, 2);
                List<Double> uniqueList = Pick_GeneralNumeric.pick_3(path + FILES[a], count, 7, 2);
                List<Double> daysList = Pick_GeneralNumeric.pick_3(path + FILES[a], count, 8, 2);

                Object[] datas = null;
                ArrayList< Object[]> allobj = new ArrayList<Object[]>();
                datas = new Object[]{"BaseLine", "Repos_Name", "Commits/Forks", "Created_at", "FirstCom", "LastCom", "CommitsAfter", "Unique", "Active_days", "LOC", "TotalDependencies", "Dependencies", "BuildType", "Description"};
                allobj.add(datas);

                int ct = 0;
                int count_index = 0;
                for (int aa = 0; aa < nameList.size(); aa++) {
                    // System.out.println(count+" : "+min_date);
                    List<List<String>> allList_1 = Commits.count(nameList.get(aa), "mlp", "", Constants.cons.TODAY_DATE, Constants.getToken(), ct);
                    List<String> shaList_1 = allList_1.get(0);
                    List<String> dateList_1 = allList_1.get(1);
                    List<String> messageList_1 = allList_1.get(2);

                    //datas = new Object[]{project, nameList.get(aa), shaList_1.size(), createList.get(aa), firstList.get(aa), lastList.get(aa), commitAfter.get(aa), uniqueList.get(aa), daysList.get(aa), "", "", "", "", ""};
                    //allobj.add(datas);
                    System.out.println(aa + " : " + nameList.get(aa)+" : "+shaList_1.size());

                    int index = 0;
                    try {
                        int p = 1; // Page number parameter
                        int count_forks = 0;
                        while (true) {

                            if (ct == Constants.getToken().length) {
                                ct = 0;
                            }
                            
                            // xxxxxx Need to include the Since and Until parameters so as to return only the commits between two given tags
                            String forks_url = Call_URL.callURL("https://api.github.com/repos/" + nameList.get(aa) + "/forks?page=" + p + "&until=" + Constants.cons.TODAY_DATE + "&per_page=100&access_token=" + Constants.getToken()[ct++]);

                            
                            if (JSONUtils.isValidJSON(forks_url) == false) {///

                                System.out.println(" :Invalid fork found!");
                                break;
                            }
                            //System.out.println(forks_url);
                            JSONArray jSONArray = (JSONArray) parser.parse(forks_url);
                            if (jSONArray.toString().equals("[]")) {
                                //x = 1;//System.out.println("Empty JSON Object Returned: "+a.toString());
                                break;
                            }

                            count_forks += jSONArray.size();
                            if (count_forks % 500 == 0) {
                                System.out.println("      " + count_forks);
                            }
                            //System.out.println("Forks::::: "+jSONArray.size());
                            for (Object o : jSONArray) {
                                JSONObject jsonObject = (JSONObject) o;
                                String full_name = (String) jsonObject.get("full_name");

                                if ((String) jsonObject.get("created_at") != null) {
                                    String created_at = (String) jsonObject.get("created_at");
                                    String updated_at = (String) jsonObject.get("updated_at");
                                    String description = (String) jsonObject.get("description");
                                    String language = "";
                                    if (jsonObject.get("language") != null) {
                                        language = (String) jsonObject.get("language");
                                    }
                                    long size = (long) jsonObject.get("size");

                                    List<String> Dependencies = new ArrayList<>();
                                    List<String> buildType = new ArrayList<>();
                                    ///Download the file content here
                                    if (ct == Constants.getToken().length) {
                                        ct = 0;
                                    }

                                    List<List<String>> allList = Commits.count(full_name, "fp", created_at, "", Constants.getToken(), ct);
                                    List<String> modelL = allList.get(allList.size() - 1);
                                    ct = Integer.parseInt(modelL.get(modelL.size() - 1));

                                    List<List<String>> allList_3 = allList;
                                    List<String> shaList_3 = allList_3.get(0);
                                    List<String> dateList_3 = allList_3.get(1);
                                    List<String> messageList_3 = allList_3.get(2);
                                    
                                    //System.out.println("fork of forks commits: "+shaList_3.size());

                                    if (shaList_3.size() > 0) {
                                        String jsonString = Call_URL.callURL("https://api.github.com/repos/" + full_name + "/contents?access_token=" + Constants.getToken()[ct++]);
                                        if (!jsonString.equals("Error")) {
                                            if (JSONUtils.isValidJSON(jsonString) == true) {
                                                JSONArray jsonArray = (JSONArray) parser.parse(jsonString);
                                                if (jsonArray.toString().equals("[]")) {
                                                    /// Break out of the loop, when empty array is found!
                                                    //break;
                                                }

                                                for (Object jsonObj : jsonArray) {
                                                    JSONObject OBJ = (JSONObject) jsonObj;
                                                    String name = (String) OBJ.get("name");
                                                    String type = (String) OBJ.get("type");
                                                    if (!type.equals("dir")) {
                                                        String download_url = (String) OBJ.get("download_url");

                                                        if (name.contains("build.gradle")) {
                                                            if (ct == Constants.getToken().length) {
                                                                ct = 0;

                                                            }
                                                            String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                                            String fullPath = DownloadPath + full_name.split("/")[0] + "_" + name;
                                                            CreateFiles.save(fullPath, contents);

                                                            List<String> extracts = ExtractFileContent.readGradle(contents);
                                                            Dependencies.addAll(extracts);
                                                            buildType.add("gradle");
                                                        }
                                                        if (name.contains("pom.xml")) {
                                                            if (ct == Constants.getToken().length) {
                                                                ct = 0;
                                                            }
                                                            String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                                            String fullPath = DownloadPath + full_name.split("/")[0] + "_" + index + "_" + name;
                                                            CreateFiles.save(fullPath, contents);
                                                            List<String> extracts = ExtractFileContent.dom(fullPath);
                                                            Dependencies.addAll(extracts);
                                                            buildType.add("maven");
                                                            // break;
                                                        }
                                                    }

                                                }
                                            }
                                        }

                                        if (Dependencies.size() > 0) {

                                            String dependencieString = "";
                                            String types = "";
                                            Set<String> typeSet = new HashSet<>();
                                            typeSet.addAll(buildType);
                                            for (int i = 0; i < Dependencies.size(); i++) {
                                                dependencieString = dependencieString.concat(Dependencies.get(i) + ", ");
                                            }
                                            List<String> BList = new ArrayList<>();
                                            BList.addAll(typeSet);
                                            if (BList.size() == 1) {
                                                types = BList.get(0);
                                            } else {
                                                for (int i = 0; i < BList.size(); i++) {
                                                    types = types.concat(BList.get(i) + ", ");
                                                }
                                            }
                                            String shaaDetails = "";
                                            for (int i = 0; i < shaList_1.size(); i++) {
                                                shaaDetails = shaaDetails.concat(shaList_1.get(i) + "/");
                                            }
                                            int unique_forks = 0;
                                            for (int i = 0; i < shaList_3.size(); i++) {
                                                if (!messageList_1.contains(messageList_3.get(i))) {
                                                    unique_forks++;
                                                }
                                            }

                                            String firstDate = dateList_3.get(dateList_3.size() - 1);
                                            String lastDate = dateList_3.get(0);
                                            double fdays = 0;
                                            try {
                                                fdays = Double.parseDouble(DateOperations.diff(created_at, lastDate).split("/")[0]);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            datas = new Object[]{project, nameList.get(aa), full_name, created_at, firstDate, lastDate, shaList_3.size(), unique_forks, fdays, size, Dependencies.size(), dependencieString, types, description};
                                            allobj.add(datas);
                                            System.out.println("       ------ " + index + " : " + full_name);
                                            Create_Excel.createExcel2(allobj, 0, path + "Fork_ForksDetails_002.xlsx", count + "_" + project.split("/")[0] );
                                            index++;
                                        }
                                    }

                                }

                            }

                            p++;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count_index++;
                }
                count ++;
            }
        }

    }

}
