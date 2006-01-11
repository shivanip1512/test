<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<cti:outputDoctype htmlLevel="strict"/>
<html>
    <head>
        <title></title>           

        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/CannonStyle.css" >
        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/InnerStyles.css" >
        <cti:outputOtherCss/>
        <script type="text/javascript" src="<c:url value="/JavaScript/prototype.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/JavaScript/CtiMenu.js"/>"></script>
    </head>
<body>

<div id="InnerContent">
<cti:outputContent/>
</div> <!-- Content -->

</body>
</html>