<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="moveIn">
    
    <%-- show widget or show results? --%>
    <c:choose>
        <c:when test="${submissionType != 'moveIn'}">
    
            <%-- only show widget if user has permission --%>
            <c:choose>
                <c:when test="${readable}">
                    <div class="column-12-12 clearfix">
                        <div class="column one">
                             <ct:widget bean="meterInformationWidget" identify="false" deviceId="${deviceId}" 
                                hideEnabled="false" container="section"/>
                        </div>
                        <div class="column two nogutter">
                            <jsp:include page="/WEB-INF/pages/amr/meter/moveInForm.jsp"/>
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
                <jsp:include page="/WEB-INF/pages/amr/meter/moveInResults.jsp"/>
            </div>
        </c:otherwise>
    </c:choose>
</cti:standardPage>