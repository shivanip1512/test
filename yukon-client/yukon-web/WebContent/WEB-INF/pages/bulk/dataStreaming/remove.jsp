<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.dataStreaming.remove">

    <cti:flashScopeMessages/>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.remove" deviceCollection="${deviceCollection}">
    
        <form method="post" action="<cti:url value="/bulk/dataStreaming/remove" />">
            <cti:csrfToken/>
            
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <div style="margin-top:50px;"><b>
                <i:inline key=".message"/>
            </b></div>
                
            <div class="page-action-area">
                <cti:button nameKey="remove" classes="primary action js-action-submit" busy="true"/>
            </div>
        </form>
            
    </tags:bulkActionContainer>
    
</cti:msgScope>