<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="moveOutFormUrl"
    value="/WEB-INF/pages/amr/meter/moveOutForm.jsp" />
<cti:url var="moveOutResultsUrl"
    value="/WEB-INF/pages/amr/meter/moveOutResults.jsp" />

<cti:standardPage module="amr" page="moveOut">
    
    <%-- show widget or show results? --%>
	<c:choose>
		<c:when test="${submissionType != 'moveOut'}">
		
			<%-- only show widget if user has permission --%>
            <c:choose>
                <c:when test="${readable}">
    
        
			        <div id="meterinfo" style="width: 600px">
			            <ct:widget bean="meterInformationWidget"
			                identify="false" deviceId="${deviceId}"
			                hideEnabled="false" />
			        	<br>
				        <div id="moveout">
				            <jsp:include page="${moveOutFormUrl}" />
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
	            <jsp:include page="${moveOutResultsUrl}" />
	        </div>
	    </c:otherwise>
	</c:choose>
	
</cti:standardPage>
