<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>


<cti:standardPage title="${title}" module="capcontrol">
	<%@include file="/capcontrol/cbc_inc.jspf"%>
    
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
				<div id="systemCommandLink" align="right"></div>
				<div align="right">
					<a href="javascript:void(0);" id="systemResetOpCountsLink" onclick="sendResetOpCountCommand()">Reset All Op Counters</a>
				</div>
				<br>
			</cti:checkRolesAndProperties>
		</cti:checkRolesAndProperties>
	</c:if>
	
	<c:choose>
	    <c:when test="${hasEditingRole}">
	        <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/pencil.gif"/>
	    </c:when>
	    <c:otherwise>
	        <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/information.gif"/>
	    </c:otherwise>
    </c:choose>
	
	<tags:abstractContainer type="box" hideEnabled="false" title="${title}" id="last_titled_container">
		<table id="areaTable" class="tierTable">
			<thead>
    			<tr>
                    <th>Area Name</th>
                    <th>State</th>
                    <th>Setup</th>
                    <th>Available<br> kVARS</th>
                    <th>Unavailable <br>kVARS</th>
                    <th>Closed <br>kVARS</th>
                    <th>Tripped <br>kVARS</th>
                    <th>PFactor/Est.</th>
            	</tr>
        	</thead>
        	<tbody>
        		<c:forEach var="viewableArea" items="${ccAreas}">
        			<c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>
        			<tr class="<tags:alternateRow odd="" even="altRow"/>">
	        			<td>
							<input type="image" id="showAreas${thisAreaId}" src="/capcontrol/images/nav-plus.gif"
								 onclick="showRowElems( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;" class="tierImg">
		                        <a class="tierIconLink" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisAreaId}">
		                            <img class="tierImg" src="${editInfoImage}" alt="Edit">
		                        </a>
		                        <c:if test="${hasEditingRole}">
			                        <a class="tierIconLink" href="/editor/deleteBasePAO.jsf?value=${thisAreaId}">
			                            <img class="tierImg" src="/WebConfig/yukon/Icons/delete.gif" alt="Delete">
			                        </a>
                                </c:if>
	        				<a href="/spring/capcontrol/tier/substations?areaId=${thisAreaId}&amp;isSpecialArea=${isSpecialArea}">
	        					${viewableArea.area.ccName}
	        				</a>
	        			</td>
	        			<td>
                            <capTags:warningImg paoId="${thisAreaId}" type="${updaterType}"/>
                            
                            <a id="area_state_${thisAreaId}"
		                       name="area_state"
		                       class=""
		                       href="javascript:void(0);"
		                       <c:if test="${hasAreaControl}">
									<c:choose>
										<c:when test="${isSpecialArea}"> 
											onclick="getSpecialAreaMenu('${thisAreaId}', event);" 
										</c:when>
										<c:otherwise> 
											onclick="getAreaMenu('${thisAreaId}', event);" 
										</c:otherwise>
									</c:choose>
								</c:if>>
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
						<td colspan="2" class="tableCellSnapShot">
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
						<td colspan="6" class="tableCellSnapShot"></td>
		  			</tr>
					
        		</c:forEach>
        	</tbody>
        	
        </table>

	</tags:abstractContainer>
	
	<script type="text/javascript" language="JavaScript">
		//register the event handler for the system command
		if ($('systemCommandLink')) {
		    Event.observe(window, 'load', function () { 
		        new Ajax.PeriodicalUpdater('systemCommandLink', 
		            '/spring/capcontrol/cbcAjaxController?action=updateSystemCommandMenu', {
		            method:'post', 
		            asynchronous:true, 
		            frequency: 5, 
		            onFailure: function() { $('cannonUpdaterErrorDiv').show();}
		        });
		    });
		}
	</script>  
</cti:standardPage>