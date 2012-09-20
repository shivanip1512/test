<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="energyCompanyId" required="true" type="java.lang.String"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>

<cti:yukonListEntryList var="entryList" listName="${listName}" energyCompanyId="${energyCompanyId}"/>

<tags:selectWithItems path="${path}" items="${entryList}" itemValue="entryID" itemLabel="entryText" 
                      defaultItemValue="${pageScope.defaultItemValue}" 
                      defaultItemLabel="${pageScope.defaultItemLabel}"
                      onchange="${pageScope.onchange}" />

