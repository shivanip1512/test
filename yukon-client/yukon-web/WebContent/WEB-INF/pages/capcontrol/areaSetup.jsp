<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        var areaVoltReductionPointPicker = new PointPicker('areaVoltReductionPointValue','com.cannontech.common.search.criteria.StatusPointCriteria','pointName:areaVoltReductionPoint;deviceName:areaDevice','areaVoltReductionPointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
</f:verbatim>

<f:subview id="areaSetup" rendered="#{capControlForm.visibleTabs['CBCArea']}">
    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}">
    <x:panelGrid id="areaBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">
    <h:column>
    <f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
    <legend>
    Area Info
    </legend>
    </f:verbatim>
    <x:outputLabel for="geoLocation" value="#{capControlForm.PAODescLabel}: " 
        title="Physical location of the area." 
        rendered="#{!empty capControlForm.PAODescLabel}" />
    <x:inputText id="geoLocation" value="#{capControlForm.PAOBase.geoAreaName}" 
        required="true" maxlength="60" styleClass="char32Label" 
        rendered="#{!empty capControlForm.PAODescLabel}" />

    <f:verbatim>
    <br />
    </fieldset>
    </f:verbatim>
    
    <x:panelGroup>
        <f:verbatim>
            <br />
            <fieldset class="fieldSet"><legend> Area Points </legend>
        </f:verbatim>
        <x:div forceId="true" id="SubstationBusEditorScrollDiv"
            styleClass="scrollSmall">
            <%-- Find out why you can't do this and why the capcontrolform is newly created! --%>
            <%-- binding="#{capControlForm.pointTreeForm.pointTree}" --%>
            <x:tree2 
                id="SubstationBusEditPointTree"
                value="#{capControlForm.pointTreeForm.pointList}" var="node"
                showRootNode="false" varNodeToggler="t" preserveToggle="true"
                clientSideToggle="false" showLines="false">

                <f:facet name="root">
                    <x:panelGroup>
                        <x:outputText id="rootLink" value="#{node.description}" />
                    </x:panelGroup>
                </f:facet>
                <f:facet name="pointtype">
                    <x:panelGroup>
                        <x:outputText id="paChCnt"
                            value="#{node.description} (#{node.childCount})"
                            rendered="#{!empty node.children}" />
                    </x:panelGroup>
                </f:facet>

                <f:facet name="sublevels">
                    <x:panelGroup>
                        <x:outputText id="subLvlCnt"
                            value="#{node.description} (#{node.childCount})"
                            rendered="#{!empty node.children}" />
                    </x:panelGroup>
                </f:facet>

                <f:facet name="points">
                    <x:panelGroup>
                        <x:commandLink id="ptLink" value="#{node.description}"
                            actionListener="#{capControlForm.pointTreeForm.pointClick}">
                            <f:param name="ptID" value="#{node.identifier}" />
                        </x:commandLink>
                    </x:panelGroup>
                </f:facet>
            </x:tree2>
        </x:div>
        <f:verbatim>
            </fieldset>
        </f:verbatim>
    </x:panelGroup>
    
    <f:verbatim>
        <br />
    </f:verbatim>
    
    <h:outputText styleClass="tableHeader" value="Point Editor: " />
    
    <x:commandLink id="addPtLnk" value="Add Point"
        actionListener="#{capControlForm.pointTreeForm.addPointClick}">
        <f:param name="parentId"
            value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
    </x:commandLink>
    
    <f:verbatim>
        &nbsp; <bold>|</bold>&nbsp;
    </f:verbatim>
    
    <x:commandLink id="deletePtLnk" value="Delete Point"
        actionListener="#{capControlForm.pointTreeForm.deletePointClick}">
    </x:commandLink>
    
    </h:column>
    <h:column>
    <f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
    <legend>
    Volt Reduction Control Point Setup
    </legend>
    </f:verbatim>
    <x:div id="areaVoltReductionPointDiv" forceId="true">
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <x:inputHidden id="areaVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlArea.voltReductionPointId }" />
    <x:outputLabel for="areaDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
    <x:outputText id="areaDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlArea.voltReductionPointId]}"/> 
    <x:outputText id="areaDevicePointSeperator" forceId="true" value=" : " />
    <x:outputText id="areaVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlArea.voltReductionPointId]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:areaVoltReductionPointPicker.showPicker()" >
    <h:outputText value="Select point"/>
    </h:outputLink>
                 
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:commandLink id="areaVoltReductionPoint_setNone" 
        title="Do not use a point for control." 
        styleClass="medStaticLabel"
        value="No Volt Reduction Point" 
        actionListener="#{capControlForm.areaNoVoltReductionPointClicked}">
    <f:param name="ptId" value="0"/>
    </x:commandLink>

    </x:div>
    <f:verbatim>
    </fieldset>
    </f:verbatim>
    
    </h:column>
    </x:panelGrid>
    </f:subview>
</f:subview>
    