<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.validationProgress.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.validationProgress.boxTitle"/>
<cti:msg var="viewResultButton" key="yukon.web.modules.support.databaseMigration.validationProgress.viewResultsButton"/>

<cti:standardPage title="${pageTitle}" module="support">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/support/" title="Support" />
	    <cti:crumbLink url="/spring/support/database/migration/home" title="${homePageTitle}">
	    	<cti:param name="import" value="true"/>
	    </cti:crumbLink>
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="database|migration"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <script type="text/javascript">

    	var buttonHasBeenEnabled = false;
    
    	jQuery(function() {

	    	<c:choose>
			    <c:when test="${not status.complete}">
			    	$('validateButton').disable();
			    </c:when>
			    <c:otherwise>
			    	buttonHasBeenEnabled = true;
			    </c:otherwise>
			</c:choose>
	    });

		
	    function enableMigrationImportValidateButton() {
		    try {
			    if (!buttonHasBeenEnabled) {
	    			$('validateButton').enable();
			    }
	    		// may not be rendered yet
		    } catch(e){}
	    }
    
    </script>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
	    <%-- PROGRESS BAR --%>
	    <tags:resultProgressBar totalCount="${status.totalCount}"
	        						 countKey="DATABASE_MIGRATION/${status.id}/VALIDATION_COMPLETED_ITEMS"
	        						 progressLabelTextKey="yukon.web.modules.support.databaseMigration.validationProgress.progressLabel"
	        						 statusTextKey="DATABASE_MIGRATION/${status.id}/VALIDATION_STATUS_TEXT"
									 hideCount="true"
	        						 completionCallback="enableMigrationImportValidateButton"/>
	        						 
	    <%-- VALIDATE --%>
	    <br>
		<form id="importValidateForm" action="/spring/support/database/migration/importValidate" method="get">
	    	<input type="hidden" name="statusKey" value="${status.id}">
	    </form>
	    
	    <tags:slowInput id="validateButton" myFormId="importValidateForm" label="${viewResultButton}" />

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>