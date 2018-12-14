<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:includeScript link="JQUERY_TREE" />
<cti:includeScript link="JQUERY_TREE_HELPERS" />
<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
    <c:set var="hasControlAttr" value="data-has-control" />

<cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasCapBankCommandsAndActionsAccess" value="true"/>
    <c:set var="hasControlAttr" value="data-has-control" />
</cti:checkRolesAndProperties>
    <table id="capBankTable" class="compact-results-table has-alerts has-actions row-highlighting" ${hasControlAttr}>
        <thead>
            <tr>
                <th></th>
                <th><i:inline key=".cbcName"/></th>
                <th class="wsnw">
                    <i:inline key=".cbName"/>
                    <div id="bank-order-info" class="dn"><cti:msg2 key=".cbOrderInfo.tooltip"/></div>
                    <cti:icon icon="icon-information" classes="M0 vatt fn" data-tooltip="#bank-order-info"/>
                </th>
                <th class="wsnw">
                    <i:inline key=".state"/>
                    <div id="bank-state-info" class="dn js-sticky-tooltip"><cti:msg2 key=".stateInfoToolTip"/></div>
                    <cti:icon nameKey="stateInfo" icon="icon-information" classes="M0 vatt fn" data-tooltip="#bank-state-info"/>
                </th>
                <th><i:inline key=".timestamp"/></th>
                <th class="tar"><i:inline key=".bankSize"/></th>
                <th><i:inline key=".parentFeeder"/></th>
                <th><i:inline key=".dailyMaxTotal"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>

        <c:forEach var="viewableCapBank" items="${capBankList}">
            <c:set var="bankId" value="${viewableCapBank.ccId}"/>
            <c:set var="parentId" value="${viewableCapBank.parentId}"/>
            <cti:msg2 var="moveBankTitle" key=".moveBankTitle" arguments="${viewableCapBank.ccName}"/>
            <cti:msg2 var="cbcInfoTitle" key=".cbcPointsTitle" arguments="${viewableCapBank.cbcName}" javaScriptEscape="true"/>
            <cti:msg2 var="bankInfoTitle" key=".addInfoTitle" arguments="${viewableCapBank.ccName}" javaScriptEscape="true"/>

            <tr class="bank"
                data-bank-id="${bankId}"
                data-bank-name="${fn:escapeXml(viewableCapBank.ccName)}"
                data-cbc-id="${viewableCapBank.cbcId}"
                data-parent-id="${parentId}"
                data-move-bank-title="${fn:escapeXml(moveBankTitle)}"
                data-cbc-info-title="${fn:escapeXml(cbcInfoTitle)}"
                data-bank-info-title="${fn:escapeXml(bankInfoTitle)}"
                id="tr_cap_${bankId}_parent_${parentId}">

                <%-- CBC Name --%>
                <td width="16px">
                    <capTags:capBankWarningImg paoId="${bankId}" type="CAPBANK"/>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${viewableCapBank.cbcId != 0}">
                            <cti:url var="editUrl" value="/capcontrol/cbc/${viewableCapBank.cbcId}" />
                            <a href="${editUrl}">
                                ${fn:escapeXml(viewableCapBank.cbcName)}
                            </a> 
                        </c:when>
                        <c:otherwise>
                            <i:inline key="yukon.common.dashes"/>
                        </c:otherwise>
                    </c:choose>
                </td>

                <%-- Bank Name --%>
                <td>
                    <cti:url var="editUrl" value="/capcontrol/capbanks/${bankId}" />
                    <a href="${editUrl}">
                        <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_NAME"/>
                    </a>
                </td>

                <%-- State --%>
                <td class="wsnw">
                    <cti:capBankStateColor paoId="${bankId}" type="CAPBANK" format="CB_STATUS_COLOR" />
                    <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_STATUS"/>
                    <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="LOCAL_FLAG"/>
                </td>

                <%-- Timestamp --%>
                <td>
                    <div id="bank-timestamp-${bankId}" class="dn">
                        <table class="compact-results-table">
                            <tr>
                                <th><i:inline key=".kvar"/></th>
                                <c:if test="${viewableCapBank.userPerPhaseData}">
                                    <th><i:inline key=".phaseA"/></th>
                                    <th><i:inline key=".phaseB"/></th>
                                    <th><i:inline key=".phaseC"/></th>
                                </c:if>
                                <th><i:inline key=".total"/></th>
                            </tr>
                            <tr>
                                <td><i:inline key=".before"/></td>
                                <c:if test="${viewableCapBank.userPerPhaseData}">
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEA_BEFORE"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEB_BEFORE"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEC_BEFORE"/></td>
                                </c:if>
                                <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_BEFORE_TOTAL"/></td>
                            </tr>
                            <tr>
                                <td><i:inline key=".after"/></td>
                                <c:if test="${viewableCapBank.userPerPhaseData}">
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEA_AFTER"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEB_AFTER"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEC_AFTER"/></td>
                                </c:if>
                                <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_AFTER_TOTAL"/></td>
                            </tr>
                            <tr>
                                <td><i:inline key=".change"/></td>
                                <c:if test="${viewableCapBank.userPerPhaseData}">       
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEA_PERCENTCHANGE"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEB_PERCENTCHANGE"/></td>
                                    <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PHASEC_PERCENTCHANGE"/></td>
                                </c:if>
                                <td><cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PERCENTCHANGE_TOTAL"/></td>
                            </tr>
                        </table>
                    </div>
                    <span data-tooltip="#bank-timestamp-${bankId}">
                        <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="DATE_TIME"/> 
                    </span>
                </td>

                <%-- Bank Size --%>
                <td class="tar">
                    <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_SIZE"/>
                </td>

                <%-- Parent Feeder --%>
                <td>
                    <c:set var="movedClass" value="${viewableCapBank.bankMoved ? 'warning' : ''}" />
                    <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_PARENT" styleClass="${movedClass}"/>
                </td>

                <%-- Daily/Max/Total Ops --%>
                <td>
                    <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="DAILY_MAX_OPS"/>
                </td>
                <td>
                    <cti:url var="collectionsUrl" value="/bulk/collectionActions">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="/System/Device Configs/${config.name}"/>
                    </cti:url>
                    <cm:dropdown icon="icon-cog">
                        <c:if test="${hasCapBankCommandsAndActionsAccess}">
                            <cm:dropdownOption key=".capBank.actions" icon="icon-cog" href="javascript:void(0);" classes="js-bank-command" />
                            <cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
                                NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
                                <cm:dropdownOption key=".capBank.state" icon="icon-pencil" href="javascript:void(0);" classes="js-bank-state" />
                            </cti:checkRolesAndProperties>
                            <li class="divider" />
                        </c:if>
                        <cti:checkRolesAndProperties value="SHOW_CB_ADDINFO">
                            <cti:msg2 var="addInfoTitle" key=".addInfoTitle" arguments="${viewableCapBank.ccName}" javaScriptEscape="true"/>
                            <cm:dropdownOption key=".capBank.info" icon="icon-magnifier" classes="js-bank-info"
                                href="javascript:void(0);" />
                        </cti:checkRolesAndProperties>
                        <c:if test="${viewableCapBank.twoWayCbc || viewableCapBank.logicalCBC}">
                                <cti:msg2 var="cbcPointsTitle" key=".cbcPointsTitle" arguments="${viewableCapBank.cbcName}" javaScriptEscape="true"/>
                                <cm:dropdownOption key=".cbc.info" icon="icon-magnifier" classes="js-cbc-info" href="javascript:void(0);" />
                        </c:if>
                        <li class="divider" />
                        <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${bankId}" 
                            data-pao-name="${fn:escapeXml(viewableCapBank.ccName)}"/>
                        <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${bankId}" />
                        <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
                        <cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
                            NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
                            <li class="divider" />
                            <c:if test="${not viewableCapBank.bankMoved}">
                                <cm:dropdownOption key=".bankMove" icon="icon-bullet-go" classes="js-move-bank" href="javascript:void(0);" />
                            </c:if>
                            <c:if test="${viewableCapBank.bankMoved}">
                                <cm:dropdownOption classes="js-return" key=".command.RETURN_CAP_TO_ORIGINAL_FEEDER" icon="icon-bullet-go-left" />
                                <cm:dropdownOption classes="js-assign" key=".command.assignBankHere" icon="icon-bullet-go-down" />
                            </c:if>
                        </cti:checkRolesAndProperties>
                    </cm:dropdown>
                </td>
            </tr>
        </c:forEach>
    </table>