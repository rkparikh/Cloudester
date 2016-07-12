/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.bean.FileBean;
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
 * InsertFilePath CLASS USE FOR INSERT FILE INFORMATIONS IN DATA BASE
 */
public class InsertFilePath {

    final static Logger logger = Logger.getLogger(InsertFilePath.class);

    /**
     * insertFileWithBBB_ID(FileBean fileBean) METHOD USE FOR INSERT FILE
     * INFORATION WITH BBB_ID USE IN DOWNLOADING CASE*
     */
    public static void insertFileWithBBB_ID(FileBean fileBean) {
        try {
            Connection conn = Initialize.getConn();
            logger.info("Insert File Details   : ");
            String insert_Query = "Insert into FILE(BBB_ID,FILE_NAME,SIZE,CREATED,MODIFIED,VERSION,FOLDER_ID,PATH,FOLDER_PATH,S3_PATH,FULL_FILE_PATH_LOCAL,STATUS,LOGIN_ID) values (?,?,?,?,?,?,?,?,?,?,?,2,?)";
            PreparedStatement stmt = conn.prepareStatement(insert_Query);
            stmt.setInt(1, fileBean.getBbb_id() == null ? 0 : fileBean.getBbb_id());
            stmt.setString(2, fileBean.getFile_name() == null ? "" : fileBean.getFile_name());
            stmt.setLong(3, fileBean.getSize() == null ? 0 : fileBean.getSize());
            stmt.setLong(4, fileBean.getCreated() == null ? 0 : fileBean.getCreated());
            stmt.setLong(5, fileBean.getModified() == null ? 0 : fileBean.getModified());
            stmt.setInt(6, fileBean.getVersion() == null ? 0 : fileBean.getVersion());
            stmt.setString(7, fileBean.getFolder_id() == null ? "" : fileBean.getFolder_id());
            stmt.setString(8, fileBean.getPath() == null ? "" : fileBean.getPath());
            stmt.setString(9, fileBean.getFolder_path() == null ? "" : fileBean.getFolder_path());
            stmt.setString(10, fileBean.getS3_path() == null ? "" : fileBean.getS3_path());
            stmt.setString(11, fileBean.getFull_file_path() == null ? "" : fileBean.getFull_file_path());
            stmt.setInt(12, Initialize.getLogin_id());
            stmt.executeUpdate();
            logger.info("Insert File Details in  File TABLE ");

        } catch (SQLException sqlExcept) {
            logger.error("Insert File Detail insertFileWithBBB_ID Error " + sqlExcept);
        }
    }

    /**
     * insertFileWithoutBBB_ID(FileBean fileBean) METHOD USE FOR INSERT FILE
     * INFORATION WITHOUT BBB_ID USE IN UPLOAD CASE OR USE BY WATCHER*
     */
    public static void insertFileWithoutBBB_ID(FileBean fileBean) {
        try {
            Connection conn = Initialize.getConn();
            logger.info("Insert File Details   : ");
            String insert_Query = "Insert into FILE(FILE_NAME,SIZE,CREATED,MODIFIED,VERSION,FOLDER_ID,PATH,FOLDER_PATH,S3_PATH,FULL_FILE_PATH_LOCAL,STATUS,LOGIN_ID) values (?,?,?,?,?,?,?,?,?,?,"+Initialize.fileStopStatus+",?)";
            PreparedStatement stmt = conn.prepareStatement(insert_Query);
            stmt.setString(1, fileBean.getFile_name() == null ? "" : fileBean.getFile_name());
            stmt.setLong(2, fileBean.getSize() == null ? 0 : fileBean.getSize());
            stmt.setLong(3, fileBean.getCreated() == null ? 0 : fileBean.getCreated());
            stmt.setLong(4, fileBean.getModified() == null ? 0 : fileBean.getModified());
            stmt.setInt(5, fileBean.getVersion() == null ? 0 : fileBean.getVersion());
            stmt.setString(6, fileBean.getFolder_id() == null ? "" : fileBean.getFolder_id());
            stmt.setString(7, fileBean.getPath() == null ? "" : fileBean.getPath());
            stmt.setString(8, fileBean.getFolder_path() == null ? "" : fileBean.getFolder_path());
            stmt.setString(9, fileBean.getS3_path() == null ? "" : fileBean.getS3_path());
            stmt.setString(10, fileBean.getFull_file_path() == null ? "" : fileBean.getFull_file_path());
            stmt.setInt(11, Initialize.getLogin_id());
            stmt.executeUpdate();
            logger.info("Insert File Details in  File TABLE ");

        } catch (SQLException sqlExcept) {
            logger.error("Insert File insertFileWithoutBBB_ID Error " + sqlExcept);
        }
    }
    /**
     * insertParentFolderId() METHOD IS USED FOR SET THE PARENT FOLDER ID
     * 
     */
    public static void insertParentFolderId(String folder_id){
        try{
             Connection conn = Initialize.getConn();
            logger.info("Insert File Details   : ");
            String insert_Query = "UPDATE FILE SET PARENT_FOLDER_ID='"+folder_id+"', FOLDER_SYNC_STATUS=1 ";
            PreparedStatement stmt = conn.prepareStatement(insert_Query);
//            stmt.setString(1, folder_id);
//            stmt.setInt(2, 1);
            stmt.executeUpdate();
             logger.info("Insert Parent Folder Id in  File TABLE ");
        }catch(SQLException e){
            logger.error("Insert Parent Folder Id in  File TABLE " + e);
        }
    }

}
