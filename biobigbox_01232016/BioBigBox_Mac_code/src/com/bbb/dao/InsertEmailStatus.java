/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Chandrabhan
 */
public class InsertEmailStatus {

    final static Logger logger = Logger.getLogger(InsertEmailStatus.class);

    /**
     * insertEmailStatus(String userName, Integer logFileStatus, String token, String logFileName) METHOD USE
     * FOR INSERT LOGIN INFORATION *
     */
    public static void insertEmailStatus(String userName, Integer logFileStatus, String token, String logFileName) {
        try {
            Date currDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
            Connection conn = Initialize.getConn();
            logger.info("Insert Login Details   : ");
            String Insert_Query = "Insert into EMAIL(USERNAME,TOKEN,DATE,LOGFILE_SENT_STATUS,LOG_FILE_NAME) values(?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(Insert_Query, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, userName);
            st.setString(2, token);
            st.setString(3, dateFormat.format(currDate));
            st.setInt(4, logFileStatus);
            st.setString(5, logFileName);
            st.executeUpdate();
//            ResultSet keyResultSet = st.getGeneratedKeys();
//            int newLoginID = 0;
//            if (keyResultSet.next()) {
//                Initialize.setUsername(userName);
//                newLoginID = (int) keyResultSet.getInt(1);
//                Initialize.setLogin_id(newLoginID);
//            }
        } catch (SQLException sqlExcept) {
            logger.error("Insert Email Status Error " + sqlExcept);
        }
    }

}
