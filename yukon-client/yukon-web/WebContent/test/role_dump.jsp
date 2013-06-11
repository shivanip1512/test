<%@page import="com.cannontech.core.roleproperties.dao.RolePropertyDao"%>
<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<%
	// Dumps out all the roles and properties for the logged in user
%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>

<%@ page import="com.cannontech.spring.YukonSpringHook"%> 
<%@ page import="com.cannontech.core.dao.RoleDao"%>
<%@ page import="com.cannontech.core.dao.AuthDao"%>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonRole" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>

<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>

<%
	LiteYukonUser user = YUKON_USER;
	List allRoles = DefaultDatabaseCache.getInstance().getAllYukonRoles();
	List allRoleProperties = DefaultDatabaseCache.getInstance().getAllYukonRoleProperties();

	out.println("Role and Properties for userid: " + user.getUserID() + "&nbsp&nbsp&nbspusername: " + user.getUsername() + "<br><br>");	
	for(Iterator i = allRoles.iterator(); i.hasNext();) {
		LiteYukonRole r = (LiteYukonRole) i.next();
		System.out.println("h1");
		r = YukonSpringHook.getBean(AuthDao.class).getRole(user, r.getRoleID());
		if(r != null) {
			out.println("roleid: " + r.getRoleID() + "&nbsp&nbsp&nbspname: " + r.getRoleName() + "<br>");
			LiteYukonRoleProperty[] roleProps = YukonSpringHook.getBean(RoleDao.class).getRoleProperties(r.getRoleID());
			for(int j = 0; j < roleProps.length; ++j) {
				LiteYukonRoleProperty p = roleProps[j];
                YukonRoleProperty yp = YukonRoleProperty.getForId(p.getRolePropertyID());
                RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
				if (YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(yp, user)) {
					out.println("propertyid: " + p.getRolePropertyID() + "&nbsp&nbsp&nbspkey: " + p.getKeyName() + "&nbsp&nbsp&nbspdefault: " + p.getDefaultValue() + "&nbsp&nbsp&nbspvalue: " + rolePropertyDao.getPropertyStringValue(yp, user) + "<br>");
				}
				else {
					out.println("propertyid: " + p.getRolePropertyID() + "&nbsp&nbsp&nbspkey: " + p.getKeyName() + "&nbsp&nbsp&nbspdefault: " + p.getDefaultValue() + "&nbsp&nbsp&nbspThis property is not attached to this user, it probably should be.  Check YukonGroupRole<br>");
				}
			}
			out.println("<br>");
		}
	}
%>	