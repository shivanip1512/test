<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        var specAreaVoltReductionPointPicker = new PointPicker('specAreaVoltReductionPointValue','com.cannontech.common.search.criteria.StatusPointCriteria','pointName:specAreaVoltReductionPoint;deviceName:areaDevice','specAreaVoltReductionPointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
</f:verbatim>

<f:subview id="specialAreaSetup" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
    <x:panelGrid id="areaBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">
    <h:column>
    
    <f:verbatim>
    <br />
    <fieldset class="fieldSet">
    <legend>
    Special Area Info
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
    
    </h:column>
    <h:column>
    
    <f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
    <legend>
    Volt Reduction Control Point Setup
    </legend>
    <br/>
    </f:verbatim>
    
    <x:div id="specAreaVoltReductionPointDiv" forceId="true">
    <x:inputHidden id="specAreaVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId }" />
    <x:outputLabel for="areaDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
    <x:outputText id="areaDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId]}"/> 
    <x:outputText id="areaDevicePointSeperator" forceId="true" value=" : " />
    <x:outputText id="specAreaVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSpecialArea.voltReductionPointId]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:specAreaVoltReductionPointPicker.showPicker()" >
        <h:outputText value="Select point"/>
    </h:outputLink>
                 
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:commandLink id="specialAreaVoltReductionPoint_setNone" 
        title="Do not use a point for control." 
        styleClass="medStaticLabel"
        value="No Volt Reduction Point" 
        actionListener="#{capControlForm.specialAreaNoVoltReductionPointClicked}">
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
    