/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.bean.LoginBean;
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
 * @author Totaram
 */
/**
 * InsertLogin CLASS USE FOR INSERT LOGIN INFORMATIONS IN DATA BASE
 */
public class InsertLogin {

    final static Logger logger = Logger.getLogger(InsertLogin.class);

    /**
     * insertLogin(String userName, String password, String token) METHOD USE
     * FOR INSERT LOGIN INFORATION *
     */
    public static void insertLogin(String userName, String password, String token) {
        try {
            Date currDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Connection conn = Initialize.getConn();
            logger.info("Insert Login Details   : ");
            String Insert_Query = "Insert into Login(USERNAME,PASSWORD,TOKEN,DATE) values(?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(Insert_Query, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, userName);
            st.setString(2, password);
            st.setString(3, token);
            st.setString(4, dateFormat.format(currDate));
            st.executeUpdate();
            ResultSet keyResultSet = st.getGeneratedKeys();
            int newLoginID = 0;
            if (keyResultSet.next()) {
                Initialize.setUsername(userName);
                newLoginID = (int) keyResultSet.getInt(1);
                Initialize.setLogin_id(newLoginID);
            }
        } catch (SQLException sqlExcept) {
            logger.error("Insert Login Detail Error " + sqlExcept);
        }
    }

    /**
     * insertLoginWithDirectory(LoginBean loginBean) METHOD USE FOR INSERT LOGIN
     * INFORATION WITH DEFAULT DIRECTORY PATH *
     */
    public static void insertLoginWithDirectory(LoginBean loginBean) {
        try {
            Initialize.setUsername(loginBean.getUsername());
            Date currDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Connection conn = Initialize.getConn();
            logger.info("Insert Login Details   : ");
            String Insert_Query = "Insert into Login(USERNAME,PASSWORD,TOKEN,DATE,DEFAULT_DIR ,ACTIVE_STATUS) values(?,?,?,?,?,1)";
            PreparedStatement st = conn.prepareStatement(Insert_Query, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, loginBean.getUsername());
            st.setString(2, loginBean.getPassword());
            st.setString(3, loginBean.getToken());
            st.setString(4, dateFormat.format(currDate));
            st.setString(5, loginBean.getDirectorypath());
            st.executeUpdate();
            ResultSet keyResultSet = st.getGeneratedKeys();
            int newLoginID = 0;
            if (keyResultSet.next()) {
                newLoginID = (int) keyResultSet.getInt(1);
                Initialize.setLogin_id(newLoginID);
            }
        } catch (SQLException sqlExcept) {
            logger.error("Insert Login Detail Error " + sqlExcept);
        }
    }

}
