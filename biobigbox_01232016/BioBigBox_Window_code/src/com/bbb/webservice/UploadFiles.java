/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.webservice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.bbb.bean.FileBean;
import com.bbb.dao.UpdateFilePath;
import com.bbb.init.Initialize;
import com.bbb.main.Main;
import com.bbb.ui.ProgressBarDia;
import com.bbb.utils.BBBUtils;
import com.bbb.utils.ProcessUtils;
import com.bbb.utils.TrayIconUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
public class UploadFiles {

    final static Logger logger = Logger.getLogger(UploadFiles.class);
    public static String url = "";
    static int count = 0;
    static double totalByteRead = 0;
    public static Upload upload = null;
    static PersistableUpload persistableUpload = null;
    public static TransferManager tm = null;
    static PutObjectRequest request = null;
    static String file_name = "";
    static FileBean fileBean1 = new FileBean();
    public static int progressBarValue = 0;
    public static boolean pausePlayWork = false;
    public static int bbFileId = 0;
    public static ArrayList<StackFile> arrayFile = new ArrayList<StackFile>();
    public static Date currDate = new Date();
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static FileWriter fw = null;

    public static boolean UploadFile(FileBean fileBean) {
        boolean status = false;
        url = fileBean.getFull_file_path();
        file_name = fileBean.getFile_name();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String filePath[] = fileBean.getFull_file_path().split(pattern);
        AWSCredentials credentials = new BasicAWSCredentials(Initialize.getACCESS_KEY(), Initialize.getSECRET_KEY());
        tm = new TransferManager(credentials);
        TransferManagerConfiguration configuration = new TransferManagerConfiguration();
        configuration.setMultipartUploadThreshold(1024 * 1024);
        tm.setConfiguration(configuration);

        request = new PutObjectRequest(Initialize.existingBucketName, Initialize.keyName + filePath[filePath.length - 1], new File(fileBean.getFull_file_path()));
        upload();
        upload = tm.upload(request);

        if (progressBarValue != 0) {
            ProgressBarDia.progressSync.setValue(progressBarValue);
        }

        try {
            // You can block and wait for the upload to finish
            while (!upload.isDone()) {

            }
            wait:
            while (upload.getProgress().getPercentTransferred() < 100) {
                upload.waitForCompletion();
                continue wait;
            }
            if (upload.isDone()) {
                TrayIconUtils.setApplicationIcon();
                // System.out.println("Upload status Completed ");
                progressBarValue = 0;
                return true;

            } else {
                UpdateFilePath.noFileStatus(fileBean);
                ProgressBarDia.fileNameUrl.setText("...............");
                ProgressBarDia.progressSync.setValue(0);
                ProgressBarDia.textareamQueue.setText(url + " Uncompleted \n" + ProgressBarDia.textareamQueue.getText());
                BBBUtils.highlight(url + " Uncompleted \n");

                arrayFile.add(new StackFile(url, dateFormat.format(currDate)));
                try {

                    fw = new FileWriter(Main.getLogFileName);
                    fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");
                    fw.append("| Name                                                                                                       | Date        |\n");
                    fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");

                    Iterator<StackFile> itS = arrayFile.iterator();

                    while (itS.hasNext()) {
                        StackFile sf = itS.next();
                        fw.append("| " + String.format("%-107s", sf.getName()) + "| " + String.format("%-10s", sf.getDate()) + "  |\n");
                        fw.append((itS.hasNext())
                                ? "|------------------------------------------------------------------------------------------------------------|-------------|\n"
                                : "+------------------------------------------------------------------------------------------------------------+-------------+\n");

                    }

                    fw.close();
                } catch (IOException e) {

                }
                String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
                int count = Integer.parseInt(countLabel[0]);
                count++;
                ProgressBarDia.fileCountLabel.setText(count + "/" + countLabel[1]);

                TrayIconUtils.setApplicationIcon();
                return false;
            }
        } catch (Exception amazonClientException) {
            System.out.println("Pause exception");
            amazonClientException.printStackTrace();

        }
        return status;
    }

