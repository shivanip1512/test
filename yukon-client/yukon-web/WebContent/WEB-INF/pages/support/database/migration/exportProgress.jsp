<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.exportProgress.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.exportProgress.boxTitle"/>
<cti:msg var="noteLabel" key="yukon.web.modules.support.databaseMigration.exportProgress.noteLabel"/>
<cti:msg var="noteText" key="yukon.web.modules.support.databaseMigration.exportProgress.noteText"/>
<cti:msg var="downloadButton" key="yukon.web.modules.support.databaseMigration.exportProgress.downloadButton"/>

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
	    	$('downloadButton').disable();
	    });

	    function enableDownloadButton(totalCount) {
	        return function(data) {
	            if (data['completedItems'] == totalCount) {
		            $('downloadButton').enable();
	            }
	        };
	    } 
    
    </script>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
		<tags:nameValueContainer>
			<tags:nameValue name="File Location" nameColumnWidth="120px">
				C:\Yukon\Server\Log\PSPL0019_dbUser_LoginGroup_111509.xml
			</tags:nameValue>
		</tags:nameValueContainer>
		<br>
	    
	    <%-- PROGRESS BAR --%>
	    <tags:resultProgressBar totalCount="5"
	        						 countKey="DATABASE_MIGRATION/12345/COMPLETED_ITEMS"
	        						 progressLabelTextKey="yukon.web.modules.support.databaseMigration.exportProgress.progressLabel"
	        						 statusTextKey="DATABASE_MIGRATION/12345/STATUS_TEXT"/>
	
	
		<%-- DOWNLOAD BUTTON --%>	
		<cti:dataUpdaterCallback function="enableDownloadButton('5')" initialize="true" completedItems="DATABASE_MIGRATION/12345/COMPLETED_ITEMS"/>
	
		<br>
		<form id="cancelForm" action="/spring/support/database/migration/home" method="post">
			<input type="hidden" name="export" value="true">
    	</form>
    	
		<form id="downloadExportFileForm" action="/spring/support/database/migration/downloadExportFile" method="post">
	    	<input type="hidden" name="fileKey" value="dummyFileKey">
	    </form>
	    
	    <input type="button" id="downloadButton" value="${downloadButton}" onclick="$('downloadExportFileForm').submit();" style="width:80px;"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>