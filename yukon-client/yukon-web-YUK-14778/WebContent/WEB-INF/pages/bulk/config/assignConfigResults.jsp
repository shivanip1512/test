<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.assignConfigResults">
        
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.assignConfigResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="assignConfigResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="assignConfig" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
    
</cti:standardPage>