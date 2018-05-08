<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.massDeleteConfirm">

    <tags:bulkActionContainer key="yukon.common.device.bulk.massDeleteConfirm" deviceCollection="${deviceCollection}" noteTextArguments="${deviceCount}">

        <cti:url value="/bulk/massDelete/doMassDelete" var="massDeleteUrl"/>
        <div class="page-action-area">
            <form method="post" action="${massDeleteUrl}">
                <cti:csrfToken/>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <cti:button type="submit" nameKey="delete" classes="primary action" busy="true"/>
            </form>
        </div>
    
    </tags:bulkActionContainer>
    
</cti:msgScope>