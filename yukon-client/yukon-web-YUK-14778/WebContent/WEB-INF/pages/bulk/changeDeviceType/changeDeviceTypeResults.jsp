<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.changeDeviceTypeResults">
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.changeDeviceTypeResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="changeDeviceTypeResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="changeDeviceType" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
</cti:standardPage>