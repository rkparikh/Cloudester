/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.webservice;

import com.amazonaws.util.IOUtils;
import com.bbb.bean.FileBean;
import com.bbb.dao.UpdateFilePath;
import com.bbb.dao.UpdateLogFileStatus;
import com.bbb.dao.UpdateLogin;
import com.bbb.init.Initialize;
import com.bbb.main.Main;
import com.bbb.ui.LoginPanel;
import static com.bbb.ui.LoginPanel.credentialError;
import com.bbb.ui.MainAppFrame;
import com.bbb.ui.ProgressBarDia;
import com.bbb.utils.BBBUtils;
import com.bbb.utils.TrayIconUtils;
import static com.bbb.utils.TrayIconUtils.appImageIcon;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jdesktop.swingx.border.DropShadowBorder;

/**
 *
 * @author Totaram
 */
/**
 * WebServices CLASS USE FOR HANDLE ALL WEBSERVICES OF
 * http://biobigbox.infinity-stores.co.uk/ SERVER
 */
public class WebServices {

    final static Logger logger = Logger.getLogger(WebServices.class);
    //public static boolean status = false;
    public static int mail = 0;
    public static Map<String, String> emailFileLogs = new HashMap();

    /**
     * loginCheck(String userName, String password) WESERVICE METHOD
     */
    public static boolean loginCheck(String userName, String password) {
        boolean loginStatus = false;
        try {
            System.out.println("....----------------Login method come.....................");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.webServiceURL);
            StringEntity input = new StringEntity("{\"username\":\"" + userName + "\",\"password\":\"" + password + "\"}");
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            System.out.println("response---------------------" + response);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                loginStatus = true;
                String output;
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("Json Object--------" + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        Initialize.setLoginToken(innerJsonObject.get("token").toString());
                    } else {
                        credentialError.setText("<html><p>" + jsonObject.get("message").toString() + "</p></html>");
//                        if (jsonObject.get("message").toString().contains("You have entered an invalid password. Please try again.")) {
//                            LoginPanel.forgot.setVisible(true);
//                        }
                        loginStatus = false;
                        break;
                    }

                }

            } else {
                loginStatus = false;
            }

        } catch (Exception e) {
            if (e.toString().contains("java.net.UnknownHostException")) {
                credentialError.setText("<html><p>Network Connection Error</p></html>");
            } else if (e.toString().contains("java.net.SocketException")) {
                credentialError.setText("<html><p>Error occur to connect Server</p></html>");
            } else if (e.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                credentialError.setText("<html><p>Error occur to connect Server</p></html>");
            }
            logger.error("WebServices loginCheck Error " + e);
        }
        return loginStatus;
    }

    /**
     * getAmazonKeys() GET AMAZON KEYS WEBSERVICE METHOD
     */
    public static void getAmazonKeys() {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.getKeyURL);
            StringEntity input = new StringEntity("{\"token\":\"" + Initialize.getLoginToken() + "\"}");
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        Initialize.setACCESS_KEY(innerJsonObject.get("aws_s3_access_key").toString());
                        Initialize.setSECRET_KEY(innerJsonObject.get("aws_s3_secret_key").toString());
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getAmazonKeys Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * getFileList() WEBSERVICE METHOD TO GET LIST OF FILES FOR DOWNLOAD
     */
    public static List<FileBean> getFileList() {
        List<FileBean> filesList = new ArrayList<FileBean>();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.getFilesList);
            StringBuilder stringBuilder = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"page\":1,\"limit\":10000}");
            System.out.println("File List " + stringBuilder.toString());
            StringEntity input = new StringEntity(stringBuilder.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        JSONArray jSONArray = (JSONArray) innerJsonObject.get("files");
                        System.out.println(jSONArray.size() + "File List Array " + jSONArray);
                        if (jSONArray != null && !jSONArray.equals("null")) {
                            for (int arrCont = 0; arrCont < jSONArray.size(); arrCont++) {
                                JSONObject jsonArrayObject = (JSONObject) jSONArray.get(arrCont);
                                /*SET DATA IN FILEBEAN*/
                                FileBean fileBean = new FileBean();
                                fileBean.setBbb_id((Integer.parseInt(jsonArrayObject.get("id").toString().trim())));
                                fileBean.setFolder_id((String) jsonArrayObject.get("folder_id"));
                                fileBean.setFolder_path((String) jsonArrayObject.get("folder_path"));
                                byte[] get_s3_encoded_path = org.apache.commons.codec.binary.Base64.decodeBase64((String) jsonArrayObject.get("s3_encoded_path"));
                                fileBean.setS3_path(new String(get_s3_encoded_path));
                                fileBean.setCreated(BBBUtils.getDateToLong(jsonArrayObject.get("created") + ""));
                                fileBean.setFile_name((String) jsonArrayObject.get("filename"));
                                fileBean.setVersion((Integer.parseInt(jsonArrayObject.get("version").toString().trim())));
                                fileBean.setSize((Long.parseLong(jsonArrayObject.get("size").toString().trim())));
                                filesList.add(fileBean);

                            }
                        } else {
                            return filesList;
                        }
                    }
                }

            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("WebServices getFileList Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return filesList;
    }

    /**
     * getFilesAndFolderList() WEBSERVICE METHOD TO GET FILES AND FOLDER LIST
     * FOR DOWNLOAD
     */
    public static List<FileBean> getFilesAndFolderList(String folder_id) {
        List<FileBean> filesList = new ArrayList<FileBean>();
        String getServerDate = getDateTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            //Date dateTime = new SimpleDateFormat("EEE hh:mma MMM d, yyyy").parse(getServerDate);
            Date currDate = dateFormat.parse(getServerDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
//            DateFormat timeFormatter = new SimpleDateFormat("hh:mma");
            //Date currDate = dateFormat.parse(getServerDate);
            System.out.println("get server date:" + dateFormat.format(currDate));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.getFilesAndFolders);
            StringBuilder stringBuilder;
            if (folder_id.equals("")) {
                stringBuilder = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"date_time\":\"" + dateFormat.format(currDate) + "\",\"folder_id\":0,\"type\":3}");

            } else {
                stringBuilder = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"date_time\":\"" + dateFormat.format(currDate) + "\",\"folder_id\":" + folder_id + ",\"type\":3}");

            }
            System.out.println("File Folder List " + stringBuilder.toString());
            StringEntity input = new StringEntity(stringBuilder.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        JSONArray jsonArrayFiles = (JSONArray) innerJsonObject.get("files");
                        JSONArray jsonArrayFilesAndFolders = (JSONArray) innerJsonObject.get("folders");
                        System.out.println(jsonArrayFiles.size() + "File List Array " + jsonArrayFiles);

                        if (jsonArrayFilesAndFolders != null) {
                            System.out.println("inside filesandfolder : " + jsonArrayFilesAndFolders.size());
                            for (int arrCont = 0; arrCont < jsonArrayFilesAndFolders.size(); arrCont++) {
                                JSONObject jsonArrayObject = (JSONObject) jsonArrayFilesAndFolders.get(arrCont);
                                FileBean downloadFileBean = new FileBean();
                                downloadFileBean.setId((Integer.parseInt(jsonArrayObject.get("id").toString().trim())));
                                downloadFileBean.setFile_name((String) jsonArrayObject.get("name"));
                                downloadFileBean.setSize((Long.parseLong(jsonArrayObject.get("size").toString().trim())));
                                downloadFileBean.setCreated(BBBUtils.getDateToLong(jsonArrayObject.get("date_created") + ""));
                                downloadFileBean.setFolder(true);
                                filesList.add(downloadFileBean);

                            }
                        }
                        if (jsonArrayFiles != null) {
                            for (int arrCont = 0; arrCont < jsonArrayFiles.size(); arrCont++) {
                                JSONObject jsonArrayObject = (JSONObject) jsonArrayFiles.get(arrCont);
                                /*SET DATA IN FILEBEAN*/
                                FileBean downloadFileBean = new FileBean();
                                downloadFileBean.setBbb_id((Integer.parseInt(jsonArrayObject.get("id").toString().trim())));
                                downloadFileBean.setFolder_id((String) jsonArrayObject.get("folder_id"));
                                downloadFileBean.setFolder_path((String) jsonArrayObject.get("folder_path"));
                                byte[] get_s3_encoded_path = org.apache.commons.codec.binary.Base64.decodeBase64((String) jsonArrayObject.get("s3_encoded_path"));
                                downloadFileBean.setS3_path(new String(get_s3_encoded_path));
                                downloadFileBean.setCreated(BBBUtils.getDateToLong(jsonArrayObject.get("created") + ""));
                                downloadFileBean.setFile_name((String) jsonArrayObject.get("filename"));
                                downloadFileBean.setVersion((Integer.parseInt(jsonArrayObject.get("version").toString().trim())));
                                downloadFileBean.setSize((Long.parseLong(jsonArrayObject.get("size").toString().trim())));
                                downloadFileBean.setFolder(false);
                                filesList.add(downloadFileBean);

                            }
                        }

                        return filesList;

                    }
                }

            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            logger.error("WebServices getFileAndFolderList Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return filesList;
    }

    /**
     * getUploadUrl(String url, FileBean filebean) GET UPLOAD URL WEBSERVICE
     * METHOD
     */
    public static boolean getUploadUrl(final String url, final FileBean filebean) {
        boolean status = false;
        try {
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String filePath[] = url.split(pattern);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.uploadURL);
            StringEntity input = null;
            byte[] bytesEncoded = org.apache.commons.codec.binary.Base64.encodeBase64(filePath[filePath.length - 1].getBytes());
            //String s1=(String)System.getProperties().setProperty("file.encoding","UTF-8");
            if (filebean.isIsOld()) {

                StringBuffer stBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"" + BBBUtils.getFileSize(url) + "\",\"is_old\":" + filebean.isIsOld() + ",\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + new String(bytesEncoded) + "\"}");
                System.out.println(" st Buufer " + stBuffer);
                //logger.error(" st Buufer " + stBuffer);
                // StringBuffer stBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"54545445666666656456456454\",\"is_old\":" + filebean.isIsOld() + ",\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                // System.out.println("Request Upload Json " + stBuffer.toString());
                input = new StringEntity(stBuffer.toString());
            } else {
                StringBuffer stringBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"" + BBBUtils.getFileSize(url) + "\",\"is_old\":0,\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + new String(bytesEncoded) + "\"}");
                //StringBuffer stringBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"45555654564554564564564564\",\"is_old\":0,\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                //  System.out.println("Request Upload Json " + stringBuffer.toString());
                input = new StringEntity(stringBuffer.toString());
                System.out.println(" st Buufer " + stringBuffer);
                //logger.error(" st Buufer " + stringBuffer);

            }
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    final JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        status = true;
                        // JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                    } else {

                        int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, jsonObject.get("message").toString(), "Error",
                                JOptionPane.ERROR_MESSAGE, 1, TrayIconUtils.errImageIcon);
                        logger.error("WebServices getUploadUrl Error " + jsonObject);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            System.out.println("i am in yes");
                            try {

                                Desktop.getDesktop().browse(new URL("https://biobigbox.com/#/page/pricing").toURI());

//                                class MyTimerTask extends TimerTask {
//                                     Timer timer = new Timer(true);
//                                    
//                                    @Override
//                                    public void run() {
//                                        System.out.println("Timer task started at:" + new Date());
//                                        completeTask();
//                                        System.out.println("Timer task finished at:" + new Date());
//                                    }
//
//                                    private void completeTask() {
//                                        try {
//                                           boolean getstatus=getUploadUrlForPackageUpgradetion(url, filebean);
//                                                System.out.println("get Status:"+getstatus);
//                                              if(getstatus==true){
//                                                  status=true;
//                                                  System.out.println("get status true");
//                                              }
//                                              else{
//                                                  System.out.println("");
//                                              }
//                                            Thread.sleep(20000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//
//                                TimerTask timerTask = new MyTimerTask();
//                                //running timer task as daemon thread
//                                Timer timer = new Timer(true);
//                                timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
//                                System.out.println("TimerTask started");
//
//                                //cancel after sometime
//                                try {
//                                    Thread.sleep(425000);
//
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                timer.cancel();
//                                System.out.println("TimerTask cancelled");
//
//                                UpdateFilePath.noFileStatus(filebean);
//                                logger.info("Account");
                                long start = System.currentTimeMillis();
                                System.out.println("started at" + start);
                                long end = start + 60 * 1000 * 15; // 60 seconds * 1000 ms/sec
                                while (System.currentTimeMillis() < end) {
                                    try {
                                        Thread.sleep(10000);
                                        if (getUploadUrlForPackageUpgradetion(url, filebean) == true) {
                                            System.out.println("in true option for status");
                                            status = true;
                                            break;
                                        } else {
                                            continue;
                                        }
                                    } catch (Exception e) {

                                    }

                                }
                                UpdateFilePath.noFileStatus(filebean);
                            } catch (Exception ex) {
                                logger.error("Account  :" + ex);
                            }

                        } else {
                            System.out.println("i am in else");
                            UpdateFilePath.noFileStatus(filebean);
                            status = false;
                        }
                        logger.error("WebServices getUploadUrl Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadUrl Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ex.printStackTrace();

        }
        return status;
    }

    /**
     * getUploadUrlForPackageUpgradetion(String url, FileBean filebean) GET
     * UPLOAD URL FOR PACKAGE UPGRADETION WEBSERVICE METHOD
     */
    public static boolean getUploadUrlForPackageUpgradetion(String url, FileBean filebean) {
        boolean status = false;
        try {
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String filePath[] = url.split(pattern);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.uploadURL);
            StringEntity input = null;
            if (filebean.isIsOld()) {
                StringBuffer stBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"" + BBBUtils.getFileSize(url) + "\",\"is_old\":" + filebean.isIsOld() + ",\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                System.out.println(" st Buufer " + stBuffer);
                // StringBuffer stBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"54545445666666656456456454\",\"is_old\":" + filebean.isIsOld() + ",\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                // System.out.println("Request Upload Json " + stBuffer.toString());
                input = new StringEntity(stBuffer.toString());
            } else {
                StringBuffer stringBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"" + BBBUtils.getFileSize(url) + "\",\"is_old\":0,\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                //StringBuffer stringBuffer = new StringBuffer("{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"45555654564554564564564564\",\"is_old\":0,\"old_size\":" + filebean.getOldSize() + ",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + filePath[filePath.length - 1] + "\"}");
                //  System.out.println("Request Upload Json " + stringBuffer.toString());
                input = new StringEntity(stringBuffer.toString());
                System.out.println(" st Buufer " + stringBuffer);

            }
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    final JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        status = true;
                        // JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadUrlForPackageUpgradetion Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            //ex.printStackTrace();

        }
        return status;
    }

    /**
     * getUploadFileID(String url, FileBean filebean) WEBSERVICE METHOD USE FOR
     * GET FILE ID AFTER UPLOAD FILE
     */
    public static FileBean getUploadFileID(String url, FileBean filebean) {
        int fileid = 0;

        FileBean fileBean1 = new FileBean();
        try {
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String filePath[] = url.split(pattern);
            HttpClient httpClient = new DefaultHttpClient();
            String folder_path = url.replace(Initialize.getDefaultDirectory(), "");
            folder_path = folder_path.replace("\\", "/");
            folder_path = folder_path.replace("/" + filebean.getFile_name(), "");
            folder_path = folder_path.equals("") ? folder_path : folder_path + "/";
            folder_path = folder_path.replaceFirst("/", "");
            String directory = BBBUtils.getFileNameByUrl(Initialize.getDefaultDirectory());
            HttpPost postRequest = new HttpPost(Initialize.getUploadFileID);
            byte[] bytesEncoded = org.apache.commons.codec.binary.Base64.encodeBase64(filePath[filePath.length - 1].getBytes());
            System.out.println("ecncoded value is " + new String(bytesEncoded));
            String uploadJson = "{\"token\":\"" + Initialize.getLoginToken() + "\",\"size\":\"" + BBBUtils.getFileSize(url) + "\",\"type\":\"" + BBBUtils.getContentType(filePath[filePath.length - 1]) + "\",\"name\":\"" + new String(bytesEncoded) + "\",\"encoded_name\":\"" + new String(bytesEncoded) + "\",\"file_id\":" + filebean.getBbb_id_inString() + ",\"folder_path\":\"" + directory.replaceAll("'", "\'") + "/" + folder_path.replaceAll("'", "`") + "\"}";
            System.out.println("Upload Url " + uploadJson);
            //logger.error("Upload Url " + uploadJson);
            StringEntity input = new StringEntity(uploadJson);
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
//                    String code=jsonObject.get("code").toString();
//                    String getSuccess=jsonObject.get("success").toString();
                    System.out.println("return JSon " + jsonObject);
                    //  logger.error("return JSon " + jsonObject);
                    JSONObject innerJsonObject = null;
                    String folder_idForFailer = null;
                    if ((boolean) jsonObject.get("success")) {
                        innerJsonObject = (JSONObject) jsonObject.get("result");
                        if (jsonObject.get("success").toString().equals("true")) {
                            folder_idForFailer = innerJsonObject.get("folder_id").toString();

                        }

                        fileBean1.setBbb_id(Integer.parseInt(innerJsonObject.get("file_id").toString()));
                        fileBean1.setFolder_id((innerJsonObject.get("folder_id").toString()));
                        fileBean1.setParent_folder_id(innerJsonObject.get("parent_id").toString());
                        System.out.println("parent id is:" + innerJsonObject.get("parent_id").toString());


                    } else if (jsonObject.get("code").toString().equals("400") && jsonObject.get("success").toString().equals("false")) {
                        // System.out.println("in else if file " + jsonObject.get("code").toString() + "--" + jsonObject.get("success").toString());
                        logger.error("WebServices getUploadFileID" + jsonObject.get("message").toString() + " Error for: " + filePath[filePath.length - 1]);
                       

                        fileBean1.setBbb_id(50000000);
                        fileBean1.setFolder_id(folder_idForFailer);

                    } else if (jsonObject.get("code").toString().equals("404") && jsonObject.get("success").toString().equals("false")) {
                        System.out.println("in else if 2 " + jsonObject.get("code").toString() + "--" + jsonObject.get("success").toString());
                        logger.error("WebServices getUploadFileID " + jsonObject.get("message").toString() + " Error for:" + filePath[filePath.length - 1]);
                 
                        fileBean1.setBbb_id(50000000);
                        fileBean1.setFolder_id(folder_idForFailer);

                    }else if (jsonObject.get("code").toString().equals("500") && jsonObject.get("success").toString().equals("false")) {
                        System.out.println("in else if 2 " + jsonObject.get("code").toString() + "--" + jsonObject.get("success").toString());
                        logger.error("WebServices getUploadFileID " + jsonObject.get("message").toString() + " Error for:" + filePath[filePath.length - 1]);
                 
                        fileBean1.setBbb_id(50000000);
                        fileBean1.setFolder_id(folder_idForFailer);

                    } 
                    else {
                        logger.error("WebServices getUploadFileID Error " + jsonObject);
                    }
                }

            }

        } catch (Exception ex) {
            logger.error("WebServices getUploadFileID Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println("error loag count" + mail);
//            if(mail>0)
//            {
//                StringBuffer st=new StringBuffer();
//               
//                for (Map.Entry<String, String> entry : emailFileLogs.entrySet())
//                {
//                    st.append(entry.getValue()+" Error for: "+entry.getKey()+"\n");
//                    //System.out.println(entry.getKey() + "/" + entry.getValue());
//                }
//                System.out.println("string buffer"+st);
//              //sendLogs("WebServices getUploadFileID "+jsonObject.get("message").toString()+" Error for:" + filePath[filePath.length - 1] + " File");  
//            }
        return fileBean1;
    }

    /**
     * deleteFile(FileBean filebean) DELETE FILE FROM SERVER WEBSERVICE METHOD
     */
    public static boolean deleteFile(FileBean filebean) {
        boolean result = false;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.deleteFileURL);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"files\":\"" + filebean.getBbb_id().toString() + "\"}");
            System.out.println("delete Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("delete Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
//                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
//                        Integer.parseInt(innerJsonObject.get("result").toString());
                        logger.info("WebServices deleteFile Error " + jsonObject);
                    } else {
                        logger.error("WebServices deleteFile Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadFileID Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * deleteFolder(FileBean filebean) DELETE FOLDER FROM SERVER WEBSERVICE
     * METHOD
     */
    public static boolean deleteFolder(FileBean filebean) {
        boolean result = false;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.deleteFolderURL);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"folder_id\":\"" + filebean.getFolder_id() + "\"}");
            System.out.println("delete Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("delete Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
//                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
//                        Integer.parseInt(innerJsonObject.get("result").toString());
                        logger.info("WebServices deleteFile Error " + jsonObject);
                    } else {
                        logger.error("WebServices deleteFile Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadFileID Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * updateSyncRequest() Update sync request FROM SERVER WEBSERVICE METHOD
     */
    public static boolean updateSyncRequest() {
        boolean result = false;
        Date currDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.updateSyncRequest);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"date_time\":\"" + dateFormat.format(currDate) + "\"}");
            System.out.println("update sync Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("update sync Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
//                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
//                        Integer.parseInt(innerJsonObject.get("result").toString());
                        logger.info("WebServices updateSyncFile Error " + jsonObject);
                    } else {
                        if (jsonObject.get("message").toString().equals("API Token is not valid.")) {

                            //JOptionPane.showMessageDialog(null, "API Token is not valid. Please login AGAIN.", "Error", JOptionPane.ERROR_MESSAGE);
                            UpdateLogin.logOutUser();
                            Initialize.setLogin_id(0);
                            System.out.println("try icon set");
                            TrayIconUtils.setApplicationIcon();
                            System.out.println("try icon set done");
                            MainAppFrame.progressDialog.dispose();
                            Main.mainAppFrame.setVisible(true);
                            // mainAppFrame.setExtendedState(mainAppFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                            TrayIconUtils.setApplicationIcon();
                            TrayIconUtils.processUtils.stop();
                            TrayIconUtils.setApplicationIcon();
                            TrayIconUtils.processUtils = null;
                            ProgressBarDia.fileNameUrl.setText("");
                            ProgressBarDia.progressSync.setValue(0);
                            ProgressBarDia.textareamQueue.setText("");
                            // SyncPanel.directorySelectLabel.setText("");
                            LoginPanel.userName.setText("");
                            LoginPanel.password.setText("");
                            Initialize.setDefaultDirectory("");
                            CardLayout cardLayout = (CardLayout) Main.mainAppFrame.getContentPane().getLayout();
                            cardLayout.show(Main.mainAppFrame.getContentPane(), "loginPanel");
                            TrayIconUtils.trayIcon("SignWithoutAdd");
                            TrayIconUtils.setApplicationIcon();
                            break;

                        }
                        logger.error("WebServices updateSyncFile Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadFileID Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * updateFolderSync() Update Folder Sync request FROM SERVER WEBSERVICE
     * METHOD
     */
    public static boolean updateFolderSync(Integer is_folder_sync, String folder_id) {
        boolean result = false;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.updateFolderSync);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"is_folder_sync\":\"" + is_folder_sync + "\",\"fid\":\"" + folder_id + "\"}");
            System.out.println("update folder sync Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("update folder sync Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
//                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
//                        Integer.parseInt(innerJsonObject.get("result").toString());
                        logger.info("WebServices updateSyncFile Error " + jsonObject);
                    } else {
                        logger.error("WebServices updateSyncFile Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUploadFileID Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * getDateTime() GET DATE AND TIME FROM SERVER WEBSERVICE METHOD
     */
    public static String getDateTime() {
        String getServerDate = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.getDateTime);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\"}");
            System.out.println("get date and time  Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("get date and time Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        getServerDate = innerJsonObject.get("date_time").toString();
                        logger.info("WebServices getDateTime Error " + jsonObject);
                    } else {
                        logger.error("WebServices getDateTime Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getDateTime Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return getServerDate;
    }

    /**
     * sendLogs() sends the error message for failed files to upload on SERVER
     * WEBSERVICE METHOD
     */
    public static boolean sendLogs(String logs) {
        boolean result = false;
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bbbappversion");
        String version = bundle.getString("bbb.app.version");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osNameAndVersion = "OS Name:" + osName + " - OS Version:" + osVersion;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.sendLogs);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"logs\":\"" + logs + "\",\"version\":\"" + version + "\",\"osdetails\":\"" + osNameAndVersion + "\"}");
            System.out.println("Error Logs Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("Error logs Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
//                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
//                        Integer.parseInt(innerJsonObject.get("result").toString());
                        logger.info("WebServices sendLogs Error " + jsonObject);
                    } else {
                        logger.error("WebServices sendLogs Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices sendLogs Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * getFilesAndFolderList() WEBSERVICE METHOD TO GET FILES AND FOLDER LIST
     * FOR DOWNLOAD
     */
    public static String getDeletedFilesAndFolders() {
        String deletedFileIds = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.getDeletedFilesAndFolders);
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\",\"seconds\":3600}");
            System.out.println("Deleted Files & Folders List " + stringBuilder.toString());
            StringEntity input = new StringEntity(stringBuilder.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    if ((boolean) jsonObject.get("success")) {
                        JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        JSONArray jsonArrayFiles = (JSONArray) innerJsonObject.get("files");
                        //JSONArray jsonArrayFolders = (JSONArray) innerJsonObject.get("folders");
                        System.out.println(jsonArrayFiles.size() + "File List Array " + jsonArrayFiles);

                        if (jsonArrayFiles != null) {
                            for (int arrCont = 0; arrCont < jsonArrayFiles.size(); arrCont++) {
                                JSONObject jsonArrayObject = (JSONObject) jsonArrayFiles.get(arrCont);
                                /*SET DATA IN FILEBEAN*/
                                if (deletedFileIds.equals("")) {
                                    deletedFileIds += jsonArrayObject.get("id").toString().trim();
                                } else {
                                    deletedFileIds += "," + jsonArrayObject.get("id").toString().trim();
                                }

                            }
                        }
//                        if (jsonArrayFolders != null) {
//                            System.out.println("inside filesandfolder : " + jsonArrayFolders.size());
//                            for (int arrCont = 0; arrCont < jsonArrayFolders.size(); arrCont++) {
//                                JSONObject jsonArrayObject = (JSONObject) jsonArrayFolders.get(arrCont);
//                               if(deleted)
//
//                            }
//                        }

                        return deletedFileIds;

                    } else {
                        logger.error("WebServices getDeletedFilesAndFolders Error " + jsonObject);
                    }
                }

            }

        } catch (Exception ex) {
            logger.error("WebServices getDeletedFilesAndFolders Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return deletedFileIds;
    }

    /**
     * sendLogsEmail() SENDS ERROR LOGS FOR SUCCESSFULLT UPLOADED FILES ON
     * SERVER WEBSERVICE METHOD
     */
    public static boolean sendLogsEmail(File uploadFile) {
        boolean result = false;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Initialize.sendLogsEmail);
            FileBody fileBody = new FileBody(uploadFile, "application/octet-stream");
            System.out.println("filebody : " + fileBody);
            StringBody jsonBody = new StringBody("{\"token\":\"" + Initialize.getLoginToken() + "\"}");
            // System.out.println("json value"+input.toString());
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("logfile", fileBody);
            multipartEntity.addPart("json", jsonBody);
            httppost.setEntity(multipartEntity);
            System.out.println("multipart entity" + multipartEntity.toString());
            System.out.println("executing request " + httppost.getAllHeaders());
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
            String result1 = IOUtils.toString(responseEntity.getContent());
            System.out.println("result for sendLogsEmail upload:" + result1);
            UpdateLogFileStatus.updateLogFileStatus();
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("sendLogsEmail Responed Json " + jsonObject);
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
                        UpdateLogFileStatus.updateLogFileStatus();
                        logger.info("WebServices sendLogsEmail Error " + jsonObject);
                    } else {
                        //InsertEmailStatus.insertEmailStatus(Initialize.getUsername(), 0, Initialize.getLoginToken(), uploadFile.getPath());
                        logger.error("WebServices sendLogsEmail Error " + jsonObject);
                    }
                }

            }
        } catch (Exception e) {

            logger.error("WebServices sendLogsEmail Error", e);
            if (e.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;

    }

    /**
     * checkUpdatedVersion() WILL CHECK FOR LATEST VERSION OF BIOBIGBOX
     * APPLICATION ON SERVER WEBSERVICE METHOD
     */
    public static boolean checkUpdatedVersion() {
        boolean result = false;
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bbbappversion");
        String version = bundle.getString("bbb.app.version");
        String getVersionNo[] = version.split(" ");

        System.out.println("get version no:" + getVersionNo[1]);
        //float versionNo = Float.parseFloat(getVersionNo[2]);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.checkUpdatedVersion);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\"}");
            System.out.println("Error Logs Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("Error checkUpdatedVersion Responed Json " + jsonObject);
                    // null;
                    if ((boolean) jsonObject.get("success")) {
                        result = true;
                        final JSONObject innerJsonObject = (JSONObject) jsonObject.get("result");
                        if (!innerJsonObject.get("version").toString().equals(getVersionNo[1])) {
                            int dialogResult = JOptionPane.showConfirmDialog(Initialize.mainAppFrame, "New Update is available, with resolved bug. \nDo you want to continue?", "BioBigBox Setup - Welcome",
                                    JOptionPane.ERROR_MESSAGE, 1, TrayIconUtils.appImageIcon);
                            logger.error("WebServices getUploadUrl Error " + jsonObject);
                            if (dialogResult == JOptionPane.YES_OPTION) {
                                JOptionPane.showMessageDialog(null, "Please wait...Your new version of BioBigBox is in progress.\nOnce it will be complete, It will run automatically.  ", "BBB Updation", JOptionPane.INFORMATION_MESSAGE, appImageIcon);
                                System.out.println("app url:" + innerJsonObject.get("app_url").toString());
                                int bytesRead = -1;
//                                final JProgressBar pbFile = new JProgressBar();
//                                pbFile.setBounds(10, 10, 400, 20);
//                                JLabel title = new JLabel("BioBigBox Version Upgrade");
//                                title.setSize(600,20);
//                                pbFile.setValue(0);
//                                pbFile.setMaximum(100);
//                                pbFile.setStringPainted(true);
//                                pbFile.setSize(500, 30);
//                                pbFile.setBounds(20, 60, 500, 20);
//                          
//                                pbFile.setBorder(BorderFactory.createTitledBorder("Download file"));
//                          final JFrame theFrame = new JFrame("ProgressBar Demo");
//                                theFrame.setUndecorated(true);                            

//                                title.setFont(title.getFont().deriveFont(13.0f));                                                         
//                                Container contentPane = theFrame.getContentPane();
//                                contentPane.add(title,BorderLayout.NORTH);                        
//                                contentPane.add(pbFile, BorderLayout.SOUTH);
                                //final JButton btnDownload = new JButton("Download");
                                // contentPane.add(btnDownload);
                                try {
                                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                                } catch (Exception e) {

                                }
                                final JFrame theFrame = new JFrame("JProgressBar Sample");
                                theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                theFrame.setUndecorated(true);
                                Container content = theFrame.getContentPane();
                                final JLabel label = new JLabel("BioBigBox Update");
                                label.setBounds(1, 1, 200, 20);
                                final JProgressBar pbFile = new JProgressBar();
                                pbFile.setValue(0);
                                pbFile.setMaximum(100);
                                UIManager.put("nimbusBlue", new Color(51, 153, 255));
                                pbFile.setStringPainted(true);
                                Border border = BorderFactory.createTitledBorder("Downloading...");
                                pbFile.setBorder(border);
                                DropShadowBorder b = new DropShadowBorder(Color.BLUE, 10, 0.2f, 10, true, true, true, true);
                                theFrame.getRootPane().setBorder(b);
//                                pan.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.blue));
//                                pan.add(label, FlowLayout.LEFT);
//                                pan.add(pbFile, BorderLayout.SOUTH);
                                content.add(label);
                                content.add(pbFile, BorderLayout.SOUTH);

                                final AtomicBoolean running = new AtomicBoolean(false);
                                // btnDownload.addActionListener(new ActionListener() {
                                //  public void actionPerformed(ActionEvent e) {
                                running.set(!running.get());
                                //  btnDownload.setText(running.get() ? "Pause" : "Continue");
                                System.out.println("inside action");
                                new Thread() {
                                    public void run() {
                                        try {
                                            URL url = new URL(innerJsonObject.get("app_url").toString());
                                            URLConnection conn = url.openConnection();
                                            long filesize = conn.getContentLength();
                                            InputStream inputStream = conn.getInputStream();
                                            long startTime = System.currentTimeMillis();
                                            System.out.println("Size of the file to download in kb is:-" + filesize / 1024);
                                            FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.home") + File.separator + "BBBApp.dmg");
                                            byte[] buffer = new byte[(int) filesize];
                                            //download file in a thread
                                            int v = -1;
                                            long downloadedFileSize = 0;
                                            while (running.get() && (v = inputStream.read(buffer)) != -1) {
                                                outputStream.write(buffer, 0, v);
                                                downloadedFileSize += v;
                                                int currentProgress = (int) ((((double) downloadedFileSize) / ((double) filesize)) * 100d);
                                                System.out.println("get value for progress:" + currentProgress);
                                                pbFile.setValue(currentProgress);
                                                System.out.println("get value:" + pbFile.getValue());
                                                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
                                            }
                                            theFrame.dispose();
                                            outputStream.close();
                                            outputStream.flush();
                                            inputStream.close();
                                            long endTime = System.currentTimeMillis();
                                            System.out.println("File downloaded");
                                            System.out.println("Download time in sec. is:-" + (endTime - startTime) / 1000);
                                            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", System.getProperty("user.home") + File.separator + "BBBApp.dmg"});
                                        } catch (IOException ex) {

                                        }
                                    }
                                }.start();
                                // }
                                // }
                                // );
                                theFrame.setBounds(350, 300, 500, 90);
//                              f.setSize(500, 70);
                                theFrame.setVisible(true);
//                                theFrame.setSize(600, 100);
                                theFrame.setBackground(Color.white);
//                                theFrame.setLocationRelativeTo(null);
//                                theFrame.setVisible(true);

                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "Your current version is up to date", "BBB Updation", JOptionPane.INFORMATION_MESSAGE, appImageIcon);
                        }
                        logger.info("WebServices checkUpdatedVersion Error " + jsonObject);
                    } else {
                        logger.error("WebServices checkUpdatedVersion Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices checkUpdatedVersion Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * getUpdatedVersion() GET LATEST VERSION OF BIOBIGBOX APPLICATION ON SERVER
     * WEBSERVICE METHOD
     */
    public static boolean getUpdatedVersion() {
        boolean result = false;
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bbbappversion");
        String version = bundle.getString("bbb.app.version");
        String getVersionNo[] = version.split(" ");
        System.out.println("get version no:" + getVersionNo[1]);
        // float versionNo = Float.parseFloat(getVersionNo[2]);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Initialize.checkUpdatedVersion);
            StringBuilder sb = new StringBuilder("{\"token\":\"" + Initialize.getLoginToken() + "\"}");
            System.out.println("Error Logs Json" + sb.toString());
            StringEntity input = new StringEntity(sb.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                while ((output = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println("Error getUpdatedVersion Responed Json " + jsonObject);
                    JSONObject innerJsonObject = null;
                    if ((boolean) jsonObject.get("success")) {

                        innerJsonObject = (JSONObject) jsonObject.get("result");
                        if (!innerJsonObject.get("version").toString().equals(getVersionNo[1])) {
                            result = true;
                        }
                        logger.info("WebServices getUpdatedVersion Error " + jsonObject);
                    } else {
                        logger.error("WebServices getUpdatedVersion Error " + jsonObject);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("WebServices getUpdatedVersion Error " + ex);
            if (ex.toString().contains("java.net.ConnectException")) {
                JOptionPane.showMessageDialog(null, "You have very slow connection, So you can face some issues in this process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }
//    public static void main(String args[]) {
//        String ACCESS_KEY = "AKIAIIC3UQW4R7ODLLCQ";
//        String SECRET_KEY = "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";
//////
//        Initialize.setLoginToken("BBB-5564150fa52146.38417236");
//        Initialize.setACCESS_KEY(ACCESS_KEY);
//        Initialize.setSECRET_KEY(SECRET_KEY);
//        Initialize.setDefaultDirectory("E:\\Project\\BBB\\test");
//      //  getUploadUrl("D:\\sync2\\files\\0\\0\\1\\abhi.jpg");
//////        boolean status = UploadFiles.UploadFile("D:\\sync2\\files\\0\\0\\1\\abhi.jpg");
//////        System.out.println("Status :" + status);
//////        getUploadFileID("D:\\sync2\\files\\0\\0\\1\\abhi.jpg");
//        List<FileBean> filesList = getFileList();
//       DownloadFiles.downLoadFromAmazon(filesList);
//    }
}
