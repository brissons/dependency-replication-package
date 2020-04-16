/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author mosesopenja
 */
public class CreateFiles {

    public static void save(String filedirectory, String filecontent) {
        try {
            File f = new File(filedirectory);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            //Remove if clause if you want to overwrite file
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                //dir will change directory and specifies file name for writer
                File dir = new File(f.getParentFile(), f.getName());
                PrintWriter writer = new PrintWriter(dir);
                writer.print(filecontent);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
