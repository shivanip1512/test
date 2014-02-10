<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="tools" page="bulk.unassignConfig">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.unassignConfig" deviceCollection="${deviceCollection}">
    
        <form id="unassignConfigForm" method="post" action="<cti:url value="/bulk/config/doUnassignConfig" />">
            <cti:csrfToken/>
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- UNASSIGN BUTTON --%>
            <cti:button nameKey="unassign" type="submit" name="unassignButton" value="${unassign}" classes="primary action" busy="true"/>
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>