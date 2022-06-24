<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.operator.signalTransmitter">
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:url var="createUrl" value="/stars/device/signalTransmitter/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" href="${createUrl}"/>
            <cti:url var="editUrl" value="/stars/device/signalTransmitter/${signalTransmitter.id}/edit" />
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}"/>
            
            <li class="divider"></li>
            <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple"
                               data-popup="#js-copy-signal-transmitter-popup"/>
                           
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-delete" key="yukon.web.components.button.delete.label" id="js-delete"
                               data-ok-event="yukon:signal-transmitter:delete" classes="js-hide-dropdown"/>
            <d:confirm on="#js-delete" nameKey="confirmDelete" argument="${signalTransmitter.name}" />
            <cti:url var="deleteUrl" value="/stars/device/signalTransmitter/${signalTransmitter.id}/delete" />
            <form:form id="js-delete-form" action="${deleteUrl}" method="delete" modelAttribute="signalTransmitter">
                <tags:hidden path="name" />
                <cti:csrfToken />
            </form:form>
        </div>
        <!-- Copy dialog -->
        <cti:msg2 var="copyDialogTitle" key=".copy"/>
        <cti:url var="renderCopyDialogUrl" value="/stars/device/signalTransmitter/${signalTransmitter.id}/renderCopyDialog"/>
        <cti:msg2 var="copyText" key="components.button.copy.label"/>
        <div class="dn" id="js-copy-signal-transmitter-popup" data-title="${copyDialogTitle}" data-dialog data-ok-text="${copyText}" 
             data-event="yukon:signalTransmitter:copy" data-url="${renderCopyDialogUrl}"></div>
    </cti:displayForPageEditModes>
    
    <cti:url var="action" value="/stars/device/signalTransmitter/save" />
    <form:form modelAttribute="signalTransmitter" action="${action}" method="post">
        <cti:csrfToken />
        <input type="hidden" name="signalTransmitter" value="${selectedSignalTransmitterType}"/>
        <form:hidden path="id" />
        <c:if test="${signalTransmitter.type != 'TNPP_TERMINAL'}">
            <%@ include file="general.jsp" %>
            <div id="js-signal-transmitter-container" class='noswitchtype'>
                <c:if test="${signalTransmitter.type != null}">
                    <tags:sectionContainer2 nameKey="communication">
                        <c:if test="${signalTransmitter.type == 'TAPTERMINAL'}">
                            <%@ include file="pagingTapTerminal.jsp" %>
                        </c:if>
                        <c:if test="${signalTransmitter.type == 'SNPP_TERMINAL'}">
                            <%@ include file="snppTerminal.jsp" %>
                        </c:if>
                        <c:if test="${signalTransmitter.type == 'WCTP_TERMINAL'}">
                            <%@ include file="wctpTerminal.jsp" %>
                        </c:if>
                    </tags:sectionContainer2>
                </c:if>
            </div>
        </c:if>
        <c:if test="${signalTransmitter.type == 'TNPP_TERMINAL'}">
            <%@ include file="tnppTerminal.jsp" %>
        </c:if>
        
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button id="save" nameKey="save" type="submit" classes="primary action" busy="true"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url var="listUrl" value="/stars/device/signalTransmitter/list" />
                <cti:button nameKey="cancel" href="${listUrl}" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.assets.signalTransmitter.js" />
</cti:msgScope>