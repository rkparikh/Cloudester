/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import com.bbb.init.Initialize;
import java.io.File;
import java.util.Stack;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * FileHandlerRead CLASS USE FOR WATCH FILE MOVE IN DEFAULT FOLDER
 */
public class FileHandlerRead {

    final static Logger logger = Logger.getLogger(FileHandlerRead.class);

    public static void printFiles(File dir) {
        Stack<File> stack = new Stack<File>();
        stack.push(dir);
        while (!stack.isEmpty()) {
            File child = stack.pop();
            if (child != null && child.isDirectory()) {
                try {
                    for (File f : child.listFiles()) {
                        stack.push(f);
                    }
                } catch (Exception e) {
                }
            } else if (child.isFile()) {
                Initialize.tempMap.put(child.getPath(), "ENTRY_MODIFY");
            }
        }
    }

}
