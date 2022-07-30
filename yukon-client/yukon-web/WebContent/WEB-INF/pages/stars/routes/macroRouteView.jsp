<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
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
                    <cti:csrfToken/>
                </form:form>
            </div>
        </cti:displayForPageEditModes>
        
        <!-- page contents -->
        <cti:url var="action" value="/stars/device/routes/macroRoutes/save" />
        <form:form modelAttribute="macroRouteModel" action="${action}" method="post" id="js-macro-route-form">
            <cti:csrfToken />
            <form:hidden path="deviceId" />
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            
            <tags:sectionContainer2 nameKey="routesAssignment">
                <div class="column-12-12 clearfix select-box bordered-div" style="margin-top: 30px;">
                    <div class="column one">
                        <h3>
                            <i:inline key="yukon.common.available" />
                        </h3>
                        <div class="bordered-div" style="height: 660px;">
                            <div id="js-unassigned-signal-transmitter-container">
                                <tags:pickerDialog id="js-signal-transmitter-picker"
                                                   type="signalTransmitterPickerForMacroRoute"
                                                   multiSelectMode="${true}"
                                                   container="js-unassigned-signal-transmitter-container"/>
                            </div>
                            <div>
                                <cti:button nameKey="add" classes="fr js-add" icon="icon-add"/>
                            </div>
                        </div>
                    </div>
                    <div class="column two nogutter">
                        <h3>
                            <i:inline key="yukon.common.selected" />
                        </h3>
                        <div class="bordered-div" style="height: 660px;">
                            <div id="js-assigned-signal-transmitter-container" class="select-box-selected js-with-movables" 
                                 data-item-selector=".select-box-item" style="min-height: 150px;">
                            </div>
                        </div>
                        <div class="dn template-row select-box-item cm" data-id="0" style="min-height: 35px;">
                            <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove" />
                            <div class="select-box-item-movers">
                                <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                                <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down"
                                            disabled="${true}" />
                            </div>
                        </div>
                    </div>
                </div>
            </tags:sectionContainer2>
            
            <div class="page-action-area">
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <cti:button id="js-save-macro-routes" nameKey="save" classes="primary action" busy="true"/>
                </cti:displayForPageEditModes>
            </div>
            
        </form:form>
        
        <script>
            $(function () {
                yukon.pickers['js-signal-transmitter-picker'].show();
            });
        </script>
        
        <cti:includeScript link="/resources/js/pages/yukon.assets.macroRoutes.js"/>
    </cti:standardPage>
</cti:msgScope>