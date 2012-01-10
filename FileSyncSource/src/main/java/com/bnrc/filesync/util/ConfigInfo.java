package com.bnrc.filesync.util;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ConfigInfo {
	
	private String hdfs_ip = null;
	private String hdfs_port = null;
	
	public ConfigInfo(String ip,String port){
		this.hdfs_ip = ip;
		this.hdfs_port = port;
	}
	
	
	public void setHDFS_IP(String ip){
		this.hdfs_ip = ip;
	}
	
	public void setHDFS_PORT(String port){
		this.hdfs_port = port;
	}
	
	public String getHDFS_IP(){
		return hdfs_ip;
	}
	
	public String getHDFS_PORT() {
		return hdfs_port;
	}
}
