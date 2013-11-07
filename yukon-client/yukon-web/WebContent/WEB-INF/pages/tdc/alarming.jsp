<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<style>
.alarm-text .property-list:nth-child(1) {margin-right:10px;}
.alarm-ack .button, .alarm-ack button {margin-top: 14px;}
</style>

<cti:url var="allAlarms" value="/tdc/${allAlarmsDislay}" />
<cti:msgScope paths="modules.tools.tdc">
    <cti:msg2 key="yukon.web.modules.tools.tdc.activeAlarms.title" arguments="${alarms.size()}" var="title" />
    <tags:sectionContainer title="${title}">
        <table class="compact-results-table has-actions with-form-controls separated no-stripes">
            <thead></thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="alarm" items="${alarms}">
                    <tr>
                        <td><span class="${colorStateBoxes.get(alarm.pointId).get(alarm.condition)}">&nbsp;</span></td>
                        <td class="alarm-text">
                            <div class="clearfix">
                                <ul class="property-list fl">
                                    <li class="name"><i:inline key="yukon.common.device"/>:</li>
                                    <li class="value">${fn:escapeXml(alarm.deviceName)}</li>
                                </ul>
                                <ul class="property-list fl">
                                    <li class="name"><i:inline key="yukon.common.point"/>:</li>
                                    <li class="value">${fn:escapeXml(alarm.pointName)}</li>
                                </ul>
                            </div>
                            <div>
                                <cti:msg2 key=".deviceNamePointName" arguments="${alarm.deviceName},${alarm.pointName}" argumentSeparator="," var="title" />
                                <a href="${allAlarms}" title="${title}">${fn:escapeXml(alarm.textMessage)}</a>
                            </div>
                        </td>
                        <td class="alarm-ack">
                            <c:if test='${unackAlarms.get(alarm) != null}'>
                                <cti:button nameKey="alarm.acknowledge" icon="icon-tick" classes="fr f-ack" pointId="${alarm.pointId}" condition="${alarm.condition}" renderMode="buttonImage" />
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </tags:sectionContainer>
</cti:msgScope>