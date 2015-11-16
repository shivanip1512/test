<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.support.databaseMigration.validationProgress.pageTitle"/>
<cti:msg var="homePageTitle" key="yukon.web.modules.support.databaseMigration.pageTitle"/>
<cti:msg var="boxTitle" key="yukon.web.modules.support.databaseMigration.validationProgress.boxTitle"/>
<cti:msg var="viewResultButton" key="yukon.web.modules.support.databaseMigration.validationProgress.viewResultsButton"/>

<cti:standardPage title="${pageTitle}" module="support">

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Home"  />
        <cti:crumbLink url="/support" title="Support" />
        <cti:crumbLink url="/support/database/migration/home" title="${homePageTitle}">
            <cti:param name="import" value="true"/>
        </cti:crumbLink>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    
    <script type="text/javascript">

        var buttonHasBeenEnabled = false;
    
        $(function() {

            <c:choose>
                <c:when test="${not status.complete}">
                    $('#validateButton').prop('disabled', true);
                </c:when>
                <c:otherwise>
                    buttonHasBeenEnabled = true;
                </c:otherwise>
            </c:choose>
        });

        
        function enableMigrationImportValidateButton() {
            try {
                if (!buttonHasBeenEnabled) {
                    $('#validateButton').prop('disabled', false);
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
        <form id="importValidateForm" action="/support/database/migration/importValidate" method="get">
            <input type="hidden" name="statusKey" value="${status.id}">
            <cti:button type="submit" busy="true" label="${viewResultButton}"/>
        </form>

   </tags:boxContainer>

</cti:standardPage>