/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import java.io.File;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * FileHandlerWrite CLASS USE FOR CREATE FILE
 */
public class FileHandlerWrite {

    final static Logger logger = Logger.getLogger(FileHandlerWrite.class);

    public static void createFiles(String destination, String filePath) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] directory = filePath.split(pattern);
        for (int dirLevel = 2; dirLevel < (directory.length - 1); dirLevel++) {
            destination += "/" + directory[dirLevel];
        }

        File file = new File(destination + "/" + filePath);
        if (!file.exists()) {
            if (file.mkdir()) {
                // System.out.println("Directory is created!");
            } else {
                //  System.out.println("Failed to create directory!");
            }
        }

    }

}
