<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
                    var areaVoltReductionPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'voltReductionPointPicker',
                            '', 'areaVoltReductionPointPicker', 
                            'pointId:areaVoltReductionPointValue;pointName:areaVoltReductionPoint;deviceName:areaDevice');
                    areaVoltReductionPointPicker.destinationFieldId = 'areaVoltReductionPointValue';
                    <cti:pickerProperties var="outputColumns" property="OUTPUT_COLUMNS" type="voltReductionPointPicker"/>
                    areaVoltReductionPointPicker.outputColumns = ${outputColumns};
                </script>
</f:verbatim>

<f:subview id="areaSetup" rendered="#{capControlForm.visibleTabs['CBCArea']}">
    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}">
        <x:panelGrid id="areaBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
            <h:column>
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Area Info"/></x:htmlTag>
                    <x:outputLabel for="geoLocation" value="#{capControlForm.PAODescLabel}: " 
                        title="Physical location of the area." 
                        rendered="#{!empty capControlForm.PAODescLabel}" />
                    <x:inputText id="geoLocation" value="#{capControlForm.PAOBase.geoAreaName}" 
                        required="true" maxlength="60" styleClass="char32Label" 
                        rendered="#{!empty capControlForm.PAODescLabel}" />

                    <x:htmlTag value="br"/>
                </x:htmlTag>
            
                <x:panelGroup>
                    <x:htmlTag value="br"/>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Area Points"/></x:htmlTag>
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
                                            <f:param name="tabId" value="3"/>
                                        </x:commandLink>
                                    </x:panelGroup>
                                </f:facet>
                            </x:tree2>
                        </x:div>
                    </x:htmlTag>
                </x:panelGroup>
    
                <x:htmlTag value="br"/>    
                
                <h:outputText styleClass="tableHeader" value="Point Editor: " rendered="#{capControlForm.editingAuthorized}"/>
    
                <x:commandLink id="addPtLnk" value="Add Point" actionListener="#{capControlForm.pointTreeForm.addPointClick}" rendered="#{capControlForm.editingAuthorized}">
                    <f:param name="parentId" value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
                    <f:param name="tabId" value="3"/>
                </x:commandLink>
    
			    <x:outputText value=" | " rendered="#{capControlForm.editingAuthorized}"/>
    
                <x:commandLink id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.pointTreeForm.deletePointClick}" rendered="#{capControlForm.editingAuthorized}">
                    <f:param name="tabId" value="3"/>
                </x:commandLink>
    
            </h:column>
            
            <h:column>
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Volt Reduction Control Point Setup"/></x:htmlTag>
                    <x:div id="areaVoltReductionPointDiv" forceId="true">
                        
                        <x:htmlTag value="br"/>
    
                        <x:inputHidden id="areaVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlArea.voltReductionPointId }" />
                        <x:outputLabel for="areaDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
                        <x:outputText id="areaDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlArea.voltReductionPointId]}"/> 
                        <x:outputText id="areaDevicePointSeperator" forceId="true" value=" : " />
                        <x:outputText id="areaVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlArea.voltReductionPointId]}" /> 
    
                        <x:htmlTag value="br"/>
                            
                        <h:outputLink  value="javascript:areaVoltReductionPointPicker.show.call(areaVoltReductionPointPicker)" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point"/>
                        </h:outputLink>
                 
						<x:htmlTag value="br"/>
						<x:htmlTag value="br"/>
                        
                        <x:commandLink id="areaVoltReductionPoint_setNone" 
                            title="Do not use a point for control." 
                            styleClass="medStaticLabel"
                            value="No Volt Reduction Point" 
                            actionListener="#{capControlForm.areaNoVoltReductionPointClicked}"
                            rendered="#{capControlForm.editingAuthorized}">
                            <f:param name="ptId" value="0"/>
                        </x:commandLink>

                    </x:div>
                </x:htmlTag>
    
            </h:column>
        </x:panelGrid>
    </f:subview>
</f:subview>
    