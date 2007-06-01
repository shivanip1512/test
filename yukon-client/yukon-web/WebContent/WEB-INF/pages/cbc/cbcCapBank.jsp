<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.editor.point.PointForm" %>

<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.CapControlForm" %>


<%
  CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
  String itemid = JSFParamUtil.getJSFReqParam("itemid");
  String type = JSFParamUtil.getJSFReqParam("type");

  if (itemid != null && type != null)
    capControlForm.initItem(Integer.parseInt(itemid), Integer.parseInt(type));
%>

<f:subview id="cbcCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >


<f:verbatim>

<div id="pointSelectHelp">
	<jsp:include flush="true" page="include/pointSelectInc.jsp"/>	
</div>

<script type="text/javascript">
formatSelectedPoint ('cbcPointDiv');
var cbcPointPicker = new PointPicker('cbc_point','com.cannontech.common.search.criteria.CBCControlPointCriteria',
				'pointName:ctlPoint;deviceName:cbcDevice','cbcPointPicker','', attachPoint, saveOldPoint);

</script>
	
</f:verbatim>
	
	
    <x:panelGrid id="capbankBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

        <x:column>
          <f:verbatim>
             <br />
             <br />
              <fieldset>
                 <legend>
                    Cap Bank Info
                 </legend>
                </f:verbatim>    
