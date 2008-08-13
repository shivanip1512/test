<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CUSTOMER INFORMATION --%>
<br>
<table class="compactResultsTable">
    <tr><th colspan="2" align="left">Customer Information:</th></tr>

    <amr:moreInfoRows infoList="${custBasicsInfo}" />
    
    <c:choose>
        <c:when test="${fn:length(custBasicsInfo) % 2 eq 0}">
            <tr valign="top">
        </c:when>
        <c:otherwise>
            <tr valign="top" class="altRow">
        </c:otherwise>
    </c:choose>
    
        <td class="label">Address:</td>
        <td>
            <tags:address address1="${custAddressInfo['Address 1']}"
                  address2="${custAddressInfo['Address 2']}"
                  city="${custAddressInfo['City']}"
                  state="${custAddressInfo['State']}"
                  zip="${custAddressInfo['Zip']}" />
        </td>
    </tr>
</table>

<c:if test="${not empty custContactInfo}">
    <amr:moreInfoTable title="Contact Info" infoList="${custContactInfo}" indent="30" titleSize="11" />
</c:if>

<%-- SERVICE LOCATION INFORMATION --%>
<br>
<table class="compactResultsTable">
    <tr><th colspan="2" align="left">Service Location:</th></tr>

    <amr:moreInfoRows infoList="${servLocBasicsInfo}" />
    
    <c:choose>
        <c:when test="${fn:length(servLocBasicsInfo) % 2 eq 0}">
            <tr valign="top">
        </c:when>
        <c:otherwise>
            <tr valign="top" class="altRow">
        </c:otherwise>
    </c:choose>
    
        <td class="label">Address:</td>
        <td>
            <tags:address address1="${servLocAddressInfo['Address 1']}"
                  address2="${servLocAddressInfo['Address 2']}"
                  city="${servLocAddressInfo['City']}"
                  state="${servLocAddressInfo['State']}"
                  zip="${servLocAddressInfo['Zip']}" />
        </td>
    </tr>
</table>

<%-- SERVICE LOCATION NETWORK INFORMATION --%>
<amr:moreInfoTable title="Network" infoList="${servLocNetworkInfo}" indent="30" titleSize="11"/>

<%-- METER BASIC INFORMATION --%>
<amr:moreInfoTable title="Meter" infoList="${meterBasicsInfo}" />

<%-- METER NAMEPLATE INFORMATION --%>
<amr:moreInfoTable title="Nameplate" infoList="${meterNameplateInfo}" indent="30" titleSize="11" />

<%-- METER UTILITY INFORMATION --%>
<amr:moreInfoTable title="Utility Information" infoList="${meterUtilInfoInfo}" indent="30" titleSize="11" />
