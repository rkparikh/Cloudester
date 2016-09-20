/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author Chandrabhan
 */
public class SelectLogFileName {

    final static Logger logger = Logger.getLogger(SelectLogFileName.class);

    /**
     * getLogFilePath() METHOD USE FOR GET FILE OLD ID*
     */
    public static String getLogFilePath() {
        String logFileName = null;
        boolean empty = true;
        try {
            logger.info("Select Log File   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT LOG_FILE_NAME FROM EMAIL WHERE LOGFILE_SENT_STATUS=0 ORDER BY CAST(DATE AS DATE) ASC";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logFileName = rs.getString("LOG_FILE_NAME");
                File f=new File(logFileName);
				if (f.exists()) {
					empty = false;
					break;
				}
            }
            if (empty) {
                return "" + empty;
            }

        } catch (Exception sqlExcept) {
            logger.error("Error in isFileOld " + sqlExcept);

        }
        return logFileName;
    }

}
