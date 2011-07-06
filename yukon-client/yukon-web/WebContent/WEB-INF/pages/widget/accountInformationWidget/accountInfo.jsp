<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
<c:when test="${!hasVendorId}">
	<i:inline key=".noVendor"/>
</c:when>
<c:otherwise>

<%-- MORE INFO LINK --%>
<div style="text-align:right;padding-right:20px;font-size:12px;">
    <a href="javascript:void(0);" onclick="$('moreInfo').toggle();"><i:inline key=".viewDetails"/></a>
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
        <th colspan="2" align="left"><i:inline key=".custInfo"/></th>
    </tr>
    <tr>
    	<td>
    		<ct:nameValueContainer2>
	    		<ct:nameValue2 nameKey=".name">
					${mspCustomer.firstName} 
            		<c:if test="${not empty mspCustomer.MName}">${mspCustomer.MName}</c:if>
            		${mspCustomer.lastName}
				</ct:nameValue2>
				<ct:nameValueGap2 gapHeight="6px" />
			    <c:if test="${not empty mspCustomer.DBAName}">
    	    		<ct:nameValue2 nameKey=".dba" >
					     ${mspCustomer.DBAName}
			        </ct:nameValue2>
				    <ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
   	    		<ct:nameValue2 nameKey=".address" >
				     <tags:address address="${custAddress}" />
		        </ct:nameValue2>
    		</ct:nameValueContainer2>
        </td>
    </tr>
</table>

<%-- SERVICE LOCATION INFORMATION --%>
<br>
<table class="compactResultsTable">
    <tr>
        <th colspan="2" align="left"><i:inline key=".serviceLocation"/>:</th>
    </tr>
    <tr>
    	<td>
    		<ct:nameValueContainer2>
    			<c:if test="${not empty mspServLoc.objectID}">
			    	<ct:nameValue2 nameKey=".serviceLocation">
			    		${mspServLoc.objectID}
			    	</ct:nameValue2>
			    	<ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.custID}">
			    	<ct:nameValue2 nameKey=".custNumber">
			    		${mspServLoc.custID}
			    	</ct:nameValue2>
			    	<ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.accountNumber}">
			        <ct:nameValue2 nameKey=".acctNumber">
			    		${mspServLoc.accountNumber}
			    	</ct:nameValue2>
			    	<ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.siteID}">
  			        <ct:nameValue2 nameKey=".siteNumber">
			    		${mspServLoc.siteID}
			    	</ct:nameValue2>
			    	<ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.gridLocation}">
  			        <ct:nameValue2 nameKey=".mapLocation">
			    		${mspServLoc.gridLocation}
			    	</ct:nameValue2>
			    	<ct:nameValueGap2 gapHeight="6px" />
			    </c:if>
		        <ct:nameValue2 nameKey=".address">
		    		<tags:address address="${servLocAddress}" />
		    	</ct:nameValue2>
		    	<ct:nameValueGap2 gapHeight="6px" />
	        </ct:nameValueContainer2>
        </td>
    </tr>
</table>

</c:otherwise>
</c:choose>
