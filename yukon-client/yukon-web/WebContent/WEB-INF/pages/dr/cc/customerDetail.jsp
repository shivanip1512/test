<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.customerDetail">

    <cti:url var="saveUrl" value="/dr/cc/customerSave/${customerId}"/>
    <form:form modelAttribute="customerModel" action="${saveUrl}">
        <cti:csrfToken/>
        
        <div class="column-12-12">
            <div class="column one stacked-lg">
                <div class="stacked-md">
                    <h3><i:inline key=".satisfiedPointGroups"/></h3>
                    <c:choose>
                        <c:when test="${not empty satisfiedPointGroups}">
                            <ul>
                                <c:forEach var="pointGroup" items="${satisfiedPointGroups}">
                                    <li>${pointGroup}</li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-list"><i:inline key=".noSatisfiedPointGroups"/></div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <h3><i:inline key=".attachedPoints"/></h3>
                <table class="compact-results-table">
                    <c:forEach var="pointType" items="${customerModel.pointTypes}">
                        <c:set var="pointValue" value="${customerModel.pointValues[pointType]}"/>
                        <c:choose>
                            <c:when test="${not empty pointValue}">
                                <tr>
                                    <td>${pointType.label}</td>
                                    <td><form:input path="pointValues[${pointType}]"/></td>
                                    <td>${pointType.unit}</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td>${pointType.label}</td>
                                    <td colspan="2">
                                        <cti:url var="createPointUrl" value="/dr/cc/customerDetail/${customerId}/createPoint/${pointType}"/>
                                        <cti:button nameKey="createPoint" renderMode="labeledImage" icon="icon-add" href="${createPointUrl}"/>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </table>
            </div>
            <div class="column two nogutter"></div>
        </div>
        <div class="column-12-12">
            <div class="column one">
                <h3><i:inline key=".assignedPrograms"/></h3>
                <table class="compact-results-table" id="active-programs">
                    <tbody>
                        <c:forEach var="activeProgram" items="${customerModel.activePrograms}" varStatus="status">
                            <tr>
                                <td>
                                    <cti:button renderMode="buttonImage" icon="icon-delete" data-program-id="${activeProgram.paoId}"/>
                                    <form:hidden path="activePrograms[${status.index}].paoId"/>
                                    <form:hidden path="activePrograms[${status.index}].paoName"/>
                                    ${fn:escapeXml(activeProgram.paoName)}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="column two nogutter">
                <h3><i:inline key=".availablePrograms"/></h3>
                <table class="compact-results-table" id="available-programs">
                    <tbody>
                        <c:forEach var="availableProgram" items="${customerModel.availablePrograms}" varStatus="status">
                            <tr>
                                <td>
                                    <cti:button renderMode="buttonImage" icon="icon-add" data-program-id="${availableProgram.paoId}"/>
                                    <form:hidden path="availablePrograms[${status.index}].paoId"/>
                                    <form:hidden path="availablePrograms[${status.index}].paoName"/>
                                    ${fn:escapeXml(availableProgram.paoName)}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="page-action-area">
            <cti:button nameKey="save" classes="action primary" type="submit"/>
            <cti:url value="/dr/cc/customerList" var="cancelUrl"/>
            <cti:button nameKey="cancel" href="${cancelUrl}"/>
        </div>
    
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

</cti:standardPage>