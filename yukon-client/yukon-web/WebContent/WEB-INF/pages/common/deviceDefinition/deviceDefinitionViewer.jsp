<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Device Definitions" module="support">
<cti:standardMenu menuSelection="information|deviceDef"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/support/" title="Support" />
    <cti:crumbLink>Device Definitions</cti:crumbLink>
</cti:breadCrumbs>

	<c:url var="url" value="/spring/common/deviceDefinition.xml"/>
	
	<style type="text/css">
		ul {margin:0 0 0 1em;padding: 0;}
		li {margin:0;padding:0;list-style-type:circle;}
	</style>

	<script language="JavaScript">
		function doDefinitionFilter(selectEl, filterType) {
			var filterValue = selectEl.options[selectEl.selectedIndex].value;
			if (filterValue.strip() == '') {
				window.location = '${url}';
				return;
			}
			window.location = '${url}?' + filterType + '=' + filterValue;
		}
	</script>

	
	<%-- WRITE SELECT --%>
	<tags:sectionContainer title="Filters">
	<table class="miniResultsTable" style="width:75%">
		<tr>
			<th>Device Type</th>
			<th>Display Group</th>
			<th>Change Group</th>
			<th>Attribute</th>
			<th>Tag</th>
		</tr>
		<tr>
			<%-- device type select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'deviceType');">
					<option value="" >All Definitions</option>
					<c:forEach var="group" items="${allDeviceTypes}" >
						<optgroup label="${group.key}">
							<c:forEach var="definition" items="${group.value}">
								<c:choose>
									<c:when test="${deviceTypeParam == definition.type}">
										<option value="${definition.type}" selected>${definition.displayName}</option>
									</c:when>
									<c:otherwise>
										<option value="${definition.type}">${definition.displayName}</option>
									</c:otherwise>
								</c:choose>
								
							</c:forEach>
						</optgroup>
					</c:forEach>
				</select>
			</td>
			
			<%-- display group select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'displayGroup');">
					<option value="" >Any Display Group</option>
					<c:forEach var="group" items="${allDisplayGroups}" >
						<c:choose>
							<c:when test="${displayGroupParam == group}">
								<option value="${group}" selected>${group}</option>
							</c:when>
							<c:otherwise>
								<option value="${group}">${group}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
			
			<%-- change group select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'changeGroup');">
					<option value="" >Any Change Group</option>
					<c:forEach var="changeGroup" items="${allChangeGroups}" >
						<c:choose>
							<c:when test="${changeGroupParam == changeGroup}">
								<option value="${changeGroup}" selected>${changeGroup}</option>
							</c:when>
							<c:otherwise>
								<option value="${changeGroup}">${changeGroup}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
			
			<%-- attribute select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'attribute');">
					<option value="" >Any Attribute</option>
					<c:forEach var="attribute" items="${allAttributes}" >
						<c:choose>
							<c:when test="${attributeParam == attribute.key}">
								<option value="${attribute.key}" selected>${attribute.description}</option>
							</c:when>
							<c:otherwise>
								<option value="${attribute.key}">${attribute.description}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
			
			<%-- tag select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'tag');">
					<option value="" >Any Tag</option>
					<c:forEach var="tag" items="${allTags}" >
						<c:choose>
							<c:when test="${tagParam == tag.name}">
								<option value="${tag.name}" selected>${tag.description}</option>
							</c:when>
							<c:otherwise>
								<option value="${tag.name}">${tag.description}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
		</tr>
	</table>
	</tags:sectionContainer>
	<br>
	
	
	
	<%-- WRITE DEFINITIONS --%>
	<c:forEach var="info" items="${displayDefinitionsMap}">
	
		<c:set var="sectionName" value="${info.key}"/>
		
		<tags:sectionContainer title="${sectionName}">
		
			<c:forEach var="deviceInfo" items="${info.value}">
			
				<tags:boxContainer title="${deviceInfo.definition.displayName}" id="${deviceInfo.definition.type}" hideEnabled="true">
				
					<%-- BASICS --%>
					<table class="compactResultsTable">
					    <tr>
					        <td width="15%" class="label">Type:</td>
					        <td>${deviceInfo.definition.type}</td>
					    </tr>
					    <tr>
					        <td class="label">Java Constant</td>
					        <td>${deviceInfo.definition.javaConstant}</td>
					    </tr>
					    <tr>
					        <td class="label">Change Group:</td>
					        <td>${deviceInfo.definition.changeGroup}</td>
					    </tr>
					</table>
					
					<div style="padding-left:30px;">
					
					<%-- POINTS --%>
					<c:if test="${fn:length(deviceInfo.points) > 0}">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th>Points</th>
								<th>Type</th>
								<th>Init</th>
								<th>Offset</th>
								<th>Multiplier</th>
								<th>UofM</th>
								<th>State Group</th>
							</tr>
							<c:forEach var="point" items="${deviceInfo.points}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${point.pointTemplate.name}</td>
									<td><cti:msg key="${point.pointType}"/></td>
									<td>
										<c:choose>
											<c:when test="${point.init}">Init</c:when>
											<c:otherwise></c:otherwise>
										</c:choose>
									</td>
									<td>${point.pointTemplate.pointIdentifier.offset}</td>
									<td>${point.pointTemplate.multiplier}</td>
									<td>${point.uomString}</td>
									<td>${point.stateGroup}</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
					
					<%-- ATTRIBUTES --%>
					<c:if test="${fn:length(deviceInfo.attributes) > 0}">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th>Attributes</th>
								<th>Lookup</th>
								<th>Lookup Attributes</th>
							</tr>
							<c:forEach var="attribute" items="${deviceInfo.attributes}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${attribute.attribute.attribute.description}</td>
									<td>basicLookup</td>
									<td>Point = ${attribute.pointTemplateWrapper.pointTemplate.name}</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
					<%-- COMMANDS --%>
					<c:if test="${fn:length(deviceInfo.commands) > 0}">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th>Commands</th>
								<th>Command Strings</th>
								<th>Point Names</th>
							</tr>
							<c:forEach var="command" items="${deviceInfo.commands}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td valign="top">${command.commandDefinition.name}</td>
									<td valign="top">
										<ul>
											<c:forEach var="cmd" items="${command.commandDefinition.commandStringList}">
												<li>${cmd}</li>
											</c:forEach>
										</ul>
									</td>
									<td valign="top">
										<ul>
											<c:forEach var="point" items="${command.pointNames}">
												<li>${point}</li>
											</c:forEach>
										</ul>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
					<%-- TAGS --%>
					<c:if test="${fn:length(deviceInfo.tagDefinitions) > 0}">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th>Tags</th>
                                <th>Value Support</th>
                                <th>Values</th>
							</tr>
							<c:forEach var="tagDefinition" items="${deviceInfo.tagDefinitions}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${tagDefinition.tag.description}</td>
                                    <c:if test="${tagDefinition.tag.tagHasValue}">
                                    	<td>Yes</td>
                                    	<td>${tagDefinition.value}</td>
                                    </c:if>
                                    <c:if test="${not tagDefinition.tag.tagHasValue}">
                                   		<td>No</td>
                                        <td></td>
                                    </c:if>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
					</div>

				</tags:boxContainer>
				<br>
				
			
			
			</c:forEach>
			
		</tags:sectionContainer>
		

	</c:forEach>
	

</cti:standardPage>
