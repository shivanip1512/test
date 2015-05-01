<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:choose>
        <c:when test="${noEnergyCompany}">
            <span class="error empty-list"><i:inline key=".noApplianceCategoriesForUser"/></span>
        </c:when>
        <c:when test="${fn:length(appCatAssignments) == 0}">
            <span class="empty-list"><i:inline key=".noApplianceCategories"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="compact-results-table four-column-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".appCatType"/></th>
                            <th><i:inline key=".applianceLoad"/></th>
                            <th><i:inline key=".formula"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="appCatAssignment" items="${appCatAssignments}">
                            <c:set var="appCat" value="${appCatAssignment.applianceCategory}"/>
                             <tr>
                                 <td>
                                    <cti:url var="appCatUrl" value="/admin/energyCompany/applianceCategory/view">
                                        <cti:param name="applianceCategoryId" value="${appCat.applianceCategoryId}"/>
                                        <cti:param name="ecId" value="${energyCompanyIds[appCat.applianceCategoryId]}"/>
                                    </cti:url>
                                    <a href="${appCatUrl}">${fn:escapeXml(appCat.name)}</a>
                                 </td>
                                 <td><i:inline key="${appCat.applianceType}"/></td>
                                 <td><i:inline key=".loadInKw" arguments="${appCat.applianceLoad}"/></td>
                                 <td id="formula-picker-row-app-cat_${appCat.applianceCategoryId}">
                                    <%@ include file="_appCatFormulaPicker.jsp" %>
                                 </td>
                         </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</cti:msgScope>