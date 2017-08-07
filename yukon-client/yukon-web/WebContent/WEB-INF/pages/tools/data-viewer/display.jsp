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

    <cti:includeScript link="/resources/js/pages/yukon.tools.tdc.js"/>
    <flot:defaultIncludes />
    <div id="page-buttons" class="dn">
        <c:if test="${display.acknowledgable}">
            <tags:dynamicChoose updaterString="TDC/ALARM_DISPLAY/${display.displayId}" suffix="${display.displayId}">
                <tags:dynamicChooseOption optionId="MULT_ALARMS">
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" 
                        data-display-id="${display.displayId}" 
                        icon="icon-tick" 
                        classes="js-tdc-ack-alarms"/>
                </tags:dynamicChooseOption>
                <tags:dynamicChooseOption optionId="NO_ALARMS"> 
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" 
                        data-display-id="${display.displayId}" 
                        icon="icon-tick" 
                        classes="js-tdc-ack-alarms dn"/>
                </tags:dynamicChooseOption>
            </tags:dynamicChoose>
        </c:if>
        <c:if test="${display.type != cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
            <cti:url var="download" value="/tools/data-viewer/${display.displayId}/download"/>
            <cti:button nameKey="download" href="${download}" icon="icon-page-white-excel"/>
        </c:if>
    </div>
    <c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
    
        <div id="page-actions" class="dn">    
            <cti:url var="download" value="/tools/data-viewer/${display.displayId}/download"/>
                <cm:dropdownOption key=".download" href="${download}" icon="icon-page-white-excel"/>
            <cti:url var="copy" value="/tools/data-viewer/${display.displayId}/copy"/>
            <cti:msg2 key=".copy" var="copyTitle"/>
                <cm:dropdownOption key="yukon.web.modules.tools.tdc.copy" icon="icon-disk-multiple"
            data-display-id="${display.displayId}" data-copy-title="${copyTitle}" 
            classes="js-tdc-copy"/>
            <cti:url var="deleteCustomDisplay" value="/tools/data-viewer/${display.displayId}/deleteCustomDisplay"/>
                <cm:dropdownOption key=".display.DELETE" href="${deleteCustomDisplay}" icon="icon-cross"/>
        </div>
    </c:if>
    
    <cti:url var="url" value="/tools/data-viewer/${display.displayId}/page"/>
    <div data-url="${url}" data-static>
        <table class="compact-results-table has-actions has-alerts">
            <thead>
                <tr>
                    <th></th>
                    <c:forEach var="column" items="${display.columns}">
                        <c:choose>
                            <c:when test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_VALUE')}">
                                    <th></th>
                                    <th><i:inline key="yukon.common.value"/></th>
                            </c:when>
                            <c:when test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.STATE')}">
                                <c:if test="${not stateValue}">
                                    <th>${fn:escapeXml(column.title)}</th>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <th>${fn:escapeXml(column.title)}</th>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <th class="action-column"><c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}"><cti:icon icon="icon-cog" classes="M0"/></c:if></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="row" items="${result.resultList}">
                    <c:choose>
                        <c:when test="${row.isBlank() && display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                            <tr class="vh">
                                <td colspan="${display.columns.size() + 3}">&nbsp;</td> 
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr id="row-${row.pointId}">
                                <td>
                                    <c:if test="${display.acknowledgable}">
                                        <c:choose>
                                            <c:when test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                                                <c:set var="alarmIndicator" value="TDC/BG_COLOR_POINT/${row.pointId}/0"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="alarmIndicator" value="TDC/BG_COLOR_POINT/${row.pointId}/${row.condition}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="${colorStateBoxes.get(row.pointId).get(row.condition)}" 
                                            data-class-updater="${alarmIndicator}"></span>
                                    </c:if>
                                </td>
                                <c:forEach var="column" items="${display.columns}">
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_ID')}">
                                        <td>${fn:escapeXml(row.pointId)}</td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_NAME')}">
                                        <td>
                                            <cti:url var="pointEditor" value="/tools/points/${row.pointId}"/>
                                                <a href="${pointEditor}">${fn:escapeXml(row.pointName)}</a>
                                        </td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_TYPE')}">
                                        <td><i:inline key="${row.pointType}"/></td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_STATE')}">
                                        <td><i:inline key=".point.enabled.${row.pointEnabled}"/></td>
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
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_TYPE')}">
                                        <c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                                            <td><tags:paoType yukonPao="${row.device}"/></td>
                                        </c:if>
                                        <c:if test="${display.type != cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                                            <td><tags:paoType yukonPao="${row.device}" showLink="false"/></td>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_CURRENT_STATE')}">
                                        <td>${fn:escapeXml(row.deviceCurrentState)}</td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.DEVICE_ID')}">
                                        <td>${row.device.deviceId}</td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_VALUE')}">
                                        <td class="state-indicator">
                                            <c:if test="${row.pointType.status}">
                                                <cti:pointStatus pointId="${row.pointId}"/>
                                            </c:if>
                                        </td>
                                        <td><cti:pointValue pointId="${row.pointId}" format="VALUE_UNIT"/></td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_QUALITY')}">
                                        <td><cti:pointValue pointId="${row.pointId}" format="{quality}"/></td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.POINT_TIME_STAMP')}">
                                        <td><tags:historicalValue pao="${row.device}" pointId="${row.pointId}"/></td>
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
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.STATE')}">
                                        <c:if test="${not stateValue}">
                                            <td><cti:dataUpdaterValue type="TDC" identifier="STATE/${row.pointId}"/></td>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.TAG')}">
                                        <td>${fn:escapeXml(row.tagName)}</td>
                                    </c:if>
                                    <c:if test="${column.type == cti:constantValue('com.cannontech.common.tdc.model.ColumnType.U_OF_M')}">
                                        <td><cti:pointValue pointId="${row.pointId}" format="UNIT"/></td>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${display.acknowledgable && display.type != cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
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
                                <c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
                                    <td class="action-column">
                                        <cm:dropdown triggerClasses="fr vh">
                                            <tags:dynamicChoose updaterString="TDC/ALARM_COUNT_POINT/${row.pointId}" 
                                                    suffix="${row.pointId}">
                                                <tags:dynamicChooseOption optionId="ONE_ALARM">
                                                    <cm:dropdownOption key=".alarm.acknowledge" icon="icon-tick" 
                                                        data-point-id="${row.pointId}" data-condition="${row.condition}" 
                                                        classes="js-tdc-one-alarm-ack"/>
                                                    <li class="divider"></li>
                                                </tags:dynamicChooseOption>
                                                <tags:dynamicChooseOption optionId="MULT_ALARMS">
                                                    <cti:msg2 key=".alarm.acknowledge" var="title"/>
                                                    <cti:list var="arguments">
                                                        <cti:item value="${title}"/>
                                                        <cti:item value="${row.deviceName}"/>
                                                        <cti:item value="${row.pointName}"/>
                                                    </cti:list>
                                                    <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                    <cm:dropdownOption key=".alarm.acknowledge" icon="icon-tick" 
                                                        data-point-id="${row.pointId}" data-popup-title="${popupTitle}" 
                                                        classes="js-tdc-mult-alarm-ack" id="acknowledge-${row.pointId}"/>
                                                    <li class="divider"></li>
                                                </tags:dynamicChooseOption>
                                            </tags:dynamicChoose>
                                            <tags:dynamicChoose updaterString="TDC/MAN_ENTRY/${row.pointId}/${row.pointType.pointTypeId}/${hasPointValueColumn}" 
                                                    suffix="${row.pointId}">
                                                <cti:msg2 key=".manualEntry.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <tags:dynamicChooseOption optionId="TRUE">
                                                    <cm:dropdownOption key=".manualEntry.title" icon="icon-pencil" 
                                                        data-point-id="${row.pointId}" data-popup-title="${popupTitle}" 
                                                        classes="js-tdc-manual-entry" id="manualEntry-${row.pointId}"/>
                                                    <li class="divider"></li>
                                                </tags:dynamicChooseOption>
                                            </tags:dynamicChoose>
                                            <tags:dynamicChoose updaterString="TDC/MAN_CONTROL/${row.pointId}" 
                                                    suffix="${row.pointId}">
                                                <cti:msg2 key=".tdc.manualControl.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".tdc.popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <tags:dynamicChooseOption optionId="TRUE">
                                                    <cm:dropdownOption key=".manualControl.title" 
                                                        icon="icon-wrench" 
                                                        data-point-id="${row.pointId}" 
                                                        data-popup-title="${popupTitle}" 
                                                        data-device-id="${row.device.deviceId}" 
                                                        classes="js-tdc-manual-control" 
                                                        id="manualControl-${row.pointId}"/>
                                                </tags:dynamicChooseOption>
                                            </tags:dynamicChoose>
                                            <c:if test="${row.cog.tags}">
                                                <cti:msg2 key=".tags.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <cm:dropdownOption key=".tags.title" 
                                                    icon="icon-tag-blue" 
                                                    data-point-id="${row.pointId}" 
                                                    data-device-id="${row.device.deviceId}" 
                                                    data-popup-title="${popupTitle}" 
                                                    classes="js-tdc-tags" 
                                                    id="tagsDialog-${row.pointId}"/>
                                            </c:if>
                                            <c:if test="${row.cog.enableDisable}">
                                                <cti:msg2 key=".enableDisable.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <cm:dropdownOption key=".enableDisable.title" 
                                                    icon="icon-accept" 
                                                    data-point-id="${row.pointId}" 
                                                    data-popup-title="${popupTitle}" 
                                                    classes="js-tdc-enable-disable" 
                                                    id="enableDisable-${row.pointId}"/>
                                            </c:if>
                                            <c:if test="${row.cog.trend}">
                                                <cti:msg2 key=".trend.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <cm:dropdownOption key=".trend.title" 
                                                    icon="icon-chart-line" 
                                                    data-point-id="${row.pointId}" 
                                                    data-popup-title="${popupTitle}" 
                                                    classes="js-tdc-trend" 
                                                    id="trendDialog-${row.pointId}"/>
                                            </c:if>
                                            <c:if test="${row.cog.altScan}">
                                                <cti:msg2 key=".altScan.title" var="title"/>
                                                <cti:list var="arguments">
                                                    <cti:item value="${title}"/>
                                                    <cti:item value="${row.deviceName}"/>
                                                    <cti:item value="${row.pointName}"/>
                                                </cti:list>
                                                <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                                                <cm:dropdownOption key=".altScan.title" 
                                                    icon="icon-transmit-blue" 
                                                    data-popup-title="${popupTitle}" 
                                                    data-device-id="${row.device.deviceId}" 
                                                    data-device-name="${row.deviceName}" 
                                                    classes="js-tdc-alt-scan" 
                                                    id="altScan-${row.pointId}"/>
                                            </c:if>
                                        </cm:dropdown>
                                    </td>
                                </c:if>
                            </tr> 
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${pageable}">
            <tags:pagingResultsControls adjustPageCount="true" result="${result}"/>
        </c:if>
    </div>
    <tags:simplePopup id="tdc-popup" title=""/>
</cti:standardPage>