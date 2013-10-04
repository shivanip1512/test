<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="tdc.display.${mode}">

    <cti:includeScript link="/JavaScript/yukon/ui/confirm_dialog_manager.js" />
    <cti:includeScript link="/JavaScript/yukon/yukon.tdc.js" />
    <flot:defaultIncludes />

    <div id="f-page-buttons" class="dn">
        <c:if test="${display.acknowledgable}">
            <tags:dynamicChoose updaterString="TDC/ALARM_DISPLAY/${display.displayId}" suffix="${display.displayId}">
                <tags:dynamicChooseOption optionId="MULT_ALARMS">
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" displayId="${display.displayId}" icon="icon-tick" classes="f-display-alarm-ack" />
                </tags:dynamicChooseOption>
                <tags:dynamicChooseOption optionId="NO_ALARMS">
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" displayId="${display.displayId}" icon="icon-tick" classes="f-display-alarm-ack dn" />
                </tags:dynamicChooseOption>
            </tags:dynamicChoose>
        </c:if>
        <cti:url var="download" value="/tdc/${display.displayId}/download" />
        <cti:button nameKey="download" href="${download}" icon="icon-page-white-excel" />
    </div>

    <table class="compactResultsTable pointHighlighting has-actions separated">
        <thead>
            <tr>
                <th></th>
                <c:forEach var="column" items="${display.columns}">
                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_VALUE')}">
                        <th></th>
                    </c:if>
                    <th>${fn:escapeXml(column.title)}</th>
                </c:forEach>
                <th></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="row" items="${displayData}">
                <c:set var="dataVar" value=""/>
                <c:if test="${display.acknowledgable}">
                    <c:choose>
                        <c:when test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                            <c:set var="bgColor" value="data-class-updater='TDC/BG_COLOR_POINT/${row.pointId}/0'" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="bgColor" value="data-class-updater='TDC/BG_COLOR_POINT/${row.pointId}/${row.condition}'" />
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <tr id="row-${row.pointId}">
                    <td><c:if test="${display.acknowledgable}">
                            <span class="${colorStateBoxes.get(row.pointId).get(row.condition)}" ${bgColor}</span>
                        </c:if></td>
                    <c:forEach var="column" items="${display.columns}">
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_ID')}">
                            <td>${fn:escapeXml(row.pointId)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_NAME')}">
                            <td>${fn:escapeXml(row.pointName)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_TYPE')}">
                            <td><i:inline key="${row.pointType}" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_STATE')}">
                            <td><i:inline key=".point.enabled.${row.pointEnabled}" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_NAME')}">
                            <td>${fn:escapeXml(row.deviceName)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_TYPE')}">
                            <td><tags:paoType yukonPao="${row.device}" showLink="false" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_CURRENT_STATE')}">
                            <td>${fn:escapeXml(row.deviceCurrentState)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_ID')}">
                            <td>${row.device.deviceId}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_VALUE')}">
                            <td class="state-indicator tar"><c:if test="${row.pointType.status}">
                                    <cti:pointStatusColor pointId="${row.pointId}" styleClass="box stateBox" background="true">&nbsp;</cti:pointStatusColor>
                                </c:if></td>
                            <td><cti:pointValue pointId="${row.pointId}" format="VALUE_UNIT" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_QUALITY')}">
                            <td><cti:pointValue pointId="${row.pointId}" format="{quality}" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_TIME_STAMP')}">
                            <td><tags:historicalValue device="${row.device}" pointId="${row.pointId}" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TIME_STAMP')}">
                            <td><cti:formatDate value="${row.date}" type="BOTH" /></td>
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
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.STATE')}">
                            <td><cti:dataUpdaterValue type="TDC" identifier="STATE/${row.pointId}" /></td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TAG')}">
                            <td>${fn:escapeXml(row.tagName)}</td>
                        </c:if>
                        <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.U_OF_M')}">
                            <td><cti:pointValue pointId="${row.pointId}" format="UNIT" /></td>
                        </c:if>
                    </c:forEach>
                    <c:if test="${display.acknowledgable && display.type != cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                        <td><tags:dynamicChoose updaterString="TDC/ALARM_POINT_CONDITION/${row.pointId}/${row.condition}" suffix="${row.pointId}">
                                <tags:dynamicChooseOption optionId="ONE_ALARM">
                                    <cti:button nameKey="alarm.acknowledge" icon="icon-tick" pointId="${row.pointId}" condition="${row.condition}" classes="f-one-alarm-ack-b fr" renderMode="buttonImage" />
                                </tags:dynamicChooseOption>
                            </tags:dynamicChoose></td>
                    </c:if>
                    <c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                        <td style="width: 56px;">
                            <cm:dropdown id="dropdown_${row.pointId}" containerCssClass="fr vh">
                                <tags:dynamicChoose updaterString="TDC/ALARM_COUNT_POINT/${row.pointId}" suffix="${row.pointId}">
                                    <tags:dynamicChooseOption optionId="ONE_ALARM">
                                        <cm:dropdownOption key=".alarm.acknowledge" icon="icon-tick" pointId="${row.pointId}" condition="${row.condition}" classes="clearfix f-one-alarm-ack"></cm:dropdownOption>
                                        <li class="divider"></li>
                                    </tags:dynamicChooseOption>
                                    <tags:dynamicChooseOption optionId="MULT_ALARMS">
                                        <cti:msg2 key=".alarm.acknowledge" var="title" />
                                        <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                        <cm:dropdownOption key=".alarm.acknowledge" icon="icon-tick" pointId="${row.pointId}" popupTitle="${popupTitle}" classes="clearfix f-mult-alarm-ack" id="acknowledge-${row.pointId}"></cm:dropdownOption>
                                        <li class="divider"></li>
                                    </tags:dynamicChooseOption>
                                </tags:dynamicChoose>
                                <tags:dynamicChoose updaterString="TDC/MAN_ENTRY/${row.pointId}/${row.pointType.pointTypeId}/${hasPointValueColumn}" suffix="${row.pointId}">
                                    <cti:msg2 key=".manualEntry.title" var="title" />
                                    <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <tags:dynamicChooseOption optionId="TRUE">
                                        <cm:dropdownOption key=".manualEntry.title" icon="icon-pencil" pointId="${row.pointId}" popupTitle="${popupTitle}" classes="clearfix f-manualEntry" id="manualEntry-${row.pointId}"></cm:dropdownOption>
                                        <li class="divider"></li>
                                    </tags:dynamicChooseOption>
                                </tags:dynamicChoose>
                                <tags:dynamicChoose updaterString="TDC/MAN_CONTROL/${row.pointId}" suffix="${row.pointId}">
                                    <cti:msg2 key=".tdc.manualControl.title" var="title" />
                                    <cti:msg2 key=".tdc.popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <tags:dynamicChooseOption optionId="TRUE">
                                        <cm:dropdownOption key=".manualControl.title" icon="icon-wrench" pointId="${row.pointId}" popupTitle="${popupTitle}" deviceId="${row.device.deviceId}" classes="clearfix f-manualControl" id="manualControl-${row.pointId}"></cm:dropdownOption>
                                    </tags:dynamicChooseOption>
                                </tags:dynamicChoose>
                                <c:if test="${row.cog.tags}">
                                    <cti:msg2 key=".tags.title" var="title" />
                                    <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <cm:dropdownOption key=".tags.title" icon="icon-tag-blue" pointId="${row.pointId}" deviceId="${row.device.deviceId}" popupTitle="${popupTitle}" classes="clearfix f-tags" id="tagsDialog-${row.pointId}"></cm:dropdownOption>
                                </c:if>
                                <c:if test="${row.cog.enableDisable}">
                                    <cti:msg2 key=".enableDisable.title" var="title" />
                                    <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <cm:dropdownOption key=".enableDisable.title" icon="icon-accept" pointId="${row.pointId}" popupTitle="${popupTitle}" classes="clearfix f-enableDisable" id="enableDisable-${row.pointId}"></cm:dropdownOption>
                                </c:if>
                                <c:if test="${row.cog.trend}">
                                    <cti:msg2 key=".trend.title" var="title" />
                                    <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <cm:dropdownOption key=".trend.title" icon="icon-chart-line" pointId="${row.pointId}" popupTitle="${popupTitle}" classes="clearfix f-trend" id="trendDialog-${row.pointId}"></cm:dropdownOption>
                                </c:if>
                                <c:if test="${row.cog.altScan}">
                                    <cti:msg2 key=".altScan.title" var="title" />
                                    <cti:msg2 key=".popupTitle" arguments="${title},${row.deviceName},${row.pointName}" argumentSeparator="," var="popupTitle" />
                                    <cm:dropdownOption key=".altScan.title" icon="icon-transmit-blue" popupTitle="${popupTitle}" deviceId="${row.device.deviceId}" deviceName="${row.deviceName}" classes="clearfix f-altScan" id="altScan-${row.pointId}"></cm:dropdownOption>
                                </c:if>
                            </cm:dropdown></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:simplePopup id="tdc-popup" title="" />
</cti:standardPage>