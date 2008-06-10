<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/EventManager.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extTreeHelper.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extTreeMaker.js"/>
<c:choose>
    <c:when test="${not empty treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<%-- BASICS --%>
<%-- id will be the internal id of the tree, also will be name of dom elememt to access tree in jsp --%>
<%-- treeCss allows you to customize the styling of the tree's icons, etc --%>
<%-- create a new css file under /JavaScript/extjs_cannon/resources/css/ that overrides styles you want --%>
<%-- treeAttributes should be convertable to a js hash and contains any configs to override default tree panel configs --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeAttributes" required="false" type="java.lang.String"%>
<%@ attribute name="treeCallbacks" required="false" type="java.lang.String"%>

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

    // show on window load
    Ext.BLANK_IMAGE_URL = '/JavaScript/extjs/resources/images/default/s.gif';
    Ext.onReady(function(){
    
        // TREE STATIC SETUP
        <c:if test="${empty dataUrl}">
        
            var treeMaker = new ExtTreeMaker('${id}', true);
            treeMaker.setupStaticDataLoader(${dataJson});
            
        </c:if>
        
        // TREE ASYNC SETUP
        <c:if test="${not empty dataUrl}">
        
            var treeMaker = new ExtTreeMaker('${id}', false);
            treeMaker.setupAsyncDataLoader('${dataUrl}', ${baseParams}, $H(${rootAttributes}));
            
        </c:if>
        
        // SET TREE ATTRIBUTES
        treeMaker.setAttributes($H(${treeAttributes}));
        
        // SHOW TREE
        tree_${id} = treeMaker.showTree('${divId}');
        
        // TREE CALLBACKS
        <c:if test="${not empty treeCallbacks}">
            tree_${id}.on(${treeCallbacks});
        </c:if>
                
    });
    
    function toggleExpand(expand) {
        
        if (expand) {
            tree_${id}.root.expandChildNodes(true);
        }
        else {
            tree_${id}.root.collapseChildNodes(true);
        }
    }
    
</script>

<div id="internalTreeContainer" style="width:${width}px;height:${height + 30}px;">

    <div id="expandCollapseAllLinks" style="font-size:9px;text-align:right;padding-left:2px;padding-bottom:5px;">
        <a href="javascript:void(0);" onclick="toggleExpand(true);"><cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/></a> | 
        <a href="javascript:void(0);" onclick="toggleExpand(false);"><cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/></a>
    </div>
            
    <%-- ELEMENT TO RENDER TO --%>
    <div id="${divId}" style="width:${width}px;height:${height}px;"></div>

</div>
