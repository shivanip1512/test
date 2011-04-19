<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<cti:standardPage title="${substation.ccName}" module="capcontrol" page="feeders">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
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
            validElems.each(function (elem) {
                targets.push(elem.getAttribute('value'));
            });
            
            url += '&targets=' + targets.join(',');
            
            GB_showFullScreen(title, url, null);
            return void(0);
        }
    
           function onGreyBoxClose () {
               window.location.href = window.location.href;
           }
    
     </script>
    
    <input type="hidden" id="paoId_${substation.ccId}" value="${substation.ccId}">
    
    <c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
    
    <tags:abstractContainer type="box" title="Substation" hideEnabled="false">
        <table id="substationTable" class="tierTable">
            
            <thead>
                <tr>
                    <th>Substation Name</th>
                    <th>Actions</th>
                    <th>State</th>
                </tr>
            </thead>
            
            <tr class="tableCell" id="tr_substation_${substation.ccId}">
            
                <td id="anc_${thisSubStationId}">
                    <span><spring:escapeBody>${substation.ccName}</spring:escapeBody></span>
                    <span class="errorRed textFieldLabel updatingSpan">
                        <cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="SA_ENABLED"/>
                    </span>
                </td>
                
                <td>
                    <cti:img key="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${substation.ccId}" styleClass="tierIconLink"/>
                    <c:if test="${hasEditingRole}">
                        <cti:img key="remove" href="/editor/deleteBasePAO.jsf?value=${substation.ccId}" styleClass="tierIconLink"/>
                    </c:if>
                    <cti:img key="location" href="/spring/capcontrol/capbank/capBankLocations?value=${substation.ccId}&amp;specialArea=${isSpecialArea}" styleClass="tierIconLink"/>
                </td>
                
                <td>
                    <capTags:warningImg paoId="${substation.ccId}" type="SUBSTATION"/>
                    <a id="substation_state_${substation.ccId}" <c:if test="${hasSubstationControl}">href="javascript:void(0);" onclick="getSubstationMenu('${substation.ccId}', event);"</c:if>>
                        <cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="STATE"/>
                    </a>
                    <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${substation.ccId}')" initialize="true" value="SUBSTATION/${substation.ccId}/STATE"/>
                </td>
                
            </tr>

        </table>
    </tags:abstractContainer>
    
    <br>

    <tags:boxContainer title="Substation Bus" hideEnabled="true" showInitially="true">

        <table id="subBusTable" class="tierTable rowHighlighting">
            
            <thead>
                
                <tr>
                    <th>
                        <input type="checkbox" name="chkAllSubBusesBx" onclick="checkAll(this, 'cti_chkbxSubBuses');" class="tierCheckBox">
                        <span class="checkBoxLabel">Substation Bus Name</span>
                    </th>
                    <th>Actions</th>
                    <th>State</th>
                    <th>Target</th>
                    <th>kVAR Load / Est.</th>
                    <th>Date/Time</th>
                    <th>PFactor / Est.</th>
                    <th>kW / Volts</th>
                    <th>Daily / <span class="nonwrapping">Max Ops</span></th>
                </tr>
                
            </thead>

            <c:forEach var="viewableSubBus" items="${subBusList}">
                <!-- Setup Variables -->
                <c:set var="thisSubBusId" value="${viewableSubBus.subBus.ccId}"/>
                <cti:url value="${onelineCBCServlet}" var="oneLineLink">
                    <cti:param name="id" value="${thisSubBusId}"/>
                    <cti:param name="redirectURL" value="${fullURL}"/>
                </cti:url>
                <cti:url value="/spring/capcontrol/ivvc/bus/detail" var="ivvcLink">
                    <cti:param name="subBusId" value="${thisSubBusId}"/>
                    <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                </cti:url>
                <cti:url value="/spring/capcontrol/tier/feeders" var="dualBusLink">
                    <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                    <cti:param name="subStationId" value="${viewableSubBus.alternateStationId}"/>
                    <cti:param name="areaId" value="${viewableSubBus.alternateAreaId}"/>
                </cti:url>
                <cti:url value="/spring/capcontrol/capbank/capBankLocations" var="locationLink">
                    <cti:param name="value" value="${thisSubBusId}"/>
                    <cti:param name="specialArea" value="${isSpecialArea}"/>
                </cti:url>
                
                <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>"  id="tr_sub_${thisSubBusId}">
                    
                    <td id="anc_${thisSubBusId}">
                        <input type="hidden" id="paoId_${viewableSubBus.subBus.ccId}" value="${thisSubBusId}">
                        <input type="checkbox" name="cti_chkbxSubBuses" value="${thisSubBusId}" class="tierCheckBox">
                        <input type="image" id="showSnap${thisSubBusId}" src="/capcontrol/images/nav-plus.gif" class="tierImg"
                           onclick="showRowElems('subSnapShot${thisSubBusId}', 'showSnap${thisSubBusId}'); return false;">
                        <spring:escapeBody htmlEscape="true">${viewableSubBus.subBus.ccName}</spring:escapeBody>
                        <a href="${dualBusLink}" class="tierIconLink">
                            <capTags:dualBusImg paoId="${thisSubBusId}" type="SUBBUS"/>
                        </a>
                        <capTags:verificationImg paoId="${thisSubBusId}" type="SUBBUS"/>
                    </td>
                    
                    <td>
                        <cti:img key="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisSubBusId}" styleClass="tierIconLink"/>
                        <c:if test="${hasEditingRole}">
                            <cti:img key="remove" href="/editor/deleteBasePAO.jsf?value=${thisSubBusId}" styleClass="tierIconLink"/>
                        </c:if>
                        <cti:img key="location" href="${locationLink}" styleClass="tierIconLink"/>
                        <c:if test="${!hideOneLine}">
                            <cti:img key="oneline" href="${oneLineLink}" styleClass="tierIconLink"/>
                        </c:if>
                        <c:if test="${viewableSubBus.ivvcControlled}">
                            <cti:img key="ivvc" href="${ivvcLink}" styleClass="tierIconLink"/>
                        </c:if>
                    </td>
                    
                    <td>
                        <capTags:warningImg paoId="${thisSubBusId}" type="SUBBUS"/>
                        <a id="subbus_state_${thisSubBusId}"<c:if test="${hasSubBusControl}">href="javascript:void(0);" onclick="getSubBusMenu('${thisSubBusId}', event);"</c:if>>
                            <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="STATE"/>
                        </a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('subbus_state_${thisSubBusId}')" initialize="true" value="SUBBUS/${thisSubBusId}/STATE"/>
                    </td>
                    
                    <td>
                        <c:set var="isPowerFactorControlled" value="${viewableSubBus.subBus.powerFactorControlled}"/>
                        <c:choose>
                            <c:when test="${viewableSubBus.subBus.controlMethod.busControlled}">
                                <span <c:if test="${viewableSubBus.showTargetTooltip}">
                                        onmouseover="showDynamicPopupAbove($('subPFPopup_${thisSubBusId}_${isPowerFactorControlled}'))"
                                        onmouseout="nd();"
                                    </c:if>>
                                    <c:choose>
                                        <c:when test="${viewableSubBus.ivvcControlled}">
                                            <span style='font-weight:bold;font-size:11px;'>U:</span>
                                            <span class="updatingSpan"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_PEAKLEAD"/></span>
                                            <span style='font-weight:bold;font-size:11px;'> L:</span>
                                            <span class="updatingSpan"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_PEAKLAG"/></span>
                                            <span style='font-weight:bold;font-size:11px;'> PF:</span>
                                            <span class="updatingSpan"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_CLOSEOPENPERCENT"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET"/>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="updatingSpan">
                                    <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET"/>
                                </span>
                            </c:otherwise>
                        </c:choose>
                        <div class="ccPFPopup" id="subPFPopup_${thisSubBusId}_${isPowerFactorControlled}" style="display: none;" >
                            <span class="updatingSpan">
                                <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_MESSAGE"/>
                            </span>     
                        </div>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${viewableSubBus.subBus.usePhaseData}">
                                <a onmouseover="showDynamicPopup($('subVarLoadPopup_${thisSubBusId}'));" 
                                   onmouseout="nd();">
                                    <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD"/>
                                    <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/KVAR_LOAD_QUALITY">
                                        <img src="/WebConfig/yukon/Icons/bullet_red.gif" class="tierImg" title="Questionable Quality">
                                    </cti:classUpdater>
                                    <span> / </span> 
                                    <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD_EST"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a>
                                    <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD"/>
                                    <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/KVAR_LOAD_QUALITY">
                                        <img src="/WebConfig/yukon/Icons/bullet_red.gif" class="tierImg" title="Questionable Quality">
                                    </cti:classUpdater>
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
                            <span class="updatingSpan">
                                <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KW"/>
                            </span>
                            <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/WATT_QUALITY">
                                <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                            </cti:classUpdater>
                            <span style="vertical-align:top"> / </span>
                            <span class="updatingSpan">
                                <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="VOLTS"/>
                            </span>
                            <cti:classUpdater type="SUBBUS" identifier="${thisSubBusId}/VOLT_QUALITY">
                                <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                            </cti:classUpdater>
                        </a>
                    </td>
                    <td>
                        <a id="dailyMaxOps_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="DAILY_MAX_OPS"/></a>
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
                                    <span class="errorRed"><cti:capControlValue paoId="${substation.ccId}" type="SUBSTATION" format="SA_ENABLED"/></span>
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
    </tags:boxContainer>
    <c:if test="${fn:length(subBusList) > 1}">
        <tags:boxContainerFooter>
            <cti:labeledImg key="filter"/>
            <select id='subBusFilter' onchange='applySubBusFilter(this);'>
                <option>All SubBuses</option>
                <c:forEach var="sub" items="${subBusList}" >
                   <option value="${sub.subBus.ccId}"><spring:escapeBody htmlEscape="true">${sub.subBus.ccName}</spring:escapeBody></option>
                </c:forEach>
            </select>
        </tags:boxContainerFooter>
    </c:if>

    <br>

    <tags:abstractContainer type="box" title="Feeders" hideEnabled="true" showInitially="true">

        <table id="fdrTable" class="tierTable rowHighlighting">
            
            <thead>
            
                <tr>
                     <th>
                        <input type="checkbox" name="chkAllFdrsBx" onclick="checkAll(this, 'cti_chkbxFdrs');" class="tierCheckBox">
                        <span class="checkBoxLabel">Feeder Name</span>
                    </th>
                    <th>Actions</th>
                     <th>State</th>
                     <th>Target</th>
                     <th>kVAR Load / Est.</th>
                     <th>Date/Time</th>
                     <th>PFactor / Est.</th>
                     <th>kW / Volts</th>
                    <th>Daily / <span class="nonwrapping">Max Ops</span></th>
                 </tr>
                
            </thead>
            
            <!-- Reset the alternating row style -->
            <tags:alternateRowReset/>
            
            <c:forEach var="viewfeeder" items="${feederList}">
                <c:set var="thisFeederId" value="${viewfeeder.feeder.ccId}"/>
                <c:set var="parentId" value="${viewfeeder.feeder.parentID}"/>
                
                <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>" id="tr_feeder_${thisFeederId}_parent_${parentId}">
                    <td>
                        <input type="hidden" id="paoId_${thisFeederId}" value="${thisFeederId}">
                        <input type="checkbox" name="cti_chkbxFdrs" value="${thisFeederId}" class="tierCheckBox">
                        <span <c:if test="${viewfeeder.movedFeeder}">class="warningColor"</c:if> >
                            <spring:escapeBody htmlEscape="true">${viewfeeder.feeder.ccName}</spring:escapeBody>
                        </span>
                    </td>
                    
                    <td>
                        <cti:img key="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisFeederId}" styleClass="tierIconLink"/>
                        <c:if test="${hasEditingRole}">
                            <cti:img key="remove" href="/editor/deleteBasePAO.jsf?value=${thisFeederId}" styleClass="tierIconLink"/>
                        </c:if>
                        <cti:img key="location" href="/spring/capcontrol/capbank/capBankLocations?value=${thisFeederId}&amp;specialArea=${isSpecialArea}" styleClass="tierIconLink"/>
                    </td>
                    
                    <td>
                        <capTags:warningImg paoId="${thisFeederId}" type="FEEDER"/>
                        <a id="feeder_state_${thisFeederId}"<c:if test="${hasFeederControl}">href="javascript:void(0);" onclick="getFeederMenu('${thisFeederId}', event);"</c:if>>
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="STATE"/>    
                        </a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('feeder_state_${thisFeederId}')" initialize="true" value="FEEDER/${thisFeederId}/STATE"/>
                    </td>
                    
                    <td>
                        <c:set var="isPowerFactorControlled" value="${viewfeeder.feeder.powerFactorControlled}"/>
                        <c:choose>
                            <c:when test="${viewfeeder.individualFeederControlled || viewfeeder.ivvcControlled}">
                                <span <c:if test="${viewfeeder.showTargetTooltip}">
                                        onmouseover="showDynamicPopupAbove($('feederPFPopup_${thisFeederId}_${isPowerFactorControlled}'));"
                                        onmouseout="nd();"
                                    </c:if>>
                                    <c:choose>
                                        <c:when test="${viewfeeder.ivvcControlled}">
                                            <span style='font-weight:bold;font-size:11px;'>U:</span>
                                            <span class="updatingSpan">
                                                <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_PEAKLEAD"/>
                                            </span>
                                            <span style='font-weight:bold;font-size:11px;'> L:</span>
                                            <span class="updatingSpan">
                                                <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_PEAKLAG"/>
                                            </span>
                                            <span style='font-weight:bold;font-size:11px;'> PF:</span>
                                            <span class="updatingSpan">
                                                <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_CLOSEOPENPERCENT"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET" />
                                        </c:otherwise>
                                    </c:choose>
                                </span>
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
                                    <cti:classUpdater type="FEEDER" identifier="${thisFeederId}/KVAR_LOAD_QUALITY">
                                        <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                                    </cti:classUpdater>    
                                    <span> / </span> 
                                    <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD_EST"/>
                                </a>
                              </c:when>
                              <c:otherwise>
                                <a>
                                    <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD"/>
                                    <cti:classUpdater type="FEEDER" identifier="${thisFeederId}/KVAR_LOAD_QUALITY">
                                        <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                                     </cti:classUpdater>
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
                            <cti:classUpdater type="FEEDER" identifier="${thisFeederId}/WATT_QUALITY">
                                <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                            </cti:classUpdater>
                            /
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="VOLTS"/>
                            <cti:classUpdater type="FEEDER" identifier="${thisFeederId}/VOLT_QUALITY">
                                <img src="/WebConfig/yukon/Icons/bullet_red.gif" title="Questionable Quality">
                            </cti:classUpdater>
                        </a>
                    </td>
                    <td>
                        <a id="dailyMaxOps_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="DAILY_MAX_OPS"/></a>
                    </td>
                </tr>
            </c:forEach>

        </table>

    </tags:abstractContainer>
    <c:if test="${fn:length(feederList) > 1}">
        <tags:boxContainerFooter>
            <cti:labeledImg key="filter"/>
            <select id='feederFilter' onchange='applyFeederSelectFilter(this);'>
                <option>All Feeders</option>
                <c:forEach var="feeder" items="${feederList}">
                    <option value="${feeder.feeder.ccId}"><spring:escapeBody htmlEscape="true">${feeder.feeder.ccName}</spring:escapeBody></option>
                </c:forEach>
            </select>
        </tags:boxContainerFooter>
    </c:if>

    <br>
    
    <tags:abstractContainer type="box" title="Capacitor Banks" hideEnabled="true" showInitially="true">
        
        <table id="capBankTable" class="tierTable rowHighlighting">
        
            <thead>
            
                <tr>
                    <th>
                        <input type="checkbox" name="chkAllBanksBx" onclick="checkAll(this, 'cti_chkbxBanks');" class="tierCheckBox">
                        CBC Name
                    </th>
                    <th>Actions</th>
                    <th>CB Name (Order) 
                        <img alt="Info" class="tierImg helpImg" src="/WebConfig/yukon/Icons/information.gif"
                            onmouseover="statusMsgAbove(this, 'Order is the order the CapBank will control in. Commands that can be sent to a field device are initiated from this column');" >
                    </th>
                    <th>Actions</th>
                    <th>State 
                        <img alt="Info" class="tierImg helpImg" src="/WebConfig/yukon/Icons/information.gif"
                            onmouseover="statusMsgAbove(this,'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column. <br>-V : Auto Volt Control (ovUv) is Disabled. <br>-U: CBC reported unsolicited state change. <br>-Q: CapBank state reflects abnormal data quality. <br>-CF: Communications Failure. <br>-P: Partial - phase imbalance. <br>-S: Significant - questionable var response on all phases.');">  
                        <span id="cb_state_td_hdr2" style="display:none" >[Op Count Value]</span>
                    </th>
                    <th>Date/Time</th>
                    <th>Bank Size</th>
                    <th>Parent Feeder</th>
                    <th>Daily / Max / <span class="nonwrapping">Total Ops</span></th>
                </tr>
            
            </thead>
            
            <!-- Reset the alternating row style -->
            <tags:alternateRowReset/>
            
            <c:forEach var="viewableCapBank" items="${capBankList}">
                
                <c:set var="thisCapBankId" value="${viewableCapBank.capBankDevice.ccId}"/>
                <c:set var="parentId" value="${viewableCapBank.capBankDevice.parentID}"/>
                
                <tr id="tr_cap_${thisCapBankId}_parent_${parentId}" class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                    <td>
                        <input type="hidden" id="paoId_${thisCapBankId}" value="${thisCapBankId}">
                        <input type="checkbox" name="cti_chkbxBanks" value="${thisCapBankId}" class="tierCheckBox">
                        
                        <c:choose>
                            <c:when test="${viewableCapBank.capBankDevice.controlDeviceID != 0}">
                                <spring:escapeBody htmlEscape="true">${viewableCapBank.controlDevice.paoName}</spring:escapeBody>
                            </c:when>
                            <c:otherwise>
                            ---
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
                    <td>
                        <c:if test="${viewableCapBank.capBankDevice.controlDeviceID != 0}">
                            <cti:img key="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${viewableCapBank.capBankDevice.controlDeviceID}" styleClass="tierIconLink"/>
                            <c:if test="${hasEditingRole}">
                                <cti:img key="copy" href="/editor/copyBase.jsf?itemid=${viewableCapBank.capBankDevice.controlDeviceID}&amp;type=1>" styleClass="tierIconLink"/>
                            </c:if>
                        </c:if>
                        <c:if test="${viewableCapBank.twoWayCbc}">
                            <span onmouseover="statusMsgAbove(this,'Click here to see the timestamp information for the cap bank controller device.');">
                                <cti:img key="view" href="javascript:showCbcPointList(${viewableCapBank.controlDevice.liteID}, '${viewableCapBank.controlDevice.paoName}')" styleClass="tierIconLink"/>
                            </span>
                        </c:if>
                    </td>
                    
                    <td>
                        <c:if test="${hasCapbankControl}"><a href="javascript:void(0);" onclick="getCapBankMenu('${thisCapBankId}', event);"></c:if>
                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_NAME"/>
                        <c:if test="${hasCapbankControl}"></a></c:if>
                    </td>
                    
                    <td>
                        <cti:img key="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${thisCapBankId}" styleClass="tierIconLink"/>
                        <c:if test="${hasEditingRole}">
                            <cti:img key="remove" href="/editor/deleteBasePAO.jsf?value=${thisCapBankId}" styleClass="tierIconLink"/>
                        </c:if>
                        <cti:checkRolesAndProperties value="SHOW_CB_ADDINFO">
                            <span onmouseover="statusMsgAbove(this, 'Click to see additional information for the cap bank.');">
                                <cti:img key="view" href="javascript:showCapBankAddInfo(${thisCapBankId}, '${viewableCapBank.capBankDevice.ccName}')" styleClass="tierIconLink"/>
                            </span>
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
                                    class="warning pointer" onclick="getCapBankTempMoveBack('${thisCapBankId}', event);" 
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
            
    </tags:abstractContainer>
</cti:standardPage>