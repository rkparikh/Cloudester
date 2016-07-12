/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.bean;

import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * FileBean CLASS USE FOR GET AND SET FILE INFORMATION
 */
public class FileBean {

    final static Logger logger = Logger.getLogger(FileBean.class);
    private Integer bbb_id;
    private Integer id;
    private String file_name;
    private String name;
    private Long size;
    private Long created;
    private Long modified;
    private Integer version;
    private String folder_id;
    private String path;
    private String folder_path;
    private String s3_path;
    private Integer status = 2;
    private String full_file_path;
    private boolean isOld;
    private String bbb_id_inString;
    private Long oldSize;
    private boolean folder;
    private Integer emailStatus;
    private String parent_folder_id;
    public Long getOldSize() {
        return oldSize;
    }

    public void setOldSize(Long oldSize) {
        this.oldSize = oldSize;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public String getBbb_id_inString() {
        return bbb_id_inString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setBbb_id_inString(String bbb_id_inString) {
        this.bbb_id_inString = bbb_id_inString;
    }

    public boolean isIsOld() {
        return isOld;
    }

    public void setIsOld(boolean isOld) {
        this.isOld = isOld;
    }

    public String getFull_file_path() {
        return full_file_path;
    }

    public void setFull_file_path(String full_file_path) {
        this.full_file_path = full_file_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBbb_id() {
        return bbb_id;
    }

    public void setBbb_id(Integer bbb_id) {
        this.bbb_id = bbb_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolder_path() {
        return folder_path;
    }

    public void setFolder_path(String folder_path) {
        this.folder_path = folder_path;
    }

    public String getS3_path() {
        return s3_path;
    }

    public void setS3_path(String s3_path) {
        this.s3_path = s3_path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getParent_folder_id() {
        return parent_folder_id;
    }

    public void setParent_folder_id(String parent_folder_id) {
        this.parent_folder_id = parent_folder_id;
    }
    
}
