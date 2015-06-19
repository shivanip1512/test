<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.customerDetail">

<div class="column-12-12">
    <div class="column one">
        <h3><i:inline key=".satisfiedPointGroups"/></h3>
        <c:choose>
	        <c:when test="${not empty satisfiedPointGroups}">
		        <ul>
		            <c:forEach var="pointGroup" items="${satisifedPointGroups}">
		                <li>${pointGroup}</li>
		            </c:forEach>
		        </ul>
	        </c:when>
	        <c:otherwise>
	            <div class="empty-list"><i:inline key=".noSatisfiedPointGroups"/></div>
	        </c:otherwise>
        </c:choose>
        
        <h3><i:inline key=".attachedPoints"/></h3>
        <table class="compact-results-table">
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
                    <td><em><i:inline key=".createPoint"/></em></td>
                    <td></td>
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
        <h3><i:inline key=".availablePrograms"/></h3>
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

</cti:standardPage>