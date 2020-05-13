<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="commChannelDetail">
    <!-- Actions dropdown -->
    <!-- TODO : Replace the list urls with create and delete url -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/commChannel/list" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="create-option" href="${createUrl}"/>
        <!-- Delete -->
        <li class="divider"></li>
        <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="delete-option" 
           data-ok-event="yukon:commChannel:delete"/>

        <cti:msg2 var="userWarningMessage" key="yukon.web.modules.operator.commChannel.delete.warning"/>
        <d:confirm on="#delete-option" nameKey="confirmDelete" argument="${fn:escapeXml(commChannel.name)}" userMessage="${userWarningMessage}" userMessageClass="warning"/>
        <cti:url var="deleteUrl" value="/stars/device/commChannel/delete/${commChannel.id}"/>
            <form:form id="delete-commChannel-form" action="${deleteUrl}" method="delete" modelAttribute="commChannel">
                  <tags:hidden path="id" />
                 <cti:csrfToken/>
            </form:form>
    </div>
    <tags:widgetContainer deviceId="${id}" identify="false">
        <div class="column-12-12 clearfix">
            <div class="one column">
                <tags:widget bean="commChannelInfoWidget"/>
            </div>
        </div>
    </tags:widgetContainer>
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:standardPage>
