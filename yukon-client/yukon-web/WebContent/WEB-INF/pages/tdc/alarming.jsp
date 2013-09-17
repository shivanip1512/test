<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:url var="allAlarms" value="/tdc/${allAlarmsDislay}" />
<cti:msgScope paths="modules.tools.tdc">
    <cti:msg2 key="yukon.web.modules.tools.tdc.activeAlarms.title" arguments="${alarms.size()}" var="title" />
    <tags:sectionContainer title="${title}">
        <table class="compactResultsTable has-actions with-form-controls separated">
            <thead></thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="alarm" items="${alarms}">
                    <c:choose>
                        <c:when test='${unackAlarms.get(alarm) != null}'>
                            <c:set var="alert" value="alert" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="alert" value="" />
                        </c:otherwise>
                    </c:choose>
                    <tr class="${alert}">
                        <td class="alarm-text"><a href="${allAlarms}" title="${alarm.device.deviceId} ${fn:escapeXml(alarm.deviceName)} : ${fn:escapeXml(alarm.pointName)}">${fn:escapeXml(alarm.textMessage)}</a></td>
                        <td><c:if test='${unackAlarms.get(alarm) != null}'>
                                <cti:button nameKey="alarm.acknowledge" icon="icon-tick" classes="fr f-ack" pointId="${alarm.pointId}" condition="${alarm.condition}" renderMode="buttonImage" />
                            </c:if></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <hr>
    </tags:sectionContainer>
</cti:msgScope>