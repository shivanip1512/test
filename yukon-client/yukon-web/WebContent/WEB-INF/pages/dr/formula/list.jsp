<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dr" page="formulaList">

    <tags:sectionContainer2 nameKey="formulas">
        <c:if test="${not empty formulas}">
            <table id="jobsTable" class="compactResultsTable">
                <thead>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".calculationType"/></th>
                    <th><i:inline key=".formulaType"/></th>
                    <th><i:inline key=".assignments"/></th>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="formula" items="${formulas}">
                    <tr>
                        <td><a href="view/${formula.formulaId}">${formula.name}</a></td>
                        <td><i:inline key="${formula.calculationType}"/></td>
                        <td><i:inline key="${formula.formulaType}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${formula.formulaType == 'GEAR'}">${formula.assignments}</c:when>
                                <c:otherwise>
                                    <a href="#">${formula.assignments}</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            </c:if>
        <c:if test="${empty formulas}">
            <span class="empty-list" colspan="3"><i:inline key=".noFormulas"/></span>
        </c:if>
        <div class="actionArea">
           <cti:button icon="icon-plus-green" nameKey="newFormula" href="create"/>
        </div>
    </tags:sectionContainer2>
</cti:standardPage>
