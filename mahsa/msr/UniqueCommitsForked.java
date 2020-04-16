/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Create_Excel;
import core.DateOperations;
import core.File_Details;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import picks.Pick_GeneralNext;
import picks.Pick_GeneralNumeric;
import reads.Collect_Forks;
import reads.Commits;
import reads.Creation_Date;
import util.Constants;

/**
 *
 * @author mahsa
 */
public class UniqueCommitsForked {
    
    public static void main(String[] args) throws Exception {
        indentify();
    }

    /**
     *
     * @throws Exception
     */
    private static void indentify() throws Exception {
        ////String toDay = "2017-07-06T00:00:00Z";
        Object[] datas = null;
        String[] tokens = Constants.getToken();
        //String file1 = "ReposFork_TravisBuild.xlsx";
        //String file1 = "ReposFork_TravisBuild2.xlsx";
        String file1 = "ReposFork_TravisBuild6.xlsx";
        //String file1 = "ReposFork_TravisBuild4.xlsx";
        
        String path = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/ReposFork_TravisBuild/1/";
        String path_new = "C:/Users/mahsa/Documents/GitHub/00Release-Class/mahsa/ReposFork_TravisBuild/1/uniqueForks/";
        String[] FILES = {file1};
        int ct = 0;
        int sheet_count = 0;
        for (int a = 0; a < FILES.length; a++) {
            int numbers = File_Details.getWorksheets(path + FILES[a]);
            int count = 0;
            while (count < numbers) {
                
                int flag = 0;
                String project = File_Details.setProjectName(path + FILES[a], count, "A2");
                String mCreation = File_Details.setProjectName(path + FILES[a], count, "C2");
                List<String> nameList = Pick_GeneralNext.pick(path + FILES[a], count, 1, 2);
                List<String> createList = Pick_GeneralNext.pick(path + FILES[a], count, 2, 2);
                String min_date = DateOperations.sorts(createList, createList).split("/")[0];
                
                List<List<List<String>>> lists = new ArrayList<>();
                
                System.out.println(count + " \t" + project);

                // System.out.println(count+" : "+min_date);
                List<List<String>> allList_1 = Commits.count(project, "mlp", min_date, Constants.cons.TODAY_DATE, tokens, ct);
                List<String> shaList_1 = allList_1.get(0);
                List<String> dateList_1 = allList_1.get(1);
                List<String> messageList_1 = allList_1.get(2);
                
                String pFirst = "date", pLast = "date";
                String dateOBJ = Creation_Date.creation(project, tokens, ct);
                String created = dateOBJ.split("/")[0];
                ct = Integer.parseInt(dateOBJ.split("/")[1]);
                
                if (dateList_1.size() > 0) {
                    pLast = dateList_1.get(0);
                    pFirst = dateList_1.get(dateList_1.size() - 1);
                }
                
                System.out.println(count + " :ML " + shaList_1.size());
                String shaa_mlp = "";
                for (int i = 0; i < shaList_1.size(); i++) {
                    shaa_mlp = shaa_mlp.concat(shaList_1.get(i) + "/");
                }
                
                ArrayList< Object[]> allobj = new ArrayList<Object[]>();
                datas = new Object[]{"MLP", "FP", "Created_at", "FirstCom", "LastCom", "COM", "UNIQUE", "Active_days", "Shaas"
                };// end of assigning the header to the object..
                double mdays = 0;
                try {
                    mdays = Double.parseDouble(DateOperations.diff(mCreation, dateList_1.get(0)).split("/")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                allobj.add(datas);
                datas = new Object[]{project, "", created, pFirst, pLast, Double.parseDouble(shaList_1.size() + ""), "", mdays, shaa_mlp};
                allobj.add(datas);
                
                List< ArrayList< Object[]>> obj_list = new ArrayList<>();
                for (int b = 0; b < nameList.size(); b++) {
                    List<List<String>> allList = Commits.count(nameList.get(b), "fp", createList.get(b), "", tokens, ct);
                    List<String> modelL = allList.get(allList.size() - 1);
                    ct = Integer.parseInt(modelL.get(modelL.size() - 1));
                    
                    List<List<String>> allList_3 = allList;
                    List<String> shaList_3 = allList_3.get(0);
                    List<String> dateList_3 = allList_3.get(1);
                    List<String> messageList_3 = allList_3.get(2);
                    
                    String fDate = "", lDate = "";
                    if (dateList_3.size() > 0) {
                        fDate = dateList_3.get(dateList_3.size() - 1);
                        lDate = dateList_3.get(0);
                        
                    }
                    
                    System.out.println("         " + b + "  : " + nameList.get(b) + "\t" + shaList_3.size());
                    
                    double total_unique = 0, total_vip = 0, total_scattered = 0, total_pervasive = 0, num_times = 0, total_main = 0;
                    String sha_collections = "";
                    Set<Integer> sha_unique = new LinkedHashSet<Integer>();
                    for (int c = 0; c < shaList_3.size(); c++) {
                        int c_shas = 0;
                        int fp = 0, mlp = 0;
                        
                        if (messageList_1.contains(messageList_3.get(c))) {
                            c_shas++;
                            mlp++;
                        }
                        String cat_ = "";
                        if (c_shas == 0) {
                            total_unique++;
                            cat_ = "Unique";
                            sha_collections = sha_collections.concat(shaList_3.get(c) + "/");
                        }
                        //num_times += sha_unique.size();

                    }
                    
                    if (total_unique > 0) {
                        flag++;
                        
                        double fdays = 0;
                        try {
                            fdays = Double.parseDouble(DateOperations.diff(createList.get(b), dateList_3.get(0)).split("/")[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        datas = new Object[]{"", nameList.get(b), createList.get(b), fDate, lDate, Double.parseDouble(shaList_3.size() + ""), total_unique,fdays, sha_collections};
                        allobj.add(datas);
                        
                       
                        String f_name = "repos_forks_unique_new_06.xlsx";
                        Create_Excel.createExcel2(allobj, 0, path_new + f_name, project.split("/")[0] + "_" + sheet_count);
                        
                    }
                    
                }
                //if (flag > 0) {
                sheet_count++;
                // }

                count++;
            }
        }
    }
}
