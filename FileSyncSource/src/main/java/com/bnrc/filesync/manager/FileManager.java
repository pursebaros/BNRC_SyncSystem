package com.bnrc.filesync.manager;

import java.util.*;

import com.bnrc.filesync.dao.FileDAO;
import com.bnrc.filesync.exception.DAOException;
import com.bnrc.filesync.exception.EntityException;
import com.bnrc.filesync.model.FileObjectWrapper;

public abstract class FileManager {
	
	
	/**
     * Array with the lists of the changed items:
     * - first element [0] = new items
     * - second element [1]= updated items
     * - third element [2]= deleted items
     */
	protected boolean isInitied = false;
	protected List<String>[] changedItems = null;	
	
	
	
	
	
	/*
	 * Get All files' GUIDs.
	 * return:
	 * a list of all files' GUID.
	 */
	public List getAllItemGUID() throws EntityException{
		try {
			return getFileDAO().getAllItems();
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error getting all item guids.",daoe); 
		}
		
	}
	
	
	/*
	 * Get New files' GUIDs.
	 * param:
	 * @sinceTs: since Timestamp;
	 * @untilTs:until Timestamp;
	 * return:
	 * a list of new files' GUID.
	 */
	public List getNewItemGUID(long sinceTs,long untilTs) throws EntityException{
		try {
			
			initChangedItems(sinceTs, untilTs);
			return changedItems[0];
			
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error getting new item guids", daoe);
		}
	}
	
	
	/*
	 * Get Update files' GUIDs.
	 * param:
	 * @sinceTs: since Timestamp;
	 * @untilTs:until Timestamp;
	 * return:
	 * a list of update files' GUID.
	 */
	public List getUpdateGUID(long sinceTs,long untilTs) throws EntityException{
		try {
			
			initChangedItems(sinceTs, untilTs);
			return changedItems[1];
			
		} catch (DAOException daoe) {
			// TODO: handle exception
			throw new EntityException("Error getting update item guids", daoe);
		}
	}
	
	
	/*
	 * Get delete files' GUIDs.
	 * param:
	 * @sinceTs: since Timestamp;
	 * @untilTs:until Timestamp;
	 * return:
	 * a list of delete files' GUID.
	 */
	 public List getDeleteGUID(long sinceTs,long untilTs) throws EntityException{
		 try {
				
			    initChangedItems(sinceTs, untilTs);
				return changedItems[2];
				
			} catch (DAOException daoe) {
				// TODO: handle exception
			   throw new EntityException("Error getting deleted item guids", daoe);
			}
	 }
	 
	 /*
	  * returns the FileDAO specific for every subclass
	  */
	 protected abstract FileDAO getFileDAO();
	 
	 
	    /*
		 * 初始化changedItem集合（查询）
		 */
		
	 protected void initChangedItems(long sinceTs,long untilTs) throws DAOException{
		
			if(!isInitied){
				changedItems = getFileDAO().getChangedItemsByLastSync(sinceTs, untilTs);
				isInitied = true;
			}
			 
	 }
	
}
