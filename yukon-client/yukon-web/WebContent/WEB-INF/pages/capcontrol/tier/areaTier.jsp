<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="areas.${type}">
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <c:if test="${hasAreaControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </c:if>
     
	<c:if test="${!isSpecialArea}">
        <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
			<cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <div class="fr">
                    <tags:boxContainer2 nameKey="systemActions" hideEnabled="false" styleClass="systemCommands padBottom">
                        <div id="systemCommandLink">
                            <tags:dynamicChoose updaterString="CAPCONTROL/SYSTEM_ENABLE_COMMAND" suffix="">
                                <tags:dynamicChooseOption optionId="enabled">
                                    <cti:button nameKey="disableSystem" renderMode="labeledImage" onclick="doSystemCommand(${systemStatusCommandId})" id="systemOn"/>
                                </tags:dynamicChooseOption>
                                <tags:dynamicChooseOption optionId="disabled">
                                    <cti:button nameKey="enableSystem" renderMode="labeledImage" onclick="doSystemCommand(${systemStatusCommandId})" id="systemOff"/>
                                </tags:dynamicChooseOption>
                            </tags:dynamicChoose>
                        </div>
        				<div>
                            <cti:button nameKey="resetOpCount" renderMode="labeledImage" onclick="doSystemCommand(${resetOpCountCommandId})" id="systemResetOpCountsLink"/>
        				</div>
                    </tags:boxContainer2>
                </div>
				<br>
			</cti:checkRolesAndProperties>
		</cti:checkRolesAndProperties>
	</c:if>
	
	<c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
    
	<tags:boxContainer2 nameKey="areasContainer" hideEnabled="false" styleClass="cr padBottom">
		<table id="areaTable" class="compactResultsTable">
        
			<thead>
    			<tr>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".actions"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".substations"/></th>
                    <th><i:inline key=".availableKvars"/></th>
                    <th><i:inline key=".unavailableKvars"/></th>
                    <th><i:inline key=".closedKvars"/></th>
                    <th><i:inline key=".trippedKvars"/></th>
                    <th><i:inline key=".pfactorEstimated"/></th>
            	</tr>
        	</thead>
            
        	<tbody>
        		<c:forEach var="viewableArea" items="${ccAreas}">
        			
                    <!-- Setup Variables -->
                    <c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>
                    
                    <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                        <cti:param name="itemid" value="${thisAreaId}"/>
                        <cti:param name="type" value="2"/>
                    </cti:url>
                    
                    <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                        <cti:param name="value" value="${thisAreaId}"/>
                    </cti:url>
                    
                    <cti:url var="substationUrl" value="/spring/capcontrol/tier/substations">
                        <cti:param name="bc_areaId" value="${thisAreaId}"/>
                        <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                    </cti:url>
                    
        			<tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
	        			
                        <td>
							<input type="image" id="showAreas${thisAreaId}" src="/capcontrol/images/nav-plus.gif" <c:if test="${empty viewableArea.subStations}">style="visibility: hidden;"</c:if> 
								 onclick="expandRow( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;" class="tierImg">
	        				<a href="${substationUrl}">
	        					<spring:escapeBody htmlEscape="true">${viewableArea.area.ccName}</spring:escapeBody>
	        				</a>
	        			</td>
                        
                        <td>
                            <cti:img nameKey="${editKey}" href="${editUrl}" styleClass="tierIconLink"/>
	                        <c:if test="${hasEditingRole}">
                                <cti:img nameKey="remove" href="${deleteUrl}" styleClass="tierIconLink"/>
                            </c:if>
                        </td>
	        			
                        <td>
                            <capTags:warningImg paoId="${thisAreaId}" type="${updaterType}"/>
                            <a id="areaState_${thisAreaId}">
								<cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="STATE" />
							</a>
                            <cti:dataUpdaterCallback function="updateStateColorGenerator('areaState_${thisAreaId}')" initialize="true" value="${updaterType}/${thisAreaId}/STATE"/>
	        			</td>
                        
	        			<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="CHILD_COUNT"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_AVAILABLE"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_UNAVAILABLE"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_CLOSED"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_TRIPPED"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="PFACTOR"/></td>
                        
					</tr>
					
					<tr>
						<td colspan="9" class="tableCellSnapShot">
						<table id="allAreas${thisAreaId}">
							<c:forEach var="station" items="${viewableArea.subStations}">
								<tr style="display: none;">
									<td><span class="smallIndent">${station.name}</span></td>
									<td align="right"><i:inline key=".feeders" arguments="${station.feederCount}"/></td>
									<td align="right"><i:inline key=".banks" arguments="${station.capBankCount}"/></td>
								</tr>
							</c:forEach>
	  					</table>
		  				</td>
		  			</tr>
					
        		</c:forEach>
        	</tbody>
        	
        </table>

	</tags:boxContainer2>
	
</cti:standardPage>