<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<%-- Passthrough to inlineTree --%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="treeCss" %>
<%@ attribute name="treeParameters" description="This should be a object '{}' with arguments for tree initialization." %>
<%@ attribute name="triggerElement" %>
<%@ attribute name="highlightNodePath" %>
<%@ attribute name="multiSelect" type="java.lang.Boolean" %>
<%@ attribute name="includeControlBar" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="dataJson" %>

<%-- POPUP WINDOW SETUP --%>
<%-- buttonsList is used to add buttons (with handlers) to the window. It is not required. --%>
<%-- windowAttributes should be convertable to a js hash and contains any configs to override default window panel configs --%>
<%-- It should be in form of a list of hash. buttonsList should look something like this:  --%>
<%-- [{text:'Button 1', click:doAction1();},{text:'Button 2', click:doAction1();}] --%>
<%-- where doAction1() and doAction2() are javascript functions in your jsp --%>
<%@ attribute name="title" %>
<%@ attribute name="maxHeight"  type="java.lang.Integer" description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Defaults is 500." %>
<%@ attribute name="windowAttributes" %>
<%@ attribute name="buttonsList" %>

<c:if test="${!empty pageScope.treeCss}"><cti:includeCss link="${treeCss}"/></c:if>

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
    jQuery(document).ready(function() {
        var offsetTop,
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
                maxHeight = dialogCont.height() - paneHeights - 15;
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
        yukon.namespace('yukon.ui.dialogs');
        if ('undefined' === typeof yukon.ui.dialogs.${id}) {
            yukon.namespace('yukon.ui.dialogs.${id}');
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
            if ('undefined' === typeof yukon.ui.dialogs.${id}.offsetTop) {
                yukon.ui.dialogs.${id}.offsetTop = offsetTop;
            } else {
                offsetTop = yukon.ui.dialogs.${id}.offsetTop;
            }
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