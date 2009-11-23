<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
	    <cti:crumbLink url="/spring/support/" title="Support" />
	    <cti:crumbLink url="/spring/support/database/migration/home" title="${homePageTitle}" />
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
		<tags:sectionContainer title="${fileInfoSection}" styleClass="migrationSection">
		
			<tags:nameValueContainer>
				<tags:nameValue name="${orgEnvironment}" nameColumnWidth="180px">jdbc:jtds:sqlserver://ABCD9999:1433;APPNAME=yukon-client;TDS=8.0</tags:nameValue>
				<tags:nameValue name="${orgSchemaUser}">yukonTestUser</tags:nameValue>
				<tags:nameValueGap gapHeight="15px;"/>
				<tags:nameValue name="${filePathLabel}" nameColumnWidth="190px">
					<form id="openFileForm" action="/spring/support/database/migration/openFile" method="post">
						<input type="hidden" name="fileKey" value="${fileKey}">
			    	</form>
					<a href="javascript:void(0);" onclick="$('openFileForm').submit();" title="${fileViewContentsLabel}">${filePath}</a>
				</tags:nameValue>
				<tags:nameValue name="${fileSizeLabel}">
					${fileSize}kB
				</tags:nameValue>
			</tags:nameValueContainer>
			
		</tags:sectionContainer>
		<br>
		
		<%-- COMPONENT INFO --%>
		<tags:sectionContainer title="${componentsInfoSection}" styleClass="migrationSection">
		
			<%-- NOTE --%>
			<span class="smallBoldLabel">${noteLabel}</span>
			<span style="font-size:11px;">${noteText}</span>
			<br><br>

			<table class="compactResultsTable" style="width:50%;">
			
				<tr>
					<th class="component">${componentHeader}</th>
					<th class="basic">${objects}</th>
					<th class="warnings">${warnings}</th>
					<th class="basic">${errors}</th>
				</tr>
			
				<%-- MAIN COMPONENT ROW --%>
				<tr class="component">
				
					<td class="component">Load Groups</td>
				
					<%-- objects --%>
					<td class="component basic">
						265
						<cti:url var="componentName_1_objects_url" value="/spring/support/database/migration/objectsViewPopup">
							<cti:param name="someObjectKey" value="ABC"/>
						</cti:url>
						<tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.objects" 
                                               dialogId="sharedPopupDialog" 
                                               actionUrl="${componentName_1_objects_url}" 
                                               logoKey="yukon.web.modules.support.databaseMigration.importValidate.mag.img"/>
					</td>
					
					<%-- warnings --%>
					<td class="component warnings">
						15
						<cti:url var="componentName_1_warnings_url" value="/spring/support/database/migration/warningsViewPopup">
							<cti:param name="someObjectKey" value="ABC"/>
						</cti:url>
						<tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.warnings" 
                                               dialogId="sharedPopupDialog" 
                                               actionUrl="${componentName_1_warnings_url}" 
                                               logoKey="yukon.web.modules.support.databaseMigration.importValidate.warning.img"/>
						&nbsp;
						<select name="S1" class="warning">
							<option value="IGNORE">Use Existing</option>
							<option value="OVERWRITE">Overwrite</option>
						</select>
					</td>
					
					<%-- errors --%>
					<td class="component basic">
						4
						<cti:url var="componentName_1_errors_url" value="/spring/support/database/migration/errorsViewPopup">
							<cti:param name="someObjectKey" value="ABC"/>
						</cti:url>
						<tags:simpleDialogLink titleKey="yukon.web.modules.support.databaseMigration.importValidate.componentsInfoSection.header.errors" 
                                               dialogId="sharedPopupDialog" 
                                               actionUrl="${componentName_1_errors_url}" 
                                               logoKey="yukon.web.modules.support.databaseMigration.importValidate.error.img"/>
					</td>
				</tr>
				
			</table>
			
		
		</tags:sectionContainer>
		<br><br>
		
		<%-- COMMIT --%>
		<form id="cancelForm" action="/spring/support/database/migration/home" method="post">
			<input type="hidden" name="import" value="true">
    	</form>
    
		<form id="confirmForm" action="/spring/support/database/migration/importConfirm" method="post">
	    	<input type="hidden" name="fileKey" value="${fileKey}">
	    </form>
	    
	    <tags:slowInput myFormId="confirmForm" label="${commitFile}"/>
	    <tags:slowInput myFormId="cancelForm" label="${cancel}"/>

   </tags:boxContainer>
   	
   
    
    
</cti:standardPage>