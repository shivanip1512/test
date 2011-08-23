<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag body-content="empty" description="Use inside a table inside :dynamicTable." %>
<%@ attribute name="tableId" required="true" %>
<%@ attribute name="isFirst" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isLast" type="java.lang.Boolean" required="true" %>
<%@ attribute name="skipMoveButtons" type="java.lang.Boolean" %>

<cti:includeScript link="/JavaScript/dynamicTable.js"/>

<c:if test="${empty pageScope.skipMoveButtons}">
    <c:set var="skipMoveButtons" value="false"/>
</c:if>

<td class="actions">
    <c:if test="${!skipMoveButtons}">
        <cti:button renderMode="image" nameKey="up.disabled" disabled="true" styleClass="disabledMoveUpBtn"/>
        <cti:button renderMode="image" nameKey="up" styleClass="moveUpBtn"/> 
        <cti:button renderMode="image" nameKey="down.disabled" disabled="true" styleClass="disabledMoveDownBtn"/>
        <cti:button renderMode="image" nameKey="down" styleClass="moveDownBtn"/>
    </c:if>
    <cti:button renderMode="image" nameKey="remove" styleClass="removeBtn"/>
</td>
