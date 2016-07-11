<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="tools" page="bulk.dataStreaming.remove">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.remove" deviceCollection="${deviceCollection}">
    
        <form id="unassignConfigForm" method="post" action="<cti:url value="/bulk/dataStreaming/remove" />">
            <cti:csrfToken/>
            
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <div class="page-action-area">
                <cti:button nameKey="remove" type="submit" name="removeButton" value="${unassign}" classes="primary action"/>
            </div>
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>