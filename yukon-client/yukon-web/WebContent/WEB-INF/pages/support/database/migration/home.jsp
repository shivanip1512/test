<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="exportConfig" key="yukon.web.modules.support.databaseMigration.exportConfig"/>
<cti:msg var="importConfig" key="yukon.web.modules.support.databaseMigration.importConfig"/>
<cti:msg var="cancel" key="yukon.web.modules.support.databaseMigration.cancel"/>

<cti:standardPage module="support" page="databaseMigration">

    <tags:simpleDialog id="sharedPopupDialog"/>
    
    <cti:tabs>
    
   		<%-- EXPORT TAB --%>
		<cti:tab title="${exportConfig}" selected="${exportTab}">
			<%@ include file="export.jspf" %>
		</cti:tab>
		
		
		<%-- IMPORT TAB --%>
		<cti:tab title="${importConfig}" selected="${importTab}">
			<%@ include file="loadImport.jspf" %>
		</cti:tab>
    
    </cti:tabs>
    
    
</cti:standardPage>