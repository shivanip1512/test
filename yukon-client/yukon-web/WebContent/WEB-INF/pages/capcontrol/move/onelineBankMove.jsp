<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="Move Bank" module="blank">
    <cti:standardMenu />
    <%@ include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/WebConfig/yukon/styles/da/CapcontrolGeneralStyles.css"/>
    <cti:url value="/oneline/OnelineCBCServlet" var="returnUrl">
        <cti:param name="id" value="${subBusId}"/>
        <cti:param name="redirectURL" value="/capcontrol/tier/feeders?isSpecialArea=false&substationId=${substationId}"/>
    </cti:url>
    <cti:breadCrumbs>
        <cti:crumbLink url="${returnUrl}" title="Return" />
    </cti:breadCrumbs>
    
    <%@ include file="bankMove.jsp" %>
    
</cti:standardPage>