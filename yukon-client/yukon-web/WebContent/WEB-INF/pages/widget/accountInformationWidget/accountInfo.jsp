<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%-- MORE INFO LINK --%>
<div style="text-align:right;padding-right:20px;font-size:12px;">
    <a href="javascript:void(0);" onclick="$('moreInfo').toggle();">View CIS Details</a>
</div>

<div id="moreInfoDiv">
<tags:simplePopup id="moreInfo" title="CIS Details" onClose="$('moreInfo').toggle();">
    <div style="height:300px;overflow:auto;">
        <jsp:include page="/WEB-INF/pages/widget/accountInformationWidget/moreInfo.jsp" />
    </div>
</tags:simplePopup>
</div>

    

<%-- CUSTOMER INFORMATION --%>
<table class="compactResultsTable">

    <tr>
        <th colspan="2" align="left">Customer Information:</th>
    </tr>

    <tr>
        <td width="30%" class="label">Name:</td>
        <td>${mspCustomer.firstName} ${mspCustomer.lastName}</td>
    </tr>
    
    <tr valign="top">
        <td class="label">Address:</td>
        <td>
            <tags:address address="${custAddress}" />
        </td>
    </tr>
    
</table>
    


<%-- SERVICE LOCATION INFORMATION --%>
<br>
<table class="compactResultsTable">

    <tr>
        <th colspan="2" align="left">Service Location:</th>
    </tr>

    <c:if test="${not empty mspServLoc.custID}">
    <tr>
        <td width="30%" class="label">Customer Number:</td>
        <td>${mspServLoc.custID}</td>
    </tr>
    </c:if>
    
    <c:if test="${not empty mspServLoc.accountNumber}">
    <tr>
        <td class="label">Account Number:</td>
        <td>${mspServLoc.accountNumber}</td>
    </tr>
    </c:if>
    
    <c:if test="${not empty mspServLoc.siteID}">
    <tr>
        <td class="label">Site Number:</td>
        <td>${mspServLoc.siteID}</td>
    </tr>
    </c:if>
    
    <c:if test="${not empty mspServLoc.gridLocation}">
    <tr>
        <td class="label">Map Location:</td>
        <td>${mspServLoc.gridLocation}</td>
    </tr>
    </c:if>
    
    <tr valign="top">
        <td class="label">Address:</td>
        <td>
            <tags:address address="${servLocAddress}" />
        </td>
    </tr>
    
</table>
