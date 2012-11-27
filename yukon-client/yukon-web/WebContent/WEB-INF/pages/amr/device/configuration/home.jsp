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

	<h2>Device Configuration Setup</h2>
    <br>
	
	<c:if test="${!empty param.message}">
		${fn:escapeXml(param.message)}
        <br>
        <br>
	</c:if>
	
    <c:if test="${editConfig}">
        <form name="configTemplateForm" action="/deviceConfiguration">
            <tags:sectionContainer title="Create New Configuration">
                <tags:nameValueContainer style="width: 350px;">
                    <tags:nameValue name="Configuration Type">
                        <select name="configurationTemplate">
                            <c:forEach var="template" items="${configurationTemplateList}">
                                <option id="${template}">${template}</option>	
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                </tags:nameValueContainer>
            </tags:sectionContainer>
            <input type="submit" name="createConfig" value="Create" class="formSubmit"/>
        </form>
    </c:if>
	
    <br>
    <br>
	
	<c:choose>
		<c:when test="${fn:length(existingConfigs) > 0}">
            <form name="configForm" action="/deviceConfiguration">
                <tags:sectionContainer title="${editConfig ? 'Manage' : 'View'} Existing Configurations">
                    <tags:nameValueContainer style="width: 380px;">
                        <tags:nameValue name="Existing Configurations">
            				<select id="configuration" name="configuration">
            					<c:forEach var="config" items="${existingConfigs}">
            						<option value="${config.id}">${fn:escapeXml(config.name)}</option>	
            					</c:forEach>
            				</select>
    				    </tags:nameValue>
                    </tags:nameValueContainer>
                    <br>
                    <a href="/bulk/deviceSelection">Manage Device Configuration Assignments</a>
                </tags:sectionContainer>
                <input type="submit" name="editConfig" value="${editConfig ? 'Edit' : 'View'}" class="formSubmit"/>
                <c:if test="${editConfig}">
                    <input type="submit" name="removeConfig" value="Delete" onclick="return deleteConfig()" class="formSubmit"/>
                    <input type="submit" name="cloneConfig" value="Copy" class="formSubmit"/>
                </c:if>
			</form>
		</c:when>
		<c:otherwise>
            <tags:sectionContainer title="${editConfig ? 'Manage' : 'View'} Existing Configurations">
                There are no existing configurations.
            </tags:sectionContainer>
		</c:otherwise>
	</c:choose>
	
</cti:standardPage>