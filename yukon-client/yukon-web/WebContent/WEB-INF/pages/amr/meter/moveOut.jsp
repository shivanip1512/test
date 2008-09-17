<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="moveOutFormUrl"
    value="/WEB-INF/pages/amr/meter/moveOutForm.jsp" />
<c:url var="moveOutResultsUrl"
    value="/WEB-INF/pages/amr/meter/moveOutResults.jsp" />

<cti:standardPage title="Move Out Page" module="amr">
    <cti:standardMenu menuSelection="meters" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp"
            title="Operations Home" />
        <cti:crumbLink url="/spring/meter/search" title="Meters" />
        <cti:crumbLink url="/spring/meter/home?deviceId=${meter.deviceId}">
            <cti:deviceName deviceId="${meter.deviceId}"></cti:deviceName>
        </cti:crumbLink>
        &gt; Move Out
    </cti:breadCrumbs>

    <h2>Move Out</h2>
    <br><br>
    
    <%-- show widget or show results? --%>
	<c:choose>
		<c:when test="${not (submissionType eq 'moveOut')}">
		
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
			    	<div class="notAuthorized">User is not authorized to perform Move Out</div>
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
