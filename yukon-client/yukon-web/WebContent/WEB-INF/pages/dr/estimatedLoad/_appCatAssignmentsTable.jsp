<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:set var="sortUrl" value="appCatAssignmentsPage"/>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:choose>
        <c:when test="${noEnergyCompany}">
            <span class="error empty-list"><i:inline key=".noApplianceCategoriesForUser"/></span>
        </c:when>
        <c:when test="${empty pagedAppCats.resultList}">
            <span class="empty-list"><i:inline key=".noApplianceCategories"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table row-highlighting sortable-table">
                <thead>
                    <tr>
                        <th width="25%"><tags:sortLink nameKey="name" fieldName="NAME" baseUrl="${sortUrl}" styleClass="f-drFormula-sortLink" isDefault="true" /></th>
                        <th width="25%"><tags:sortLink nameKey="appCatType" fieldName="TYPE" baseUrl="${sortUrl}" styleClass="f-drFormula-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="applianceLoad" fieldName="APP_CAT_AVERAGE_LOAD" baseUrl="${sortUrl}" styleClass="f-drFormula-sortLink"/></th>
                        <th width="25%"><tags:sortLink nameKey="formula" fieldName="IS_ASSIGNED" baseUrl="${sortUrl}" styleClass="f-drFormula-sortLink"/></th>
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
                                <a href="${appCatUrl}">${fn:escapeXml(appCat.name)}</a>
                             </td>
                             <td width="25%"><i:inline key="${appCat.applianceType}"/></td>
                             <td width="25%"><i:inline key=".loadInKw" arguments="${appCat.applianceLoad}"/></td>
                             <td width="25%" id="formulaPickerRowAppCat_${appCat.applianceCategoryId}">
                                <%@ include file="_appCatFormulaPicker.jsp" %>
                             </td>
                     </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls baseUrl="${sortUrl}" result="${pagedAppCats}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>