/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import com.opm.mahsa.msr.updated.ExtractFileContent;
import core.Call_URL;
import core.JSONUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import reads.ReadImportFile;
import util.Constants;

/**
 *
 * @author mahsa
 */
public class ShaaFiles {

    public static List<List<String>> details(String project, String shaa, String downloadPath, String[] tockens, int ct) throws ParseException {
        List<String> DependenciesMLP = new ArrayList<>();
        List<String> buildTypeMLP = new ArrayList<>();

        if (ct >= (tockens.length)) {/// the the index for the tokens array...
            ct = 0; //// go back to the first index......
        }
        String jsonString = Call_URL.callURL("https://api.github.com/repos/" + project + "/commits/" + shaa + "?access_token=" + tockens[ct++]);
        JSONParser parser = new JSONParser();
        if (JSONUtils.isValidJSONObject(jsonString) == true) {
            JSONObject jSONObject = (JSONObject) parser.parse(jsonString);
            JSONObject comOBJ = (JSONObject) jSONObject.get("commit");
            JSONObject authOBJ = (JSONObject) comOBJ.get("author");

            if ((JSONArray) jSONObject.get("files") != null) {
                JSONArray fileObj = (JSONArray) jSONObject.get("files");
                //System.out.println("      Files: "+fileObj.size());
                for (int i = 0; i < fileObj.size(); i++) {
                    JSONObject OBJ = (JSONObject) fileObj.get(i);
                    String name = (String) OBJ.get("filename");
                    String download_url = (String) OBJ.get("raw_url");
                    if (name.contains("travis.yml")) {
                        if (ct == Constants.getToken().length) {
                            ct = 0;
                        }
                        String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                        String fullPath = downloadPath + project.split("/")[0] + "_" + name;
                        CreateFiles.save(fullPath, contents);

                    }
                    if (name.contains("build.gradle")) {
                        if (ct == Constants.getToken().length) {
                            ct = 0;
                        }
                        String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                        String fullPath = downloadPath + project.split("/")[0] + "_" + name;
                        CreateFiles.save(fullPath, contents);

                        List<String> extracts = ExtractFileContent.readGradle(contents);
                        DependenciesMLP.addAll(extracts);
                        buildTypeMLP.add("gradle");

                        break;
                    }
                    if (name.contains("pom.xml")) {
                        if (ct == Constants.getToken().length) {
                            ct = 0;
                        }
                        String contents = Call_URL.callURL(download_url + "?access_token=" + Constants.getToken()[ct++]);
                        String fullPath = downloadPath + project.split("/")[0] + "_" + name;
                        CreateFiles.save(fullPath, contents);

                        List<String> extracts = ExtractFileContent.dom(fullPath);
                        DependenciesMLP.addAll(extracts);
                        buildTypeMLP.add("maven");
                        break;
                    }

                }
            }
        }

        buildTypeMLP.add(ct + "");
        List<List<String>> Lists = new ArrayList<>();
        Lists.add(DependenciesMLP);
        Lists.add(buildTypeMLP);

        return Lists;
    }
}
