<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:choose>
        <c:when test="${fn:length(gearAssignments) == 0}">
            <span class="empty-list"><i:inline key=".noGears"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="four-column-table compact-results-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".gearControlMethod"/></th>
                            <th><i:inline key=".program"/></th>
                            <th><i:inline key=".formula"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="gearAssignment" items="${gearAssignments}">
                             <tr>
                                 <td>${fn:escapeXml(gearAssignment.gear.gearName)}</td>
                                 <td><i:inline key="${gearAssignment.gear.controlMethod}"/></td>
                                 <td>
                                    <cti:url var="programUrl" value="/dr/program/detail">
                                        <cti:param name="programId" value="${gearAssignment.gear.deviceId}"/>
                                    </cti:url>
                                    <a href="${programUrl}">${fn:escapeXml(gearPrograms[gearAssignment.gear.deviceId].paoName)}</a>
                                 </td>
                                 <td id="formula-picker-row-gear_${gearAssignment.gear.yukonID}">
                                     <%@ include file="_gearFormulaPicker.jsp" %>
                                 </td>
                         </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</cti:msgScope>