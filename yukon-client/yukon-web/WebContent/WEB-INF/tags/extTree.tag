<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="dataUrl" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="rootVisible" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeCss link="/JavaScript/extjs/resources/css/ext-all.css"/>
<cti:includeCss link="/JavaScript/extjs/resources/css/xtheme-gray.css"/>
<c:choose>
    <c:when test="${not empty treeCss}">
        <cti:includeCss link="/JavaScript/extjs_cannon/resources/css/${treeCss}"/>
    </c:when>
    <c:otherwise>
        <cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/>
    </c:otherwise>
</c:choose>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>

<cti:uniqueIdentifier prefix="exttree" var="treeId"/>
<cti:uniqueIdentifier prefix="exttree" var="rootId"/>

<script type="text/javascript">

    Ext.onReady(function(){
    
        // tree loader
        var treeLoader = new Ext.tree.TreeLoader({
                dataUrl:"${dataUrl}",
                baseParams:${baseParams}
            })
        
        // tree panel
        var tree = new Ext.tree.TreePanel({
                el:"${treeId}",
                useArrows:false,
                autoScroll:true,
                animate:false,
                enableDD:false,
                border:false,
                rootVisible:${rootVisible},
                pathSeperator:'>',
                loader: treeLoader
            });
    
        // root node
        var root = new Ext.tree.AsyncTreeNode({
            text:"${name}",
            draggable:false,
            id:"myRoot"
        });
        tree.setRootNode(root);
        tree.render();
        
        // manage tree state
        var treeState = new TreePanelState(tree);
        treeState.init();
        tree.on('expandnode', treeState.onExpand, treeState);
        tree.on('collapsenode', treeState.onCollapse, treeState);
        treeState.restoreState(tree.root.getPath());
    });
    
</script>

<div id="${treeId}" style="height:${height}px;width:${width}px;"></div>