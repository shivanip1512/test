<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.editor.point.PointForm" %>
<%@ page import="com.cannontech.web.util.*" %>


<%

PointForm ptEditorForm =
    (PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );
%>

<f:subview id="cbcCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >

<f:verbatim>

<script type="text/javascript">

addSmartScrolling('capbankHiden', 'capbankDiv', null, null);

</script>

</f:verbatim>

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
			<x:inputText id="bankSize" styleClass="char8Label" required="true"
					value="#{capControlForm.PAOBase.capBank.bankSize}" >
				<f:validateLongRange minimum="0" maximum="99999" />
			</x:inputText>
			<x:outputText id="bankSizeDesc" value="kVar"/>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="bankReclose" value="Reclose Delay: " title="The amount of time this CapBank should wait before executing the close command"/>
			<x:selectOneMenu id="bankReclose"
					value="#{capControlForm.PAOBase.capBank.recloseDelay}" >
				<f:selectItem itemLabel="(none)" itemValue="0"/>
				<f:selectItems value="#{capControlForm.timeInterval}"/>
			</x:selectOneMenu>


			<f:verbatim><br/><br/></f:verbatim>
	    	<x:div id="capbankDiv" forceId="true" styleClass="scrollSmall" 
				rendered="#{capControlForm.bankControlPtVisible}" >

			<x:outputLabel for="cntrlPoint" value="Control Point: " title="Point used for monitoring the control (Only displays points that are not yet used by CapBanks)" styleClass="medStaticLabel"/>
	        <x:outputText id="cntrlPoint" rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}"
	        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}
	        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].pointName}" styleClass="medLabel"/>
	        <x:outputText id="cntrlPoint_none" rendered="#{capControlForm.PAOBase.capBank.controlPointID == 0}"
	        	value="(none)" styleClass="medLabel"/>


			<x:tree2 id="capBankPoints" value="#{capControlForm.capBankPoints}" var="node"
					showRootNode="false" varNodeToggler="t"
					preserveToggle="true" clientSideToggle="false" >
		        <f:facet name="root">
		        	<x:panelGroup>
			            <h:commandLink id="paoRoot" action="#{t.toggleExpanded}" value="#{node.description}"/>
		        	</x:panelGroup>
		        </f:facet>
	
		        <f:facet name="paoTypes">
					<x:panelGroup>
						<x:outputText id="paoTypeNodeCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
		        	</x:panelGroup>
		        </f:facet>

		        <f:facet name="paos">
					<x:panelGroup>
						<x:outputText id="paoNodeCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
		        	</x:panelGroup>
		        </f:facet>
	
				<f:facet name="points">
					<x:panelGroup>
	                  	<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2"
                  			rendered="#{capControlForm.PAOBase.capBank.controlPointID == node.identifier}" />
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

		    <f:verbatim><br/><fieldset><legend>CapBank Operations</legend></f:verbatim>
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="maxDailyOps" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
			<x:inputText id="maxDailyOps" styleClass="char16Label" required="true"
					value="#{capControlForm.PAOBase.capBank.maxDailyOps}" >
					<f:validateLongRange minimum="0" maximum="9999" />
			</x:inputText>
			<x:outputText id="maxDailyOpsDesc" value="(0 = unlimited)"/>
	
			<f:verbatim><br/></f:verbatim>
			<h:selectBooleanCheckbox id="disabledOps"
					value="#{capControlForm.PAOBase.capBank.maxOperationDisabled}" />
			<x:outputLabel for="disabledOps" value="Disable upon reaching max operations" title="Should we be automatically disabled after reaching our max op counts"/>
			<f:verbatim></fieldset></f:verbatim>

		</h:column>

        <h:column>
        <f:verbatim><br/><fieldset><legend>Control Point Editors</legend><br></f:verbatim>
            <x:outputText value="CBC Controller: " title="Click on the link to edit the CBC"/>
            <x:commandLink id="CBCEditor" value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}"
                           actionListener="#{ptEditorForm.paoClick}" title="Click on the link to edit the CBC">
            <f:param name="paoID" value="#{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID}"/>
          </x:commandLink>    
          <f:verbatim>
          <br> <br>
          </f:verbatim>
          <x:outputText value="Point : " title="Click on the link to edit the Point"/>
          <x:commandLink id="PointEditor" value="#{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].pointName}"
                           actionListener="#{capControlForm.CBControllerEditor.pointClick}" title="Click on the link to edit the Point">
            <f:param name="ptID" value="#{capControlForm.PAOBase.capBank.controlPointID}"/>
          </x:commandLink>    
        <f:verbatim></fieldset></f:verbatim>
		</h:column>
	

	
	</h:panelGrid>
		
    <x:inputHidden id="capbankHiden" forceId="true" value="#{capControlForm.offsetMap['capbankHiden']}"/>


</f:subview>