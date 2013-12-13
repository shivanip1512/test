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

<style type="text/css">
    .monitor-subs th.subscribe {width: 5%;}
    .monitor-subs th.name {width: 45%;}
    .monitor-subs th.data {width: 20%;}
    .monitor-subs th.monitoring {width: 25%;}
    .monitor-subs th.enable {width: 5%;}
</style>

<cti:msg2 var="undoText" key="yukon.common.undo"/>

<cti:msgScope paths="widgetClasses.DeviceDataMonitorsWidget">
    <c:if test="${not empty deviceDataMonitors}">
    <table class="compact-results-table monitor-subs dashed stacked" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".name"/></th>
                <th class="data tar"><i:inline key=".violations"/></th>
                <th class="monitoring tar"><i:inline key=".monitoring"/></th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${deviceDataMonitors}">
                <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitor.name)}"/>

                <tr class="monitor" data-removed-text="${removedText}">
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-subscription-type="DEVICE_DATA_MONITOR" data-ref-id="${monitor.id}" />
                    </td>
                    <%-- monitor name --%>
                    <td>

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
                                    nameKey="disable" arguments="${monitor.name}" btnClass="fr M0"
                                    monitorId="${monitor.id}" icon="icon-enabled" />
                            </c:when>
                            <c:otherwise>
                                <tags:widgetActionRefreshImage method="toggleEnabledDeviceData"
                                    nameKey="enable" arguments="${monitor.name}" btnClass="fr M0"
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
    <table class="compact-results-table monitor-subs dashed stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${outageMonitors}">
                <c:set var="monitorId" value="${monitor.outageMonitorId}"/>
                <c:set var="monitorName" value="${monitor.outageMonitorName}"/>
                <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitorName)}"/>

                <tr class="monitor" data-removed-text="${removedText}">
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-name="${fn:escapeXml(monitorName)}" data-subscription-type="OUTAGE_MONITOR" data-ref-id="${monitorId}"/>
                    </td>
                    <%-- monitor name --%>
                    <td>

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
                                <tags:widgetActionRefreshImage method="toggleEnabledOutage" outageMonitorId="${monitorId}" btnClass="fr M0"
                                                               nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                            </c:when>
                            <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledOutage" outageMonitorId="${monitorId}" btnClass="fr M0"
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
        <div class="error">${tamperFlagMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty tamperFlagMonitors}">
    <table class="compact-results-table monitor-subs dashed stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.violations"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="monitor" items="${tamperFlagMonitors}">
                <c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
                <c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>
                <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitorName)}"/>

                <tr class="monitor" data-removed-text="${removedText}">
                    <td>
                        <c:set var="buttonClasses" value="" />
                        <c:set var="iconClasses" value="disabled cp" />
                        <c:if test="${isSubscribedWidget}">
                            <c:set var="iconClasses" value="" />
                            <c:set var="buttonClasses" value="remove" />
                        </c:if>

                        <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                            data-name="${fn:escapeXml(monitorName)}" data-subscription-type="TAMPER_FLAG_MONITOR" data-ref-id="${monitorId}"/>
                    </td>

                    <%-- monitor name --%>
                    <td>

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
                                <tags:widgetActionRefreshImage method="toggleEnabledTamperFlag" tamperFlagMonitorId="${monitorId}" btnClass="fr M0"
                                                               nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                            </c:when>
                            <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabledTamperFlag" tamperFlagMonitorId="${monitorId}" btnClass="fr M0"
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
        <div class="error"><i:inline key="${statusPointMonitorsWidgetError}"/></div>
    </c:if>
    <c:if test="${not empty statusPointMonitors}">
    <table class="compact-results-table monitor-subs dashed stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data">&nbsp;</th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${statusPointMonitors}">
            <c:set var="monitorId" value="${monitor.statusPointMonitorId}"/>
            <c:set var="monitorName" value="${monitor.statusPointMonitorName}"/>
            <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitorName)}"/>

            <tr class="monitor" data-removed-text="${removedText}">
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>

                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitorName)}" data-subscription-type="STATUS_POINT_MONITOR" data-ref-id="${monitorId}"/>
                </td>
                <%-- monitor name --%>
                <td colspan="2">

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
                            <tags:widgetActionRefreshImage method="toggleEnabledStatusPoint" statusPointMonitorId="${monitorId}" btnClass="fr M0"
                                                           nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledStatusPoint" statusPointMonitorId="${monitorId}" btnClass="fr M0"
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
        <div class="error">${porterResponseMonitorError}</div>
    </c:if>

    <c:if test="${not empty porterResponseMonitors}">
    <table class="compact-results-table monitor-subs dashed stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".name"/></th>
                <th class="data">&nbsp;</th>
                <th class="monitoring">&nbsp;</th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${porterResponseMonitors}">
            <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitor.name)}"/>

            <tr class="monitor" data-removed-text="${removedText}">
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>

                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitor.name)}" data-subscription-Type="PORTER_RESPONSE_MONITOR" data-ref-id="${monitor.monitorId}"/>
                </td>
                <%-- monitor name --%>
                <td colspan="3">

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
                                nameKey="disable" arguments="${monitor.name}" btnClass="fr M0"
                                monitorId="${monitor.monitorId}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledPorterResponse"
                                nameKey="enable" arguments="${monitor.name}" btnClass="fr M0"
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
        <div class="error">${validationMonitorsWidgetError}</div>
    </c:if>

    <c:if test="${not empty validationMonitors}">
    <table class="compact-results-table monitor-subs dashed stacked" data-removed-text="${removedText}" data-undo-text="${undoText}">
        <thead>
            <tr>
                <th class="subscribe">&nbsp;</th>
                <th class="name"><i:inline key=".tableHeader.name"/></th>
                <th class="data tar"><i:inline key=".tableHeader.threshold"/></th>
                <th class="monitoring tar"><i:inline key=".tableHeader.monitoring"/></th>
                <th class="enable">&nbsp;</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="monitor" items="${validationMonitors}">
            <c:set var="monitorId" value="${monitor.validationMonitorId}"/>
            <c:set var="monitorName" value="${monitor.name}"/>
            <cti:msg2 var="removedText" key="widgetClasses.SubscribedMonitorsWidget.subscription.removed" argument="${fn:escapeXml(monitorName)}"/>

            <tr class="monitor" data-removed-text="${removedText}">
                <td>
                    <c:set var="buttonClasses" value="" />
                    <c:set var="iconClasses" value="disabled cp" />
                    <c:if test="${isSubscribedWidget}">
                        <c:set var="iconClasses" value="" />
                        <c:set var="buttonClasses" value="remove" />
                    </c:if>
                    <cti:button classes="hover-actions b-subscribe ${buttonClasses}" nameKey="subscribe" renderMode="image" icon="icon-feed ${iconClasses}"
                        data-name="${fn:escapeXml(monitorName)}" data-subscription-type="VALIDATION_MONITOR" data-ref-id="${monitorId}"/>
                </td>

                <%-- monitor name --%>
                <td>

                    <cti:url var="viewValidationMonitorEditorUrl" value="/common/vee/monitor/edit">
                        <cti:param name="validationMonitorId" value="${monitorId}"/>
                    </cti:url>
                    <a href="${viewValidationMonitorEditorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${fn:escapeXml(monitorName)}"/>" >
                        ${fn:escapeXml(monitorName)}
                    </a>
                </td>

                <%-- threshold --%>
                <td class="tar">
                    <cti:msg2 key="yukon.common.float.tenths" argument="${monitor.reasonableMaxKwhPerDay}" />&nbsp;<i:inline key=".thresholdUnits"/>
                </td>

                <%-- monitoring count --%>
                <td class="tar">
                    <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
                </td>

                <%-- Enable/Disable --%>
                <td>
                    <c:choose>
                        <c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                            <tags:widgetActionRefreshImage  method="toggleEnabledValidation" validationMonitorId="${monitorId}" btnClass="fr M0" nameKey="disable" arguments="${monitorName}" icon="icon-enabled"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabledValidation" validationMonitorId="${monitorId}" btnClass="fr M0" nameKey="enable" arguments="${monitorName}" icon="icon-disabled"/>
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

