<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="feederSetup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >


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
				showRootNode="false" varNodeToggler="t"
				preserveToggle="true" clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
					<x:outputText id="fdrRootLink" value="#{node.description}" />
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
					<x:outputText id="paChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2"
                  		rendered="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID == node.identifier}" />
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
				showRootNode="false" varNodeToggler="t"
				preserveToggle="true" clientSideToggle="false" >
		
	        <f:facet name="root">
	        	<x:panelGroup>
					<x:outputText id="fdrwRootLink" value="#{node.description}" />
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
					<x:outputText id="wPAChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2"
                  		rendered="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID == node.identifier}" />
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
				showRootNode="false" varNodeToggler="t"
				preserveToggle="true" clientSideToggle="false" >

	        <f:facet name="root">
	        	<x:panelGroup>
					<x:outputText id="fdrvltRootLink" value="#{node.description}" />
	        	</x:panelGroup>
	        </f:facet>

	        <f:facet name="paos">
				<x:panelGroup>
					<x:outputText id="vltChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
	        	</x:panelGroup>
	        </f:facet>

			<f:facet name="points">
				<x:panelGroup>
                  	<x:graphicImage value="blue_check.gif" height="14" width="14" hspace="2"
                  		rendered="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID == node.identifier}" />
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