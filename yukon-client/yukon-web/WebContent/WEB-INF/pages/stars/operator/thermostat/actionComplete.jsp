<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/StandardStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/StarsConsumerStyles.css" type="text/css">

    <h3>
        <cti:msg key="yukon.dr.operator.actionComplete.header" /><br>
    </h3>
    
    <div class="message">
        <div style="text-align: left;">
            <cti:msg key="${message}" />
        </div>
        
        <c:url var="viewUrl" value="${viewUrl}"></c:url>
        <br><br>
        <a href="${viewUrl}"><cti:msg key="yukon.dr.operator.actionComplete.ok" /></a>
    </div>

