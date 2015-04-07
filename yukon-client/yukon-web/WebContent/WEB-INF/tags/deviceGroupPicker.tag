<%@ tag body-content="empty" trimDirectiveWhitespaces="true" 
        description="
        Device group picker that sets selected group names in a hidden input.
        If using multi-selection mode, multiple hidden inputs with the same name are used
        and the controller should expect an array of Strings: 'String[] groups'.
        When a selection is made the 'yukon:tags:device:group:picker:chosen' event is fired
        with the dialog being the event target." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="inputName" required="true" description="The name of the hidden input(s)." %>
<%@ attribute name="inputValue" description="Optional initial group names selected." type="java.util.List" %>

<%@ attribute name="multi" type="java.lang.Boolean" description="If true, selection mode will be multiselection.
                                                                 Default: single selection." %>
<%@ attribute name="predicates" description="Optional comma separated strings of DeviceGroupPredicateEnum
                                             entry names to filter the tree data by. Default: 'NON_HIDDEN'." %>
<%@ attribute name="callbacks" type="java.util.Set" description="Callbacks to be preformed on the node. 
                                                                 Can be used to disable selecting certain nodes." %>

<%@ attribute name="classes" description="CSS class names applied to the component element." %>
<%@ attribute name="id" description="The html id attribute of the component element." %>
<%@ attribute name="dialogId" description="The html id attribute of the picker dialog element." %>

<cti:default var="multi" value="${false}"/>
<cti:default var="predicates" value="NON_HIDDEN"/>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>

<cti:includeScript link="JQUERY_TREE"/>
<cti:includeScript link="JQUERY_TREE_HELPERS"/>

<cti:deviceGroupHierarchyJson var="groups" predicates="${predicates}" callbacks="${callbacks}"/>
<c:set var="titleKey" value="${multi ? 'yukon.common.selectGroups.title' : 'yukon.common.selectGroup.title'}"/>
<c:set var="selectKey" value="${multi ? 'yukon.common.selectGroups' : 'yukon.common.selectGroup'}"/>

<a href="javascript:void(0);"
        <c:if test="${not empty pageScope.id}">id="${id}"</c:if> 
        <c:if test="${multi}">data-multi</c:if>
        data-groups="${fn:escapeXml(groups)}"
        data-select-text="<cti:msg2 key="${selectKey}"/>"
        data-more-text="<cti:msg2 key="yukon.common.selectGroup.more"/>"
        class="js-device-group-picker ${pageScope.classes}">
    <cti:icon icon="icon-folder-edit" classes="fn vatb"/>
    <c:choose>
        <c:when test="${empty inputValue}">
            <span><i:inline key="${selectKey}"/></span>
            <input type="hidden" name="${inputName}">
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${fn:length(inputValue) > 1}">
                    <span>${fn:escapeXml(inputValue[0])}&nbsp;
                        <i:inline key="yukon.common.selectGroup.more" arguments="${fn:length(inputValue) - 1}"/>
                    </span>
                </c:when>
                <c:otherwise>
                    <span>${fn:escapeXml(inputValue[0])}</span>
                </c:otherwise>
            </c:choose>
            <c:forEach var="group" items="${inputValue}">
                <input type="hidden" name="${inputName}" value="${group}">
            </c:forEach>
        </c:otherwise>
    </c:choose>
</a>

<div data-dialog class="js-device-group-picker-dialog inline-tree dn pr" 
        <c:if test="${not empty pageScope.dialogId}">id="${dialogId}"</c:if>
        data-title="<cti:msg2 key="${titleKey}"/>"
        data-width="400"
        data-event="yukon:tags:device:group:picker:chosen">
    
    <div class="tree-controls clearfix">
        <cti:msg2 var="expand" key="yukon.common.expandAll"/>
        <cti:msg2 var="collapse" key="yukon.common.collapseAll"/>
        <cti:msg2 var="search" key="yukon.common.search.placeholder"/>
        <cti:msg2 var="tooltip" key="yukon.web.components.jstree.input.search.tooltip"/>
        <a href="javascript:void(0);" class="open-all fl" title="${expand}">${expand}</a>
        <a href="javascript:void(0);" class="close-all fl" title="${collapse}">${collapse}</a>
        <input type="text" class="tree-search fl" placeholder="${search}" title="${tooltip}"/>
    </div>
    <div class="clearfix tree-canvas oa" style="max-height: 400px"></div>
</div>