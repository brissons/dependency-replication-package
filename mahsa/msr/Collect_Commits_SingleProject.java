/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import static com.opm.mouna_research.csv.ReadFromCSV.listFilesForFolder;
import core.Call_URL;
import core.Create_Excel;
import core.DateOperations;
import core.JSONUtils;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import picks.Pick_GeneralNext;
import util.Constants;

/**
 *
 * @author mahsa
 */
public class Collect_Commits_SingleProject {

    public static void main(String[] args) throws Exception {
        read();
    }

    private static void read() throws Exception {
        String project = "kubernetes/kubernetes";
        String file = "PR-Details-kubernetes2.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/Data/";
        // String output = "Code-Smell-Details-statistics-fi.xlsx";
        List<String> PRList = Pick_GeneralNext.pick(path + file, 0, 24, 2);
        Object[] datas= null;
        ArrayList< Object[]> allobj = new ArrayList<Object[]>();
        datas = new Object[]{"Hash", "AuthorDate", "AuthorEmail", "AuthorName", "AuthorLogin", "CommiterDate", "CommiterEmail", "CommiterName", "CommiterLogin", "TotalComments", "Messages"};
        allobj.add(datas);
        int p =1;
        int ct = 0;
        int times = 0;
        while (true) {////loop thru the pagess....
            if (ct == (Constants.getToken().length)) {/// the the index for the tokens array...
                ct = 0; //// go back to the first index......
            }
            String jsonString = Call_URL.callURL("https://api.github.com/repos/" + project + "/commits?until=" + Constants.cons.RELEASE_TODAY_UNTIL + "&page=" + p + "&per_page=100&access_token=" + Constants.getToken()[ct++]);
            if (jsonString.equals("Error")) {
                break;
            }
            JSONParser parser = new JSONParser();
            if (JSONUtils.isValidJSON(jsonString) == true) {
                JSONArray jsonArray = (JSONArray) parser.parse(jsonString);
                //System.out.println( "    "+p+" \t"+jsonArray.size());
                times += jsonArray.size();
                if (times % 500 == 0) {
                    System.out.println("     " + times);
                }
                if (jsonArray.toString().equals("[]")) {
                    /// Break out of the loop, when empty array is found!
                    break;
                }
                for (Object jsonObj : jsonArray) {
                    JSONObject jsonObject = (JSONObject) jsonObj;
                    String shaa = (String) jsonObject.get("sha");
                    String authorName = "", authorDate = "", authorEmail = "", authorLogin = "";
                    String commiterName = "", commiterDate = "", commiterEmail = "", commiterLogin = "";
                    String message = "";

                    long comment_count = 0;
                    if ((JSONObject) jsonObject.get("commit") != null) {
                        JSONObject commitsObj = (JSONObject) jsonObject.get("commit");
                        comment_count = (long) commitsObj.get("comment_count");
                        if ((JSONObject) commitsObj.get("author") != null) {
                            JSONObject author_Obj = (JSONObject) commitsObj.get("author");
                            authorName = (String) author_Obj.get("name");
                            authorDate = (String) author_Obj.get("date");
                            authorEmail = (String) author_Obj.get("email");
                        }
                        if ((JSONObject) commitsObj.get("committer") != null) {
                            JSONObject commiterObj = (JSONObject) commitsObj.get("committer");
                            commiterDate = (String) commiterObj.get("date");
                            commiterName = (String) commiterObj.get("name");
                            commiterEmail = (String) commiterObj.get("email");
                        }
                        if ((String) commitsObj.get("message") != null) {
                            message = (String) commitsObj.get("message");
                        }
                    }
                    if ((JSONObject) jsonObject.get("author") != null) {
                        JSONObject author_Obj = (JSONObject) jsonObject.get("author");
                        authorLogin = (String) author_Obj.get("login");
                    }
                    if ((JSONObject) jsonObject.get("committer") != null) {
                        JSONObject author_Obj = (JSONObject) jsonObject.get("committer");
                        commiterLogin = (String) author_Obj.get("login");
                    }

                    datas = new Object[]{shaa, authorDate, authorEmail, authorName, authorLogin, commiterDate, commiterEmail, commiterName, commiterLogin, comment_count, message};
                    allobj.add(datas);

                }
            }
            p++;

            //Write to excel here..!
            Create_Excel.createExcel2(allobj, 0, path + "kubernetes-commits-upto-05-10-2019.xlsx", "commits");
        }
    }
}
