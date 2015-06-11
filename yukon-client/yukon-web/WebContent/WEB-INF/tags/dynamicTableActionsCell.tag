<%@ tag body-content="empty" description="Use inside a table inside :dynamicTable." %>

<%@ attribute name="tableId" required="true" %>
<%@ attribute name="isFirst" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isLast" type="java.lang.Boolean" required="true" %>
<%@ attribute name="skipMoveButtons" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeScript link="/resources/js/common/yukon.table.dynamic.js"/>

<c:if test="${empty pageScope.skipMoveButtons}">
    <c:set var="skipMoveButtons" value="false"/>
</c:if>

<td class="actions">
    <cti:button renderMode="buttonImage" nameKey="remove" classes="js-remove-btn fr" icon="icon-cross"/>
    <c:if test="${!skipMoveButtons}">
        <cti:button renderMode="buttonImage" nameKey="up.disabled" disabled="true" classes="disabledMoveUpBtn right fr" icon="icon-bullet-go-up"/>
        <cti:button renderMode="buttonImage" nameKey="up" classes="moveUpBtn right fr" icon="icon-bullet-go-up"/> 
        <cti:button renderMode="buttonImage" nameKey="down.disabled" disabled="true" classes="disabledMoveDownBtn left fr" icon="icon-bullet-go-down"/>
        <cti:button renderMode="buttonImage" nameKey="down" classes="moveDownBtn left fr" icon="icon-bullet-go-down"/>
    </c:if>
</td>
