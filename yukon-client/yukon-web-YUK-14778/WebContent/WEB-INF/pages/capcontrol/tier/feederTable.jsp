<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:checkRolesAndProperties value="ALLOW_FEEDER_CONTROLS">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="feederState"]');
    </script>
</cti:checkRolesAndProperties>

<table id="fdrTable" class="compact-results-table has-alerts-multi has-actions">
    <thead>
        <tr>
            <th>&nbsp;</th>
            <th><i:inline key=".name"/></th>
            <th><i:inline key=".state"/></th>
            <th><i:inline key=".target"/></th>
            <th><i:inline key=".load"/></th>
            <th><i:inline key=".timestamp"/></th>
            <th><i:inline key=".pfactorEstimated"/></th>
            <th><i:inline key=".kwVolts"/></th>
            <th><i:inline key=".dailyMax"/></th>
         </tr>
    </thead>
    <c:forEach var="viewfeeder" items="${feederList}">
        <c:set var="feederId" value="${viewfeeder.ccId}"/>

        <tr data-feeder-id="${feederId}" data-parent-id="${viewfeeder.parentId}" data-pao-id="${feederId}">
            <%-- Icons --%>
            <td>
                <capTags:warningImg paoId="${feederId}" type="FEEDER"/>
                <cti:icon icon="icon-bullet-orange" classes="thin dn" data-dual-bus-feeder="${feederId}" />
                <cti:dataUpdaterCallback function="yukon.da.updaters.feederDualBus" initialize="true" value="FEEDER/${feederId}/DUALBUS"/>
            </td>
            <%-- Name --%>
            <td class="wsnw">
                <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                    <cti:param name="type" value="2"/>
                    <cti:param name="itemid" value="${feederId}"/>
                </cti:url>
                <a href="${editUrl}">${fn:escapeXml(viewfeeder.ccName)}</a>
            </td>

            <%-- State --%>
            <td class="wsnw">
                <span class="box state-box js-cc-state-updater" data-pao-id="${feederId}">&nbsp;</span>
                <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" value="FEEDER/${feederId}/STATE_FLAGS"/>

                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="STATE"/>
            </td>

            <%-- Target --%>
            <td>
                <c:choose>
                    <c:when test="${viewfeeder.individualFeederControlled || viewfeeder.ivvcControlled}">
                        <c:if test="${viewfeeder.showTargetTooltip}">
                            <div id="feeder-target-msg-${feederId}" class="dn">
                                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET_MESSAGE"/>
                            </div>
                        </c:if>
                        <span data-tooltip="#feeder-target-msg-${feederId}">
                            <c:choose>
                                <c:when test="${viewfeeder.ivvcControlled}">
                                    <span class="ivvcTargetLabel"><i:inline key=".ivvcUpper"/></span>
                                    <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET_PEAKLEAD"/>
                                    <span class="ivvcTargetLabel"><i:inline key=".ivvcLower"/></span>
                                    <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET_PEAKLAG"/>
                                    <br>
                                    <span class="ivvcTargetLabel"><i:inline key=".ivvcPowerFactor"/></span>
                                    <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET_CLOSEOPENPERCENT"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </c:when>
                    <c:otherwise>
                       <cti:capControlValue paoId="${feederId}" type="FEEDER" format="TARGET"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <%-- kVAR Load / Est. --%>
            <td class="wsnw">
                <c:choose>
                    <c:when test="${viewfeeder.usePhaseData}">
                        <div id="feeder-kvar-msg-${feederId}" class="dn">
                            <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KVAR_LOAD_MESSAGE"/> 
                        </div>
                        <span data-tooltip="#feeder-kvar-msg-${feederId}">
                            <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KVAR_LOAD"/>
                            <cti:classUpdater type="FEEDER" identifier="${feederId}/KVAR_LOAD_QUALITY">
                                <cti:icon nameKey="questionableQuality" icon="icon-bullet-red" classes="thin fn M0"/>
                            </cti:classUpdater>    
                            <i:inline key=".statSeparator"/> 
                            <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KVAR_LOAD_EST"/>
                        </span>
                      </c:when>
                      <c:otherwise>
                        <span>
                            <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KVAR_LOAD"/>
                            <cti:classUpdater type="FEEDER" identifier="${feederId}/KVAR_LOAD_QUALITY">
                                <cti:icon nameKey="questionableQuality" icon="icon-bullet-red" classes="thin fn M0"/>
                             </cti:classUpdater>
                            <i:inline key=".statSeparator"/>  
                            <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KVAR_LOAD_EST"/>
                        </span>
                      </c:otherwise>
                </c:choose>
            </td>
            
            <%-- Date/Time --%>
            <td>
                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="DATE_TIME"/>
            </td>
            
            <%-- PFactor / Est. --%>
            <td>
                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="PFACTOR"/>
            </td>
            
            <%-- kW / Volts--%>
            <td class="wsnw"> 
                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="KW"/> 
                <cti:classUpdater type="FEEDER" identifier="${feederId}/WATT_QUALITY">
                    <cti:icon nameKey="questionableQuality" icon="icon-bullet-red" classes="thin fn M0"/>
                </cti:classUpdater>
                <i:inline key=".statSeparator"/> 
                <cti:capControlValue paoId="${feederId}" type="FEEDER" format="VOLTS"/>
                <cti:classUpdater type="FEEDER" identifier="${feederId}/VOLT_QUALITY">
                    <cti:icon nameKey="questionableQuality" icon="icon-bullet-red" classes="thin fn M0"/>
                </cti:classUpdater>
            </td>
            <%-- Daily / Max Ops --%>
            <td>

                <span id="dailyMaxOps_${feederId}">
                    <cti:capControlValue paoId="${feederId}" type="FEEDER" format="DAILY_MAX_OPS"/>
                </span>
                <cm:dropdown icon="icon-cog" triggerClasses="fr">
                    <c:if test="${hasFeederControl}">
                        <li>
                            <a id="feederState_${feederId}" href="javascript:void(0)" class="clearfix">
                                <cti:icon icon="icon-cog" /><span class="dib"><i:inline key=".feeder.actions"/></span>
                            </a>
                        </li>
                    </c:if>
                    <cti:url var="locationUrl" value="/capcontrol/capbank/capBankLocations">
                        <cti:param name="value" value="${feederId}"/>
                    </cti:url>
                    <cm:dropdownOption key=".location.label" icon="icon-interstate" href="${locationUrl}" />
                </cm:dropdown>
            </td>
        </tr>
    </c:forEach>
</table>