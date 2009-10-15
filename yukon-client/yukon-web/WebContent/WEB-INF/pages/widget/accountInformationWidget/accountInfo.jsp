<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:choose>
<c:when test="${!hasVendorId}">

	No primary CIS vendor set.

</c:when>
<c:otherwise>

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
    	<td>
    		<ct:nameValueContainer>
	    		<ct:nameValue name="Name" nameColumnWidth="30%">
					${mspCustomer.firstName} 
            		<c:if test="${not empty mspCustomer.MName}">${mspCustomer.MName}</c:if>
            		${mspCustomer.lastName}
				</ct:nameValue>
				<ct:nameValueGap gapHeight="6px" />
			    <c:if test="${not empty mspCustomer.DBAName}">
    	    		<ct:nameValue name="DBA" >
					     ${mspCustomer.DBAName}
			        </ct:nameValue>
				    <ct:nameValueGap gapHeight="6px" />
			    </c:if>
   	    		<ct:nameValue name="Address" >
				     <tags:address address="${custAddress}" />
		        </ct:nameValue>
    		</ct:nameValueContainer>
        </td>
    </tr>
    
</table>
    


<%-- SERVICE LOCATION INFORMATION --%>
<br>
<table class="compactResultsTable">

    <tr>
        <th colspan="2" align="left">Service Location:</th>
    </tr>

    <tr>
    	<td>
    		<ct:nameValueContainer>
			    <c:if test="${not empty mspServLoc.custID}">
			    	<ct:nameValue name="Customer Number" nameColumnWidth="30%">
			    		${mspServLoc.custID}
			    	</ct:nameValue>
			    	<ct:nameValueGap gapHeight="6px" />
			    </c:if>
			    
			    <c:if test="${not empty mspServLoc.accountNumber}">
			        <ct:nameValue name="Account Number">
			    		${mspServLoc.accountNumber}
			    	</ct:nameValue>
			    	<ct:nameValueGap gapHeight="6px" />
			    </c:if>
			    
			    <c:if test="${not empty mspServLoc.siteID}">
  			        <ct:nameValue name="Site Number">
			    		${mspServLoc.siteID}
			    	</ct:nameValue>
			    	<ct:nameValueGap gapHeight="6px" />
			    </c:if>
			    
			    <c:if test="${not empty mspServLoc.gridLocation}">
  			        <ct:nameValue name="Map Location">
			    		${mspServLoc.gridLocation}
			    	</ct:nameValue>
			    	<ct:nameValueGap gapHeight="6px" />
			    </c:if>

		        <ct:nameValue name="Address">
		    		<tags:address address="${servLocAddress}" />
		    	</ct:nameValue>
		    	<ct:nameValueGap gapHeight="6px" />
			    
	        </ct:nameValueContainer>
        </td>
    </tr>
    
</table>
	
</c:otherwise>
</c:choose>
