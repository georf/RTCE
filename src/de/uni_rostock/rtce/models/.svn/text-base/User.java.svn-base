package de.uni_rostock.rtce.models;

import java.util.Random;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.log.Log;

public class User {
	
	protected String name;
	protected String shortName;
	protected String userId;
	
	/**
	 * Generates an user with a random id
	 * 
	 * @param name
	 * @param shortName
	 */
	public User(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
		
		byte[] buffer = new byte[40];
		(new Random()).nextBytes(buffer);
		this.userId = Core.convertToHex(buffer);
		Log.debug("Me: "+userId);
	}
	
	/**
	 * Generates an user with firm id
	 * 
	 * @param name
	 * @param shortName
	 * @param userId
	 */
	public User(String name, String shortName, String userId) {
		this.name = name;
		this.shortName = shortName;
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getId() {
		return userId;
	}

}
