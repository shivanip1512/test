<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.updatePointsResults">
 
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updatePointsResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="updatePointsResultsContainer" hideEnabled="false">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="updatePoints" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
    
</cti:standardPage>