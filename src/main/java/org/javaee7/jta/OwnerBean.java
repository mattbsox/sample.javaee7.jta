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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.javaee7.jta.RequestInfo.ExceptionToThrow;
import org.javaee7.jta.RequestInfo.TransactionalType;
import org.javaee7.jta.activity.ActivityLogBean;
import org.javaee7.jta.db.DBBean;
import org.javaee7.jta.exceptions.BadOwnerDataException;
import org.javaee7.jta.exceptions.BadOwnerDataRuntimeException;
import org.javaee7.jta.exceptions.BadPetDataException;
import org.javaee7.jta.exceptions.BadPetDataRuntimeException;

@RequestScoped
public class OwnerBean {

	@Inject
	private PetBean petBean;
	
	@Inject
	private ActivityLogBean log;
	
	@Inject
	private DBBean db;
	
	@Transactional(value = TxType.REQUIRED)
	public void reqd(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		doWork(owner, pet);
	}
	
	@Transactional(value = TxType.REQUIRED, dontRollbackOn = { BadPetDataRuntimeException.class, BadOwnerDataRuntimeException.class })
	public void reqdNoRollbackRTE(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		doWork(owner, pet);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void reqNew(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		doWork(owner, pet);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW, dontRollbackOn = { BadPetDataRuntimeException.class, BadOwnerDataRuntimeException.class })
	public void reqNewNoRollbackRTE(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		doWork(owner, pet);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public List<String[]> getOwnerAndPetData() {
		return db.getAllOwnersAndPets();
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void deleteOwnerAndPetData() {
		db.deleteAllOwnersAndPets();
	}
	
	private void doWork(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		log.log("OwnerBean", "Attempting to create owner: " + owner);
		
		Integer ownerID = db.createOwner(owner);
		
		boolean exception = false;
		
		try {
			createPet(pet, ownerID);
		} catch (RuntimeException e) {
			exception = true;
			throw e;
		} finally {
			if(!exception) {
				throwExceptionIfRequired(owner);
			}
		}
	}
	
	private void createPet(RequestInfo pet, Integer ownerID) throws BadPetDataException {
		if(pet.getTranType().equals(TransactionalType.Required)) {
			if (pet.rollbackOnRTE()) {
				petBean.reqd(pet, ownerID);
			} else {
				petBean.reqdNoRollbackRTE(pet, ownerID);
			}
		} else if (pet.getTranType().equals(TransactionalType.RequiresNew)) {
			if (pet.rollbackOnRTE()) {
				petBean.reqNew(pet, ownerID);
			} else {
				petBean.reqNewNoRollbackRTE(pet, ownerID);
			}
		}
	}
	
	private void throwExceptionIfRequired(RequestInfo owner) throws BadOwnerDataException {
		if(owner.getException().equals(ExceptionToThrow.Checked)) {
			throw new BadOwnerDataException();
		} else if(owner.getException().equals(ExceptionToThrow.Runtime)) {
			throw new BadOwnerDataRuntimeException();
		}
	}
}
