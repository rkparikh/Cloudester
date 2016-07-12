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
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * UpdateFilePath CLASS USE FOR UPDATE FILE TABLE INFORMATION
 */
public class UpdateFilePath {

    final static Logger logger = Logger.getLogger(UpdateFilePath.class);

    /**
     * updateFileStatus(FileBean fileBean) METHOD USE FOR UPDATE FILE STATUS
     */
    public static boolean updateFileStatus(FileBean fileBean) {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET BBB_ID=?, FOLDER_ID=?, STATUS=2 WHERE FILE_ID=?";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setInt(1, fileBean.getBbb_id());
            ps.setString(2, fileBean.getFolder_id());
            ps.setInt(3, fileBean.getId());
            ps.executeUpdate();
            statu = true;
            logger.info("UPDATE FILE SET BBB_ID=?,STATUS=2 WHERE FILE_ID=?'");

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Update  File Status Error " + sqlExcept);
        }
        return statu;
    }
    

    /**
     * deleteFileStatus(FileBean fileBean) METHOD USE FOR SET STATUS FILE DELETE
     */
    public static boolean deleteFileStatus(FileBean fileBean) {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET STATUS=0 WHERE FILE_ID=?";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setInt(1, fileBean.getId());
            ps.executeUpdate();
            statu = true;
            logger.info("UPDATE FILE SET BBB_ID=?,STATUS=0 WHERE FILE_ID=?'");

        } catch (SQLException sqlExcept) {
            logger.error("Update  File Status Error " + sqlExcept);
        }
        return statu;
    }

    /**
     * deleteFolderStatus(FileBean fileBean) METHOD USE FOR SET STATUS FOR
     * DELETED FOLDERS
     */
    public static boolean deleteFolderStatus(FileBean fileBean) {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET STATUS=0 WHERE FOLDER_ID=?";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setString(1, fileBean.getFolder_id());
            ps.executeUpdate();
            statu = true;
            logger.info("UPDATE FILE SET BBB_ID=?,STATUS=0 WHERE FILE_ID=?'");

        } catch (SQLException sqlExcept) {
            logger.error("Update  File Status Error " + sqlExcept);
        }
        return statu;
    }

    /**
     * noFileStatus(FileBean fileBean) METHOD USE FOR SET STATUS FILE NOT FOUND
     */
    public static boolean noFileStatus(FileBean fileBean) {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET STATUS=4 WHERE FILE_ID=?";
            // System.out.println("Update Query" + Update_Query + " File ID " + fileBean.getId());
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.setInt(1, fileBean.getId());
            ps.executeUpdate();
            statu = true;
            System.out.println("no File Status " + Update_Query);
            logger.info("UPDATE FILE SET BBB_ID=?,STATUS=4 WHERE FILE_ID=?'");

        } catch (SQLException sqlExcept) {
            logger.error("Update  File Status Error " + sqlExcept);
        }
        return statu;
    }

    /**
     * updateFileStatus(FileBean fileBean) METHOD USE FOR UPDATE FILE STATUS
     */
    public static void updateFileStatus(String fileIds) {

        PreparedStatement ps = null;
        try {
              if(!fileIds.trim().equals("")){
            String Update_Query = "UPDATE FILE SET STATUS=1 WHERE BBB_ID in (" + fileIds + ") AND FULL_FILE_PATH_LOCAL like '" + Initialize.getDefaultDirectory() + "%'";
            System.out.println("deleted files query" + Update_Query);
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            int row = ps.executeUpdate();
            String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
            System.out.println("counter length :"+countLabel.length);
            if(countLabel.length==2){
            int count = Integer.parseInt(countLabel[1]);
            count = count + row;
            System.out.println("Count File " + count + " row update " + row);
            ProgressBarDia.fileCountLabel.setText(countLabel[0] + "/" + count);
            }
              }
        } catch (SQLException sqlExcept) {sqlExcept.printStackTrace();
            logger.error("Update  File Status Error " + sqlExcept);
        }

    }
     /**
     * updateFileStatus(FileBean fileBean) METHOD USE FOR UPDATE FILE STATUS
     */
    public static boolean updateFileForCancelStatus() {
        boolean statu = false;
        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET  STATUS=5 WHERE FULL_FILE_PATH_LOCAL like '" + Initialize.getDefaultDirectory() + "%'";
            Connection conn = Initialize.getConn();
            logger.info("Update File STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.executeUpdate();
            statu = true;
          
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Update  File Status Error " + sqlExcept);
        }
        return statu;
    }
     /**
     * updateBBBIdForLogs() METHOD USE FOR UPDATE FILE STATUS
     */
    public static void updateBBBIdForLogs() {

        PreparedStatement ps = null;
        try {
            String Update_Query = "UPDATE FILE SET  BBB_ID=9000000 WHERE BBB_ID=5000000 and STATUS=2";
            Connection conn = Initialize.getConn();
            logger.info("Update BBB_ID STATUS   : ");
            ps = conn.prepareStatement(Update_Query);
            ps.executeUpdate();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
            logger.error("Update  BBB_ID Status Error " + sqlExcept);
        }

    }


}
