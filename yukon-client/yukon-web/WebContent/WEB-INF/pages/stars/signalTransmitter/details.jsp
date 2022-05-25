<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
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
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label"/>
            
            <li class="divider"></li>
            <cm:dropdownOption key="yukon.web.components.button.copy.label"
                               icon="icon-disk-multiple"/>
                           
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-delete"
                               key="yukon.web.components.button.delete.label"/>
        </div>
    </cti:displayForPageEditModes>
    
    <cti:url var="action" value="/stars/device/signalTransmitter/save" />
    <form:form modelAttribute="signalTransmitter" action="${action}" method="post">
        <cti:csrfToken />
        <input type="hidden" name="signalTransmitter" value="${selectedSignalTransmitterType}"/>
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".type">
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                        <tags:selectWithItems items="${types}"
                                              path="type"
                                              defaultItemLabel="${selectLbl}"
                                              defaultItemValue=""
                                              inputClass="js-type"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT,VIEW">
                        <i:inline key="${signalTransmitter.type}"/>
                        <!-- TODO: Check if we need this hidden field -->
                        <form:hidden path="type" value="${signalTransmitter.type}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <c:if test="${signalTransmitter.type == 'WCTP_TERMINAL' || signalTransmitter.type == 'SNPP_TERMINAL' ||  signalTransmitter.type == 'TAPTERMINAL'}">
                    <tags:nameValue2 nameKey=".pageNumber" rowClass="noswitchtype">
                        <tags:input path="pagerNumber" maxlength="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".status" rowClass="noswitchtype">
                        <tags:switchButton path="enabled" offNameKey="yukon.common.disabled" onNameKey="yukon.common.enabled" />
                    </tags:nameValue2>
                </c:if>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
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