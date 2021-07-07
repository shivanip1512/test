<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meter.points">

    <tags:widgetContainer deviceId="${paoId}" identify="false">
        <tags:widget bean="devicePointsWidget" />
    </tags:widgetContainer>

</cti:standardPage>