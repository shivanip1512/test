<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="op" tagdir="/WEB-INF/tags/operator" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="contactList">

	<script type="text/javascript">
	
		function deleteAdditionalContact(id) {

			$('deleteAdditionalContactId').value = id;
			$('deleteAdditionalContactForm').submit();
		}
		
	</script>
	
	<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
	<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
	<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
	<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
	<form id="addContactForm" action="/spring/stars/operator/general/contacts/contactEdit" method="post">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
	</form>
	
	<form id="deleteAdditionalContactForm" action="/spring/stars/operator/general/contacts/deleteAdditionalContact" method="post">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
		<input type="hidden" name="deleteAdditionalContactId" value=""/>
	</form>
	
	<table class="resultsTable">
	
		<tr>
			<th><i:inline key=".header.contact"/></th>
			<th><i:inline key=".header.notifications"/></th>
			<th style="text-align:center;width:100px;"><i:inline key=".header.remove"/></th>
		</tr>
	
	
		
	
		<c:forEach var="contact" items="${contacts}">
		
			<tr>
			
				<td>
				
					<cti:url var="contactEditUrl" value="/spring/stars/operator/general/contacts/contactEdit">
						<cti:param name="accountId" value="${accountId}"/>
						<cti:param name="energyCompanyId" value="${energyCompanyId}"/>
						<cti:param name="contactId" value="${contact.contactId}"/>
					</cti:url>
				
					<a href="${contactEditUrl}">${contact.lastName}, ${contact.firstName}</a>
				
				</td>
				
				<td>
					
					<tags:nameValueContainer tableClass="noStyle">
					
						<c:forEach var="otherContactNotification" items="${contact.otherContactNotifications}">
							<i:nameValue nameKey="${otherContactNotification.operatorContactNotificationType.formatKey}" nameColumnWidth="150px">
								${otherContactNotification.notification}
							</i:nameValue>
						</c:forEach>
					
					</tags:nameValueContainer>
				
				</td>
				
				<td style="text-align:center;">
				
					<c:choose>
						<c:when test="${!contact.primary}">
							<img src="${delete}" onclick="deleteAdditionalContact(${contact.contactId});" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
						</c:when>
						<c:otherwise>
							<i:inline key=".isPrimary"/>
						</c:otherwise>
					</c:choose>
					
				</td>
			
			</tr>
			
		</c:forEach>
		
		<%-- ADD NOTIFICATION --%>
		<tr style="background-color:#EEE;">
			<td colspan="3">
				<img src="${add}" onclick="$('addContactForm').submit();" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'">
				<i:inline key=".addContact"/>
			</td>
		</tr>
	
	
	</table>

</cti:standardPage>