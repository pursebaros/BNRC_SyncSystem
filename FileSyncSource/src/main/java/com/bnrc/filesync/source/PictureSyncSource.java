package com.bnrc.filesync.source;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import com.bnrc.filesync.exception.EntityException;
import com.bnrc.filesync.exception.FileTransformException;
import com.bnrc.filesync.manager.PictureManager;
import com.bnrc.filesync.model.FileObjectWrapper;
import com.bnrc.filesync.util.ConfigInfo;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;

public class PictureSyncSource extends AbstractFileSyncSource {
	
	private transient PictureManager manager;
	//private ConfigInfo config = null;
	private String hdfs_ip = null;
	private String hdfs_port = null;

	
	
	/*
	 * Constructors
	 */
	public PictureSyncSource(){
		
	}
	
	public PictureSyncSource(String name,String type,String sourceURI){
		super(name, type, sourceURI);
	}
	
	
	
	/*
	 * @override methods:
	 * 
	 */
	@Override
	public void beginSync(SyncContext syncContext) throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			this.manager = new PictureManager(syncContext.getPrincipal().getUsername());
			super.manager = this.manager;
			super.beginSync(syncContext);
			ConfigInfo config = new ConfigInfo(hdfs_ip, hdfs_port);
			manager.beginSync(config);
		} catch (Exception e) {
			// TODO: handle exception
			throw new SyncSourceException("Error in beginSync. ", e);
		}
		
		
	}
	
	/*
	 * end Sync
	 * @see com.bnrc.filesync.source.AbstractFileSyncSource#endSync()
	 */
	@Override
	public void endSync() throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			manager.endSync();
		} catch (Exception e) {
			// TODO: handle exception
			throw new SyncSourceException("Error in endSync. ", e);
		}
	}
	
	@Override
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			List guidList = manager.getAllItemGUID();
			SyncItemKey[] keyList = new SyncItemKey[guidList.size()];
			for(int i=0;i<guidList.size();i++){
				keyList[i] = new SyncItemKey((String)guidList.get(i));
			}
			return keyList;
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error retrieving all item keys. ", e);
		}
		
	}
	
	@Override
	public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs, Timestamp untilTs)
	throws SyncSourceException {
		try {
			List guidList = manager.getNewItemGUID(sinceTs.getTime(), untilTs.getTime());
			SyncItemKey[] keyList = new SyncItemKey[guidList.size()];
			for(int i=0;i<guidList.size();i++){
				keyList[i] = new SyncItemKey((String)guidList.get(i));
			}
			return keyList;
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error retrieving all item keys. ", e);
		}
    }
	
	
	
	
	@Override
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs,Timestamp untilTs) 
	throws SyncSourceException {
		try {
			List guidList = manager.getUpdateGUID(sinceTs.getTime(), untilTs.getTime());
			SyncItemKey[] keyList = new SyncItemKey[guidList.size()];
			for(int i=0;i<guidList.size();i++){
				keyList[i] = new SyncItemKey((String)guidList.get(i));
			}
			return keyList;
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error retrieving all item keys. ", e);
		}
	}
	
	@Override
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,Timestamp untilTs) 
	throws SyncSourceException {
		try {
			List guidList = manager.getDeleteGUID(sinceTs.getTime(), untilTs.getTime());
			SyncItemKey[] keyList = new SyncItemKey[guidList.size()];
			for(int i=0;i<guidList.size();i++){
				keyList[i] = new SyncItemKey((String)guidList.get(i));
			}
			return keyList;
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error retrieving all item keys. ", e);
		}
	}
	
	
	@Override
	public void removeSyncItem(SyncItemKey itemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			manager.deleteItem(itemKey.getKeyAsString());
			
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error deleting item .", e);
		}

	}
	
	@Override
	public SyncItem updateSyncItem(SyncItem syncInstance)
	throws SyncSourceException {
		try {
			FileObjectWrapper file = getFileFromSyncItem(syncInstance);
			manager.updateItem(syncInstance.getKey().getKeyAsString(), file);
			SyncItem updateItem = new InMemorySyncItem(this, 
													syncInstance.getKey().getKeyAsString(), 
													null, 
													SyncItemState.UPDATED, 
													syncInstance.getContent(), 
													syncInstance.getFormat(), 
													syncInstance.getType(), 
													syncInstance.getTimestamp());
			
			return updateItem;
		}catch (FileTransformException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error fetching syncitem content.", e);
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error updating new item.", e);
		}
	}
	
	
	@Override
	public SyncItem addSyncItem(SyncItem syncInstance)
	throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			FileObjectWrapper file = getFileFromSyncItem(syncInstance);
			String guid = manager.addItem(file);
			SyncItem newItem = new InMemorySyncItem(this, 
													guid, 
													null, 
													SyncItemState.NEW, 
													syncInstance.getContent(), 
													syncInstance.getFormat(), 
													syncInstance.getType(), 
													syncInstance.getTimestamp());
			
			return newItem;
		}catch (FileTransformException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error fetching syncitem content.", e);
		} catch (EntityException e) {
			// TODO: handle exception
			throw new SyncSourceException("Error adding new item.", e);
		}
	
	}
	
	
	@Override
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
	throws SyncSourceException {
		// TODO Auto-generated method stub
		try {
			FileObjectWrapper file = manager.getItem(syncItemKey.getKeyAsString());
			byte[] format_file = Format_File(file);
			Timestamp timestamp = new Timestamp(Long.parseLong(file.getProperty("timestamp")));
			SyncItem newItem = new InMemorySyncItem(this, 
													syncItemKey, 
													null, 
													file.getProperty("status").charAt(0), 
													format_file, 
													"pic_f",//format,not use b64 in order to prevent engine handle. 
													file.getProperty("type"), 
													timestamp);
			return newItem;
			
			
		}catch (Exception e) {
			// TODO: handle exception
			throw new SyncSourceException("Error retriving item.",e);
		}
	}
	
	/*
	 * setter and getter methods
	 */
	public void setHdfs_ip(String hdfs_ip){
		this.hdfs_ip = hdfs_ip;
	}
	
	public void setHdfs_port(String hdfs_port){
		this.hdfs_port = hdfs_port;
	}
	
	public String getHdfs_ip(){
		return hdfs_ip;
	}
	
	public String getHdfs_port(){
		return hdfs_port;
	}
	
	/*
	 * 从SyncItem中提取出文件信息，包括文件内容、文件名等信息
	 */
	private FileObjectWrapper getFileFromSyncItem(SyncItem syncInstance) 
	throws FileTransformException{
		
		try {
			
			byte[] origin_content = syncInstance.getContent();
			String  tmp_content = new String(origin_content);
			InputStream ns = new ByteArrayInputStream(tmp_content.getBytes());
			Properties data = new Properties();
			data.load(ns);
			FileObjectWrapper file = new FileObjectWrapper(data.getProperty("name"), 
														   data.getProperty("size"), 
														   data.getProperty("type"), 
														   data.getProperty("content").getBytes("utf8"));
			
			return file;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new FileTransformException();
		}
	}
	
	
	/*
	 * 将一个文件从FileObjectWrapper形式格式化为SyncML的数据形式
	 */
	private byte[] Format_File(FileObjectWrapper fo)
	throws FileTransformException{
		
			try {
				
				StringBuffer sb = new StringBuffer();
				sb.append("name"+"="+fo.getProperty("name")+"\n");
				sb.append("size"+"="+fo.getProperty("size")+"\n");
				sb.append("type"+"="+fo.getProperty("type")+"\n");
				sb.append("content"+"="+new String(fo.getFileContent())+"\n");
				
				String result = sb.toString();
				return result.getBytes("utf8");
			
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				throw new FileTransformException();
			}
		
		
	}
}
