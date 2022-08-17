<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- submitCallback is optional, can be used for application specific behavior after submit button click is handled (like to submit a form or do nothing) --%>
<%@ attribute name="fieldId" required="true" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="fieldValue" %>
<%@ attribute name="nodeValueName" required="true" %>
<%@ attribute name="submitButtonText" required="true" %>
<%@ attribute name="cancelButtonText" required="true" %>
<%@ attribute name="submitCallback" %>

<%-- PASS THROUGH PARAMETERS TO ext:popupTree --%>
<%-- see popupTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="triggerElement" %>
<%@ attribute name="highlightNodePath" %>
<%@ attribute name="dataJson" type="java.lang.String" description="A dictionary starting with attributes of the root node. Either dataJson or dataUrl is required."%>
<%@ attribute name="dataUrl" type="java.lang.String" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>
<%@ attribute name="title" required="true" %>
<%@ attribute name="maxHeight" type="java.lang.Integer" description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Defaults is 500."%>
<%@ attribute name="noSelectionAlertText" %>
<%@ attribute name="multiSelect" type="java.lang.Boolean"%>
<%@ attribute name="includeControlBar" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" %>

<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

    window['selectedNodeId_${id}'] = null;
    window['saveValue_${id}'] = '${pageScope.fieldValue}';

    window['recordNameValue_${id}'] = function(select, node) {
        if (select) {
            var nodeValue = node.data.metadata['${nodeValueName}'];
            $('#${fieldId}').val(nodeValue);
            window['selectedNodeId_${id}'] = node;
        } else {
            // deselect happened
            $('#${fieldId}').val('');
            window['selectedNodeId_${id}'] = null;
        }
    };
    
    window['submitNodeSelection_${id}'] = function() {
        if (window['selectedNodeId_${id}'] == null && '${pageScope.noSelectionAlertText}' != '') {
            alert('${pageScope.noSelectionAlertText}');
            return false;
        }
        window['saveValue_${id}'] = $('#${fieldId}').val();
        window['closeWindow_${id}']();
        ${pageScope.submitCallback}
    };
    
    window['cancelNodeSelection_${id}'] = function() {
        window['selectedNodeId_${id}'] = null;
        $('#${fieldId}').val(window['saveValue_${id}']);
        window['closeWindow_${id}']();
    };
    
    window['closeWindow_${id}'] = function() {
        $('#window_${id}').dialog('close');
    };

</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="${pageScope.fieldValue}">

<%-- POPUP TREE --%>
<t:popupTree id="${id}"
             treeCss="/resources/js/lib/dynatree/skin/device.group.css"
             treeParameters="{onSelect:recordNameValue_${id}}"
             triggerElement="${triggerElement}"
             highlightNodePath="${pageScope.highlightNodePath}"
             dataJson="${dataJson}"
             dataUrl="${dataUrl}"
             title="${title}"
             maxHeight="${pageScope.maxHeight}"
             buttonsList="[{text:'${cancelButtonText}', click:cancelNodeSelection_${id}},{text:'${submitButtonText}', click:submitNodeSelection_${id}, 'class':'primary action'}]"
             multiSelect="${multiSelect}"
             includeControlBar="${includeControlBar}"
             styleClass="${styleClass}" />