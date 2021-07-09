<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="${page}">
    <cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    
        <tags:widgetContainer deviceId="${deviceId}" identify="false">
            <tags:widget bean="devicePointsWidget" />
        </tags:widgetContainer>
    
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>