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
package org.javaee7.jta.activity;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

public class ActivityLogEntry {
	private int txid;
	private String beanName;
	private String text;
	
	public ActivityLogEntry(int txid, String beanName, String text) {
		this.txid = txid;
		this.beanName = beanName;
		this.text = text;
	}
	
	public JsonObjectBuilder toJSONBuilder() {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		return factory.createObjectBuilder().add("txid", txid)
			.add("beanName", beanName)
			.add("text", text);
	}
	
	public String toString() {
		return "txid: " + txid + ", beanName: " + beanName + ", text: " + text;
	}
}
