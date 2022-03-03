<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="commChannelDetail">
    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE"> 
            <cti:msg2 key="yukon.web.modules.operator.commChannel.create" var="popupTitle"/>
            <cti:url var="createUrl" value="/stars/device/commChannel/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" data-popup="#js-create-comm-channel-popup"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER"> 
            <!-- Delete -->
            <c:set var="toolTipMessage" value=""/>
            <c:set var="disableFlag" value="false"/>
            <c:if test="${not empty deviceNames}">
                <c:set var="disableFlag" value="true"/>
                <cti:msg2 var="toolTipMessage" key="yukon.web.modules.operator.commChannel.delete.devicesAssigned.error" argument="${fn:escapeXml(name)}"/>
            </c:if>
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-delete" title="${toolTipMessage}"  key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="js-delete-option"
                               data-ok-event="yukon:commChannel:delete" disabled="${disableFlag}"/>
            <c:if test="${empty deviceNames}">
                <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${fn:escapeXml(name)}" />
            </c:if>
            <cti:url var="deleteUrl" value="/stars/device/commChannel/delete/${id}"/>
            <form:form id="delete-commChannel-form" action="${deleteUrl}" method="delete">
               <cti:csrfToken/>
            </form:form>
        </cti:checkRolesAndProperties> 
    </div>
    <tags:widgetContainer deviceId="${id}" identify="false">
        <div class="column-12-12 clearfix">
            <div class="one column">
                <tags:widget bean="commChannelInfoWidget"/>
            </div>
        </div>
        <div>
            <tags:widget bean="commChannelLinkedDeviceWidget"/>
        </div>
    </tags:widgetContainer>

    <cti:msg2 var="saveText" key="components.button.save.label"/>
    <div id="js-create-comm-channel-popup" 
         class="dn"
         data-title="${popupTitle}"
         data-dialog
         data-ok-text="${saveText}" 
         data-width="500"
         data-height="400"
         data-event="yukon:assets:commChannel:create" 
         data-url="${createUrl}"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:standardPage>
