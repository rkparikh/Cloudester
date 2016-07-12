package com.bbb.main;

import com.bbb.dao.InsertEmailStatus;
import com.bbb.dao.MyDerbyConnection;
import com.bbb.dao.SelectFilePath;
import com.bbb.dao.SelectLogFileName;
import com.bbb.dao.SelectLogin;
import com.bbb.dao.UpdateFilePath;
import com.bbb.dao.UpdateLogin;
import com.bbb.init.Initialize;
import com.bbb.server.BBBServer;
import com.bbb.ui.MainAppFrame;
import com.bbb.ui.ProgressBarDia;
import com.bbb.ui.RightSidePopUp;
import com.bbb.utils.BBBUtils;
import com.bbb.utils.FileHandlerRead;
import com.bbb.utils.SplashScreen;
import com.bbb.utils.TrayIconUtils;
import static com.bbb.utils.TrayIconUtils.processUtils;
import com.bbb.webservice.WebServices;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * Main CLASS USE FOR MAIN METHOD
 */
public class Main {

    final static Logger logger = Logger.getLogger(Main.class);
    public static MainAppFrame mainAppFrame = null;
    SplashScreen splash = null;
    public static Date currDate = new Date();
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static String logFileName = null;
    public static File getLogFileName = null;

    /**
     * action() METHOD USE FOR LOAD SPLASH SCREEN AND APPLICATION
     */
    private void action() {
        BBBUtils.createDirectory(System.getProperty("user.home") + File.separator + "BBBSyncDirectory");

        //File f = new File(logFileName);
        //System.out.println("file path " + logFileName);
        System.out.println("Folder Path " + System.getProperty("user.home") + File.separator + "BBBSyncDirectory");

        logFileName = System.getProperty("user.home") + File.separator + "dailyLogs.txt" + dateFormat.format(currDate);
        getLogFileName = new File(logFileName);
        try {
            if (!getLogFileName.exists()) {
                getLogFileName.createNewFile();
            }

        } catch (IOException ex) {

        }

//              else{
//                  InsertEmailStatus.insertEmailStatus(Initialize.getUsername(),0, Initialize.getLoginToken(), logFileName);
//              }
        deleteLogFileAfter10Days(10);
        deleteEmailFileAfter10Days(10, ".txt");
        (new Thread(new SplashScreenLoad())).start();
        (new Thread(new ApplicationLoad())).start();
        (new Thread(new UpdateSyncRequest())).start();
        (new Thread(new UpdateRequestForSuccessfullyUploadedFiles())).start();
        (new Thread(new CheckLatestVersion())).start();
        //(new Thread(new DeletedFilesAndFolders())).start();

    }

    public void deleteEmailFileAfter10Days(long days, String fileExtension) {

        File folder = new File(System.getProperty("user.home") + File.separator);

        if (folder.exists()) {

            File[] listFiles = folder.listFiles();

            long eligibleForDeletion = System.currentTimeMillis()
                    - (days * 24 * 60 * 60 * 1000L);

            for (File listFile : listFiles) {

                if (listFile.getName().endsWith(fileExtension)
                        && listFile.lastModified() < eligibleForDeletion) {

                    if (!listFile.delete()) {

                        System.out.println("Sorry Unable to Delete Files..");

                    }
                }
            }
        }
    }

    public void deleteLogFileAfter10Days(long days) {

        File folder = new File(System.getProperty("user.home") + File.separator + "BBBLog");

        if (folder.exists()) {

            File[] listFiles = folder.listFiles();

            long eligibleForDeletion = System.currentTimeMillis()
                    - (days * 24 * 60 * 60 * 1000L);

            for (File listFile : listFiles) {

                if (listFile.lastModified() < eligibleForDeletion) {

                    if (!listFile.delete()) {

                        System.out.println("Sorry Unable to Delete Files..");

                    }
                }
            }
        }
    }
    /*UpdateSyncRequest CLASS */

