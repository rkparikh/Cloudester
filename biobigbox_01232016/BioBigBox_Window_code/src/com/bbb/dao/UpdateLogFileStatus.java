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
 * @author Chandrabhan
 */
public class UpdateLogFileStatus {

    final static Logger logger = Logger.getLogger(UpdateLogFileStatus.class);

    /**
     * updateLogFileStatus() METHOD USE FOR UPDATE
     * @param fileToUpdate 
     */
    public static boolean updateLogFileStatus(String fileToUpdate) {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE EMAIL SET LOGFILE_SENT_STATUS=1 WHERE LOGFILE_SENT_STATUS=0 AND LOG_FILE_NAME='"+fileToUpdate+"'";
            Connection conn = Initialize.getConn();
            logger.info("Update LOGFILE SENT STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.executeUpdate();
            statu = true;
            logger.info("UPDATE EMAIL SET LOGFILE_SENT_STATUS=1 WHERE LOGFILE_SENT_STATUS=0");

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Update LOG FILE SENT STATUS  Error " + sqlExcept);
        }
        return statu;
    }

}
