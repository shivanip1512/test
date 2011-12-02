<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeScript link="JQUERY_COOKIE" force="true"/>
<cti:includeScript link="JQUERY_SCROLLTO" force="true"/>
<cti:includeScript link="JQUERY_TREE" force="true"/>
<cti:includeScript link="JQUERY_TREE_HELPERS" force="true"/>

<cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/ui.dynatree.css"/>

<c:choose>
    <c:when test="${not empty treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<%-- BASICS --%>
<%-- id will be the internal id of the tree, also will be name of dom elememt to access tree in jsp --%>
<%-- treeCss allows you to customize the styling of the tree's icons, etc --%>
<%-- create a new css file under /JavaScript/extjs_cannon/resources/css/ that overrides styles you want --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeCallbacks" required="false" type="java.lang.String"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>
<%@ attribute name="multiSelect" required="false" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<%-- ASYNC JSON --%>
<%-- json should be a list of children of the root node (root node is created in javascript!) --%>
<%-- Use baseParams to append parameters to your dataUrl, baseParams should be a array dictionary as a string --%>
<%-- Since you'll be creating the root node in js, things like it's text, href, disable need to be set using rootAttributes parameter --%>
<%-- rootAttributes is used to configure the root node, since it should not come async json --%>
<%@ attribute name="dataUrl" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>
<%@ attribute name="rootAttributes" required="false" type="java.lang.String"%>

<%-- STATIC JSON --%>
<%-- json should be a dictionary starting with attributes of the root node (root node is supplied by you!) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>

<%-- RENDER TO DIV SETUP --%>
<%-- width and height are used to size either the div element the tree is rendered to --%>
<%@ attribute name="divId" required="false" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>

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
                clean(data);
            </c:when>
            
            <c:otherwise >
            var data = {};
            </c:otherwise>
        </c:choose>
        
        <c:choose>
            <c:when test="${not empty highlightNodePath}">
                var highlight = "${highlightNodePath}".split("/");
                var open = highlight[highlight.length-2];
                var initially_select = highlight[highlight.length-1];
            </c:when>
            
            <c:otherwise>
                var initially_select = null;
            </c:otherwise>
       </c:choose>
        
        var args = {
            children: data,
            minExpandLevel: 2,
            onPostInit: function(isReloading, isError){
                if(initially_select){
                    this.selectKey(initially_select);
                    this.getNodeByKey(initially_select).makeVisible();
                }
                
            },
            
            <c:choose>
                <c:when test="${not empty multiSelect and multiSelect}">
                selectMode: 2,
                </c:when>
                
                <c:otherwise>
                selectMode: 1,
                </c:otherwise>
            </c:choose>
//             persist: true, //keep the tree in the last opened state
//             cookieId: "${pageScope.divId}",
            checkbox: false,
            onClick: function(node, event) {
                if(node.getEventTargetType(event) != 'expander'){
                    if(!node.data.isFolder){
                        node.toggleSelect();
                    }
                }
                    
            },
            clickFolderMode: 2,
            activeVisible: false
        };
        
        <c:if test="${not empty treeCallbacks}">
        //Merge the tree callbacks into the initialization object
        var callbacks = ${treeCallbacks};
        for(key in callbacks){
            args[key] = callbacks[key];
        }
        </c:if>
        
        jQuery(document.getElementById("selectDeviceGRoupNameTreeDiv_${id}")).dynatree(args);
    });
</script>

<div id="internalTreeContainer_${pageScope.divId}"  class="inlineTree ${styleClass}" style="width:${width}px; height:${height + 30}px;">

    <c:if test="${not empty includeControlBar and includeControlBar}">
        <div class="tree_helper_controls">
            <input type="text" class="searchTree default fr" data-tree-id="${pageScope.divId}" data-default-value="<cti:msg key="yukon.web.components.button.search.label"/>" value="<cti:msg key="yukon.web.components.button.search.label"/>" />
    
    <!--  YUK-7060        <a href="javascript:void(0);" onclick="toggleExpand(true);"><cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/></a> |-->
           
            <a href="javascript:void(0);" class="open_all fl" data-tree-id="${pageScope.divId}" 
            title="<cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/>">
                <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/>
            </a>
            <a href="javascript:void(0);" class="close_all fl" data-tree-id="${pageScope.divId}" 
            title="<cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/>">
                <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/>
            </a>
        </div>
    </c:if>
            
    <%-- THE TREE GOES HERE --%>
    <div class="tree_container">
    <div id="${pageScope.divId}" class="fl treeCanvas" style="height: ${height}px; width:100%; overflow:auto;"></div>
    </div>

</div>
