<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:url var="dateUrl" value="/tools/data-viewer/${display.displayId}/page"/>
<form:form id="date-form" action="${dateUrl}"  method="get" modelAttribute="backingBean">
    <c:choose>
        <c:when test="${display.isAlarmDisplay()}">
            <span class="fr">
                <tags:selectWithItems id="alarmFilter" path="alarmFilter" items="${alarmFilters}"/>
                <c:if test="${backingBean.alarmFilter eq 'ALARM_HISTORY'}">
                    <span class="fr"><dt:date id="date" path="date" value="${backingBean.date}" forceDisplayPicker="true"/></span>
                </c:if>
            </span>
        </c:when>
        <c:otherwise>
            <span class="fr"><dt:date id="date" path="date" value="${backingBean.date}" forceDisplayPicker="true"/></span>
        </c:otherwise>
    </c:choose>
</form:form>
<input id="display-id" type="hidden" value="${display.displayId}">
<cti:url var="url" value="/tools/data-viewer/${display.displayId}/page">
    <cti:param name="date" value="${backingBean.date}"/>
    <cti:param name="alarmFilter" value="${backingBean.alarmFilter}"/>
</cti:url>
<div data-url="${url}" data-static>
    <table class="compact-results-table has-actions has-alerts">
        <thead>
            <tr>
                <th></th>
                <c:forEach var="column" items="${sortableColumns}">
                    <tags:sort column="${column}"/>
                </c:forEach>
                <c:if test="${display.isAlarmDisplay()}">
                    <th/>
                </c:if>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:set var="isPointLinkVisible" value= "false" />
            <cti:checkRolesAndProperties value="MANAGE_POINTS" level="RESTRICTED">
                <c:set var="isPointLinkVisible" value= "true" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <c:set var="isPointLinkVisible" value= "true" />
            </cti:checkRolesAndProperties>
            <c:forEach var="row" items="${result.resultList}">
                <tr id="row-${row.pointId}">
                    <td>
                        <c:if test="${display.acknowledgable}">
                            <c:set var="alarmIndicator" value="TDC/BG_COLOR_POINT/${row.pointId}/${row.condition}"/>
                            <span class="${colorStateBoxes.get(row.pointId).get(row.condition)}" 
                                data-class-updater="${alarmIndicator}"></span>
                        </c:if>
                    </td>
                    <c:forEach var="column" items="${display.columns}">
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_NAME')}">
                            <td>
                                <c:choose>
                                    <c:when test="${!isPointLinkVisible}">${fn:escapeXml(row.pointName)}</c:when>
                                    <c:otherwise>
                                        <cti:url var="pointEditor" value="/tools/points/${row.pointId}"/>
                                        <a href="${pointEditor}">${fn:escapeXml(row.pointName)}</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_NAME')}">
                            <c:choose>
                                <c:when test="${!empty row.device}">
                                    <td>
                                        <cti:paoDetailUrl yukonPao="${row.device}">
                                            <c:if test="${!empty row.deviceName}">${fn:escapeXml(row.deviceName)}</c:if>
                                        </cti:paoDetailUrl>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>${fn:escapeXml(row.deviceName)}</td>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TIME_STAMP')}">
                            <td><cti:formatDate value="${row.date}" type="BOTH"/></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TEXT_MESSAGE')}">
                            <td>${fn:escapeXml(row.textMessage)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.ADDITIONAL_INFO')}">
                            <td>${fn:escapeXml(row.additionalInfo)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DESCRIPTION')}">
                            <td>${fn:escapeXml(row.description)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.USERNAME')}">
                            <td>${fn:escapeXml(row.userName)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TAG')}">
                            <td>${fn:escapeXml(row.tagName)}</td>
                        </c:if>
                    </c:forEach>
                    <c:if test="${display.acknowledgable}">
                        <td>
                            <tags:dynamicChoose updaterString="TDC/ALARM_POINT_CONDITION/${row.pointId}/${row.condition}" suffix="${row.pointId}">
                                <tags:dynamicChooseOption optionId="ONE_ALARM">
                                    <cti:button nameKey="alarm.acknowledge" 
                                        icon="icon-tick" 
                                        data-point-id="${row.pointId}" 
                                        data-condition="${row.condition}" 
                                        classes="js-tdc-one-alarm-ack-btn fr" 
                                        renderMode="buttonImage"/>
                                </tags:dynamicChooseOption>
                            </tags:dynamicChoose>
                        </td>
                    </c:if>
                </tr> 
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls adjustPageCount="true" result="${result}"/>
</div>