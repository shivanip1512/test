<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url value="listPageAjax" var="sortUrl"/>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:if test="${not empty pagedFormulas.resultList}">
        <table class="compact-results-table sortable-table">
            <thead>
                <th><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" styleClass="f-drFormula-sortLink" isDefault="true"/></th>
                <th><tags:sortLink nameKey="calculationType" baseUrl="${sortUrl}" fieldName="CALCULATION_TYPE" styleClass="f-drFormula-sortLink"/></th>
                <th><tags:sortLink nameKey="formulaType" baseUrl="${sortUrl}" fieldName="TYPE" styleClass="f-drFormula-sortLink"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="formula" items="${pagedFormulas.resultList}">
                    <tr>
                        <td><a href="formula/view?formulaId=${formula.formulaId}">${fn:escapeXml(formula.name)}</a></td>
                        <td><i:inline key="${formula.calculationType}"/></td>
                        <td><i:inline key="${formula.formulaType}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty pagedFormulas.resultList}">
        <span class="empty-list"><i:inline key=".noFormulas"/></span>
    </c:if>

    <tags:pagingResultsControls baseUrl="${sortUrl}" result="${pagedFormulas}"/>
</cti:msgScope>