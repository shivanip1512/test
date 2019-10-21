<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.meterProgramming.summary">
    <table id="program-statistics" class="compact-results-table has-actions row-highlighting">
        <thead>
            <tr>
                <tags:sort column="${program}" width="40%"/>
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
                                <a href="${programUrl}&statuses=PROGRAMMED">${program.deviceTotal}</a>
                            </c:when>
                            <c:otherwise>
                                ${program.deviceTotal}
                            </c:otherwise>
                        </c:choose>
                   </td>
                    <td>
                        <c:choose>
                            <c:when test="${program.inProgressTotal > 0}">
                                <a href="${programUrl}&statuses=IN_PROGRESS&statuses=CONFIRMING">${program.inProgressTotal}</a>
                            </c:when>
                            <c:otherwise>
                                ${program.inProgressTotal}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${program.isUnused()}">
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
</cti:msgScope>