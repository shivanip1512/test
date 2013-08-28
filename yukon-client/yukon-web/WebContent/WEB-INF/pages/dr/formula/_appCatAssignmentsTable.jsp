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
            <span class="error empty-list"><i:inline key=".noApplianceCategoriesForUser"/></span>
        </c:when>
        <c:when test="${empty pagedAppCats.resultList}">
            <span class="empty-list"><i:inline key=".noApplianceCategories"/></span>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable rowHighlighting sortable-table">
                <thead>
                    <tr>
                        <th width="25%"><tags:sortLink nameKey="name" fieldName="NAME" baseUrl="${sortUrl}" styleClass="f-sortLink" isDefault="true" /></th>
                        <th width="25%"><tags:sortLink nameKey="appCatType" fieldName="TYPE" baseUrl="${sortUrl}" styleClass="f-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="applianceLoad" fieldName="APP_CAT_AVERAGE_LOAD" baseUrl="${sortUrl}" styleClass="f-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="formula" fieldName="IS_ASSIGNED" baseUrl="${sortUrl}" styleClass="f-sortLink"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="appCatAssignment" items="${pagedAppCats.resultList}">
                        <c:set var="appCat" value="${appCatAssignment.applianceCategory}"/>
                         <tr>
                             <td width="25%">
                                <cti:url var="appCatUrl" value="/adminSetup/energyCompany/applianceCategory/view">
                                    <cti:param name="applianceCategoryId" value="${appCat.applianceCategoryId}"/>
                                    <cti:param name="ecId" value="${energyCompanyIds[appCat.applianceCategoryId]}"/>
                                </cti:url>
                                <a href="${appCatUrl}">${appCat.name}</a>
                             </td>
                             <td width="25%"><i:inline key="${appCat.applianceType}"/></td>
                             <td width="25%">${appCat.applianceLoad} kW</td>
                             <td width="25%" id="formulaPickerRowAppCat_${appCat.applianceCategoryId}">
                                <%@ include file="_appCatFormulaPicker.jsp" %>
                             </td>
                     </tr>
                    </c:forEach>
                </tbody>
            </table>
        
            <cti:url value="${sortUrl}" var="pagedurl">
               <cti:param name="appCatSort" value="${appCatSort}"/>
               <cti:param name="appCatOrderByDescending" value="${appCatOrderByDescending}"/>
            </cti:url>
            <tags:pagingResultsControls baseUrl="${pagedurl}" result="${pagedAppCats}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>