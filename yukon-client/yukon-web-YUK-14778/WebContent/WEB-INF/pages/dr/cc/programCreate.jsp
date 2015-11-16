<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.programCreate">
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<div class="column-24">
    <div class="column one">
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".programType">
            <select id="program-type">
                <c:forEach var="programType" items="${programTypes}">
                    <option value="${programType.id}" <c:if test="${selectedTypeId == programType.id}">selected</c:if>>
                        ${programType.name}
                    </option>
                </c:forEach>
            </select>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".programName">
            <input type="text" id="program-name" value="${programName}">
            <div id="program-errors" class="error dn"><i:inline key=".required.name"/></div>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <cti:url value="/dr/cc/programDetailCreate" var="createUrl"/>
    <cti:button nameKey="create" data-url="${createUrl}" id="create-program"/>
    
</div>
</cti:standardPage>