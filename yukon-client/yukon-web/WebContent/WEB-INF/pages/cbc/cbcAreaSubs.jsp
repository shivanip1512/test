<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="areaSubs" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
	    <f:verbatim><br/><br/>
	    	<fieldset>
	    	<legend>Substation Assignment</legend>
	    </f:verbatim>
	    <f:verbatim><br/></f:verbatim>

	    <h:panelGrid id="areaBody" columns="2" styleClass="gridLayout"
	    		rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
	            <h:dataTable id="subAvailData" var="sub" 
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capControlForm.dataModel.unassigned}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{sub.subID}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Available Substations" />
	                   </f:facet>
	                   <x:outputText value="#{sub.subName}" title="#{sub.subName}"/>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="Add >>" action="#{capControlForm.dataModel.add}">
							<f:param name="id" value="#{sub.subID}"/>
	                   </x:commandLink>
	               </h:column>
	
	            </h:dataTable>
<h:panelGrid columns="1" columnClasses="scrollerCentered">
	                <x:dataScroller id="areaScrollButtons" for="subAvailData" fastStep="25"
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
	
	                <x:dataScroller id="subScrollDisplay" for="subAvailData" rowsCountVar="rowsCount"
	                		styleClass="scroller"
	                        displayedRowsCountVar="displayedRowsCountVar"
	                        firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
	                        pageCountVar="pageCount" pageIndexVar="pageIndex">
	                    <h:outputFormat value="{0} Substations found" >
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
	            <h:dataTable id="subAssignData" var="sub"
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	                    rendered="#{capControlForm.visibleTabs['CBCArea']}"	                    
	            		value="#{capControlForm.dataModel.assigned}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" >
	               <h:column rendered="#{!capControlForm.dataModel.areaNone}">
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="<< Remove" action="#{capControlForm.dataModel.remove}">
							<f:param name="id" value="#{sub.subID}"/>
	                   </x:commandLink>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Assigned Substations"/>
	                   </f:facet>
	                   <x:outputText value="#{sub.subName}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
							<x:outputText value="Order" title="Order used for control (Range: 1 to 1000 )" />
	                   </f:facet>
	                   <x:inputText value="#{sub.displayOrder}" styleClass="char4Label" required="true" >
	                   </x:inputText>
	                   
	               </h:column>
	
	            </h:dataTable>
				<f:verbatim>
					<br/> <br/>
	    			<fieldset>
	    				<legend>Links</legend>
	    			</f:verbatim>
	    				<f:verbatim><br/></f:verbatim>
					<x:commandLink action="#{capControlForm.dataModel.createWizardLink}" 
								   value="Create Sub" 
								   title="Click here to create a sub. Return after creation to assign the sub.">
								   		<f:param  name="type" id="type" value="#{selLists.substationType}"/>
								   </x:commandLink>
					<f:verbatim>
	    				</fieldset>
	    			</f:verbatim>
			</x:panelGroup>
	
	    </h:panelGrid>
	


	    <f:verbatim>
	    	</fieldset>
	    </f:verbatim>
</f:subview>