<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.collectionActions.deviceCollectionReport.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- device collection report --%>
        <cti:crumbLink title="${pageTitle}"/>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    
    <cti:msg var="collectionDescription" key="${collectionDescriptionResovlable}"/>
    
    <cti:simpleReportUrlFromNameTag var="reportUrl"
                                htmlOutput="true"
                                viewType="extView"
                                viewJsp="BODY"
                                definitionName="deviceCollectionDefinition"
                                deviceGroup="${tempDeviceGroup}"
                                collectionDescription="${collectionDescription}"
                                showLoadMask="false"
                                refreshRate="0" />
                                
	<jsp:include page="${reportUrl}" />

</cti:standardPage>