<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="accountId" required="true" type="java.lang.Integer"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>


<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
	<spring:bind path="${path}">
		<cti:yukonListEntryValue entryId="${status.value}"/>
	</spring:bind>
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

	<cti:yukonListEntryList var="entryList" listName="${listName}" accountId="${accountId}"/>
	
	<tags:selectWithItems path="${path}" items="${entryList}" itemValue="entryID" itemLabel="entryText" defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}"/>

</cti:displayForPageEditModes>