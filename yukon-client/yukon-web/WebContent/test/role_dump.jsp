<%
	// Dumps out all the roles and properties for the logged in user
%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>

<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
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
		r = AuthFuncs.checkRole(user, r.getRoleID());
		if(r != null) {
			out.println("roleid: " + r.getRoleID() + "&nbsp&nbsp&nbspname: " + r.getRoleName() + "<br>");
			List roleProps = AuthFuncs.getRoleProperties(r);
			for(Iterator j = roleProps.iterator(); j.hasNext();) {
						System.out.println("h2");
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) j.next();
				if(AuthFuncs.checkRoleProperty(user, p.getRolePropertyID())) {
					out.println("propertyid: " + p.getRolePropertyID() + "&nbsp&nbsp&nbspkey: " + p.getKeyName() + "&nbsp&nbsp&nbspdefault: " + p.getDefaultValue() + "&nbsp&nbsp&nbspvalue: " + AuthFuncs.getRolePropertyValue(user, p.getRolePropertyID()) + "<br>");
				}
				else {
					out.println("propertyid: " + p.getRolePropertyID() + "&nbsp&nbsp&nbspkey: " + p.getKeyName() + "&nbsp&nbsp&nbspdefault: " + p.getDefaultValue() + "&nbsp&nbsp&nbspThis property is not attached to this user, it probably should be.  Check YukonGroupRole or YukonUserRole<br>");
				}
			}
			out.println("<br>");
		}
	}
%>	