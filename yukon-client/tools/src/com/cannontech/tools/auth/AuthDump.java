package com.cannontech.tools.auth;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author alauinger
 */
public class AuthDump {

	public static void main(String[] args) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		List allUsers = cache.getAllYukonUsers();
		System.out.println("Users:");
		
		Iterator i = allUsers.iterator();
		while(i.hasNext()) {
			LiteYukonUser u = (LiteYukonUser) i.next();
			System.out.println("userid: " + u.getUserID() + "  username: " + u.getUsername() + "  password: " + u.getPassword());
		}
		
		System.out.println("\nGroups:");
		List allGroups = cache.getAllYukonGroups();
		i = allGroups.iterator();
		while(i.hasNext()) {
			LiteYukonGroup g = (LiteYukonGroup) i.next();
			System.out.println("groupid: " + g.getGroupID() + "  name: " + g.getGroupName());
		}
		
		System.out.println("\nRoles: ");
		List allRoles = cache.getAllYukonRoles();
		i = allRoles.iterator();
		while(i.hasNext()) {
			LiteYukonRole r = (LiteYukonRole) i.next();
			System.out.println("roleid: " + r.getRoleID() + " name: " + r.getRoleName() + " default value: " + r.getDefaultValue());			
		}		
		
		System.out.println("Users/Groups:");
		Map ugMap = cache.getAllYukonUserGroupMap();
		i = allUsers.iterator();
		while(i.hasNext()){
			LiteYukonUser u = (LiteYukonUser) i.next();
			System.out.println("userid: " + u.getUserID() + "  username: " + u.getUsername() + "  password: " + u.getPassword());
			
			List gList = (List) ugMap.get(u);
			Iterator gIter = gList.iterator();
			while(gIter.hasNext()){
				LiteYukonGroup g = (LiteYukonGroup) gIter.next();
				System.out.println("  groupid: " + g.getGroupID() + "  name: " + g.getGroupName());
			}
			System.out.println("");			
		}
		
		Map lookupMap = cache.getAllYukonUserRoleLookupMap();
		
	    Iterator i1 = allUsers.iterator();
	    while(i1.hasNext()) {
	    	LiteYukonUser user = (LiteYukonUser) i1.next();
	    	Iterator i2 = allRoles.iterator();
	    	while(i2.hasNext()) {
	    		LiteYukonRole role = (LiteYukonRole) i2.next();
	    		Map m = (Map) lookupMap.get(user);
	    		if(m != null) {
	    			//Pair p = (Pair) m.get(role.getRoleName());
	    			Pair p = AuthFuncs.checkRole(user,role.getRoleName());
	    			if(p != null) {
	    			 	LiteYukonRole r2 = (LiteYukonRole) p.first;
	    			 	String value = (String) p.second;
	    				System.out.println("userid: " + user.getUserID() + "  username: " + user.getUsername() + " has roleid: " + r2.getRoleID() + " name: " + r2.getRoleName() + " value: " + value );
	    			}		
	    		}
	    	}
	    }
	    
	    LiteYukonUser yukonUser = AuthFuncs.login("yukon", "yukon");
	    if(yukonUser != null) {
	    	System.out.println("yukon/yukon is a valid login");
	    }
	    else {
	    	System.out.println("yukon/yukon failed to login");
	    }
	    
	    yukonUser = AuthFuncs.login("yukon", "yuKon");
	    if(yukonUser != null) {
	    	System.out.println("yukon/yuKon is a valid login");
	    }
	    else {
	    	System.out.println("yukon/yuKon failed to login");
	    }
	    
		System.exit(0);
	}
}
