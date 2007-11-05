<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="Device Name">${meter.name}</tags:nameValue>
	<tags:nameValue name="Meter Number">${meter.meterNumber}</tags:nameValue>
	<tags:nameValue name="Type">${deviceType}</tags:nameValue>
	<tags:nameValue name="Physical Address">${meter.address}</tags:nameValue>
	<tags:nameValue name="Route">${meter.route}</tags:nameValue>
    
    <tags:nameValue name="Outage Status">
        <c:if test="${state == 'RESTORED'}">
            Meter responded successfully.
        </c:if>
        <c:if test="${state == 'OUTAGE'}">
            Meter did not respond.
        </c:if>
        
        <div style="text-align: right">
          <tags:widgetActionRefresh method="ping" label="Ping" labelBusy="Pinging"/>
        </div>
    </tags:nameValue>
  
</tags:nameValueContainer>

<c:if test="${errorsExist}">
    <br/>
    <c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
</c:if>


