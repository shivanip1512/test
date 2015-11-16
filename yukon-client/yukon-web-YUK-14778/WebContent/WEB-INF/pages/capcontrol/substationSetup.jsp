<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<f:verbatim>
<script type="text/javascript">
(function () {
    yukon.pickers = yukon.pickers || {};
    yukon.pickers['substationVoltReductionPointPicker'] = new Picker('OK', 
                'Cancel', 
                '(none selected)', 
                'voltReductionPointPicker',
                '', 
                'substationVoltReductionPointPicker',
                'pointId:substationVoltReductionPointValue;pointName:substationVoltReductionPoint;deviceName:substationDevice');
    
    yukon.pickers['substationVoltReductionPointPicker'].destinationFieldId = 'substationVoltReductionPointValue';
    <cti:pickerProperties var="outputColumns" property="OUTPUT_COLUMNS" type="voltReductionPointPicker"/>
    <cti:pickerProperties var="idField" property="ID_FIELD_NAME" type="voltReductionPointPicker"/>
    yukon.pickers['substationVoltReductionPointPicker'].outputColumns = ${outputColumns};
    yukon.pickers['substationVoltReductionPointPicker'].idFieldName = '${idField}';
    
    $(function () {
        $('.js-volt-reduction-picker').click(function () {
            yukon.pickers['substationVoltReductionPointPicker'].show();
        });
    });
    
})();
</script>
</f:verbatim>

<f:subview id="substationSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
    <f:subview id="paoSubstation" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
        <x:panelGrid id="substationBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
            <h:column>
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Substation Info"/></x:htmlTag>
                    <x:panelGrid columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
                        <x:outputLabel for="geoLocation" value="#{capControlForm.PAODescLabel}: " 
                            title="Physical location of the substation." 
                            rendered="#{!empty capControlForm.PAODescLabel}" />
                        <x:inputText id="geoLocation" value="#{capControlForm.PAOBase.geoAreaName}" 
                            required="true" maxlength="60" styleClass="char32Label" 
                            rendered="#{!empty capControlForm.PAODescLabel}" />
    
                        <x:outputLabel for="subMapLocID" value="Map Location ID: "
                            title="Mapping code/string used for third-party systems" />
    
                        <x:inputText id="subMapLocID"
                            value="#{capControlForm.PAOBase.capControlSubstation.mapLocationID}"
                            required="true" maxlength="48" styleClass="char32Label" />

                    </x:panelGrid>
                </x:htmlTag>
                
                <x:htmlTag value="br" />
                
                <x:panelGroup>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Substation Points"/></x:htmlTag>
                        <x:div forceId="true" id="SubstationBusEditorScrollDiv"
                            styleClass="scrollSmall">
                            <%-- binding="#{capControlForm.pointTreeForm.pointTree}" --%>
                            <x:tree2 id="SubstationBusEditPointTree"
                                value="#{capControlForm.pointTreeForm.pointList}" var="node"
                                showRootNode="false" varNodeToggler="t" preserveToggle="true"
                                clientSideToggle="true" showLines="false">

                                <f:facet name="root">
                                    <x:panelGroup>
                                        <x:outputText id="rootLink" value="#{node.description}" />
                                    </x:panelGroup>
                                </f:facet>
                                <f:facet name="pointtype">
                                    <x:panelGroup>
                                        <x:outputText id="paChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
                                    </x:panelGroup>
                                </f:facet>
    
                                <f:facet name="sublevels">
                                    <x:panelGroup>
                                        <x:outputText id="subLvlCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
                                    </x:panelGroup>
                                </f:facet>
    
                                <f:facet name="points">
                                    <x:panelGroup>
                                        <x:commandLink id="ptLink" value="#{node.description}" actionListener="#{capControlForm.pointTreeForm.pointClick}">
                                            <f:param name="ptID" value="#{node.identifier}" />
                                            <f:param name="tabId" value="2"/>
                                        </x:commandLink>
                                    </x:panelGroup>
                                </f:facet>
                            </x:tree2>
                        </x:div>

                        <f:verbatim>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <tags:pointCreation paoId="${capControlForm.itemId}"/>
                            </cti:checkRolesAndProperties>
                        </f:verbatim>

                    </x:htmlTag>
                </x:panelGroup>
            </h:column>
            
            <h:column>
        
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Volt Reduction Control Point Setup"/></x:htmlTag>
        
                    <x:div id="substationVoltReductionPointDiv" forceId="true">
                        <x:inputHidden id="substationVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSubstation.voltReductionPointId }" />
                        <x:outputLabel for="substationDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
                        <x:outputText id="substationDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstation.voltReductionPointId]}"/> 
                        <x:outputText id="substationDevicePointSeperator" forceId="true" value=" : " />
                        <x:outputText id="substationVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstation.voltReductionPointId]}" /> 
        
                        <x:htmlTag value="br"/>
        
                        <h:outputLink value="javascript:void(0);" styleClass="js-volt-reduction-picker"
                            rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point"/>
                        </h:outputLink>
                     
                        <x:htmlTag value="br"/>
                        <x:htmlTag value="br"/>
        
                        <x:commandLink id="substatonVoltReductionPoint_setNone" 
                            title="Do not use a point for control." 
                            rendered="#{capControlForm.editingAuthorized}"
                            styleClass="medStaticLabel"
                            value="No Volt Reduction Point" 
                            actionListener="#{capControlForm.substationNoVoltReductionPointClicked}">
                            <f:param name="ptId" value="0"/>
                        </x:commandLink>
    
                    </x:div>
        
                </x:htmlTag>    
            </h:column>
        </x:panelGrid>
    </f:subview>
</f:subview>
    