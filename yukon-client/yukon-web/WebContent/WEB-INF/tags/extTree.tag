<%-- treeId will be used as part of the name of the state cookie (among other internal things I assume) --%>
<%@ attribute name="treeId" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="rootVisible" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>

<%-- Required to retrieve JSON from a url --%>
<%-- JSON should be a list of children of the root node (root node is created in javascript below) --%>
<%-- Use baseParams to append parameters to your dataUrl, baseParams should be a array dictionary as a string --%>
<%-- Since you'll be creating the root node in js, things like it's text, href, disable need to be set using rootAttributes parameter --%>
<%-- rootAttributes should be convertable to a js dictionary --%>
<%@ attribute name="dataUrl" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>
<%@ attribute name="rootAttributes" required="false" type="java.lang.String"%>

<%-- Required to statically set the JSON --%>
<%-- JSON should be a dictionary starting with attributes of the root node (root node is supplied by you) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeCss link="/JavaScript/extjs/resources/css/ext-all.css"/>
<cti:includeCss link="/JavaScript/extjs/resources/css/xtheme-gray.css"/>
<c:choose>
    <c:when test="${not empty treeCss}">
        <cti:includeCss link="${treeCss}"/>
    </c:when>
    <c:otherwise>
        <cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/>
    </c:otherwise>
</c:choose>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/EventManager.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>

<%-- name of the div element to render the tree in, ok if it changes all the time --%>
<cti:uniqueIdentifier prefix="exttree" var="elId"/>

<script type="text/javascript">
    
    Ext.BLANK_IMAGE_URL = '/JavaScript/extjs/resources/images/default/s.gif';
    Ext.onReady(function(){
    
        // fetch JSON data from a URL when dataUrl parameter is used
        // use local JSON expected in dataJson parameter otherwise
        <c:choose>
            <c:when test="${not empty dataUrl}">
            
                var treeLoader = new Ext.tree.TreeLoader({
                        dataUrl:"${dataUrl}"
                        <c:if test="${not empty baseParams}">
                            , baseParams:${baseParams}
                        </c:if>
                    })
                    
                var rootAttributes = $H(${rootAttributes});
                rootAttributes['id'] = "${treeId}_root"
                var root = new Ext.tree.AsyncTreeNode(rootAttributes);
                    
            </c:when>
            <c:otherwise>
            
                var treeLoader = new Ext.tree.TreeLoader();
                var root = new Ext.tree.AsyncTreeNode(${dataJson});
                
            </c:otherwise>
        </c:choose>
        
        // tree panel
        var tree = new Ext.tree.TreePanel({
                id:"${treeId}",
                el:"${elId}",
                useArrows:false,
                autoScroll:true,
                animate:false,
                enableDD:false,
                border:false,
                rootVisible:${rootVisible},
                pathSeperator:'>',
                loader: treeLoader
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

<div id="${elId}" style="height:${height}px;width:${width}px;"></div>