
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import com.bbb.bean.FileBean;
import com.bbb.dao.InsertFilePath;
import com.bbb.dao.SelectFilePath;
import com.bbb.dao.UpdateFilePath;
import com.bbb.init.Initialize;
import com.bbb.ui.ProgressBarDia;
import static com.bbb.utils.WatchDir.sourceDirectory;
import com.bbb.webservice.UploadFiles;
import com.bbb.webservice.WebServices;
import static com.bbb.webservice.WebServices.sendLogs;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * ProcessUtils CLASS USE FOR RUN UPLOAD FILE PROCESS
 */
public class ProcessUtils {

    final static Logger logger = Logger.getLogger(ProcessUtils.class);
    public static Thread watcherProcess = null;
    public static Thread uploadProcess = null;
    public static Thread fileProcess = null;
    public static Thread moveDirectoryProcess = null;
    public static boolean run = true;
    //public static int filecounting = 0;

    public void run() throws InterruptedException {
        watcherProcess = (new Thread(new WatcherProcess()));
        fileProcess = (new Thread(new FileProcess()));
        uploadProcess = (new Thread(new UploadProcess()));
        run = true;
        upload = true;

    }

    public void stop() {
        try {
            watcherProcess.stop();
            fileProcess.stop();
            uploadProcess.stop();
            UploadFiles.tm.shutdownNow();
            ProgressBarDia.fileCountLabel.setText("0/0");
            TrayIconUtils.setApplicationIcon();
            run = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void watcherStart() {
        logger.error("watcher start before");
        fileProcess.start();
        uploadProcess.start();
        watcherProcess.start();

        logger.error("watcher start after");
    }

    /**
     * WatcherProcess INNER CLASS USE FOR RUN WATCH DIRECTORY PROCESS
     */
    public class WatcherProcess implements Runnable {

        public void run() {
            try {
                while (true) {
                    ProgressBarDia.fileCountLabel.setText("0/" + SelectFilePath.getFileCount());
                    // logger.error("Default dir " + Initialize.getDefaultDirectory());
                    WatchDir.watchSource(Initialize.getDefaultDirectory().replaceAll("'","''"));
                    // Thread.sleep(1);
                }
            } catch (Exception e) {
                logger.error("ProcessUtils i am in  WatcherProcess " + e);
                e.printStackTrace();
            }

        }
    }

    /**
     * FileProcess INNER CLASS USE FOR INSERT FILE INFORMATION IN DATABASE WATCH
     * BY WatcherProcess
     */
    public class FileProcess implements Runnable {

        public synchronized void run() {

            try {
                while (true) {
                    //  System.out.println("Map Size " + Initialize.tempMap);
                    Iterator<Map.Entry<String, String>> it = Initialize.tempMap.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, String> entry = it.next();
                        File file = new File(entry.getKey());
                        int count = 0;

                        if (file.isFile() && isCompletelyWritten(file) && entry.getValue().equals("ENTRY_MODIFY") && SelectFilePath.isFileInDatabase(entry.getKey())) {
                            Initialize.tempMap.remove(entry.getKey());
                        } else if (file.isFile() && isCompletelyWritten(file) && entry.getValue().equals("ENTRY_MODIFY") && !SelectFilePath.isFileInDatabase(entry.getKey())) {
                            String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
                            count = Integer.parseInt(countLabel[1]);
                            count++;
                            System.out.println("Count File " + count);
                            ProgressBarDia.fileCountLabel.setText(countLabel[0] + "/" + count);
                            Initialize.tempMap.remove(entry.getKey());
                            FileBean fileBean = new FileBean();
                            fileBean.setFile_name(BBBUtils.getFileNameByUrl(entry.getKey()));
                            fileBean.setCreated(BBBUtils.getCreatedTime(entry.getKey()));
                            fileBean.setModified(BBBUtils.getLastModified(entry.getKey()));
                            fileBean.setFolder_path(BBBUtils.getPathInsideSourceFolder(entry.getKey(), sourceDirectory, BBBUtils.getFileNameByUrl(entry.getKey())));
                            fileBean.setVersion(1);
                            fileBean.setSize(BBBUtils.getFileSize(entry.getKey()));
                            fileBean.setFull_file_path(entry.getKey());
                            InsertFilePath.insertFileWithoutBBB_ID(fileBean);
                            ProgressBarDia.fileCountLabel.setText(countLabel[0] + "/" + count);
                            System.out.println("Map Size After " + Initialize.tempMap.size());

                        } else if (entry.getValue().equals("ENTRY_DELETE")) {
                            /*Delete Code API */
//                            System.out.println("I am in delete File ");
//                            FileBean fileBean = SelectFilePath.getFileBeanByPath(entry.getKey());
//                            if (fileBean != null && fileBean.getBbb_id() != null && fileBean.getStatus() == 2 && WebServices.deleteFile(fileBean)) {
//                                UpdateFilePath.deleteFileStatus(fileBean);
//                            } else if (fileBean != null && fileBean.getBbb_id() != null) {
//                                UpdateFilePath.deleteFileStatus(fileBean);
//                            }
                            Initialize.tempMap.remove(entry.getKey());

                        } else if (entry.getValue().equals("ENTRY_DELETE_FOLDER")) {
                            /*Delete Code API */
//                            System.out.println("I am in delete Folder ");
//                            List<FileBean> fileBeanList = SelectFilePath.getFolderBeanByPath(BBBUtils.getPathInsideSourceFolder(entry.getKey(), sourceDirectory, ""));
//                            System.out.println("Size of list items:" + fileBeanList.size());
//                            int count1 = 0;
//                            for (FileBean fileBean : fileBeanList) {
//                                count1++;
//                                if (count1 == 1) {
//                                    WebServices.deleteFolder(fileBean);
//                                }
//                                if (fileBean != null && fileBean.getBbb_id() != null && fileBean.getStatus() == 2) {
//                                    UpdateFilePath.deleteFileStatus(fileBean);
//                                } else if (fileBean != null && fileBean.getBbb_id() != null) {
//                                    UpdateFilePath.deleteFileStatus(fileBean);
//                                }
//                            }
                           Initialize.tempMap.remove(entry.getKey());

                        }
                        

                    }
                    Thread.sleep(20);
                }
            } catch (Exception e) {
                logger.error("ProcessUtils FileProcess " + e);
                e.printStackTrace();
            }

        }
    }

    public static boolean upload = true;

    /**
     * UploadProcess INNER CLASS USE FOR UPLOAD PROCESS
     */
    public class UploadProcess implements Runnable {

        FileBean fileBean = null;

        public void run() {
            try {
//                while (run) {
//                    if (!run) {
//                        break;
//                    }
                while (true) {
                    if (upload) {
                        fileBean = SelectFilePath.getFileNotSYNCDatabase();
                        if (fileBean.getId() != null) {
                            /* SELECT FROM YOUR DEFAULT DIRECTORY*/
                            FileBean fileBeanOld = SelectFilePath.isFileOld(fileBean.getFull_file_path());
                            fileBean.setIsOld(fileBeanOld.isIsOld());
                            fileBean.setOldSize(fileBeanOld.getOldSize());
                            fileBean.setBbb_id_inString(SelectFilePath.getFileOldID(fileBean.getFull_file_path()) + "");
//                            int line = ProgressBarDia.textareamQueue.getLineCount();
//                            String countLabelBefore[] = ProgressBarDia.fileCountLabel.getText().split("/");
//                            ProgressBarDia.fileCountLabel.setText(countLabelBefore[0] + "/" + line);
                            ProgressBarDia.fileNameUrl.setText(fileBean.getFull_file_path());

                            File file = new File(fileBean.getFull_file_path());
                            Initialize.uploadingfile = fileBean.getFull_file_path();
                            if (!file.exists()) {
                                UpdateFilePath.noFileStatus(fileBean);
                                ProgressBarDia.fileNameUrl.setText("...............");
                                ProgressBarDia.progressSync.setValue(0);
                                ProgressBarDia.textareamQueue.setText(fileBean.getFull_file_path() + " Uncompleted \n" + ProgressBarDia.textareamQueue.getText());
                                BBBUtils.highlight(fileBean.getFull_file_path() + " Uncompleted \n");
                                String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
                                int count = Integer.parseInt(countLabel[0]);
                                count++;
                                ProgressBarDia.fileCountLabel.setText(count + "/" + countLabel[1]);
                                Initialize.uploadingfile = "";
                            } else if (WebServices.getUploadUrl(fileBean.getFull_file_path(), fileBean) && UploadFiles.UploadFile(fileBean) && ProcessUtils.upload) {

                                FileBean fileBeanReturn = WebServices.getUploadFileID(fileBean.getFull_file_path(), fileBean);
                                System.out.println("BBB_IDandFOLDER:" + fileBeanReturn.getBbb_id() + "/" + fileBeanReturn.getFolder_id());
                                if (fileBeanReturn.getBbb_id() != null && fileBeanReturn.getBbb_id() != 0) {
                                    fileBean.setBbb_id(fileBeanReturn.getBbb_id());
                                    fileBean.setFolder_id(fileBeanReturn.getFolder_id());
                                    if (UpdateFilePath.updateFileStatus(fileBean)) {
                                        String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
                                        int j=1;
                                        if(j==1){
                                            InsertFilePath.insertParentFolderId(fileBeanReturn.getParent_folder_id());
                                            j++;
                                        }
                                         
                                        int count = Integer.parseInt(countLabel[0]);
                                        count++;
                                        ProgressBarDia.fileCountLabel.setText(count + "/" + countLabel[1]);
                                        if (count == Integer.parseInt(countLabel[1])) {
                                            
                                             InsertFilePath.insertParentFolderId(fileBeanReturn.getParent_folder_id());
                                             Initialize.is_folder_sync = 1;
                                             WebServices.updateFolderSync(Initialize.is_folder_sync,fileBeanReturn.getParent_folder_id());
//                                            String folder_id = SelectFilePath.getFolderId();
//                                            if (!folder_id.equals("null")) {
//                                                if (fileBean.getFolder_id().equals(fileBean.getFolder_id())) {
//                                                    WebServices.updateFolderSync(1, fileBeanReturn.getFolder_id());
//                                                }
//                                                WebServices.updateFolderSync(0, folder_id);
//                                            }

                                            List getList = SelectFilePath.getFailureFilesByName();
                                            String getListConcated = "";
                                            if (getList == null || getList.equals("")) {

                                            } else {
                                                for (int i = 0; i < getList.size(); i++) {

                                                    getListConcated += " " + getList.get(i) + " <br> ";
                                                }
                                                //System.out.println("get all failed file names:" + getListConcated);
                                                if (getListConcated.equals("")) {

                                                } else {
                                                    // if(getList)
                                                    System.out.println("get all failed file names:" + getListConcated);
                                                    sendLogs("The following files  were not backed up successfully:<br>" + getListConcated);
                                                    UpdateFilePath.updateBBBIdForLogs();
                                                }

                                            }
                                        }
                                        Initialize.uploadingfile = "";
                                        fileBean = null;
                                    }
                                }
                            }
                        }
                    }

                    Thread.sleep(20);

                }
            } catch (Exception e) {
                logger.error("ProcessUtils UploadProcess " + e);
                e.printStackTrace();
            }
        }

    }

    private boolean isCompletelyWritten(File file) {

        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

//    public static byte[] getAllFilesValue() throws IOException {
//        File storeData = new File(Initialize.getDefaultDirectory());
//        FileInputStream fin = null;
//        BufferedInputStream bin = null;
//        FileOutputStream fout = null;
//        BufferedOutputStream bout = null;
//        byte getData[]=null;
//        if (storeData.isDirectory()) {
//
//            fin = new FileInputStream(storeData);
//            bin = new BufferedInputStream(fin);
//             getData = new byte[(int) storeData.length()];
//            bin.read(getData);
//            BBBUtils.createDirectory(System.getProperty("user.home") + File.separator + "BBBDataStore");
//            String getpath = System.getProperty("user.home");
//            fout = new FileOutputStream(getpath + "\\BBBDataStore");
//            bout = new BufferedOutputStream(fout);
//            bout.write(getData);
//            
//        }
//        return getData;
//    }
}
