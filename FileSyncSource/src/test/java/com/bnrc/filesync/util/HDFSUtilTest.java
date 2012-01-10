package com.bnrc.filesync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.jsp.jstl.core.Config;

import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import com.bnrc.filesync.exception.HDFSException;


public class HDFSUtilTest {
	
	private FileSystem fs = null;
	private static final String LOC_SRC_FILE = "d:\\picture1.jpg";
	private static final String LOC_DST_FILE = "d:\\picture_from_hdfs.jpg";
	
	@Before
	public void connectHDFS(){
		try {
			ConfigInfo config = new ConfigInfo("192.168.1.171", "9000");
			fs = HDFSUtil.getHDFSFileSystem(config);
		} catch (HDFSException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*
	@Test
	public void TestUploadFile(){
		
		File locFile = null;
		InputStream fis = null;
		byte[] buffer = null;
		try{
			locFile = new File(LOC_SRC_FILE);
			if (locFile.exists()) {
				fis = new FileInputStream(locFile);
				buffer = new byte[(int)locFile.length()];
				fis.read(buffer);
			}
			ConfigInfo config = new ConfigInfo("192.168.1.171", "9000");
			String file_name = "wyj/picture_1.jpg";
			HDFSUtil.WriteFile(fs, config, file_name, buffer);
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
	}
	*/
	
	@Test
	public void GetFile(){
		File loc_target_file=null;
		OutputStream fos = null;
		byte[] buffer = null;
		try {
			loc_target_file = new File(LOC_DST_FILE);
			if (loc_target_file.exists()) {
				loc_target_file.delete();
			}
			loc_target_file.createNewFile();
			fos = new FileOutputStream(loc_target_file);
			
			ConfigInfo config = new ConfigInfo("192.168.1.171", "9000");
			String dstPath = "guest/mickey.jpg";
			buffer = HDFSUtil.ReadFile(fs, config, dstPath);
			fos.write(buffer);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
	}
	
	
	@After
	public void cleanUp(){
		try {
			FileSystem.closeAll();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
}
