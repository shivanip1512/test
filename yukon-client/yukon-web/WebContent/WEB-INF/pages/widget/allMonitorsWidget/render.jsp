<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<script type="text/javascript">
    jQuery(function() {
        Yukon.Favorites.initSubscribe();
    });
</script>

<c:if test="${isSubscribedWidget}">
    <cti:msg2 var="removedText" key=".subscription.removed"/>
    <cti:msg2 var="undoText" key="yukon.common.undo"/>
</c:if>

<cti:msgScope paths="widgetClasses.DeviceDataMonitorsWidget">
    <c:if test="${not empty deviceDataMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th class="tar"><i:inline key=".violations"/></th>
                <th class="tar"><i:inline key=".monitoring"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${deviceDataMonitors}">
                <tr class="monitor">
                    <%-- monitor name --%>
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-name="${fn:escapeXml(monitor.name)}" data-subscription-type="DEVICE_DATA_MONITOR" data-ref-id="${monitor.id}"/>

                        <cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
                            <cti:param name="monitorId" value="${monitor.id}" />
                        </cti:url>
                        <a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitor.name)}"/>">${fn:escapeXml(monitor.name)}</a>
                    </td>

                    <%-- violations count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="${monitor.id}/VIOLATIONS_COUNT"/>
                    </td>

                    <%-- monitoring count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="${monitor.id}/MONITORING_COUNT"/>
                    </td>

                    <%-- enable/disable --%>
                    <td class="tar">

                        <c:choose>
                            <c:when test="${monitor.enabled}">
                                <tags:widgetActionRefreshImage method="toggleEnabledDeviceData"
                                    nameKey="disable" arguments="${monitor.name}" btnClass="fr"
                                    monitorId="${monitor.id}" icon="icon-enabled" />
                            </c:when>
                            <c:otherwise>
                                <tags:widgetActionRefreshImage method="toggleEnabledDeviceData"
                                    nameKey="enable" arguments="${monitor.name}" btnClass="fr"
                                    monitorId="${monitor.id}" checked="false" icon="icon-disabled" />
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>

<cti:msgScope paths="widgets.outageMonitorsWidget">
    <c:if test="${not empty outageMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".tableHeader.name"/></th>
                <th class="tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${outageMonitors}">
                <c:set var="monitorId" value="${monitor.outageMonitorId}"/>
                <c:set var="monitorName" value="${monitor.outageMonitorName}"/>

                <tr class="monitor">
                    <%-- monitor name --%>
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-name="${fn:escapeXml(monitorName)}" data-subscription-type="OUTAGE_MONITOR" data-ref-id="${monitorId}"/>

                        <cti:url var="viewOutageProcessingUrl" value="/amr/outageProcessing/process/process">
                            <cti:param name="outageMonitorId" value="${monitorId}"/>
                        </cti:url>
                        <a href="${viewOutageProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                    </td>

                    <%-- violations count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
                    </td>

                    <%-- monitoring count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
                    </td>

                    <%-- enable/disable --%>
                    <td class="tar">
                        <c:choose>
                            <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledOutage" outageMonitorId="${monitorId}" btnClass="fr"
                                                               nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                            </c:when>
                            <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledOutage" outageMonitorId="${monitorId}" btnClass="fr"
                                                               nameKey="enable" arguments="${monitorName}" icon="icon-disabled"/>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>

<cti:msgScope paths="widgets.tamperFlagMonitorsWidget">
    <c:if test="${not empty tamperFlagMonitorsWidgetError }">
        <div class="errorMessage">${tamperFlagMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty tamperFlagMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".tableHeader.name"/></th>
                <th class="tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${tamperFlagMonitors}">
                <c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
                <c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>

                <tr class="monitor">
                    <%-- monitor name --%>
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-name="${fn:escapeXml(monitorName)}" data-subscription-type="TAMPER_FLAG_MONITOR" data-ref-id="${monitorId}"/>

                        <cti:url var="viewTamperFlagProcessingUrl" value="/amr/tamperFlagProcessing/process/process">
                            <cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
                        </cti:url>
                        <a href="${viewTamperFlagProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                    </td>

                    <%-- violations count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
                    </td>

                    <%-- monitoring count --%>
                    <td class="tar">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
                    </td>

                    <%-- enable/disable --%>
                    <td class="${tdClass} tar">
                        <c:choose>
                            <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledTamperFlag" tamperFlagMonitorId="${monitorId}" btnClass="fr"
                                                               nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                            </c:when>
                            <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledTamperFlag" tamperFlagMonitorId="${monitorId}" btnClass="fr"
                                                               nameKey="enable" arguments="${monitorName}" icon="icon-disabled"/>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>


<cti:msgScope paths="widgets.statusPointMonitorsWidget">
    <c:if test="${not empty statusPointMonitorsWidgetError}">
        <div class="errorMessage"><i:inline key="${statusPointMonitorsWidgetError}"/></div>
    </c:if>
    <c:if test="${not empty statusPointMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".tableHeader.name"/></th>
                <th class="tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${statusPointMonitors}">
            <c:set var="monitorId" value="${monitor.statusPointMonitorId}"/>
            <c:set var="monitorName" value="${monitor.statusPointMonitorName}"/>

            <tr class="monitor">
                <%-- monitor name --%>
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>

                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitorName)}" data-subscription-type="STATUS_POINT_MONITOR" data-ref-id="${monitorId}"/>

                    <cti:url var="viewStatusPointMonitoringUrl" value="/amr/statusPointMonitoring/viewPage">
                        <cti:param name="statusPointMonitorId" value="${monitorId}"/>
                    </cti:url>
                    <a href="${viewStatusPointMonitoringUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>">${fn:escapeXml(monitorName)}</a>
                </td>

                <%-- monitoring count --%>
                <td class="tar">
                    <cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${monitorId}/MONITORING_COUNT"/>
                </td>

                <%-- enable/disable --%>
                <td class="tar">
                    <c:choose>
                        <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledStatusPoint" statusPointMonitorId="${monitorId}" btnClass="fr"
                                                           nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledStatusPoint" statusPointMonitorId="${monitorId}" btnClass="fr"
                                                           nameKey="enable" arguments="${monitorName}" icon="icon-disabled"/>
                        </c:when>
                    </c:choose>
                </td> 
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>

