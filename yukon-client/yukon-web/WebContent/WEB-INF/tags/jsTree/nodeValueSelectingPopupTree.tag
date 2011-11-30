<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- submitCallback is optional, can be used for application specific behavior after submit button click is handled (like to submit a form or do nothing) --%>
<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="nodeValueName" required="true" type="java.lang.String"%>
<%@ attribute name="submitButtonText" required="true" type="java.lang.String"%>
<%@ attribute name="cancelButtonText" required="true" type="java.lang.String"%>
<%@ attribute name="submitCallback" required="false" type="java.lang.String"%>

<%-- PASS THROUGH PARAMETERS TO ext:popupTree --%>
<%-- see popupTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="true" type="java.lang.String"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="noSelectionAlertText" required="false" type="java.lang.String"%>
<%@ attribute name="multiSelect"        required="false"     type="java.lang.Boolean"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>


<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

    var selectedNodeId_${id} = null;
    var saveValue_${id} = '${pageScope.fieldValue}';

    function recordNameValue_${id}(node) {
        // save group name
        var nodeValue = node.data.metadata["${nodeValueName}"];
        jQuery(document.getElementById("${fieldId}")).val(nodeValue);
        selectedNodeId_${id} = node;
    }
    
    function submitNodeSelection_${id}() {
        if (selectedNodeId_${id} == null && '${pageScope.noSelectionAlertText}' != '') {
            alert('${pageScope.noSelectionAlertText}');
            return false;
        }
        saveValue_${id} = jQuery(document.getElementById('${fieldId}')).val();
        closeWindow_${id}();
        ${pageScope.submitCallback}
    }
    
    function cancelNodeSelection_${id}() {
        selectedNodeId_${id} = null;
        jQuery(document.getElementById("${fieldId}")).val(saveValue_${id});
        closeWindow_${id}();
    }
    
    function closeWindow_${id}() {
        jQuery(document.getElementById("window_${id}")).dialog("close");
    }

</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="${pageScope.fieldValue}">

<%-- POPUP TREE --%>
<jsTree:popupTree  id="${id}"
                treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                treeCallbacks="{onActivate:recordNameValue_${id}}"
                triggerElement="${triggerElement}"
                highlightNodePath="${pageScope.highlightNodePath}"
                dataJson="${dataJson}"
                title="${title}"
                width="${width}"
                height="${height}" 
                buttonsList="[{text:'${submitButtonText}', click:submitNodeSelection_${id}},{text:'${cancelButtonText}', click:cancelNodeSelection_${id}}]"
                multiSelect="${multiSelect}"
                includeControlBar="${includeControlBar}"
                styleClass="${styleClass}" />