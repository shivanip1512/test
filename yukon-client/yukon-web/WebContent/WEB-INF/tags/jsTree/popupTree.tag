<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti"     uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="jsTree"  tagdir="/WEB-INF/tags/jsTree" %>

<%-- Passthrough to inlineTree --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="treeParameters" required="false" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="false" type="java.lang.String"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="multiSelect"        required="false"     type="java.lang.Boolean"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
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

<c:choose>
    <c:when test="${!empty pageScope.treeCss}"><cti:includeCss link="${treeCss}"/></c:when>
    <c:otherwise><cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/></c:otherwise>
</c:choose>

<div class="dn">
    <!-- Hide the contents of this window. clicking the button will be MAGIC! -->
    <div id="window_${id}">
        <div class="some_contents">
            <jsTree:inlineTree id="${id}"
                 treeCss="${treeCss}"
                 treeParameters="${treeParameters}"
                 highlightNodePath="${highlightNodePath}"
                 dataJson="${dataJson}"
                 width="${width}"
                 height="${height}"
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
        
        <c:if test="${!empty pageScope.buttonsList}">
        var buttons = ${buttonsList}; 
        </c:if>
        
        <c:if test="${empty pageScope.buttonsList}">
        var buttons = [];
        </c:if>
        
        var args = {
                modal: true,
                width: ${width} + 16,
                <c:if test="${not empty pageScope.title}">
                title: "${title}",
                </c:if>
                buttons: buttons,
                resizable: false,
                autoOpen: false,
                draggable: false
            };
        
        <c:if test="${!empty pageScope.windowAttributes}">
        var parameters = ${windowAttributes};
        for(key in parameters){
            args[key] = parameters[key];
        }
        </c:if>

        window_${id} = jQuery(document.getElementById("window_${id}")).dialog(args);

        <c:if test="${!empty pageScope.triggerElement}">
        //click a button, get the window
        jQuery(document.getElementById("${triggerElement}")).click(function(){
             jQuery(document.getElementById("window_${id}")).dialog('open');
        });
        </c:if>
    });
</script>