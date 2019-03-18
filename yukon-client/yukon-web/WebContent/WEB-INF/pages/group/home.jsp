<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage module="tools" page="deviceGroups">

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.deviceGroups.editor.tab.title" initiallySelected="true">
        <c:url value="/group/editor/home" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.amr.deviceGroupUpload.tab.title">
        <c:url value="/group/updater/upload" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    <c:if test="${not empty errorMessage}">
        <div class="js-error error">${fn:escapeXml(errorMessage)}</div>
    </c:if>
    
    <div id="homeViewDiv" class="column-12-12 clear">
        <div class="column one">
            <%--GROUPS HIERARCHY BOX--%>
            <cti:msg2 key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
            <cti:url value="/group/editor/home" var="baseGroupUrl"/>
            <tags:boxContainer title="${groupsTitle}" hideEnabled="false">
                   
                <jsTree:nodeValueSelectingInlineTree fieldId="groupName" 
                        fieldName="groupName"
                        nodeValueName="groupName" 
                        multiSelect="false"
                        id="selectGroupTree" 
                        dataJson="${allGroupsDataJson}" 
                        maxHeight="400" 
                        highlightNodePath="${extSelectedNodePath}"                         
                        scrollToHighlighted="true"
                        includeControlBar="true"/>
            </tags:boxContainer>
        </div>
        
        <div id="subViewDiv" class="column two nogutter">
            <%@ include file="selectedDeviceGroup.jsp" %>
        </div>
        
    </div>
    
    <!-- Add Subgroup Dialog -->
    <cti:msgScope paths="deviceGroups.editor.operationsContainer">
        <cti:msg2 var="invalidGroupNameError" key=".invalidGroupNameError"/>
        <cti:msg2 var="addSubgroupText" key=".addSubgroupText"/>
        <cti:msg2 var="subgroupNameLabel" key=".subgroupNameLabel" />
        <cti:msg2 var="subgroupTypeLabel" key=".subgroupTypeLabel" />
        <cti:msg2 var="subgroupTypeBasicLabel" key=".subgroupType.basicLabel" />
        <cti:msg2 var="subgroupTypeComposedLabel" key=".subgroupType.composedLabel" />
        <cti:msg2 var="subgroupEmptyGroupTitle" key=".subgroup.emptyGroupTitle" />
        <cti:msg2 var="subgroupComposedGroupTitle" key=".subgroup.composedGroupTitle" />
        <cti:msg2 var="subgroupNameSaveText" key=".subgroupNameSaveText" />
    </cti:msgScope>
                                
    <div id="addSubGroupPopup" title="${addSubgroupText}" class="groupEditorPopup dn">
        <form id="addSubGroupForm" method="post" action="<cti:url value="/group/editor/addChild"/>">
            <cti:csrfToken/>
            <div class="js-invalid-group-name error dn">${invalidGroupNameError}</div>
            <input type="hidden" id="addPopupGroupName" name="groupName">
            <tags:nameValueContainer>
                <tags:nameValue name="${subgroupNameLabel}" nameColumnWidth="120px">
                    <input id="childGroupName" name="childGroupName" type="text" maxlength="60"/>
                </tags:nameValue>
                <tags:nameValue name="${subgroupTypeLabel}">
                    <select name="subGroupType">
                        <option value="STATIC" title="${subgroupEmptyGroupTitle}">${subgroupTypeBasicLabel}</option>
                        <option value="COMPOSED" title="${subgroupComposedGroupTitle}">${subgroupTypeComposedLabel}</option>
                    </select>
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="action-area">
                <cti:button id="addSubGroupSaveButton" label="${subgroupNameSaveText}" classes="js-add-sub-grp-save primary action" busy="true"/>
            </div>
        </form>
    </div>
    
    <!-- Edit Group Name Dialog -->
    <cti:msgScope paths="deviceGroups.editor.operationsContainer">
        <cti:msg2 var="editGroupNameText" key=".editGroupNameText" />
        <cti:msg2 var="newGroupNameText" key=".newGroupNameText" />
        <cti:msg2 var="changeNameButtonText" key=".newGroupNameSaveText" />
    </cti:msgScope>
    
    <div id="editGroupNamePopup" title="${editGroupNameText}" class="groupEditorPopup dn">
        <form id="editGroupNameForm" method="post" action="<cti:url value="/group/editor/updateGroupName"/>" >
            <div class="js-invalid-group-name error dn">${invalidGroupNameError}</div>
            <cti:csrfToken/>    
            <input type="hidden" id="editPopupGroupName" name="groupName">
            <tags:nameValueContainer>
                <tags:nameValue name="${newGroupNameText}">
                    <input id="newGroupName" name="newGroupName" type="text" maxlength="60"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="action-area">
                <cti:button id="editGroupNameSaveButton" label="${changeNameButtonText}" classes="js-edit-grp-name-save primary action" busy="true"/>
            </div>
        </form>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.group.editor.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.device.selection.js"/>
    <cti:includeScript link="JQUERY_FILE_UPLOAD"/>
    
</cti:standardPage>