<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti"     uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="jsTree"  tagdir="/WEB-INF/tags/jsTree" %>

<%-- <cti:includeScript link="JSTREE" force="true"/> --%>
<cti:includeScript link="SCROLLTO" force="true"/>

<c:choose>
    <c:when test="${not empty treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<cti:includeScript link="/JavaScript/lib/jQuery/plugins/dynatree/jquery.dynatree.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/ui.dynatree.css"/>

<%-- BASICS --%>
<%-- id will be the internal id of the tree, also will be name of dom elememt to access tree in jsp --%>
<%-- treeCss allows you to customize the styling of the tree's icons, etc --%>
<%-- create a new css file under /JavaScript/extjs_cannon/resources/css/ that overrides styles you want --%>
<%-- treeAttributes should be convertable to a js hash and contains any configs to override default tree panel configs --%>
<%-- triggerElement is the id of the page (button, link, etc) whose 'click' signal will trigger the popup to open --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeCallbacks" required="false" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="true" type="java.lang.String"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="multiSelect"        required="false"     type="java.lang.Boolean"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

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

<div class="dn">
    <!-- Hide the contents of this window. clicking the button will be MAGIC! -->
    <div id="window_${id}">
        <div class="some_contents">
            <jsTree:inlineTree id="${id}" treeCss="${treeCss}" treeCallbacks="${treeCallbacks}"
                 highlightNodePath="${highlightNodePath}" dataUrl="${dataUrl}"
                 baseParams="${baseParams}" rootAttributes="${rootAttributes}" dataJson="${dataJson}"
                 divId="selectDeviceGRoupNameTreeDiv_${id}" width="${width}" height="${height}"
                 includeControlBar="${includeControlBar}"
                 styleClass="${styleClass} popupTree"
                 multiSelect="${multiSelect}" />
        </div>
    </div>
</div>

<script type="text/javascript">

    // reference to the tree and window get created to it may be manipulated in jsp if needed.
    // names will be the id, prefixed with tree_
    var tree_${id};
    var window_${id};
    
    jQuery(document).ready(function(){
        
        <c:if test="${not empty buttonsList}">
        var buttons = ${buttonsList}; 
        </c:if>
        
        <c:if test="${empty buttonsList}">
        var buttons = [];
        </c:if>

        window_${id} = jQuery(document.getElementById("window_${id}")).dialog({
                modal: true,
                width: ${width} + 30,
                buttons: buttons,
                resizable: false,
                autoOpen: false,
                draggable: false
            });

        //click a button, get the window
        jQuery(document.getElementById("${triggerElement}")).click(function(){
             jQuery(document.getElementById("window_${id}")).dialog('open');
        });
    });
    var oh_s = ${dataJson};
</script>