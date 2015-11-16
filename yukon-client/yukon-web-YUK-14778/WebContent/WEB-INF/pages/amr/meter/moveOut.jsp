<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="moveOut">
    
    <%-- show widget or show results? --%>
    <c:choose>
        <c:when test="${submissionType != 'moveOut'}">
        
            <%-- only show widget if user has permission --%>
            <c:choose>
                <c:when test="${readable}">
                    <div class="column-12-12 clearfix">
                        <div class="column one">
                        <tags:widget bean="meterInformationWidget" identify="false" deviceId="${deviceId}" 
                            hideEnabled="false" container="section"/>                        
                        </div>
                        <div class="column two nogutter">
                            <jsp:include page="/WEB-INF/pages/amr/meter/moveOutForm.jsp"/>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="notAuthorized"><i:inline key=".notAuthorized"/></div>
                </c:otherwise>
            </c:choose>
            
        </c:when>
        <c:otherwise>
            <div id="results">
                <jsp:include page="/WEB-INF/pages/amr/meter/moveOutResults.jsp"/>
            </div>
        </c:otherwise>
    </c:choose>
    
</cti:standardPage>