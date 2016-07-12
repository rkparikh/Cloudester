/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.bean.FileBean;
import com.bbb.init.Initialize;
import com.bbb.ui.ProgressBarDia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * SelectFilePath CLASS USE FOR SELECT FILE TABLE INFORMATION
 */
public class SelectFilePath {

    final static Logger logger = Logger.getLogger(SelectFilePath.class);

    /**
     * isFileInDatabase(String file_fuulpath) METHOD USE FOR CHECK FILE IN
     * DATABASE
     */
    public static boolean isFileInDatabase(String file_fuulpath) {
        boolean status = false;
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE STATUS=1 AND FULL_FILE_PATH_LOCAL=? ";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ps.setString(1, file_fuulpath);
            // ps.setInt(2, Initialize.getLogin_id());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                status = true;
            }

        } catch (SQLException sqlExcept) {
            logger.error("Error in isFileInDatabase " + sqlExcept);
        }
        return status;
    }

    /**
     * getFileNotSYNCDatabase() METHOD USE FOR GET FILE IN DATABASE THOSE ARE
     * NOT UPLOAD
     */
    public static FileBean getFileNotSYNCDatabase() {
        FileBean fileBean = new FileBean();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            Statement stmt = conn.createStatement();
            stmt = conn.createStatement();
            //String Select_Query = "SELECT * FROM FILE WHERE STATUS=1 AND BBB_ID is null AND FULL_FILE_PATH_LOCAL like '" + Initialize.getDefaultDirectory() + "%'";
            String Select_Query = "SELECT * FROM FILE WHERE STATUS=1 AND FULL_FILE_PATH_LOCAL like '" + Initialize.getDefaultDirectory() + "%'";
            // System.out.println("File NotSync " + Select_Query);
            ResultSet results = stmt.executeQuery(Select_Query);
            while (results.next()) {
                ProgressBarDia.stop.setEnabled(true);
                Initialize.Uptodatemsg = false;
                // ProgressBarDia.fileNameUrl.setForeground(Color.BLACK);
                fileBean.setId(results.getInt("FILE_ID"));
                fileBean.setBbb_id(results.getInt("BBB_ID"));
                fileBean.setFile_name(results.getString("FILE_NAME"));
                fileBean.setFolder_path(results.getString("FOLDER_PATH"));
                fileBean.setSize(results.getLong("SIZE"));
                fileBean.setOldSize(results.getLong("SIZE"));
                fileBean.setStatus(results.getInt("STATUS"));
                fileBean.setCreated(results.getLong("CREATED"));
                fileBean.setModified(results.getLong("MODIFIED"));
                fileBean.setFull_file_path(results.getString("FULL_FILE_PATH_LOCAL"));
            }
            if ((fileBean.getId() == null || fileBean.getId() == 0) && !ProgressBarDia.textareamQueue.getText().trim().equals("")) {
                ProgressBarDia.fileNameUrl.setText("Sync complete");
                int line = ProgressBarDia.textareamQueue.getLineCount();
                ProgressBarDia.fileCountLabel.setText((line - 1) + "/" + (line - 1));
                ProgressBarDia.stop.setEnabled(false);
                Initialize.Uptodatemsg = true;
                //System.out.println("in sync last"+WebServices.emailFileLogs.size()+"--"+WebServices.mail);
            } else if ((fileBean.getId() == null || fileBean.getId() == 0)) {
                ProgressBarDia.stop.setEnabled(false);
                Initialize.Uptodatemsg = true;
            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
//           if(sqlExcept.toString().contains("Serverjava.net.ConnectException")){
//               
//           }
            logger.error("Error in getFileNotSYNCDatabase " + sqlExcept);
        }

        return fileBean;
    }

    /**
     * getFileCount() METHOD USE FOR GET FILE COUNT THOSE ARE NOT UPLOAD*
     */
    public static int getFileCount() {
        int count = 0;
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            Statement stmt = conn.createStatement();
            stmt = conn.createStatement();
            String Select_Query = "SELECT count(*) FROM FILE WHERE STATUS=1 AND BBB_ID is null AND FULL_FILE_PATH_LOCAL like '" + Initialize.getDefaultDirectory() + "%'";
            ResultSet results = stmt.executeQuery(Select_Query);
            while (results.next()) {
                count = results.getInt(1);
            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFileNotSYNCDatabase " + sqlExcept);
        }

        return count;
    }

    /**
     * isFileOld(String fullpath) METHOD USE FOR GET FILE THOSE ARE OLD*
     */
    public static FileBean isFileOld(String fullpath) {
        FileBean fileBean = new FileBean();
        fileBean.setIsOld(false);
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE STATUS=2 AND FULL_FILE_PATH_LOCAL=? ";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ps.setString(1, fullpath);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fileBean.setIsOld(true);
                fileBean.setOldSize(rs.getLong("SIZE"));

            }

        } catch (SQLException sqlExcept) {
            logger.error("Error in isFileOld " + sqlExcept);

        }
        return fileBean;
    }

    /**
     * getFileOldID(String fullpath) METHOD USE FOR GET FILE OLD ID*
     */
    public static int getFileOldID(String fullpath) {
        int file_id = 0;
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT BBB_ID FROM FILE WHERE STATUS=2 AND FULL_FILE_PATH_LOCAL=? AND BBB_ID IS NOT NULL AND LOGIN_ID=" + Initialize.getLogin_id();
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ps.setString(1, fullpath);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                file_id = rs.getInt("BBB_ID");
            }

        } catch (SQLException sqlExcept) {
            logger.error("Error in isFileOld " + sqlExcept);

        }
        return file_id;
    }

    /**
     * getFileBeanByPath(String url) METHOD USE FOR GET FILEBEAN BY FILE PATH*
     */
    public static FileBean getFileBeanByPath(String url) {
        FileBean fileBean = new FileBean();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE  FULL_FILE_PATH_LOCAL=? AND LOGIN_ID=?";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ps.setString(1, url);
            ps.setInt(2, Initialize.getLogin_id());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fileBean.setId(rs.getInt("FILE_ID"));
                fileBean.setBbb_id(rs.getInt("BBB_ID"));
                fileBean.setFile_name(rs.getString("FILE_NAME"));
                fileBean.setFolder_path(rs.getString("FOLDER_PATH"));
                fileBean.setSize(rs.getLong("SIZE"));
                fileBean.setOldSize(rs.getLong("SIZE"));
                fileBean.setStatus(rs.getInt("STATUS"));
                fileBean.setCreated(rs.getLong("CREATED"));
                fileBean.setModified(rs.getLong("MODIFIED"));
                fileBean.setFull_file_path(rs.getString("FULL_FILE_PATH_LOCAL"));
            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFileBeanByPath " + sqlExcept);
        }

        return fileBean;
    }

    /**
     * getFileBeanByPath(String url) METHOD USE FOR GET FILEBEAN BY FILE PATH*
     */
    public static List getFailureFilesByName() {
        List list = new ArrayList();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE BBB_ID=5000000 and STATUS=2";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                list.add(rs.getString("FULL_FILE_PATH_LOCAL"));

            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFailureFilesByName " + sqlExcept);
        }

        return list;
    }
    /**
     * getFolderName(String url) METHOD USE FOR GET FILEBEAN BY FILE PATH*
     */
    public static List getFolderName() {
        List list = new ArrayList();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE  STATUS=2";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                list.add(rs.getString("FULL_FILE_PATH_LOCAL"));

            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFolderName " + sqlExcept);
        }

        return list;
    }
    /**
     * getFileBeanByPath(String url) METHOD USE FOR GET FILEBEAN BY FILE PATH*
     */
    public static String getParentFolderId() {
      String folder_id=null;
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT PARENT_FOLDER_ID FROM FILE WHERE FOLDER_SYNC_STATUS=1";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

              folder_id= rs.getString("PARENT_FOLDER_ID");

            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFileNotSYNCDatabase " + sqlExcept);
        }

        return folder_id;
    }
 /**
     * getFileBeanByPath(String url) METHOD USE FOR GET FILEBEAN BY FILE PATH*
     */
    public static List getFolderIdForSync() {
        List list = new ArrayList();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE BBB_ID=5000000 and STATUS=2";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                list.add(rs.getString("FULL_FILE_PATH_LOCAL"));

            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFileNotSYNCDatabase " + sqlExcept);
        }

        return list;
    }

    public static List<FileBean> getFolderBeanByPath(String url) {
        List<FileBean> fileBeanList = new ArrayList<FileBean>();
        try {
            logger.info("Select data   : ");
            Connection conn = Initialize.getConn();
            String Query_Select = "SELECT * FROM FILE WHERE  FOLDER_PATH=?";
            PreparedStatement ps = conn.prepareStatement(Query_Select);
            System.out.println("Url for delete folder:" + url);
            ps.setString(1, url + "/");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FileBean fileBean = new FileBean();
                fileBean.setFolder_id(rs.getString("FOLDER_ID"));
                fileBean.setId(rs.getInt("FILE_ID"));
                fileBean.setBbb_id(rs.getInt("BBB_ID"));
                fileBean.setFile_name(rs.getString("FILE_NAME"));
                fileBean.setFolder_path(rs.getString("FOLDER_PATH"));
                fileBean.setSize(rs.getLong("SIZE"));
                fileBean.setOldSize(rs.getLong("SIZE"));
                fileBean.setStatus(rs.getInt("STATUS"));
                fileBean.setCreated(rs.getLong("CREATED"));
                fileBean.setModified(rs.getLong("MODIFIED"));
                fileBean.setFull_file_path(rs.getString("FULL_FILE_PATH_LOCAL"));
                fileBeanList.add(fileBean);
            }

        } catch (Exception sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Error in getFileNotSYNCDatabase " + sqlExcept);
        }

        return fileBeanList;
    }
}
