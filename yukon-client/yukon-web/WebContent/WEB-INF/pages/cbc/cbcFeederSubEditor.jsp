<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>


<f:subview id="feederSub" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}" >

    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}" >
    <h:panelGrid id="subBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
    
		<h:column>
	    <f:verbatim><br/><br/><fieldset><legend>SubBus Info</legend></f:verbatim>    
		<x:outputLabel for="subAreaName" value="#{capControlForm.PAODescLabel}: " title="Physical location of the Substation Bus"
						rendered="#{!empty capControlForm.PAODescLabel}" />
		<x:inputText id="subAreaName" value="#{capControlForm.PAOBase.geoAreaName}" required="true" maxlength="60"
						rendered="#{!empty capControlForm.PAODescLabel}" />

		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="subMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
		<x:inputText id="subMapLocID" value="#{capControlForm.PAOBase.capControlSubstationBus.mapLocationID}" required="true" maxlength="64" />
		<f:verbatim></fieldset></f:verbatim>


	    <f:verbatim><br/><br/><fieldset><legend>VAR Point Setup</legend></f:verbatim>
		<x:outputLabel for="subVarPoint" value="Selected Point: " title="Data point used for the current kVAR value" styleClass="medStaticLabel"/>
        <x:outputText id="subVarPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID != 0}"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="subVarPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>


    	<x:div styleClass="scrollSmall">
		<x:tree2 id="subVarPaoListTree" value="#{capControlForm.varTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" imageLocation="/editor/images/myfaces"
			clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="rootLink" action="#{t.toggleExpanded}" value="#{node.description}" immediate="true" />
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="paoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="paChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="/editor/images/blue_check.gif" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="ptLink" value="#{node.description}"
						actionListener="#{capControlForm.varPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
		
        <x:commandLink id="subVarPoint_setNone" title="Do not use a point for the VAR value"
        			value="No VAR Point" actionListener="#{capControlForm.varPtTeeClick}" styleClass="medStaticLabel" >
            <f:param name="ptID" value="0"/>
        </x:commandLink>

		<f:verbatim></fieldset></f:verbatim>
		</h:column>
		
		<h:column>
	    <f:verbatim><br/><br/><fieldset><legend>Watt Point Setup</legend></f:verbatim>
		<x:outputLabel for="subWattPoint" value="Selected Point: " title="Data point used for the current WATT value" styleClass="medStaticLabel"/>
        <x:outputText id="subWattPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID != 0}" styleClass="medStaticLabel"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="subWattPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>

    	<x:div styleClass="scrollSmall">
		<x:tree2 id="subWattListTree" value="#{capControlForm.wattTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" imageLocation="/editor/images/myfaces/"
			showLines="true" showNav="true" clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="wRootLink" action="#{t.toggleExpanded}" value="#{node.description}"/>
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="wPaoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="wPAChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="/editor/images/blue_check.gif" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="wPtLink" value="#{node.description}"
						actionListener="#{capControlForm.wattPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
        <x:commandLink id="subWattPoint_setNone" title="Do not use a point for the Watt value" styleClass="medStaticLabel"
        			value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
            <f:param name="ptID" value="0"/>
        </x:commandLink>
		
		<f:verbatim></fieldset></f:verbatim>


	    <f:verbatim><br/><br/><fieldset><legend>Volt Point Setup</legend></f:verbatim>
		<x:outputLabel for="subVoltPoint" value="Selected Point: " title="Data point used for the current Volt value" styleClass="medStaticLabel"/>
        <x:outputText id="subVoltPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID != 0}" styleClass="medStaticLabel"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="subVoltPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>

    	<x:div styleClass="scrollSmall">
		<x:tree2 id="subVoltPaoListTree" value="#{capControlForm.voltTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" showLines="true" imageLocation="/editor/images/myfaces/"
			showNav="true" clientSideToggle="false" >

	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="vltRootLink" action="#{t.toggleExpanded}" value="#{node.description}"/>
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="vltPaoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="vltChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="/editor/images/blue_check.gif" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="ptLink" value="#{node.description}"
						actionListener="#{capControlForm.voltPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
        <x:commandLink id="subVoltPoint_setNone" title="Do not use a point for the Volt value" styleClass="medStaticLabel"
        			value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}" >
            <f:param name="ptID" value="0"/>
        </x:commandLink>
		
		<f:verbatim></fieldset></f:verbatim>

		</h:column>
		
    </h:panelGrid>

    </f:subview>



    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >    
    <h:panelGrid id="fdrBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
    
		<h:column>
	    <f:verbatim><br/><br/><fieldset><legend>Feeder Info</legend></f:verbatim>    
		<x:outputLabel for="fdrMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
		<x:inputText id="fdrMapLocID" value="#{capControlForm.PAOBase.capControlFeeder.mapLocationID}" required="true" maxlength="64" />
		<f:verbatim></fieldset></f:verbatim>


	    <f:verbatim><br/><br/><fieldset><legend>VAR Point Setup</legend></f:verbatim>
		<x:outputLabel for="varPoint" value="Selected Point: " title="Data point used for the current kVAR value" styleClass="medStaticLabel" />
        <x:outputText id="varPoint" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID != 0}"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="varPoint_none" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>


    	<x:div styleClass="scrollSmall">
		<x:tree2 id="varPaoListTree" value="#{capControlForm.varTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" imageLocation="/editor/images/myfaces/"
			showLines="true" showNav="true" clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="fdrRootLink" action="#{t.toggleExpanded}" value="#{node.description}"/>
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="fdrPaoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="paChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="/editor/images/blue_check.gif" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="ptLink" value="#{node.description}"
						actionListener="#{capControlForm.varPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
		
        <x:commandLink id="varPoint_setNone" title="Do not use a point for the VAR value"
        			value="No VAR Point" actionListener="#{capControlForm.varPtTeeClick}" styleClass="medStaticLabel" >
            <f:param name="ptID" value="0"/>
        </x:commandLink>

		<f:verbatim></fieldset></f:verbatim>
		</h:column>
		
		<h:column>
	    <f:verbatim><br/><br/><fieldset><legend>Watt Point Setup</legend></f:verbatim>
		<x:outputLabel for="wattPoint" value="Selected Point: " title="Data point used for the current WATT value" styleClass="medStaticLabel"/>
        <x:outputText id="wattPoint" rendered="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID != 0}" styleClass="medStaticLabel"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="wattPoint_none" rendered="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>

    	<x:div styleClass="scrollSmall">
		<x:tree2 id="paoWattListTree" value="#{capControlForm.wattTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" imageLocation="/editor/images/myfaces/"
			showLines="true" showNav="true" clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="fdrwRootLink" action="#{t.toggleExpanded}" value="#{node.description}"/>
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="fdrwPaoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="wPAChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="/editor/images/blue_check.gif" rendered="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="wPtLink" value="#{node.description}"
						actionListener="#{capControlForm.wattPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
        <x:commandLink id="wattPoint_setNone" title="Do not use a point for the Watt value" styleClass="medStaticLabel"
        			value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
            <f:param name="ptID" value="0"/>
        </x:commandLink>
		
		<f:verbatim></fieldset></f:verbatim>


	    <f:verbatim><br/><br/><fieldset><legend>Volt Point Setup</legend></f:verbatim>
		<x:outputLabel for="voltPoint" value="Selected Point: " title="Data point used for the current Volt value" styleClass="medStaticLabel"/>
        <x:outputText id="voltPoint" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID != 0}" styleClass="medStaticLabel"
        	value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID].pointName}" styleClass="medLabel"/>
        <x:outputText id="voltPoint_none" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID == 0}"
        	value="(none)" styleClass="medLabel"/>

    	<x:div styleClass="scrollSmall">
		<x:tree2 id="voltPaoListTree" value="#{capControlForm.voltTreeData}" var="node"
			showRootNode="true" varNodeToggler="t" imageLocation="/editor/images/myfaces/"
			showLines="true" showNav="true" clientSideToggle="false" >

	        <f:facet name="root">
	        	<x:panelGroup>
		            <h:commandLink id="fdrvltRootLink" action="#{t.toggleExpanded}" value="#{node.description}"/>
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
		            <h:commandLink id="fdrvltPaoLinkNodes" action="#{t.toggleExpanded}" value="#{node.description}"/>
					<x:outputText id="vltChCnt" value=" (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<h:graphicImage value="blue_check.gif" rendered="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID == node.identifier}" border="0"/>
		            <x:commandLink id="ptLink" value="#{node.description}"
						actionListener="#{capControlForm.voltPtTeeClick}">
			            <f:param name="ptID" value="#{node.identifier}"/>
		            </x:commandLink>	            

				</x:panelGroup>
	        </f:facet>

		</x:tree2>
		</x:div>
        <x:commandLink id="voltPoint_setNone" title="Do not use a point for the Volt value" styleClass="medStaticLabel"
        			value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}" >
            <f:param name="ptID" value="0"/>
        </x:commandLink>
		
		<f:verbatim></fieldset></f:verbatim>

		</h:column>
		
    </h:panelGrid>

		
    </f:subview>
    


</f:subview>