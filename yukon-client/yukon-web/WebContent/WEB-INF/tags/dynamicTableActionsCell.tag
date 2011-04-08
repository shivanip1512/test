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

<cti:includeScript link="/JavaScript/dynamicTable.js"/>

<td class="actions">
    <cti:img key="up.disabled" hide="${!isFirst}" styleClass="disabledMoveUpBtn"/>
    <cti:img key="up" hide="${isFirst}" isButton="true" styleClass="moveUpBtn"/>
    <cti:img key="down.disabled" hide="${!isLast}" styleClass="disabledMoveDownBtn"/>
    <cti:img key="down" hide="${isLast}" isButton="true" styleClass="moveDownBtn"/>
    <cti:img key="remove" isButton="true" styleClass="removeBtn"/>
</td>
