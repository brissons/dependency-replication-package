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
import core.JSONUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import picks.Pick_GeneralNext;
import picks.Pick_GeneralNumeric;
import reads.Commits;
import util.Constants;

/**
 *
 * @author john
 */
public class ReposForks_TravisBuild {

    public static void main(String[] args) throws Exception {
        getDatas();
    }

    public static void getDatas() throws ParseException, Exception {

        String file = "remainofremain_18.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/00newRepo/";
        String DownloadPath = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/downloads/";
        int y_m_d = 20160306;
        int h_m_s = 0;
        long marchDate = 20160306;
        marchDate = (long) 20160306;
        ArrayList<String> forkDetails = new ArrayList<String>();

        //int index = 0;
        JSONParser parser = new JSONParser();
        List<String> projectL = Pick_GeneralNext.pick2(path + file, 0, 0, 1);
        List<String> createdAtL = Pick_GeneralNext.pick2(path + file, 0, 2, 1);
        List<Double> ForksL = Pick_GeneralNumeric.pick_3(path + file, 0, 4, 1);
        List<Double> commits = Pick_GeneralNumeric.pick_3(path + file, 0, 5, 1);
        List<Double> LOC = Pick_GeneralNumeric.pick_3(path + file, 0, 6, 1);
        List<String> LanguangeL = Pick_GeneralNext.pick2(path + file, 0, 7, 1);
        List<String> descriptionsL = Pick_GeneralNext.pick2(path + file, 0, 8, 1);

        
        Object[] datas = null;
        int ct = 0;
        int count_index = 0;
        for (int aa = 0; aa < projectL.size(); aa++) {

            ArrayList< Object[]> allobj = new ArrayList<Object[]>();
            datas = new Object[]{"Repos_Name", "Forks", "Created_at", "Commits", "LOC", "Language", "TotalDependencies", "Dependencies", "BuildType", "Description","Commits","commit_details"};
            allobj.add(datas);
            List<String> DependenciesMLP = new ArrayList<>();
            List<String> buildTypeMLP = new ArrayList<>();
            if (ct == Constants.getToken().length) {
                ct = 0;
            }
            //System.out.println(projectL.size());

            String jsonString = Call_URL.callURL("https://api.github.com/repos/" + projectL.get(aa) + "/contents?access_token=" + Constants.getToken()[ct++]);
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
                            if (name.contains("travis.yml")) {
                                // if (ct == Constants.getToken().length) {
                                //     ct = 0;
                                // }
                                // String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                // String fullPath = DownloadPath + projectL.get(aa).split("/")[0] + "_" + name;
                                // CreateFiles.save(fullPath, contents);

                            }
                            if (name.contains("build.gradle")) {
                                if (ct == Constants.getToken().length) {
                                    ct = 0;
                                }
                                String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                String fullPath = DownloadPath + projectL.get(aa).split("/")[0] + "_" + aa + "_" + name;
                                CreateFiles.save(fullPath, contents);

                                List<String> extracts = ExtractFileContent.readGradle(contents);
                                DependenciesMLP.addAll(extracts);
                                buildTypeMLP.add("gradle");
                            }
                            if (name.contains("pom.xml")) {
                                if (ct == Constants.getToken().length) {
                                    ct = 0;
                                }
                                String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                String fullPath = DownloadPath + projectL.get(aa).split("/")[0] + "_" + aa + "_" + name;
                                CreateFiles.save(fullPath, contents);

                                List<String> extracts = ExtractFileContent.dom(fullPath);
                                DependenciesMLP.addAll(extracts);
                                buildTypeMLP.add("maven");
                                // break;
                            }
                        }
                    }
                }
            }
            String dependencieString = "";
            String types = "";
            Set<String> typeSet = new HashSet<>();
            typeSet.addAll(buildTypeMLP);
            for (int i = 0; i < DependenciesMLP.size(); i++) {
                dependencieString = dependencieString.concat(DependenciesMLP.get(i) + ", ");
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

            if (DependenciesMLP.size() > 0) {

                datas = new Object[]{projectL.get(aa), ForksL.get(aa), createdAtL.get(aa), commits.get(aa), LOC.get(aa), LanguangeL.get(aa), DependenciesMLP.size(), dependencieString, types, descriptionsL.get(aa)};
                allobj.add(datas);
                System.out.println(aa + " : " + projectL.get(aa));
                Create_Excel.createExcel2(allobj, 0, path + "remainofremain1.xlsx", projectL.get(aa).split("/")[0] + "_" + count_index);

                int index = 0;
                try {
                    int p = 1; // Page number parameter
                    int count = 0;
                    while (true) {

                        if (ct == Constants.getToken().length) {
                            ct = 0;
                        }
                        // xxxxxx Need to include the Since and Until parameters so as to return only the commits between two given tags
                        String forks_url = Call_URL.callURL("https://api.github.com/repos/" + projectL.get(aa) + "/forks?page=" + p + "&until=" + Constants.cons.TODAY_DATE + "per_page=100&access_token=" + Constants.getToken()[ct++]);

                        if (JSONUtils.isValidJSON(forks_url) == false) {///

                            System.out.println(" :Invalid fork found!");
                            break;
                        }
                        JSONArray a = (JSONArray) parser.parse(forks_url);
                        if (a.toString().equals("[]")) {
                            //x = 1;//System.out.println("Empty JSON Object Returned: "+a.toString());
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

                                List<List<String>> allList_1 = Commits.count(full_name, "mlp", created_at, Constants.cons.TODAY_DATE, Constants.getToken(), ct);
                                List<String> shaList_1 = allList_1.get(0);
                                List<String> dateList_1 = allList_1.get(1);
                                List<String> messageList_1 = allList_1.get(2);
                                if (shaList_1.size() > 0) {
                                    jsonString = Call_URL.callURL("https://api.github.com/repos/" + full_name + "/contents?access_token=" + Constants.getToken()[ct++]);
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
                                                    if (name.contains("travis.yml")) {
                                                    //if (ct == Constants.getToken().length) {
                                                        //    ct = 0;
                                                        //}
                                                        //String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                                                        //String fullPath = DownloadPath + full_name.split("/")[0] + "_" + name;
                                                        //CreateFiles.save(fullPath, contents);

                                                    }
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

                                        dependencieString = "";
                                        types = "";
                                        typeSet = new HashSet<>();
                                        typeSet.addAll(buildType);
                                        for (int i = 0; i < Dependencies.size(); i++) {
                                            dependencieString = dependencieString.concat(Dependencies.get(i) + ", ");
                                        }
                                        BList = new ArrayList<>();
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
                                            shaaDetails = shaaDetails.concat(shaList_1.get(i)+"/");
                                        }
                                        datas = new Object[]{"", full_name, created_at, "", size, language, Dependencies.size(), dependencieString, types, description,shaList_1.size(),shaaDetails};
                                        allobj.add(datas);
                                        System.out.println("       ------ " + index + " : " + full_name);
                                        Create_Excel.createExcel2(allobj, 0, path + "remainofremain1.xlsx", projectL.get(aa).split("/")[0] + "_" + count_index);
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
        }

    }
}
