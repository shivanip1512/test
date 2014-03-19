<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="x" uri="http://myfaces.apache.org/tomahawk" %>

<jsp:directive.page import="com.cannontech.web.editor.CapControlForm" />
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil" />

<f:subview id="cbcCapBankInfo" rendered="#{capControlForm.visibleTabs['CBAddInfo']}">
	<x:panelGrid id="capbankBody" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell">
		<x:column>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Configuration"/></x:htmlTag>
                
                <x:panelGrid columns="2">
                
    				<x:outputLabel for="CapBank_MaintAreaID" value="Maintenance Area Id " title="" />
    				<x:inputText id="CapBank_MaintAreaID" value="#{capBankEditor.additionalInfo.maintAreaID}" required="true" maxlength="10" size="10">
    					<f:validateLongRange minimum="-9999999999" maximum="9999999999" />
    				</x:inputText>
    	
    				<x:outputLabel for="CapBank_PoleNum" value="Pole Number " title="" />
    				<x:inputText id="CapBank_PoleNum"
    					value="#{capBankEditor.additionalInfo.poleNumber}" required="true"
    					maxlength="10" size="10">
    					<f:validateLongRange minimum="-9999999999" maximum="9999999999" />
    				</x:inputText>
    	
    				<x:outputLabel for="SelectConfig" value="Config " title="Select Cap Bank Configuration" />
    				<x:selectOneMenu id="SelectConfig" value="#{capBankEditor.additionalInfo.capBankConfig}" onchange="submit();"
    				    disabled="#{!capControlForm.editingAuthorized}">
    					<f:selectItems id="ConfigList" value="#{selLists.capBankConfigs}" />
    				</x:selectOneMenu>
    				
    				<x:outputLabel for="SelectPotentTrans" value="Potential Transformer " title="Select Cap Bank Potential Transformer" />
    				<x:selectOneMenu id="SelectPotentTrans" value="#{capBankEditor.additionalInfo.potentTransformer}" onchange="submit();"
    				    disabled="#{!capControlForm.editingAuthorized}">
    					<f:selectItems id="PotentTransList" value="#{selLists.potentialTransformer}" />
    				</x:selectOneMenu>
                </x:panelGrid>
            </x:htmlTag>
			
			<x:htmlTag value="br"/>

            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Communication"/></x:htmlTag>
                
                <x:panelGrid columns="2">
                    <x:outputLabel for="SelectCommMed" value="Comm. Medium " title="" />
					<x:panelGroup>
						<x:panelGrid columns="3">
						<x:selectOneMenu id="SelectCommMed"
							disabled="#{!capControlForm.editingAuthorized}"
							value="#{capBankEditor.additionalInfo.commMedium}"
							onchange="submit();" rendered="#{!capBankEditor.customCommMedium}">
						<f:selectItems id="CommMedList" value="#{selLists.capBankCommMedium}" />
						</x:selectOneMenu>
						
						<x:inputText id="bankMedium" styleClass="char8Label" required="true"
							value="#{capBankEditor.additionalInfo.commMedium}"
							rendered="#{capBankEditor.customCommMedium}">
						</x:inputText>
						<x:outputLabel for="isCustomCommMedium" value="Custom" title="The Comm Medium" />
						<x:selectBooleanCheckbox id="isCustomCommMedium"
						    disabled="#{!capControlForm.editingAuthorized}"
							value="#{capBankEditor.customCommMedium}" onclick="submit()" />
						</x:panelGrid>
					</x:panelGroup> 
										
					<x:outputLabel for="CapBank_CommStrength" value="Comm. Strength "
						title="" />
					<x:inputText id="CapBank_CommStrength" value="#{capBankEditor.additionalInfo.commStrengh}" required="true" maxlength="10" size="10">
						<f:validateLongRange minimum="-9999999999" maximum="9999999999" />
					</x:inputText>
					
					<x:outputLabel for="CapBank_ExtAntenna" value="External Antenna " title="" />
					<x:selectBooleanCheckbox id="CapBank_ExtAntenna" value="#{capBankEditor.additionalInfo.extAnt}" onclick="submit()"
                        disabled="#{!capControlForm.editingAuthorized}"/>
					
					<x:outputLabel for="SelectAntType" value="Antenna Type " title="" />
					<x:selectOneMenu id="SelectAntType"
						value="#{capBankEditor.additionalInfo.antennaType}"
						onchange="submit();"
						disabled="#{!capBankEditor.additionalInfo.extAnt || !capControlForm.editingAuthorized}">
						<f:selectItems id="AntTypeList" value="#{selLists.capBankAntennaType}" />
					</x:selectOneMenu>
	
				</x:panelGrid>

			</x:htmlTag>
		</x:column>
		
		<x:column>
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Location"/></x:htmlTag>
                
                <x:panelGrid columns="2">
                
					<x:outputLabel for="CapBank_Latit" value="Latitude " title="" />
					<x:inputText id="CapBank_Latit"
						value="#{capBankEditor.additionalInfo.latit}" required="true"
						maxlength="10" size="10">
						<f:validateDoubleRange minimum="-9999999999" maximum="9999999999" />
					</x:inputText>
					
					<x:outputLabel for="CapBank_Longit" value="Longitude " title="" />
					<x:inputText id="CapBank_Longit"
						value="#{capBankEditor.additionalInfo.longtit}" required="true"
						maxlength="10" size="10">
						<f:validateDoubleRange minimum="-9999999999" maximum="9999999999" />
					</x:inputText>
				
				</x:panelGrid>
			
			</x:htmlTag>
			
			<x:htmlTag value="br"/>
			
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Maintenance"/></x:htmlTag>
                
                <x:panelGrid columns="2">
                
					<x:outputLabel for="CapBank_LastVisit" value="Last Visit " title="" />
					<x:inputDate id="CapBank_LastVisit" value="#{capBankEditor.additionalInfo.lastMaintVisit}" type="both" popupCalendar="true" />
					
					<x:outputLabel for="CapBank_LastInsp" value="Last Inspection " title="" />
					<x:inputDate id="CapBank_LastInsp" value="#{capBankEditor.additionalInfo.lastInspVisit}" type="both" popupCalendar="true" />
					
					<x:outputLabel for="CapBank_LastOpcntReset" value="Last Opcount Reset " title="" />
					<x:inputDate id="CapBank_LastOpcntReset" value="#{capBankEditor.additionalInfo.opCountResetDate}" type="both" popupCalendar="true" />
					
					<x:outputLabel for="CapBank_ReqPend" value="Request Pending " title="" />
					<x:selectBooleanCheckbox id="CapBank_ReqPend" value="#{capBankEditor.additionalInfo.reqPend}" onclick="submit()"
                        disabled="#{!capControlForm.editingAuthorized}"/>
					
					<x:outputLabel for="CapBank_OthrComments" value="Other Comments " title="" />
					<x:inputText id="CapBank_OthrComments" value="#{capBankEditor.additionalInfo.otherComments}" required="false" maxlength="150" size="50">
						<f:validateLength minimum="1" maximum="150" />
					</x:inputText>
					
					<x:outputLabel for="CapBank_OpTeamComments" value="Op Team Comments " title="" />
					<x:inputText id="CapBank_OpTeamComments" value="#{capBankEditor.additionalInfo.opTeamComments}" required="false" maxlength="150" size="50">
						<f:validateLength minimum="1" maximum="150" />
					</x:inputText>
					
					<x:outputLabel for="CapBank_CBCInstallDate" value="CBC Install Date " title="" />
					<x:inputDate id="CapBank_CBCInstallDate" value="#{capBankEditor.additionalInfo.cbcBattInstallDate}" type="both" popupCalendar="true" />
					
					<x:outputLabel for="CapBank_DriveDir" value="Driving Directions " title="" />
					<x:inputText id="CapBank_DriveDir" value="#{capBankEditor.additionalInfo.driveDir}" required="false" maxlength="120" size="50">
						<f:validateLength minimum="1" maximum="120" />
					</x:inputText>
				
				</x:panelGrid>

			</x:htmlTag>
		</x:column>
	</x:panelGrid>
</f:subview>