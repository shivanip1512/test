<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>

<cti:standardPage module="capcontrol" page="areas.${type}">
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <c:if test="${hasAreaControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </c:if>
     
	<tags:boxContainer2 nameKey="areasContainer" hideEnabled="false">
		<table id="areaTable" class="compactResultsTable">
        
			<thead>
    			<tr>
                    <th width="16px">&nbsp;</th>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".substations"/></th>
                    <th class="tar"><i:inline key=".availableKvars"/></th>
                    <th class="tar"><i:inline key=".unavailableKvars"/></th>
                    <th class="tar"><i:inline key=".closedKvars"/></th>
                    <th class="tar"><i:inline key=".trippedKvars"/></th>
                    <th class="tar"><i:inline key=".pfactorEstimated"/></th>
            	</tr>
        	</thead>
            <tfoot></tfoot>
            
        	<tbody>
        		<c:forEach var="viewableArea" items="${ccAreas}">
        			
                    <!-- Setup Variables -->
                    <c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>
                    
                    <cti:url var="substationUrl" value="/capcontrol/tier/substations">
                        <cti:param name="bc_areaId" value="${thisAreaId}"/>
                        <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                    </cti:url>
                    
        			<tr>
                        <td>
                            <capTags:warningImg paoId="${thisAreaId}" type="${updaterType}"/>
                        </td>
                        <td>
                            <div class="f-tooltip dn">
                                <c:forEach var="station" items="${viewableArea.subStations}">
                                        <div class="detail fwb">${station.name}</div>
                                        <ul class="detail simple-list">
                                            <li class="fl" style="margin-left:10px;"><i:inline key=".feeders" arguments="${station.feederCount}"/></li>
                                            <li class="fl" style="margin-left:10px;"><i:inline key=".banks" arguments="${station.capBankCount}"/></li>
                                        </ul>
                                </c:forEach>
                            </div>
	        				<a href="${substationUrl}" class="f-has-tooltip">${fn:escapeXml(viewableArea.area.ccName)}</a>
	        			</td>
                        
                        <td>
                            <c:if test="${hasAreaControl}"><a id="areaState_${thisAreaId}" href="javascript:void(0);" class="subtle-link"></c:if>
                            <c:if test="${!hasAreaControl}"><span id="areaState_${thisAreaId}"></c:if>
                                <span id="areaState_box_${thisAreaId}" class="box stateBox">&nbsp;</span>
								<cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="STATE" />
							<c:if test="${hasAreaControl}"></a></c:if>
							<c:if test="${!hasAreaControl}"></span></c:if>
                            <cti:dataUpdaterCallback function="updateStateColorGenerator('areaState_box_${thisAreaId}')" initialize="true" value="${updaterType}/${thisAreaId}/STATE"/>
	        			</td>
	        			<td><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="CHILD_COUNT"/></td>
						<td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_AVAILABLE"/></td>
						<td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_UNAVAILABLE"/></td>
						<td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_CLOSED"/></td>
						<td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="KVARS_TRIPPED"/></td>
						<td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${updaterType}" format="PFACTOR"/></td>
                        
					</tr>
					
        		</c:forEach>
        	</tbody>
        	
        </table>

	</tags:boxContainer2>

</cti:standardPage>