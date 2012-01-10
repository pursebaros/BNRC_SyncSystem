package com.bnrc.filesync.manager;

import java.util.List;

import com.bnrc.filesync.dao.FileDAO;
import com.bnrc.filesync.dao.PictureDAO;
import com.bnrc.filesync.exception.DAOException;
import com.bnrc.filesync.exception.EntityException;
import com.bnrc.filesync.model.FileMetaInfo;
import com.bnrc.filesync.model.FileObjectWrapper;
import com.bnrc.filesync.util.ConfigInfo;

import com.funambol.framework.tools.Base64;

public class PictureManager extends FileManager {

	
	/*
	 * member
	 */
	private PictureDAO dao;
	
	/*
	 * 
	 */
	public PictureManager(String userid){
		
		 dao = new PictureDAO(userid);
		 
	}
	
	/*
	 * 在SyncSource的beginSync方法中调用
	 */
	public void beginSync(ConfigInfo config) throws EntityException{
		try{
		   dao.init(config);
		}catch(Exception e){
			throw new EntityException("Error initializing PictureManager.", e);
		}
	}
	
	
	/*
	 * 在SyncSource的endSync方法中调用
	 * 
	 */
	
	public void endSync() throws EntityException{
		try {
			dao.CloseHDFSConnection();
		} catch (Exception e) {
			// TODO: handle exception
			throw new EntityException("Error ending PictureManager", e);
		}
	}
	
	
	/*
	 * 增加一个图片到后台
	 * @return : 图片的GUID
	 */
	public String addItem(FileObjectWrapper file) throws EntityException {
		
		try {
			
			//int file_size = Integer.parseInt(file.getProperty("size"));
			String file_name = file.getProperty("name");
			byte[] file_content=file.getFileContent();
			return dao.addPicture(file_name, Base64.decode(file_content)); //此处需要进行BASE64解码
			
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error adding item at manager layer", daoe);
		}
		
	}

	/*
	 * Get a file object from outside source.
	 * param:
	 * @guid: the file's GUID.
	 * return:
	 * the file content.
	 */
	public FileObjectWrapper getItem(String guid) throws EntityException {
		// TODO Auto-generated method stub
		try {
			//1.get file path:
			FileMetaInfo fmInfo = dao.getPictureMetaInfo(guid);
			//2.get file content:
			byte[] file_content = dao.getPictureContent(fmInfo.getFile_path()); //base64 encode
			//3.wrap file into FileObjectWrapper
			
			FileObjectWrapper fo = new FileObjectWrapper(fmInfo.getFileName(),
														 String.valueOf(file_content.length), 
														 "image/"+fmInfo.getFileType(), 
														 Base64.encode(file_content));
			//3.retrive file timestamp
			fo.setProperty("timestamp", fmInfo.getLast_timestamp());
			fo.setProperty("status", fmInfo.getStatus());
			return fo;
			
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error getting item at manager layer", daoe);
		}
	}

	/*
	 * Delete a file from outside source.
	 * param:
	 * @guid:the file's GUID.
	 * 
	 */
	public void deleteItem(String guid) throws EntityException {
		// TODO Auto-generated method stub
		try {
			dao.deletePicture(guid);
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error deleting item at manager layer", daoe);
		}
	}

	/*
	 * Update a file .
	 * param:
	 * @guid:the file's GUID.
	 * @file:the file to be updated.
	 * return:
	 * the file's GUID,it should be equal to argument @guid.
	 */
	public String updateItem(String guid, FileObjectWrapper file)
	throws EntityException {
		try {
			
			String file_name = file.getProperty("name");
			byte[] file_content=file.getFileContent();
			return dao.updatePicture(guid, file_name, file_content);
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error updating item at manager layer", daoe);
		}
	}



	@Override
	protected FileDAO getFileDAO(){
		return this.dao;
	}
	

}
