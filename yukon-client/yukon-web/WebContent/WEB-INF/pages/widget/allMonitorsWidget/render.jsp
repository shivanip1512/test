<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<style type="text/css">
    .monitor-subs th.name {width: 45%;}
    .monitor-subs th.data {width: 20%;}
    .monitor-subs th.monitoring {width: 25%;}
    .monitor-subs th.enable {width: 5%;}
</style>

<cti:msg2 var="undoText" key="yukon.common.undo"/>

<cti:checkRolesAndProperties value="DEVICE_DATA_MONITORING">
<cti:msgScope paths="widgetClasses.DeviceDataMonitorsWidget">
    <c:if test="${not empty deviceDataMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".name"/></th>
                <th class="data tar"><i:inline key=".violations"/></th>
                <th class="monitoring tar"><i:inline key=".monitoring"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${deviceDataMonitors}">
                <c:set var="monitorType" value="device-data-monitor"/>

                <tr class="monitor js-monitor-${monitorType}-${monitor.id}" data-removed-text="${removedText}">
                    <%-- monitor name --%>
                    <td>

                        <cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
                            <cti:param name="monitorId" value="${monitor.id}" />
                        </cti:url>
                        <a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitor.name)}"/>">${fn:escapeXml(monitor.name)}</a>
                    </td>

                    <c:set var="cssClass" value="${monitor.enabled ? '' : 'very-disabled-look'}"/>

                    <%-- violations count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="VIOLATIONS_COUNT/${monitor.id}" styleClass="js-violations-count ${cssClass}"/>
                    </td>

                    <%-- monitoring count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="MONITORING_COUNT/${monitor.id}" styleClass="js-monitoring-count ${cssClass}"/>
                    </td>

                    <%-- cog-menu --%>
                    <td>
                        <cm:dropdown icon="icon-cog" data-name="device-data-monitor" data-id="${monitor.id}" data-status="${monitor.enabled}">
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${monitor.groupName}"/>
                                <cti:param name="monitorType" value="Device Data"/>
                                <cti:param name="monitorId" value="${monitor.id}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.modules.amr.mapViolations" 
                                               href="${mapUrl}" newTab="true"/>
                            <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitor.id} ${!monitor.enabled? 'dn': ''}" 
                                               icon="icon-delete"/>
                            <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitor.id} ${monitor.enabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="OUTAGE_PROCESSING">
<cti:msgScope paths="widgets.outageMonitorsWidget">
    <c:if test="${not empty outageMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${outageMonitors}">
                <c:set var="monitorId" value="${monitor.outageMonitorId}"/>
                <c:set var="monitorName" value="${monitor.name}"/>
                <c:set var="monitorType" value="outage-monitor"/>

                <tr class="monitor js-monitor-${monitorType}-${monitor.outageMonitorId}" data-removed-text="${removedText}">
                    <%-- monitor name --%>
                    <td>
                        <cti:url var="viewOutageProcessingUrl" value="/amr/outageProcessing/process/process">
                            <cti:param name="outageMonitorId" value="${monitorId}"/>
                        </cti:url>
                        <a href="${viewOutageProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                    </td>

                    <c:set var="isOutageEnabled" value="${monitor.evaluatorStatus eq 'ENABLED'}"/>
                    <c:set var="cssClass" value="${isOutageEnabled ? '' : 'very-disabled-look'}"/>

                    <%-- violations count --%>
                    <td id="violations_${monitorId}" class="tar">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT" styleClass="js-violations-count ${cssClass}"/>
                    </td>

                    <%-- monitoring count --%>
                    <td id="monitoring_${monitorId}" class="tar">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/MONITORING_COUNT" styleClass="js-monitoring-count ${cssClass}"/>
                    </td>

                    <td>
                        <cm:dropdown icon="icon-cog" data-name="outage-monitor" data-id="${monitorId}" data-status="${monitor.evaluatorStatus eq 'ENABLED'}">
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${monitor.groupName}"/>
                                <cti:param name="monitorType" value="Outage"/>
                                <cti:param name="monitorId" value="${monitorId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.modules.amr.mapViolations" href="${mapUrl}" newTab="true"/>
                            <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitorId} ${!(isOutageEnabled)? 'dn': ''}" 
                                               icon="icon-delete"/>
                            <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitorId} ${isOutageEnabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="TAMPER_FLAG_PROCESSING">
<cti:msgScope paths="widgets.tamperFlagMonitorsWidget">
    <c:if test="${not empty tamperFlagMonitorsWidgetError }">
        <div class="error">${tamperFlagMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty tamperFlagMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${tamperFlagMonitors}">
                <c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
                <c:set var="monitorName" value="${monitor.name}"/>
                <c:set var="monitorType" value="tamper-flag-monitor"/>

                <tr class="monitor js-monitor-${monitorType}-${monitorId}" data-removed-text="${removedText}">

                    <%-- monitor name --%>
                    <td>
                        <cti:url var="viewTamperFlagProcessingUrl" value="/amr/tamperFlagProcessing/process/process">
                            <cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
                        </cti:url>
                        <a href="${viewTamperFlagProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                    </td>

                    <c:set var="isTamperEnabled" value="${monitor.evaluatorStatus eq 'ENABLED'}"/>
                    <c:set var="cssClass" value="${isTamperEnabled ? '' : 'very-disabled-look'}"/>

                    <%-- violations count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT" styleClass="js-violations-count ${cssClass}"/>
                    </td>

                    <%-- monitoring count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/MONITORING_COUNT" styleClass="js-monitoring-count ${cssClass}"/>
                    </td>

                    <%-- cog-menu --%>
                    <td>
                        <cm:dropdown icon="icon-cog" data-name="tamper-flag-monitor" data-id="${monitorId}">
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagGroupBase}${monitor.name}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.modules.amr.mapViolations" href="${mapUrl}" newTab="true"/>
                            <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitorId} ${!(isTamperEnabled)? 'dn': ''}" 
                                               icon="icon-delete"/>
                            <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitorId} ${isTamperEnabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="STATUS_POINT_MONITORING">
<cti:msgScope paths="widgets.statusPointMonitorsWidget">
    <c:if test="${not empty statusPointMonitorsWidgetError}">
        <div class="error"><i:inline key="${statusPointMonitorsWidgetError}"/></div>
    </c:if>
    <c:if test="${not empty statusPointMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data">&nbsp;</th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${statusPointMonitors}">
            <c:set var="monitorId" value="${monitor.statusPointMonitorId}"/>
            <c:set var="monitorName" value="${monitor.name}"/>
            <c:set var="monitorType" value="status-point-monitor"/>

            <tr class="monitor js-monitor-${monitorType}-${monitorId}" data-removed-text="${removedText}">
                <%-- monitor name --%>
                <td colspan="2">
                    <cti:url var="viewStatusPointMonitoringUrl" value="/amr/statusPointMonitoring/viewPage">
                        <cti:param name="statusPointMonitorId" value="${monitorId}"/>
                    </cti:url>
                    <a href="${viewStatusPointMonitoringUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                </td>

                    <c:set var="isStatusEnabled" value="${monitor.evaluatorStatus eq 'ENABLED'}"/>
                    <c:set var="cssClass" value="${isStatusEnabled ? '' : 'very-disabled-look'}"/>

                <%-- monitoring count --%>
                <td class="tar">
                    <cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${monitorId}/MONITORING_COUNT" styleClass="js-monitoring-count ${cssClass}"/>
                </td>

                <%-- cog-menu --%>
                <td>
                    <cm:dropdown icon="icon-cog" data-name="status-point-monitor" data-id="${monitorId}">
                        <cti:url var="mapUrl" value="/tools/map/dynamic">
                            <cti:param name="collectionType" value="group"/>
                            <cti:param name="group.name" value="${monitor.groupName}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}" newTab="true"/>
                        <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitorId} ${!(isStatusEnabled)? 'dn': ''}" 
                                               icon="icon-delete"/>
                        <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitorId} ${isStatusEnabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                    </cm:dropdown>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="PORTER_RESPONSE_MONITORING">
<cti:msgScope paths="widgets.porterResponseMonitorsWidget">
    <c:if test="${not empty porterResponseMonitorError}">
        <div class="error">${porterResponseMonitorError}</div>
    </c:if>

    <c:if test="${not empty porterResponseMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".name"/></th>
                <th class="data">&nbsp;</th>
                <th class="monitoring">&nbsp;</th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${porterResponseMonitors}">
            <c:set var="monitorType" value="porter-response-monitor"/>

            <tr class="monitor js-monitor-${monitorType}-${monitor.monitorId}" data-removed-text="${removedText}">
                <%-- monitor name --%>
                <td colspan="3">
                    <cti:url var="viewMonitorUrl" value="/amr/porterResponseMonitor/viewPage">
                        <cti:param name="monitorId" value="${monitor.monitorId}" />
                    </cti:url>
                    <a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitor.name)}"/>">${fn:escapeXml(monitor.name)}</a>
                </td>

                <c:set var="isPorterEnabled" value="${monitor.evaluatorStatus eq 'ENABLED'}"/>
                <c:set var="cssClass" value="${isPorterEnabled ? '' : 'very-disabled-look'}"/>

                <%-- cog-menu --%>
                <td>
                    <cm:dropdown icon="icon-cog" data-name="porter-response-monitor" data-id="${monitor.monitorId}">
                        <cti:url var="mapUrl" value="/tools/map/dynamic">
                            <cti:param name="collectionType" value="group"/>
                            <cti:param name="group.name" value="${monitor.groupName}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}" newTab="true"/>
                        <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitor.monitorId} ${!(isPorterEnabled)? 'dn': ''}" 
                                               icon="icon-delete"/>
                        <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitor.monitorId} ${isPorterEnabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                    </cm:dropdown>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="VALIDATION_ENGINE">
<cti:msgScope paths="widgets.validationMonitorsWidget">
    <c:if test="${not empty validationMonitorsWidgetError}">
        <div class="error">${validationMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty validationMonitors}">
     <div class="scroll-lg">
    <table class="compact-results-table monitor-subs dashed stacked bcs has-alerts has-actions" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.threshold"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${validationMonitors}">
            <c:set var="monitorId" value="${monitor.validationMonitorId}"/>
            <c:set var="monitorName" value="${monitor.name}"/>
            <c:set var="monitorType" value="validation-monitor"/>

            <tr class="monitor js-monitor-${monitorType}-${monitorId}" data-removed-text="${removedText}">

                <%-- monitor name --%>
                <td>
                    <cti:url var="viewValidationMonitorEditorUrl" value="/amr/vee/monitor/${monitorId}/view"/>
                    <a href="${viewValidationMonitorEditorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>" >
                        ${fn:escapeXml(monitorName)}
                    </a>
                </td>

                    <c:set var="isValidationEnabled" value="${monitor.evaluatorStatus eq 'ENABLED'}"/>
                    <c:set var="cssClass" value="${isValidationEnabled? '' : 'very-disabled-look'}"/>

                <%-- threshold --%>
                <td class="tar js-threshold ${cssClass}">
                    <cti:msg2 key="yukon.common.float.tenths" argument="${monitor.reasonableMaxKwhPerDay}"/>&nbsp;<i:inline key=".thresholdUnits"/>
                </td>

                <%-- monitoring count --%>
                <td class="tar">
                    <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT" styleClass="js-monitoring-count ${cssClass}"/>
                </td>

                <%-- cog-menu --%>
                <td>
                    <cm:dropdown icon="icon-cog" data-name="validation-monitor" data-id="${monitorId}">
                        <cti:url var="mapUrl" value="/tools/map/dynamic">
                            <cti:param name="collectionType" value="group"/>
                            <cti:param name="group.name" value="${monitor.groupName}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}" newTab="true"/>
                        <cm:dropdownOption key="yukon.common.disable" classes="js-toggle-status js-disable-${monitorType}-${monitorId} ${!(isValidationEnabled)? 'dn': ''}" 
                                               icon="icon-delete"/>
                        <cm:dropdownOption key="yukon.common.enable" classes="js-toggle-status js-enable-${monitorType}-${monitorId} ${isValidationEnabled? 'dn': ''}" 
                                               icon="icon-accept"/>
                    </cm:dropdown>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:if>
</cti:msgScope>
</cti:checkRolesAndProperties>

</table>

<c:if test="${ empty deviceDataMonitors and
               empty outageMonitors and
               empty tamperFlagMonitors and
               empty statusPointMonitors and
               empty porterResponseMonitors and
               empty validationMonitors
             }">
    <div class="empty-list"><i:inline key=".emptyList"/></div>
