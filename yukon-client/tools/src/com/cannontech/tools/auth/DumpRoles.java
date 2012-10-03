package com.cannontech.tools.auth;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.roles.OperatorRoleDefs;
import com.cannontech.roles.application.BillingRole;
import com.cannontech.roles.application.CalcHistoricalRole;
import com.cannontech.roles.application.CommanderRole;
import com.cannontech.roles.application.DBEditorRole;
import com.cannontech.roles.application.EsubEditorRole;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.roles.application.TrendingRole;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.operator.OddsForControlRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;

/*
 * Created on May 22, 2003
 *
 *	Ugly little utility class that will print out all the role and property ids
 *  And then try to match these with what is found in the database.
 *  Should print out a crusty little report.
  */

public class DumpRoles {

	private static Class[] classToDump = 
	{
		BillingRole.class,
		CalcHistoricalRole.class,
		CommanderRole.class,
		DBEditorRole.class,
		EsubEditorRole.class,
		TDCRole.class,
		TrendingRole.class,
		WebClientRole.class,

		ResidentialCustomerRole.class,
		
		AdministratorRole.class,
		MeteringRole.class,
		ConsumerInfoRole.class,
		com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.class,
		com.cannontech.roles.operator.EsubDrawingsRole.class,
		OddsForControlRole.class,
		
		EnergyCompanyRole.class,
		
	};
	
	public static void main(String[] args) throws Throwable{
		HashMap roleIDMap = new HashMap();
		ArrayList roleIDList = new ArrayList();
				
		HashMap propIDMap = new HashMap();
		ArrayList propIDList = new ArrayList();
		
		for(int i = 0; i < classToDump.length; i++) {
			Field[] f = classToDump[i].getDeclaredFields();
			String className = classToDump[i].getName();
			for(int j = 0; j < f.length; j++) {
				String name = f[j].getName();
				Integer id = new Integer(f[j].get(null).toString());
				
				if(name.indexOf("ROLEID") != -1 ) {
					roleIDMap.put(id, className + "." + name + "=" + id.toString());
					roleIDList.add(id);
				}
				else {
					propIDMap.put(id, className + "." + name + "=" + id.toString());
					propIDList.add(id);
				}

			}	
		}
		
		Collections.sort(roleIDList);
		Collections.reverse(roleIDList);
		
		Collections.sort(propIDList);
		Collections.reverse(propIDList);
		
		for(Iterator i = roleIDList.iterator(); i.hasNext();) {
			System.out.println(roleIDMap.get(i.next()));
			}
		for(Iterator i = propIDList.iterator(); i.hasNext();) {
			System.out.println(propIDMap.get(i.next()));
		}		
		
		List allRoles = DefaultDatabaseCache.getInstance().getAllYukonRoles();
		List allProps = DefaultDatabaseCache.getInstance().getAllYukonRoleProperties();
		
		System.out.println("Roles in the database but not defined:");
		for(Iterator i = allRoles.iterator(); i.hasNext(); ) {
			LiteYukonRole r = (LiteYukonRole) i.next();
			if(roleIDMap.get(new Integer(r.getRoleID())) == null) {
				System.out.println("roleid " + r.getRoleID() + " name: " + r.getRoleName());
			}
		}
		
		System.out.println("\nProperties in the database but not defined:");
		for(Iterator i = allProps.iterator(); i.hasNext(); ) {
			LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
			if(propIDMap.get(new Integer(p.getRolePropertyID())) == null) {
				System.out.println("propertyid: " + p.getRolePropertyID() + " key: " + p.getKeyName() + " val: " + p.getDefaultValue());
			}
		}
		
		System.out.println("\nRoles defined but not in the database");
		for(Iterator i = roleIDList.iterator(); i.hasNext();) {
			Integer id = (Integer) i.next();
			if(DaoFactory.getAuthDao().getRole(id.intValue()) == null) {
				System.out.println(roleIDMap.get(id));		
			}
		}
		
		System.out.println("\nProperties defined but not in the database");
		for(Iterator i = propIDList.iterator(); i.hasNext();) {
			Integer id = (Integer) i.next();
			if(DaoFactory.getRoleDao().getRoleProperty(id.intValue()) == null) {
				System.out.println(propIDMap.get(id));
			}
		}
		
		System.exit(0);
	}
	
	public static void dumpClass(Class c) throws IllegalAccessException{
		Field[] f = OperatorRoleDefs.class.getDeclaredFields();
		for(int i = 0; i < f.length; i++) {
			Object name = f[i].getName();
				Object val = f[i].get(null);
				System.out.println(name.toString() + "=" + val.toString());
		}
	}
}
