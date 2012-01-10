package com.bnrc.filesync.dao;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.funambol.framework.tools.IOTools;
import com.funambol.server.db.DataSourceContextHelper;



public class PictureDAOTest {
	
	private static final String TEST_USER_ID = "wuyijian";
	private static final String TEST_DST_FILE = "picture.jpg";
	private static final String TEST_LOC_DST_FILE = "src/test/resources/com/bnrc/filesync/dao/PictureDAO/picture_from_hdfs.jpg";
	private static final String TEST_LOC_SRC_FILE = "src/test/resources/com/bnrc/filesync/dao/PictureDAO/picture.jpg";
	
	private PictureDAO dao = null;
	
	
	/*
	/*
	 * 测试前初始化工作
	 */
	
	@Before
	public void PrepareTest(){
		try {
			
			System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();
			
			dao = new PictureDAO(TEST_USER_ID);
			//dao.init();
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*
	 * 测试步骤：
	 * 1. add file to HDFS.
	 * 2. get added file from HDFS.
	 * 3. update the file.
	 * 4. get updated file from HDFS.
	 * 5. remove file in HDFS.
	 */
	@Test
	public void doTest(){
		try {
			
			byte[] loc_pic = IOTools.readFileBytes(TEST_LOC_SRC_FILE);
			String guid = dao.addPicture(TEST_DST_FILE, loc_pic);
			System.out.println("The added file's GUID is:"+guid);
			
			byte[] hdfs_pic = dao.getPictureContent(dao.getPictureName(guid));
			IOTools.writeFile(hdfs_pic, TEST_LOC_DST_FILE);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	@After
	public void endTest(){
		try {
			dao.CloseHDFSConnection();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
