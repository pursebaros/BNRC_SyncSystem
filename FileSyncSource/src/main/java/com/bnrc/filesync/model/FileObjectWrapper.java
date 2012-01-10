package com.bnrc.filesync.model;

import java.util.Hashtable;
import java.util.Properties;

public class FileObjectWrapper {
	
	
	/*file content*/
	private byte[] content;
	
	
	/*file properties*/
	protected Properties meta_data;
	
	
	public FileObjectWrapper(String file_name,String size,String type,byte[] content){
		
		meta_data = new Properties();
		
		meta_data.setProperty("name", file_name);
		meta_data.setProperty("size", size);
		meta_data.setProperty("type", type);
				
		this.content = new byte[content.length];
		for(int i=0;i<content.length;i++){
			this.content[i]=content[i];
		}
	}
	
	/*
	 * set methods:
	 */
	
	public void setFileContent(byte[] content){
		this.content = new byte[content.length];
		for(int i=0;i<content.length;i++){
			this.content[i]=content[i];
		}
	}
	
	public void setProperty(String name,String value){
		this.meta_data.setProperty(name, value);
	}
	
	/*
	 * get methods:
	 */
	
	
	
	public byte[] getFileContent(){
		return this.content;
	}
	
	public String getProperty(String property_name){
		return meta_data.getProperty(property_name);
	}
	
}
