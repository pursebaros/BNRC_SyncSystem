package com.bnrc.filesync.model;

/*
 * represents file meta information. e.g. file name, last modified timestamp, status,file path.etc. 
 */

public class FileMetaInfo {
	
	private String fileName = null;
	private String fileType = null;
	private String last_timestamp = null;
	private String guid = null;
	private String userid = null;
	private String file_path = null;
	private String status;
	
	public FileMetaInfo(long guid,String userid,String file_path,String status,long last_timestamp){
		this.guid = String.valueOf(guid);
		this.userid = userid;
		this.file_path = file_path;
		this.fileName = file_path.substring(file_path.indexOf("/")+1);
		this.fileType = file_path.substring(file_path.lastIndexOf(".")+1);
		this.last_timestamp = String.valueOf(last_timestamp);
		this.status = status;
		
	}
	
	//getter:
	public String getFileName(){
		return fileName;
	}
	
	public String getFileType(){
		return fileType;
	}
	
	public String getLast_timestamp(){
		return last_timestamp;
	}
	
	public String getGuid(){
		return guid;
	}
	
	public String getUserid(){
		return userid;
	}
	
	public String getFile_path(){
		return file_path;
	}
	
	public String getStatus(){
		return status;
	}

}
