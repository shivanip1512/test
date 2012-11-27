<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i18n" tagdir="/WEB-INF/tags/i18n" %>


<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.importValidate.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="currentEnvironment" key="yukon.web.modules.support.databaseMigration.currentEnvironment"/>
<cti:msg var="currentSchemaUsername" key="yukon.web.modules.support.databaseMigration.currentSchemaUser"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.importValidate.boxTitle"/>
<cti:msg var="noteLabel" key="yukon.web.modules.support.databaseMigration.importValidate.noteLabel"/>
<cti:msg var="noteText" key="yukon.web.modules.support.databaseMigration.importValidate.noteText"/>
<cti:msg var="fileInfoSection" key="yukon.web.modules.support.databaseMigration.importValidate.fileInfoSection"/>
<cti:msg var="filePathLabel" key="yukon.web.modules.support.databaseMigration.importValidate.filePath"/>
<cti:msg var="fileSizeLabel" key="yukon.web.modules.support.databaseMigration.importValidate.fileSize"/>
<cti:msg var="orgEnvironment" key="yukon.web.modules.support.databaseMigration.importValidate.orgEnvironment"/>
<cti:msg var="orgSchemaUser" key="yukon.web.modules.support.databaseMigration.importValidate.orgSchemaUser"/>
<cti:msg var="fileViewContentsLabel" key="yukon.web.modules.support.databaseMigration.importValidate.fileViewContents"/>
<cti:msg var="componentsInfoSection" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection"/>
<cti:msg var="componentHeader" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.component"/>
<cti:msg var="countHeader" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.count"/>
<cti:msg var="objects" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.objects"/>
<cti:msg var="warnings" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.warnings"/>
<cti:msg var="errors" key="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.errors"/>
<cti:msg var="commitFile" key="yukon.web.modules.support.databaseMigration.importValidate.commitFile"/>
<cti:msg var="cancel" key="yukon.web.modules.support.databaseMigration.importValidate.cancel"/>

<cti:msg var="ignore" key="yukon.web.modules.support.databaseMigration.importValidate.ignore"/>
<cti:msg var="overwrite" key="yukon.web.modules.support.databaseMigration.importValidate.overwrite"/>
<cti:msg var="warningHelpTitle" key="yukon.web.modules.support.databaseMigration.importValidate.warningHelpTitle"/>
<cti:msg var="warningHelp" key="yukon.web.modules.support.databaseMigration.importValidate.warningHelp"/>

<cti:msg var="magImg" key="yukon.web.modules.support.databaseMigration.importValidate.mag.img"/>
<cti:msg var="magImgOver" key="yukon.web.modules.support.databaseMigration.importValidate.mag.img.hover"/>
<cti:msg var="warningImg" key="yukon.web.modules.support.databaseMigration.importValidate.warning.img"/>
<cti:msg var="warningImgOver" key="yukon.web.modules.support.databaseMigration.importValidate.warning.img.hover"/>
<cti:msg var="errorImg" key="yukon.web.modules.support.databaseMigration.importValidate.error.img"/>
<cti:msg var="errorImgOver" key="yukon.web.modules.support.databaseMigration.importValidate.error.img.hover"/>
<cti:msg var="placeHolderImg" key="yukon.web.modules.support.databaseMigration.importValidate.placeholder.img"/>

