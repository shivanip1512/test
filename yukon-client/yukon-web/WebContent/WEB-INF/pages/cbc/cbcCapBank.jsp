<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>

<f:subview id="cbcCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >

	<h:panelGrid id="capbankBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

		<h:column>
		    <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankOperation" value="Operation Method: " title="How this CapBank should operate in the field"/>
			<x:selectOneMenu id="bankOperation" onchange="submit();"
					value="#{capControlForm.PAOBase.capBank.operationalState}" >
				<f:selectItems value="#{selLists.capBankOpStates}"/>
			</x:selectOneMenu>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankSize" value="Bank Size: " title="The total size of the CapBank"/>
			<x:selectOneMenu id="bankSize"
					value="#{capControlForm.PAOBase.capBank.bankSize}" >
				<f:selectItems value="#{selLists.capBankSizes}"/>
			</x:selectOneMenu>


			<f:verbatim><br/><br/></f:verbatim>
	    	<x:div styleClass="scrollSmall" 
				rendered="#{capControlForm.bankControlPtVisible}" >

			<x:outputLabel for="cntrlPoint" value="Control Point: " title="Point used for monitoring the control (Only displays points that are not yet used by CapBanks)" styleClass="medStaticLabel"/>
	        <x:outputText id="cntrlPoint" rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}"
	        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}
	        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].pointName}" styleClass="medLabel"/>
	        <x:outputText id="cntrlPoint_none" rendered="#{capControlForm.PAOBase.capBank.controlPointID == 0}"
	        	value="(none)" styleClass="medLabel"/>


			<x:tree2 id="capBankPoints" value="#{capControlForm.capBankPoints}" var="node"
				showRootNode="false" varNodeToggler="t" imageLocation="/editor/images/myfaces"
				showLines="true" showNav="true" clientSideToggle="true" >
			
		        <f:facet name="root">
		        	<x:panelGroup>
			            <h:commandLink id="paoRoot" action="#{t.toggleExpanded}" value="#{node.description}"/>
		        	</x:panelGroup>
		        </f:facet>
	
		        <f:facet name="paoTypes">
					<x:panelGroup>
			            <h:commandLink id="paoTypeNode" action="#{t.toggleExpanded}" value="#{node.description}"/>
						<x:outputText id="paoTypeNodeCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
		        	</x:panelGroup>
		        </f:facet>

		        <f:facet name="paos">
					<x:panelGroup>
			            <h:commandLink id="paoNode" action="#{t.toggleExpanded}" value="#{node.description}"/>
						<x:outputText id="paoNodeCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
		        	</x:panelGroup>
		        </f:facet>
	
				<f:facet name="points">
					<x:panelGroup>
	                  	<h:graphicImage value="/editor/images/AlarmFlag.gif" rendered="#{capControlForm.PAOBase.capBank.controlPointID == node.identifier}" border="0"/>
			            <x:commandLink id="pointNode" value="#{node.description}"
							actionListener="#{capControlForm.capBankTeeClick}">
				            <f:param name="ptID" value="#{node.identifier}"/>
			            </x:commandLink>	            
	
					</x:panelGroup>
		        </f:facet>
	
			</x:tree2>
			</x:div>
	        <x:commandLink id="capBankPoint_setNone" title="Do not use a control point" styleClass="medStaticLabel"
	        			rendered="#{capControlForm.bankControlPtVisible}"
	        			value="No Control Point" actionListener="#{capControlForm.capBankTeeClick}">
	            <f:param name="ptID" value="0"/>
	        </x:commandLink>


			<f:verbatim></fieldset></f:verbatim>

		</h:column>






		<h:column>
		    <f:verbatim><br/><fieldset><legend>Controller Information</legend></f:verbatim>

			<x:outputText id="warnNoCBC" styleClass="alert" rendered="#{!capControlForm.controllerCBC}"
					value="No CBC information avaible since the controller is not a CBC" />

			<f:verbatim><br/></f:verbatim>
			<h:selectBooleanCheckbox id="editCntrl" value="#{capControlForm.editingController}"
					rendered="#{capControlForm.controllerCBC}" onclick="submit();"
					valueChangeListener="#{capControlForm.editController}" />
			<x:outputLabel for="editCntrl" value="Edit Controller" title="Allows editing of the controller this CapBank currently uses"
					rendered="#{capControlForm.controllerCBC}" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="cntrlDev" value="Controller Device: " title="Controller device selected from the point list"
					rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}" />
			<x:outputText id="cntrlDev" styleClass="staticLabel"
					rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}"
					value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="cntrlPt" value="Controller Point: " title="Name of the controller point selected from the point list"
					rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}" />
			<x:outputText id="cntrlPt" styleClass="staticLabel"
					rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}"
					value="#{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].pointName}" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="cntrlSerNum" value="Serial Number: " title="Serial number of the controller device" rendered="#{capControlForm.controllerCBC}" />
			<x:inputText id="cntrlSerNumEd" styleClass="staticLabel"
					rendered="#{capControlForm.controllerCBC}" disabled="#{!capControlForm.editingController}"
					value="#{capControlForm.cbcDevicesMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].serialNumber}" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" rendered="#{capControlForm.controllerCBC}" />
			<x:selectOneMenu id="cntrlRoute" rendered="#{capControlForm.controllerCBC}"
					value="#{capControlForm.cbcDevicesMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].routeID}"
					disabled="#{!capControlForm.editingController}" >
				<f:selectItem itemLabel="(none)" itemValue="0"/>
				<f:selectItems value="#{selLists.routes}"/>
			</x:selectOneMenu>


			<f:verbatim></fieldset></f:verbatim>



		    <f:verbatim><br/><fieldset><legend>Information</legend></f:verbatim>
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="capMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
			<x:inputText id="capMapLocID" value="#{capControlForm.PAOBase.capBank.mapLocationID}" required="true" maxlength="64" />

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankMan" value="Switch Manufacture: " title="What company manufactured this item"/>
			<x:selectOneMenu id="bankMan" value="#{capControlForm.PAOBase.capBank.switchManufacture}" >
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
			<x:selectOneMenu id="cntrlerType" value="#{capControlForm.PAOBase.capBank.controllerType}" >
				<f:selectItem itemValue="(none)"/>
				<f:selectItem itemValue="CTI DLC"/>
				<f:selectItem itemValue="CTI Paging"/>
				<f:selectItem itemValue="CTI FM"/>
				<f:selectItem itemValue="FP Paging"/>
				<f:selectItem itemValue="Telemetric"/>
			</x:selectOneMenu>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankType" value="Type of Switch: " title="What type of switch this item is"/>
			<x:selectOneMenu id="bankType" value="#{capControlForm.PAOBase.capBank.typeOfSwitch}" >
				<f:selectItem itemValue="(none)"/>
				<f:selectItem itemValue="Oil"/>
				<f:selectItem itemValue="Vacuum"/>
			</x:selectOneMenu>

			<f:verbatim></fieldset></f:verbatim>

		</h:column>
	

	
	</h:panelGrid>
		
    


</f:subview>