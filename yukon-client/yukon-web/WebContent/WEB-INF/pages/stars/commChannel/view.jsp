<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:standardPage module="operator" page="commChannelDetail">
    <!-- Actions dropdown -->
    <!-- TODO : Replace the list url with create url-->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/commChannel/list" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="create-option" href="${createUrl}"/>
        <!-- Delete -->
        <c:set var="toolTipMessage" value=""/>
        <c:set var="disableFlag" value="false"/>
        <c:if test="${not empty deviceNames}">
            <c:set var="disableFlag" value="true"/>
            <cti:list var="arguments">
                <cti:item value="${fn:escapeXml(name)}"/>
                <cti:item value="${deviceNames}"/>
            </cti:list>
            <cti:msg2 var="toolTipMessage" key="yukon.web.modules.operator.commChannel.delete.devicesAssigned.error" arguments="${arguments}"/>
        </c:if>
        <li class="divider"></li>
        <cm:dropdownOption icon="icon-cross" title="${toolTipMessage}"  key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="js-delete-option" 
            data-ok-event="yukon:commChannel:delete" disabled="${disableFlag}"/>
        <c:if test="${empty deviceNames}">
             <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${fn:escapeXml(name)}" />
        </c:if>    
        <cti:url var="deleteUrl" value="/stars/device/commChannel/delete/${id}"/>
        <form:form id="delete-commChannel-form" action="${deleteUrl}" method="delete">
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
