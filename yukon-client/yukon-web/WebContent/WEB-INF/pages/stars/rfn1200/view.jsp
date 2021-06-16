<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="rfn1200Detail">
    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER"> 
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
            <cm:dropdownOption icon="icon-cross" title="${toolTipMessage}"  key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="js-delete-option"
                               data-ok-event="yukon:rfn1200:delete" disabled="${disableFlag}"/>
            <c:if test="${empty deviceNames}">
                <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${fn:escapeXml(name)}" />
            </c:if>
            <cti:url var="deleteUrl" value="/stars/device/rfn1200/delete/${id}"/>
            <form:form id="delete-rfn1200-form" action="${deleteUrl}" method="delete">
               <cti:csrfToken/>
            </form:form>
        </cti:checkRolesAndProperties> 
    </div>
    <tags:widgetContainer deviceId="${id}" identify="false">
        <div class="column-12-12 clearfix">
            <div class="one column">
                <tags:widget bean="rfn1200InfoWidget"/>
            </div>
        </div>
    </tags:widgetContainer>

    <cti:includeScript link="/resources/js/pages/yukon.assets.rfn1200.js"/>
</cti:standardPage>
