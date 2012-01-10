package com.bnrc.filesync.source;

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.framework.security.Sync4jPrincipal;

import com.bnrc.filesync.manager.*;
import com.bnrc.filesync.util.ConfigInfo;

public class AbstractFileSyncSource implements SyncSource , java.io.Serializable{

	
	protected String name = null;
	protected String type = null;
	protected String sourceURI = null;
	protected SyncSourceInfo info = null;
	
	
	protected FileManager manager;
	protected Sync4jPrincipal principal;
	protected String userId;
	protected int syncMode;
	
	/*---------------------------------------------------
	-----------------------------------------------------*/
	/*
	 * no parameter constructor,for serizalize
	 */
	protected AbstractFileSyncSource(){
		
	}
	
	
	public AbstractFileSyncSource(String name,String type,String sourceURI){
		this.name = name;
		this.type = type;
		this.sourceURI = sourceURI;
	}
	
	
	
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public String getSourceURI() {
		// TODO Auto-generated method stub
		return this.sourceURI;
	}

	public SyncSourceInfo getInfo() {
		// TODO Auto-generated method stub
		return this.info;
	}
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSourceURI(String sourceURI){
		this.sourceURI = sourceURI;
	}
	
	public void setInfo(SyncSourceInfo info){
		this.info = info;
	}

	public void beginSync(SyncContext syncContext) throws SyncSourceException {
		// TODO Auto-generated method stub
		principal = syncContext.getPrincipal();
		userId = principal.getUsername();
		syncMode = syncContext.getSyncMode();
		
		
	}

	public void endSync() throws SyncSourceException {
		// TODO Auto-generated method stub

	}

	public void commitSync() throws SyncSourceException {
		// TODO Auto-generated method stub

	}

	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs,
			Timestamp untilTs) throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,
			Timestamp untilTs) throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs, Timestamp untilTs)
			throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItem addSyncItem(SyncItem syncInstance)
			throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItem updateSyncItem(SyncItem syncInstance)
			throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSyncItem(SyncItemKey itemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {
		// TODO Auto-generated method stub

	}

	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
			throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOperationStatus(String operationName, int status,
			SyncItemKey[] keys) {
		// TODO Auto-generated method stub

	}

}
