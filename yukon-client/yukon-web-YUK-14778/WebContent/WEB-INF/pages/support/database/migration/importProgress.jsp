<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.importProgress.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.importProgress.boxTitle"/>

<cti:standardPage title="${pageTitle}" module="support">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/dashboard" title="Home"  />
	    <cti:crumbLink url="/support" title="Support" />
	    <cti:crumbLink url="/support/database/migration/home" title="${homePageTitle}">
	    	<cti:param name="import" value="true"/>
	    </cti:crumbLink>
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="database|migration"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <script type="text/javascript">
    </script>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
	    <%-- PROGRESS BAR --%>
	    <tags:resultProgressBar totalCount="${status.totalCount}"
	        						 countKey="DATABASE_MIGRATION/${status.id}/IMPORT_COMPLETED_ITEMS"
	        						 progressLabelTextKey="yukon.web.modules.support.databaseMigration.importProgress.progressLabel"
	        						 statusTextKey="DATABASE_MIGRATION/${status.id}/IMPORT_STATUS_TEXT"
									 hideCount="true"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>