<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="moveInFormUrl"
    value="/WEB-INF/pages/amr/meter/moveInForm.jsp" />
<c:url var="moveInResultsUrl"
    value="/WEB-INF/pages/amr/meter/moveInResults.jsp" />
<cti:standardPage title="Move In" module="amr">
    <cti:standardMenu menuSelection="meters" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp"
            title="Operations Home" />
        <cti:crumbLink url="/spring/meter/search" title="Meters" />
        <cti:crumbLink url="/spring/meter/home?deviceId=${meter.deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
        &gt; Move In
    </cti:breadCrumbs>

	<h2>Move In</h2>
	<br><br>
	
	<%-- show widget or show results? --%>
	<c:choose>
		<c:when test="${not (submissionType eq 'moveIn')}">
	
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
			    	<div class="notAuthorized">User is not authorized to perform Move In</div>
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
