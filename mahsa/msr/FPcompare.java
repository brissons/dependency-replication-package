/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Call_URL;
import core.Create_Excel;
import core.DateOperations;
import core.File_Details;
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
import util.Constants;

/**
 *
 * @author mahsa
 */
public class FPcompare {

    public static void main(String[] args) throws Exception {
        getDatas();
    }

    public static void getDatas() throws ParseException, Exception {
        String file1 = "ReposFork_TravisBuild.xlsx";
        String file2 = "ReposFork_TravisBuild2.xlsx";
        String file3 = "ReposFork_TravisBuild3.xlsx";
        String file4 = "ReposFork_TravisBuild4.xlsx";
        String path = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/";
        String DownloadPath = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/downloads002/";

        List<String> ProjList = new ArrayList<>();
        ProjList.add("apache/dubbo");
        ProjList.add("iluwatar/java-design-patterns");
        ProjList.add("zxing/zxing");
        ProjList.add("mybatis/mybatis-3");
        ProjList.add("google/guava");
        ProjList.add("square/okhttp");
        ProjList.add("checkstyle/checkstyle");
        ProjList.add("ReactiveX/RxJava");
        ProjList.add("chanjarster/weixin-java-tools");
        ProjList.add("crossoverJie/JCSprout");
        ProjList.add("square/retrofit");
        ProjList.add("libgdx/libgdx");
        ProjList.add("apache/storm");
        ProjList.add("square/dagger");
        ProjList.add("redisson/redisson");
        ProjList.add("apache/hive");
        ProjList.add("looly/hutool");
        ProjList.add("hs-web/hsweb-framework");
        ProjList.add("hibernate/hibernate-orm");
        ProjList.add("xkcoding/spring-boot-demo");
        ProjList.add("aws/aws-sdk-java");
        ProjList.add("apache/flink");
        ProjList.add("code4craft/webmagic");
        ProjList.add("spring-projects/spring-security");
        ProjList.add("alibaba/fastjson");
        ProjList.add("Activiti/Activiti");
        ProjList.add("dianping/cat");
        ProjList.add("apache/zookeeper");
        ProjList.add("alibaba/arthas");
        ProjList.add("google/gson");
        ProjList.add("xetorthio/jedis");
        ProjList.add("bitcoinj/bitcoinj");
        ProjList.add("openhab/openhab2-addons");
        ProjList.add("NLPchina/ansj_seg");
        ProjList.add("dropwizard/dropwizard");
        ProjList.add("android10/Android-CleanArchitecture");
        ProjList.add("prestodb/presto");
        ProjList.add("android10/Android-CleanArchitecture");
        ProjList.add("dropwizard/dropwizard");
        ProjList.add("openhab/openhab2-addons");
        ProjList.add("bitcoinj/bitcoinj");
        ProjList.add("apereo/cas");
        ProjList.add("junit-team/junit4");
        ProjList.add("alibaba/easyexcel");
        ProjList.add("halo-dev/halo");
        ProjList.add("skylot/jadx");
        ProjList.add("stanfordnlp/CoreNLP");
        ProjList.add("yasserg/crawler4j");
        ProjList.add("brettwooldridge/HikariCP");
        ProjList.add("apache/groovy");
        ProjList.add("se-edu/addressbook-level2");
        ProjList.add("joelittlejohn/jsonschema2pojo");
        ProjList.add("apache/flume");

        ProjList.add("naver/pinpoint");
        ProjList.add("alibaba/Sentinel");
        ProjList.add("codecentric/spring-boot-admin");
        ProjList.add("grpc/grpc-java");
        ProjList.add("apache/zeppelin");
        ProjList.add("TEAMMATES/teammates");
        ProjList.add("medcl/elasticsearch-analysis-ik");
        ProjList.add("orhanobut/logger");
        ProjList.add("se-edu/addressbook-level4");
        ProjList.add("cucumber/cucumber-jvm");
        ProjList.add("mockito/mockito");
        ProjList.add("jhy/jsoup");
        ProjList.add("dropwizard/metrics");
        ProjList.add("javaee-samples/javaee7-samples");
        ProjList.add("eclipse-vertx/vert.x");
        ProjList.add("MinecraftForge/MinecraftForge");
        ProjList.add("apache/shiro");
        ProjList.add("roughike/BottomBar");
        ProjList.add("perwendel/spark");
        ProjList.add("traccar/traccar");
        ProjList.add("NanoHttpd/nanohttpd");
        ProjList.add("AsyncHttpClient/async-http-client");
        ProjList.add("pxb1988/dex2jar");

       


        Object[] datas = null;
        String[] FILES = {file1, file2, file3, file4};
        int ct = 0;
        for (int a = 0; a < FILES.length; a++) {
            String file = FILES[a];
            int count = 0;
            int number = File_Details.getWorksheets(path + file);
            while (count < number) {
                String Project = File_Details.setProjectName(path + file, count, "A2");
                if (!ProjList.contains(Project)) {
                    List<String> projectL = Pick_GeneralNext.pick2(path + file, count, 1, 2);
                    List<Double> sizeL = Pick_GeneralNumeric.pick_3(path + file, count, 3, 2);
                    List<Double> DependT = Pick_GeneralNumeric.pick_3(path + file, count, 5, 2);
                    List<String> DependenciesL = Pick_GeneralNext.pick2(path + file, count, 6, 2);

                    ArrayList< Object[]> allobj = new ArrayList<Object[]>();
                    datas = new Object[]{"Repos_Name", "Forks", "Created_at", "Updated_At", "Pushed_At", "Size", "TotalDependencies", "Dependencies", "", "TotalDep_After", "Depend_After", "Modifications", "DateHash", "Flag_After", "CommitsAfter"};
                    allobj.add(datas);

                    System.out.println(a + " : --- " + count + " : " + Project);
                    datas = new Object[]{Project, projectL.size(), "", "", "", "", "", "", "", "", "", "", ""};
                    allobj.add(datas);
                    JSONParser parser = new JSONParser();
                    for (int aa = 0; aa < projectL.size(); aa++) {
                        if (ct == Constants.getToken().length) {
                            ct = 0;
                        }
                        String jsonString = Call_URL.callURL("https://api.github.com/repos/" + projectL.get(aa) + "?access_token=" + Constants.getToken()[ct++]);
                        if (!jsonString.equals("Error")) {
                            if (JSONUtils.isValidJSONObject(jsonString) == true) {
                                JSONObject jSONObject = (JSONObject) parser.parse(jsonString);
                                String created_at = (String) jSONObject.get("created_at");
                                String updated_at = (String) jSONObject.get("updated_at");
                                String pushed_at = (String) jSONObject.get("pushed_at");

                                List<String> dateL = new ArrayList<>();
                                dateL.add(updated_at);
                                dateL.add(pushed_at);
                                String maxDate = DateOperations.sorts(dateL, dateL).split("/")[1];

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
                                    jsonString = Call_URL.callURL("https://api.github.com/repos/" + projectL.get(aa) + "/commits?since=" + created_at + "&until=" + maxDate + "&page=" + p + "&per_page=100&access_token=" + Constants.getToken()[ct++]);

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
                                            List<List<String>> Lists = ShaaFiles.details(projectL.get(aa), shaa, DownloadPath, Constants.getToken(), ct);
                                            List<String> DependMLP = Lists.get(0);
                                            List<String> buildTMLP = Lists.get(1);
                                            if (DependMLP.size() > 0) {
                                                DependenciesMLP.addAll(DependMLP);
                                                buildTypeMLP.addAll(buildTMLP);
                                                dateList.add(shaa + "/" + dat);
                                            //flag = 1;
                                                //break;
                                            }
                                        }/// *** End of JSon Object.....  
                                    }
                                    if (flag == 1) {
                                        // break;
                                    }
                                    p++;//// Goto the next Page.......
                                } /// ******** End of while loop ......
                                String dependencieString = "";
                                String types = "";
                                Set<String> depSet = new HashSet<>();
                                depSet.addAll(DependenciesMLP);
                                List<String> depL = new ArrayList<>();
                                depL.addAll(depSet);
                                String dateString = "";
                                for (int i = 0; i < depL.size(); i++) {
                                    dependencieString = dependencieString.concat(depL.get(i) + ", ");
                                }
                                for (int i = 0; i < dateList.size(); i++) {
                                    dateString = dateString.concat(dateList.get(i) + ", ");
                                }
                                datas = new Object[]{"", projectL.get(aa), created_at, updated_at, pushed_at, sizeL.get(aa), DependT.get(aa), DependenciesL.get(aa), "", depL.size(), dependencieString, dateList.size(), dateString, flag, times};
                                allobj.add(datas);
                                if (aa % 100 == 0) {
                                    System.out.println(" ---- " + aa + " : " + projectL.get(aa));
                                    Create_Excel.createExcel2(allobj, 0, path + "ReposFork_BuildLast011.xlsx", Project.split("/")[0] + "_" + count);

                                }

                            }
                        }
                    }
                    Create_Excel.createExcel2(allobj, 0, path + "ReposFork_BuildLast011.xlsx", Project.split("/")[0] + "_" + count);
               
                    ProjList.add(Project);
                }
                count++;
            }
        }
    }
}
