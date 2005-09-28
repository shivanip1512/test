<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="t" %>

<%

%>

<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8" />
  <title>Yukon Editor</title>
  <link rel="stylesheet" type="text/css" href="/editor/css/base.css" />
  
</head>


<body>

<f:view>

    <x:panelLayout id="basePage" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:panelGrid id="baseBody" columns="1" styleClass="pageBody">

				<f:facet name="header">
					<x:panelGroup>

	                </x:panelGroup>
				</f:facet>


				<!-- content of our editor/wizard -->
				<x:panelGroup>

					<x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Wizard"/>
	                <f:verbatim><br/><hr/><br/></f:verbatim>
				    <x:messages id="baseMessageList" showSummary="true" showDetail="true"
				    		styleClass="smallResults" errorClass="errorResults" layout="table"/>

					<jsp:include page="cbcGeneralEditor.jsp"/>

				</x:panelGroup>				



				<f:facet name="footer">
					<x:panelGroup>
				        <f:verbatim><br/><hr/><br/></f:verbatim>
				        <x:commandButton value="Create" action="#{capControlForm.create}" styleClass="stdButton" title="Saves this item to the database" />
				        <x:commandButton value="Cancel" action="none" styleClass="stdButton" immediate="true" title="Cancels editing and returns to the last module that was used" >
							<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
						</x:commandButton>
				    </x:panelGroup>
				</f:facet>

			</h:panelGrid>

        </f:facet>
    </x:panelLayout>


</f:view>


</body>
</html>