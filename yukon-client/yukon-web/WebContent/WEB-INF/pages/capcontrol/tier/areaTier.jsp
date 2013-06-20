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
                <div class="fr clearfix stacked">
                    <tags:dynamicChoose updaterString="CAPCONTROL/SYSTEM_ENABLE_COMMAND" suffix="">
                        <tags:dynamicChooseOption optionId="enabled">
                            <cti:button nameKey="disableSystem" onclick="doSystemCommand(${systemStatusCommandId})" id="systemOn" icon="icon-delete" classes="left"/>
                        </tags:dynamicChooseOption>
                        <tags:dynamicChooseOption optionId="disabled">
                            <cti:button nameKey="enableSystem" onclick="doSystemCommand(${systemStatusCommandId})" id="systemOff" icon="icon-accept" classes="left"/>
                        </tags:dynamicChooseOption>
                    </tags:dynamicChoose>
                    <cti:button nameKey="resetOpCount" onclick="doSystemCommand(${resetOpCountCommandId})" id="systemResetOpCountsLink" icon="icon-arrow-undo" classes="right"/>
                </div>
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
            <tfoot></tfoot>
            
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
                    
                    <cti:url var="substationUrl" value="/capcontrol/tier/substations">
                        <cti:param name="bc_areaId" value="${thisAreaId}"/>
                        <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                    </cti:url>
                    
        			<tr>
	        			
                        <td>
	        				<a href="${substationUrl}" class="f-has-tooltip">
	        					<spring:escapeBody htmlEscape="true">${viewableArea.area.ccName}</spring:escapeBody>
	        				</a>
                            <div class="f-tooltip dn">
                                <c:forEach var="station" items="${viewableArea.subStations}">
                                        <div class="detail fwb">${station.name}</div>
                                        <ul class="detail simple-list">
                                            <li class="fl" style="margin-left:10px;"><i:inline key=".feeders" arguments="${station.feederCount}"/></li>
                                            <li class="fl" style="margin-left:10px;"><i:inline key=".banks" arguments="${station.capBankCount}"/></li>
                                        </ul>
                                </c:forEach>
                            </div>
	        			</td>
                        
                        <td>
                            <cti:icon nameKey="${editKey}" href="${editUrl}" icon="icon-pencil"/>
	                        <c:if test="${hasEditingRole}">
                                <cti:icon nameKey="remove" href="${deleteUrl}" icon="icon-cross"/>
                            </c:if>
                        </td>
	        			
                        <td>
                            <capTags:warningImg paoId="${thisAreaId}" type="${updaterType}"/>
                            <c:if test="${hasAreaControl}"><a id="areaState_${thisAreaId}" href="javascript:void(0);"></c:if>
                            <c:if test="${!hasAreaControl}"><span id="areaState_${thisAreaId}"></c:if>
								<cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="STATE" />
							<c:if test="${hasAreaControl}"></a></c:if>
							<c:if test="${!hasAreaControl}"></span></c:if>
                            <cti:dataUpdaterCallback function="updateStateColorGenerator('areaState_${thisAreaId}')" initialize="true" value="${updaterType}/${thisAreaId}/STATE"/>
	        			</td>
                        
	        			<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="CHILD_COUNT"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_AVAILABLE"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_UNAVAILABLE"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_CLOSED"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_TRIPPED"/></td>
						<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="PFACTOR"/></td>
                        
					</tr>
					
        		</c:forEach>
        	</tbody>
        	
        </table>

	</tags:boxContainer2>
	
</cti:standardPage>