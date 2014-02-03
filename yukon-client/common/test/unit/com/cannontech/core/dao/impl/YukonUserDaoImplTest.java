package com.cannontech.core.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserDaoImplTest {

	private class testAlwaysPass extends YukonUserDaoImpl {
		
		public LiteYukonUser findUserByUsername(String username) {
			return null;
		}
	}
	
	private class testAlwaysFail extends YukonUserDaoImpl {
		
		public LiteYukonUser findUserByUsername(String username) {
			return new LiteYukonUser(5, "thisUserNameAlreadyExists");
		}
	}
	
	private class testFailFirstRunOnly extends YukonUserDaoImpl {
		boolean done = false;
		public LiteYukonUser findUserByUsername(String username) {
			if(!done) {
				done = true;
				return new LiteYukonUser(6, "thisUserNameAlreadyExists");
			} else {
				return null;
			}
		}
	}
	
	@Test
	public void testGenerateUsernameWorks() {
		YukonUserDaoImpl dao = new testAlwaysPass();
		
		String firstName = "FirstName";
		String lastName = "LastName";
		
		try {
			String result = dao.generateUsername(firstName, lastName);
			assertEquals("flastname",result);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
	@Test
	public void testGenerateUsernameFail() {
		YukonUserDaoImpl dao = new testAlwaysFail();
		
		String firstName = "FirstName";
		String lastName = "LastName";
		
		try {
			dao.generateUsername(firstName, lastName);
			assertEquals("This Should not happen","But it did");
		} catch (RuntimeException e) {
			assertEquals(1,1);
		}
	}
	
	@Test
	public void testGenerateUsernameTruncate() {
		YukonUserDaoImpl dao = new testAlwaysPass();
		
		String firstName = "FirstName";
		String lastName = "LastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastName";
		
		try {
			//This should only Truncate. No random numbers
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastnamelastnamelastnamelastnamelastnamelastnamelastnamelastnam";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();

			assertEquals(true,match);
			assertEquals(result.length(),64);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
	@Test
	public void testGenerateUsernameTruncateGenerate() {
		YukonUserDaoImpl dao = new testFailFirstRunOnly();
		
		String firstName = "FirstName";
		String lastName = "LastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastName";
		
		try {
			//This should generate a random number and still maintain < 64 characters.
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastnamelastnamelastnamelastnamelastnamelastnamelastnamela[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();

			assertEquals(true,match);			
			assertEquals(result.length(),64);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
	@Test
	public void testGenerateUsernameNoTruncateGenerate() {
		YukonUserDaoImpl dao = new testFailFirstRunOnly();
		
		String firstName = "FirstName";
		String lastName = "LastName";
		
		try {
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastname[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();

			assertEquals(true,match);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
}
