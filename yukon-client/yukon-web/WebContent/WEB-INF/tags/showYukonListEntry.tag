<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ tag body-content="empty" %>
<%@ attribute name="energyCompanyId" required="true" type="java.lang.Integer"%>
<%@ attribute name="value" required="true" type="java.lang.String"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<cti:yukonListEntryList var="entryList" listName="${listName}" energyCompanyId="${energyCompanyId}"/>

<tags:showListEntry value="${value}" items="${entryList}" itemValue="entryID" itemLabel="entryText"/>
