/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * UpdateLogin CLASS USE FOR UPDATE LOGIN TABLE INFORMATION
 */
public class UpdateLogin {

    final static Logger logger = Logger.getLogger(UpdateLogin.class);

    /**
     * updateDirectoryPath() METHOD USE FOR UPDATE DIRECTORY PATH IN LOGIN TABLE 
     */
    public static void updateDirectoryPath() {
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE LOGIN SET DEFAULT_DIR=? ,ACTIVE_STATUS=1 WHERE ID=?";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setString(1, Initialize.getDefaultDirectory().replaceAll("'","''"));
            ps.setInt(2, Initialize.getLogin_id());
            ps.executeUpdate();

            logger.info("UPDATE LOGIN SET DEFAULT_DIR=?,WHERE ID=?");

        } catch (SQLException sqlExcept) {
            logger.error("Update  Login updateDirectoryPath Error " + sqlExcept);
        }
    }

    /**
     * logOutUser() METHOD USE FOR SET LOGOUT STATUS IN LOGIN TABLE 
     */
    public static void logOutUser() {
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE LOGIN SET ACTIVE_STATUS=0 WHERE ID=?";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setInt(1, Initialize.getLogin_id());
            ps.executeUpdate();
            logger.info("UPDATE LOGIN ACTIVE_STATUS=0 WHERE ID=?");

        } catch (SQLException sqlExcept) {
            logger.error("Update  Login logOutUser Error " + sqlExcept);
        }
    }

}
