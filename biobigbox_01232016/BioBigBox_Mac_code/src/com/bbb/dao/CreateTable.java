/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * CreateTable CLASS USE FOR CREATE TABLES
 */
public class CreateTable {

    final static Logger logger = Logger.getLogger(CreateTable.class);
    private static String createTableFile = "CREATE TABLE FILE (\n"
            + "        FILE_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
            + "        BBB_ID INTEGER ,\n"
            + "        FILE_NAME VARCHAR (200) NOT NULL,\n"
            + "        SIZE BIGINT NOT NULL,\n"
            + "        CREATED BIGINT NOT NULL,\n"
            + "        MODIFIED BIGINT NOT NULL,\n"
            + "        VERSION INTEGER,\n"
            + "        FOLDER_ID VARCHAR (50),\n"
            + "        PARENT_FOLDER_ID VARCHAR (50),\n"
            + "        PATH VARCHAR (1000),\n"
            + "        FOLDER_PATH VARCHAR (1000),\n"
            + "        S3_PATH VARCHAR (1000),\n"
            + "        FULL_FILE_PATH_LOCAL VARCHAR (2000),\n"
            + "        STATUS INTEGER,\n"
            + "        FOLDER_SYNC_STATUS INTEGER,\n"
            + "        LOGIN_ID INTEGER ,\n"
            + "        PRIMARY KEY (FILE_ID))";
    private static String createTableLogin = "CREATE TABLE LOGIN (ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            + "USERNAME VARCHAR (50) NOT NULL,"
            + "PASSWORD VARCHAR (50) NOT NULL,"
            + "TOKEN  VARCHAR (200) NOT NULL,"
            + "DATE  VARCHAR (20) NOT NULL,"
            + "DEFAULT_DIR  VARCHAR (1000) ,"
            + "ACTIVE_STATUS INTEGER ," /*0 for sign out 1 for sign in */
            + "PRIMARY KEY (ID))";
    private static String createTableEmail = "CREATE TABLE EMAIL (ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            + "USERNAME VARCHAR (50) NOT NULL,"
            + "TOKEN  VARCHAR (200) NOT NULL,"
            + "DATE  VARCHAR (20) NOT NULL,"
            + "LOGFILE_SENT_STATUS INTEGER ," /*0 for not sent email and  1 for sent email  */
            + "LOG_FILE_NAME VARCHAR (500) NOT NULL ,"
            + "PRIMARY KEY (ID))";

    /**
     * createTableLogin() METHOD USE FOR CREATE TABLE LOGIN *
     */
    public void createTableLogin() {
        Connection connection = null;
        try {
            connection = Initialize.getConn();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableLogin);
            logger.info(createTableLogin);
        } catch (SQLException e) {
            logger.info("Table already exists.  No need to recreate Login");

        }
    }

    /**
     * createTableFile() METHOD USE FOR CREATE TABLE FILE *
     */
    public void createTableFile() {
        Connection connection = null;
        try {
            connection = Initialize.getConn();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableFile);
            logger.info(createTableFile);
        } catch (SQLException e) {
            logger.info("Table already exists.  No need to recreate File" + e);

        }
    }

    /**
     * createTableEmail() METHOD USE FOR CREATE TABLE FILE *
     */
    public void createTableEmail() {
        Connection connection = null;
        try {
            connection = Initialize.getConn();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableEmail);
            logger.info(createTableEmail);
        } catch (SQLException e) {
            logger.info("Table already exists.  No need to recreate File" + e);

        }
    }

}
