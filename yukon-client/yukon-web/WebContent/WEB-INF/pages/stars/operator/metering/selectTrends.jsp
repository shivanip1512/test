<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="operator" page="selectTrends">
    <cti:checkAccountEnergyCompanyOperator showError="true" >
    <tags:sectionContainer2 nameKey="availableTrends">
    
        <c:choose>
        
            <c:when test="${fn:length(availablTrendWrappers) == 0}">
                <cti:msg2 key=".noTrends"/>
            </c:when>
            
            <c:otherwise>

                <cti:url var="submitUrl" value="/stars/operator/metering/saveSelectedTrends"/>
                <form action="${submitUrl}">

                    <input type="hidden" name="accountId" value="${accountId}">
                    
                    
                    
                    
            
                    <c:forEach var="trend" items="${availablTrendWrappers}">
                    
                        <div class="availableTrendOption">
                            <label>
                                <input type="checkbox" name="graphDefinitionId" value="${trend.graphDefinitionId}" <c:if test="${trend.selected}">checked</c:if>>
                                <span class="radioLabel">
                                    <spring:escapeBody htmlEscape="true">${trend.name}</spring:escapeBody>
                                </span>
                            </label>
                        </div>
                    </c:forEach>

                    <div class="page-action-area">
                        <cti:button nameKey="save" type="submit" classes="primary action" busy="true"/>
                    </div>

                </form>
            
            </c:otherwise>
        
        </c:choose>
    
    </tags:sectionContainer2>
    <cti:checkAccountEnergyCompanyOperator showError="true" >
</cti:standardPage>