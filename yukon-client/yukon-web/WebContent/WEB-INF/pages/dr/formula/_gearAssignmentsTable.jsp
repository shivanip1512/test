<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:url value="gearAssignmentsPage" var="sortUrl"/>

<cti:msgScope paths="modules.dr.assignments">
    <c:choose>
        <c:when test="${empty pagedGears.resultList}">
            <span class="empty-list"><i:inline key=".noGears"/></span>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable rowHighlighting sortable-table">
                <thead>
                    <tr>
                        <th width="33%"><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" sortParam="orderBy" styleClass="f-sortLink" isDefault="true"/></th>
                        <th width="33%"><tags:sortLink nameKey="gearControlMethod" baseUrl="${sortUrl}" fieldName="CONTROL_METHOD" sortParam="orderBy" styleClass="f-sortLink"/></th>
                        <th width="34%"><tags:sortLink nameKey="formula" baseUrl="${sortUrl}" fieldName="IS_ASSIGNED" sortParam="orderBy" styleClass="f-sortLink"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="gearAssignment" items="${pagedGears.resultList}">
                         <tr>
                             <td width="33%">${gearAssignment.gear.gearName}</td>
                             <td width="33%"><i:inline key="${gearAssignment.gear.controlMethod}"/></td>
                             <td width="34%" id="formulaPickerRowGear_${gearAssignment.gear.yukonID}">
                                 <%@ include file="_gearFormulaPicker.jsp" %>
                             </td>
                     </tr>
                    </c:forEach>
                </tbody>
            </table>
            <cti:url value="${sortUrl}" var="pagedUrl">
               <cti:param name="gearOrderBy" value="${gearOrderBy}"/>
               <cti:param name="gearOrderByDescending" value="${gearOrderByDescending}"/>
            </cti:url>
            <tags:pagingResultsControls baseUrl="${pagedUrl}" result="${pagedGears}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>