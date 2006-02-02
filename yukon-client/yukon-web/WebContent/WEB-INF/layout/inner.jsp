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
        <cti:outputHeadContent>
          <cti:includeScript link="/JavaScript/prototype.js"/>
          <cti:includeScript link="/JavaScript/CtiMenu.js"/>
        </cti:outputHeadContent>
    </head>
<body>

<div id="InnerContent">
<cti:outputContent/>
</div> <!-- Content -->

</body>
</html>