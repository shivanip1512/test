<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg2 var="noAssignedPrograms" key=".scenarioDetail.noAssignedPrograms"/>
<cti:msg2 var="noScenarioControl" key=".scenarioDetail.noControl"/>
<cti:msg2 var="startAction" key=".scenarioDetail.actions.start"/>
<cti:msg2 var="stopAction" key=".scenarioDetail.actions.stop"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown triggerClasses="fr">
        <tags:dynamicChoose updaterString="DR_SCENARIO/${paoId}/SHOW_ACTION" suffix="${paoId}">

            <tags:dynamicChooseOption optionId="hasNoPrograms">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noAssignedPrograms}">
                    ${startAction}
                </cm:dropdownOption>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noAssignedPrograms}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="enabled">
                <li>
                    <cti:url var="startScenarioUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="scenarioId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${startScenarioUrl}" 
                                       icon="icon-control-play-blue" labelKey=".scenarioDetail.actions.start"/>
                </li>
                <li>
                    <cti:url var="stopScenarioUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="scenarioId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${stopScenarioUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".scenarioDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>

        </tags:dynamicChoose>
    </cm:dropdown>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown triggerClasses="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noScenarioControl}">
            ${startAction}
        </cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noScenarioControl}">
            ${stopAction}
        </cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>
