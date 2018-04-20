<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.demandReset">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.demandReset" deviceCollection="${deviceCollection}">
    
        <cti:url value="/bulk/demand-reset/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <form action="${commandUrl}" method="POST">
            <cti:csrfToken/>
            <cti:button type="submit" nameKey="start" classes="primary action"/>
        </form>
    
    </tags:bulkActionContainer>
    
</cti:standardPage>