<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Configuration Page" module="amr">
	<cti:standardMenu/>
	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/deviceConfiguration?home" title="Device Configuration Home"  />
	    &gt; Device Configuration
	</cti:breadCrumbs>
	
	<script type="text/javascript">
		<!-- Focus on the name field on page load -->
		jQuery(function() {
			var name = $('name');
		 	if(name){
		  		name.focus();
		  	}
		});
		
		function saveConfig(){
			var confirmSave = confirm("Are you sure you want to change this configuration?  This change will affect *ALL* devices that have been assigned this configuration once the configuration is downloaded to the devices again.");
			return confirmSave;
		}
	
		function cancel(){
			window.location = '/spring/deviceConfiguration?home';
			window.event.returnValue = false;
		}
		
	</script>

	<c:set var="editConfig" scope="page">
		<cti:getProperty property="AdministratorRole.ADMIN_EDIT_CONFIG"/>
	</c:set>

	<h2>Device Configuration</h2>
	<br>
    <tags:sectionContainer title="Device Configuration Settings">
	<spring:hasBindErrors name="deviceConfiguration">
		   
	   	<div style="color: red">
			Please fix the following issues:
		   <ul>
		      <c:forEach var="errorMsg" items="${errors.allErrors}">
		         <li>
		            <cti:msg key="${errorMsg}"/>
		         </li>
		      </c:forEach>
		   </ul>
	   	</div>
	</spring:hasBindErrors>
		
	<form id="configurationForm" action="" method="post" onsubmit="return saveConfig()">
		
		<spring:nestedPath path="deviceConfiguration">
			<div style="width: 600px; margin: 15px 10px; font-size: 12px;">
				
				<c:if test="${editConfig}">
					<input type="button" value="Cancel" onclick="cancel()" />
					<input type="submit" value="Save" /><br/><br/>
				</c:if>
					
				<tags:boxContainer title="Details" hideEnabled="false">
					<div style="margin: 0px 0px 5px 0px;">
						${configurationTemplate.description}
					</div>
					<div style="margin: 0px 0px 5px 0px;">
						<cti:renderInput input="${name}" />
					</div>
				</tags:boxContainer>
				
				<br/>
	
				<c:forEach var="category" items="${configurationTemplate.categoryList}">
				
					<tags:boxContainer title="${category.name}" showInitially="false">
                        <div>
                            ${category.description}
                        </div>
						<c:forEach var="item" items="${category.inputList}">
							<div style="margin: 0px 0px 5px 0px;">
								<cti:renderInput input="${item}" /><br/>
							</div>
						</c:forEach>
					</tags:boxContainer>
					<br/>
	
				</c:forEach>
	
				<c:if test="${editConfig}">
                    <input type="button" value="Cancel" onclick="cancel()" />
					<input type="submit" value="Save" />
				</c:if>
				
			</div>
		</spring:nestedPath>
		
	</form>
    </tags:sectionContainer>
</cti:standardPage>