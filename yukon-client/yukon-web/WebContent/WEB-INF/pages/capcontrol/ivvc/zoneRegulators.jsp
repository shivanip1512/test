<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<table class="compact-results-table">
    <c:forEach items="${zoneDto.regulators}" var="regulator">
        <c:set value="${regulator.key}" var="phaseKey"/>
        <tr>
            <td class="PL0">
                <capTags:regulatorModeIndicator paoId="${regulatorIdMap[phaseKey]}" showBlankIcon="${zoneDto.zoneType == threePhase}"/>
        
                <c:if test="${zoneDto.zoneType != gangOperated}"> 
                    <i:inline key="${phaseKey}"/> -
                </c:if>
        
                <cti:url var="regulatorUrl" value="/capcontrol/regulators/${regulatorIdMap[phaseKey]}" />
                <a href="${regulatorUrl}" class="PR5">${fn:escapeXml(regulatorNameMap[phaseKey])}</a>
                <cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
                    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
                    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS">
                    <cm:dropdown data-pao-id="${regulatorIdMap[phaseKey]}" triggerClasses="fr vv">
                        <cm:dropdownOption key=".scan.label" icon="icon-transmit-blue" classes="js-command-button" 
                            data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${scanCommandHolder.commandId}" />
                            <cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
                                ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS">
                                <cm:dropdownOption key=".up.label" icon="icon-arrow-up-green" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${tapUpCommandHolder.commandId}" />
                                <cm:dropdownOption key=".down.label" icon="icon-arrow-down-orange" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${tapDownCommandHolder.commandId}" />
                                <cm:dropdownOption key=".enable.label" icon="icon-accept" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${enableRemoteCommandHolder.commandId}" />
                                <cm:dropdownOption key=".disable.label" icon="icon-delete" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${disableRemoteCommandHolder.commandId}" />
                            </cti:checkRolesAndProperties>
                    </cm:dropdown>
                </cti:checkRolesAndProperties>
            </td>
        </tr>
    </c:forEach>
</table>