    public static void upload() {
        request.setGeneralProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                if (!ProcessUtils.upload) {
                    upload.pause();
                } else {
                    // System.out.println("Running......");
                    if (!ProgressBarDia.fileNameUrl.getText().trim().equals(url)) {
                        ProgressBarDia.progressSync.setValue(0);
                        ProgressBarDia.progressSync.setMaximum(100);
                        ProgressBarDia.progressSync.repaint();
                    }
                    progressEvent.getBytesTransferred();
                    if (Initialize.getLogin_id() > 0 && ProcessUtils.run && !ProgressBarDia.fileNameUrl.getText().trim().equals("")) {
                        TrayIconUtils.uploadIconChange();
                    } else {
                        TrayIconUtils.setApplicationIcon();
                    }
                    if (progressBarValue != 0 && progressBarValue < (int) upload.getProgress().getPercentTransferred()) {
                        ProgressBarDia.progressSync.setValue((int) upload.getProgress().getPercentTransferred());

                    } else if (progressBarValue != 0) {
                        ProgressBarDia.progressSync.setValue(progressBarValue);
                    } else {
                        ProgressBarDia.progressSync.setValue((int) upload.getProgress().getPercentTransferred());

                    }
                    switch (progressEvent.getEventCode()) {

                        case ProgressEvent.COMPLETED_EVENT_CODE:
                            ProgressBarDia.fileNameUrl.setText("...............");
                            ProgressBarDia.textareamQueue.setText(url + " Completed task 100%\n" + ProgressBarDia.textareamQueue.getText());
                            arrayFile.add(new StackFile(url, dateFormat.format(currDate)));
                            try {
                                // fw = new FileWriter(new File(Initialize.logFileName));
                                fw = new FileWriter(Main.getLogFileName);
                                fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");
                                fw.append("| Full File Name                                                                                             | Date        |\n");
                                fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");
                                Iterator<StackFile> itS = arrayFile.iterator();
                                while (itS.hasNext()) {
                                    StackFile sf = itS.next();

                                    fw.append("| " + String.format("%-107s", sf.getName()) + "| " + String.format("%-10s", sf.getDate()) + "  |\n");
                                    fw.append((itS.hasNext())
                                            ? "|------------------------------------------------------------------------------------------------------------|-------------|\n"
                                            : "+------------------------------------------------------------------------------------------------------------+-------------+\n");

                                }

                                fw.close();
                            } catch (IOException e) {

                            }

                            TrayIconUtils.setApplicationIcon();
                            break;
                        case ProgressEvent.FAILED_EVENT_CODE:
                            try {
                            } catch (Exception e) {
                                System.out.println("Pause exception1");
                            }
                            break;
                    }
                }
            }

        }
        );
    }

    public static void pauseOperation() {
        System.out.println("in pause operation");

        try {
            TrayIconUtils.setApplicationIcon();

            long MB = 1024 * 1024;
            TransferProgress progress = upload.getProgress();

            //System.out.println("The pause will occur once 1 MB of data is uploaded");
//            while (progress.getBytesTransferred() < 1 * MB) {
//                Thread.sleep(2000);
//            }
            boolean forceCancel = true;
            float dataTransfered = (float) upload.getProgress().getBytesTransferred();
            System.out.println("Data Transfered until now: " + dataTransfered);
            PauseResult<PersistableUpload> pauseResult = ((Upload) upload).tryPause(forceCancel);
            System.out.println("The upload has been paused. The code that we've got is " + pauseResult.getPauseStatus());
            pauseResult = ((Upload) upload).tryPause(forceCancel);
            PersistableUpload persistableUpload = (PersistableUpload) pauseResult.getInfoToResume();
            System.out.println("Storing information into file");
            File f = new File(System.getProperty("user.home") + File.separator + "resume-upload");
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }
            progressBarValue = ProgressBarDia.progressSync.getValue();
            System.out.println(" Progress Bar Value on Pause" + progressBarValue);

            FileOutputStream fos = new FileOutputStream(f);
            persistableUpload.serialize(fos);
            fos.close();
//            if(progressBarValue>=0){
//                System.out.println("in if od progress bar value");
//            TrayIconUtils.setApplicationIcon();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            TrayIconUtils.setApplicationIcon();
        }

    }

    public static Thread uploadProcessThread;

    public static void resumeOperation() {
        //TrayIconUtils.uploadIconChange();
        Initialize.mainAppFrame.progressDialog.setVisible(Initialize.progressBarVisible);
        uploadProcessThread = new Thread(new UploadProcessThread());
        uploadProcessThread.start();
    }

    public static void killThread() {
        try {
            uploadProcessThread.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* For DownloadProcessThread in file  */
    static class UploadProcessThread implements Runnable {

        public void run() {
            try {

                FileInputStream fis = new FileInputStream(new File(System.getProperty("user.home") + File.separator + "resume-upload"));
                PersistableUpload persistableUpload;
                persistableUpload = PersistableTransfer.deserializeFrom(fis);
                Upload uploadd = tm.resumeUpload(persistableUpload);
                long MB = 1024 * 1024;
                TransferProgress progress = uploadd.getProgress();
                upload = uploadd;
                float dataTransfered = progress.getBytesTransferred();
                System.out.println("Resume  " + dataTransfered);

                pausePlayWork = false;
                boolean exception = false;
                while (!uploadd.isDone()) {
                    try {
                        TrayIconUtils.uploadIconChange();
                        upload = uploadd;
                        if (!ProcessUtils.upload) {
                            uploadd.pause();
                            killThread();

                        }
                        pausePlayWork = true;
                        dataTransfered = progress.getBytesTransferred();
                        System.out.println("Data Transfer " + dataTransfered);
                        if (ProgressBarDia.progressSync.getValue() < (int) uploadd.getProgress().getPercentTransferred()) {
                            ProgressBarDia.progressSync.setValue((int) uploadd.getProgress().getPercentTransferred());
                        }
                    } catch (Exception ex) {
                        exception = true;
                        break;
                    }

                }
                fis.close();
                if (pausePlayWork && !exception) {
                    ProgressBarDia.fileNameUrl.setText("...............");
                    ProgressBarDia.textareamQueue.setText(url + " Completed task 100%\n" + ProgressBarDia.textareamQueue.getText());
                    arrayFile.add(new StackFile(url, dateFormat.format(currDate)));
                    try {
                        // fw = new FileWriter(new File(Initialize.logFileName));
                        fw = new FileWriter(Main.getLogFileName);
                        fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");
                        fw.append("| Full File Name                                                                                             | Date        |\n");
                        fw.append("+------------------------------------------------------------------------------------------------------------+-------------+\n");
                        Iterator<StackFile> itS = arrayFile.iterator();

                        while (itS.hasNext()) {
                            StackFile sf = itS.next();
                            fw.append("| " + String.format("%-110s", sf.getName()) + "| " + String.format("%-10s", sf.getDate()) + "  |\n");
                            fw.append((itS.hasNext())
                                    ? "|------------------------------------------------------------------------------------------------------------|-------------|\n"
                                    : "+------------------------------------------------------------------------------------------------------------+-------------+\n");

                        }

                        fw.close();
                    } catch (IOException e) {

                    }
                    TrayIconUtils.setApplicationIcon();
                    killThread();
                } else {
                    killThread();
                    UploadFile(fileBean1);
                }

            } catch (Exception ex) {
                System.out.println("i am in exception");
                ex.printStackTrace();
            }
        }
    }

    static class StackFile {

        private String ID;
        private String Name;
        private String Date;

        public String getID() {
            return ID;
        }

        public void setID(String iD) {
            ID = iD;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }

        public StackFile(String _name, String _date) {
            //ID = _ID;
            Name = _name;
            Date = _date;
        }
    }

}
