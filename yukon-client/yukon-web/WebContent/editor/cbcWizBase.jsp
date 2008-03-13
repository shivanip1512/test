
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

<%
    //****
    // Entry point file for all operations that create a CBC PAObject
    //****

    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);

    if( type != PAOGroups.INVALID ) {
        JSFUtil.resetBackingBean("capControlForm");
        JSFUtil.resetBackingBean("capBankEditor");
        JSFUtil.resetBackingBean("cbcCopyForm");
        CapControlForm capControlForm =
            (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );

        capControlForm.initWizard( type );
    }

%>



    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="wizardForm">

            <f:verbatim><br/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Wizard"/>
            <f:verbatim><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>

                <x:panelGroup>
                    <f:verbatim><br/><fieldset class="fieldSet"><legend>General</legend></f:verbatim>

                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="Name" value="Name: " title="A label for the item in the system"/>
                    <x:inputText id="Name" maxlength="32" styleClass="char32Label"
                            value="#{capControlForm.wizData.name}" />
    
                    <f:verbatim><br/></f:verbatim>
                    <h:selectBooleanCheckbox id="disabledCheck" value="#{capControlForm.wizData.disabled}" />
                    <x:outputLabel for="disabledCheck" value="Disabled" title="Is the item disabled"/>
                    <f:verbatim></fieldset></f:verbatim>
                </x:panelGroup>


                <x:panelGroup rendered="#{capControlForm.visibleTabs['CBCType']}" >
                    <f:verbatim><br/><br/></f:verbatim>
                    <f:verbatim><fieldset><legend>Item Type</legend></f:verbatim>
                    <x:outputLabel for="CBC_Type" value="CBC Type: "
                            title="Type of CBC this object will be"/>
                    <x:selectOneMenu id="CBC_Type" value="#{capControlForm.wizData.secondaryType}"
                            required="true" onchange="submit();" >
                        <f:selectItems value="#{selLists.CBCTypes}" />
                    </x:selectOneMenu>

                    <f:verbatim><br/><br/></f:verbatim>
                    <x:outputLabel for="CBC_Comm_Channel" value="Comm. Channel: "
                            title="The communication channel this CBC will use (only applies to two-way CBC types)"/>
                    <x:selectOneMenu id="CBC_Comm_Channel" disabled="#{!capControlForm.wizData.portNeeded}"
                            value="#{capControlForm.wizData.portID}" >
                        <f:selectItems value="#{selLists.commChannels}"/>
                    </x:selectOneMenu>
                    
                    <f:verbatim></fieldset></f:verbatim>

                </x:panelGroup>             


                <x:panelGroup rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >
                    <f:verbatim><br/><br/></f:verbatim>

                    <h:selectBooleanCheckbox id="createCBC" value="#{capControlForm.wizData.createNested}"
                            onclick="submit();" />
                    <x:outputLabel for="createCBC" value="Create New CBC" title="Automatically create a CBC device for this CapBank"/>

                    <f:verbatim><fieldset><legend>Controller Type</legend></f:verbatim>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="Controller_Name" value="Controller Name: " title="A label for the Controller in the system"/>
                    <x:inputText id="Controller_Name" required="false" maxlength="32" styleClass="char32Label"
                            value="#{capControlForm.wizData.nestedWizard.name}"
                            disabled="#{!capControlForm.wizData.createNested}" />

                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="Controller_Type" value="CBC Type: "
                            title="Type of CBC this object will be"/>
                    <x:selectOneMenu id="Controller_Type" value="#{capControlForm.wizData.nestedWizard.secondaryType}"
                            required="true" onchange="submit();"
                            disabled="#{!capControlForm.wizData.createNested}" >
                        <f:selectItems value="#{selLists.CBCTypes}" />
                    </x:selectOneMenu>

                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="Controller_Comm_Channel" value="Comm. Channel: "
                            title="The communication channel this CBC will use (only applies to two-way CBC types)"/>
                    <x:selectOneMenu id="Controller_Comm_Channel"
                            disabled="#{!capControlForm.wizData.nestedWizard.portNeeded || !capControlForm.wizData.createNested}"
                            value="#{capControlForm.wizData.nestedWizard.portID}" >
                        <f:selectItems value="#{selLists.commChannels}"/>
                    </x:selectOneMenu>
                    
                    <f:verbatim></fieldset></f:verbatim>

                </x:panelGroup>             

                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Create" action="#{capControlForm.create}" styleClass="stdButton" title="Saves this item to the database" />
                        <x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
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
