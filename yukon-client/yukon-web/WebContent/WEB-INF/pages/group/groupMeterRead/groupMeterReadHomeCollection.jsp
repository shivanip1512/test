<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.readAttribute">

	<c:if test="${not empty errorMsg}">
   		<br>
   		<div class="error">${errorMsg}</div>
   	</c:if>
   	
   	<br>

   	<tags:bulkActionContainer key="yukon.web.modules.tools.bulk.readAttribute" deviceCollection="${deviceCollection}">
   	
   		<form id="groupMeterReadForm" action="/group/groupMeterRead/readCollection" method="post">
            <cti:csrfToken/>
	   		<cti:deviceCollection deviceCollection="${deviceCollection}" />
	   		
	        <strong class="db"><cti:msg2 key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>:</strong>
	        <tags:attributeSelector attributes="${allGroupedReadableAttributes}" 
                fieldName="attribute" 
                selectedAttributes="${selectedAttributes}" 
                multipleSize="8" 
                groupItems="true"/>
            <div class="page-action-area">
                <cti:button nameKey="read" type="submit" classes="f-disable-after-click primary action"/>
            </div>
   		</form>
   	
   	</tags:bulkActionContainer>
	
</cti:standardPage>