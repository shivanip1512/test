<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.demandReset">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.demandReset" deviceCollection="${deviceCollection}">
    
        <cti:url value="/bulk/demand-reset/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <cti:button nameKey="start" classes="primary action" href="${commandUrl}" />
    
    </tags:bulkActionContainer>
    
</cti:standardPage>