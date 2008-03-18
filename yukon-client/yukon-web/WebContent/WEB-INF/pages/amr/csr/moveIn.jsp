<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="moveInFormUrl"
    value="/WEB-INF/pages/amr/csr/moveInForm.jsp" />
<c:url var="moveInResultsUrl"
    value="/WEB-INF/pages/amr/csr/moveInResults.jsp" />
<cti:standardPage title="Move In" module="amr">
    <cti:standardMenu menuSelection="deviceselection" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp"
            title="Operations Home" />
        <cti:crumbLink url="/spring/csr/search" title="Device Selection" />
        <cti:crumbLink url="/spring/csr/home?deviceId=${meter.deviceId}"
            title="Device Detail" />
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
			    	<div id="meterinfo" style="width: 400px">
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
			    	<div style="color:red;text-align:center;font-size:14px;font-weight:bold;">User is not authorized to perform Move In</div>
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
