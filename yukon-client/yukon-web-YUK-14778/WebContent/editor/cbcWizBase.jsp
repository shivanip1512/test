<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
	<cti:standardPage title="CapControl Wizard" module="capcontrol">
        <cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>
		<%
		    /* Entry point file for all operations that create a CBC PAObject */
		
		    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
		
		    if( type != PAOGroups.INVALID ) {
		        CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
		        
				/* This is needed because this was handled in the CBCSerlvet before entering faces pages. */
				/* Since the servlet bypass, this static method will need to be called entering any faces page. */
		        CapControlForm.setupFacesNavigation();
		        
		        capControlForm.initWizard( type );
		    }

        %>
		<x:panelLayout id="page" styleClass="pageLayout"
			headerClass="pageHeader" navigationClass="pageNavigation"
			bodyClass="pageBody" footerClass="pageFooter">
			<f:facet name="body">
				<h:form id="wizardForm">
                    <f:verbatim><cti:csrfToken/></f:verbatim>
					<x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Wizard" />
					<x:htmlTag value="br"/>
					<x:messages id="messageList" showSummary="true" showDetail="true"
						styleClass="smallResults" errorClass="errorResults" layout="table" />

					<h:panelGrid id="body" columns="1" styleClass="pageBody">

						<x:panelGroup>
							<x:htmlTag value="fieldset" styleClass="fieldSet">
                                <x:htmlTag value="legend"><x:outputText value="General"/></x:htmlTag>
								<x:outputLabel for="Name" value="Name: "
									title="A label for the item in the system" />
								<x:inputText id="Name" maxlength="60" styleClass="char32Label"
									value="#{capControlForm.wizData.name}" />
	
								<x:htmlTag value="br"/>
								
								<h:selectBooleanCheckbox id="disabledCheck" value="#{capControlForm.wizData.disabled}" />
								<x:outputLabel for="disabledCheck" value="Disabled" title="Is the item disabled" />
							</x:htmlTag>
						</x:panelGroup>

						<x:panelGroup rendered="#{capControlForm.visibleTabs['CBCType']}">
							<x:htmlTag value="fieldset" styleClass="fieldSet">
                                <x:htmlTag value="legend"><x:outputText value="Item Type"/></x:htmlTag>
                                
                                <x:panelGrid columns="2">
                                
									<x:outputLabel for="CBC_Type" value="CBC Type: " title="Type of CBC this object will be" />
									<x:selectOneMenu id="CBC_Type" value="#{capControlForm.wizData.secondaryType}" required="true"
										onchange="submit();">
										<f:selectItems value="#{selLists.CBCTypes}" />
									</x:selectOneMenu>
		
									<x:outputLabel for="CBC_Comm_Channel" value="Comm. Channel: "
										title="The communication channel this CBC will use (only applies to two-way CBC types)" />
									<x:selectOneMenu id="CBC_Comm_Channel"
										disabled="#{!capControlForm.wizData.portNeeded}"
										value="#{capControlForm.wizData.portID}">
										<f:selectItems value="#{selLists.commChannels}" />
									</x:selectOneMenu>
								
								</x:panelGrid>

							</x:htmlTag>

						</x:panelGroup>

						<x:panelGroup rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
                            
                            <x:htmlTag value="br"/>
                            
							<h:selectBooleanCheckbox id="createCBC" value="#{capControlForm.wizData.createNested}"
								onclick="submit();" />
							<x:outputLabel for="createCBC" value="Create New CBC" title="Automatically create a CBC device for this CapBank" />

							<x:htmlTag value="fieldset" styleClass="fieldSet">
                                <x:htmlTag value="legend"><x:outputText value="Controller Type"/></x:htmlTag>
                                
                                <x:panelGrid columns="2">
                                
									<x:outputLabel for="Controller_Name" value="Controller Name: "
										title="A label for the Controller in the system" />
									<x:inputText id="Controller_Name" required="false" maxlength="60"
										styleClass="char32Label"
										value="#{capControlForm.wizData.nestedWizard.name}"
										disabled="#{!capControlForm.wizData.createNested}" />
		
									<x:outputLabel for="Controller_Type" value="CBC Type: "
										title="Type of CBC this object will be" />
									<x:selectOneMenu id="Controller_Type"
										value="#{capControlForm.wizData.nestedWizard.secondaryType}"
										required="true" onchange="submit();"
										disabled="#{!capControlForm.wizData.createNested}">
										<f:selectItems value="#{selLists.CBCTypes}" />
									</x:selectOneMenu>
		
									<x:outputLabel for="Controller_Comm_Channel"
										value="Comm. Channel: "
										title="The communication channel this CBC will use (only applies to two-way CBC types)" />
									<x:selectOneMenu id="Controller_Comm_Channel"
										disabled="#{!capControlForm.wizData.nestedWizard.portNeeded || !capControlForm.wizData.createNested}"
										value="#{capControlForm.wizData.nestedWizard.portID}">
										<f:selectItems value="#{selLists.commChannels}" />
									</x:selectOneMenu>
								
								</x:panelGrid>

							</x:htmlTag>

						</x:panelGroup>
                        
                        <x:panelGroup rendered="#{capControlForm.wizData.portNeeded || 
                                                  (capControlForm.wizData.createNested && capControlForm.wizData.nestedWizard.portNeeded)}">
                            <x:htmlTag value="fieldset" styleClass="fieldSet">
                                <x:htmlTag value="legend"><x:outputText value="Configuration"/></x:htmlTag>
                                
                                <x:panelGrid columns="2">
                                    <x:outputText value="DNP Configuration: " title="The default DNP configuration the CBC will be assigned to."/>
                                    <x:outputText value="#{capControlForm.defaultDnpConfiguration.name}"/>
                                </x:panelGrid>

                            </x:htmlTag>
                        </x:panelGroup>
                        
						<f:facet name="footer">
							<x:panelGroup>
								<x:htmlTag value="br"/>
								<x:htmlTag value="hr"/>
								<x:htmlTag value="br"/>
								<x:commandButton value="Create" action="#{capControlForm.create}" styleClass="stdButton"
									title="Saves this item to the database" />
								<x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true"
									title="Returns to the last module page that was used to enter this editor">
									<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
								</x:commandButton>
							</x:panelGroup>
						</f:facet>
					</h:panelGrid>
				</h:form>
			</f:facet>
		</x:panelLayout>
	</cti:standardPage>
</f:view>
