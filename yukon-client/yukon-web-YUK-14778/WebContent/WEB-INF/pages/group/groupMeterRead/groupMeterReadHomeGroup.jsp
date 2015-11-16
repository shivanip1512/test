<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="groupMeterReadHome">

<cti:msg var="selectAttributeLabel" key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>
<cti:msg var="selectGroupLabel" key="yukon.common.device.groupMeterRead.home.selectGroupLabel"/>
<cti:msg var="recentResultLinkLabel" key="yukon.common.device.groupMeterRead.home.recentResultsLinkLabel"/>

<cti:url var="groupMeterReadURL" value="/group/groupMeterRead/readGroup"/>

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
    <div class="error buffered">${errorMsg}</div>
</c:if>

<a href="<cti:url value="/group/groupMeterRead/resultsList"/>" class="fr">${recentResultLinkLabel}</a>
<form id="groupMeterReadForm" action="${groupMeterReadURL}" method="post">
    <cti:csrfToken/>
    <%-- GROUPS HIERARCHY BOX --%>
    <div class="stacked half-width">
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
                                            includeControlBar="true"/>
    </div>
    
    <%-- SELECT ATTRIBUTE --%>
    <tags:attributeSelector attributes="${allGroupedReadableAttributes}" name="attribute" 
        selectedAttributes="${selectedAttributes}" multipleSize="8" groupItems="true"/>
    
    <%-- READ BUTTON --%>
    <div class="page-action-area stacked">
        <cti:button type="submit" nameKey="read" busy="true" classes="primary action"/>
    </div>

</form>
    
</cti:standardPage>