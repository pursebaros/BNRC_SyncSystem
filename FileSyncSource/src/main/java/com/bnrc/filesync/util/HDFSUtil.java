package com.bnrc.filesync.util;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.bnrc.filesync.exception.HDFSException;

public class HDFSUtil {
	
	/*
	 * ��ȡHDFS��FileSystem
	 */
	public static FileSystem getHDFSFileSystem(ConfigInfo config) 
	throws HDFSException{
		FileSystem fs = null;
		String url = "hdfs://"+config.getHDFS_IP()+":"+config.getHDFS_PORT();
		org.apache.hadoop.conf.Configuration con = new Configuration();
		try {
			fs = FileSystem.get(URI.create(url),con);
			return fs;
		} catch (Exception e) {
			// TODO: handle exception
			throw new HDFSException("Error getting HDFS system.", e);
		}
	}
	
	
	/*
	 * ��HDFS��д�ļ�
	 * @return true:�ɹ� false:ʧ��
	 */
	public static void WriteFile(FileSystem fs,ConfigInfo config,String dstPath,byte[] content)
	throws HDFSException{
		String file_url = "hdfs://"+config.getHDFS_IP()+":"+config.getHDFS_PORT()+"/"+dstPath;
		Path dst = new Path(file_url);
		try {
			FSDataOutputStream fos = fs.create(dst);
			fos.write(content);
			fos.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new HDFSException("Error writing file to HDFS system.", e);
		}
	}
	
	/*
	 * ��HDFS�ж��ļ�
	 */
	public static byte[] ReadFile(FileSystem fs,ConfigInfo config,String dstPath)
	throws HDFSException{
		String file_url = "hdfs://"+config.getHDFS_IP()+":"+config.getHDFS_PORT()+"/"+dstPath;
		Path dst = new Path(file_url);
		
		try {
			if (!fs.exists(dst)) {
				throw new HDFSException(file_url+" not exists.");
			}
			FSDataInputStream fis = fs.open(dst);
			byte[] content = new byte[(int)fs.getFileStatus(dst).getLen()];
			fis.read(content);
			fis.close();
			return content;
		} catch (Exception e) {
			// TODO: handle exception
			throw new HDFSException("Error reading file from HDFS system.", e);
		}
	}
	
	
	/*
	 * ��HDFS��ɾ���ļ�
	 */
	public static boolean RemoveFile(FileSystem fs,ConfigInfo config,String dstPath)
	throws HDFSException {
		String file_url = "hdfs://"+config.getHDFS_IP()+":"+config.getHDFS_PORT()+"/"+dstPath;
		Path dst = new Path(file_url);
		try {
			return fs.delete(dst, true);
		} catch (Exception e) {
			// TODO: handle exception
			throw new HDFSException("Error deleting file in HDFS system.", e);
		}
	}
}
