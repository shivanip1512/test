<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
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
	
	<cti:standardMenu/>
	<!-- DIV element for the non flyover type popups -->
	<ct:simplePopup onClose="closeTierPopup()" title="Comments:" id="tierPopup" styleClass="thinBorder">
        <div id="popupBody"></div>
    </ct:simplePopup>
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
		<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
			<div id="systemCommandLink" align="right"></div>
			<div align="right">
				<a href="javascript:void(0);" id="systemResetOpCountsLink" align="right" onclick="sendResetOpCountCommand()">Reset All Op Counters</a>
			</div>
			<br>
		</cti:checkProperty>
	</c:if>
	
	<c:choose>
	    <c:when test="${hasEditingRole}">
	        <c:set var="editInfoImage" value="/editor/images/edit_item.gif"/>
	    </c:when>
	    <c:otherwise>
	        <c:set var="editInfoImage" value="/editor/images/info_item.gif"/>
	    </c:otherwise>
    </c:choose>
	
	<cti:titledContainer title="${title}" id="last_titled_container">
		<form id="areaForm" action="" method="get">
		<input type="hidden" name="CCSessionInfo.STR_CC_AREA" />
		<input type="hidden" name="CCSessionInfo.STR_CC_AREAID"/>
        <div>
			<table id="areaTable" width="100%" border="0" cellspacing="0" cellpadding="0" >
			<thead>
			<tr class="columnHeader lAlign">                
                <th>Area Name</th>
                <th width="2%"></th>
                <th>State</th>
                <th>Setup</th>
                <th>Available<br/> kVARS</th>
                <th>Unavailable <br/>kVARS</th>
                <th>Closed <br/>kVARS</th>
                <th>Tripped <br/>kVARS</th>
                <th>PFactor/Est.</th>
        	</tr>
        	</thead>
        	<tbody>
        		<c:forEach var="viewableArea" items="${ccAreas}">
        			<c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>
        			<tr class="<ct:alternateRow odd="altRow" even=""/>">
	        			<td>
	        				<input type="checkbox" name="cti_chkbxAreas" value="${thisAreaId}"/>
							<input type="image" id="showAreas${thisAreaId}" src="/capcontrol/images/nav-plus.gif"
								 onclick="showRowElems( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;"/>
		                        <a class="editImg" href="/editor/cbcBase.jsf?type=2&itemid=${thisAreaId}&ignoreBookmark=true">
		                            <img class="rAlign editImg" src="${editInfoImage}"/>
		                        </a>
		                        <c:if test="${hasEditingRole}">
			                        <a class="editImg" href="/editor/deleteBasePAO.jsf?value=${thisAreaId}">
			                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
			                        </a>
                                </c:if>
	        				<a href="/spring/capcontrol/tier/substations?areaId=${thisAreaId}&isSpecialArea=${isSpecialArea}">
	        					<c:out value="${viewableArea.area.ccName}"/>
	        				</a>
	        			</td>
	        			<td>
	        				<capTags:warningImg paoId="${thisAreaId}" type="${updaterType}"/>
	        			</td>
	        			<td>
	
						    <a id="area_state_${thisAreaId}"
		                       name="area_state"
		                       class=""
		                       href="javascript:void(0);" 
							   onclick="getAreaMenu('${thisAreaId}', event);">
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
						<td colspan="3">
						<table id="allAreas${thisAreaId}" width="100%" border="0" cellspacing="0" cellpadding="0">
							<c:forEach var="station" items="${viewableArea.subStations}">
								<tr class="<ct:alternateRow odd="altRow" even="" skipToggle="true"/>" style="display: none;">
									<td><font class="lIndent">${station.subStationName}</font></td>
									<td align="right">${station.feederCount} Feeder(s)</td>
									<td align="right">${station.capBankCount} Bank(s)</td>
								</tr>
							</c:forEach>
	  					</table>
		  				</td>
						<td class="${css}" colspan="6"></td>
		  			</tr>
					
					
        		</c:forEach>
        	</tbody>
        	
        </table>
        </div>
        </form>

	</cti:titledContainer>
	
	<capTags:commandMsgDiv/>

    <ct:disableUpdaterHighlights/>
	
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