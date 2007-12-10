<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="moveInFormUrl"
    value="/WEB-INF/pages/amr/csr/moveInForm.jsp" />
<c:url var="moveInResultsUrl"
    value="/WEB-INF/pages/amr/csr/moveInResults.jsp" />
<cti:standardPage title="Move In Page" module="amr">
    <cti:standardMenu menuSelection="deviceselection" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp"
            title="Operations Home" />
        <cti:crumbLink url="/spring/csr/search" title="Device Selection" />
        <cti:crumbLink url="/spring/csr/home?deviceId=${meter.deviceId}"
            title="Device Detail" />
        &gt; Move In
    </cti:breadCrumbs>

    <body>
        <c:choose>
            <c:when test="${not (submissionType eq 'moveIn')}">

                <div id="meterinfo" style="width: 400px">
                    <ct:widget bean="meterInformationWidget"
                        identify="false" deviceId="${deviceId}"
                        hideEnabled="false" />
                </div>

                <div id="movein">
                    <jsp:include page="${moveInFormUrl}" />
                </div>
            </c:when>
            <c:otherwise>
                <div id="results">
                    <jsp:include page="${moveInResultsUrl}" />
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</cti:standardPage>
