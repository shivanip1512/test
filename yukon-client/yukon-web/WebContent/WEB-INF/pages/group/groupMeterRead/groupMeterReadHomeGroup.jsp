<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:msg var="selectAttributeLabel" key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>
<cti:msg var="selectGroupLabel" key="yukon.common.device.groupMeterRead.home.selectGroupLabel"/>
<cti:msg var="recentResultLinkLabel" key="yukon.common.device.groupMeterRead.home.recentResultsLinkLabel"/>
<cti:msg var="recentResultLink" key="yukon.common.device.groupMeterRead.home.recentResultsLink"/>
<cti:msg var="readButtonText" key="yukon.common.device.groupMeterRead.home.readButton"/>
    
<cti:standardPage module="tools" page="groupMeterReadHome">

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.deviceGroups.editor.tab.title">
        <c:url value="/group/editor/home" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.deviceGroups.commander.tab.title">
        <c:url value="/group/commander/groupProcessing" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.common.device.groupMeterRead.home.tab.title" initiallySelected="true">
        <c:url value="/group/groupMeterRead/homeGroup" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.amr.deviceGroupUpload.tab.title">
        <c:url value="/group/updater/upload" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    	<c:if test="${not empty errorMsg}">
    		<br>
    		<div class="error">${errorMsg}</div>
    	</c:if>
	
        <form id="groupMeterReadForm" action="/group/groupMeterRead/readGroup" method="post">
        <div class="column-12-12">
            <div class="column one">
                <%-- GROUPS HIERARCHY BOX --%>
                <cti:msg2 key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
                <tags:boxContainer title="${selectGroupLabel}" hideEnabled="false">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" selectGroupName="${groupName}" selectedNodePathVar="selectedNodePath"/>
                    <jsTree:nodeValueSelectingInlineTree fieldId="groupName" 
                                                        fieldName="groupName"
                                                        nodeValueName="groupName" 
                                                        fieldValue="${groupName}"
                                                        multiSelect="false"
                                                        maxHeight="400"
                                                        id="selectGroupTree" 
                                                        dataJson="${dataJson}" 
                                                        highlightNodePath="${selectedNodePath}"
                                                        includeControlBar="true" />
                </tags:boxContainer>

    			<%-- READ BUTTON --%>
                <div class="action-area stacked">
                    <tags:slowInput myFormId="groupMeterReadForm" labelBusy="${readButtonText}" label="${readButtonText}"/>
                </div>
    
    			<%-- RECENT RESULTS --%>
    			<span class="largeBoldLabel">${recentResultLinkLabel}</span> 
    			<a href="/group/groupMeterRead/resultsList">${recentResultLink}</a>
            </div>
            
            <div class="column two nogutter">
                <%-- SELECT ATTRIBUTE --%>
                <div class="largeBoldLabel">${selectAttributeLabel}:</div>
                <tags:attributeSelector attributes="${allGroupedReadableAttributes}" fieldName="attribute" 
                    selectedAttributes="${selectedAttributes}" multipleSize="8" groupItems="true"/>
            </div>
        </div>

		</form>
	
</cti:standardPage>