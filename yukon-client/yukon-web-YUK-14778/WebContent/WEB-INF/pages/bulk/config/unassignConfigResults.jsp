<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.unassignConfigResults">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.unassignConfigResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="unassignConfigResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="unassignConfig" callbackResult="${callbackResult}"/>
    </tags:sectionContainer>
    
</cti:standardPage>