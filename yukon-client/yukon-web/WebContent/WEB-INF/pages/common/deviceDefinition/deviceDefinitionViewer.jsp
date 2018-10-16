<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="deviceDef">
	<c:url var="url" value="/common/deviceDefinition.xml"/>
	<c:url var="url" value="/common/deviceDefinition.xml"/>
	
	<style type="text/css">
		ul {margin:0 0 0 1em;padding: 0;}
		li {margin:0;padding:0;list-style-type:circle;}
        .filterOption {margin-right: 15px; margin-bottom: 15px;}
	</style>
    <cti:msg2 var="noneText" key="yukon.common.none.choice"/>
    
	<%-- WRITE SELECT --%>
    <cti:msgScope paths=".filters">
	<tags:sectionContainer2 nameKey="title">
    <div>
    <cti:url var="detailUrl" value="/common/deviceDefinition.xml"/>
    <form:form id="filterForm" action="${detailUrl}" method="GET" modelAttribute="definitionsFilter">
        <div class="dib filterOption">
            <div>
                <strong><i:inline key=".deviceType"/></strong>
            </div>
            <cti:msg2 var="allText" key=".allDefinitions"/>
            <tags:selectWithItems id="deviceType" inputClass="deviceDefinition-select" path="deviceType" useKeyLabel="true" groupItems="true" 
            items="${allDeviceTypes}" itemValue="type" itemLabel="${fn:escapeXml(definition.displayName)}" defaultItemLabel="${noneText}" allKeyLabel="${allText}"/>
        </div>
        <div class="dib filterOption">
            <div>
                <strong><i:inline key=".displayGroup"/></strong>
            </div>
            <cti:msg2 var="allText" key=".anyDisplayGroup"/>
            <tags:selectWithItems id="displayGroup" inputClass="deviceDefinition-select" path="displayGroup" items="${allDisplayGroups}" allKeyLabel="${allText}" defaultItemLabel="${noneText}" />
        </div>
        <div class="dib filterOption">
            <div>
                <strong><i:inline key=".changeGroup"/></strong>
            </div>
            <cti:msg2 var="allText" key=".anyChangeGroup"/>
            <tags:selectWithItems id="changeGroup" inputClass="deviceDefinition-select" path="changeGroup" items="${allChangeGroups}" allKeyLabel="${allText}" defaultItemLabel="${noneText}" />
        </div>
        
        <div class="dib filterOption">
            <div>
                <strong><i:inline key=".attribute"/></strong>
            </div>
            <cti:msg2 var="allText" key=".anyAttribute"/>
            <tags:selectWithItems id="attribute" inputClass="deviceDefinition-select" path="attribute" items="${allAttributes}" allKeyLabel="${allText}" defaultItemLabel="${noneText}" />
        </div>
        
        <div class="dib filterOption">
            <div>
                <strong><i:inline key=".tag"/></strong>
            </div>
            <cti:msg2 var="allText" key=".anyTag"/>
            <tags:selectWithItems id="tag" inputClass="deviceDefinition-select" path="tag" items="${allTags}" itemLabel="description" allKeyLabel="${allText}" defaultItemLabel="${noneText}" />
        </div>
        </form:form>
    </div>
	</tags:sectionContainer2>
    </cti:msgScope>
	
	<%-- WRITE DEFINITIONS --%>
    <cti:msgScope paths=".info">
	<c:forEach var="info" items="${displayDefinitionsMap}">
	
		<c:set var="sectionName" value="${info.key}"/>
		
		<tags:sectionContainer title="${sectionName}">
		
			<c:forEach var="deviceInfo" items="${info.value}">
			
				<tags:boxContainer title="${fn:escapeXml(deviceInfo.definition.displayName)}" id="${deviceInfo.definition.type}" hideEnabled="true">
				
					<%-- BASICS --%>
					<table class="compact-results-table">
					    <tr>
					        <td width="15%"><i:inline key=".type"/></td>
					        <td>${deviceInfo.definition.type}</td>
					    </tr>
					    <tr>
					        <td><i:inline key=".changeGroup"/></td>
					        <td>${deviceInfo.definition.changeGroup}</td>
					    </tr>
					</table>
					
					<div style="padding-left:30px;">
					
					<%-- POINTS --%>
					<c:if test="${fn:length(deviceInfo.points) > 0}">
                    <cti:msgScope paths=".points">
						<br>
						<table class="results-table" style="width:900px">
                            <thead>
    							<tr>
								<th><i:inline key=".index"/></th>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".type"/></th>
								<th><i:inline key=".init"/></th>
								<th><i:inline key=".offset"/></th>
								<th><i:inline key=".multiplier"/></th>
								<th><i:inline key=".uofm"/></th>
								<th><i:inline key=".stateGroup"/></th>
    							</tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
    							<c:forEach var="point" items="${deviceInfo.points}" varStatus="status">
    								<tr>
    									<td>${status.index +1}</td>
    									<td>${fn:escapeXml(point.pointTemplate.name)}</td>
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
                            </tbody>
						</table>
                    </cti:msgScope>
					</c:if>
					
					<%-- ATTRIBUTES --%>
					<c:if test="${fn:length(deviceInfo.attributes) > 0}">
                    <cti:msgScope paths=".attributes">
						<br>
						<table class="results-table" style="width:900px">
                            <thead>
    							<tr>
								<th><i:inline key=".index"/></th>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".lookup"/></th>
								<th><i:inline key=".lookupAttributes"/></th>
    							</tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
    							<c:forEach var="attribute" items="${deviceInfo.attributes}" varStatus="status">
    								<tr>
                                        <td>${status.index +1}</td>
    									<td><cti:msg2 key="${attribute.attribute.attribute}"/></td>
									<td><i:inline key=".lookup.default"/></td>
									<td><i:inline key=".lookupAttributes.point"/> = ${fn:escapeXml(attribute.pointTemplateWrapper.pointTemplate.name)}</td>
    								</tr>
    							</c:forEach>
                            </tbody>
						</table>
                    </cti:msgScope>
					</c:if>
					
					<%-- COMMANDS --%>
					<c:if test="${fn:length(deviceInfo.commands) > 0}">
                    <cti:msgScope paths=".commands">
						<br>
						<table class="results-table" style="width:900px">
                            <thead>
    							<tr>
								<th><i:inline key=".index"/></th>
								<th><i:inline key=".name"/></th>
								<th><i:inline key=".commandStrings"/></th>
								<th><i:inline key=".pointNames"/></th>
    							</tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
    							<c:forEach var="command" items="${deviceInfo.commands}" varStatus="status">
    								<tr>
    									<td valign="top">${status.index +1}</td>
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
                            </tbody>
						</table>
                    </cti:msgScope>
					</c:if>
					
					<%-- TAGS --%>
					<c:if test="${fn:length(deviceInfo.tagDefinitions) > 0}">
                    <cti:msgScope paths=".tags">
						<br>
						<table class="results-table" style="width:900px">
                            <thead>
    							<tr>
								<th><i:inline key=".index"/></th>
								<th><i:inline key=".name"/></th>
                                <th><i:inline key=".valueSupport"/></th>
                                <th><i:inline key=".values"/></th>
    							</tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
    							<c:forEach var="tagDefinition" items="${deviceInfo.tagDefinitions}" varStatus="status">
    								<tr>
    									<td>${status.index +1}</td>
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
                            </tbody>
						</table>
                    </cti:msgScope>
					</c:if>
					
					</div>

				</tags:boxContainer>
			</c:forEach>
		</tags:sectionContainer>
	</c:forEach>
	</cti:msgScope>
    <cti:includeScript link="/resources/js/pages/yukon.support.deviceDefinitions.js"/>
</cti:standardPage>