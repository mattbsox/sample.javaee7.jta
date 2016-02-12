/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source
 * code form. You may copy, modify, and distribute these sample programs in any 
 * form without payment to IBM for the purposes of developing, using, marketing 
 * or distributing application programs conforming to the application programming 
 * interface for the operating platform for which the sample code is written. 
 * 
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE 
 * ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, 
 * BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, 
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR 
 * CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR
 * OPERATION OF THE SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE
 * SOURCE CODE.
 * 
 * (C) Copyright IBM Corp. 2016.
 * 
 * All Rights Reserved. Licensed Materials - Property of IBM.  
 */
package org.javaee7.jta;

/**
 * Describes a a request to create an owner or a pet 
 */
public class RequestInfo {
	private String name;
	private ExceptionToThrow exception;
	private TransactionalType tranType;
	private boolean rollbackRTE = false;	

	public RequestInfo(String name, String tranType, String exception, String rollbackRTE) {
		if(name == null || tranType == null || exception == null) {
			throw new IllegalArgumentException("A null parameter was supplied");
		}
		
		this.name = htmlEncode(name);
		
		if(rollbackRTE != null && rollbackRTE.equals("true")) {
			this.rollbackRTE = true;
		}
		
		if(exception.equals("rte")) {
			this.exception = ExceptionToThrow.Runtime;
		} else if (exception.equals("non")) {
			this.exception = ExceptionToThrow.None;
		} else if (exception.equals("exc")) {
			this.exception = ExceptionToThrow.Checked;
		} else {
			throw new IllegalArgumentException("Unknown exception type provided: " + exception);
		}
		
		if(tranType.equals("reqd")) {
			this.tranType = TransactionalType.Required;
		} else if(tranType.equals("reqn")) {
			this.tranType = TransactionalType.RequiresNew;
		} else {
			throw new IllegalArgumentException("Unknown transactional type provided: " + tranType);
		}
	}
	
	private String htmlEncode(String s) {
		String ret = s.replace("&", "&amp;");
		ret = ret.replace("<", "&lt;");
		ret = ret.replace(">", "&gt;");
		ret = ret.replace("\"", "&#034");
		ret = ret.replace("'", "&#039");
		
		return ret;
	}
	
	public String toString() {
		return "Name: " + name + 
				", RollbackOnRTE: " + rollbackRTE +
				", TransactionalType: " + this.tranType +
				", ExceptionToThrow: " + this.exception;
	}
	
	public String getName() {
		return name;
	}

	public ExceptionToThrow getException() {
		return exception;
	}

	public TransactionalType getTranType() {
		return tranType;
	}

	public boolean rollbackOnRTE() {
		return rollbackRTE;
	}
	
	enum ExceptionToThrow {
		Checked,
		None,
		Runtime,
	}
	
	enum TransactionalType {
		Required,
		RequiresNew
	}
	
	 
	
}
