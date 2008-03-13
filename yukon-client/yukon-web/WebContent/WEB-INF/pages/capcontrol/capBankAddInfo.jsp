
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<jsp:directive.page import="com.cannontech.web.editor.CapControlForm"/>
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil"/>
<jsp:directive.page import="com.cannontech.database.data.pao.DBEditorTypes"/>
<%
  CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
  String itemid = JSFParamUtil.getJSFReqParam("itemid");
  String type = JSFParamUtil.getJSFReqParam("type");
  
  if (itemid != null && type != null)
    capControlForm.initItem(Integer.parseInt(itemid), Integer.parseInt(type));
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
			
		

		<x:outputLabel for="SelectConfig" value="Config " title="Select Cap Bank Configuration" />
		<x:selectOneMenu id="SelectConfig" value="#{capBankEditor.additionalInfo.capBankConfig}" onchange="submit();">
			<f:selectItems id="ConfigList" value="#{selLists.capBankConfigs}"/>
		</x:selectOneMenu>
		<f:verbatim><br/></f:verbatim>

		<x:outputLabel for="SelectPotentTrans" value="Potential Transformer " title="Select Cap Bank Potential Transformer" />
		<x:selectOneMenu id="SelectPotentTrans" value="#{capBankEditor.additionalInfo.potentTransformer}" onchange="submit();">
			<f:selectItems id="PotentTransList" value="#{selLists.potentialTransformer}"/>
		</x:selectOneMenu>
		<f:verbatim><br/></f:verbatim>

	 <f:verbatim>
	             <br />
	             <br />
	              <fieldset class="fieldSet">
	                 <legend>
	                    Communication
	                 </legend>
	                </f:verbatim>    
	<f:verbatim><br/></f:verbatim>
	<x:outputLabel for="SelectCommMed" value="Comm. Medium " title="" />
	<x:selectOneMenu id="SelectCommMed" value="#{capBankEditor.additionalInfo.commMedium}" onchange="submit();">
			<f:selectItems id="CommMedList" value="#{selLists.capBankCommMedium}"/>
		</x:selectOneMenu>
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
	<x:outputLabel for="SelectAntType" value="Antenna Type " title="" />
	<x:selectOneMenu id="SelectAntType" value="#{capBankEditor.additionalInfo.antennaType}" onchange="submit();" 
					 disabled="#{!capBankEditor.additionalInfo.extAnt}">
			<f:selectItems id="AntTypeList" value="#{selLists.capBankAntennaType}"/>
		</x:selectOneMenu>

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
	              <fieldset class="fieldSet">
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
    <fieldset class="fieldSet">
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
		
		<x:outputLabel for="CapBank_DriveDir" value="Driving Directions " title="" />
		<x:inputText id="CapBank_DriveDir" value="#{capBankEditor.additionalInfo.driveDir}" required="true" maxlength="120" size="50">
			<f:validateLength minimum="1" maximum="120"/>
		</x:inputText>
		<f:verbatim><br/></f:verbatim>			            	

<f:verbatim>
   <br />
   	
   </fieldset>
</f:verbatim>
	</x:column>
</x:panelGrid>
</f:subview>