<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
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
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.group.editor.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.device.selection.js"/>
    <cti:includeScript link="JQUERY_FILE_UPLOAD"/>
    
  
    
</cti:standardPage>