<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="CapBank_Address" value="Address: " title="Geographical location of this CapBank" />
			<x:inputText id="CapBank_Address" value="#{capBankEditor.capBank.location}" required="true" maxlength="60" size="60"/>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="capMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
			<x:inputText id="capMapLocID" value="#{capBankEditor.capBank.capBank.mapLocationID}" required="true" maxlength="64" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankMan" value="Switch Manufacture: " title="What company manufactured this item"/>
			<x:selectOneMenu id="bankMan" value="#{capBankEditor.capBank.capBank.switchManufacture}" >
				<f:selectItem itemValue="(none)"/>
				<f:selectItem itemValue="ABB"/>
				<f:selectItem itemValue="Cannon Tech"/>
				<f:selectItem itemValue="Cooper"/>
				<f:selectItem itemValue="Trinetics"/>
				<f:selectItem itemValue="Siemens"/>
				<f:selectItem itemValue="Westinghouse"/>
			</x:selectOneMenu>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="cntrlerType" value="Controller Type: " title="What type of switch this item is"/>
			<x:selectOneMenu id="cntrlerType" value="#{capBankEditor.capBank.capBank.controllerType}" >
				<f:selectItem itemValue="(none)"/>
				<f:selectItem itemValue="CTI DLC"/>
				<f:selectItem itemValue="CTI Paging"/>
				<f:selectItem itemValue="CTI FM"/>
				<f:selectItem itemValue="FP Paging"/>
				<f:selectItem itemValue="Telemetric"/>
			</x:selectOneMenu>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankType" value="Type of Switch: " title="What type of switch this item is"/>
			<x:selectOneMenu id="bankType" value="#{capBankEditor.capBank.capBank.typeOfSwitch}" >
				<f:selectItem itemValue="(none)"/>
				<f:selectItem itemValue="Oil"/>
				<f:selectItem itemValue="Vacuum"/>
				<f:selectItem itemValue="Mix"/>

			</x:selectOneMenu>
         <f:verbatim>
             <br />
              </fieldset>
          </f:verbatim>
            <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

            <f:verbatim><br/></f:verbatim>
            
            <x:outputLabel for="bankOperation" value="Operation Method: " title="How this CapBank should operate in the field"/>
            <x:selectOneMenu id="bankOperation" 
                    value="#{capBankEditor.capBank.capBank.operationalState}"   onchange="submit()">
                <f:selectItems value="#{selLists.capBankOpStates}"/>
            </x:selectOneMenu>

            
            <f:verbatim><br/></f:verbatim>
            
            <x:outputLabel for="bankSize" value="Bank Size: " title="The total size of the CapBank"/>
			
            <x:selectOneMenu id="bankSizeSelect" 
                    value="#{capBankEditor.capBank.capBank.bankSize}" onchange="submit()" rendered="#{!capBankEditor.customSize}">
                <f:selectItems value="#{selLists.addCapBankSizes}"/>
            </x:selectOneMenu>
            <f:verbatim>&nbsp;&nbsp;</f:verbatim>

            <x:inputText id="bankSize" styleClass="char8Label" required="true"
                    value="#{capBankEditor.capBank.capBank.bankSize}" rendered="#{capBankEditor.customSize}">
                <f:validateLongRange minimum="0" maximum="99999" />
            </x:inputText>
            <x:outputText id="bankSizeDesc" value="kVar"/>
                        <f:verbatim>&nbsp;&nbsp;</f:verbatim>

		   <x:outputLabel for="isCustomSize" value="Custom Bank Size?" title="The total size of the CapBank"/>
            <x:selectBooleanCheckbox id="isCustomSize" value="#{capBankEditor.customSize}" onclick="submit()"/>

            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="bankReclose" value="Reclose Delay: " title="The amount of time this CapBank should wait before executing the close command"/>
            <x:selectOneMenu id="bankReclose"
                    value="#{capBankEditor.capBank.capBank.recloseDelay}" >
                <f:selectItem itemLabel="(none)" itemValue="0"/>
                <f:selectItems value="#{capControlForm.timeInterval}"/>
            </x:selectOneMenu>
			

                      <f:verbatim>
                    <br />
                    <br />
                </f:verbatim>

                 <x:div id="cbcPointDiv" forceId="true">
                 <x:inputHidden id="cbc_point" forceId="true"
                 value="#{capBankEditor.capBank.capBank.controlPointID }" valueChangeListener="#{capBankEditor.ctlPointChanged}"/>        
                <x:outputLabel for="cntrlPoint" value="Control Device/Point: " 
                 title="Point used for monitoring the control (Only displays points that are not yet used by CapBanks)" 
                 styleClass="medStaticLabel"/>
                 <x:outputText id="cbcDevice" forceId="true" 
                 value="#{capBankEditor.ctlPaoName}" /> 

                 <x:outputText id="ctlPoint" forceId="true" 
                 value="#{capBankEditor.ctlPointName}" /> 
                <f:verbatim>
                    <br/>
                </f:verbatim>
                    <h:outputLink  value="javascript:cbcPointPicker.showPicker()" >
                       <h:outputText value="Select point..."/>
                    </h:outputLink>
                </x:div>
                <f:verbatim>
                    <br/>
                </f:verbatim>
                 <x:commandLink id="cbcPoint_setNone" title="Do not use a point to control cap bank" 
                 styleClass="medStaticLabel"
                    value="No Control Point" actionListener="#{capBankEditor.capBankTeeClick}">
                        <f:param name="ptID" value="0"/>
                  </x:commandLink>
                

        </x:column>
        <x:column>
        
        <f:verbatim><br/><fieldset><legend>CapBank Operations</legend></f:verbatim>
            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="maxDailyOps" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
            <x:inputText id="maxDailyOps" styleClass="char16Label" required="true"
                    value="#{capBankEditor.capBank.capBank.maxDailyOps}" >
                    <f:validateLongRange minimum="0" maximum="9999" />
            </x:inputText>
            <x:outputText id="maxDailyOpsDesc" value="(0 = unlimited)"/>
    
            <f:verbatim><br/></f:verbatim>
            <x:selectBooleanCheckbox id="disabledOps"
                    value="#{capBankEditor.capBank.capBank.maxOperationDisabled}" />
            <x:outputLabel for="disabledOps" value="Disable upon reaching max operations" title="Should we be automatically disabled after reaching our max op counts"/>
            <f:verbatim><br><br></f:verbatim>
            <x:outputText styleClass="legendLikeLabel" value="CapBank Points"/>
            <f:verbatim><br><br></f:verbatim>
            <x:div forceId="true" id = "CapBankPointsDiv" styleClass="scrollVerySmall">
             <x:dataList id="CapBankPointsList" var="item" value="#{capControlForm.capBankPointList}" layout="unorderedList" styleClass="listWithNoBullets">                    
               <x:panelGroup>
                <f:verbatim>&nbsp;&nbsp;&nbsp;</f:verbatim>
                  <x:commandLink id="ptLink" value="#{item.pointName}" actionListener="#{capControlForm.capBankPointClick}" >
                     <f:param name="ptID" value="#{item.liteID}" />
                  </x:commandLink>
               </x:panelGroup>
             </x:dataList>
          </x:div>
            <f:verbatim><br><br></f:verbatim>
            <f:verbatim></fieldset></f:verbatim>
                    <f:verbatim><br/><fieldset><legend>Controller Configuration</legend></f:verbatim>
                        <f:verbatim><br/></f:verbatim> 
                         
                         <x:outputText value="CBC Controller: " title="Click on the link to edit the CBC"/>
                            <x:commandLink rendered="#{capBankEditor.ctlPaoID != 0}" id="CBCEditor" value="#{capBankEditor.ctlPaoName}"
                           actionListener="#{capControlForm.paoClick}" title="Click on the link to edit the CBC" >
                               
                                <f:param name="paoID" value="#{capBankEditor.ctlPaoID}"/>
                            </x:commandLink>
                            <x:outputText styleClass="medStaticLabel" value="No Controller Selected" rendered="#{capBankEditor.ctlPaoID == 0}"/> 
						
            <f:verbatim><br/></f:verbatim>
            <x:panelGroup id="oneWayCBC" rendered="#{capBankEditor.oneWayController}">
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlSerNum" value="Serial Number: " title="Serial number of the controller device" rendered="#{capBankEditor.capBank.capBank.controlPointID != 0}"/>
                <x:inputText id="cntrlSerNumEd" styleClass="staticLabel" disabled="true" rendered="#{capBankEditor.capBank.capBank.controlPointID != 0}"
                        value="#{capBankEditor.controller.deviceCBC.serialNumber}" maxlength="9" size="9">
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" rendered="#{capBankEditor.capBank.capBank.controlPointID != 0}"/>
                <x:selectOneMenu id="cntrlRoute" value="#{capBankEditor.controller.deviceCBC.routeID}" disabled="true" rendered="#{capBankEditor.capBank.capBank.controlPointID != 0}">
                    <f:selectItem itemLabel="(none)" itemValue="0"/>
                    <f:selectItems value="#{selLists.routes}"/>
                </x:selectOneMenu>
            </x:panelGroup>


            <x:panelGroup id="twoWayCBC" rendered="#{capBankEditor.twoWayController}" >
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlSerNum2" value="Serial Number: " title="Serial number of the controller device" />
                <x:inputText id="cntrlSerNumEd2" styleClass="staticLabel" disabled="true"
                        value="#{capBankEditor.controller.deviceCBC.serialNumber}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>
                
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field" />
                <x:inputText id="cntrlMast" styleClass="staticLabel" disabled="true"
                        value="#{capBankEditor.controller.deviceAddress.masterAddress}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field" />
                <x:inputText id="cntrlMSlav" styleClass="staticLabel" disabled="true"
                        value="#{capBankEditor.controller.deviceAddress.slaveAddress}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses" />
                <x:selectOneMenu id="cntrlComChann" value="#{capBankEditor.controller.deviceDirectCommSettings.portID}" disabled="true">
                    <f:selectItem itemLabel="(none)" itemValue="0"/>
                    <f:selectItems value="#{selLists.commChannels}"/>
                </x:selectOneMenu>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="How long to wait after communications" />
                <x:inputText id="cntrlCommW" styleClass="staticLabel" disabled="true"
                        value="#{capBankEditor.controller.deviceAddress.postCommWait}" >
                    <f:validateLongRange minimum="0" maximum="99999" />
                </x:inputText>


                <x:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
                    <x:column>
                  
                    <f:verbatim><br/></f:verbatim>
                    <x:selectBooleanCheckbox id="scanIntegrityChk" value="#{capBankEditor.editingIntegrity}"
                            immediate="true" disabled="true"/>
                    <x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityInterval"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].intervalRate}" 
                            disabled="true">
                        <f:selectItems value="#{selLists.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityAltInterval" disabled="true"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].alternateRate}" >
                        <f:selectItems value="#{selLists.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityGrp" disabled="true"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].scanGroup}" >
                        <f:selectItem itemLabel="Default" itemValue="0" />
                        <f:selectItem itemLabel="First" itemValue="1" />
                        <f:selectItem itemLabel="Second" itemValue="2" />
                    </x:selectOneMenu>
                    </x:column>


                    <x:column>
                    <f:verbatim><br/></f:verbatim>
                    <x:selectBooleanCheckbox id="scanExceptionChk" 
                            value="#{capBankEditor.editingException}"
                            immediate="true" disabled="true"/>
                    <x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionInterval" disabled="true"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Exception'].intervalRate}" >
                        <f:selectItems value="#{selLists.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionAltInterval" disabled="true"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Exception'].alternateRate}" >
                        <f:selectItems value="#{selLists.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionGrp" disabled="true"
                            rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
                            value="#{capBankEditor.controller.deviceScanRateMap['Exception'].scanGroup}" >
                        <f:selectItem itemLabel="Default" itemValue="0" />
                        <f:selectItem itemLabel="First" itemValue="1" />
                        <f:selectItem itemLabel="Second" itemValue="2" />
                    </x:selectOneMenu>
                    </x:column>
                    
                </x:panelGrid>

            </x:panelGroup>
            
       <f:verbatim></fieldset></f:verbatim>
       
     
  </x:column>
    

    
    </x:panelGrid>
 			
    <x:inputHidden id="capbankHiden" forceId="true" value="#{capControlForm.offsetMap['capbankHiden']}"/>

</f:subview>