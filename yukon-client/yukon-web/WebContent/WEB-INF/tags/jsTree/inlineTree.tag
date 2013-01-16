<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- RENDER TO DIV SETUP --%>
<%-- width and height are used to size either the div element the tree is rendered to --%>
<%@ attribute name="width" required="false" type="java.lang.Integer"%>
<%@ attribute name="height" required="false" type="java.lang.Integer"%>

<%-- BASICS --%>
<%-- id will be the internal id of the tree, also will be name of dom elememt to access tree in jsp --%>
<%-- treeCss allows you to customize the styling of the tree's icons, etc --%>
<%-- create a new css file under /JavaScript/extjs_cannon/resources/css/ that overrides styles you want --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeParameters" required="false" type="java.lang.String" description="This should be a object '{}' with arguments for tree initialization." %>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>
<%@ attribute name="multiSelect" required="false" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<%-- STATIC JSON --%>
<%-- json should be a dictionary starting with attributes of the root node (root node is supplied by you!) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>


<cti:includeScript link="JQUERY_COOKIE" />
<cti:includeScript link="JQUERY_SCROLLTO" />
<cti:includeScript link="JQUERY_TREE" />
<cti:includeScript link="JQUERY_TREE_HELPERS" />
<cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/ui.dynatree.css"/>
<c:choose>
    <c:when test="${not empty pageScope.treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<script type="text/javascript">

    // reference to the tree gets created to it may be manipulated in jsp if needed.
    // name will be the id, prefixed with tree_
    var tree_${id};

    var clean = function(child){
        child.title = child.text;
        child.key = child.id;
        child.addClass = child.iconCls;

        if(child.disabled){
            child.addClass += " disabled";
            child.unselectable = true;
        }

        if(child.href){
            child.data.href = child.href;
        }

        for(var i=0; i<child.children.length; i++){
            clean(child.children[i]);
        }
    }

    jQuery(document).ready(function(){
        //dataJson
        <c:choose>
            <c:when test="${not empty pageScope.dataJson}">
                var data = ${pageScope.dataJson};
            </c:when>

            <c:otherwise >
            var data = {};
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty pageScope.highlightNodePath}">
                var highlight = "${highlightNodePath}".split("/");
                var initially_select = highlight[highlight.length-1];
            </c:when>

            <c:otherwise>
                var initially_select = null;
            </c:otherwise>
       </c:choose>

        var args = {
            children: data,
            minExpandLevel: 2,	//prevent the top level elements (visually - dynatree has 1 hidden root by default) from expanding/collapsing
            onPostInit: function(isReloading, isError){
            	//show the initially selected item
                if(initially_select){
                    this.selectKey(initially_select);
                    this.getNodeByKey(initially_select).makeVisible();
                //or open all of the first level children
                }else{
                	var root = this.getRoot();
                	for(i=0; i<root.childList.length; i++){
                		root.childList[i].expand(true);
               		}
                }
            },

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
                if(node.getEventTargetType(event) != 'expander' && node.getEventTargetType(event) != 'checkbox'){
                    if(!node.data.isFolder){
                        node.toggleSelect();
                    }
                }

            },
            clickFolderMode: 2,
            activeVisible: false
        };

        <c:if test="${not empty pageScope.treeParameters}">
        //Merge the tree callbacks into the initialization object
        var callbacks = ${treeParameters};
        for(key in callbacks){
            args[key] = callbacks[key];
        }
        </c:if>
        jQuery(document.getElementById("${id}")).dynatree(args);
    });
</script>
<c:if test="${not empty width && width > 0}">
    <c:set var="treeWidth" value="width:${width}px;"/>
</c:if>
<c:if test="${not empty height && height > 0}">
    <c:set var="treeHeight" value="height:${height}px;"/>
</c:if>

<div id="internalTreeContainer_${id}"  class="inlineTree ${pageScope.styleClass}" style="${treeWidth} ${treeHeight}">

    <c:if test="${not empty pageScope.includeControlBar and pageScope.includeControlBar}">
        <div class="tree_helper_controls">
            <input type="text" class="searchTree default fr" data-tree-id="${id}"
                   data-default-value="<cti:msg key="yukon.web.components.button.search.label"/>"
                   value="<cti:msg key="yukon.web.components.button.search.label"/>"
                   title="<cti:msg key="yukon.web.components.jstree.input.search.tooltip"/>" />


            <%--  YUK-7060 - the old ext.js tree was much slower than dynatree, if we feel this is
            still a performance risk going forward we can comment out/remove the following link  --%>
            <a href="javascript:void(0);" class="open_all fl" data-tree-id="${id}"
            title="<cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/>">
                <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/>
            </a>
            <%--  END YUK-7060  --%>

            <a href="javascript:void(0);" class="close_all fl" data-tree-id="${id}"
            title="<cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/>">
                <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/>
            </a>
        </div>
    </c:if>

    <%-- THE TREE GOES HERE --%>
    <div class="tree_container">
        <div id="${id}" class="fl treeCanvas" style="height: ${height - 27}px; width:100%; overflow:auto;"></div>
    </div>

</div>
