<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.changeDeviceTypeChoose">

    <tags:bulkActionContainer key="yukon.common.device.bulk.changeDeviceTypeChoose" deviceCollection="${deviceCollection}">
    
        <cti:url var="changeTypeUrl" value="/bulk/changeDeviceType/changeDeviceType"/>
        <form id="changeTypeForm" method="post" action="${changeTypeUrl}">
            <cti:csrfToken/>
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- AVAILABLE TYPES --%>
            <select id="deviceType" name="deviceTypes">
                <c:forEach var="deviceType" items="${deviceTypes}">
                    <option value="${deviceType.value}">${deviceType.key}</option>
                </c:forEach>
            </select>
            
            <div class="page-action-area">
              <cti:button nameKey="change" type="submit" name="changeButton" classes="primary action"/>
            </div>
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>