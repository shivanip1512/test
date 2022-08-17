<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<%-- Passthrough to inlineTree --%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="treeCss" %>
<%@ attribute name="treeParameters" description="This should be a object '{}' with arguments for tree initialization." %>
<%@ attribute name="triggerElement" description="The elements that trigger the popup.  If there are multiple, 
                                        they must be separated by a comma and any after the first one need to have a # symbol.  Example: triggerElement1, #triggerElement2"%>
<%@ attribute name="highlightNodePath" %>
<%@ attribute name="multiSelect" type="java.lang.Boolean" %>
<%@ attribute name="includeControlBar" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="dataJson" type="java.lang.String" description="A dictionary starting with attributes of the root node. Either dataJson or dataUrl is required."%>
<%@ attribute name="dataUrl" type="java.lang.String" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>

<%-- POPUP WINDOW SETUP --%>
<%-- buttonsList is used to add buttons (with handlers) to the window. It is not required. --%>
<%-- It should be in form of a list of hash. buttonsList should look something like this:  --%>
<%-- [{text:'Button 1', click:doAction1();},{text:'Button 2', click:doAction1();}] --%>
<%-- where doAction1() and doAction2() are javascript functions in your jsp --%>
<%@ attribute name="title" %>
<%@ attribute name="maxHeight"  type="java.lang.Integer" description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Defaults is 500." %>
<%@ attribute name="buttonsList" %>

<c:if test="${!empty pageScope.treeCss}"><cti:includeCss link="${treeCss}"/></c:if>

<div id="window_${id}" class="dn">
        <jsTree:inlineTree id="${id}"
             treeCss="${treeCss}"
             treeParameters="${treeParameters}"
             highlightNodePath="${highlightNodePath}"
             dataJson="${dataJson}"
             dataUrl="${dataUrl}"
             maxHeight="${pageScope.maxHeight}"
             includeControlBar="${includeControlBar}"
             styleClass="${styleClass} popupTree"
             multiSelect="${multiSelect}"/>
</div>

<script type="text/javascript">

    // reference to the tree and window get created to it may be manipulated in jsp if needed.
    // names will be the id, prefixed with tree_
    var tree_${id};
    var window_${id};
    $(function() {
        var calcMaxHeight = function (container) {
                return $(container).height() - 29; // height 26px plus margin 3px of controls element
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
                minWidth: 400,
                buttons: buttons,
                resizable: true,
                autoOpen: false,
                draggable: true,
                resizeStop: function(event, ui) {
                    var maxHeight = calcMaxHeight(event.target);
                    $('#${id}').css('max-height', maxHeight);
                }
            };
        
        window_${id} = $(document.getElementById("window_${id}")).dialog(args);
        
        if ('${!empty pageScope.triggerElement}' === 'true') {
            //click a button, get the window
            $(document).on('click', '#${triggerElement}', function () {
                var maxHeight,
                    dialog = $('#' + 'window_${id}')
                    windowHeight = $(window).height(),
                    dialogMaxHeight = windowHeight * 0.70,
                    divHeigth = windowHeight * 0.50;
                
                // prevents double scrollbars on tree container
                dialog.css('overflow', 'hidden');

                // Set the max-height of the div that displays the tree inside the dialog.
                dialog.find('div.tree-canvas').css('max-height',divHeigth);
                
                // Initialize the dialog's height to resize automatically and also set the maximum height to which it can grow.
                dialog.dialog({
                    "height" : "auto",
                    "maxHeight" : dialogMaxHeight
                });
                
                dialog.dialog('open');
            });
        }
    });
</script>