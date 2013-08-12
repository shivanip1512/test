<%@page import="com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="configs.config.${mode}">

<cti:includeScript link="/JavaScript/ajaxDialog.js"/>
<cti:includeScript link="/JavaScript/configurationCategory.js"/>

<script type="text/javascript">
jQuery(function() {
    jQuery("#addTypeBtn").click(function() {
        jQuery('#supportedTypePopup').load('processAddTypes', {configId : '${deviceConfigurationBackingBean.configId}'});
    });

    jQuery(".f-removeTypeBtn").click(function() {
        var typeClass = jQuery(this).attr('class').match(/f-deviceType-(\w*)/)[0];
        var splitClass = typeClass.split('-');
        var devType = splitClass[splitClass.length - 1];
        jQuery('#supportedTypePopup').load('removeSupportedTypeConfirm', {paoType : devType, configId : '${deviceConfigurationBackingBean.configId}'});
    });
    
    if (${configurationDeviceTypesBackingBean.supportedTypesEmpty} && '${mode}' == 'VIEW') {
        jQuery('#supportedTypePopup').load('processAddTypes', {configId : '${deviceConfigurationBackingBean.configId}'});
    }
    
    jQuery(".f-editBtn").click(function() {
        var catTypeClass = jQuery(this).attr('class').match(/f-devType-\w*/)[0].split('-');
        var params = {'categoryId' : jQuery('#categoryId_' + catTypeClass[catTypeClass.length - 1]).val(),
                      'configId' : ${configurationCategoriesBackingBean.configId} };
        
        makeAjaxCall('editInPlace', params, jQuery(this));
    });
    
    jQuery(".f-createBtn").click(function() {
        var catTypeClass = jQuery(this).attr('class').match(/f-devType-\w*/)[0].split('-');
        var params = {'categoryType' : catTypeClass[catTypeClass.length - 1], 
                      'configId' : ${configurationCategoriesBackingBean.configId} };

        makeAjaxCall('createInPlace', params, jQuery(this));        
    });
});

</script>

    <tags:setFormEditMode mode="${mode}"/>
    
    <form:form commandName="deviceConfigurationBackingBean" action="save">
        <form:hidden path="configId"/>
        
        <c:if test="${configurationDeviceTypesBackingBean.supportedTypesEmpty}">
            <tags:alertBox nameKey=".noSupportedTypesPage"/>
        </c:if>

        <!-- CONFIGURATION NAME AND DESCRIPTION -->
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:inputNameValue nameKey=".name" path="configName" size="50" maxlength="60"/>
            <tags:textareaNameValue nameKey=".description" rows="4" cols="100" path="description" nameClass="vat"/>
        </tags:nameValueContainer2>

        <!-- PAGE ACTION BUTTONS -->
        <div class="pageActionArea clear" style="margin-bottom: 20px;">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:url value="edit" var="editUrl">
                        <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </cti:checkRolesAndProperties>
                <cti:button nameKey="back" name="back" href="/deviceConfiguration/home"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:url var="viewUrl" value="view">
                        <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                    </cti:url>
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                    <c:if test="${isDeletable}">
                        <dialog:confirm on="#remove" nameKey="confirmRemove"/>
                        <cti:url var="deleteUrl" value="delete">
                            <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                        </cti:url>
                        <cti:button nameKey="remove" id="remove" href="${deleteUrl}" disabled="${disabled}"/>
                    </c:if>
                    <cti:button nameKey="save" id="save" type="submit" classes="primary action"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:button nameKey="cancel" href="/deviceConfiguration/home"/>
                    <cti:button nameKey="create" type="submit" classes="primary action"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:displayForPageEditModes modes="VIEW,EDIT">
        <tags:sectionContainer2 nameKey="warning"><i:inline key=".changeWarning"/></tags:sectionContainer2>
        
        <div class="column_6_18">
            <!-- CATEGORIES -->
            <div class="column one">
                <form:form commandName="configurationDeviceTypesBackingBean" action="addSupportedType">
                    <input type="hidden" name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                    <c:forEach var="item" items="${configurationDeviceTypesBackingBean.supportedTypes}">
                        <form:hidden path="supportedTypes[${item.key}]"/>
                    </c:forEach>
                    
                    <tags:sectionContainer title="Device Types">
                        <ul class="editable-list">
                            <c:forEach var="type" items="${configurationDeviceTypesBackingBean.supportedTypes}">
                                <c:if test="${not empty type.value}">
                                    <li class="f-categories f-devtype-${type.key}">
                                        <a href="javascript:void(0);" class="pipe-selector ">
                                            <span class="pipe f-categories f-devtype-${type.key}">&nbsp;</span><span>${type.key.dbString}</span>
                                        </a>
                                        <cti:checkRolesAndProperties value="${editingRoleProperty}">
                                            <cti:displayForPageEditModes modes="VIEW">
                                                <c:if test="${fn:length(configurationDeviceTypesBackingBean.supportedTypes) > 1}">
                                                    <cti:button nameKey="remove" icon="icon-cross" renderMode="image" classes="remove-item fr vh f-removeTypeBtn f-deviceType-${type.key}"/>
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
                            <c:if test="${! empty configurationDeviceTypesBackingBean.availableTypes}">
                                <div class="actionArea">
                                    <cti:button nameKey="addTypes" id="addTypeBtn" icon="icon-add" renderMode="button"/>
                                </div>
                            </c:if>
                        </cti:checkRolesAndProperties>
                    </cti:displayForPageEditModes>
                </form:form>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer title="Categories">
                    <div class="separated-sections">
                        <cti:displayForPageEditModes modes="VIEW,EDIT,CREATE">
                            <c:forEach var="category" items="${configurationCategoriesBackingBean.categorySelections}" end="${fn:length(configurationCategoriesBackingBean.categorySelections)}" varStatus="loopStatus">
                                <div class="pipe-selector section <c:if test="${!loopStatus.last}">stacked</c:if>">
                                    <%@ include file="categories.jspf" %>
                                </div>
                            </c:forEach>
                        </cti:displayForPageEditModes>
                    </div>
                </tags:sectionContainer>
            </div>
        </div>
    </cti:displayForPageEditModes>
    
    <div id="supportedTypePopup"></div>
    
    <div id="categoryPopup" class="dn"></div>
</cti:standardPage>