<cti:msgScope paths="widgets.porterResponseMonitorsWidget">
    <c:if test="${not empty porterResponseMonitorError}">
        <div class="errorMessage">${porterResponseMonitorError}</div>
    </c:if>

    <c:if test="${not empty porterResponseMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${porterResponseMonitors}">
            <tr class="monitor">
                <%-- monitor name --%>
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>

                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitor.name)}" data-subscription-Type="PORTER_RESPONSE_MONITOR" data-ref-id="${monitor.monitorId}"/>

                    <cti:url var="viewMonitorUrl" value="/amr/porterResponseMonitor/viewPage">
                        <cti:param name="monitorId" value="${monitor.monitorId}" />
                    </cti:url>
                    <a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitor.name)}"/>">${fn:escapeXml(monitor.name)}</a>
                </td>

                <%-- enable/disable --%>
                <td class="tar">
                     <c:choose>
                        <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledPorterResponse"
                                nameKey="disable" arguments="${monitor.name}" btnClass="fr"
                                monitorId="${monitor.monitorId}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledPorterResponse"
                                nameKey="enable" arguments="${monitor.name}" btnClass="fr"
                                monitorId="${monitor.monitorId}" checked="false" icon="icon-disabled"/>
                        </c:when>
                    </c:choose>
                </td> 
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>


<cti:msgScope paths="widgets.validationMonitorsWidget">
    <c:if test="${not empty validationMonitorsWidgetError}">
        <div class="errorMessage">${validationMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty validationMonitors}">
    <table class="compactResultsTable stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th><i:inline key=".tableHeader.name"/></th>
                <th class="tar"><i:inline key=".tableHeader.threshold"/>&nbsp;(<i:inline key=".thresholdUnits"/>)</th>
                <th class="tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${validationMonitors}">
            <c:set var="monitorId" value="${monitor.validationMonitorId}"/>
            <c:set var="monitorName" value="${monitor.name}"/>

            <tr class="monitor">
                <%-- monitor name --%>
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>
                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitorName)}" data-subscription-type="VALIDATION_MONITOR" data-ref-id="${monitorId}"/>

                    <cti:url var="viewValidationMonitorEditorUrl" value="/common/vee/monitor/edit">
                        <cti:param name="validationMonitorId" value="${monitorId}"/>
                    </cti:url>
                    <a href="${viewValidationMonitorEditorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>" >
                        ${fn:escapeXml(monitorName)}
                    </a>
                </td>

                <%-- threshold --%>
                <td class="tar">
                    <cti:msg2 key="yukon.common.float.tenths" argument="${monitor.reasonableMaxKwhPerDay}" />
                </td>

                <%-- monitoring count --%>
                <td class="tar">
                    <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
                </td>

                <%-- Enable/Disable --%>
                <td>
                    <c:choose>
                        <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                            <tags:widgetActionRefreshImage  method="toggleEnabledValidation" validationMonitorId="${monitorId}" btnClass="fr" nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledValidation" validationMonitorId="${monitorId}" btnClass="fr" nameKey="enable" arguments="${monitorName}" icon="icon-disabled"/>
                        </c:when>
                    </c:choose>
                </td> 
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:if>
</cti:msgScope>

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

<c:if test="${not isSubscribedWidget}">
    <div class="actionArea">
        <cti:button id="createMonitorBtn" nameKey="create" icon="icon-plus-green"/>
    </div>

    <tags:simplePopup id="createMonitorDialog" title="Create Monitor" on="#createMonitorBtn">
        <ul class="simple-list">
            <li>
                <a href="/amr/deviceDataMonitor/createPage"><i:inline key="widgetClasses.DeviceDataMonitorsWidget.name"/></a>
            </li>
            <li>
                <a href="/amr/outageProcessing/monitorEditor/edit"><i:inline key="widgets.outageMonitorsWidget.tableHeader.name"/></a>
            </li>
            <li>
                <a href="/amr/tamperFlagProcessing/edit"><i:inline key="widgets.tamperFlagMonitorsWidget.tableHeader.name"/></a>
            </li>
            <li>
                <a href="/amr/statusPointMonitoring/creationPage"><i:inline key="widgets.statusPointMonitorsWidget.tableHeader.name"/></a>
            </li>
            <li>
                <a href="/amr/porterResponseMonitor/createPage"><i:inline key="widgets.porterResponseMonitorsWidget.name"/></a>
            </li>
            <li>
                <a href="/common/vee/monitor/edit"><i:inline key="widgets.validationMonitorsWidget.tableHeader.name"/></a>
            </li>
        </ul>
    </tags:simplePopup>
</c:if>