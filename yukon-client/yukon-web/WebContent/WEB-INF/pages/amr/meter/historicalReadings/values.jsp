<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings">

<cti:flashScopeMessages/>

<table class="compact-results-table has-actions">
    <thead>
        <tr>
            <tags:sort column="${timestamp}"/>
            <tags:sort column="${value}"/>
            <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </cti:checkRolesAndProperties>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="point" items="${points}">
            <cti:uniqueIdentifier var="id" />
            <tr>
                <td><cti:pointValueFormatter format="DATE" value="${point}" /></td>
                <td><cti:pointValueFormatter format="SHORT" value="${point}" /></td>
                <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <c:choose>
                                <c:when test="${point.pointDataTimeStamp == maxTimestamp}">
                                    <cti:msg2 var="disabledTitle" key=".editDisabled"/>
                                    <span title="${disabledTitle}"><cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" disabled="true"/></span>
                                    <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="OWNER">
                                    <span title="${disabledTitle}"><cm:dropdownOption key="components.button.delete.label" icon="icon-delete" disabled="true"/></span>
                                    </cti:checkRolesAndProperties>
                                </c:when>
                                <c:otherwise>
                                    <cti:url value="/meter/historicalReadings/edit" var="editUrl">
                                        <cti:param name="pointId" value="${pointId}"/>
                                        <cti:formatDate var="dateTime" type="BOTH" value="${point.pointDataTimeStamp}"/>
                                        <cti:param name="timestamp" value="${dateTime}"/>
                                        <cti:param name="value" value="${point.value}"/>
                                    </cti:url>
                                    <cti:msg2 var="editTitle" key=".value.editValue.title"/>
                                    <div class="dn js-edit-value-${id}" data-dialog data-title="${editTitle}"
                                        data-url="${editUrl}" data-event="yukon:historical:readings:editValue" data-point-id="${pointId}"></div>
                                    <cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" data-popup=".js-edit-value-${id}"/>
                                    <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="OWNER">
                                        <cm:dropdownOption data-popup=".js-delete-value-${id}" key="components.button.delete.label" icon="icon-delete"/>
                                        <div class="dn js-delete-value-${id}" data-dialog data-title="<cti:msg2 key=".value.confirmDelete.title"/>" 
                                            data-ok-text="<cti:msg2 key="components.button.delete.label"/>" data-event="yukon:historical:readings:delete" 
                                            data-timestamp="${dateTime}" data-value="${point.value}" data-pointId="${pointId}">
                                            <i:inline key=".value.confirmDelete.message" arguments="${point.pointDataTimeStamp}"/>
                                        </div>
                                    </cti:checkRolesAndProperties>
                                </c:otherwise>
                            </c:choose>
                        </cm:dropdown>
                    </td>
                </cti:checkRolesAndProperties>
            </tr>
        </c:forEach>
    </tbody>
</table>
</cti:msgScope>

<cti:includeScript link="/resources/js/pages/yukon.historical.readings.js"/>
