<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url value="gearAssignmentsPage" var="sortUrl"/>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:choose>
        <c:when test="${empty pagedGears.resultList}">
            <span class="empty-list"><i:inline key=".noGears"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table row-highlighting sortable-table">
                <thead>
                    <tr>
                        <th width="25%"><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" styleClass="f-drFormula-sortLink" isDefault="true"/></th>
                        <th width="25%"><tags:sortLink nameKey="gearControlMethod" baseUrl="${sortUrl}" fieldName="GEAR_CONTROL_METHOD" styleClass="f-drFormula-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="program" baseUrl="${sortUrl}" fieldName="PROGRAM_NAME" styleClass="f-drFormula-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="formula" baseUrl="${sortUrl}" fieldName="IS_ASSIGNED" styleClass="f-drFormula-sortLink"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="gearAssignment" items="${pagedGears.resultList}">
                         <tr>
                             <td width="25%">${fn:escapeXml(gearAssignment.gear.gearName)}</td>
                             <td width="25%"><i:inline key="${gearAssignment.gear.controlMethod}"/></td>
                             <td width="25%">
                                <cti:url var="programUrl" value="/dr/program/detail">
                                    <cti:param name="programId" value="${gearAssignment.gear.deviceId}"/>
                                </cti:url>
                                <a href="${programUrl}">${fn:escapeXml(gearPrograms[gearAssignment.gear.deviceId].paoName)}</a>
                             </td>
                             <td width="25%" id="formulaPickerRowGear_${gearAssignment.gear.yukonID}">
                                 <%@ include file="_gearFormulaPicker.jsp" %>
                             </td>
                     </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls baseUrl="${sortUrl}" result="${pagedGears}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>