<div class="action-area">
    <c:if test="${not empty validationMonitors}">
        <cti:msgScope paths="widgets.validationMonitorsWidget">
            <cti:url var="reviewUrl" value="/common/veeReview/home"/>
            <a href="${reviewUrl}" class="notes"><i:inline key=".review"/></a>
        </cti:msgScope>
    </c:if>
    <c:if test="${not isSubscribedWidget}">
        <cm:dropdown key="components.button.create.label" icon="icon-plus-green" type="button" containerCssClass="fr">
            <cm:dropdownOption key="widgetClasses.DeviceDataMonitorsWidget.name" href="/amr/deviceDataMonitor/createPage" />
            <cm:dropdownOption key="widgets.outageMonitorsWidget.tableHeader.name" href="/amr/outageProcessing/monitorEditor/edit" />
            <cm:dropdownOption key="widgets.tamperFlagMonitorsWidget.tableHeader.name" href="/amr/tamperFlagProcessing/edit" />
            <cm:dropdownOption key="widgets.statusPointMonitorsWidget.tableHeader.name" href="/amr/statusPointMonitoring/creationPage" />
            <cm:dropdownOption key="widgets.porterResponseMonitorsWidget.name" href="/amr/porterResponseMonitor/createPage" />
            <cm:dropdownOption key="widgets.validationMonitorsWidget.tableHeader.name" href="/common/vee/monitor/edit" />
        </cm:dropdown>
    </c:if>
</div>