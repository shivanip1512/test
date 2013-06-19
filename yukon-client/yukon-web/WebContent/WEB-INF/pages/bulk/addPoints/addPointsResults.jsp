<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.addPointsResults">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.addPointsResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="addPointsResultsContainer" hideEnabled="false">
        <%-- RESULTS --%>
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="addPoints" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
    
</cti:standardPage>