</c:if>

<div class="action-area">
    <cti:checkRolesAndProperties value="VALIDATION_ENGINE">
        <c:if test="${not empty validationMonitors}">
            <cti:classUpdater type="VALIDATION_PROCESSING" identifier="SHOW_VIOLATIONS">
                <cti:url var="reviewUrl" value="/amr/veeReview/home"/>
                <a href="${reviewUrl}"><i:inline key="yukon.web.widgets.validationMonitorsWidget.review"/></a>
                <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="TOTAL_VIOLATIONS" styleClass="badge badge-error"/>
            </cti:classUpdater>
        </c:if>
    </cti:checkRolesAndProperties>
    <c:if test="${not isSubscribedWidget}">
        <cm:dropdown key="components.button.create.label" icon="icon-plus-green" type="button" triggerClasses="fr" menuClasses="no-icons">
            <cti:checkRolesAndProperties value="DEVICE_DATA_MONITORING">
	            <cti:url var="url" value="/amr/deviceDataMonitor/createPage"/>
                <cm:dropdownOption key="widgetClasses.DeviceDataMonitorsWidget.name" href="${url}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="OUTAGE_PROCESSING">
                <cti:url var="url" value="/amr/outageProcessing/monitorEditor/create"/>
                <cm:dropdownOption key="widgets.outageMonitorsWidget.tableHeader.name" href="${url}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="TAMPER_FLAG_PROCESSING">
                <cti:url var="url" value="/amr/tamperFlagProcessing/create"/>
                <cm:dropdownOption key="widgets.tamperFlagMonitorsWidget.tableHeader.name" href="${url}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="STATUS_POINT_MONITORING">
                <cti:url var="url" value="/amr/statusPointMonitoring/creationPage"/>
                <cm:dropdownOption key="widgets.statusPointMonitorsWidget.tableHeader.name" href="${url}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="PORTER_RESPONSE_MONITORING">
                <cti:url var="url" value="/amr/porterResponseMonitor/createPage"/>
                <cm:dropdownOption key="widgets.porterResponseMonitorsWidget.name" href="${url}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="VALIDATION_ENGINE">
                <cti:url var="url" value="/amr/vee/monitor/create"/>
                <cm:dropdownOption key="widgets.validationMonitorsWidget.tableHeader.name" href="${url}"/>
            </cti:checkRolesAndProperties>
        </cm:dropdown>
    </c:if>
</div>
<cti:includeScript link="/resources/js/widgets/yukon.widget.monitors.js"/>