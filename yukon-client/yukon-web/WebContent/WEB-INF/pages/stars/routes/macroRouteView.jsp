<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:msgScope paths="yukon.web.modules.operator.routes">
    <cti:standardPage module="operator" page="routes.macroRoutes.${mode}">
        <tags:setFormEditMode mode="${mode}"/>

        <!-- Actions drop-down -->
        <cti:displayForPageEditModes modes="VIEW">
            <div id="page-actions" class="dn">
                <!-- Create -->
                <cti:url var="createUrl" value="/stars/device/routes/macroRoutes/create"/>
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" data-popup="#js-create-route-popup" href="${createUrl}"/>
                <!-- Edit --> 
                <cti:url var="editUrl" value="/stars/device/routes/macroRoutes/${macroRoute.deviceId}/edit" />
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}"/>
                <!-- Delete -->
                <li class="divider"></li>
                <cm:dropdownOption icon="icon-delete" 
                                   key="yukon.web.components.button.delete.label" 
                                   classes="js-hide-dropdown" 
                                   id="js-delete" 
                                   data-ok-event="yukon:macroRoute:delete"/>
                <d:confirm on="#js-delete" nameKey="confirmDelete" argument="${macroRoute.deviceName}" />
                <cti:url var="deleteUrl" value="/stars/device/routes/macroRoutes/${macroRoute.deviceId}/delete"/>
                <form:form id="delete-macroRoute-form" action="${deleteUrl}" method="delete" modelAttribute="macroRoute">
                    <tags:hidden path="deviceId"/>
                    <tags:hidden path="deviceName"/>
                    <cti:csrfToken/>
                </form:form>
            </div>
        </cti:displayForPageEditModes>
        
        <!-- page contents -->
        <cti:url var="action" value="/stars/device/routes/save" />
        <form:form modelAttribute="macroRoute" action="${action}" method="post" id="js-macro-route-form">
            <cti:csrfToken />
            <form:hidden path="deviceId" />
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </form:form> 

        <cti:includeScript link="/resources/js/pages/yukon.assets.macroRoutes.js"/>
    </cti:standardPage>
</cti:msgScope>