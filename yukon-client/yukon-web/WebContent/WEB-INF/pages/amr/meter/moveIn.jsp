<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="moveInFormUrl"
    value="/WEB-INF/pages/amr/meter/moveInForm.jsp" />
<cti:url var="moveInResultsUrl"
    value="/WEB-INF/pages/amr/meter/moveInResults.jsp" />

<cti:standardPage module="amr" page="moveIn">
	
	<%-- show widget or show results? --%>
	<c:choose>
		<c:when test="${submissionType != 'moveIn'}">
	
			<%-- only show widget if user has permission --%>
            <c:choose>
                <c:when test="${readable}">
			    	<div id="meterinfo" style="width: 600px">
			            <ct:widget bean="meterInformationWidget"
			                identify="false" deviceId="${deviceId}"
			                hideEnabled="false" />
						<br>
				        <div id="movein">
				            <jsp:include page="${moveInFormUrl}" />
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
	            <jsp:include page="${moveInResultsUrl}" />
	        </div>
	    </c:otherwise>
	</c:choose>
</cti:standardPage>
