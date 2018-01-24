<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                                    <span title="${disabledTitle}"><cm:dropdownOption key="components.button.delete.label" icon="icon-cross" disabled="true"/></span>
                                </c:when>
                                <c:otherwise>
                                    <cti:url value="/meter/historicalReadings/edit" var="editUrl">
                                        <cti:param name="pointId" value="${pointId}"/>
                                        <cti:formatDate var="dateTime" type="FULL" value="${point.pointDataTimeStamp}"/>
                                        <cti:param name="timestamp" value="${dateTime}"/>
                                        <cti:param name="value" value="${point.value}"/>
                                    </cti:url>
                                    <cti:msg2 var="editTitle" key=".value.editValue.title"/>
                                    <div class="dn js-edit-value-${id}" data-dialog data-title="${editTitle}"
                                        data-url="${editUrl}" data-event="yukon:historical:readings:editValue" data-point-id="${pointId}"></div>
                                    <cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" data-popup=".js-edit-value-${id}"/>
                                    <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="OWNER">
                                        <cm:dropdownOption id="deleteValue_${id}" key="components.button.delete.label" icon="icon-cross" classes="js-hide-dropdown" 
                                            data-ok-event="yukon:historical:readings:delete" data-timestamp="${dateTime}" data-value="${point.value}" data-pointId="${pointId}"/>
                                        <d:confirm on="#deleteValue_${id}" nameKey="value.confirmDelete" argument="${point.pointDataTimeStamp}"/>
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
