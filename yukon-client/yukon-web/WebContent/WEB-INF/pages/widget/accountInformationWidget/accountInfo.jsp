<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
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
    		<tags:nameValueContainer2>
	    		<tags:nameValue2 nameKey=".name">
					${mspCustomer.firstName} 
            		<c:if test="${not empty mspCustomer.MName}">${mspCustomer.MName}</c:if>
            		${mspCustomer.lastName}
				</tags:nameValue2>
				<tags:nameValueGap2 gapHeight="6px" />
			    <c:if test="${not empty mspCustomer.DBAName}">
    	    		<tags:nameValue2 nameKey=".dba" >
					     ${mspCustomer.DBAName}
			        </tags:nameValue2>
				    <tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
   	    		<tags:nameValue2 nameKey=".address" >
				     <tags:address address="${custAddress}" />
		        </tags:nameValue2>
    		</tags:nameValueContainer2>
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
    		<tags:nameValueContainer2>
    			<c:if test="${not empty mspServLoc.objectID}">
			    	<tags:nameValue2 nameKey=".serviceLocation">
			    		${mspServLoc.objectID}
			    	</tags:nameValue2>
			    	<tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.custID}">
			    	<tags:nameValue2 nameKey=".custNumber">
			    		${mspServLoc.custID}
			    	</tags:nameValue2>
			    	<tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.accountNumber}">
			        <tags:nameValue2 nameKey=".acctNumber">
			    		${mspServLoc.accountNumber}
			    	</tags:nameValue2>
			    	<tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.siteID}">
  			        <tags:nameValue2 nameKey=".siteNumber">
			    		${mspServLoc.siteID}
			    	</tags:nameValue2>
			    	<tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
			    <c:if test="${not empty mspServLoc.gridLocation}">
  			        <tags:nameValue2 nameKey=".mapLocation">
			    		${mspServLoc.gridLocation}
			    	</tags:nameValue2>
			    	<tags:nameValueGap2 gapHeight="6px" />
			    </c:if>
		        <tags:nameValue2 nameKey=".address">
		    		<tags:address address="${servLocAddress}" />
		    	</tags:nameValue2>
		    	<tags:nameValueGap2 gapHeight="6px" />
	        </tags:nameValueContainer2>
        </td>
    </tr>
</table>

</c:otherwise>
</c:choose>
