<%@ page pageEncoding="UTF-8" import="java.util.*"%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<%@ page import="com.cannontech.web.editor.CapBankEditorForm" %>
<%@ page import="com.cannontech.web.util.JSFParamUtil" %>

<%
CapControlForm capControlForm =  (CapControlForm)JSFParamUtil.getJSFVar("capControlForm");
CapBankEditorForm editor =  (CapBankEditorForm)JSFParamUtil.getJSFVar("capBankEditor");
editor.init(capControlForm.getPAOBase());
%>


<f:verbatim>
<script type="text/javascript" >
function checkAdaptiveCount(el) {
if (el.value <= 0) {
 var message = "Adaptive Count value must  be greater then 0";
 alert ( message );
 }
}
</script>
</f:verbatim>
 <f:subview id="paoCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >

	    <f:verbatim><br/><br/><fieldset class="fieldSet"><legend>Point Assignment</legend></f:verbatim>
	    <f:verbatim><br/></f:verbatim>
	    <h:panelGrid id="cbBody" columns="2" styleClass="gridLayout"
	    		rowClasses="gridCell" columnClasses="gridCell" >
			<x:panelGroup>
	            <h:dataTable id="pointAvailData" var="point"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capBankEditor.unassignedPoints}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{point.pointId}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Available Points" />
	                   </f:facet>
	                   <x:outputText value="#{point.pointName}"/>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="Add >>" action="#{capBankEditor.treeSwapAddAction}" 
	                   								 rendered="#{not empty capBankEditor.unassignedPoints}">
							<f:param name="swapType" value="CapBankPoint"/>
							<f:param name="id" value="#{point.pointId}"/>
	                   </x:commandLink>
	               </h:column>
	
	            </h:dataTable>
	
	            <h:panelGrid columns="1" columnClasses="scrollerCentered">
	                <x:dataScroller id="scrollButtons" for="pointAvailData" fastStep="25"
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
	
	                <x:dataScroller id="scrollDisplay" for="pointAvailData" rowsCountVar="rowsCount"
	                		styleClass="scroller"
	                        displayedRowsCountVar="displayedRowsCountVar"
	                        firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex">
	                    <h:outputFormat value="{0} points found" >
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
	            <h:dataTable id="pointAssignData" var="point"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	                    rendered="#{capControlForm.visibleTabs['CapBank']}"	                    
	            		value="#{capBankEditor.assignedPoints}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="<< Remove" action="#{capBankEditor.treeSwapRemoveAction}" 
	                   									rendered="#{not empty capBankEditor.assignedPoints}">
							<f:param name="swapType" value="CapBankPoint"/>
							<f:param name="id" value="#{point.pointId}"/>
	                   </x:commandLink>
	               </h:column>
				   <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{point.pointId}" />
	               </h:column>
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Assigned Points"/>
	                   </f:facet>
	                   <x:outputText value="#{point.pointName}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
							<x:outputText value="Order" title="Order used for control (Range: 1 to 1000 )" />
	                   </f:facet>
	                   <x:inputText value="#{point.displayOrder}" styleClass="char4Label" required="true" >
	                   		<f:validateLongRange minimum="1" maximum="1000" />
	                   </x:inputText>
	                   
	               </h:column>
	
	            </h:dataTable>
			</x:panelGroup>
	
	    </h:panelGrid>
	
		<f:verbatim></fieldset></f:verbatim>		
  	    
  	    <f:verbatim><br/><br/><fieldset class="fieldSet"><legend>Point Configuration</legend></f:verbatim>
	    	<f:verbatim><br/></f:verbatim>
	    		<h:panelGrid id="assignedPointsConfig" columns="1"  styleClass="gridLayout"
	    			rowClasses="gridCell" columnClasses="gridCell" rendered="#{not empty capBankEditor.assignedPoints}">
					<x:panelGroup>
    				<h:dataTable id="pointConfig" var="point"
	                    styleClass="bigScrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capBankEditor.assignedPoints}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25"  >			               
			             	<h:column >
			                   <x:outputText value="#{point.pointName}" styleClass="scrollerTableHeader"/>			                   
			                   <f:verbatim> <br> </f:verbatim>
			                    <x:outputText value="Override feeder limits:" /> 
			                   <x:selectBooleanCheckbox id="inheritFdr1"  value="#{point.overrideFdrLimits}" onclick="submit();" />				
			                   <x:outputText value = "Upper Bandwidth: " />
			                   <x:inputText value="#{point.upperBandwidth}" disabled="#{!point.overrideFdrLimits}" />
			                   <x:outputText value = "Lower Bandwidth: " />
			                   <x:inputText value="#{point.lowerBandwidth}" disabled="#{!point.overrideFdrLimits}" />
			                   <x:outputText value="Initiate scan on unsolicited: " /> 
			                   <x:selectBooleanCheckbox id="scanable1"  value="#{point.initScan}" onclick="submit();" />
			                   <x:outputText value = "Adaptive Count: " />
			                   <x:inputText value="#{point.NINAvg}" onchange="checkAdaptiveCount(this);"/>			                   								  
			               </h:column>		               
			
			          </h:dataTable>     
  				</x:panelGroup>
  			</h:panelGrid>
  			
  			<f:verbatim></fieldset></f:verbatim>

    </f:subview>
