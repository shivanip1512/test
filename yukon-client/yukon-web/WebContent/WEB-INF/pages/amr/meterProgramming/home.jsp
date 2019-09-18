<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterProgramming">

    <div id="page-actions" class="dn">
        <cti:url var="summaryUrl" value="/amr/meterProgramming/summary"/>
        <cm:dropdownOption key=".summaryLink" icon="icon-application-view-columns" href="${summaryUrl}"/>
        <cti:url var="programOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/meterProgramming/inputs"/>
        <cm:dropdownOption key=".programOtherDevices" href="${programOthersUrl}" icon="icon-cog-add"/>
    </div>
    
    <cti:url var="programUrl" value="/amr/meterProgramming/home"/>
    <div data-url="${filterUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${program}"/>
                    <tags:sort column="${numberOfDevices}"/>
                    <tags:sort column="${numberInProgress}"/>
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="program" items="${programs}">
                    <tr>
                        <td>
                            <cti:url var="programUrl" value="/amr/meterProgramming/summary">
                                <cti:param name="programs[0].guid" value="${program.programInfo.guid}"/>
                                <cti:param name="programs[0].name" value="${program.programInfo.name}"/>
                                <cti:param name="programs[0].source" value="${program.programInfo.source}"/>
                            </cti:url>
                            <a href="${programUrl}">${fn:escapeXml(program.programInfo.name)}</a>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${program.deviceTotal > 0}">
                                    <cti:url var="programDevicesUrl" value="/amr/meterProgramming/summary">
                                        <cti:param name="programs[0].guid" value="${program.programInfo.guid}"/>
                                        <cti:param name="programs[0].name" value="${program.programInfo.name}"/>
                                        <cti:param name="programs[0].source" value="${program.programInfo.source}"/>
                                        <cti:param name="statuses" value="SUCCESS"/>
                                    </cti:url>
                                    <a href="${programDevicesUrl}">${program.deviceTotal}</a>
                                </c:when>
                                <c:otherwise>
                                    ${program.deviceTotal}
                                </c:otherwise>
                            </c:choose>
                       </td>
                        <td>
                            <c:choose>
                                <c:when test="${program.inProgressTotal > 0}">
                                    <cti:url var="programInProgressUrl" value="/amr/meterProgramming/summary">
                                        <cti:param name="programs[0].guid" value="${program.programInfo.guid}"/>
                                        <cti:param name="programs[0].name" value="${program.programInfo.name}"/>
                                        <cti:param name="programs[0].source" value="${program.programInfo.source}"/>
                                        <cti:param name="statuses" value="IN_PROGRESS"/>
                                    </cti:url>
                                    <a href="${programInProgressUrl}">${program.inProgressTotal}</a>
                                </c:when>
                                <c:otherwise>
                                    ${program.inProgressTotal}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${program.displayDelete()}">
                                <cm:dropdown icon="icon-cog">
                                    <cm:dropdownOption id="deleteProgram-${program.programInfo.guid}" icon="icon-cross" key="yukon.web.components.button.delete.label"
                                        data-program-guid="${program.programInfo.guid}" data-ok-event="yukon:program:delete"/>
                                    <d:confirm on="#deleteProgram-${program.programInfo.guid}" nameKey="confirmDelete" argument="${program.programInfo.name}"/>
                                </cm:dropdown>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty programs}">
                    <tr>
                        <td colspan="4"><span class="empty-list"><i:inline key=".noProgramsFound" /></span></td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.meterProgramming.summary.js" />

</cti:standardPage>