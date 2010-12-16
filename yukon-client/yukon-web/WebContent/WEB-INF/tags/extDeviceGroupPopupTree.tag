<%-- BASIC TREE SETUP --%>
<%-- treeId will be used as part of the name of the state cookie (among other internal things I assume) --%>
<%-- treeCss is the path to a css file containing classes to override standard Cannon tree.css styles, if not specified --%>
<%--    /JavaScript/extjs_cannon/resources/css/tree.css is used (which contains general Cannon overrrides of Ext's tree.css) --%>
<%-- treeCallbacks is a string representation of a config hash for setting event listeners on the tree. --%>
<%--    The tag parameter should look like: treeCallbacks="{'eventType1':callbackFunction1, 'eventType2':callbackFunction2}" --%>
<%--    The callbacks themselves should be of the form: function callbackFunction1(node, event){} --%>
<%@ attribute name="treeId" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="rootVisible" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeCallbacks" required="false" type="java.lang.String"%>

<%-- POPUP WINDOW SETUP --%>
<%-- closeOnSubmit value will determine if popup will close when submit button is clicked, if not used, default is true --%>
<%-- submitHandler, closeHandler should be optional functions you want to run when the buton is clicked --%>
<%--    These should be specified in the tag like: submitHandler="myHandlerFunc();" --%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="submitText" required="true" type="java.lang.String"%>
<%@ attribute name="closeText" required="true" type="java.lang.String"%>
<%@ attribute name="modal" required="true" type="java.lang.String"%>
<%@ attribute name="closeOnSubmit" required="false" type="java.lang.Boolean"%>
<%@ attribute name="submitHandler" required="false" type="java.lang.String"%>
<%@ attribute name="closeHandler" required="false" type="java.lang.String"%>

<%-- TREE DATA FROM URL --%>
<%-- JSON should be a list of children of the root node (root node is created in javascript below) --%>
<%-- Use baseParams to append parameters to your dataUrl, baseParams should be a array dictionary as a string --%>
<%-- Since you'll be creating the root node in js, things like it's text, href, disable need to be set using rootAttributes parameter --%>
<%-- rootAttributes should be convertable to a js dictionary --%>
<%@ attribute name="dataUrl" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>
<%@ attribute name="rootAttributes" required="false" type="java.lang.String"%>

<%-- TREE DATA FROM STATIC JSON --%>
<%-- JSON should be a dictionary starting with attributes of the root node (root node is supplied by you) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>

<%-- TAG LIBS --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%-- CSS & JAVASCRIPT INCLUDES --%>
<cti:includeCss link="/JavaScript/extjs/resources/css/ext-all.css"/>
<cti:includeCss link="/JavaScript/extjs/resources/css/xtheme-gray.css"/>
<c:choose>
    <c:when test="${not empty pageScope.treeCss}">
        <cti:includeCss link="${pageScope.treeCss}"/>
    </c:when>
    <c:otherwise>
        <cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/>
    </c:otherwise>
</c:choose>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>

<%-- UNIQUE IDS --%>
<%-- id for button --%>
<cti:uniqueIdentifier prefix="${treeId}" var="buttonId"/>

<%-- THE BUTTON TO TRIGGER POPUP --%>
<input type="button" id="${buttonId}" value="${title}"/>

<script type="text/javascript">

    Ext.BLANK_IMAGE_URL = '/JavaScript/extjs/resources/images/default/s.gif';
    
    // add button click
    var button = Ext.get('${buttonId}');
    button.on('click', function(){
    
        // TREE ------------------------------------------------------------------------------------
        
        // fetch JSON data from a URL when dataUrl parameter is used
        // use local JSON expected in dataJson parameter otherwise
        <c:choose>
            <c:when test="${not empty pageScope.dataUrl}">
            
                var treeLoader = new Ext.tree.TreeLoader({
                        dataUrl:"${pageScope.dataUrl}"
                        <c:if test="${not empty pageScope.baseParams}">
                            , baseParams:${pageScope.baseParams}
                        </c:if>
                    })
                    
                var rootAttributes = $H(${pageScope.rootAttributes});
                rootAttributes.set('id', "${treeId}_root");
                var root = new Ext.tree.AsyncTreeNode(rootAttributes);
                    
            </c:when>
            <c:otherwise>
            
                var treeLoader = new Ext.tree.TreeLoader();
                var root = new Ext.tree.AsyncTreeNode(${pageScope.dataJson});
                
            </c:otherwise>
        </c:choose>
    
        // tree panel
        var tree = new Ext.tree.TreePanel({
                id:"${treeId}",
                useArrows:false,
                autoScroll:true,
                animate:false,
                enableDD:false,
                border:false,
                rootVisible:${rootVisible},
                pathSeperator:'>',
                loader: treeLoader,
                region: 'center',
                margins:'3 0 3 3',
                cmargins:'3 3 3 3',
                width: ${width}
            });
        tree.setRootNode(root);
        
        // manage tree state
        var treeState = new TreePanelState(tree);
        treeState.init();
        tree.on('expandnode', treeState.onExpand, treeState);
        tree.on('collapsenode', treeState.onCollapse, treeState);
        treeState.restoreState(tree.root.getPath());
        
        // tree callbacks
        <c:if test="${not empty pageScope.treeCallbacks}">
            tree.on(${pageScope.treeCallbacks});
        </c:if>
     
        
       
        // WINDOW ----------------------------------------------------------------------------------
        
        // submit button handler
        function submitHandler() {
            ${pageScope.submitHandler}
            <c:if test="${empty pageScope.closeOnSubmit || pageScope.closeOnSubmit}">
                win.hide();
            </c:if>
        }
        
        // close button handler
        function closeHandler() {
            ${pageScope.closeHandler}
            win.hide();
        }
        
        // the popup window
        var win = new Ext.Window({
            title: '${title}',
            width:${width},
            height:${height},
            layout: 'border',
            modal:${modal},
            items:[tree],
            buttons:[
                {text:'${submitText}', handler:submitHandler},
                {text:'${closeText}', handler:closeHandler}
            ]
        });

        win.show(this);
    
    });
    
</script>