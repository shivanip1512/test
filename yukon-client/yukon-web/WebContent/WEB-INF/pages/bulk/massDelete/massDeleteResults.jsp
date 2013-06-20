<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.massDeleteResults">

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:msg2 key="yukon.web.menu.home" var="homeCrumb"/>
        <cti:crumbLink url="/dashboard" title="${homeCrumb}"/>
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.web.modules.tools.bulk.home.pageName"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.web.modules.tools.bulk.deviceSelection.pageName"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- mass delete --%>
        <cti:crumbLink><cti:msg2 key=".pageName"/></cti:crumbLink>
        
    </cti:breadCrumbs>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.massDeleteResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="massDeleteResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="massDelete" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
    
</cti:standardPage>