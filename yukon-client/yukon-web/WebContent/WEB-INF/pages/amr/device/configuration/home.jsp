<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Configuration Page" module="amr">
<cti:standardMenu/>
<cti:breadCrumbs>
	<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; Device Configuration Home
</cti:breadCrumbs>

	<script type="text/javascript">
	
		function deleteConfig(){
			var confirmDelete = confirm("Are you sure you want to delete this configuration?  You will not be able to recover this configuration after it is deleted.  All devices with this configuration will be changed to have no configuration assigned to them.  However, any configuration settings that have been downloaded to the meter will remain until a new configuration is assigned and downloaded.");
			return confirmDelete;
		}
	
	</script>

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

	<c:choose>
		<c:when test="${fn:length(existingConfigs) > 0}">
			<form name="configForm" action="/spring/deviceConfiguration">
			
				<select id="configuration" name="configuration">
					<c:forEach var="config" items="${existingConfigs}">
						<option value="${config.id}">${config.name}</option>	
					</c:forEach>
				</select>
				
				<input type="submit" name="editConfig" value="${editConfig ? 'Edit' : 'View'}"/>
				<c:if test="${editConfig}">
					<input type="submit" name="removeConfig" value="Delete" onclick="return deleteConfig()"/>
					<input type="submit" name="cloneConfig" value="Clone"/>
					<input type="submit" name="assignDevices" value="Assign to Devices"/>
				</c:if>
			
			</form>
		</c:when>
		<c:otherwise>
			There are no existing configurations.
		</c:otherwise>
	</c:choose>
	
</cti:standardPage>