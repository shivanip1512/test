<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:outputDoctype htmlLevel="strict"/>
<html>
    <head>
        <title><c:out value="${ctiPageTitle}"/></title>           

        <cti:outputHeadContent>
          <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
          <cti:includeCss link="/WebConfig/yukon/styles/StandardStyles.css"/>
          <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
          <cti:includeScript link="/JavaScript/prototype.js"/>
          <cti:includeScript link="/JavaScript/CtiMenu.js"/>
        </cti:outputHeadContent>
    </head>
<body class="<c:out value="${ctiModuleName}"/>_module">
<div id="Header">
    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
    <div class="stdhdr_right"><div id="TopRightLogo"></div></div>
    <div class="stdhdr_clear"></div>
</div>
<cti:outputMenu/>

<div id="Content">
<cti:outputContent/>
</div> <!-- Content -->

<div id="CopyRight">
Yukon Version ${ctiYukonVersion}. 
Copyright &copy; 2002-2007, 
  Cannon Technologies. All rights reserved.
</div>

</body>
</html>