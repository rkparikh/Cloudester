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
import java.sql.Statement;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * SelectLogin CLASS USE FOR SELECT LOGIN TABLE INFORMATION
 */
public class SelectLogin {

    final static Logger logger = Logger.getLogger(SelectLogin.class);

    /**
     * getLoginCridential() METHOD USE FOR GET LOGIN CRIDENTIAL 
     */
    public static TreeMap<String, String> getLoginCridential() {
        TreeMap<String, String> loginMap = new TreeMap<String, String>();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM  LOGIN  WHERE ID=(select MAX(ID) FROM LOGIN) AND ACTIVE_STATUS=1");
            while (results.next()) {
                String userName = results.getString(2);
                String password = results.getString(3);
                Initialize.setUsername(userName);
                Initialize.setLoginToken(results.getString(4));
                Initialize.setLogin_id(results.getInt("ID"));
                Initialize.setDefaultDirectory(results.getString("DEFAULT_DIR"));
                logger.info("Select File Details  User NAME:" + userName + " PASSWORD:" + password);
                loginMap.put(userName, password);
            }

        } catch (Exception ex) {
            logger.error("Error in getLoginCridential " + ex);
        }
        return loginMap;
    }

    /**
     * getLoginAlready(LoginBean loginBean) METHOD USE FOR GET LOGIN BEAN  
     */
    public static LoginBean getLoginAlready(LoginBean loginBean) {
        LoginBean newLoginBean = new LoginBean();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM  LOGIN  WHERE USERNAME=? AND PASSWORD=?";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ps.setString(1, loginBean.getUsername());
            ps.setString(2, loginBean.getPassword());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                newLoginBean.setDirectorypath(rs.getString("DEFAULT_DIR"));
                newLoginBean.setPassword(loginBean.getPassword());
                newLoginBean.setUsername(loginBean.getUsername());
            }

        } catch (SQLException sqlExcept) {
            logger.error("Error in isFileOld " + sqlExcept);

        }
        return newLoginBean;
    }
}
