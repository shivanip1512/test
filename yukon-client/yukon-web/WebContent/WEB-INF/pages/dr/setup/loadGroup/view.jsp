<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.loadGroup">
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cti:url var="createUrl" value="/dr/setup/loadGroup/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="UPDATE">
                <cti:url var="editUrl" value="/dr/setup/loadGroup/${loadGroup.id}/edit"/>
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <li class="divider"></li>
                <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple"
                               id="copy-option" data-popup="#copy-loadGroup-popup"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="OWNER">
                <li class="divider"></li>
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="delete-option" data-ok-event="yukon:loadGroup:delete"/>
            
                <d:confirm on="#delete-option" nameKey="confirmDelete" argument="${loadGroup.name}" />
                <cti:url var="deleteUrl" value="/dr/setup/loadGroup/${loadGroup.id}/delete"/>
                <form:form id="delete-loadGroup-form" action="${deleteUrl}" method="delete" modelAttribute="loadGroup">
                    <tags:hidden path="id"/>
                    <tags:hidden path="name"/>
                    <cti:csrfToken/>
                </form:form>
            </cti:checkRolesAndProperties>
        </div>

        <!-- Copy loadGroup dialog -->
        <cti:msg2 var="copyloadGroupPopUpTitle" key="yukon.web.modules.dr.setup.loadGroup.copy"/>
        <cti:url var="renderCopyloadGroupUrl" value="/dr/setup/loadGroup/${loadGroup.id}/renderCopyLoadGroup">
            <c:if test="${isLoadGroupSupportRoute}">
                <cti:param name="routeId" value="${loadGroup.routeId}" />
            </c:if>
        </cti:url>
        <cti:msg2 var="copyText" key="components.button.copy.label"/>
        <div class="dn" id="copy-loadGroup-popup" data-title="${copyloadGroupPopUpTitle}" data-dialog data-ok-text="${copyText}" 
             data-event="yukon:loadGroup:copy" data-url="${renderCopyloadGroupUrl}"></div>
    </cti:displayForPageEditModes>

    <cti:url var="action" value="/dr/setup/loadGroup/save" />
    <form:form modelAttribute="loadGroup" action="${action}" method="post" cssClass="js-load-group-form">
        <cti:csrfToken />
        <form:hidden path="id"/>
        <input type="hidden" name="loadGroup" value="${selectedSwitchType}"> 
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input id="name" path="name" maxlength="60" autofocus="autofocus" inputClass="w300"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".type">
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                        <tags:selectWithItems items="${switchTypes}" id="type" path="type" defaultItemLabel="${selectLbl}" defaultItemValue=""/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT,VIEW">
                        <i:inline key="${loadGroup.type}"/>
                        <form:hidden path="type" value="${loadGroup.type}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                
                    <c:if test="${selectedSwitchType == 'LM_GROUP_ITRON'}">
                        <%@ include file="itron.jsp" %>
                    </c:if>
                    <c:if test="${isLoadGroupSupportRoute}">
                        <tags:nameValue2 nameKey=".route" rowClass="noswitchtype">
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <tags:selectWithItems items="${routes}" id="route" path="routeId" itemValue="liteID"/>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">
                                <cti:deviceName deviceId="${loadGroup.routeId}"/>
                            </cti:displayForPageEditModes>
                        </tags:nameValue2>
                    </c:if>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <div id='js-loadgroup-container' class='noswitchtype'>
            <!-- Include jsp for load group type -->
            <c:if test="${selectedSwitchType == 'LM_GROUP_EXPRESSCOMM' ||
                      selectedSwitchType == 'LM_GROUP_RFN_EXPRESSCOMM'}">
                <%@ include file="expresscom.jsp" %>
            </c:if>
            <c:if test="${selectedSwitchType == 'LM_GROUP_VERSACOM'}">
                <%@ include file="versacom.jsp" %>
            </c:if>
            <c:if test="${selectedSwitchType == 'LM_GROUP_DIGI_SEP'}">
                <%@ include file="digisep.jsp" %>
            </c:if>
            <c:if test="${selectedSwitchType == 'LM_GROUP_EMETCON'}">
                <%@ include file="emetcon.jsp" %>
            </c:if>
            <c:if test="${isMctGroupSelected}">
                <%@ include file="mctGroup.jsp" %>
            </c:if>
            <c:if test="${isRippleGroupSelected}">
                <input type="hidden" class="js-ripple-group-selected">
                <%@ include file="rippleGroup.jsp" %>
            </c:if>
            <c:if test="${isPointGroupSelected}">
                <%@ include file="pointGroup.jsp" %>
            </c:if>
            <c:if test="${not empty selectedSwitchType}">
                <%@ include file="loadGroupOptional.jsp" %>
            </c:if>
        </div>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button id="save" nameKey="save" type="submit" classes="primary action" busy="true"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/loadGroup/${loadGroup.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url var="setupUrl" value="/dr/setup/filter?filterByType=LOAD_GROUP" />
                <cti:button nameKey="cancel" href="${setupUrl}" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroup.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroupExpresscom.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroupVersacom.js" />
</cti:msgScope>