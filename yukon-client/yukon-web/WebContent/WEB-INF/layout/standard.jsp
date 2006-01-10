<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<cti:outputDoctype htmlLevel="strict"/>
<html>
    <head>
        <title><c:out value="${ctiPageTitle}"/></title>           

        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/CannonStyle.css" >
        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
        <cti:outputOtherCss/>
        <script type="text/javascript" src="<c:url value="/JavaScript/prototype.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/JavaScript/CtiMenu.js"/>"></script>
    </head>
<body>
<div id="Header">
    <div class="stdhdr_left"><img src="/capcontrol/images/YourLogo.gif" alt="Your Logo Here"></div>
    <div class="stdhdr_right"><img src="/capcontrol/images/HeaderGraphic.gif" alt="Capacitorcontrol.com powered by Yukon"></div>
    <div class="stdhdr_clear"></div>
</div>
<cti:outputMenu/>

<div id="Content">
<cti:outputContent/>
</div> <!-- Content -->

<div id="CopyRight">
Copyright &copy; 2002-2006, 
  Cannon Technologies, Inc. All rights reserved.
</div>

</body>
</html>