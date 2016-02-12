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

package org.javaee7.jta.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.javaee7.jta.RequestInfo;
import org.javaee7.jta.activity.ActivityLogEntry;

@RequestScoped
public class DBBean {

	private static String ACTIVITY_TABLE = "ACTIVITY";
	private static String OWNER_TABLE = "OWNER";
	private static String PET_TABLE = "PET";
	private static volatile boolean tablesInitialised = false;
	
	private String jndi = "java:comp/DefaultDataSource";
	private String schema = "JTASAMPLE";  
	private String actTable = schema + "." + ACTIVITY_TABLE;
	private String ownerTable = schema + "." + OWNER_TABLE;
	private String petTable = schema + "." + PET_TABLE;
	
	private DataSource dbSource = null;

	@PostConstruct
	private void init() {
		schema = System.getProperty("com.ibm.ws.jtasample.DB_SCHEMA", "JTASAMPLE");
		jndi = System.getProperty("com.ibm.ws.jtasample.JNDI_NAME", "java:comp/DefaultDataSource");
		actTable = schema + "." + ACTIVITY_TABLE;
		ownerTable = schema + "." + OWNER_TABLE;
		petTable = schema + "." + PET_TABLE;

		try {
			dbSource = getDataSource();
		} catch (NamingException e) {
			throw new RuntimeException("Couldn't get data source!", e);
		}

		createTablesIfNotExist();

	}

	private DataSource getDataSource() throws NamingException {
		Context ctx = new InitialContext();
		return (DataSource)ctx.lookup(jndi);
	}

	/**
	 * Creates the pet, returning its database-generated ID
	 */
	public Integer createPet(RequestInfo pet, Integer ownerID) {
		System.out.println("creating pet: " + pet + " with owner ID: " + ownerID);
		
		Integer petId = null;
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement("INSERT INTO " + petTable + "(NAME,OWNER_ID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, pet.getName());
			ps.setInt(2, ownerID);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys();) {
				while (rs.next()) {
					petId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			handleError(e);
		}

		return petId;
	}

	/**
	 * Creates the owner, returning its database-generated ID
	 */
	public Integer createOwner(RequestInfo owner) {
		System.out.println("creating owner: " + owner);

		Integer ownerId = null;
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement("INSERT INTO " + ownerTable + "(NAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, owner.getName());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys();) {
				while (rs.next()) {
					ownerId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			handleError(e);
		}

		return ownerId;
	}

	/**
	 * Returns a table containing all pets and their owners.  Since for the
	 * unusual case of this sample one of pet or owner can be null, we need
	 * a full outer join to get all the data. Derby doesn't support that, 
	 * so we simulate it using a union.
	 */
	public List<String[]> getAllOwnersAndPets() {
		List<String[]> results = new ArrayList<String[]>();

		final String sql = "SELECT b.OWNER, b.PET FROM (SELECT COALESCE(owner.NAME, '') AS owner, COALESCE(pet.NAME, '') AS pet, owner.ID ownid, pet.ID petid "
				+ "FROM " + ownerTable + " owner "
				+ "LEFT OUTER JOIN " + petTable + " pet "
				+ "ON owner.ID = pet.owner_id "
				+ "UNION "
				+ "SELECT COALESCE(owner2.NAME, '') AS owner, COALESCE(pet2.NAME, '') AS pet, owner2.ID, pet2.ID "
				+ "FROM " + ownerTable + " owner2 "
				+ "RIGHT OUTER JOIN " + petTable + " pet2 "
				+ "ON owner2.ID = pet2.owner_id) b ORDER BY owner, pet";
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {

			while(rs.next()) {
				results.add(new String[] {rs.getString(1), rs.getString(2)});
			}

		} catch (SQLException e) {
			handleError(e);
		}

		return results;
	}

	public void deleteAllOwnersAndPets() {
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement("DELETE FROM " + ownerTable);
				PreparedStatement ps2 = c.prepareStatement("DELETE FROM " + petTable);) {
			ps.executeUpdate();
			ps2.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void log(int txid, String beanName, String text) {

		System.out.println("LOGGING: " + txid + ", " + beanName + ", " + text);
		
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement("INSERT INTO " + actTable + "(TXID, BEAN_NAME, TEXT) VALUES (?, ?, ?)");) {
			ps.setInt(1, txid);
			ps.setString(2, beanName);
			ps.setString(3, text);
			ps.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public List<ActivityLogEntry> getAllLogEntries() {
		List<ActivityLogEntry> results = new ArrayList<ActivityLogEntry>();

		final String sql = "SELECT TXID, BEAN_NAME, TEXT FROM " + actTable + " ORDER BY ID DESC";
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {

			while(rs.next()) {
				ActivityLogEntry entry = new ActivityLogEntry(rs.getInt(1),
						rs.getString(2),
						rs.getString(3));
				results.add(entry);
			}

		} catch (SQLException e) {
			handleError(e);
		}

		return results;
	}

	public void deleteAllLogEntries() {
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement("DELETE FROM " + actTable);) {
			ps.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}
	
	private void createTablesIfNotExist() {
		// double checked lock to limit attempts to check if tables
		// have been created. Strictly speaking this method needs a
		// bit of extra error checking: in the case of multiple app
		// servers two could attempt to create the tables at the same time,
		// and one might have its request fail out.
		if (!tablesInitialised) {
			synchronized (DBBean.class) {
				if(!tablesInitialised) {
					try {
						if(!checkTableExists(ACTIVITY_TABLE)) {
							createActivityTable();
						}
						if(!checkTableExists(OWNER_TABLE)) {
							createOwnerTable();
						}
						if(!checkTableExists(PET_TABLE)) {
							createPetTable();
						}
						tablesInitialised = true;
					} catch (SQLException e) {
						handleError(e);
					}
				}
			}
		}
	}
	
	private void createActivityTable() throws SQLException {
		String sql = "CREATE TABLE " + actTable + "(ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "TXID INTEGER NOT NULL, "
				+ "BEAN_NAME VARCHAR(100) NOT NULL, "
				+ "TEXT VARCHAR(1000) NOT NULL)";
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);) {
			ps.execute();
		}
	}
	
	private void createOwnerTable() throws SQLException {
		String sql = "CREATE TABLE " + ownerTable + 
				"(ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "NAME VARCHAR(1000) NOT NULL)";
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);) {
			ps.execute();
		}
	}
	
	private void createPetTable() throws SQLException {
		String sql = "CREATE TABLE " + petTable + 
				"(ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "NAME VARCHAR(1000) NOT NULL, "
				+ "OWNER_ID INTEGER)";
		try (Connection c = dbSource.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);) {
			ps.execute();
		}
	}

	private boolean checkTableExists(String table) {
		boolean exists = false;
		try (Connection c = dbSource.getConnection();
				ResultSet rs = c.getMetaData().getTables(null, schema, table, null)) {

			exists = rs.next();

		} catch (SQLException e) {
			handleError(e);
		}
		
		return exists;
	}

	// For the purposes of this demo we're punting on error handling a bit,
	// wrapping the error in a RuntimeException and throwing it.
	private void handleError(SQLException e) {
		SQLException oldE = e;
		do {
			e.printStackTrace(System.out);
			e = e.getNextException();
		} while(e != null);
		throw new RuntimeException("Encountered unexpected SQLException", oldE);
	}
}
