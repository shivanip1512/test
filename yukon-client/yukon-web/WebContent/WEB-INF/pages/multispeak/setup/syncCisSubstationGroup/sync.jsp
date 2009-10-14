<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="Sync CIS Substation Device Group Results"/>
	
<cti:standardPage title="${pageTitle}" module="multispeak">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/msp_setup.jsp" title="MultiSpeak"  />
	    <cti:crumbLink url="/spring/multispeak/setup/syncCisSubstationGroup/home" title="Sync CIS Substation Device Group"  />
	    <cti:crumbLink title="Results"/>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="sync|sync_cis_substation_group"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <tags:boxContainer title="Results">
    
    	<c:set var="meterCount" value="${fn:length(meterAndSubsList)}"/>
		<c:set var="scrollIfOver" value="20"/>
  			${meterCount} synchronized with CIS Substation Device Group. 
    	<br><br>
    	<div style="font-size:11px;">
    	&bull; ${totalNoChange} remained in existing group, displayed in black.<br>
		&bull; ${totalAdded} added to a group, displayed in <span class="okGreen">green</span>.<br>
		&bull; ${totalRemoved} removed from one or more groups, displayed in <span class="errorRed">red</span>.<br>
		</div>
                    
	    <br>
	    <c:if test="${meterCount > scrollIfOver}">
			<div style="overflow:auto;height:350px;padding-right:20px;" >
		</c:if>

	    <table class="compactResultsTable">
    
	    	<tr>
		    	<th>Meter Number</th>
		    	<th>CIS Substation Device Group</th>
		    	<th>Removed From</th>
	    	</tr>
	    	
	    	<c:forEach var="meterAndSubs" items="${meterAndSubsList}">
	    
	    		<tr>
	    		
	   				<td style="vertical-align:top;">
	   					${meterAndSubs.meter.meterNumber}
	   				</td>
	   				
	   				<%-- substation groups --%>
	    			<td>
	   					<c:set var="linkClass" value=""/>
	   					<c:if test="${meterAndSubs.added}">
	   						<c:set var="linkClass" value="okGreen"/>
	   					</c:if>
	   					<span class="${linkClass}" style="text-decoration:none;">${meterAndSubs.belongsIn.fullName}</span>
	  				</td>
	    				
	  				<%-- removed --%>
	  				<td>
	    				<c:forEach var="group" items="${meterAndSubs.removed}" varStatus="status">
	    					<c:if test="${status.count > 1}"><br></c:if>
	  						<span class="errorRed">${group.fullName}</span>
	  					</c:forEach>
	  					<c:if test="${fn:length(meterAndSubs.removed) == 0}">
	  						N/A
	  					</c:if>
	  				</td>
	    		
	    		</tr>
	    
	    	</c:forEach> 
	    
	    </table>
	    
	    <c:if test="${meterCount > scrollIfOver}">
			</div>
		</c:if>
		
		<br>
    	<form id="gotoGroupsForm" action="/spring/group/editor/home" method="post">
    		<input type="hidden" name="groupName" value="${substationGroupPrefix}">
    		<tags:slowInput myFormId="gotoGroupsForm" labelBusy="View CIS Substation Device Groups" label="View CIS Substation Device Groups"/>
    	</form>
    
    </tags:boxContainer>
    
</cti:standardPage>