    public class UpdateSyncRequest implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    if (Initialize.getLoginToken() != null && !Initialize.getLoginToken().equals("") && WebServices.updateSyncRequest()) {
                        //Thread sleep for 23 hours 55 min
                        Thread.sleep(81600000);
                    } else if (Initialize.getLoginToken() != null && !Initialize.getLoginToken().equals("")) {
                        //Thread sleep for 5 min
                        Thread.sleep(300000);
                    } else {
                        //Thread sleep for 1 min
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    /*DeletedFilesAndFolders CLASS */

    public class DeletedFilesAndFolders implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    if (Initialize.getLoginToken() != null && !Initialize.getLoginToken().equals("")) {
                        //Thread sleep for 10 min
                        UpdateFilePath.updateFileStatus(WebServices.getDeletedFilesAndFolders());
                        Thread.sleep(100000);
                    } else {
                        //Thread sleep for 1 min
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    /*UpdateRequestForSuccessfullyUploadedFiles CLASS */

    public class UpdateRequestForSuccessfullyUploadedFiles implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    if (Initialize.getLoginToken() != null && !Initialize.getLoginToken().equals("")) {
                        //Thread sleep for 23 hours 55 min
                        Thread.sleep(81600000);
                        String getLogFileNameFromDB = SelectLogFileName.getLogFilePath();
                        String getDateInfo[] = getLogFileNameFromDB.split("dailyLogs");
                        String getDate[] = getDateInfo[1].split(".txt");
                        if (dateFormat.format(currDate).equals(getDate[0])) {

                        } else {
                            if (getLogFileNameFromDB != null && !getLogFileNameFromDB.equals("")) {
                                File f = new File(getLogFileNameFromDB);
                                WebServices.sendLogsEmail(f);
                            }
                        }
                    } else if (Initialize.getLoginToken() != null && !Initialize.getLoginToken().equals("")) {
                        //Thread sleep for 5 min
                        Thread.sleep(300000);
                    } else {
                        //Thread sleep for 1 min
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    /*CheckLatestVersion CLASS */

    public class CheckLatestVersion implements Runnable {

       

        @Override
        public void run() {

            try {
                while (true) {
                    Thread.sleep(1000 * 60 * 60 * 2);
                    if (WebServices.getUpdatedVersion() == true) {
                        RightSidePopUp rspu = new RightSidePopUp();
                        //rspu.pack();
                        rspu.setVisible(true);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    /**
     * SplashScreenLoad CLASS IS INNER CLASS FOR SPLASH SCREEN
     */
    class SplashScreenLoad implements Runnable {

        @Override
        public void run() {
            try{
                
            
            splash = new SplashScreen(6000);
            splash.showSplashAndExit();
            }catch(NullPointerException e){
                
            }
        }

    }

    /**
     * ApplicationLoad CLASS IS INNER CLASS FOR LOAD APPLICATION
     */
    class ApplicationLoad implements Runnable {

        @Override
        public void run() {
            boolean netConnection = BBBUtils.netIsAvailable();

            if (netConnection) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }

                /*ACCESS DATBASE */
                MyDerbyConnection myDerbyConnection = new MyDerbyConnection();
                myDerbyConnection.getMyConnection();

                mainAppFrame = new MainAppFrame();
                Initialize.setMainAppFrame(mainAppFrame);
                /*CREATE TRAY ICON */
                TrayIconUtils.trayIcon("Sign");

                /*CHECK FOR ALREADY SAVE CREDIANTIAL*/
                TreeMap<String, String> loginMap = SelectLogin.getLoginCridential();
                if (loginMap != null && loginMap.size() > 0 && loginMap.firstEntry().getKey() != null && loginMap.firstEntry().getValue() != null && WebServices.loginCheck(loginMap.firstEntry().getKey(), loginMap.firstEntry().getValue())) {
                    BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                    UpdateLogin.updateDirectoryPath();
                    Initialize.is_folder_sync = 1;
                    //WebServices.updateFolderSync(Initialize.is_folder_sync);

                    try {

                        if (SelectLogFileName.getLogFilePath().equals(logFileName)) {
                            System.out.println("inside if log file");
                        }
                        if (SelectLogFileName.getLogFilePath().equals("true")) {

                            InsertEmailStatus.insertEmailStatus(Initialize.getUsername(), 0, Initialize.getLoginToken(), logFileName);
                            System.out.println("filename:" + logFileName);
                        }
                        String getLogFileNameFromDB = SelectLogFileName.getLogFilePath();
                        String getDateInfo[] = getLogFileNameFromDB.split("dailyLogs");
                        String getDate[] = getDateInfo[1].split(".txt");
                        if (dateFormat.format(currDate).equals(getDate[0])) {

                        } else {
                            if (!getLogFileNameFromDB.equals("") || getLogFileNameFromDB.equals("false")) {
                                System.out.println("inside one day mail");
                                File f = new File(getLogFileNameFromDB);
                                if (f.length() == 0) {
//                                    System.out.println("before write file");
//                                    try {
//                                        FileWriter fw = new FileWriter(f);
//                                        fw.write("Dear User,\nYou did not upload any file on " + getDateInfo[1] + "");
//                                        fw.close();
//                                        System.out.println("after write file");
//                                        WebServices.sendLogsEmail(f);
//                                    } catch (IOException ex) {
//
//                                    }

                                } else {

                                    WebServices.sendLogsEmail(f);
                                }
                            }
                        }

                    } catch (NullPointerException e) {

                    }
                    WebServices.getAmazonKeys();
                    TrayIconUtils.trayIcon("Sync");
                    TrayIconUtils.synProcessUpload();
                    if (splash != null) {
                        splash.showSplashAndExit();
                    }
                } else {
                    // splash.showSplashAndExit();
                    mainAppFrame.setVisible(true);

                }

            } else {
                if (splash != null) {
                    splash.showSplashAndExit();
                }
                TrayIconUtils.trayIcon("Network_Error");
            }

        }

    }

    /**
     * MAIN METHID
     */
    public static void main(String[] a) throws Exception {

        Main main = new Main();
        main.action();
        BBBServer ss = new BBBServer(main);
        ss.request();

    }

    /**
     * save(String path) METHOD IS USE FOR ON RIGHT CLICK CHANGE DIRECTORY PATH
     */
    public void save(String path) {
//        try {
//
//            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", "defaults write", "com.apple.desktopservices", "DSDontWriteNetworkStores", "true"});
//            System.out.println("sudo created");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        logger.error("Path " + path);
        if (Initialize.getLogin_id() == null || Initialize.getLogin_id() == 0) {
            JOptionPane.showMessageDialog(null, "Please Sign in to the BBB Desktop Sync Application", "BBB Backup App", JOptionPane.INFORMATION_MESSAGE, TrayIconUtils.errImageIcon);
            CardLayout cardLayout = (CardLayout) mainAppFrame.getContentPane().getLayout();
            cardLayout.show(mainAppFrame.getContentPane(), "loginPanel");
            Initialize.setDefaultDirectory(path);
            mainAppFrame.setVisible(true);
            //Initialize.setDefaultDirectory(path);

            System.out.println("Main set Path" + Initialize.getDefaultDirectory());

        } else {
            MainAppFrame.progressDialog.dispose();
            if (Initialize.getDefaultDirectory().equals(path) && Initialize.fileStopStatus == 1) {
                int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, "Stop Syncing this Folder to BioBigBox", "BBB Backup App", JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String parent_id = SelectFilePath.getParentFolderId();
                    TrayIconUtils.setApplicationIcon();
                    Initialize.fileStopStatus = 5;
                    Initialize.is_folder_sync = 0;
                    WebServices.updateFolderSync(Initialize.is_folder_sync, parent_id);
                    TrayIconUtils.trayIcon("Sync");
                    UpdateFilePath.updateFileForCancelStatus();
                }
            } else if (Initialize.getDefaultDirectory().equals(path) && Initialize.fileStopStatus == 5) {
                int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, "Start Syncing this Folder to BioBigBox", "BBB Backup App", JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    Initialize.fileStopStatus = 1;
                    Initialize.is_folder_sync = 1;
                    //WebServices.updateFolderSync(Initialize.is_folder_sync);
                    TrayIconUtils.trayIcon("Sync");
                    JOptionPane.showMessageDialog(null, "Folder " + path + " will now be backed up and synced to your BioBigBox account", "BBB Syncing Directory", JOptionPane.INFORMATION_MESSAGE, TrayIconUtils.appImageIcon);
                    processUtils.stop();
                    processUtils = null;
                    ProgressBarDia.fileNameUrl.setText("");
                    ProgressBarDia.progressSync.setValue(0);
                    ProgressBarDia.textareamQueue.setText("");
                    TrayIconUtils.setApplicationIcon();
                    if (!path.equals("")) {
                        Initialize.setDefaultDirectory(path);
                    }
                    if (Initialize.getDefaultDirectory() == null || Initialize.getDefaultDirectory().equals("")) {
                        logger.info("Defalult Directory Not select ");
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            logger.error("SyncPanel nothanksActionPerformed Error " + e);

                        }
                        JOptionPane.showMessageDialog(mainAppFrame, "Please Select Default Directory ", "BBB Backup App",
                                JOptionPane.ERROR_MESSAGE);
                    } else {

                        BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                        UpdateLogin.updateDirectoryPath();

                        mainAppFrame.setVisible(false);
                        try {
                            logger.error("i am in change directory");
                            FileHandlerRead.printFiles(new File(Initialize.getDefaultDirectory()));
                            TrayIconUtils.trayIcon("Sync");
                            TrayIconUtils.synProcessUpload();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (Initialize.fileStopStatus == 5) {
                int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, "Start Syncing this Folder to BioBigBox", "BBB Backup App", JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    Initialize.fileStopStatus = 1;
                    Initialize.is_folder_sync = 1;
                    //WebServices.updateFolderSync(Initialize.is_folder_sync);
                    TrayIconUtils.trayIcon("Sync");
                    JOptionPane.showMessageDialog(null, "Folder " + path + " will now be backed up and synced to your BioBigBox account", "BBB Syncing Directory", JOptionPane.INFORMATION_MESSAGE, TrayIconUtils.appImageIcon);
                    processUtils.stop();
                    processUtils = null;
                    ProgressBarDia.fileNameUrl.setText("");
                    ProgressBarDia.progressSync.setValue(0);
                    ProgressBarDia.textareamQueue.setText("");
                    TrayIconUtils.setApplicationIcon();
                    if (!path.equals("")) {
                        Initialize.setDefaultDirectory(path);
                    }
                    if (Initialize.getDefaultDirectory() == null || Initialize.getDefaultDirectory().equals("")) {
                        logger.info("Defalult Directory Not select ");
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            logger.error("SyncPanel nothanksActionPerformed Error " + e);

                        }
                        JOptionPane.showMessageDialog(mainAppFrame, "Please Select Default Directory ", "BBB Backup App",
                                JOptionPane.ERROR_MESSAGE);
                    } else {

                        BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                        UpdateLogin.updateDirectoryPath();
                        mainAppFrame.setVisible(false);
                        try {
                            logger.error("i am in change directory");
                            FileHandlerRead.printFiles(new File(Initialize.getDefaultDirectory()));
                            TrayIconUtils.trayIcon("Sync");
                            TrayIconUtils.synProcessUpload();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, "You have already assigned a folder to be the BioBigBox sync folder. Assigning a new sync folder will stop the previous folder from syncing.\nWould you like to continue?", "BBB Backup App", JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String parent_id = SelectFilePath.getParentFolderId();
                    Initialize.fileStopStatus = 1;
                    Initialize.is_folder_sync = 0;
                    WebServices.updateFolderSync(Initialize.is_folder_sync, parent_id);
                    //UpdateFilePath.up
                    JOptionPane.showMessageDialog(null, "Folder " + path + " will now be backed up and synced to your BioBigBox account", "BBB Syncing Directory", JOptionPane.INFORMATION_MESSAGE, TrayIconUtils.appImageIcon);
                    processUtils.stop();
                    processUtils = null;
                    ProgressBarDia.fileNameUrl.setText("");
                    ProgressBarDia.progressSync.setValue(0);
                    ProgressBarDia.textareamQueue.setText("");
                    TrayIconUtils.setApplicationIcon();
                    if (!path.equals("")) {
                        Initialize.setDefaultDirectory(path);
                    }
                    if (Initialize.getDefaultDirectory() == null || Initialize.getDefaultDirectory().equals("")) {
                        logger.info("Defalult Directory Not select ");
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            logger.error("SyncPanel nothanksActionPerformed Error " + e);

                        }
                        JOptionPane.showMessageDialog(mainAppFrame, "Please Select Default Directory ", "BBB Backup App",
                                JOptionPane.ERROR_MESSAGE);
                    } else {

                        BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                        UpdateLogin.updateDirectoryPath();
//                            Initialize.is_folder_sync = 1;
//                            WebServices.updateFolderSync(Initialize.is_folder_sync);
                        mainAppFrame.setVisible(false);
                        try {
                            logger.error("i am in change directory");
                            FileHandlerRead.printFiles(new File(Initialize.getDefaultDirectory()));
                            TrayIconUtils.trayIcon("Sync");
                            TrayIconUtils.synProcessUpload();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
