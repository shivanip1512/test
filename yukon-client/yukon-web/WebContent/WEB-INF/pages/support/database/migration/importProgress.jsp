<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.importProgress.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.importProgress.boxTitle"/>

<cti:standardPage title="${pageTitle}" module="support">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/support/" title="Support" />
	    <cti:crumbLink url="/spring/support/database/migration/home" title="${homePageTitle}" />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="database|migration"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <script type="text/javascript">

	    Event.observe(window, 'load', function() {
	    	$('validateButton').disable();
	    });

	    function enableValidateButton(totalCount) {
	        return function(data) {
	            if (data['completedItems'] == totalCount) {
		            $('validateButton').enable();
	            }
	        };
	    } 
    
    </script>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
	    <%-- PROGRESS BAR --%>
	    <tags:resultProgressBar totalCount="${status.totalCount}"
	        						 countKey="DATABASE_MIGRATION/${status.id}/IMPORT_COMPLETED_ITEMS"
	        						 progressLabelTextKey="yukon.web.modules.support.databaseMigration.importProgress.progressLabel"
	        						 statusTextKey="DATABASE_MIGRATION/${status.id}/IMPORT_STATUS_TEXT"/>
	        						 
	    <%-- BUTTON --%>
	    <br>
		<cti:dataUpdaterCallback function="enableValidateButton('${status.totalCount}')" initialize="true" completedItems="DATABASE_MIGRATION/${status.id}/IMPORT_COMPLETED_ITEMS"/>
	
		<form id="importValidateForm" action="/spring/support/database/migration/importValidate" method="get">
	    	<input type="hidden" name="statusKey" value="${status.id}">
	    </form>
	    
	    <input type="button" id="validateButton" value="Validate" onclick="$('importValidateForm').submit();" style="width:80px;"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>