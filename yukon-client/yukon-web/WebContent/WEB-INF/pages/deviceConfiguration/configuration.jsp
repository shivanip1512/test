<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="configs.config.${mode}">

<cti:includeScript link="/resources/js/pages/yukon.dialog.ajax.js"/>
<cti:includeScript link="/resources/js/pages/yukon.device.config.js"/>

<style>
    .category-fields-title {padding-left: 5px;}
    .category-fields-content {padding-left: 20px;}
</style>

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="VIEW,EDIT">
    <table class="stacked-md">
        <tr>
            <td><span class="label label-warning"><i:inline key="yukon.common.warning"/></span></td>
            <td><i:inline key=".changeWarning"/><i:inline key=".unassignWarning"/></td>
        </tr>
    </table>
</cti:displayForPageEditModes>

<form:form modelAttribute="deviceConfig" action="save">
    <cti:csrfToken/>
    <form:hidden path="configId"/>
    
    <c:if test="${deviceConfigTypes.supportedTypesEmpty}">
        <tags:alertBox key=".noSupportedTypesPage"/>
    </c:if>
    
    <!-- CONFIGURATION NAME AND DESCRIPTION -->
    <tags:nameValueContainer2 tableClass="stacked">
        <tags:inputNameValue nameKey=".name" path="configName" size="50" maxlength="60"/>
        <tags:textareaNameValue nameKey=".description" rows="4" cols="100" path="description"/>
    </tags:nameValueContainer2>
    
    <!-- PAGE ACTION BUTTONS -->
    <div class="page-action-area clear" style="margin-bottom: 20px;">
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <cti:url value="edit" var="editUrl">
                    <cti:param name="configId" value="${deviceConfig.configId}"/>
                </cti:url>
                <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
            </cti:checkRolesAndProperties>
            <cti:url var="backUrl" value="/deviceConfiguration/home"/>
            <cti:button nameKey="back" name="back" href="${backUrl}"/>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <cti:button nameKey="save" id="save" type="submit" classes="primary action"/>
                <c:if test="${isDeletable}">
                    <d:confirm on="#remove" nameKey="confirmRemove"/>
                    <cti:url var="deleteUrl" value="delete">
                        <cti:param name="configId" value="${deviceConfig.configId}"/>
                    </cti:url>
                    <cti:button nameKey="remove" id="remove" href="${deleteUrl}" classes="delete" disabled="${disabled}"/>
                </c:if>
                <cti:url var="viewUrl" value="view">
                    <cti:param name="configId" value="${deviceConfig.configId}"/>
                </cti:url>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <cti:button nameKey="create" type="submit" classes="primary action"/>
                <cti:url var="cancelUrl" value="/deviceConfiguration/home"/>
                <cti:button nameKey="cancel" name="cancel" href="${cancelUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
    </div>
</form:form>

<cti:displayForPageEditModes modes="VIEW,EDIT">
    
    <div class="column-6-18">
        
        <%-- CATEGORIES --%>
        <div class="column one">
            <form:form modelAttribute="deviceConfigTypes" action="addSupportedType">
                <cti:csrfToken/>
                <input type="hidden" name="configId" value="${deviceConfig.configId}"/>
                
                <c:forEach var="item" items="${deviceConfigTypes.supportedTypes}">
                    <form:hidden path="supportedTypes[${item.key}]"/>
                </c:forEach>
                
                <tags:sectionContainer title="Device Types">
                    <ul class="editable-list">
                        
                        <c:forEach var="type" items="${deviceConfigTypes.supportedTypes}">
                            
                            <c:if test="${not empty type.value}">
                                <li class="js-categories" data-device-type="${type.key}">
                                    <a href="javascript:void(0);" class="pipe-selector ">
                                        <span data-device-type-${type.key} class="pipe">&nbsp;</span>
                                        <span><i:inline key="${type.key}"/></span>
                                    </a>
                                    
                                    <cti:checkRolesAndProperties value="${editingRoleProperty}">
                                        <cti:displayForPageEditModes modes="VIEW">
                                            <c:if test="${fn:length(deviceConfigTypes.supportedTypes) > 1}">
                                                
                                                <d:confirm on="#remove-${type.key}" nameKey="confirmSupportedTypeRemove"
                                                    argument="${type.key.dbString}"/>
                                                <cti:url var="removeUrl" value="removeSupportedType">
                                                    <cti:param name="configId" value="${deviceConfig.configId}"/>
                                                    <cti:param name="paoType" value="${type.key}"/>
                                                </cti:url>
                                                
                                                <cti:button nameKey="remove" icon="icon-cross" renderMode="image" 
                                                    id="remove-${type.key}" classes="remove-item fr vh" 
                                                    href="${removeUrl}"/>
                                                
                                            </c:if>
                                        </cti:displayForPageEditModes>
                                    </cti:checkRolesAndProperties>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </tags:sectionContainer>
                
                <cti:displayForPageEditModes modes="VIEW">
                    <cti:checkRolesAndProperties value="${editingRoleProperty}">
                        <c:if test="${! empty deviceConfigTypes.availableTypes}">
                            <div class="action-area">
                                <cti:button nameKey="addTypes" icon="icon-add" renderMode="button"
                                data-config-id="${deviceConfig.configId}" data-popup="#supportedTypePopup"
                                data-show-device-types="${showTypesPopupOnLoad}" />
                            </div>
                        </c:if>
                    </cti:checkRolesAndProperties>
                </cti:displayForPageEditModes>
            </form:form>
        </div>
        
        <div class="column two nogutter">
            <tags:sectionContainer title="Categories">
                <div class="separated-sections">
                    <c:forEach var="category" items="${configCategories.categorySelections}" varStatus="loopStatus">
                        <div class="pipe-selector section <c:if test="${!loopStatus.last}">stacked</c:if>">
                            <%@ include file="categories.jspf" %>
                        </div>
                    </c:forEach>
                </div>
            </tags:sectionContainer>
        </div>
    </div>
</cti:displayForPageEditModes>

<cti:url var="processAddTypes" value="processAddTypes">
    <cti:param name="configId" value="${deviceConfig.configId}" />
</cti:url>
<cti:msg2 var="popupTitle" key="yukon.web.modules.tools.configs.config.addSupportedTypes.title"/>

<div id="supportedTypePopup" data-dialog data-config-id="${deviceConfig.configId}"
    data-url="${processAddTypes}" data-width="900" data-title="${popupTitle}" data-form="#supported-types-form" />

<div id="category-popup" class="dn" />

</cti:standardPage>