<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="deviceselection" />

    <cti:breadCrumbs>
    	<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    	<cti:crumbLink url="/spring/csr/search" title="Device Selection" />
        <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}">Device Detail</cti:crumbLink>
        <cti:crumbLink url="/spring/csr/highBill?deviceId=${deviceId}">High Bill Complaint</cti:crumbLink>
        <cti:crumbLink>${reportTitle}</cti:crumbLink>
    </cti:breadCrumbs>

    <cti:simpleReportUrlFromNameTag var="bodyUrl"
                                    htmlOutput="false"
                                    viewType="htmlView"
                                    viewJsp="BODY"
                                    definitionName="${definitionName}"
                                    pointId="${pointId}"
                                    startDate="${startDate}"
                                    stopDate="${stopDate}" />
                                    
    <jsp:include page="${bodyUrl}" />

</cti:standardPage>