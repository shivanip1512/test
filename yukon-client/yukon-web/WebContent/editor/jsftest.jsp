<%@ page import="com.cannontech.database.data.point.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>

<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8">
  <title>Faces Impl</title>
  <link rel="stylesheet" type="text/css" href="css/base.css">
</head>


<body>

<f:view>

    <x:panelLayout id="page"
            styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
		<h:outputText styleClass="editorHeader" value="Monkey Boy"/>

        </f:facet>


    </x:panelLayout>


</f:view>


</body>
</html>