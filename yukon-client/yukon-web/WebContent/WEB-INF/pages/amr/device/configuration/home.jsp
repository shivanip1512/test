<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Configuration Page" module="amr">
<cti:standardMenu menuSelection="deviceselection"/>
<cti:breadCrumbs>
	<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; Device Configuration Home
</cti:breadCrumbs>

	<c:set var="editConfig" scope="page">
		<cti:getProperty property="AdministratorRole.ADMIN_EDIT_CONFIG"/>
	</c:set>

	<h2>Device Configuration</h2>
	
	<c:if test="${!empty param.message}">
		${param.message}
	</c:if>
	
	<c:if test="${editConfig}">
		<h4>Create new configurations</h4>
		<form name="configTemplateForm" action="/spring/deviceConfiguration">
		
			<select name="configurationTemplate">
				<c:forEach var="template" items="${configurationTemplateList}">
					<option id="${template}">${template}</option>	
				</c:forEach>
			</select>
			
			<input type="submit" name="createConfig" value="Create" />
		
		</form>
	</c:if>
	
	<h4>${editConfig ? 'Manage' : 'View'} existing configurations</h4>
	<form name="configForm" action="/spring/deviceConfiguration">
	
		<select id="configuration" name="configuration">
			<c:forEach var="config" items="${existingConfigs}">
				<option value="${config.id}">${config.name}</option>	
			</c:forEach>
		</select>
		
		<input type="submit" name="editConfig" value="${editConfig ? 'Edit' : 'View'}"/>
		<c:if test="${editConfig}">
			<input type="submit" name="removeConfig" value="Delete"/>
			<input type="submit" name="cloneConfig" value="Clone"/>
		</c:if>
		<input type="submit" name="assignDevices" value="Assign to Devices"/>
	
	</form>
	
</cti:standardPage>