<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<cti:standardPage title="${substation.ccName}" module="capcontrol">
    <%@include file="/capcontrol/cbc_inc.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <cti:url var="onelineCBCServlet" value="/capcontrol/oneline/OnelineCBCServlet"/>
    
    <cti:standardMenu/>
    
    <cti:url value="/spring/capcontrol/tier/substations" var="substationAddress">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
    <cti:breadCrumbs>
    	<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
    	<c:choose>
    		<c:when test="${isSpecialArea}">
    		  	<cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=${isSpecialArea}" title="Special Substation Areas" />
    		</c:when>
    		<c:otherwise>
    			<cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=${isSpecialArea}" title="Substation Areas" />
    		</c:otherwise>
    	</c:choose>
    
        <cti:crumbLink url="${substationAddress}" title="${areaName}" />
        <cti:crumbLink title="${substation.ccName}" />	
    
    </cti:breadCrumbs>
    
    <script type="text/javascript">
        Event.observe(window, 'load', checkPageExpire);
        
        // These two functions are neccessary since IE does not support css :hover
        function highLightRow(row) {
            row = $(row);
            row.addClassName('hover');
        }
        
        function unHighLightRow(row){
            row = $(row);
            row.removeClassName('hover');
        }   
        
        // gathers ids of selected subbuses and feeders, appends to url as target param
        // triggers call to greybox containing point chart(s)
        function loadPointChartGreyBox(title, url) {
            
            var elemBanks = document.getElementsByName('cti_chkbxBanks');
            var bankElems = new Array();
            getValidChecks( elemBanks, bankElems );
            if (bankElems.length > 0) {
                alert(title + ' is not available for a Capacitor Bank.\n\nChoose specific Substation Bus or Feeder within a Substation');
                return void(0);
            }
            
            var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
            var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
            var validElems = new Array();
            getValidChecks( elemSubs, validElems );
            getValidChecks( elemFdrs, validElems );
            
            var targets = new Array()
            for (var i = 0; i < validElems.length; i++) {
                targets.push(validElems[i].getAttribute('value'))
            }
            
            url += '&targets=' + targets.join(',');
            
            GB_showFullScreen(title, url, null);
            return void(0);
        }
    
    	//returned when a cap bank menu is triggered to appear
    	function popupWithHiLite (html, width, height, offsetx, offsety, rowID, color) {
    		return overlib (html, STICKY, WIDTH, width, HEIGHT, height, OFFSETX, offsetx, OFFSETY, offsety, MOUSEOFF, FULLHTML, ABOVE);
    	}
    	
       	function onGreyBoxClose () {
       		window.location.href = window.location.href;
       	}
    
     </script>
    
    <input type="hidden" id="lastAccessedID" value="${lastAccessed}">
    <input type="hidden" id="fullURL" value="${fullURL}">
    <input type="hidden" id="paoId_${substation.ccId}" value="${substation.ccId}">
    
    <c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/pencil.gif"/>
        </c:when>
        <c:otherwise>
            <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/information.gif"/>
        </c:otherwise>
    </c:choose>
    
	<tags:abstractContainer type="box" title="Substation" hideEnabled="false">
		<table id="substationTable" class="tierTable">
        
			<tr>
				<th>Substation Name</th>
				<th>State</th>
			</tr>
            
            <tr class="tableCell" id="tr_substation_${substation.ccId}">
            
                <td id="anc_${thisSubStationId}">
                    <a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${substation.ccId}" class="tierIconLink">
                        <img alt="Edit" class="tierImg" src="${editInfoImage}">
                    </a>
                    <c:if test="${hasEditingRole}">
	                    <a title="Delete" href="/editor/deleteBasePAO.jsf?value=${substation.ccId}" class="tierIconLink">
	                        <img alt="Delete" class="tierImg" src="/WebConfig/yukon/Icons/delete.gif">
	                    </a>
                    </c:if>
                	<a title="Bank Locations" href="/spring/capcontrol/capbank/capBankLocations?value=${substation.ccId}&amp;specialArea=${isSpecialArea}" class="tierIconLink">
                        <img alt="Location" class="tierImg" src="/WebConfig/yukon/Icons/interstate.gif">
                    </a>
                    ${substation.ccName}
                    <font color="red">
                        <cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="SA_ENABLED" />
                    </font>
                </td>
                
                <td>
                	<capTags:warningImg paoId="${substation.ccId}" type="SUBSTATION"/>
                    <a id="substation_state_${substation.ccId}"
                        <c:if test="${hasSubstationControl}">
                            href="javascript:void(0);"
                            ${popupEvent}="getSubstationMenu('${substation.ccId}', event);"
                        </c:if> 
                    >
                        <cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="STATE" />
                    </a>
                    <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${substation.ccId}')" 
                        initialize="true" value="SUBSTATION/${substation.ccId}/STATE"/>
                </td>
                
            </tr>

		</table>
	</tags:abstractContainer>
	
	<br>

	<tags:abstractContainer type="box" title="Substation Bus" hideEnabled="true" showInitially="true">

		<table id="subBusTable" class="tierTable rowHighlighting">
			<tr>
				<th><input type="checkbox" name="chkAllSubBusesBx" onclick="checkAll(this, 'cti_chkbxSubBuses');" class="tierCheckBox">
				    <select id='subBusFilter' onchange='applySubBusFilter(this);'>
    				    <option>All SubBuses</option>
    				    <c:forEach var="sub" items="${subBusList}" >
    					   <option>${sub.subBus.ccName}</option>
    				    </c:forEach>
				    </select>
				</th>
				<th>State</th>
				<th>Target</th>
				<th>kVAR Load / Est.</th>
				<th>Date/Time</th>
				<th>PFactor / Est.</th>
				<th>kW / Volts</th>
				<th>Daily / Max Ops</th>
                <th>LTC</th>
			</tr>

            <c:set var="subBusRowCount" value="0"/>
            
            <c:forEach var="viewableSubBus" items="${subBusList}">
    			<c:set var="thisSubBusId" value="${viewableSubBus.subBus.ccId}"/>
                <c:set var="subBusRowClass" value="tableCell"/>
                    <c:choose>
                        <c:when test="${subBusRowCount % 2 == 0}">
                            <c:set var="subBusRowClass" value="tableCell"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="subBusRowClass" value="altTableCell"/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="subBusRowCount" value="${subBusRowCount + 1}"/>
    			<tr class="${subBusRowClass}"  id="tr_sub_${thisSubBusId}">
    				
                    <td id="anc_${thisSubBusId}">
                        <span style="display:none;">${viewableSubBus.subBus.ccName}</span>
                        <input type="hidden" id="paoId_${viewableSubBus.subBus.ccId}" value="${thisSubBusId}">
                        <input type="checkbox" name="cti_chkbxSubBuses" value="${thisSubBusId}" class="tierCheckBox">
    					<input type="image" id="showSnap${thisSubBusId}" src="/capcontrol/images/nav-plus.gif" class="tierImg"
    					   onclick="showRowElems( 'subSnapShot${thisSubBusId}', 'showSnap${thisSubBusId}'); return false;">
    				
    				    <a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisSubBusId}" class="tierIconLink">
                            <img alt="Edit" class="tierImg" src="${editInfoImage}">
                        </a>
    	                <c:if test="${hasEditingRole}">
    	                    <a title="Delete" href="/editor/deleteBasePAO.jsf?value=${thisSubBusId}" class="tierIconLink">
    	                        <img alt="Delete" class="tierImg" src="/WebConfig/yukon/Icons/delete.gif">
    	                    </a>
    	                </c:if>
                       	<a title="Bank Locations" href="/spring/capcontrol/capbank/capBankLocations?value=${thisSubBusId}&amp;specialArea=${isSpecialArea}" class="tierIconLink">
                            <img alt="Location" class="tierImg" src="/WebConfig/yukon/Icons/interstate.gif">
                        </a>
                        <a 
                            <c:if test="${!hideOneLine}">
    				            href="${onelineCBCServlet}?id=${thisSubBusId}&amp;redirectURL=${fullURL}" title="Click to view One-Line"
                            </c:if>
                        >
                        ${viewableSubBus.subBus.ccName}
                        </a>
                        <a href="/spring/capcontrol/tier/feeders?isSpecialArea=${isSpecialArea}&amp;subStationId=${viewableSubBus.alternateStationId}&amp;areaId=${viewableSubBus.alternateAreaId}" class="tierIconLink">
                        	<capTags:dualBusImg paoId="${thisSubBusId}" type="SUBBUS"/>
                        </a>
                        <capTags:verificationImg paoId="${thisSubBusId}" type="SUBBUS"/>
    				</td>
                    
    				<td>
                        <capTags:warningImg paoId="${thisSubBusId}" type="SUBBUS"/>
    
    					<a id="subbus_state_${thisSubBusId}"
        				    <c:if test="${hasSubBusControl}">
    						  href="javascript:void(0);" ${popupEvent}="getSubBusMenu('${thisSubBusId}', event);"  
    				        </c:if>
    				    >
                            <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="STATE" />
    					</a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('subbus_state_${thisSubBusId}')" 
                            initialize="true" value="SUBBUS/${thisSubBusId}/STATE"/>
    				</td>
                    
    				<td>
                        <c:set var="isPowerFactorControlled" value="${viewableSubBus.subBus.powerFactorControlled}"/>
                        <c:choose>
                            <c:when test="${viewableSubBus.busControlled}">
    		                    <a onmouseover="showDynamicPopupAbove($('subPFPopup_${thisSubBusId}_${isPowerFactorControlled}'))"
    								onmouseout="nd();">
									<c:choose>
										<c:when test="${viewableSubBus.ivvcControlled}">
			                            	<span style='font-weight:bold;font-size:11px;'>U:</span>
			                            	<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_PEAKLEAD"/>
			                            	<span style='font-weight:bold;font-size:11px;'> L:</span>
			                            	<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_PEAKLAG"/>
			                            	<span style='font-weight:bold;font-size:11px;'> PF:</span>
			                            	<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_CLOSEOPENPERCENT"/>
			                            </c:when>
			                            <c:otherwise>
	    									<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET"/>
	    								</c:otherwise>
    								</c:choose>
    							</a>
                            </c:when>
                            <c:otherwise>
                                <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET"/>
                            </c:otherwise>
    					</c:choose>
    					<div class="ccPFPopup" id="subPFPopup_${thisSubBusId}_${isPowerFactorControlled}" style="display: none;" >
                            <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_MESSAGE"/>     
    					</div>
    				</td>
    				<td>
    					<c:choose>
    						<c:when test="${viewableSubBus.subBus.usePhaseData}">
    							<a onmouseover="showDynamicPopup($('subVarLoadPopup_${thisSubBusId}'));" 
    							onmouseout="nd();">
									<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD"/>
			                        <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/KVAR_LOAD_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
			                        <span> / </span> 
			                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD_EST"/> 
    		                    </a>
    						</c:when>
    						<c:otherwise>
    							<a>
									<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD"/>
			                        <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/KVAR_LOAD_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
			                        <span> / </span> 
			                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD_EST"/>
    		                    </a>
    						</c:otherwise>
    					</c:choose>
    				    <div class="ccVarLoadPopup" id="subVarLoadPopup_${thisSubBusId}" style="display: none;" > 
                            <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD_MESSAGE"/>
    				    </div>
    				</td>
    				<td>
                        <a id="dateTime_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="DATE_TIME"/></a>
    				</td>
    				<td>
                        <a id="pFactor_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="PFACTOR"/></a>
    				</td>
    				<td>
                        <a id="kwVolts_${thisSubBusId}">
                        	<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KW"/>
                        	<cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/WATT_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
                        	/
                        	<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="VOLTS"/>
                        	<cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/VOLT_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
                        </a>
    				</td>
    				<td>
                        <a id="dailyMaxOps_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="DAILY_MAX_OPS"/></a>
    				</td>
                    <td>
                        <c:choose>
                            <c:when test="${viewableSubBus.ltcId != null && viewableSubBus.ltcId > 0}">
                                <capTags:ltcTapIndicator paoId="${viewableSubBus.ltcId}" type="LTC"/>
                                <capTags:ltcModeIndicator paoId="${viewableSubBus.ltcId}" type="LTC"/>
                                
                                <cti:url value="/spring/capcontrol/ivvc/bus/detail" var="ivvcBusViewLink">
    				    			<cti:param name="subBusId" value="${thisSubBusId}"/>
    				    			<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
   				    			</cti:url>
                                
                                <a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${viewableSubBus.ltcId}" class="tierIconLink">
                                    <img alt="Edit" class="tierImg" src="${editInfoImage}">
                                </a>
                                <a id="ltcName_${viewableSubBus.ltcId}" href="${ivvcBusViewLink}">
                                ${viewableSubBus.ltcName}
                                </a>
                                <a href="javascript:getLtcPointsList(${viewableSubBus.ltcId})" class="tierIconLink">
                                    <img alt="View Points" class="tierImg magnifierImg" src="/WebConfig/yukon/Icons/magnifier.gif" 
                                        onmouseover="statusMsgAbove(this,'Click here to see the point values for the ltc device.');">
                               </a>
                            </c:when>
                            <c:otherwise>
                                ---
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
    			</tr>
    			<tr class="tableCellSnapShot">
    				<td colspan="9" class="tableCellSnapShot">
        				<table id="subSnapShot${thisSubBusId}">
        				
        			        <tr class="tableCellSnapShot" style="display: none;">
        				        <td colspan="2">
        				            <span class="snapShotSubLabel">Substation Info</span>
        				        </td>	
        			        </tr>
        			        <tr class="tableCellSnapShot" style="display: none;">
        				        <td><span class="smallIndent">Area: </span></td>
        	                    <td>
                                    <span class="smallIndent">${areaName}</span>
        		                    <span class="errorRed"><cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="SA_ENABLED" /></span>
        				        </td>
        					</tr>
        			        <tr class="tableCellSnapShot" style="display: none;">
        				        <td><span class="smallIndent">Control Method: </span></td>
        				        <td>
                                    <span class="smallIndent">${viewableSubBus.subBus.controlMethod} (${viewableSubBus.subBus.controlUnits})</span>
                                </td>
        					</tr>
        			        <tr class="tableCellSnapShot" style="display: none;">
        				        <td><span class="smallIndent">Var Point: </span></td>
        				        <td>
                                    <span class="smallIndent">
                				        <c:choose>
                					        <c:when test="${viewableSubBus.varPoint != null}">${viewableSubBus.varPoint.pointName}</c:when>
                					        <c:otherwise>(none)</c:otherwise>
                				        </c:choose>
        				            </span>
                                </td>
        					</tr>
        				    <tr class="tableCellSnapShot" style="display: none;">
                                <td><span class="smallIndent">Watt Point: </span></td>
                                <td>
                                    <span class="smallIndent">
        			        	    <c:choose>
        					           <c:when test="${viewableSubBus.wattPoint != null}">${viewableSubBus.wattPoint.pointName}</c:when>
        					           <c:otherwise>(none)</c:otherwise>
        				            </c:choose>
                                    </span>
                                </td>
        					</tr>
        				    <tr class="tableCellSnapShot" style="display: none;">
                                <td><span class="smallIndent">Volt Point: </span></td>
                                <td><span class="smallIndent">
            			        	<c:choose>
            					        <c:when test="${viewableSubBus.voltPoint != null}">${viewableSubBus.voltPoint.pointName}</c:when>
            					        <c:otherwise>(none)</c:otherwise>
            				        </c:choose>
                                    </span>
                                </td>
        			        </tr>
        				
        				</table>
    				</td>
    			</tr>
    
    		</c:forEach>
		</table>
	</tags:abstractContainer>

	<br>

	<tags:abstractContainer type="box" title="Feeders" hideEnabled="true" showInitially="true">

		<table id="fdrTable" class="tierTable rowHighlighting">
        	<tr>
         		<th><input type="checkbox" name="chkAllFdrsBx" onclick="checkAll(this, 'cti_chkbxFdrs');" class="tierCheckBox">
         			<select id='feederFilter' onchange='applyFeederSelectFilter(this);'>
						<option>All Feeders</option>
						<c:forEach var="feeder" items="${feederList}">
							<option>${feeder.feeder.ccName}</option>
						</c:forEach>
					</select>
				</th>
         		<th>State</th>
         		<th>Target</th>
         		<th>kVAR Load / Est.</th>
         		<th>Date/Time</th>
         		<th>PFactor / Est.</th>
         		<th>kW / Volts</th>
         		<th>Daily/Max Ops</th>
         	</tr>
         	<c:set var="feederRowCount" value="0"/>
			<c:forEach var="viewfeeder" items="${feederList}">
                <c:set var="thisFeederId" value="${viewfeeder.feeder.ccId}"/>
                <c:set var="feederRowClass" value="tableCell"/>
	            <c:choose>
	                <c:when test="${feederRowCount % 2 == 0}">
	                    <c:set var="feederRowClass" value="tableCell"/>
	                </c:when>
	                <c:otherwise>
	                    <c:set var="feederRowClass" value="altTableCell"/>
	                </c:otherwise>
	            </c:choose>
	            <c:set var="feederRowCount" value="${feederRowCount + 1}"/>
				<tr class="${feederRowClass}">
					<td>
                        <span style="display:none;">${viewfeeder.subBusName}</span>
                        <input type="hidden" id="paoId_${thisFeederId}" value="${thisFeederId}">
						<input type="checkbox" name="cti_chkbxFdrs" value="${thisFeederId}" class="tierCheckBox">
						
						<a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisFeederId}" class="tierIconLink">
                            <img alt="Edit" class="tierImg" src="${editInfoImage}">
                        </a>
	                    <c:if test="${hasEditingRole}">
	                        <a title="Delete" href="/editor/deleteBasePAO.jsf?value=${thisFeederId}" class="tierIconLink">
	                            <img alt="Delete" class="tierImg" src="/WebConfig/yukon/Icons/delete.gif">
	                        </a>
                        </c:if>
                    	<a title="Bank Locations"href="/spring/capcontrol/capbank/capBankLocations?value=${thisFeederId}&amp;specialArea=${isSpecialArea}" class="tierIconLink">
	                        <img alt="Locations" class="tierImg" src="/WebConfig/yukon/Icons/interstate.gif">
	                    </a>
						<span <c:if test="${viewfeeder.movedFeeder}">class="warningColor"</c:if> >
							${viewfeeder.feeder.ccName}
						</span>
					</td>
					
					<td>        
                        <capTags:warningImg paoId="${thisFeederId}" type="FEEDER"/>
                        
                        <a id="feeder_state_${thisFeederId}"    
                            <c:if test="${hasFeederControl}">
                                href="javascript:void(0);" ${popupEvent}="getFeederMenu('${thisFeederId}', event);" 
                            </c:if> 
                        >
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="STATE"/>    
						</a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('feeder_state_${thisFeederId}')" 
                        initialize="true" value="FEEDER/${thisFeederId}/STATE"/>
					</td>
                    
					<td>
                        <c:set var="isPowerFactorControlled" value="${viewfeeder.feeder.powerFactorControlled}"/>
                        <c:choose>
                            <c:when test="${viewfeeder.individualFeederControlled || viewfeeder.ivvcControlled}">
		                        <a onmouseover="showDynamicPopupAbove($('feederPFPopup_${thisFeederId}_${isPowerFactorControlled}'));"
								   onmouseout="nd();">
								   	<c:choose>
										<c:when test="${viewfeeder.ivvcControlled}">
			                            	<span style='font-weight:bold;font-size:11px;'>U:</span>
			                            	<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_PEAKLEAD"/>
			                            	<span style='font-weight:bold;font-size:11px;'> L:</span>
			                            	<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_PEAKLAG"/>
			                            	<span style='font-weight:bold;font-size:11px;'> PF:</span>
			                            	<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_CLOSEOPENPERCENT"/>
			                            </c:when>
			                            <c:otherwise>
	    									<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET"/>
	    								</c:otherwise>
    								</c:choose>
		                        </a>
	                        </c:when>
	                        <c:otherwise>
	                           <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET"/>
	                        </c:otherwise>
                        </c:choose>
                        <div class="ccPFPopup" id="feederPFPopup_${thisFeederId}_${isPowerFactorControlled}" style="display: none;">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_MESSAGE"/>    
					   </div>
					</td>
                    
					<td>
						<c:choose>
							<c:when test="${viewfeeder.feeder.usePhaseData}">
		                        <a onmouseover="showDynamicPopupAbove($('feederVarLoadPopup_${thisFeederId}'));" onmouseout="nd();">
			                        <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD"/>
			                    	<cti:classUpdater type="FEEDER" identifier="${thisFeederId}/KVAR_LOAD_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>    
			                        <span> / </span> 
			                        <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD_EST"/>
		                        </a>
						  	</c:when>
						  	<c:otherwise>
								<a>
									<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD"/>
			                        <cti:classUpdater type="FEEDER" identifier="${thisFeederId}/KVAR_LOAD_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
			                        <span> / </span> 
			                        <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD_EST"/>
		                        </a>
						  	</c:otherwise>
						</c:choose>
                        <div class="ccVarLoadPopup" id="feederVarLoadPopup_${thisFeederId}" style="display: none;">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD_MESSAGE"/> 
                        </div>
					</td>
                    
					<td>
                        <a id="dateTime_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="DATE_TIME"/></a>
					</td>
					<td>
                        <a id="pFactor_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="PFACTOR"/></a>
					</td>
					<td>
                        <a id="kwVolts_${thisFeederId}">
                        	<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KW"/> 
                        	<cti:classUpdater type="FEEDER" identifier="${thisFeederId}/WATT_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
                        	/
                        	<cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="VOLTS"/>
                        	<cti:classUpdater type="FEEDER" identifier="${thisFeederId}/VOLT_QUALITY"><img src="/WebConfig/yukon/Icons/bullet_red.gif"></cti:classUpdater>
                        </a>
					</td>
					<td>
                        <a id="dailyMaxOps_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="DAILY_MAX_OPS"/></a>
					</td>
				</tr>
			</c:forEach>
		</table>

    </tags:abstractContainer>

	<br>
	
	<tags:abstractContainer type="box" title="Capacitor Banks" hideEnabled="true" showInitially="true">
        <table id="capBankTable" class="tierTable rowHighlighting">
            <tr>
                <th>
                    <input type="checkbox" name="chkAllBanksBx" onclick="checkAll(this, 'cti_chkbxBanks');" class="tierCheckBox">
                    CBC Name
                </th>
                <th>CB Name (Order) 
                    <img alt="Info" class="tierImg helpImg" src="/WebConfig/yukon/Icons/information.gif"
                        onmouseover="statusMsgAbove(this, 'Order is the order the CapBank will control in. Commands that can be sent to a field device are initiated from this column');" >
                </th>                    
                <th>State 
                    <img alt="Info" class="tierImg helpImg" src="/WebConfig/yukon/Icons/information.gif"
                        onmouseover="statusMsgAbove(this,'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column. <br>-V : Auto Volt Control (ovUv) is Disabled. <br>-U: CBC reported unsolicited state change. <br>-Q: CapBank state reflects abnormal data quality. <br>-CF: Communications Failure. <br>-P: Partial - phase imbalance. <br>-S: Significant - questionable var response on all phases.');">  
                </th>
                <th>Date/Time</th>
                <th>Bank Size</th>
                <th>Parent Feeder</th>
                <th>Daily/Max/Total Op</th>
			</tr>
            
            <c:set var="bankRowCount" value="0"/>
    		<c:forEach var="viewableCapBank" items="${capBankList}">
                <c:set var="thisCapBankId" value="${viewableCapBank.capBankDevice.ccId}"/>
                <c:set var="bankRowClass" value="tableCell"/>
                <c:choose>
                    <c:when test="${bankRowCount % 2 == 0}">
                        <c:set var="bankRowClass" value="tableCell"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="bankRowClass" value="altTableCell"/>
                    </c:otherwise>
                </c:choose>
                <c:set var="bankRowCount" value="${bankRowCount + 1}"/>
    			<tr id="tr_cap_${thisCapBankId}" class="${bankRowClass}">
                    <td>
                        <span style="display: none"><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PARENT"/></span>
                        <input type="hidden" id="paoId_${thisCapBankId}" value="${thisCapBankId}">
                        <input type="checkbox" name="cti_chkbxBanks" value="${thisCapBankId}" class="tierCheckBox">
                        
                        <c:choose>
                            <c:when test="${viewableCapBank.capBankDevice.controlDeviceID != 0}">
                                
                                <a href="/editor/cbcBase.jsf?type=2&amp;itemid=${viewableCapBank.capBankDevice.controlDeviceID}" class="tierIconLink">
                                    <img alt="Edit" class="tierImg" src="${editInfoImage}">
                                </a>
                                <c:if test="${hasEditingRole}">
                                    <a href="/editor/copyBase.jsf?itemid=${viewableCapBank.capBankDevice.controlDeviceID}&amp;type=1>" class="tierIconLink">
                                       <img alt="Copy" src="/WebConfig/yukon/Icons/copy.gif" class="tierImg" border="0" height="15" width="15">
                                    </a>
                                </c:if>
                                ${viewableCapBank.controlDevice.paoName}
                            </c:when>
                            <c:otherwise>
                            ---
                            </c:otherwise>
                        </c:choose>
    
                        <c:if test="${viewableCapBank.twoWayCbc}">
                            <a href="javascript:showCbcPointList(${viewableCapBank.controlDevice.liteID}, '${viewableCapBank.controlDevice.paoName}')" class="tierIconLink">
                                <img alt="Timestamps" class="tierImg magnifierImg" src="/WebConfig/yukon/Icons/magnifier.gif" 
                                    onmouseover="statusMsgAbove(this,'Click here to see the timestamp information for the cap bank controller device.');">
                           </a>
                        </c:if>
                    </td>
                    
                    <td>
                    	<c:choose>
    	                	<c:when test="${hasCapbankControl}">
    	                        
    	                        <a href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisCapBankId}" class="tierIconLink">
    	                            <img alt="Edit" class="tierImg" src="${editInfoImage}">
    	                        </a>
    		                    <c:if test="${hasEditingRole}">
    		                        <a href="/editor/deleteBasePAO.jsf?value=${thisCapBankId}" class="tierIconLink">
    		                            <img alt="Delete" class="tierImg" src="/WebConfig/yukon/Icons/delete.gif">
    		                        </a>
    	                        </c:if>
    	                        <a href="javascript:void(0);" ${popupEvent}="getCapBankMenu('${thisCapBankId}', event);">
    	                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_NAME"/>
    	                        </a>
    						</c:when>
    						<c:otherwise>
    	                        
    	                        <a href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisCapBankId}" class="tierIconLink">
    	                            <img alt="Edit" class="tierImg" src="${editInfoImage}">
    	                        </a>
    		                    <c:if test="${hasEditingRole}">
    		                        <a href="/editor/deleteBasePAO.jsf?value=${thisCapBankId}" class="tierIconLink">
    		                            <img alt="Delete" class="tierImg" src="/WebConfig/yukon/Icons/delete.gif">
    		                        </a>
    	                        </c:if>
    	                        <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_NAME"/>
    						</c:otherwise>
    					</c:choose>
    					<cti:checkRolesAndProperties value="SHOW_CB_ADDINFO">
    					   <a href="javascript:showCapBankAddInfo(${thisCapBankId}, '${viewableCapBank.capBankDevice.ccName}')" class="tierIconLink">
    					       <img alt="Additional Info" class="tierImg magnifierImg" src="/WebConfig/yukon/Icons/magnifier.gif" onmouseover="statusMsgAbove(this, 'Click to see additional information for the cap bank.');">
    					   </a>
    					</cti:checkRolesAndProperties>
    				</td>
    
                    <td>
                        <capTags:capBankWarningImg paoId="${thisCapBankId}" type="CAPBANK"/>
                        
                        <cti:capBankStateColor paoId="${thisCapBankId}" type="CAPBANK" format="CB_STATUS_COLOR">
                            <a id="capbank_status_${thisCapBankId}"
                                <c:if test="${hasCapbankControl}">
                                    href="javascript:void(0);" onclick ="getCapBankSystemMenu('${thisCapBankId}', event);"
                                </c:if> 
                            >
    					       <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_STATUS"/>
                            </a>
                        </cti:capBankStateColor>
                        
    				    <span id="opcount_span${thisCapBankId}" style="display: none;" >Op Count:
    					   <input type="text" id="opcount_${thisCapBankId}" maxlength="5" size="5">
    					   <a href="javascript:void(0);" onclick="executeCapBankResetOpCount(${thisCapBankId});" >Reset</a>
    				    </span>
    					<div id="capBankStatusPopup_${thisCapBankId}" style="display: none">
                            <span>
                            	<table class='tierTable'>
                            	<tr>
                            		<th>kVAR</th>
                            		<th>PhaseA</th>
                            		<th>PhaseB</th>
                            		<th>PhaseC</th>
                            		<th>Total</th>
                            	</tr>
                            	<tr>
	                            	<td>Before:</td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEA_BEFORE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEB_BEFORE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEC_BEFORE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_BEFORE_TOTAL"/></td>
                            	</tr>
                            	<tr>
	                            	<td>After:</td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEA_AFTER"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEB_AFTER"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEC_AFTER"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_AFTER_TOTAL"/></td>
                            	</tr>
                            	<tr>
	                            	<td>Change:</td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEA_PERCENTCHANGE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEB_PERCENTCHANGE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PHASEC_PERCENTCHANGE"/></td>
	                            	<td><cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PERCENTCHANGE_TOTAL"/></td>
                            	</tr>
                            </table>
                            </span>
    					</div>
    				</td>
                    
    				<td>
                        <a id="dateTime_${thisCapBankId}"
    				       onmouseover = "showDynamicPopupAbove($('capBankStatusPopup_${thisCapBankId}'), 250)"
    				       onmouseout="nd()">
                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="DATE_TIME"/> 
                        </a>
    				</td>
                    
    				<td>
                        <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_SIZE"/>
                    </td>
                    
                    <td>
                        <a href="javascript:void(0);"
    	                    <c:if test="${hasCapbankControl}">
    							<c:choose>
    							<c:when test="${viewableCapBank.capBankDevice.bankMoved}">
    			                    class="warning pointer" ${popupEvent}="getCapBankTempMoveBack('${thisCapBankId}', event);" 
    		                    </c:when>
    		                    <c:otherwise>
    		                        onmouseover="statusMsgAbove(this, 'Click here to fully move or temporarily move this CapBank from its current parent feeder');"
    		                        onmouseout="nd();"
    		                        onclick="return GB_show('CapBank Move for ${viewableCapBank.capBankDevice.ccName} (Pick feeder by clicking on name)',
    		                            '/spring/capcontrol/move/bankMove?bankid=${thisCapBankId}', 580, 710, onGreyBoxClose);"
    		                    </c:otherwise>
    		                    </c:choose>
    		                </c:if>
                        >
                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PARENT"/>
                    	</a>                    
                    </td>
					<td>
						<a>
						  <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="DAILY_MAX_OPS"/>
                        </a>
					</td>
				</tr>
                
			</c:forEach>
            
		</table>
            
		<input type="hidden" id="lastUpdate" value="">
        
	</tags:abstractContainer>
</cti:standardPage>