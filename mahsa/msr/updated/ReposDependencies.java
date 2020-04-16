/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr.updated;

import com.opm.mahsa.msr.CreateFiles;
import com.opm.mahsa.msr.CreateFiles;
import com.opm.mahsa.msr.CreatedAtLOC;
import com.opm.mahsa.msr.CreatedAtLOC;
import com.opm.mahsa.msr.ShaaFiles;
import com.opm.mahsa.msr.ShaaFiles;
import core.Call_URL;
import core.Create_Excel;
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
 * @author mahsa
 */
public class ReposDependencies {

    public static void main(String[] args) throws Exception {
        getDatas();
    }

    public static void getDatas() throws ParseException, Exception {

        String file = "final_dataset_removed_deleted.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/00newRepo/";
        String DownloadPath = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/downloads2/";
        ArrayList<String> forkDetails = new ArrayList<String>();
        //int index = 0;
        JSONParser parser = new JSONParser();
        List<Double> repo_idL = Pick_GeneralNumeric.pick_3(path + file, 0, 0, 1);
        List<String> repo_nameL = Pick_GeneralNext.pick2(path + file, 0, 1, 1);
        List<String> owner_nameL = Pick_GeneralNext.pick2(path + file, 0, 3, 1);
        List<Double> owner_idL = Pick_GeneralNumeric.pick_3(path + file, 0, 2, 1);
        Object[] datas = null;
        ArrayList< Object[]> allobj = new ArrayList<Object[]>();
        datas = new Object[]{"Repos_id", "Reos_name", "Owner_id", "Owner_name", "Created_at", "Commits", "LOC", "TotalDependencies", "Dependencies", "", "CommitsAfter", "DependAfter", "DependAfter_Details"};
        allobj.add(datas);

        System.out.println(owner_nameL.size()+", "+repo_nameL.size());
        //4451
        int ct = 0;
        for (int a = 1353; a < repo_idL.size(); a++) {
            
            String projectName = owner_nameL.get(a) + "/" + repo_nameL.get(a);
            String createdAt = CreatedAtLOC.creation(projectName, Constants.getToken(), ct);
            System.out.println(a+" : "+projectName);
            if (createdAt != null) {
                String[] splits = createdAt.split("/");
                long size = Long.parseLong(splits[1]);
                ct = Integer.parseInt(splits[2]);
                List<String> Dependencies = new ArrayList<>();
                List<String> buildType = new ArrayList<>();
                ///Download the file content here
                if (ct == Constants.getToken().length) {
                    ct = 0;
                }

                String jsonString = Call_URL.callURL("https://api.github.com/repos/" + projectName + "/contents?access_token=" + Constants.getToken()[ct++]);
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
                                    String fullPath = DownloadPath + projectName.split("/")[0] + "_" + name;
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
                                    String fullPath = DownloadPath + projectName.split("/")[0] + "_" + a + "_" + name;
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

                    String dependencieString1 = "";
                    for (int i = 0; i < Dependencies.size(); i++) {
                        dependencieString1 = dependencieString1.concat(Dependencies.get(i) + ", ");
                    }

                    List<String> DependenciesMLP = new ArrayList<>();
                    List<String> buildTypeMLP = new ArrayList<>();
                    List<String> dateList = new ArrayList<>();
                    int flag = 0;

                    int times = 0;
                    int p = 1; // Page number parameter
                    while (true) {////loop thru the pagess....
                        if (ct >= (Constants.getToken().length)) {/// the the index for the tokens array...
                            ct = 0; //// go back to the first index......
                        }
                        jsonString = Call_URL.callURL("https://api.github.com/repos/" + projectName + "/commits?since=" + createdAt + "&page=" + p + "&per_page=100&access_token=" + Constants.getToken()[ct++]);
                        if (jsonString.equals("Error")) {
                            break;
                        }
                        if (JSONUtils.isValidJSON(jsonString) == true) {
                            JSONArray jsonArray = (JSONArray) parser.parse(jsonString);
                            if (jsonArray.toString().equals("[]")) {
                                /// Break out of the loop, when empty array is found!
                                break;
                            }
                            times += jsonArray.size();
                            System.out.println("          ---- " + jsonArray.size());
                            for (Object jsonObj : jsonArray) {
                                JSONObject jsonObject = (JSONObject) jsonObj;
                                String shaa = (String) jsonObject.get("sha");

                                JSONObject commitOBJ = (JSONObject) jsonObject.get("commit");
                                JSONObject commiterOBJ = (JSONObject) commitOBJ.get("committer");
                                String dat = (String) commiterOBJ.get("date");
                                List<List<String>> Lists = ShaaFiles.details(projectName, shaa, DownloadPath, Constants.getToken(), ct);
                                List<String> DependMLP = Lists.get(0);
                                List<String> buildTMLP = Lists.get(1);
                                if (DependMLP.size() > 0) {
                                    DependenciesMLP.addAll(DependMLP);
                                    buildTypeMLP.addAll(buildTMLP);
                                    dateList.add(shaa + "/" + dat);
                                    ct = Integer.parseInt(buildTMLP.get(buildTMLP.size()-1));
                                    //flag = 1;8
                                    //break;
                                }
                            }/// *** End of JSon Object.....  
                        }
                        if (flag == 1) {
                            // break;
                        }
                        p++;//// Goto the next Page.......
                    } /// ******** End of while loop ......
                    String dependencieStringAfter = "";
                    Set<String> depSet = new HashSet<>();
                    depSet.addAll(DependenciesMLP);
                    List<String> depL = new ArrayList<>();
                    depL.addAll(depSet);
                    String dateString = "";
                    for (int i = 0; i < depL.size(); i++) {
                        dependencieStringAfter = dependencieStringAfter.concat(depL.get(i) + ", ");
                    }

                    datas = new Object[]{repo_idL.get(a), repo_nameL.get(a), owner_idL.get(a), owner_nameL.get(a), createdAt, size, Dependencies.size(), dependencieString1, "", times, depL.size(), dependencieStringAfter};
                    allobj.add(datas);
                    //if (a % 100 == 0) {
                        System.out.println(" ---- " + a + " : " + projectName);
                        Create_Excel.createExcel2(allobj, 0, path + "ReposFork_Dependencies_version02.xlsx", "Repos");

                   // }
                }
            }

        }

    }
}
