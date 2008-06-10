<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/EventManager.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extTreeHelper.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extTreeMaker.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extWindowMaker.js"/>
<c:choose>
    <c:when test="${not empty treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<%-- BASICS --%>
<%-- id will be the internal id of the tree, also will be name of dom elememt to access tree in jsp --%>
<%-- treeCss allows you to customize the styling of the tree's icons, etc --%>
<%-- create a new css file under /JavaScript/extjs_cannon/resources/css/ that overrides styles you want --%>
<%-- treeAttributes should be convertable to a js hash and contains any configs to override default tree panel configs --%>
<%-- triggerElement is the id of the page (button, link, etc) whose 'click' signal will trigger the popup to open --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeAttributes" required="false" type="java.lang.String"%>
<%@ attribute name="treeCallbacks" required="false" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="true" type="java.lang.String"%>

<%-- ASYNC JSON --%>
<%-- json should be a list of children of the root node (root node is created in javascript with rootAttributes!) --%>
<%-- Use baseParams to append parameters to your dataUrl, baseParams should be a array dictionary as a string --%>
<%-- Since you'll be creating the root node in js, things like it's text, href, disable need to be set using rootAttributes parameter --%>
<%-- rootAttributes is used to configure the root node, since it should not come async json --%>
<%@ attribute name="dataUrl" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>
<%@ attribute name="rootAttributes" required="false" type="java.lang.String"%>

<%-- STATIC JSON --%>
<%-- json should be a dictionary starting with attributes of the root node (root node is supplied by you!) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>

<%-- POPUP WINDOW SETUP --%>
<%-- width and height are used to size the popup its rendered in --%>
<%-- buttonsList is used to add buttons (with handlers) to the window. It is not required. --%>
<%-- windowAttributes should be convertable to a js hash and contains any configs to override default window panel configs --%>
<%-- It should be in form of a list of hash. buttonsList should look something like this:  --%>
<%-- [{text:'Button 1', handler:doAction1();},{text:'Button 2', handler:doAction1();}] --%>
<%-- where doAction1() and doAction2() are javascript functions in your jsp --%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="windowAttributes" required="false" type="java.lang.String"%>
<%@ attribute name="buttonsList" required="false" type="java.lang.String"%>

<script type="text/javascript">

    // reference to the tree and window get created to it may be manipulated in jsp if needed.
    // names will be the id, prefixed with tree_ and window_
    var tree_${id};
    var window_${id};
    
    Ext.BLANK_IMAGE_URL = '/JavaScript/extjs/resources/images/default/s.gif';
    
    // add button click
    var button = Ext.get('${triggerElement}');
    button.on('click', function(){
    
        // TREE
        //------------------------------------------------------------------------------------------
    
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
        // add additional attributes needed to embed it in a window
        treeMaker.setAttributes($H(${treeAttributes}));
        
        var treeRegionAttributes = $H({'region':'center','margins':'3 0 3 3','cmargins':'3 3 3 3'});
        treeMaker.setAttributes(treeRegionAttributes);
        
        // GET TREE
        // set tree state since getTree() only returns the tree
        tree_${id} = treeMaker.getTree();
        
        
        
        // TREE CALLBACKS
        <c:if test="${not empty treeCallbacks}">
            tree_${id}.on(${treeCallbacks});
        </c:if>
        
        // WINDOW 
        //------------------------------------------------------------------------------------------
        
        var windowMaker = new ExtWindowMaker();
        
        // SET ITEMS
        windowMaker.setItems([tree_${id}]);
        
        // SET BUTTONS
        windowMaker.setButtons(${buttonsList});
        
        // SET OTHER WINDOW ATTRIBUTES
        windowMaker.setAttributes($H(${windowAttributes}));
        
        // SHOW WINDOW
        window_${id} = windowMaker.showWindow('${title}', ${width}, ${height});
        
        treeMaker.setState(tree_${id});
        
        
    });
    
</script>