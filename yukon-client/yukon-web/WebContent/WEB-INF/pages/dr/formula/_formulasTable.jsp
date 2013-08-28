<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:url value="listPageAjax" var="sortUrl"/>

<cti:msgScope paths="modules.dr.formulaList">
    <c:if test="${not empty pagedFormulas.resultList}">
        <table class="compactResultsTable sortable-table">
            <thead>
                <th><tags:sortLink nameKey="name" baseUrl="${sortUrl}" fieldName="NAME" styleClass="f-sortLink" isDefault="true"/></th>
                <th><tags:sortLink nameKey="calculationType" baseUrl="${sortUrl}" fieldName="CALCULATION_TYPE" styleClass="f-sortLink"/></th>
                <th><tags:sortLink nameKey="formulaType" baseUrl="${sortUrl}" fieldName="TYPE" styleClass="f-sortLink"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="formula" items="${pagedFormulas.resultList}">
                    <tr>
                        <td><a href="view/${formula.formulaId}">${formula.name}</a></td>
                        <td><i:inline key="${formula.calculationType}"/></td>
                        <td><i:inline key="${formula.formulaType}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty pagedFormulas.resultList}">
        <span class="empty-list" colspan="3"><i:inline key=".noFormulas"/></span>
    </c:if>
    <cti:url value="${sortUrl}" var="pagedUrl">
       <cti:param name="sort" value="${sort}"/>
       <cti:param name="descending" value="${descending}"/>
    </cti:url>
    <tags:pagingResultsControls baseUrl="${pagedUrl}" result="${pagedFormulas}"/>
</cti:msgScope>