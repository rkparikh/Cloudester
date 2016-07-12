/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.init;

import com.bbb.dao.SelectFilePath;
import com.bbb.ui.MainAppFrame;
import java.io.File;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * Initialize CLASS USE FOR DECLARE CONSTANTS
 */
public class Initialize {

    final static Logger logger = Logger.getLogger(Initialize.class);

    private static Connection conn;
    private static String defaultDirectory = "";
    public static final String webServiceURL = "http://biobigbox.com/desktopapi?method=login";
    public static final String getKeyURL = "http://biobigbox.com/desktopapi?method=getAmazonKeys";
    public static final String uploadURL = "http://biobigbox.com/desktopapi?method=uploadUrl";
    public static final String getUploadFileID = "http://biobigbox.com/desktopapi?method=uploadFile";
    public static final String getFilesList = "http://biobigbox.com/desktopapi?method=getFiles";
    public static final String sendLogs = "http://biobigbox.com/desktopapi?method=sendLogs";
    public static final String sendLogsEmail = "http://biobigbox.com/desktopapi?method=sendLogsEmail";
    public static final String deleteFileURL = "http://biobigbox.com/desktopapi?method=deleteFiles";
    public static final String deleteFolderURL = "http://biobigbox.com/desktopapi?method=deleteFolder";
    public static final String getDateTime = "http://biobigbox.com/desktopapi?method=getDateTime";
    public static final String updateSyncRequest = "http://biobigbox.com/desktopapi?method=updateSyncRequest";
    public static final String getFilesAndFolders = "http://biobigbox.com/desktopapi?method=getFilesAndFolders";
    public static final String updateFolderSync = "http://biobigbox.com/desktopapi?method=updateFolderSync";
    public static final String checkUpdatedVersion = "http://biobigbox.com/desktopapi?method=checkUpdatedVersion";
    public static final String getDeletedFilesAndFolders = "http://biobigbox.com/desktopapi?method=getDeletedFilesAndFolders";
    public static final String existingBucketName = "biobigbox";
    public static final String keyName = "temp/";
    private static String loginToken;
    private static String ACCESS_KEY;
    private static String SECRET_KEY;
    public static int upload_status = 0;
    public static Integer login_id;
    public static MainAppFrame mainAppFrame;
    public static boolean stopthread = true;
    public static boolean progressBarVisible = true;
    public static List<String> firstTimeDownloadList = new ArrayList<String>();
    public static String username;
    public static Map<String, String> tempMap = new ConcurrentHashMap<String, String>();
    public static boolean Uptodatemsg = false;
    public static String uploadingfile = "";
    public static Integer fileStopStatus = 1;
    public static Integer is_folder_sync = 1;
    public static Integer emailStatus = 0;
    public static int count =1;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Initialize.username = username;
    }

    public static MainAppFrame getMainAppFrame() {
        return mainAppFrame;
    }

    public static void setMainAppFrame(MainAppFrame mainAppFrame) {
        Initialize.mainAppFrame = mainAppFrame;
    }

    public static Integer getLogin_id() {
        return login_id;
    }

    public static void setLogin_id(Integer login_id) {
        Initialize.login_id = login_id;
    }

    public static String getACCESS_KEY() {
        return ACCESS_KEY;
    }

    public static void setACCESS_KEY(String ACCESS_KEY) {
        Initialize.ACCESS_KEY = ACCESS_KEY;
    }

    public static String getSECRET_KEY() {
        return SECRET_KEY;
    }

    public static void setSECRET_KEY(String SECRET_KEY) {
        Initialize.SECRET_KEY = SECRET_KEY;
    }

    public static String getLoginToken() {
        return loginToken;
    }

    public static void setLoginToken(String loginToken) {
        Initialize.loginToken = loginToken;
    }

    public static String getDefaultDirectory() {
        return defaultDirectory;
    }

    public static void setDefaultDirectory(String defaultDirectory) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currDate = new Date();
        
        List listName = SelectFilePath.getFolderName();
        System.out.println("list contains :" + listName);
        Pattern regex = Pattern.compile("[$&!%?+,;=@#|]");
        Matcher matcher = regex.matcher(defaultDirectory);

        if (matcher.find()) {
            System.out.println("inside matcher" + defaultDirectory);
            if (listName.isEmpty()) {
                System.out.println("inside empty list");
                File dirPath = new File(defaultDirectory);
                File rename = new File(dirPath.getParent() + "\\upload_" + dateFormat.format(currDate) + "_" + count);
                dirPath.renameTo(rename);
                Initialize.defaultDirectory = rename.getAbsolutePath();

//            } else if (listName.contains(Initialize.defaultDirectory)) {
//                System.out.println("inside dir matched");
//                count++;
//                File dirPath = new File(defaultDirectory);
//                File rename = new File(dirPath.getParent() + "\\upload_" + dateFormat.format(currDate) + "_" + count);
//                dirPath.renameTo(rename);
//                Initialize.defaultDirectory = rename.getAbsolutePath();
            } else {

                System.out.println("count value :" + count);
                File dirPath = new File(defaultDirectory);
                for (int i = listName.size()-1; i < listName.size(); i++) {
                    String name = (String)listName.get(i);
                    if (name.contains(defaultDirectory)) {
//                        System.out.println("count value is:" + count);
//                        
//                        System.out.println("count value is after:" + count);
                    }
                    else{
                        count++;
                         System.out.println("count value is after:" + count);
                        
                    }
                }
                  
                File rename = new File(dirPath.getParent() + "\\upload_" + dateFormat.format(currDate) + "_" + count);
                dirPath.renameTo(rename);
                Initialize.defaultDirectory = rename.getAbsolutePath();
            }

        } else {
            if (defaultDirectory.contains("'")) {
                File dirPath = new File(defaultDirectory);
                File rename = new File(dirPath.getAbsolutePath().replaceAll("'", "`"));
                dirPath.renameTo(rename);
                Initialize.defaultDirectory = rename.getAbsolutePath();//StringEscapeUtils.escapeCsv(defaultDirectory);//
            } else {
                Initialize.defaultDirectory = defaultDirectory;//StringEscapeUtils.escapeCsv(defaultDirectory);//
            }

        }

    }

    public static Connection getConn() {
        return conn;
    }

    public static void setConn(Connection conn) {
        Initialize.conn = conn;
    }

    public static Integer getEmailStatus() {
        return emailStatus;
    }

    public static void setEmailStatus(Integer emailStatus) {
        Initialize.emailStatus = emailStatus;
    }

}
