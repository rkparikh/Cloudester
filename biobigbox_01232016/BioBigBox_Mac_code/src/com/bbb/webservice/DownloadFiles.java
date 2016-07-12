/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.webservice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.bbb.bean.FileBean;
import com.bbb.dao.InsertFilePath;
import com.bbb.init.Initialize;
import com.bbb.ui.DownloadProgressBarDia;
import static com.bbb.ui.DownloadProgressBarDia.downloadStatus;
import com.bbb.utils.BBBUtils;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * DownloadFiles CLASS USE FOR DOWNLOAD FILES FROM AMAZON
 */
public class DownloadFiles {

    final static Logger logger = Logger.getLogger(DownloadFiles.class);

    public static int progress = 0;
    public static Download download;
    public static TransferManager tx;
    public static String download_directory = "";
    public static PersistableDownload persistableDownload;
    public static FileBean fileBeanOuter;

    public static boolean downLoadFromAmazon(List<FileBean> fileList, String folder_path) {
        download_directory = folder_path;

        return downLoadFromAmazon(fileList);
    }

    public static boolean downLoadFromAmazon(List<FileBean> fileList) {
        boolean downloadingStatus = false;
        AWSCredentials credentials = new BasicAWSCredentials(Initialize.getACCESS_KEY(), Initialize.getSECRET_KEY());
        AmazonS3 s3client = new AmazonS3Client(credentials);
        tx = new TransferManager(s3client);
        int fileCounter = 0;

        try {
            DownloadProgressBarDia.downfileNameUrl.setText("");
            for (FileBean fileBean : fileList) {
                boolean pausePlayInner = true;
                fileCounter++;
                String countLabel[] = DownloadProgressBarDia.downFileCountLabel.getText().split("/");
                String filepath = "";
                fileBeanOuter = fileBean;
                try {
                    GetObjectRequest request = new GetObjectRequest("biobigbox", fileBean.getS3_path());

                    String directory[] = fileBean.getFolder_path().split("\\/");
                    String directoryStruc = "";
                    for (int i = 0; i < directory.length; i++) {

                        if (i == 0) {
                            directoryStruc = directory[i];
                        } else {
                            directoryStruc += File.separator + directory[i];
                        }
                        // File file = new File(Initialize.getDefaultDirectory() + File.separator + directoryStruc);
                        File file = new File(download_directory + File.separator + directoryStruc);
                        if (!file.exists()) {
                            if (file.mkdir()) {

                            } else {
                            }
                        }
                    }
                    String folderpath = fileBean.getFolder_path().replace("/", File.separator);
                    //  File f = new File(Initialize.getDefaultDirectory() + File.separator + folderpath + fileBean.getFile_name());
                    File f = new File(download_directory + File.separator + folderpath + fileBean.getFile_name());

                    if (f.exists()) {
                        // String newfilepath = BBBUtils.GetUniqueFilePath(Initialize.getDefaultDirectory() + File.separator + folderpath + fileBean.getFile_name(), fileBean.getFile_name(), Initialize.getDefaultDirectory() + File.separator + folderpath);
                        String newfilepath = BBBUtils.GetUniqueFilePath(download_directory + File.separator + folderpath + fileBean.getFile_name(), fileBean.getFile_name(), download_directory + File.separator + folderpath);
                        f = new File(newfilepath);
                    }
                    filepath = f.getPath();
                    DownloadProgressBarDia.downfileNameUrl.setText(f.getPath());
                    download = tx.download(request, f);
                    int count = 0;
                    while (download.isDone() == false) {
                        if (downloadStatus || resumeStatus) {
                            try {
                                if (!resumeStatus) {
                                    persistableDownload = download.pause();
                                }
                                out:
                                while (pausePlayInner) {
                                    System.out.println("I AM IN PAUSE");
                                    if (resumeStatus) {
                                        Download downloadResume = tx.resumeDownload(persistableDownload);
                                        while (downloadResume.isDone() == false) {
                                            download = downloadResume;
                                            if (downloadStatus) {
                                                System.out.println("i am in again pause ");
                                                resumeStatus = false;
                                                persistableDownload = downloadResume.pause();
                                                continue out;
                                            }
                                            System.out.println("i am in running ");
                                            DownloadProgressBarDia.downProgressSync.setValue((int) download.getProgress().getPercentTransferred());

                                        }
                                        if (DownloadProgressBarDia.downProgressSync.getValue() == 100) {
                                            downloadStatus = false;
                                            resumeStatus = false;
                                            pausePlayInner = false;
                                            continue out;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        DownloadProgressBarDia.downProgressSync.setValue((int) download.getProgress().getPercentTransferred());
                        count++;
                        System.out.println("download Progress" + count);
                    }

                    if (download.isDone()) {
                        fileBean.setCreated(BBBUtils.getCreatedTime(f.getPath()));
                        DownloadProgressBarDia.downFileCountLabel.setText(fileCounter + "/" + countLabel[1]);
                        DownloadProgressBarDia.downProgressSync.setValue(0);
                        DownloadProgressBarDia.downTextareamQueue.setText(filepath + " Completed task 100%\n" + DownloadProgressBarDia.downTextareamQueue.getText());
                        fileBean.setModified(BBBUtils.getLastModified(filepath));
                        fileBean.setFull_file_path(filepath);
                        InsertFilePath.insertFileWithBBB_ID(fileBean);
                        Initialize.firstTimeDownloadList.add(filepath);
                        filepath = "";
                    }
                } catch (Exception exInner) {
                    DownloadProgressBarDia.downFileCountLabel.setText(fileCounter + "/" + countLabel[1]);
                    DownloadProgressBarDia.downTextareamQueue.setText(filepath + " Uncomplete task \n" + DownloadProgressBarDia.downTextareamQueue.getText());
                    logger.error("DownloadFiles downLoadFromAmazon " + filepath + exInner);
                    exInner.printStackTrace();

                }

            }
            downloadingStatus = true;
            DownloadProgressBarDia.downfileNameUrl.setText("");
            DownloadProgressBarDia.downTextareamQueue.setText("");
            DownloadProgressBarDia.downProgressSync.setValue(0);
            DownloadProgressBarDia.downProgressSync.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("DownloadFiles downLoadFromAmazon " + ex);

        }
        return downloadingStatus;
    }
    public static boolean resumeStatus = false;

    public static void pauseDownload() {

    }
    public static Thread downloadProcessThread;

    public static void resumeDownload() {
        resumeStatus = true;
        downloadStatus = false;
        //downloadProcessThread = new Thread(new DownloadProcessThread());
        //downloadProcessThread.start();
    }

    public static void killDownloadThread() {
        //  RestoreDia.stopDownloadThread();
        downloadProcessThread.stop();

    }

    static class DownloadProcessThread implements Runnable {

        public void run() {
            try {
                Download downloadResume = tx.resumeDownload(persistableDownload);
                while (downloadResume.isDone() == false) {

                    download = downloadResume;
                    if (downloadStatus) {
                        try {
                            persistableDownload = downloadResume.pause();
                            while (true) {

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    System.out.println("Download Resume file Progress " + (int) downloadResume.getProgress().getPercentTransferred());
                    if (DownloadProgressBarDia.downProgressSync.getValue() < (int) downloadResume.getProgress().getPercentTransferred()) {
                        DownloadProgressBarDia.downProgressSync.setValue((int) downloadResume.getProgress().getPercentTransferred());
                    }
                }
                if (download.isDone()) {
                    String countLabel[] = DownloadProgressBarDia.downFileCountLabel.getText().split("/");
                    int countPause = Integer.parseInt(countLabel[0]);
                    countPause++;
                    fileBeanOuter.setCreated(BBBUtils.getCreatedTime(DownloadProgressBarDia.downfileNameUrl.getText()));
                    DownloadProgressBarDia.downFileCountLabel.setText(countPause + "/" + countLabel[1]);
                    DownloadProgressBarDia.downProgressSync.setValue(0);
                    DownloadProgressBarDia.downTextareamQueue.setText(DownloadProgressBarDia.downfileNameUrl.getText() + " Completed task 100%\n" + DownloadProgressBarDia.downTextareamQueue.getText());
                    fileBeanOuter.setModified(BBBUtils.getLastModified(DownloadProgressBarDia.downfileNameUrl.getText()));
                    fileBeanOuter.setFull_file_path(DownloadProgressBarDia.downfileNameUrl.getText());
                    InsertFilePath.insertFileWithBBB_ID(fileBeanOuter);
                    Initialize.firstTimeDownloadList.add(DownloadProgressBarDia.downfileNameUrl.getText());

                }
                killDownloadThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
