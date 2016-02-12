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

import java.io.IOException;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javaee7.jta.RequestInfo.TransactionalType;
import org.javaee7.jta.exceptions.BadOwnerDataException;
import org.javaee7.jta.exceptions.BadOwnerDataRuntimeException;
import org.javaee7.jta.exceptions.BadPetDataException;
import org.javaee7.jta.exceptions.BadPetDataRuntimeException;

@WebServlet("/OwnerServlet")
@MultipartConfig
public class OwnerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Inject
	OwnerBean ownerBean;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//retrieve all the owners and their pets, and output them as JSON.
			response.setContentType("application/json");
			JsonBuilderFactory jsonFactory = Json.createBuilderFactory(null);
			JsonArrayBuilder arrayBuilder = jsonFactory.createArrayBuilder();


			for(String[] ownerAndPet: ownerBean.getOwnerAndPetData()) {
				arrayBuilder.add(jsonFactory.createArrayBuilder()
						.add(ownerAndPet[0])
						.add(ownerAndPet[1]));
			}

			
			try (JsonWriter writer = Json.createWriter(response.getWriter())) {
				writer.writeArray(arrayBuilder.build());
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
			response.sendError(500, "Something went wrong retrieving owner/pet data");
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ownerBean.deleteOwnerAndPetData();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			response.sendError(500, "Something went wrong deleting owner/pet data");
		}
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Get the owner and pet data from request, parse it into objects, and create them
			String ownerName = request.getParameter("ownername");
			String ownerExc = request.getParameter("excannowner");
			String ownerTranType = request.getParameter("tranannowner");
			String ownerRBRTE = request.getParameter("rbrteowner");

			String petName = request.getParameter("petname");
			String petExc = request.getParameter("excannpet");
			String petTranType = request.getParameter("tranannpet");
			String petRBRTE = request.getParameter("rbrtepet");

			RequestInfo owner = new RequestInfo(ownerName, ownerTranType, ownerExc, ownerRBRTE);
			RequestInfo pet = new RequestInfo(petName, petTranType, petExc, petRBRTE);

			create(owner, pet);
		} catch (BadOwnerDataException e) {
		} catch (BadOwnerDataRuntimeException e) {
		} catch (BadPetDataException e) {
		} catch (BadPetDataRuntimeException e) {
		} catch (Exception e) {
			response.sendError(500, "Something went wrong creating owner/pet data");
			e.printStackTrace(System.out);
		}
	}
	
	private void create(RequestInfo owner, RequestInfo pet) throws BadOwnerDataException, BadPetDataException {
		if(owner.getTranType().equals(TransactionalType.Required)) {
			if (owner.rollbackOnRTE()) {
				ownerBean.reqd(owner, pet);
			} else {
				ownerBean.reqdNoRollbackRTE(owner, pet);
			}
		} else if (owner.getTranType().equals(TransactionalType.RequiresNew)) {
			if (owner.rollbackOnRTE()) {
				ownerBean.reqNew(owner, pet);
			} else {
				ownerBean.reqNewNoRollbackRTE(owner, pet);
			}
		}
	}
}
