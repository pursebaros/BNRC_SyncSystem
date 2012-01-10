package com.bnrc.filesync.exception;

public class HDFSException extends Exception {

	
	public HDFSException (String msg) {
		super(msg);
	}
	
	
	public HDFSException(String msg, Throwable cause){
		super(msg, cause);
	}
}
