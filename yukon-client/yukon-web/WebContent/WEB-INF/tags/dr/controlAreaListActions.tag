<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">
<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg2 var="controlAreaHasNoPrograms" key=".controlAreaDetail.noAssignedPrograms"/>
<cti:msg2 var="controlAreaInactive" key=".controlAreaDetail.inactive"/>
<cti:msg2 var="controlAreaFullyActive" key=".controlAreaDetail.fullyActive"/>
<cti:msg2 var="noControlAreaControl" key=".controlAreaDetail.noControl"/>
<cti:msg2 var="startAction" key=".controlAreaDetail.actions.start"/>
<cti:msg2 var="stopAction" key=".controlAreaDetail.actions.stop"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown containerCssClass="fr">
        <tags:dynamicChoose updaterString="DR_CONTROLAREA/${paoId}/SHOW_ACTION" suffix="${paoId}">
            
            <tags:dynamicChooseOption optionId="noAssignedPrograms">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${controlAreaHasNoPrograms}">
                    ${startAction}
                </cm:dropdownOption>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${controlAreaHasNoPrograms}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="inactiveEnabled">
                <li>
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                                       icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
                </li>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${controlAreaInactive}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="enabled">
                <li>
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                                       icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
                </li>
                <li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${stopControlAreaUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="fullyActiveEnabled">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${controlAreaFullyActive}">
                    ${startAction}
                </cm:dropdownOption>
                <li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${stopControlAreaUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="inactiveDisabled">
                <li>
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                                       icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
                </li>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${controlAreaInactive}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="disabled">
                <li>
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                                       icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
                </li>
                <li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${stopControlAreaUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>
            
            <tags:dynamicChooseOption optionId="fullyActiveDisabled">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${controlAreaFullyActive}">
                    ${startAction}
                </cm:dropdownOption>
                <li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                                       dialogId="drDialog" actionUrl="${stopControlAreaUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>
        </tags:dynamicChoose>
    </cm:dropdown>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown containerCssClass="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noControlAreaControl}">
            ${startAction}
        </cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noControlAreaControl}">
            ${stopAction}
        </cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>
