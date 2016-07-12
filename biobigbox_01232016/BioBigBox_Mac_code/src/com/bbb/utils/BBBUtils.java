/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import com.bbb.main.Main;
import com.bbb.ui.ProgressBarDia;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * BBBUtils CLASS USE FOR ALL COMMAN FUNCTIONS USE IN BBB APP
 */
public class BBBUtils {

    final static Logger logger = Logger.getLogger(BBBUtils.class);

    /**
     * netIsAvailable() CHECK METHOD
     */
    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://biobigbox.infinity-stores.co.uk/");
            final URLConnection conn = url.openConnection();
            conn.connect();
            logger.info("Network Connection true");
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Network Connection false " + e);
            return false;
        }
    }

    /**
     * getContentType(String fileName) METHOD FOR FILE
     */
    public static String getContentType(String fileName) {
        String contentType;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        if (extension.equalsIgnoreCase("PNG")) {
            contentType = "image/png";
        } else if (extension.equalsIgnoreCase("JPEG") || extension.equalsIgnoreCase("JPG")) {
            contentType = "image/jpeg";
        } else if (extension.equalsIgnoreCase("GIF")) {
            contentType = "image/gif";
        } else if (extension.equalsIgnoreCase("BMP")) {
            contentType = "image/bmp";
        } else if (extension.equalsIgnoreCase("TIFF")) {
            contentType = "image/tiff";
        } else if (extension.equalsIgnoreCase("RTF")) {
            contentType = "txt/rtf";
        } else if (extension.equalsIgnoreCase("txt")) {
            contentType = "txt/plain";
        } else if (extension.equalsIgnoreCase("PDF")) {
            contentType = "application/pdf";
        } else if (extension.equalsIgnoreCase("ZIP")) {
            contentType = "application/zip";
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            contentType = "application/msword";
        } else {
            contentType = "application/octet-stream";
        }

        return contentType;
    }

    /**
     * getFileSize(String fileUrl) METHOD USE FOR GET FILE SIZE
     */
    public static long getFileSize(String fileUrl) {
        long countInBytes = 0;
        try {
            File f = new File(fileUrl);
            countInBytes = f.length();
        } catch (Exception ex) {
            logger.error("getFileSize Error " + ex);
        }

        return countInBytes;
    }

    /**
     * getDateToLong(String dateString) METHOD USE FOR DATE TO LONG CONVERSION
     */
    public static long getDateToLong(String dateString) {
        long dateLong = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Date date = formatter.parse(dateString);
            dateLong = date.getTime();
        } catch (Exception e) {
            logger.error("getDateToLong Error " + e);
        }
        return dateLong;
    }

    /**
     * GetUniqueFilePath(String filefullpath, String filename, String path)
     */
    public static String GetUniqueFilePath(String filefullpath, String filename, String path) {
        int count = 0;
        String filenameandExtension[] = filename.split("\\.");
        do {
            count++;
            if (filenameandExtension.length == 1) {
                if ((new File(filefullpath)).exists()) {
                    filefullpath = path + filenameandExtension[0] + "(" + count + ")";
                } else {
                    break;
                }
            } else {
                if ((new File(filefullpath)).exists()) {
                    filefullpath = path + filenameandExtension[0] + "(" + count + ")." + filenameandExtension[1];
                } else {
                    break;
                }
            }

        } while (true);

        return filefullpath;
    }

    /**
     * getLastModified(String path)
     */
    public static long getLastModified(String path) {
        long time = 0;
        try {
            File file = new File(path);
            time = file.lastModified();
        } catch (Exception ex) {
            logger.error("getLastModified Error " + ex);
        }
        return time;
    }

    /**
     * getCreatedTime(String path)
     */
    public static long getCreatedTime(String path) {
        Path filePath = Paths.get(path);

        BasicFileAttributes attributes = null;
        try {
            attributes
                    = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (IOException exception) {
            logger.error("getCreatedTime Error " + exception);

        }
        long milliseconds = new Date().getTime();
        try {
            milliseconds = attributes.creationTime().to(TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }

        return milliseconds;
    }

    /**
     * getFileNameByUrl(String url)
     */
    public static String getFileNameByUrl(String url) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] splittedFileName = url.split(pattern);
        return splittedFileName[splittedFileName.length - 1];
    }

    /**
     * getPathInsideSourceFolder(String fullpath, String sourcePath, String
     * filename)
     */
    public static String getPathInsideSourceFolder(String fullpath, String sourcePath, String filename) {
        String subFolderpath = "";
        subFolderpath = fullpath.replace(sourcePath, "");
        subFolderpath = subFolderpath.replace(filename, "");
        subFolderpath = subFolderpath.replace("\\", "/");
        return subFolderpath;

    }

    /**
     * getCurrentPath() METHOD USE FOR GET APPLICATION CURRENT PATH
     */
    public static String getCurrentPath() {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation(); //Gets the path
        String jarPath = null;
        try {
            jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("getCurrentPath Error " + e);
        }

        String parentPath = new File(jarPath).getParentFile().getPath();
        parentPath = parentPath + File.separator;
        return parentPath;
    }

    /**
     * createDirectory(String url) METHOD IS USE FOR CREATE DIRECTORY BY URL
     */
    public static void createDirectory(String url) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] directory = url.split(pattern);
        String directoryStruc = "";
        for (int i = 0; i < directory.length; i++) {
            if (i == 0) {
                directoryStruc = directory[i];
            } else {
                directoryStruc += File.separator + directory[i];
            }
            File file = new File(directoryStruc);
            if (!file.exists()) {
                if (file.mkdir()) {

                } else {
                }
            }
        }
    }

    /**
     * highlight(String pattern) METHOD USE FOR HIGHLIGHT NOT PROCESSING FILE
     */
    public static void highlight(String pattern) {

        try {
            Highlighter hilite = ProgressBarDia.textareamQueue.getHighlighter();

            String text = ProgressBarDia.textareamQueue.getText();
            String line = null;
            int start = 0;
            int end;
            int totalLines = ProgressBarDia.textareamQueue.getLineCount();
            for (int i = 0; i < totalLines; i++) {

                if (i == 5) { //Line Numbers Decrement by 1
                    start = ProgressBarDia.textareamQueue.getLineStartOffset(i);
                    end = ProgressBarDia.textareamQueue.getLineEndOffset(i);
                    line = text.substring(start, end);
                }
            }
            int pos = start;
            Highlighter.HighlightPainter painter
                    = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
            // Search for pattern
            if ((pos = text.indexOf(pattern, pos)) >= start) {
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos + pattern.length(), painter);
                pos += pattern.length();
            }
        } catch (BadLocationException e) {
        }
    }
    
   
}
