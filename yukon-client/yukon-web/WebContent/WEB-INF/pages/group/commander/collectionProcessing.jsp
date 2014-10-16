<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="tools" page="bulk.sendCommand">

<script type="text/javascript">
    function toggleEmailNotificaionField() {
        yukon.ui.util.toggleEmailNotificationAddressField(
                'input[name =\'sendEmail\']', 'input[name =\'emailAddress\']');
    }
</script>
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.sendCommand" deviceCollection="${deviceCollection}">
  
    <%-- ERROR MSG --%>
    <c:if test="${not empty param.errorMsg}">
        <div class="error">${param.errorMsg}</div>
        <c:set var="errorMsg" value="" scope="request"/>
        <br>
    </c:if>
    
    <div style="width: 700px;">
        <form id="collectionProcessingForm" action="<cti:url value="/group/commander/executeCollectionCommand" />" method="post">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
      
              <%-- SELECT COMMAND --%>
              <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
              <div>${selectCommandLabel}:</div>
            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" 
                selectedCommandString="${param.commandString}" selectedSelectValue="${param.commandSelectValue}"/>
            <br><br>
            <c:if test="${isSMTPConfigured}">            
            	<cti:msg var="sendEmailAddressLabel" key="yukon.common.device.commander.sendEmailNotification"/>
	            <div>${sendEmailAddressLabel}
                	<input type="checkbox" name="sendEmail" onclick="toggleEmailNotificaionField();">
	            </div>
    	        <br>          
        	    <%-- EMAIL --%>
            	<cti:msg var="emailAddressLabel" key="yukon.common.device.commander.emailNotificationAddr"/>
	            <div>${emailAddressLabel} :
    	        	<input type="text" name="emailAddress" value="${email}" size="40" disabled="disabled">
    	        </div>
	            <br><br>
			</c:if>
            <cti:button nameKey="execute" type="submit" classes="primary action" busy="true"/>
        </form>
    </div>
  
  </tags:bulkActionContainer>
    
</cti:standardPage>