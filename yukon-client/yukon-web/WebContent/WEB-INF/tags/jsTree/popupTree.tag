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
<%-- buttonsList is used to add buttons (with handlers) to the window. It is not required. --%>
<%-- windowAttributes should be convertable to a js hash and contains any configs to override default window panel configs --%>
<%-- It should be in form of a list of hash. buttonsList should look something like this:  --%>
<%-- [{text:'Button 1', click:doAction1();},{text:'Button 2', click:doAction1();}] --%>
<%-- where doAction1() and doAction2() are javascript functions in your jsp --%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="maxHeight" required="false" type="java.lang.Integer" description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Defaults is 500."%>
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
                 maxHeight="${pageScope.maxHeight}"
                 includeControlBar="${includeControlBar}"
                 styleClass="${styleClass} popupTree contained"
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
        var offsetTop,
            dialogHeight,
            titleBarObj,
            groupTreeObj,
            dialogCont,
            buttonPaneObj,
            dialogContOffset,
            treeHelperPane,
            TREE_DIALOG_MAX_WIDTH = 450,
            calcMaxHeight = function (dialogCont, titleBar, treeHelper, buttonPane) {
                var paneHeights = titleBar.height() + treeHelper.height() + buttonPane.height(),
                    maxHeight;
                // 8-pixel slop addresses top and bottom padding on the elements comprising the popup
                maxHeight = dialogCont.height() - paneHeights - 8;
                return maxHeight;
            };
        <c:if test="${!empty pageScope.buttonsList}">
        var buttons = ${buttonsList}; 
        </c:if>
        
        <c:if test="${empty pageScope.buttonsList}">
        var buttons = [];
        </c:if>
        
        var args = {
                modal: true,
                <c:if test="${not empty pageScope.title}">
                title: "${title}",
                </c:if>
                minWidth: 300,
                buttons: buttons,
                resizable: true,
                autoOpen: false,
                draggable: true,
                resizeStop: function( event, ui ) {
                    var maxHeight = calcMaxHeight(dialogCont, titleBarObj, treeHelperPane, buttonPaneObj);
                    jQuery('#${id}').css('max-height', maxHeight);
                }
            };
        
        <c:if test="${!empty pageScope.windowAttributes}">
        var parameters = ${windowAttributes};
        for(key in parameters){
            args[key] = parameters[key];
        }
        </c:if>

        window_${id} = jQuery(document.getElementById("window_${id}")).dialog(args);

        groupTreeObj = jQuery('#' + 'window_${id}');
        titleBarObj = groupTreeObj.prev('.ui-dialog-titlebar');
        buttonPaneObj = jQuery(groupTreeObj.nextAll('.ui-dialog-buttonpane')[0]);
        dialogCont = groupTreeObj.closest('.ui-dialog');
        treeHelperPane = groupTreeObj.find('.tree_helper_controls');
        // ignores if present, creates if not
        Yukon.namespace('Yukon.ui.dialogs');
        if ('undefined' === typeof Yukon.ui.dialogs.${id}) {
            Yukon.namespace('Yukon.ui.dialogs.${id}');
        }
        <c:if test="${!empty pageScope.triggerElement}">
        //click a button, get the window
        jQuery(document.getElementById("${triggerElement}")).click(function(){
            var maxHeight;
            jQuery(document.getElementById("window_${id}")).dialog('open');
            offsetTop = dialogCont.offset().top;
            // save offset of popup once. Subsequently, dialogCont.offset().top appears to change
            // Store the first value of the top offset of the dialog. Subsequent values vary
            // considerably and undermine the positioning logic
            if ('undefined' === typeof Yukon.ui.dialogs.${id}.offsetTop) {
                Yukon.ui.dialogs.${id}.offsetTop = offsetTop;
            } else {
                offsetTop = Yukon.ui.dialogs.${id}.offsetTop;
            }
            dialogHeight = window.windowHeight - offsetTop;
            // height set on dialog. when tree expanded
            groupTreeObj.dialog('option', 'height', dialogHeight + titleBarObj.height());
            // make dialog wider so long entries don't force horizontal scrollbars
            groupTreeObj.dialog('option', 'width', TREE_DIALOG_MAX_WIDTH);
            // no scrollbars on tree div, prevents double scrollbars
            groupTreeObj.css('overflow', 'hidden');
            dialogContOffset = dialogCont.offset();
            dialogCont.offset({'left' : dialogContOffset.left, 'top' : offsetTop - buttonPaneObj.height()});
            // size tree height so it is fully scrollable
            maxHeight = calcMaxHeight(dialogCont, titleBarObj, treeHelperPane, buttonPaneObj);
            jQuery('#${id}').css('max-height', maxHeight);
        });
        </c:if>
    });
</script>