<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="bulk.dataStreaming.verification">
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.verification" deviceCollection="${deviceCollection}">
        <div class="page-action-area">
            <cti:url var="assignUrl"  value="/bulk/dataStreaming/verification" />
            <form:form id="configureForm" method="post" commandName="behavior" action="${assignUrl}">
                <cti:csrfToken/>
                <form:hidden path="id" />
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
        
                <i:inline key=".message"/>
                
                <div class="page-action-area">
                    <cti:button nameKey="back" href="javascript:window.history.back()" name="backButton" classes="action" busy="true"/>
                    <cti:button nameKey="send" type="submit" name="sendButton" classes="primary action" busy="true"/>
                </div>
            </form:form>
        </div>
    </tags:bulkActionContainer>
        
</cti:standardPage>