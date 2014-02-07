<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="groupMeterReadHome">

<cti:msg var="selectAttributeLabel" key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>
<cti:msg var="selectGroupLabel" key="yukon.common.device.groupMeterRead.home.selectGroupLabel"/>
<cti:msg var="recentResultLinkLabel" key="yukon.common.device.groupMeterRead.home.recentResultsLinkLabel"/>

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
        <cti:csrfToken/>
        <div class="column-12-12">
            <div class="column one">
                <%-- GROUPS HIERARCHY BOX --%>
                
                <div><strong><i:inline key="yukon.web.deviceGroups.commander.groupSelectionLabel"/></strong></div>
                <div class="stacked">
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
                </div>
                
                <%-- READ BUTTON --%>
                <div class="page-action-area stacked">
                    <cti:button type="submit" nameKey="read" busy="true" classes="primary action"/>
                    <a href="/group/groupMeterRead/resultsList" class="fr">${recentResultLinkLabel}</a>
                </div>
            </div>
            
            <div class="column two nogutter">
                <%-- SELECT ATTRIBUTE --%>
                <div>${selectAttributeLabel}:</div>
                <tags:attributeSelector attributes="${allGroupedReadableAttributes}" fieldName="attribute" 
                    selectedAttributes="${selectedAttributes}" multipleSize="8" groupItems="true"/>
            </div>
        </div>

        </form>
    
</cti:standardPage>