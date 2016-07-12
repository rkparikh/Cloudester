/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * MyDerbyConnection CLASS USE FOR GET CONNECTION FROM DERBY DATA BASE CREATED
 * IN USER DIRECTORY
 */
public class MyDerbyConnection {

    final static Logger logger = Logger.getLogger(MyDerbyConnection.class);

    /**
     * getMyConnection() METHOD USE FOR GET CONNECTION
     */
    public void getMyConnection() {
        Connection conn = null;
        String dbURL = "jdbc:derby:" + System.getProperty("user.home") + File.separator + "bbbDB;create=true;user=tr; password=rathore";
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(dbURL);
            logger.info("Connection Successfully Created");
            Initialize.setConn(conn);
            CreateTable createTable = new CreateTable();
            createTable.createTableLogin();
            createTable.createTableFile();
            createTable.createTableEmail();
           // conn.close();
         
        } catch (ClassNotFoundException | SQLException except) {
            logger.error("Error in create Connection" + except);
               System.gc();
        }
       
        

    }

}
