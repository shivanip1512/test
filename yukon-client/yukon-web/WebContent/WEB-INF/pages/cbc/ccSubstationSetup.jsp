<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        formatSelectedPoint ('substationVoltReductionPointDiv');
        var substationVoltReductionPointPicker = new PointPicker('substationVoltReductionPointValue','com.cannontech.common.search.criteria.StatusPointCriteria','pointName:substationVoltReductionPoint;deviceName:substationDevice','substationVoltReductionPointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
</f:verbatim>

<f:subview id="substationSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
    <f:subview id="paoSubstation" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
    <x:panelGrid id="substationBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">
    <h:column>
    
    <f:verbatim>
    <br />
    <fieldset>
    <legend>
    Substation Info
    </legend>
    </f:verbatim>
    
    <x:outputLabel for="geoLocation" value="#{capControlForm.PAODescLabel}: " 
        title="Physical location of the substation." 
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
    <fieldset>
    <legend>
    Volt Reduction Control Point Setup
    </legend>
    <br/>
    </f:verbatim>
    
    <x:div id="substationVoltReductionPointDiv" forceId="true">
    <x:inputHidden id="substationVoltReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSubstation.voltReductionPointId }" />
    <x:outputLabel for="substationDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
    <x:outputText id="substationDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstation.voltReductionPointId]}"/> 
    <x:outputText id="substationDevicePointSeperator" forceId="true" value=" : " />
    <x:outputText id="substationVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstation.voltReductionPointId]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:substationVoltReductionPointPicker.showPicker()" >
        <h:outputText value="Select point"/>
    </h:outputLink>
                 
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:commandLink id="substatonVoltReductionPoint_setNone" 
        title="Do not use a point for control." 
        styleClass="medStaticLabel"
        value="No Volt Reduction Point" 
        actionListener="#{capControlForm.substationNoVoltReductionPointClicked}">
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
    
