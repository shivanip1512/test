<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="id" required="true" description="The id of the tree element."%>
<%@ attribute name="treeCss" description="Customize the styling of the tree's icons, etc."%>
<%@ attribute name="treeParameters" description="This should be an object literal '{}' with arguments for tree initialization."%>
<%@ attribute name="highlightNodePath"%>
<%@ attribute name="includeControlBar" type="java.lang.Boolean"%>
<%@ attribute name="multiSelect" type="java.lang.Boolean"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="maxHeight" type="java.lang.Integer"
    description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Defaults is 500."%>
<%@ attribute name="dataJson" type="java.lang.String" description="A dictionary starting with attributes of the root node. Either dataJson or dataUrl is required."%>
<%@ attribute name="dataUrl" type="java.lang.String" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>
<%@ attribute name="scrollToHighlighted" type="java.lang.Boolean"%>

<cti:includeScript link="JQUERY_TREE" />
<cti:includeScript link="JQUERY_TREE_HELPERS" />

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css" />
<c:if test="${not empty pageScope.treeCss}">
    <cti:includeCss link="${treeCss}" />
</c:if>

<c:set var="maxHeight" value="${not empty maxHeight and maxHeight > 0  ? maxHeight : 500}" />

<script type="text/javascript">

    // reference to the tree gets created to it may be manipulated in jsp if needed.
    // name will be the id, prefixed with tree_
    var tree_${id};

    var clean = function(child) {
        child.title = child.text;
        child.key = child.id;
        child.addClass = child.iconCls;

        if (child.disabled) {
            child.addClass += ' disabled';
            child.unselectable = true;
        }

        if (child.href) {
            child.data.href = child.href;
        }

        for (var i=0; i < child.children.length; i++) {
            clean(child.children[i]);
        }
    }

    $(function(){
        
        <c:choose>
            <c:when test="${not empty pageScope.highlightNodePath}">
                var highlight = '${highlightNodePath}'.split('/');
                var initially_select = highlight[highlight.length-1];
            </c:when>
    
            <c:otherwise>
                var initially_select = null;
            </c:otherwise>
       </c:choose>
       
       var data = {};
       
       <c:if test="${not empty pageScope.dataJson}">
           data = ${pageScope.dataJson};
       </c:if>
    
        var args = {
            minExpandLevel: 2,    //prevent the top level elements (visually - dynatree has 1 hidden root by default) from expanding/collapsing
            onPostInit: function(isReloading, isError) {
                //show the initially selected item
                if (initially_select) {                     
                    this.selectKey(initially_select);
                    this.getNodeByKey(initially_select).makeVisible();
                    <c:if test="${not empty pageScope.scrollToHighlighted and scrollToHighlighted}">
                        this.activateKey(initially_select);
                    </c:if>
                //or open all of the first level children
                } else {
                    var root = this.getRoot();
                    if (root.childList != null) {
                        for (i = 0; i < root.childList.length; i++) {
                            root.childList[i].expand(true);
                        } 
                    }
                }
            },
            
            <c:choose>
                <c:when test="${not empty pageScope.dataUrl}">
                    initAjax: {
                	    url: '${pageScope.dataUrl}',
                	},
                </c:when>
                <c:otherwise>
                    children: data,
                </c:otherwise>
        	</c:choose>
    
            <c:choose>
                <c:when test="${not empty pageScope.multiSelect and multiSelect}">
                	selectMode: 2,
                </c:when>
                <c:otherwise>
                	selectMode: 1,
                </c:otherwise>
            </c:choose>
            checkbox: false,
            onClick: function(node, event) {
                if (node.getEventTargetType(event) != 'expander' && node.getEventTargetType(event) != 'checkbox'){
                    if (!node.data.isFolder) {
                        node.toggleSelect();
                    }
                }
    
            },
            onDblClick: function(node, event) {
                node.toggleExpand();
            },
            clickFolderMode: 2,
            activeVisible: false
        };
    
        <c:if test="${not empty pageScope.treeParameters}">
        //Merge the tree callbacks into the initialization object
        var callbacks = ${treeParameters};
        for (key in callbacks) {
            args[key] = callbacks[key];
        }
        </c:if>
        $('#${id}').dynatree(args);
    });
</script>

<div id="internalTreeContainer_${id}" class="inline-tree ${pageScope.styleClass}">
    <c:if test="${not empty pageScope.includeControlBar and pageScope.includeControlBar}">
        <div class="tree-controls clearfix">
            <cti:msg2 var="expand" key="yukon.common.expandAll" />
            <cti:msg2 var="collapse" key="yukon.common.collapseAll" />
            <cti:msg2 var="search" key="yukon.common.search.placeholder" />
            <cti:msg2 var="tooltip" key="yukon.web.components.jstree.input.search.tooltip" />
            <a href="javascript:void(0);" class="open-all fl" data-tree-id="${id}" title="${expand}">${expand}</a>
            <a href="javascript:void(0);" class="close-all fl" data-tree-id="${id}" title="${collapse}">${collapse}</a>
            <input type="text" class="tree-search fl" data-tree-id="${id}" placeholder="${search}" title="${tooltip}" />
        </div>
    </c:if>

    <%-- THE TREE GOES HERE --%>
    <div class="tree_container clearfix">
        <div id="${id}" class="fl tree-canvas" style="width:100%; overflow:auto;max-height: ${maxHeight}px;"></div>
    </div>
</div>