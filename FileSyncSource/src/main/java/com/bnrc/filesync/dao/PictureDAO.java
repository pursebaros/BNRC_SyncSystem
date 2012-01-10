package com.bnrc.filesync.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.servlet.jsp.jstl.core.Config;

import org.apache.hadoop.fs.FileSystem;

import com.bnrc.filesync.exception.*;
import com.bnrc.filesync.model.FileMetaInfo;
import com.bnrc.filesync.util.ConfigInfo;
import com.bnrc.filesync.util.HDFSUtil;
import com.funambol.framework.tools.DBTools;


public class PictureDAO extends FileDAO {

	
	private static final String ID_SPACE = "pim.id";
	private static final String INSERT_NEW_ITEM = "INSERT INTO fnbl_pim_picture (id,userid,last_update,status,file_path) VALUES (?,?,?,?,?)";
	private static final String QUERY_PATH = "SELECT * FROM fnbl_pim_picture WHERE id = ?";
	private static final String UPDATE_ITEM = "UPDATE fnbl_pim_picture SET last_update = ?,status=?,file_path=? WHERE id = ?";
	private FileSystem fs = null;
	private ConfigInfo config = null;
	
	
	
	public PictureDAO(String userid){
		super(userid,ID_SPACE);
		
	}
	
	
	/*
	 * 往后台增加一个图片
	 */
	public String addPicture(String picture_name,byte[] content) throws DAOException{
		
		String picture_path = user_id + "/"+ picture_name;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			//1. add file to hdfs:
			HDFSUtil.WriteFile(fs, config, picture_path, content);
			long timestamp = System.currentTimeMillis();
			//2. generate guid:
			long guid = dbidGenerator.next();
			conn = getUserDataSource().getRoutedConnection(user_id);
			ps = conn.prepareStatement(INSERT_NEW_ITEM);
			ps.setLong(1,guid);
			ps.setString(2,user_id);
			ps.setLong(3,timestamp);
			ps.setString(4, "N");
			ps.setString(5,picture_path);
			ps.executeUpdate();
			
			return String.valueOf(guid);
		} catch (Exception e) {
			// TODO: handle exception
			throw new DAOException("Error add picture to HDFS.", e);
		} finally{
			DBTools.close(conn, ps, null);
		}
		
	}
	
	
	/*更新后台的一个图片
	 * 
	 */
	public String updatePicture(String guid,String picture_name,byte[] content) throws DAOException{
		String origin_file_path = null;
		String new_file_path = null;
		Connection conn = null;
		PreparedStatement query = null;
		PreparedStatement update = null;
		ResultSet rs = null;
		try {
			
			//1. get origin file path;
			conn = getUserDataSource().getRoutedConnection(user_id);
			query = conn.prepareStatement(QUERY_PATH);
			query.setLong(1, Long.valueOf(guid));
			rs = query.executeQuery();
			while(rs.next()){
				 origin_file_path = rs.getString(1);
			}
			if(origin_file_path == null){
				throw new Exception("Error: cannot find corresponding file_path in fnbl_pim_picture.");
			}
			
			//2. remove origin file:
			if (HDFSUtil.RemoveFile(fs, config, origin_file_path) == false) {
				throw new Exception("Error: cannot delete old file.");
			}
			
			//3. create new update file:
			new_file_path = user_id + "/" + picture_name;
			HDFSUtil.WriteFile(fs, config,new_file_path , content);
			long timestamp = System.currentTimeMillis();
			//2. update table:
			
			
			update = conn.prepareStatement(UPDATE_ITEM);
			update.setLong(1, timestamp);
			update.setString(2, "U");
			update.setString(3, new_file_path);
			update.setLong(4,Long.valueOf(guid));
			update.executeUpdate();
			
			return String.valueOf(guid);
		} catch (Exception e) {
			// TODO: handle exception
			throw new DAOException("Error update picture to HDFS.", e);
		} finally{
			DBTools.close(conn, query, null);
			DBTools.close(conn, update, null);
		}
	}
	
	/*
	 * 删除后台的某个图片
	 */
	public void deletePicture(String guid) throws DAOException{
		String file_path = null;
		Connection conn = null;
		PreparedStatement query = null;
		PreparedStatement delete = null;
		ResultSet rs = null;
		try{
			//1.Retrive the corresponding file_path.
			conn = getUserDataSource().getRoutedConnection(user_id);
			query = conn.prepareStatement(QUERY_PATH);
			query.setLong(1, Long.valueOf(guid));
			rs = query.executeQuery();
			while(rs.next()){
				 file_path = rs.getString(1);
			}
			if(file_path == null){
				throw new Exception("Error: cannot find corresponding file_path in fnbl_pim_picture.");
			}
			
			//2.Remove file:
			
			if (HDFSUtil.RemoveFile(fs, config, file_path) == false) {
				throw new Exception("Error delete file on HDFS.");
			}
			long timestamp = System.currentTimeMillis();
			
			//3.Update table:
			delete = conn.prepareStatement(UPDATE_ITEM);
			delete.setLong(1, timestamp);
			delete.setString(2, "D");
			delete.setString(3, file_path);
			delete.setLong(4, Long.valueOf(guid));
			delete.executeUpdate();
			
			
		}catch (Exception e) {
			// TODO: handle exception
			throw new DAOException("Error deleting picture to HDFS.", e);
		}finally{
			DBTools.close(conn, query, null);
			DBTools.close(conn, delete, null);
		}
	}
	
	/*
	 * 获取后台的一个图片内容
	 * @param:
	 * path为文件名
	 */
	public byte[] getPictureContent(String path) throws DAOException{
		
		try{
			
	        return HDFSUtil.ReadFile(fs, config, path);
			
		}catch (Exception e) {
			// TODO: handle exception
			throw new DAOException("Error getting picture from HDFS.", e);
		}
	}
	
	/*
	 * 根据guid构造文件元信息
	 */
	public FileMetaInfo getPictureMetaInfo(String guid) throws DAOException{
		FileMetaInfo fmInfo = null;
		String file_path = null;
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getUserDataSource().getRoutedConnection(user_id);
			query = conn.prepareStatement(QUERY_PATH);
			query.setLong(1, Long.valueOf(guid));
			rs = query.executeQuery();
			while(rs.next()){
				 fmInfo = new FileMetaInfo(rs.getLong(1), rs.getString(2), rs.getString(5), rs.getString(4), rs.getLong(3));
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			fmInfo = new FileMetaInfo(0,"","", "", 0);
			throw new DAOException("Error getting file path.", e);
		}finally{
			DBTools.close(conn, query, null);
		}
		return fmInfo;
	}
	
	
	
	
	/*
	 * DAO初始化（读取配置、连接HDFS）
	 */
	public void init(ConfigInfo config) throws DAOException {
		
			
			try {
				this.config = config;
				fs = HDFSUtil.getHDFSFileSystem(config);
				
			} catch (Exception e) {
				// TODO: handle exception
				//e.printStackTrace();
				throw new DAOException("Error connecting to HDFS.", e);
			}
	
	}
	
	
	/*
	 * 关闭HDFS连接
	 */
	public void CloseHDFSConnection() throws DAOException{
		try {
			if (fs!=null) {
				fs.closeAll();
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new DAOException("Error connecting to HDFS.", e);
		}
	}
	
	
}
