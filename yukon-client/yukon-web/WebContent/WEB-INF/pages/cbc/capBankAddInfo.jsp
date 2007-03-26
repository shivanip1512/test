
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<jsp:directive.page import="com.cannontech.web.editor.CapControlForm"/>
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil"/>
<jsp:directive.page import="com.cannontech.servlet.nav.DBEditorTypes"/>
<%



CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
  String itemid = JSFParamUtil.getJSFReqParam("itemid");
  if (itemid != null)
    capControlForm.initItem(Integer.parseInt(itemid), DBEditorTypes.EDITOR_CAPCONTROL);
%>

<f:subview id="cbcCapBankInfo" rendered="#{capControlForm.visibleTabs['CBAddInfo']}" >
	
<x:panelGrid id="capbankBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
	<x:column>
		<x:outputLabel for="CapBank_MaintAreaID" value="Maintenance Area Id " title="" />
		<x:inputText id="CapBank_MaintAreaID" value="#{capBankEditor.additionalInfo.maintAreaID}"  required="true" maxlength="10" size="10">
			<f:validateLongRange minimum="-9999999999" maximum="9999999999"/>
		</x:inputText>
		
		<f:verbatim><br/></f:verbatim>
		
		<x:outputLabel for="CapBank_PoleNum" value="Pole Number " title="" />
		<x:inputText id="CapBank_PoleNum" value="#{capBankEditor.additionalInfo.poleNumber}"  required="true" maxlength="10" size="10" >
			<f:validateLongRange minimum="-9999999999" maximum="9999999999"/>
		</x:inputText>
		
		<f:verbatim><br/></f:verbatim>
			
		<x:outputLabel for="CapBank_DriveDir" value="Driving Directions " title="" />
		<x:inputText id="CapBank_DriveDir" value="#{capBankEditor.additionalInfo.driveDir}" required="true" maxlength="120" size="50">
			<f:validateLength minimum="1" maximum="120"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_Config" value="Config " title="" />
		<x:inputText id="CapBank_Config" value="#{capBankEditor.additionalInfo.capBankConfig}" required="true" maxlength="10" size="10">
			<f:validateLength minimum="1" maximum="10"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
	 <f:verbatim>
	             <br />
	             <br />
	              <fieldset>
	                 <legend>
	                    Communication
	                 </legend>
	                </f:verbatim>    
	<f:verbatim><br/></f:verbatim>
	<x:outputLabel for="CapBank_CommMed" value="Comm. Medium " title="" />
		<x:inputText id="CapBank_CommMed" value="#{capBankEditor.additionalInfo.commMedium}" required="true" maxlength="10" size="10">
			<f:validateLength minimum="1" maximum="10"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
	<x:outputLabel for="CapBank_CommStrength" value="Comm. Strength " title="" />
		<x:inputText id="CapBank_CommStrength" value="#{capBankEditor.additionalInfo.commStrengh}" required="true" maxlength="10" size="10">
			<f:validateLongRange minimum="-9999999999" maximum="9999999999"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
	<x:outputLabel for="CapBank_ExtAntenna" value="External Antenna " title="" />
	<x:selectBooleanCheckbox id="CapBank_ExtAntenna" value="#{capBankEditor.additionalInfo.extAnt}" onclick="submit()">
	</x:selectBooleanCheckbox>
 	<f:verbatim><br/></f:verbatim>
	<x:outputLabel for="CapBank_AntType" value="Antenna Type " title="" />
		<x:inputText disabled="#{!capBankEditor.additionalInfo.extAnt}" id="CapBank_AntType" value="#{capBankEditor.additionalInfo.antennaType}" required="true" maxlength="10" size="10">
			<f:validateLength minimum="1" maximum="10"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
 	
 	<f:verbatim>
             <br />
              </fieldset>
          </f:verbatim>
	</x:column>
	<x:column>
		 <f:verbatim>
	             <br />
	             <br />
	              <fieldset>
	                 <legend>
	                    Location
	                 </legend>
	                </f:verbatim>    
	<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_Latit" value="Latitude " title="" />
		<x:inputText id="CapBank_Latit" value="#{capBankEditor.additionalInfo.latit}"  required="true" maxlength="10" size="10">
			<f:validateDoubleRange minimum="-9999999999" maximum="9999999999"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_Longit" value="Longitude " title="" />
		<x:inputText id="CapBank_Longit" value="#{capBankEditor.additionalInfo.longtit}" required="true" maxlength="10" size="10">
			<f:validateDoubleRange minimum="-9999999999" maximum="9999999999"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
		 	<f:verbatim>
             <br />
              </fieldset>
          </f:verbatim>
 <f:verbatim>
    <br />
    <br />
    <fieldset>
    	<legend>
            Maintenance
        </legend>
</f:verbatim>    
		<x:outputLabel for="CapBank_LastVisit" value="Last Visit " title="" />
	     <x:inputDate id="CapBank_LastVisit" value="#{capBankEditor.additionalInfo.lastMaintVisit}"
            	type="both" popupCalendar="true" />
		<f:verbatim><br/></f:verbatim>
            	
		<x:outputLabel for="CapBank_LastInsp" value="Last Inspection " title="" />
	     <x:inputDate id="CapBank_LastInsp" value="#{capBankEditor.additionalInfo.lastInspVisit}"
            	type="both" popupCalendar="true" />
		<f:verbatim><br/></f:verbatim>

		<x:outputLabel for="CapBank_LastOpcntReset" value="Last Opcount Reset " title="" />
	     <x:inputDate id="CapBank_LastOpcntReset" value="#{capBankEditor.additionalInfo.opCountResetDate}"
            	type="both" popupCalendar="true" />
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_ReqPend" value="Request Pending " title="" />
			<x:selectBooleanCheckbox id="CapBank_ReqPend" value="#{capBankEditor.additionalInfo.reqPend}" onclick="submit()">
		</x:selectBooleanCheckbox>
	 	<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_OthrComments" value="Other Comments " title="" />
		<x:inputText id="CapBank_OthrComments" value="#{capBankEditor.additionalInfo.otherComments}" required="true" maxlength="150" size="50">
			<f:validateLength minimum="1" maximum="150"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_OpTeamComments" value="Op Team Comments " title="" />
		<x:inputText id="CapBank_OpTeamComments" value="#{capBankEditor.additionalInfo.opTeamComments}" required="true" maxlength="150" size="50">
			<f:validateLength minimum="1" maximum="150"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="CapBank_CBCInstallDate" value="CBC Install Date " title="" />
	     <x:inputDate id="CapBank_CBCInstallDate" value="#{capBankEditor.additionalInfo.cbcBattInstallDate}"
            	type="both" popupCalendar="true" />
		<f:verbatim><br/></f:verbatim>			            	

<f:verbatim>
   <br />
   	
   </fieldset>
</f:verbatim>
	</x:column>
</x:panelGrid>
</f:subview>