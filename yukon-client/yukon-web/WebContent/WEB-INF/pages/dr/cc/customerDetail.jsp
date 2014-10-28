<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.customerDetail">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-12-12">
    <div class="column one">
        <h2><i:inline key=".customerDetail"/> ${customer.companyName}</h2>
        <h3><i:inline key=".customerData"/></h3>
        <div><i:inline key=".customerSatisfiedPointGroups"/></div>
        <ul>
            <c:forEach var="pointGroup" items="${satisifedPointGroups}">
                <li>${pointGroup}</li>
            </c:forEach>
        </ul>
        <h3><i:inline key=".customerAttachedPoints"/></h3>
        <table class="compact-results-table">
            <thead>
                <tr>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach var="pointType" items="${pointTypeList}">
                <c:set var="pointValue" value="${pointValueCache[pointType]}"/>
                <c:choose>
                <c:when test="${not empty pointValue}">
                <tr>
                    <td>${pointType.label}</td>
                    <td>${pointValue}</td>
                    <td>${pointType.unit}</td>
                </tr>
                </c:when>
                <c:otherwise>
                <tr>
                    <td>${pointType.label}</td>
                    <td><em><i:inline key=".customerCreatePoint"/></em></td>
                    <td></td>
                </tr>
                </c:otherwise>
                </c:choose>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="column two nogutter"></div>
</div>
<div class="column-12-12">
    <div class="column one">
        <h3><i:inline key=".customerAssignedPrograms"/></h3>
        <table class="compact-results-table">
            <thead>
                <tr>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="activeProgram" items="${activePrograms}">
                <tr>
                    <td>
                        <cti:button renderMode="buttonImage" icon="icon-delete"/>
                        ${activeProgram.paoName}
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="column two nogutter">
        <h3><i:inline key=".customerAvailablePrograms"/></h3>
        <table class="compact-results-table">
            <thead>
                <tr>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="availableProgram" items="${availablePrograms}">
                <tr>
                    <td>
                        <cti:button renderMode="buttonImage" icon="icon-add"/>
                        ${availableProgram.paoName}
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</cti:msgScope>
</cti:standardPage>