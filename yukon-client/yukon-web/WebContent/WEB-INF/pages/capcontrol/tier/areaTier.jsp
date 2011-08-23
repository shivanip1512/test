<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<cti:standardPage title="${title}" module="capcontrol" page="areas">
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
	<script type="text/javascript" language="JavaScript">
		// handles analysis links (which are not functional for a substation area - show error alert)
		function loadPointChartGreyBox(title, url) {
		    alert(title + ' is not available for a Substation Area.\n\nChoose specific Substation Bus or Feeder within a Substation');
		    return void(0);
		}
	</script>
	
	<c:choose>
		<c:when test="${isSpecialArea}">
			<cti:standardMenu menuSelection="view|specialareas"/>
		</c:when>
		<c:otherwise>
			<cti:standardMenu />
		</c:otherwise>
	</c:choose>
    
	<cti:breadCrumbs>
	    <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home"/>
		<c:choose>
			<c:when test="${isSpecialArea}">
				<cti:crumbLink title="Special Substation Areas"/>
			</c:when>
			<c:otherwise>
				<cti:crumbLink title="Substation Areas"/>
			</c:otherwise>
		</c:choose>
	</cti:breadCrumbs>
	
	<c:if test="${!isSpecialArea}">
        <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
			<cti:checkRolesAndProperties value="CBC_DATABASE_EDIT" >
                <div align="right">
                    <tags:boxContainer hideEnabled="false" title="System Actions" styleClass="systemCommands">
                        <div align="left" id="systemCommandLink">
                            <tags:dynamicChoose updaterString="CAPCONTROL/SYSTEM_ENABLE_COMMAND" suffix="">
                                <tags:dynamicChooseOption optionId="enabled">
                                    <cti:labeledImg nameKey="disableSystem" href="javascript:handleSystemCommand(true)" id="systemOn"/>
                                </tags:dynamicChooseOption>
                                <tags:dynamicChooseOption optionId="disabled">
                                    <cti:labeledImg nameKey="enableSystem" href="javascript:handleSystemCommand(false)" id="systemOff"/>
                                </tags:dynamicChooseOption>
                            </tags:dynamicChoose>
                        </div>
        				<div align="left">
                            <cti:labeledImg nameKey="resetOpCount" href="javascript:sendResetOpCountCommand()" id="systemResetOpCountsLink"/>
        				</div>
                    </tags:boxContainer>
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
    
	<tags:boxContainer hideEnabled="false" title="${title}">
		<table id="areaTable" class="tierTable">
        
			<thead>
    			<tr>
                    <th>Area Name</th>
                    <th>Actions</th>
                    <th>State</th>
                    <th>Substations</th>
                    <th>Available<br> kVARS</th>
                    <th>Unavailable <br>kVARS</th>
                    <th>Closed <br>kVARS</th>
                    <th>Tripped <br>kVARS</th>
                    <th>PFactor/Est.</th>
            	</tr>
        	</thead>
            
        	<tbody>
        		<c:forEach var="viewableArea" items="${ccAreas}">
        			
                    <!-- Setup Variables -->
                    <c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>
                    
                    <c:choose>
                        <c:when test="${isSpecialArea}"> 
                            <c:set var="menuEvent" value="getSpecialAreaMenu('${thisAreaId}', event)"/> 
                        </c:when>
                        <c:otherwise> 
                            <c:set var="menuEvent" value="getAreaMenu('${thisAreaId}', event)"/> 
                        </c:otherwise>
                    </c:choose>
                    
                    <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                        <cti:param name="itemid" value="${thisAreaId}"/>
                        <cti:param name="type" value="2"/>
                    </cti:url>
                    
                    <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                        <cti:param name="value" value="${thisAreaId}"/>
                    </cti:url>
                    
                    <cti:url var="substationUrl" value="/spring/capcontrol/tier/substations">
                        <cti:param name="areaId" value="${thisAreaId}"/>
                        <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                    </cti:url>
                    
        			<tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
	        			
                        <td>
							<input type="image" id="showAreas${thisAreaId}" src="/capcontrol/images/nav-plus.gif" <c:if test="${empty viewableArea.subStations}">style="visibility: hidden;"</c:if> 
								 onclick="showRowElems( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;" class="tierImg">
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
                            <a id="area_state_${thisAreaId}" href="javascript:void(0);" <c:if test="${hasAreaControl}">onclick="${menuEvent}"</c:if>>
								<cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="STATE" />
							</a>
                            <cti:dataUpdaterCallback function="updateStateColorGenerator('area_state_${thisAreaId}')" initialize="true" value="${updaterType}/${thisAreaId}/STATE"/>
	        			</td>
                        
	        			<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="SETUP" /> Substation(s)</td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_AVAILABLE" /></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_UNAVAILABLE" /></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_CLOSED" /></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_TRIPPED" /></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="PFACTOR" /></td>
                        
					</tr>
					
					<tr>
						<td colspan="9" class="tableCellSnapShot">
						<table id="allAreas${thisAreaId}">
							<c:forEach var="station" items="${viewableArea.subStations}">
								<tr style="display: none;">
									<td><span class="smallIndent">${station.subStationName}</span></td>
									<td align="right">${station.feederCount} Feeder(s)</td>
									<td align="right">${station.capBankCount} Bank(s)</td>
								</tr>
							</c:forEach>
	  					</table>
		  				</td>
		  			</tr>
					
        		</c:forEach>
        	</tbody>
        	
        </table>

	</tags:boxContainer>
	
</cti:standardPage>