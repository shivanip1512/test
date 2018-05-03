<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.demandReset">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.demandReset" deviceCollection="${deviceCollection}">
    
        <cti:url value="/bulk/demand-reset/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <form:form action="${commandUrl}" method="post" >
            <cti:csrfToken/>
            <div class="page-action-area">
                <cti:button nameKey="start" classes="primary action js-action-submit" busy="true"/>
            </div>
        </form:form>
    
    </tags:bulkActionContainer>
    
</cti:msgScope>