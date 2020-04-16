/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr.updated;

import core.Create_Excel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import picks.Pick_GeneralNext;
import picks.Pick_GeneralNumeric;

/**
 *
 * @author mahsa
 */
public class Jaccard {

    public static void main(String[] args) throws Exception {
        jaccard();
    }

    private static void jaccard() throws Exception {
        String file = "cleaned_total.xlsx";
        String path = "C:/Users/mahsa/Desktop/Variability/Clean/";
        List<Double> Repos_id = Pick_GeneralNumeric.pick_3(path + file, 0, 0, 1);
        List<String> repo_nameL = Pick_GeneralNext.pick2(path + file, 0, 1, 1);
        List<String> owner_nameL = Pick_GeneralNext.pick2(path + file, 0, 3, 1);
        //List<Double> owner_idL = Pick_GeneralNumeric.pick_3(path + file, 0, 2, 1);
        //List<Double> ClosedPR = Pick_GeneralNumeric.pick_3(path + file, 0, 4, 1);
        //List<Double> MergedPReq = Pick_GeneralNumeric.pick_3(path + file, 0, 5, 1);
        List<String> full_nameL = Pick_GeneralNext.pick2(path + file, 0, 4, 1);
        List<Double> depth = Pick_GeneralNumeric.pick_3(path + file, 0, 2, 1);
        List<Double> Commits = Pick_GeneralNumeric.pick_3(path + file, 0, 6, 1);
        List<Double> TotalDependencies = Pick_GeneralNumeric.pick_3(path + file, 0, 7, 1);
        List<Double> CommitsAfter = Pick_GeneralNumeric.pick_3(path + file, 0, 10, 1);
        List<Double> DependAfter = Pick_GeneralNumeric.pick_3(path + file, 0, 11, 1);
        List<String> Created_at = Pick_GeneralNext.pick2(path + file, 0, 5, 1);
        List<String> Dependencies = Pick_GeneralNext.pick2(path + file, 0,8, 1);
        List<String> DependAfter_Details = Pick_GeneralNext.pick2(path + file, 0, 12, 1);


        System.out.println(Repos_id.size()+","+repo_nameL.size()+", "+owner_nameL.size()+", "+full_nameL.size()+", "+depth.size()+", "+Commits.size()+","
        +TotalDependencies.size()+", "+CommitsAfter.size()+", "+DependAfter.size()+", "+Created_at.size()+", "+DependAfter_Details.size());
        List<Double> list_id = new ArrayList<>();
        List<String> list_set = new ArrayList<>();
        //list_set.addAll(set_set);

        Object[] datas = null;

        ArrayList< Object[]> allobj = new ArrayList<Object[]>();
        datas = new Object[]{"Repos_id", "Reos_name", "fullname", "Owner_name", "Created_at", "Depth", "Commits", "TotalDependencies", "Dependencies", "", "CommitsAfter", "DependAfter", "DependAfter_Details", "JaccardDistance", "JaccardSimmilarity"};
        allobj.add(datas);

        for (int i = 0; i < depth.size(); i++) {
            if (depth.get(i).intValue() == 0) {
                list_id.add(Repos_id.get(i));
                list_set.add(repo_nameL.get(i));
            }
        }
        
        System.out.println(list_id.size());

        for (int i = 0; i < list_set.size(); i++) {
            int index = Repos_id.indexOf(list_id.get(i));
            String MLP = "";
            if (DependAfter.get(index) > 0) {
                MLP = DependAfter_Details.get(index).replaceAll(", ", " ");
            } else {
                MLP = Dependencies.get(index).replaceAll(", ", " ");
            }

            for (int j = 0; j < repo_nameL.size(); j++) {
                if (list_set.get(i).equals(repo_nameL.get(j))) {
                    String FP = "";
                    if (DependAfter.get(j) > 0) {
                        FP = DependAfter_Details.get(j).replaceAll(", ", " ");
                    } else {
                        FP = Dependencies.get(j).replaceAll(", ", " ");
                    }
                    double jaccardDistance = new org.apache.commons.text.similarity.JaccardDistance().apply(MLP, FP);
                    double jaccardSimilarity = new org.apache.commons.text.similarity.JaccardSimilarity().apply(MLP, FP);

                    datas = new Object[]{Repos_id.get(j), repo_nameL.get(j), full_nameL.get(j), owner_nameL.get(j), Created_at.get(j), depth.get(j),  Commits.get(j), TotalDependencies.get(j), Dependencies.get(j), "", CommitsAfter.get(j), DependAfter.get(j), DependAfter_Details.get(j), jaccardDistance, jaccardSimilarity};
                    allobj.add(datas);

                }
            }

            Create_Excel.createExcel2(allobj, 0, path + "ReposFork_Dependencies_version_jaccard_003.xlsx", "jaccard");

        }

    }
}
