<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="id" required="true" description="The id of the tree element."%>
<%@ attribute name="treeCss" description="Customize the styling of the tree's icons, etc."%>
<%@ attribute name="treeParameters" description="This should be a JSON object with arguments for tree initialization."%>
<%@ attribute name="highlightNodePath" description="The path for the node to initially select."%>
<%@ attribute name="includeControlBar" type="java.lang.Boolean" description="If true, display options to expand/collapse all nodes and search nodes. Default is false."%>
<%@ attribute name="multiSelect" type="java.lang.Boolean" description="If true, allow user to select multiple nodes in the tree. Default is false."%>
<%@ attribute name="styleClass" type="java.lang.String" description="Styling class to add to the outer div for the tree."%>
<%@ attribute name="maxHeight" type="java.lang.Integer"
    description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Default is 500."%>
<%@ attribute name="dataJson" type="java.lang.String" description="A dictionary starting with attributes of the root node."%>
<%@ attribute name="dataUrl" type="java.lang.String" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>
<%@ attribute name="scrollToHighlighted" type="java.lang.Boolean" description="If true, scroll the tree to the highlighted node."%>
<%@ attribute name="includeCheckbox" type="java.lang.Boolean" description="If true, a checkbox will be displayed before the node text."%>
<%@ attribute name="toggleNodeSelectionOnClick" type="java.lang.Boolean" description="If true and if includeCheckbox is true, the node will be selected if we click anywhere on the node. Eg. Expander icon, checkbox, node text. Defaults to true."%>

<c:set var="maxHeight" value="${not empty maxHeight and maxHeight > 0  ? maxHeight : 500}" />

<c:if test="${not empty pageScope.highlightNodePath}">
    <c:set var="highlight" value="${fn:split(highlightNodePath, '/')}"/>
    <c:set var="initiallySelect" value="${highlight[fn:length(highlight)-1]}"/>
</c:if>

<c:set var="toggleNodeSelectionOnClickFlag" value="${pageScope.toggleNodeSelectionOnClick}" />
<c:if test="${empty pageScope.toggleNodeSelectionOnClick}">
    <c:set var="toggleNodeSelectionOnClickFlag" value="${true}" />
</c:if>

<div id="internalTreeContainer_${id}" class="inline-tree ${pageScope.styleClass}">
    <c:if test="${not empty pageScope.includeControlBar and pageScope.includeControlBar}">
        <div class="fancytree-controls clearfix">
            <cti:msg2 var="expand" key="yukon.common.expandAll" />
            <cti:msg2 var="collapse" key="yukon.common.collapseAll" />
            <cti:msg2 var="search" key="yukon.common.search.placeholder" />
            <cti:msg2 var="tooltip" key="yukon.web.components.jstree.input.search.tooltip" />
            <a href="javascript:void(0);" class="fancytree-open-all fl" data-tree-id="${id}" title="${expand}">${expand}</a>
            <a href="javascript:void(0);" class="fancytree-close-all fl" data-tree-id="${id}" title="${collapse}">${collapse}</a>
            <input type="text" class="fancytree-search fl" data-tree-id="${id}" placeholder="${search}" title="${tooltip}" />
        </div>
    </c:if>
        
    <div class="tree_container clearfix">
        <div id="${id}" data-url="${dataUrl}" data-initially-select="${initiallySelect}" data-scroll-to-highlighted="${pageScope.scrollToHighlighted}"
            data-multi-select="${pageScope.multiSelect}" class="tree-canvas js-fancy-tree" style="width:100%;overflow:auto;max-height:${maxHeight}px;"
            data-include-checkbox="${pageScope.includeCheckbox}" data-toggle-node-selection-on-click="${toggleNodeSelectionOnClickFlag}">
            <c:if test="${not empty dataJson}">
                <cti:toJson id="js-json-data" object="${dataJson}"/>
            </c:if>
            <c:if test="${not empty treeParameters}">            
                <cti:toJson id="js-tree-parameters" object="${treeParameters}"/>
            </c:if>
        </div>
    </div>
</div>

<cti:includeScript link="/resources/js/common/yukon.ui.fancyTree.js"/>
<cti:includeScript link="/resources/js/lib/fancytree/jquery.fancytree.min.js"/>
<cti:includeCss link="/resources/js/lib/fancytree/skins/skin-lion/ui.fancytree.css"/>
<c:if test="${not empty pageScope.treeCss}">
    <cti:includeCss link="${treeCss}" />
</c:if>