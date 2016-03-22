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
<%@ attribute name="useTextAsValue" description="Use the text (label) as the value" %>

<cti:yukonListEntryList var="entryList" listName="${listName}" energyCompanyId="${energyCompanyId}"/>

<c:set var="valuePath" value="entryID"/>
<c:if test="${useTextAsValue}">
    <c:set var="valuePath" value="entryText"/>
</c:if>

<tags:selectWithItems id="${pageScope.id}" path="${path}" items="${entryList}" itemValue="${valuePath}" itemLabel="entryText" 
                      defaultItemValue="${pageScope.defaultItemValue}" 
                      defaultItemLabel="${pageScope.defaultItemLabel}"
                      onchange="${pageScope.onchange}"
                      inputClass="${pageScope.classes}"/>

