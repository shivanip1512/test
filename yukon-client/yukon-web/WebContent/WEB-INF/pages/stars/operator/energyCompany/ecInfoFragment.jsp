<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<div class="energyCompanyInfoFragment">

    <h2 class="standardPageHeading">${energyCompanyInfoFragment.companyName}</h2>
    
    <cti:formatNotification var="phone" value="${energyCompanyInfoFragment.phone}" htmlEscape="true" />
    <cti:formatNotification var="fax" value="${energyCompanyInfoFragment.fax}" htmlEscape="true" />
    
    <c:if test="${not empty phone}">
        <cti:msg var="phone" key="yukon.web.modules.operator.energyCompanyInfoFragment.phoneWithSymbol" argument="${phone}"/>
    </c:if>
    
    <c:if test="${not empty fax}">
        <cti:msg var="fax" key="yukon.web.modules.operator.energyCompanyInfoFragment.faxWithSymbol" argument="${fax}"/>
    </c:if>
    
    <c:choose>
    
        <c:when test="${not empty phone && not empty fax}">
            ${phone}
            <br>
            ${fax}
        </c:when>
        
        <c:when test="${not empty phone && empty fax}">
            ${phone}
        </c:when>
    
        <c:when test="${empty phone && not empty fax}">
            ${fax}
        </c:when>
        
        <c:otherwise>
            <cti:msg2 key="yukon.web.defaults.na"/>
        </c:otherwise>
    
    </c:choose>
    <br>
    
    <tags:address address="${energyCompanyInfoFragment.address}"/>
    
</div>