<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.tools.tdc">
    <table class="compact-results-table has-actions with-form-controls clearfix">
        <thead></thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="alarm" items="${alarms}">
                <tr>
                    <td class="alarm-text">${alarm.textMessage}</td>
                    <td><cti:button id="${alarm.condition}" nameKey="alarm.acknowledge" icon="icon-tick" classes="fr js-tdc-ack-alarm" pointId="${alarm.pointId}" condition="${alarm.condition}" renderMode="buttonImage"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <div class="action-area">
        <cti:button nameKey="alarm.acknowledgeAll" pointId="${alarms.get(0).pointId}" classes="js-tdc-ack-alarms-for-point"/>
        <cti:button nameKey="close" onclick="$('#tdc-popup').dialog('close');"/>
    </div>
</cti:msgScope>