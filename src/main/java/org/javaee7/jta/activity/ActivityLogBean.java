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
package org.javaee7.jta.activity;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.javaee7.jta.db.DBBean;

@RequestScoped
public class ActivityLogBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	ActivityLogTransactional tranID;
	
	@Inject
	DBBean db;
	
	// Note that despite the fact that we need our data to be logged
	// in a new transaction (so that it definitely commits), we use
	// REQUIRED here. This is because if we used REQUIRES_NEW, our
	// transaction scoped bean would enter a new transaction, and
	// give us a new random ID.  Instead the DBBean log call itself
	// has REQUIRES_NEW. We still need REQUIRED here, because injecting
	// the @TransactionScoped bean requires that we're in a transaction.
	@Transactional(value = TxType.REQUIRED)
	public void log(String bean, String text) {
		db.log(tranID.getID(), bean, text);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public List<ActivityLogEntry> getAllLogEntries() {
		return db.getAllLogEntries();
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void deleteActivityLogData() {
		db.deleteAllLogEntries();
	}
	
}
