<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:if test="${not empty errorMsg}">
    <br>
    <div class="error">${errorMsg}</div>
</c:if>

<c:if test="${not empty results}">

    <br>
    <div class="error"><i:inline key="yukon.web.modules.amr.meterReadErrors.errorLabel"/></div>
    
    <c:forEach items="${results}" var="result">
    
    	<c:if test="${not empty result.deviceError}">
        	${result.deviceError}
        </c:if>
        
        <c:forEach items="${result.errors}" var="error">
            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
            ${error.porter}<br>
            ${error.troubleshooting}<br>
            </tags:hideReveal><br>
        </c:forEach>
        
    </c:forEach>
    
</c:if>
