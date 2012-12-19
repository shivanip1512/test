<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="deviceDef">
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
    <cti:msgScope paths=".filters">
    <cti:msg2 var="filtersTitle" key=".title"/>
	<tags:sectionContainer title="${filtersTitle}">
	<table class="miniResultsTable" style="width:75%">
		<tr>
            <cti:msgScope paths=".column">
			<th><i:inline key=".deviceType"/></th>
			<th><i:inline key=".displayGroup"/></th>
			<th><i:inline key=".changeGroup"/></th>
			<th><i:inline key=".attribute"/></th>
			<th><i:inline key=".tag"/></th>
            </cti:msgScope>
		</tr>
		<tr>
			<%-- device type select --%>
			<td>
				<select onchange="doDefinitionFilter(this, 'deviceType');">
					<option value="" ><cti:msg2 key=".allDefinitions"/></option>
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
					<option value="" ><cti:msg2 key=".anyDisplayGroup"/></option>
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
					<option value="" ><cti:msg2 key=".anyChangeGroup"/></option>
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
					<option value="" ><cti:msg2 key=".anyAttribute"/></option>
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
					<option value="" ><cti:msg2 key=".anyTag"/></option>
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
    </cti:msgScope>
	<br>
	
	
	
	<%-- WRITE DEFINITIONS --%>
    <cti:msgScope paths=".info">
	<c:forEach var="info" items="${displayDefinitionsMap}">
	
		<c:set var="sectionName" value="${info.key}"/>
		
		<tags:sectionContainer title="${sectionName}">
		
			<c:forEach var="deviceInfo" items="${info.value}">
			
				<tags:boxContainer title="${deviceInfo.definition.displayName}" id="${deviceInfo.definition.type}" hideEnabled="true">
				
					<%-- BASICS --%>
					<table class="compactResultsTable">
					    <tr>
					        <td width="15%" class="label"><i:inline key=".type"/></td>
					        <td>${deviceInfo.definition.type}</td>
					    </tr>
					    <tr>
					        <td class="label"><i:inline key=".javaConstant"/></td>
					        <td>${deviceInfo.definition.javaConstant}</td>
					    </tr>
					    <tr>
					        <td class="label"><i:inline key=".changeGroup"/></td>
					        <td>${deviceInfo.definition.changeGroup}</td>
					    </tr>
					</table>
					
					<div style="padding-left:30px;">
					
					<%-- POINTS --%>
					<c:if test="${fn:length(deviceInfo.points) > 0}">
                    <cti:msgScope paths=".points">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".type"/></th>
								<th><i:inline key=".init"/></th>
								<th><i:inline key=".offset"/></th>
								<th><i:inline key=".multiplier"/></th>
								<th><i:inline key=".uofm"/></th>
								<th><i:inline key=".stateGroup"/></th>
							</tr>
							<c:forEach var="point" items="${deviceInfo.points}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${point.pointTemplate.name}</td>
									<td><cti:msg key="${point.pointType}"/></td>
									<td>
										<c:choose>
											<c:when test="${point.init}"><i:inline key=".init.value"/></c:when>
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
                    </cti:msgScope>
					</c:if>
					
					<%-- ATTRIBUTES --%>
					<c:if test="${fn:length(deviceInfo.attributes) > 0}">
                    <cti:msgScope paths=".attributes">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".lookup"/></th>
								<th><i:inline key=".lookupAttributes"/></th>
							</tr>
							<c:forEach var="attribute" items="${deviceInfo.attributes}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${attribute.attribute.attribute.description}</td>
									<td><i:inline key=".lookup.default"/></td>
									<td><i:inline key=".lookupAttributes.point"/> = ${attribute.pointTemplateWrapper.pointTemplate.name}</td>
								</tr>
							</c:forEach>
						</table>
                    </cti:msgScope>
					</c:if>
					
					<%-- COMMANDS --%>
					<c:if test="${fn:length(deviceInfo.commands) > 0}">
                    <cti:msgScope paths=".commands">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".commandStrings"/></th>
								<th><i:inline key=".pointNames"/></th>
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
                    </cti:msgScope>
					</c:if>
					
					<%-- TAGS --%>
					<c:if test="${fn:length(deviceInfo.tagDefinitions) > 0}">
                    <cti:msgScope paths=".tags">
						<br>
						<table class="miniResultsTable" style="width:900px">
							<tr>
								<th><i:inline key=".name"/></th>
                                <th><i:inline key=".valueSupport"/></th>
                                <th><i:inline key=".values"/></th>
							</tr>
							<c:forEach var="tagDefinition" items="${deviceInfo.tagDefinitions}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td>${tagDefinition.tag.description}</td>
                                    <c:if test="${tagDefinition.tag.tagHasValue}">
                                    	<td><i:inline key=".valueSupport.yes"/></td>
                                    	<td>${tagDefinition.value}</td>
                                    </c:if>
                                    <c:if test="${not tagDefinition.tag.tagHasValue}">
                                   		<td><i:inline key=".valueSupport.no"/></td>
                                        <td></td>
                                    </c:if>
								</tr>
							</c:forEach>
						</table>
                    </cti:msgScope>
					</c:if>
					
					</div>

				</tags:boxContainer>
				<br>
				
			
			
			</c:forEach>
			
		</tags:sectionContainer>
		

	</c:forEach>
	</cti:msgScope>

</cti:standardPage>
