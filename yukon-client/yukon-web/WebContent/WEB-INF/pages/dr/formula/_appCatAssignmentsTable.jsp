<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:url value="appCatAssignmentsPage" var="sortUrl"/>

<cti:msgScope paths="modules.dr.assignments">
    <c:choose>
        <c:when test="${noEnergyCompany}">
            <span class="error"><i:inline key=".noApplianceCategoriesForUser"/></span>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable rowHighlighting">
                <thead>
                    <tr>
                        <th width="33%"><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" sortParam="orderBy" styleClass="f-sortLink" isDefault="true" /></th>
                        <th width="33%"><tags:sortLink nameKey="appCatType" baseUrl="${sortUrl}" fieldName="TYPE" sortParam="orderBy" styleClass="f-sortLink"/></th>
                        <th width="34%"><tags:sortLink nameKey="formula" baseUrl="${sortUrl}" fieldName="IS_ASSIGNED" sortParam="orderBy" styleClass="f-sortLink"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="appCatAssignment" items="${pagedAppCats.resultList}">
                         <tr>
                             <td width="33%">${appCatAssignment.applianceCategory.name}</td>
                             <td width="33%"><i:inline key="${appCatAssignment.applianceCategory.applianceType}"/></td>
                             <td width="34%" id="formulaPickerRowAppCat_${appCatAssignment.applianceCategory.applianceCategoryId}">
                                <%@ include file="_appCatFormulaPicker.jsp" %>
                             </td>
                     </tr>
                    </c:forEach>
                </tbody>
            </table>
        
            <cti:url value="${sortUrl}" var="pagedurl">
               <cti:param name="appCatOrderBy" value="${appCatOrderBy}"/>
               <cti:param name="appCatOrderByDescending" value="${appCatOrderByDescending}"/>
            </cti:url>
            <tags:pagingResultsControls baseUrl="${pagedurl}" result="${pagedAppCats}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>