<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url value="listPageAjax" var="sortUrl"/>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:if test="${not empty pagedFormulas.resultList}">
        <table class="compactResultsTable sortable-table">
            <thead>
                <th><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" styleClass="f-drFormula-sortLink" isDefault="true"/></th>
                <th><tags:sortLink nameKey="calculationType" baseUrl="${sortUrl}" fieldName="CALCULATION_TYPE" styleClass="f-drFormula-sortLink"/></th>
                <th><tags:sortLink nameKey="formulaType" baseUrl="${sortUrl}" fieldName="TYPE" styleClass="f-drFormula-sortLink"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="formula" items="${pagedFormulas.resultList}">
                    <tr>
                        <td><a href="formula/view?formulaId=${formula.formulaId}">${formula.name}</a></td>
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
    <c:if test="${pagedFormulas.hitCount > pagedFormulas.count}">
	    <cti:url value="${sortUrl}" var="pagedUrl">
	       <cti:param name="sort" value="${sort}"/>
	       <cti:param name="descending" value="${descending}"/>
	    </cti:url>
	    <tags:pagingResultsControls baseUrl="${pagedUrl}" result="${pagedFormulas}"/>
    </c:if>
</cti:msgScope>