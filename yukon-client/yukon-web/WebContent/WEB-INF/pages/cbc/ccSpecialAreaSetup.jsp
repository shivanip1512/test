<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        formatSelectedPoint ('areaControlPointDiv');
        var areaControlPointPicker = new PointPicker('controlPoint','com.cannontech.common.search.criteria.StatusPointCriteria','pointName:areaControlPoint;deviceName:areaDevice','areaControlPointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
</f:verbatim>

<f:subview id="specialAreaSetup" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
    <x:panelGrid id="areaBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">
    <h:column>
    <f:verbatim>
    <br />
    <br />
    <fieldset>
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
    
    </h:column>
    <h:column>
    <f:verbatim>
    <br />
    <br />
    <fieldset>
    <legend>
    Control Point Setup
    </legend>
    </f:verbatim>
    <x:div id="areaControlPointDiv" forceId="true">
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <x:inputHidden id="controlPoint" forceId="true" value="#{capControlForm.PAOBase.capControlSpecialArea.controlPointId }" />
    <x:outputLabel for="areaDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
    <x:outputText id="areaDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSpecialArea.controlPointId]}"/> 
    <x:outputText id="areaDevicePointSeperator" forceId="true" value=" : " />
    <x:outputText id="areaControlPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSpecialArea.controlPointId]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:areaControlPointPicker.showPicker()" >
    <h:outputText value="Select point"/>
    </h:outputLink>
                 
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:commandLink id="varPoint_setNone" 
        title="Do not use a point for control." 
        styleClass="medStaticLabel"
        value="No Control Point" 
        actionListener="#{capControlForm.specialAreaNoPointClicked}">
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
    
