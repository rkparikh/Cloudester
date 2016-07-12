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
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.internal.S3SyncProgressListener;
import com.bbb.bean.FileBean;
import com.bbb.dao.UpdateFilePath;
import com.bbb.init.Initialize;
import com.bbb.ui.ProgressBarDia;
import com.bbb.utils.BBBUtils;
import com.bbb.utils.ProcessUtils;
import com.bbb.utils.TrayIconUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
public class UploadFiles1 {

    final static Logger logger = Logger.getLogger(UploadFiles.class);
    public static String url = "";
    static int count = 0;
    static double totalByteRead = 0;
    static Upload upload = null;
    static PersistableUpload persistableUpload = null;
    static TransferManager tm = null;
    static PutObjectRequest request = null;
    static String file_name = "";

    public static boolean UploadFile(FileBean fileBean) {
        boolean status = false;
        url = fileBean.getFull_file_path();
        file_name = fileBean.getFile_name();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String filePath[] = fileBean.getFull_file_path().split(pattern);
        AWSCredentials credentials = new BasicAWSCredentials(Initialize.getACCESS_KEY(), Initialize.getSECRET_KEY());
        tm = new TransferManager(credentials);
        request = new PutObjectRequest(Initialize.existingBucketName, Initialize.keyName + filePath[filePath.length - 1], new File(fileBean.getFull_file_path()));
        upload();
        upload = tm.upload(request);

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
                return true;

            } else {
                UpdateFilePath.noFileStatus(fileBean);
                ProgressBarDia.fileNameUrl.setText("...............");
                ProgressBarDia.progressSync.setValue(0);
                ProgressBarDia.textareamQueue.setText(url + " Uncompleted \n" + ProgressBarDia.textareamQueue.getText());
                BBBUtils.highlight(url + " Uncompleted \n");
                String countLabel[] = ProgressBarDia.fileCountLabel.getText().split("/");
                int count = Integer.parseInt(countLabel[0]);
                count++;
                ProgressBarDia.fileCountLabel.setText(count + "/" + countLabel[1]);

                TrayIconUtils.setApplicationIcon();
                return false;
            }
        } catch (Exception amazonClientException) {
            amazonClientException.printStackTrace();
        }
        return status;
    }

    public static void upload() {
        request.setGeneralProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                if (!ProcessUtils.upload) {
                    try {
                        persistableUpload = upload.pause();
                    } catch (Exception ex) {
                        System.out.println("Exception in upload " + ex);
                    }
                } else {
                    // System.out.println("Running......");
                    if (!ProgressBarDia.fileNameUrl.getText().trim().equals(url)) {
                        ProgressBarDia.progressSync.setValue(0);
                        ProgressBarDia.progressSync.setMaximum(100);
                        ProgressBarDia.progressSync.repaint();
                    }

                    progressEvent.getBytesTransferred();
                    TrayIconUtils.uploadIconChange();
                    ProgressBarDia.progressSync.setValue((int) upload.getProgress().getPercentTransferred());
                    switch (progressEvent.getEventCode()) {
                        case ProgressEvent.COMPLETED_EVENT_CODE:
                            ProgressBarDia.fileNameUrl.setText("...............");
                            ProgressBarDia.textareamQueue.setText(url + " Completed task 100%\n" + ProgressBarDia.textareamQueue.getText());
                            TrayIconUtils.setApplicationIcon();
                            break;
                        case ProgressEvent.FAILED_EVENT_CODE:
                            try {
                            } catch (Exception e) {
                            }
                            break;
                    }
                }
            }
        }
        );
    }

    public static void pauseOperation() {
        try {
            long MB = 1024 * 1024;
            TransferProgress progress = upload.getProgress();
            System.out.println("The pause will occur once 5 MB of data is uploaded");
            while (progress.getBytesTransferred() < 5 * MB) {
                Thread.sleep(2000);
            }
            boolean forceCancel = true;
            float dataTransfered = (float) upload.getProgress().getBytesTransferred();
            System.out.println("Data Transfered until now: " + dataTransfered);
            PauseResult<PersistableUpload> pauseResult = ((Upload) upload).tryPause(forceCancel);
            System.out.println("The upload has been paused. The code that we've got is " + pauseResult.getPauseStatus());
            pauseResult = ((Upload) upload).tryPause(forceCancel);
            PersistableUpload persistableUpload = (PersistableUpload) pauseResult.getInfoToResume();
            System.out.println("Storing information into file");
            File f = new File("D:\\resume-upload");
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            persistableUpload.serialize(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void resumeOperation() {
        try {
            FileInputStream fis = new FileInputStream(new File("D:\\resume-upload"));
            System.out.println("Reading information from the file");
            Thread.sleep(20000);
            PersistableUpload persistableUpload;
            persistableUpload = PersistableTransfer.deserializeFrom(fis);
            System.out.println("Reading information completed");
            System.out.println("The system will resume upload now");
            System.out.println("Resume Output" + fis);
            Upload uploadd = tm.resumeUpload(persistableUpload);
            long MB = 1024 * 1024;
            TransferProgress progress = uploadd.getProgress();
            float dataTransfered = progress.getBytesTransferred();
            while (!uploadd.isDone()) {
                dataTransfered = progress.getBytesTransferred();
                System.out.println("Resume Data Transfered: " + dataTransfered / MB + " MB");
                Thread.sleep(2000);
            }
            fis.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