<cti:standardPage title="${pageTitle}" module="support">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/support/" title="Support" />
	    <cti:crumbLink url="/support/database/migration/home" title="${homePageTitle}">
	    	<cti:param name="import" value="true"/>
	    </cti:crumbLink>
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="database|migration"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <style>
    	div.migrationSection {width:80%;}
    	img.componentInfo {vertical-align:middle;padding-left:4px;}
    	table.compactResultsTable td.component {padding-top:10px;}
    	table.compactResultsTable th.basic {text-align:right;}
    	table.compactResultsTable td.basic {width:100px;text-align:right;vertical-align:middle;}
    	table.compactResultsTable th.warnings {text-align:right;}
    	table.compactResultsTable td.warnings {white-space:nowrap;text-align:right;vertical-align:middle;}
    </style>
    
    <script type="text/javascript">
    	function setWarningProcessing() {
        	var warningProcessingSelect = $('warningProcessing');
        	var warningProcessValue = warningProcessingSelect.options[warningProcessingSelect.selectedIndex].value;
        	$('warningProcessingValue').value = warningProcessValue;
    	}
    </script>
    
    <tags:simpleDialog id="sharedPopupDialog"/>
    
    <tags:boxContainer title="${boxTitle}" hideEnabled="false">
    
    	<%-- CURRENT DB --%>
		<tags:nameValueContainer>
			<tags:nameValue name="${currentEnvironment}" nameColumnWidth="190px">${dbUrl}</tags:nameValue>
			<tags:nameValue name="${currentSchemaUsername}">${dbUsername}</tags:nameValue>
		</tags:nameValueContainer>
		<br><br>
		
		<%-- FILE INFO --%>
		<tags:sectionContainer title="${fileInfoSection}">
		
			<tags:nameValueContainer>
				<tags:nameValue name="${orgEnvironment}" nameColumnWidth="190px">${orgDbUrl}</tags:nameValue>
				<tags:nameValue name="${orgSchemaUser}">${orgDbUsername}</tags:nameValue>
				<tags:nameValueGap gapHeight="15px;"/>
				<tags:nameValue name="${filePathLabel}">
  					<form id="openFileForm" action="/support/database/migration/downloadExportFile" method="post">
  						<input type="hidden" name="fileKey" value="${status.id}">
  			    	</form>
                    <div>
                        <a href="javascript:void(0);" onclick="$('openFileForm').submit();" title="${fileViewContentsLabel}">${filePath}</a>
                    </div>
				</tags:nameValue>
				<tags:nameValue name="${fileSizeLabel}">
					<cti:msg2 key="${fileSize}"/>
				</tags:nameValue>
			</tags:nameValueContainer>
			
		</tags:sectionContainer>
		<br>
		
		<%-- COMPONENT INFO --%>
		<tags:sectionContainer title="${componentsInfoSection}">
			<tags:nameValueContainer>
                <cti:msg var="exportTypeName" key="${exportType.typeKey}"/>
                <tags:nameValue name="${exportTypeName}" nameColumnWidth="200px">
        			${status.labelCount}
        			<cti:url var="componentName_1_objects_url" value="/support/database/migration/objectsViewPopup">
        				<cti:param name="fileKey" value="${status.id}"/>
        			</cti:url>
        			<tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.objects" 
                                                 dialogId="sharedPopupDialog" 
                                                 actionUrl="${componentName_1_objects_url}" 
                                                 logoKey="yukon.web.modules.support.databaseMigration.importValidate.mag.img"/>
                </tags:nameValue>
                <tags:nameValue name="${warnings}">
                    <cti:url var="componentName_1_warnings_url" value="/support/database/migration/warningsViewPopup">
                        <cti:param name="fileKey" value="${status.id}"/>
                    </cti:url>
                    ${status.warningCount}
                    <c:if test="${status.warningCount > 0}">
                        <tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.warnings" 
                                               dialogId="sharedPopupDialog" 
                                               actionUrl="${componentName_1_warnings_url}" 
                                               logoKey="yukon.web.modules.support.databaseMigration.importValidate.warning.img"/>
                        
                        <select id="warningProcessing" name="warningProcessing" onchange="setWarningProcessing()" class="warning">
                            <option value="USE_EXISTING">${ignore}</option>
                            <option value="OVERWRITE">${overwrite}</option>
                        </select>
                        <tags:helpInfoPopup title="${warningHelpTitle}">
                            ${warningHelp}
                        </tags:helpInfoPopup>
                    </c:if>
                </tags:nameValue>
                <tags:nameValue name="${errors}">
                    ${status.errorCount}
                    <cti:url var="componentName_1_errors_url" value="/support/database/migration/errorsViewPopup">
                        <cti:param name="fileKey" value="${status.id}"/>
                    </cti:url>
                    <c:if test="${status.errorCount > 0}">
                        <tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.errors" 
                                               dialogId="sharedPopupDialog" 
                                               actionUrl="${componentName_1_errors_url}" 
                                               logoKey="yukon.web.modules.support.databaseMigration.importValidate.error.img"/>
                    </c:if>
                </tags:nameValue>
            </tags:nameValueContainer>      

			<%-- NOTE --%>
			<span class="smallBoldLabel">${noteLabel}</span>
			<span style="font-size:11px;">${noteText}</span>

		</tags:sectionContainer>
        <br><br>
		
		<%-- COMMIT --%>
		<form id="cancelForm" action="/support/database/migration/home" method="post">
			<input type="hidden" name="import" value="true">
    	</form>
    
		<form id="confirmForm" action="/support/database/migration/importConfirm" method="post">
	    	<input type="hidden" name="fileKey" value="${status.id}">
	    	<input type="hidden" id="warningProcessingValue" name="warningProcessingValue" value="USE_EXISTING">
	    </form>
	    
	    <tags:slowInput myFormId="confirmForm" label="${commitFile}"/>
	    <tags:slowInput myFormId="cancelForm" label="${cancel}"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>