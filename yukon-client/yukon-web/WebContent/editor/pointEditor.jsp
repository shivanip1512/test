<%@ page import="com.cannontech.database.data.point.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.gui.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>

<%
	int ptType = ParamUtil.getInteger(request, "ptType");
	int ptId = ParamUtil.getInteger(request, "ptId");

	PointForm ptEditorForm =
		(PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );

	if( ptEditorForm.getPointBase() == null ) {
		ptEditorForm.initPoint( ptId, ptType );
	}
	
%>


<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8">
  <title>Faces Impl</title>
  <link rel="stylesheet" type="text/css" href="base.css">
</head>


<body>

<f:view>

    <x:panelLayout id="page"
            styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
		<h:form id="ptEditorForm">

            <h:panelGrid id="body" columns="2">

				<f:facet name="header">
					<h:panelGroup>
						<h:outputText styleClass="editorHeader" value="Point Editor"/>
		                <x:messages id="messageList" showSummary="true" showDetail="true" summaryFormat="{0}" styleClass="smallResults" />
		                <f:verbatim><br/><hr/><br/></f:verbatim>
	                </h:panelGroup>
				</f:facet>


               	<jsp:include page="pointBaseEditor.jsp"/>

                <jsp:include page="pointAlarmEditor.jsp"/>



                <x:panelTabbedPane bgcolor="#FFFFCC" rendered="false">
                    <x:panelTab id="tab2" label="Tab 2" rendered="#{ptEditorForm.visibleTabs['Tab2']}">
   	                    <h:selectBooleanCheckbox id="testCheckBox" value="#{testCheckBox.checked}"/>
   	                    <h:outputLabel for="testCheckBox" value="A checkbox"/>
	                    <f:verbatim><br/><br/></f:verbatim>
                        <h:inputText id="inp1"/><f:verbatim><br></f:verbatim>
                        <h:inputText id="inp2"/>
                    </x:panelTab>

                    <x:panelTab id="tab3" label="Tab 3" rendered="#{ptEditorForm.visibleTabs['Tab3']}">
						<x:selectOneRadio value="#{testRadioButton.choice}">
							<f:selectItem itemValue="0" itemLabel="First Choice" />
							<f:selectItem itemValue="1" itemLabel="Second Choice" />
                        </x:selectOneRadio>
		                
						<f:verbatim><br/><br/></f:verbatim>

                        <h:inputText id="inp3"/><f:verbatim><br/></f:verbatim>
                    </x:panelTab>

                    <f:verbatim><br/><hr/><br/></f:verbatim>
	                </x:panelTabbedPane>





				<f:facet name="footer">
					<h:panelGroup>
		                <f:verbatim><br/><hr/><br/></f:verbatim>
		                <h:commandButton value="Submit" action="#{ptEditorForm.update}" />
	                </h:panelGroup>
				</f:facet>

            </h:panelGrid>
		</h:form>
        </f:facet>


    </x:panelLayout>


</f:view>


</body>
</html>