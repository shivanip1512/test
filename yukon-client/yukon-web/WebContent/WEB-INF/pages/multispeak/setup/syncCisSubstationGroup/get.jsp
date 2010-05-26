<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="Sync CIS Substation Device Group"/>
	
<cti:standardPage title="${pageTitle}" module="multispeak">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/multispeak/setup/home" title="MultiSpeak"  />
	    <cti:crumbLink title="${pageTitle}"/>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="sync|sync_cis_substation_group"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <tags:boxContainer title="Instructions">
    
        <div style="font-size:11px;padding-top:10px;">
            &bull; Synchronizing CIS Substation device groups will retrieve meters from the Primary CIS Vendor. 
            Meters that exist in Yukon will be added to their corresponding CIS Substation device group based on the Substation Name received from the Primary CIS Vendor.
	    	<br><br>
	    	&bull; If a meter already exists in a CIS Substation device group that does not correspond to the Substation Name received from the Primary CIS Vendor, then the meter will be removed from that device group and will be placed in the updated CIS Substation device group.
	    	<br><br>
	    	&bull; Results will display after synchronization is complete.
	    	
	    	<br><br>
	    	<span class="errorRed" style="font-weight:bold;">Note:</span> 
	    	Retrieving meters from the Primary CIS Vendor may take some time. Leaving this page before the results are displayed will result in an incomplete synchronization.
        </div>
                    
	    <br>
	    <form id="syncNowForm" action="/spring/multispeak/setup/syncCisSubstationGroup/getMeters" method="post">
	    	<tags:slowInput myFormId="syncNowForm" label="Sync Now" labelBusy="Sync In Progress"/>
	    </form>
    
    </tags:boxContainer>
        
</cti:standardPage>