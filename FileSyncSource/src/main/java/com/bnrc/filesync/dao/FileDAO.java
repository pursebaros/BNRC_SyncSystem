package com.bnrc.filesync.dao;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.bcel.generic.FSTORE;

import com.bnrc.filesync.exception.DAOException;
import com.funambol.framework.core.Status;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;
import com.funambol.server.db.RoutingDataSource;

public class FileDAO {

	
	private static final String JNDI_NAME_CORE_DATASOURCE = "jdbc/fnblcore";
	private static final String JNDI_NAME_USER_DATASOURCE = "jdbc/fnbluser";
	
	protected DataSource        coreDataSource = null;
	protected RoutingDataSource userDataSource = null;
	
	protected static final String QUERY_ALL_ITEM_ID = "SELECT * from fnbl_pim_picture where status!='D' and userid=?";
	protected static final String QUERY_CHANGED_ITEM_ID = "SELECT * from fnbl_pim_picture where userid=? and last_update>? and last_update<?";
	protected String user_id;
	protected String dataSource_url;
	protected String dataSource_user;
	protected String dataSource_pwd;
	
	
	protected DBIDGenerator dbidGenerator; 
	
	
	
	public FileDAO(String userid,String uidNameSpace){
		this.user_id = userid;
		//readDataSourceConfig(System.getProperty("funambol.home")); 
		try {
	            this.coreDataSource = (DataSource)DataSourceTools.lookupDataSource(JNDI_NAME_CORE_DATASOURCE);
	    } catch (NamingException ex) {
	            throw new IllegalArgumentException("Error looking up datasource: " + JNDI_NAME_CORE_DATASOURCE, ex);
	    }
	    
	    try {
            this.userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_NAME_USER_DATASOURCE);
        } catch (NamingException ex) {
            throw new IllegalArgumentException("Error looking up datasource: " + JNDI_NAME_USER_DATASOURCE, ex);
        }
		
		this.dbidGenerator = DBIDGeneratorFactory.getDBIDGenerator(uidNameSpace,coreDataSource);
		
	}
	
	
	/*
	 * 查询指定用户下所有文件的GUID.（注：不包括删除的文件的GUID）
	 */
	public List getAllItems() throws DAOException{
		List<String> allItems = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null; 
        try {
           	conn = getUserDataSource().getRoutedConnection(user_id);
        	ps = conn.prepareStatement(QUERY_ALL_ITEM_ID);
        	ps.setString(1, user_id);
        	rs = ps.executeQuery();
        	while(rs.next()){
        		Long guid = rs.getLong(1);
        		allItems.add(guid.toString());
        	}
        	
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			   if(conn!=null){
				   try{
					  conn.close();
			       }catch (SQLException se) {
					// TODO: handle exception
			    	   se.printStackTrace();
			       }//end of finally try-catch
				}//end if
		}
		return allItems;
	}
					
	
	/*
	 * 查询自上次同步之后发生改变的数据条目GUID
	 */
	public List<String>[] getChangedItemsByLastSync(long sinceTs,long untilTs) throws DAOException{
		    
		   /*
		    * space for logging....
		    */
		
		    List<String> newItems     = new ArrayList<String>();
	        List<String> updatedItems = new ArrayList<String>();
	        List<String> deletedItems = new ArrayList<String>();

	        List<String>[] changedItems = new ArrayList[3];
	        changedItems[0] = newItems;
	        changedItems[1] = updatedItems;
	        changedItems[2] = deletedItems;
	        
	        
	        Connection conn = null;
	        PreparedStatement ps = null;

	        ResultSet rs = null;
	        
	        try {
	        	
	        	conn = getUserDataSource().getRoutedConnection(user_id);
	        	ps = conn.prepareStatement(QUERY_CHANGED_ITEM_ID);
	        	ps.setString(1, user_id);
	        	ps.setLong(2, sinceTs);
	        	ps.setLong(3, untilTs);
	        	
	        	rs = ps.executeQuery();
	        	while(rs.next()){
	        		Long guid = rs.getLong(1);
	        		String status = rs.getString(4);
	        		char s = status.charAt(0);
	        		if (s == 'N' || s == 'n') {
	                     newItems.add(guid.toString());
	                 } else if (s == 'U' || s == 'u') {
	                     updatedItems.add(guid.toString());
	                 } else if (s == 'D' || s == 'd') {
	                     deletedItems.add(guid.toString());
	                 }
	        	}
	        	
	        	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				
			}finally{
				
				if(conn!=null){
				   try{
					  conn.close();
			       }catch (SQLException se) {
					// TODO: handle exception
			    	   se.printStackTrace();
			       }//end of finally try-catch
				}//end if
			}//end finally
			return changedItems;
	}
	
	
		
	/*
	 * 返回coreDataSource;
	 */
	public DataSource getCoreDataSource(){
		return this.coreDataSource;
	}
	
	/*返回userDataSource；
	 * 
	 */
	public RoutingDataSource getUserDataSource(){
		return this.userDataSource;
	}
}
