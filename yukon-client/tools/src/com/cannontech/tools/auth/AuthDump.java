package com.cannontech.tools.auth;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.DBEditorRole;

/**
 * @author alauinger
 */
public class AuthDump {

	public static void main(String[] args) {
		
		int count = 1;
		
		if(args.length > 0) {
			count = Integer.parseInt(args[0]);
		}
		
		for(int i = count; i > 0; i--) {
			System.out.println("starting iteration " + (count-i+1));			
			long start = System.currentTimeMillis();
			dump();			
			System.out.println("*** Iteration " + (count-i+1) + " done in " + (System.currentTimeMillis()-start));
			System.out.println("");
			DefaultDatabaseCache.getInstance().releaseAllCache();
		}
		
		System.exit(0);
	}
	
	private static void dump() {
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
		List allRoleProperties = cache.getAllYukonRoleProperties();
		
		i = allRoles.iterator();
		while(i.hasNext()) {
			LiteYukonRole r = (LiteYukonRole) i.next();
			System.out.println("roleid: " + r.getRoleID() + " name: " + r.getRoleName() + " category: " + r.getCategory() );
			Iterator propIter = allRoleProperties.iterator();
			while(propIter.hasNext()) {
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) propIter.next();
				if(p.getRoleID() == r.getRoleID()) {
					System.out.println("  rolepropertyid: " + p.getRolePropertyID() + " keyname: " + p.getKeyName() + " defaultvalue: " + p.getDefaultValue());
				}
			}	
		}		
		
		System.out.println("Users/Groups:");
		Map ugMap = cache.getYukonUserGroupMap();
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
		
		Map lookupMap = cache.getYukonUserRoleIDLookupMap();
		
	    Iterator i1 = allUsers.iterator();
	    while(i1.hasNext()) {
	    	LiteYukonUser user = (LiteYukonUser) i1.next();
	    	Iterator i2 = allRoles.iterator();
	    	while(i2.hasNext()) {
	    		LiteYukonRole role = (LiteYukonRole) i2.next();
	    		Map m = (Map) lookupMap.get(user);
	    		if(m != null) {
	    			LiteYukonRole r2 = AuthFuncs.checkRole(user,role.getRoleID());
	    			if(r2 != null) {
	    				System.out.println("userid: " + user.getUserID() + "  username: " + user.getUsername() + " has roleid: " + r2.getRoleID() + " name: " + r2.getRoleName() + " category: " + r2.getCategory() );	    			
	    				
	    				Iterator propIter = allRoleProperties.iterator();
						while(propIter.hasNext()) {
							LiteYukonRoleProperty p = (LiteYukonRoleProperty) propIter.next();
							if(p.getRoleID() == r2.getRoleID()) {
								String pVal = AuthFuncs.getRolePropertyValue(user, p.getRolePropertyID());	
								System.out.println("  rolepropertyid: " + p.getRolePropertyID() + " keyname: " + p.getKeyName() + " value: " + pVal);
							}
						}	
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
	    
	    if(AuthFuncs.checkRole(yukonUser, DBEditorRole.ROLEID) != null) {
	    	System.out.println("yukon/yukon has database editor role");
	    	if(AuthFuncs.checkRoleProperty(yukonUser, DBEditorRole.POINT_ID_EDIT)) {
	    		System.out.println("yukon/yukon has property database editor point id edit is true, value is:"  + AuthFuncs.getRolePropertyValue(yukonUser, DBEditorRole.POINT_ID_EDIT));
	    	}
	    	else {
	    		System.out.println("yukon/yukon, database editor point id edit is not true, value is: " + AuthFuncs.getRolePropertyValue(yukonUser, DBEditorRole.POINT_ID_EDIT));
	    	}
	    }
	    else {
	    	System.out.println("yukon/yukon does _not_ have database editor role POINT_ID_EDIT");
	    }
	    
	    yukonUser = AuthFuncs.login("yukon", "yuKon");
	    if(yukonUser != null) {
	    	System.out.println("yukon/yuKon is a valid login");
	    }
	    else {
	    	System.out.println("yukon/yuKon failed to login");
	    }
	    
	}
}
