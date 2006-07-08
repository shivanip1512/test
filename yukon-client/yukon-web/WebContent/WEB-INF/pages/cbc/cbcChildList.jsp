<%@ page pageEncoding="UTF-8" import="java.util.*"%>
<%@ page import="org.ajaxanywhere.*"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net" prefix="aa" %>
<%

    if (AAUtils.isAjaxRequest(request)){
        AAUtils.addZonesToRefresh(request, "feeders");
    }
%>
<f:verbatim>
<script type="text/JavaScript" src="../../../JavaScript/aa.js"></script>
<script>
	ajaxAnywhere.getZonesToReload = function(url, submitButton) {
		
		if ( $("aazone.feeders") )
			return "feeders";

	}
</script>
</f:verbatim>	
<f:subview id="childList" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}" >
	<aa:zoneJSF id="feeders">
    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}" >
	    <f:verbatim><br/><br/><fieldset><legend>Feeder Assignment</legend></f:verbatim>
	    <f:verbatim><br/></f:verbatim>

	    <h:panelGrid id="subBody" columns="2" styleClass="gridLayout"
	    		rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
	            <h:dataTable id="fdrAvailData" var="feeder" 
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capControlForm.unassignedFeeders}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{feeder.liteID}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Available Feeders" />
	                   </f:facet>
	                   <x:outputText value="#{feeder.paoName}" title="#{feeder.paoDescription}"/>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="Add >>" action="#{capControlForm.treeSwapAddAction}">
							<f:param name="swapType" value="Feeder"/>
							<f:param name="id" value="#{feeder.liteID}"/>
	                   </x:commandLink>
	               </h:column>
	
	            </h:dataTable>
	
	            <h:panelGrid columns="1" columnClasses="scrollerCentered">
	                <x:dataScroller id="scrollButtons" for="fdrAvailData" fastStep="25"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex"
	                        styleClass="scroller" paginator="true"
	                        paginatorMaxPages="9" paginatorTableClass="paginator"
	                        paginatorActiveColumnStyle="font-weight:bold;">
	                    <f:facet name="first" >
	                        <x:graphicImage url="/editor/images/arrow-first.gif" border="1" title="First page"/>
	                    </f:facet>
	                    <f:facet name="last">
	                        <x:graphicImage url="/editor/images/arrow-last.gif" border="1" title="Last page"/>
	                    </f:facet>
	                    <f:facet name="previous">
	                        <x:graphicImage url="/editor/images/arrow-previous.gif" border="1" title="Previous page"/>
	                    </f:facet>
	                    <f:facet name="next">
	                        <x:graphicImage url="/editor/images/arrow-next.gif" border="1" title="Next page"/>
	                    </f:facet>
	                    <f:facet name="fastforward">
	                        <x:graphicImage url="/editor/images/arrow-ff.gif" border="1" title="Next set of pages"/>
	                    </f:facet>
	                    <f:facet name="fastrewind">
	                        <x:graphicImage url="/editor/images/arrow-fr.gif" border="1" title="Previous set of pages"/>
	                    </f:facet>
	                </x:dataScroller>
	
	                <x:dataScroller id="scrollDisplay" for="fdrAvailData" rowsCountVar="rowsCount"
	                		styleClass="scroller"
	                        displayedRowsCountVar="displayedRowsCountVar"
	                        firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex">
	                    <h:outputFormat value="{0} Feeders found" >
	                        <f:param value="#{rowsCount}" />
	                    </h:outputFormat>
						<f:verbatim><br/></f:verbatim>
	                    <h:outputFormat value="Page {0} / {1}" >
	                        <f:param value="#{pageIndex}" />
	                        <f:param value="#{pageCount}" />
	                    </h:outputFormat>
	                </x:dataScroller>
	            </h:panelGrid>
			</x:panelGroup>

			<x:panelGroup>
	            <h:dataTable id="feederAssignData" var="feeder"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	                    rendered="#{capControlForm.visibleTabs['CBCSubstation']}"	                    
	            		value="#{capControlForm.PAOBase.childList}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="<< Remove" action="#{capControlForm.treeSwapRemoveAction}">
							<f:param name="swapType" value="Feeder"/>
							<f:param name="id" value="#{feeder.feederID}"/>
	                   </x:commandLink>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Assigned Feeders"/>
	                   </f:facet>
	                   <x:outputText value="#{dbCache.allPAOsMap[feeder.feederID]}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
							<x:outputText value="Order" title="Order used for control (Range: 1 to 1000 )" />
	                   </f:facet>
	                   <x:inputText value="#{feeder.displayOrder}" styleClass="char4Label" required="true" >
	                   		<f:validateLongRange minimum="1" maximum="1000" />
	                   </x:inputText>
	                   
	               </h:column>
	
	            </h:dataTable>
			</x:panelGroup>
	
	    </h:panelGrid>
	
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>




    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
	    <f:verbatim><br/><br/><fieldset><legend>CapBank Assignment</legend></f:verbatim>
	    <f:verbatim><br/></f:verbatim>
	    <h:panelGrid id="fdrBody" columns="2" styleClass="gridLayout"
	    		rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
	            <h:dataTable id="bankAvailData" var="capBank"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capControlForm.unassignedBanks}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{capBank.liteID}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Available CapBanks" />
	                   </f:facet>
	                   <x:outputText value="#{capBank.paoName}" title="#{capBank.paoDescription}"/>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="Add >>" action="#{capControlForm.treeSwapAddAction}">
							<f:param name="swapType" value="CapBank"/>
							<f:param name="id" value="#{capBank.liteID}"/>
	                   </x:commandLink>
	               </h:column>
	
	            </h:dataTable>
	
	            <h:panelGrid columns="1" columnClasses="scrollerCentered">
	                <x:dataScroller id="scrollButtons" for="bankAvailData" fastStep="25"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex"
	                        styleClass="scroller" paginator="true"
	                        paginatorMaxPages="9" paginatorTableClass="paginator"
	                        paginatorActiveColumnStyle="font-weight:bold;">
	                    <f:facet name="first" >
	                        <x:graphicImage url="/editor/images/arrow-first.gif" border="1" title="First page"/>
	                    </f:facet>
	                    <f:facet name="last">
	                        <x:graphicImage url="/editor/images/arrow-last.gif" border="1" title="Last page"/>
	                    </f:facet>
	                    <f:facet name="previous">
	                        <x:graphicImage url="/editor/images/arrow-previous.gif" border="1" title="Previous page"/>
	                    </f:facet>
	                    <f:facet name="next">
	                        <x:graphicImage url="/editor/images/arrow-next.gif" border="1" title="Next page"/>
	                    </f:facet>
	                    <f:facet name="fastforward">
	                        <x:graphicImage url="/editor/images/arrow-ff.gif" border="1" title="Next set of pages"/>
	                    </f:facet>
	                    <f:facet name="fastrewind">
	                        <x:graphicImage url="/editor/images/arrow-fr.gif" border="1" title="Previous set of pages"/>
	                    </f:facet>
	                </x:dataScroller>
	
	                <x:dataScroller id="scrollDisplay" for="bankAvailData" rowsCountVar="rowsCount"
	                		styleClass="scroller"
	                        displayedRowsCountVar="displayedRowsCountVar"
	                        firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex">
	                    <h:outputFormat value="{0} CapBanks found" >
	                        <f:param value="#{rowsCount}" />
	                    </h:outputFormat>
						<f:verbatim><br/></f:verbatim>
	                    <h:outputFormat value="Page {0} / {1}" >
	                        <f:param value="#{pageIndex}" />
	                        <f:param value="#{pageCount}" />
	                    </h:outputFormat>
	                </x:dataScroller>
	            </h:panelGrid>
			</x:panelGroup>
			
			<x:panelGroup>
	            <h:dataTable id="bankAssignData" var="capBank"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	                    rendered="#{capControlForm.visibleTabs['CBCFeeder']}"	                    
	            		value="#{capControlForm.PAOBase.childList}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="<< Remove" action="#{capControlForm.treeSwapRemoveAction}">
							<f:param name="swapType" value="CapBank"/>
							<f:param name="id" value="#{capBank.deviceID}"/>
	                   </x:commandLink>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Assigned CapBanks"/>
	                   </f:facet>
	                   <x:outputText value="#{dbCache.allPAOsMap[capBank.deviceID]}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
							<x:outputText value="Order" title="Order used for control (Range: 1 to 1000 )" />
	                   </f:facet>
	                   <x:inputText value="#{capBank.controlOrder}" styleClass="char4Label" required="true" >
	                   		<f:validateLongRange minimum="1" maximum="1000" />
	                   </x:inputText>
	                   
	               </h:column>
	
	            </h:dataTable>
			</x:panelGroup>
	
	    </h:panelGrid>
	
		<f:verbatim></fieldset></f:verbatim>		
    </f:subview>
    

</aa:zoneJSF>
</f:subview>