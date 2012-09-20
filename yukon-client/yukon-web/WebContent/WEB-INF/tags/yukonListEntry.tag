<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ tag body-content="empty" %>

<%@ attribute name="energyCompanyId" required="true" type="java.lang.String"%>
<%@ attribute name="value" required="true" type="java.lang.String"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<cti:yukonListEntryList var="entryList" listName="${listName}" energyCompanyId="${energyCompanyId}"/>
<tags:listItem value="${value}" items="${entryList}" itemValue="entryID" itemLabel="entryText"/>