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

        jQuery(function() {

		    <c:if test="${not migrationStatus.complete}">
		    	$('downloadButton').disable();
		    </c:if>

	    });

	    function enableMigrationExportDownloadButton() {
		    try {
                $('downloadButton').enable();

	    		// may not be rendered yet
		    } catch(e){}
	    }
    
    </script>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
		<tags:nameValueContainer>
			<tags:nameValue name="File Location" nameColumnWidth="120px">
				${migrationStatus.exportFile.path}
			</tags:nameValue>
		</tags:nameValueContainer>
		<br>
	    
	    <%-- PROGRESS BAR --%>
	    <tags:resultProgressBar totalCount="${migrationStatus.totalCount}"
	                            progressLabelTextKey="yukon.web.modules.support.databaseMigration.exportProgress.progressLabel"
	                            countKey="DATABASE_MIGRATION/${migrationStatus.id}/EXPORT_COMPLETED_ITEMS"
	                            statusTextKey="DATABASE_MIGRATION/${migrationStatus.id}/EXPORT_STATUS_TEXT"
                                statusClassKey="DATABASE_MIGRATION/${migrationStatus.id}/EXPORT_STATUS_CLASS"
	        	                hideCount="true"
                                completionCallback="enableMigrationExportDownloadButton"/>

        <div class="ErrorMsg">
            <cti:dataUpdaterValue type="DATABASE_MIGRATION" identifier="${migrationStatus.id}/EXPORT_ERROR_TEXT" />
        </div>
		
		<%-- DOWNLOAD --%>	
		<br>
		<form id="downloadExportFileForm" action="/spring/support/database/migration/downloadExportFile" method="post">
	    	<input type="hidden" name="fileKey" value="${migrationStatus.id}">
	    </form>
	    
	    <input type="button" id="downloadButton" value="${downloadButton}" onclick="$('downloadExportFileForm').submit();" style="width:80px;"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>