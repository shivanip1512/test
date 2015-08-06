<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<f:verbatim>

<cti:pickerProperties var="outputColumns" property="OUTPUT_COLUMNS" type="statusPointPicker"/>
<cti:pickerProperties var="idField" property="ID_FIELD_NAME" type="statusPointPicker"/>

<script type="text/javascript">
(function () {
    
    yukon.pickers = yukon.pickers || {};
    
    var specAreaVoltReductionPointPicker = new Picker('OK', 
            'Cancel', 
            '(none selected)', 
            'statusPointPicker', 
            '',
            'specAreaVoltReductionPointPicker', 
            'pointId:specAreaVoltReductionPointValue;pointName:specAreaVoltReductionPoint;deviceName:areaDevice');
    
    specAreaVoltReductionPointPicker.destinationFieldId = 'specAreaVoltReductionPointValue';
    specAreaVoltReductionPointPicker.outputColumns = ${outputColumns};
    specAreaVoltReductionPointPicker.idFieldName = '${idField}';
    
    yukon.pickers['specAreaVoltReductionPointPicker'] = specAreaVoltReductionPointPicker;
    
    $(function () {
        $('.js-volt-reduction-point').click(function () {
            yukon.pickers['specAreaVoltReductionPointPicker'].show();
        });
    });
})();
</script>
</f:verbatim>

<f:subview id="specialAreaSetup" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
        <x:panelGrid id="areaBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
            <h:column>
    
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <f:verbatim><legend>Special Area Info</legend></f:verbatim>
    
                    <x:outputLabel for="geoLocation" value="#{capControlForm.PAODescLabel}: " 
                        title="Physical location of the area." 
                        rendered="#{!empty capControlForm.PAODescLabel}" />
                    <x:inputText id="geoLocation" value="#{capControlForm.PAOBase.geoAreaName}" 
                        required="true" maxlength="60" styleClass="char32Label" 
                        rendered="#{!empty capControlForm.PAODescLabel}" />

                    <f:verbatim><br /></f:verbatim>
                </x:htmlTag>
                
                <x:panelGroup>
                    <x:htmlTag value="br"/>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Special Area Points"/></x:htmlTag>
                        <x:div forceId="true" id="SubstationBusEditorScrollDiv" styleClass="scrollSmall">
                        <%-- Find out why you can't do this and why the capcontrolform is newly created! --%>
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
                                            <f:param name="tabId" value="4"/>
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
                    <f:verbatim><legend>Volt Reduction Control Point Setup</legend></f:verbatim>
    
                    <x:div id="specAreaVoltReductionPointDiv" forceId="true">
                    
                        <x:inputHidden id="specAreaVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId }" />
                        <x:outputLabel for="areaDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
                        <x:outputText id="areaDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId]}"/> 
                        <x:outputText id="areaDevicePointSeperator" forceId="true" value=" : " />
                        <x:outputText id="specAreaVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId]}" /> 
    
                        <x:htmlTag value="br"/>
    
                        <h:outputLink  value="javascript:void(0);" styleClass="js-volt-reduction-point"
                            rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point"/>
                        </h:outputLink>
                 
                        <x:htmlTag value="br"/>
                        <x:htmlTag value="br"/>
                        
                        <x:commandLink id="specialAreaVoltReductionPoint_setNone" 
                            rendered="#{capControlForm.editingAuthorized}"
                            title="Do not use a point for control." 
                            styleClass="medStaticLabel"
                            value="No Volt Reduction Point" 
                            actionListener="#{capControlForm.specialAreaNoVoltReductionPointClicked}">
                            <f:param name="ptId" value="0"/>
                        </x:commandLink>
                    </x:div>
    
                </x:htmlTag>
    
            </h:column>
            
        </x:panelGrid>
        
    </f:subview>
</f:subview>
    