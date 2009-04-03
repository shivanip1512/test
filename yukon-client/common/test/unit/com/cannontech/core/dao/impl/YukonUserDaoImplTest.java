package com.cannontech.core.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserDaoImplTest {

	private class testAlwaysPass extends YukonUserDaoImpl {
		
		public LiteYukonUser getLiteYukonUser(String username) {
			return null;
		}
	}
	
	private class testAlwaysFail extends YukonUserDaoImpl {
		
		public LiteYukonUser getLiteYukonUser(String username) {
			return new LiteYukonUser();
		}
	}
	
	private class testFail4times extends YukonUserDaoImpl {
		int i = 0;
		public LiteYukonUser getLiteYukonUser(String username) {
			if( i++ == 4) {
				return null;
			} else {
				return new LiteYukonUser();
			}
		}
	}
	
	private class testFail100times extends YukonUserDaoImpl {
		int i = 0;
		public LiteYukonUser getLiteYukonUser(String username) {
			if( i++ == 100) {
				return null;
			} else {
				return new LiteYukonUser();
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
	public void testGenerateUsernameFourthTruncate() {
		YukonUserDaoImpl dao = new testFail4times();
		
		String firstName = "FirstName";
		String lastName = "LastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastName";
		
		try {
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastname.*2";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();
			
			assertEquals(result.length(),64);
			assertEquals(true,match);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
	@Test
	public void testGenerateUsernameFourthNoTruncate() {
		YukonUserDaoImpl dao = new testFail4times();
		
		String firstName = "FirstName";
		String lastName = "LastName";
		
		try {
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastname[0-9]*2";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();

			assertEquals(true,match);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
	
	@Test
	public void testGenerateUsernameHundred() {
		YukonUserDaoImpl dao = new testFail100times();
		
		String firstName = "FirstName";
		String lastName = "LastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastNameLastName";
		
		try {
			String result = dao.generateUsername(firstName, lastName);
			String patternStr = "flastname.*98";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(result);
			boolean match = matcher.matches();
			
			assertEquals(result.length(),64);
			assertEquals(true,match);
		} catch (RuntimeException e) {
			assertEquals("This Should not happen","But it did");
		}
	}
}
