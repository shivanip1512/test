<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Configuration Page" module="amr">
<cti:standardMenu menuSelection="deviceselection"/>
<cti:breadCrumbs>
	<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/deviceConfiguration?home" title="Device Configuration Home"  />
    &gt; Device Configuration
</cti:breadCrumbs>

<!-- Focus on the name field on page load -->
<script type="text/javascript">
	Event.observe(window, 'load', function() {
		var name = $('name');
	 	if(name){
	  		name.focus();
	  	}
	});
</script>

	<c:set var="editConfig" scope="page">
		<cti:getProperty property="AdministratorRole.ADMIN_EDIT_CONFIG"/>
	</c:set>

	<h2>Device Configuration</h2>
	
	<spring:hasBindErrors name="deviceConfiguration">
		   
	   	<div style="color: red">
			Please fix the following issues:
		   <ul>
		      <c:forEach var="errorMsg" items="${errors.allErrors}">
		         <li>
		            <spring:message message="${errorMsg}"/>
		         </li>
		      </c:forEach>
		   </ul>
	   	</div>
	</spring:hasBindErrors>
		
	<form action="" method="post">
		
		<spring:nestedPath path="deviceConfiguration">
			<div style="width: 600px; margin: 15px 10px; font-size: 12px;">
				
				<c:if test="${editConfig}">
					<input type="submit" value="Save" /><br/><br/>
				</c:if>
					
				<tags:boxContainer title="Details" hideEnabled="false">
					<div style="margin: 0px 0px 5px 0px;">
						${configurationTemplate.description}
					</div>
					<cti:renderInput input="${name}" /><br/><br/>
				</tags:boxContainer>
	
				<c:forEach var="category" items="${configurationTemplate.categoryList}">
				
					<tags:boxContainer title="${category.name}" showInitially="false">
						<c:forEach var="item" items="${category.inputList}">
							<div style="margin: 0px 0px 5px 0px;">
								<cti:renderInput input="${item}" /><br/>
							</div>
						</c:forEach>
					</tags:boxContainer>
	
				</c:forEach>
	
				<c:if test="${editConfig}">
					<input type="submit" value="Save" />
				</c:if>
				
			</div>
		</spring:nestedPath>
		
	</form>
</cti:standardPage>