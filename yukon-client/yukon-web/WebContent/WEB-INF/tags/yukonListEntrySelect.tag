<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="defaultItemValue" %>
<%@ attribute name="defaultItemLabel" %>
<%@ attribute name="energyCompanyId" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="listName" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="onchange" %>
<%@ attribute name="classes" description="CSS class names applied to the select." %>

<cti:yukonListEntryList var="entryList" listName="${listName}" energyCompanyId="${energyCompanyId}"/>

<tags:selectWithItems id="${pageScope.id}" path="${path}" items="${entryList}" itemValue="entryID" itemLabel="entryText" 
                      defaultItemValue="${pageScope.defaultItemValue}" 
                      defaultItemLabel="${pageScope.defaultItemLabel}"
                      onchange="${pageScope.onchange}"
                      inputClass="${pageScope.classes}"/>

