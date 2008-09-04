<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CUSTOMER INFORMATION --%>
<br>
<tags:nameValueContainer tableClass="compactResultsTable nameValue" altRowOn="true">
    <tr><th colspan="2" align="left">Customer Information:</th></tr>
    <c:forEach var="x" items="${custBasicsInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
</tags:nameValueContainer>

<%-- CUSTOMER CONTACT INFORMATION --%>
<br>
<div style="padding-left:30px;">
<tags:nameValueContainer tableClass="compactResultsTable nameValue" style="font-size:11px;" altRowOn="true">
    <tr><th colspan="2" align="left">Contact Info:</th></tr>
    <c:forEach var="x" items="${custContactInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
    <tags:nameValue name="Address" nameColumnWidth="30%">
        <tags:address address="${custAddress}" />
    </tags:nameValue>
</tags:nameValueContainer>
</div>

<%-- SERVICE LOCATION INFORMATION --%>
<br>
<br>
<tags:nameValueContainer tableClass="compactResultsTable nameValue" altRowOn="true">
    <tr><th colspan="2" align="left">Service Location:</th></tr>
    <c:forEach var="x" items="${servLocBasicsInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
    <tags:nameValue name="Address" nameColumnWidth="30%">
        <tags:address address="${servLocAddress}" />
    </tags:nameValue>
</tags:nameValueContainer>

<%-- SERVICE LOCATION NETWORK INFORMATION --%>
<br>
<div style="padding-left:30px;">
<tags:nameValueContainer tableClass="compactResultsTable nameValue" style="font-size:11px;" altRowOn="true">
    <tr><th colspan="2" align="left">Network:</th></tr>
    <c:forEach var="x" items="${servLocNetworkInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
</tags:nameValueContainer>
</div>

<%-- METER BASIC INFORMATION --%>
<br>
<br>
<tags:nameValueContainer tableClass="compactResultsTable nameValue" altRowOn="true">
    <tr><th colspan="2" align="left">Meter:</th></tr>
    <c:forEach var="x" items="${meterBasicsInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
</tags:nameValueContainer>

<%-- METER NAMEPLATE INFORMATION --%>
<br>
<div style="padding-left:30px;">
<tags:nameValueContainer tableClass="compactResultsTable nameValue" style="font-size:11px;" altRowOn="true">
    <tr><th colspan="2" align="left">Nameplate:</th></tr>
    <c:forEach var="x" items="${meterNameplateInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
</tags:nameValueContainer>
</div>

<%-- METER UTILITY INFORMATION --%>
<br>
<div style="padding-left:30px;">
<tags:nameValueContainer tableClass="compactResultsTable nameValue" style="font-size:11px;" altRowOn="true">
    <tr><th colspan="2" align="left">Utility Information:</th></tr>
    <c:forEach var="x" items="${meterUtilInfoInfo}">
        <tags:nameValue name="${x.label}" nameColumnWidth="30%">${x.value}</tags:nameValue>
    </c:forEach>
</tags:nameValueContainer>
</div>