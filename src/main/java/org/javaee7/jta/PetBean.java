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
 * (C) Copyright IBM Corp. 2015.
 * 
 * All Rights Reserved. Licensed Materials - Property of IBM.  
 */
package org.javaee7.jta;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.javaee7.jta.RequestInfo.ExceptionToThrow;
import org.javaee7.jta.activity.ActivityLogBean;
import org.javaee7.jta.db.DBBean;
import org.javaee7.jta.exceptions.BadPetDataException;
import org.javaee7.jta.exceptions.BadPetDataRuntimeException;

@RequestScoped
public class PetBean {
	@Inject
	private DBBean db;
	
	@Inject
	private ActivityLogBean log;
	
	@Transactional(value = TxType.REQUIRED)
	public void reqd(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		doWork(pet, ownerID);
	}
	
	@Transactional(value = TxType.REQUIRED, dontRollbackOn = { BadPetDataRuntimeException.class})
	public void reqdNoRollbackRTE(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		doWork(pet, ownerID);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void reqNew(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		doWork(pet, ownerID);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW, dontRollbackOn = { BadPetDataRuntimeException.class})
	public void reqNewNoRollbackRTE(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		doWork(pet, ownerID);
	}
	
	private void doWork(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		log.log("PetBean", "Attempting to create pet: " + pet);
		
		db.createPet(pet, ownerID);
		
		throwExceptionIfRequired(pet);
	}
	
	private void throwExceptionIfRequired(RequestInfo owner) throws BadPetDataException {
		if(owner.getException().equals(ExceptionToThrow.Checked)) {
			throw new BadPetDataException();
		} else if(owner.getException().equals(ExceptionToThrow.Runtime)) {
			throw new BadPetDataRuntimeException();
		}
	}
}
