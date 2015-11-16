<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.removePointsResults">
        
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.removePointsResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="removePointsResultsContainer" hideEnabled="false">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="removePoints" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
    
</cti:standardPage>