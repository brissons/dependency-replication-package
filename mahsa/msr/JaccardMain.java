/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import core.Create_Excel;
import core.File_Details;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import picks.Pick_GeneralNext;
import picks.Pick_GeneralNumeric;

/**
 *
 * @author mosesopenja
 */
public class JaccardMain {

    public static void main(String[] args) throws Exception {
        getstats();
    }

    private static void getstats() throws Exception {
        String path = "C:/Users/mahsa/Desktop/Release Engineering/Indivi/";
        String[] files = {"ReposFork_TravisBuild.xlsx","ReposFork_TravisBuild2.xlsx","ReposFork_TravisBuild3.xlsx","ReposFork_TravisBuild4.xlsx"};
        int index = 0;
        for (int a = 0; a < files.length; a++) {
            int count = 0;
            int number = File_Details.getWorksheets(path + files[a]);
            while (count < number) {
                ArrayList< Object[]> allobj = new ArrayList<Object[]>();
                Object[] data = null;
                data = new Object[]{"Repos", "Forks", "created_at", "Size", "Languange", "totalDependencies", "Dependencies", "Jaccard_distance", "Jaccard_similarity"};
                allobj.add(data);
                String project = File_Details.setProjectName(path + files[a], count, "A2");
                String CreatedMLP = File_Details.setProjectName(path + files[a], count, "C2");
                String LanguangeMLP = File_Details.setProjectName(path + files[a], count, "E2");
                String DependencyMLP = File_Details.setProjectName(path + files[a], count, "G2").replaceAll(", ", " ");
                String typeMLP = File_Details.setProjectName(path + files[a], count, "H2");
                double sizeMLP = Double.parseDouble(File_Details.setProjectName(path + files[a], count, "D2"));
                double depMLP = Double.parseDouble(File_Details.setProjectName(path + files[a], count, "F2"));

                System.out.println(a+" : "+count+" : "+project);
                List<String> FP = Pick_GeneralNext.pick2(path + files[a], count, 1, 2);
                List<String> created_at = Pick_GeneralNext.pick2(path + files[a], count, 2, 2);
                List<String> Language = Pick_GeneralNext.pick2(path + files[a], count, 4, 2);
                List<Double> Size = Pick_GeneralNumeric.pick_3(path + files[a], count, 3, 2);
                List<Double> NumDep = Pick_GeneralNumeric.pick_3(path + files[a], count, 5, 2);
                List<String> DependencyL = Pick_GeneralNext.pick2(path + files[a], count, 6, 2);
                List<String> TypeL = Pick_GeneralNext.pick2(path + files[a], count, 1, 2);
                data = new Object[]{project, FP.size(), CreatedMLP, sizeMLP, LanguangeMLP, depMLP, DependencyMLP, "", ""};
                allobj.add(data);
                for (int b = 0; b < FP.size(); b++) {
                    double jaccardDistance = new org.apache.commons.text.similarity.JaccardDistance().apply(DependencyMLP, DependencyL.get(b).replaceAll(", ", " "));
                    double jaccardSimilarity = new org.apache.commons.text.similarity.JaccardSimilarity().apply(DependencyMLP, DependencyL.get(b).replaceAll(", ", " "));

                    data = new Object[]{"", FP.get(b), created_at.get(b), Size.get(b), Language.get(b), NumDep.get(b), DependencyL.get(b), jaccardDistance, jaccardSimilarity};
                    allobj.add(data);
                }
                
                Create_Excel.createExcel2(allobj, 0, path + "ReposJaccard"+a+".xlsx", project.split("/")[0]+"_"+count);
                count++;
                index++;
            }
        }

    }

}
