<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:outputDoctype htmlLevel="strict"/>
<html>
    <head>
        <title></title>           

        <cti:outputHeadContent>
          <cti:includeCss link="/WebConfig/yukon/CannonStyle.css" />
          <cti:includeCss link="/WebConfig/yukon/styles/StandardStyles.css" />
          <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
          <cti:includeCss link="/WebConfig/yukon/styles/InnerStyles.css" />
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