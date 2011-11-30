<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jsTree"  tagdir="/WEB-INF/tags/jsTree" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- multiSelect allows multiple node to be selected, 'fieldId' is set to node values --%>
<%-- values will be returned as a list the same way a <select multiple> element does --%>
<%@ attribute name="fieldId"            required="true"     type="java.lang.String"%>
<%@ attribute name="fieldName"          required="true"     type="java.lang.String"%>
<%@ attribute name="fieldValue"         required="false"    type="java.lang.String"%>
<%@ attribute name="nodeValueName"      required="true"     type="java.lang.String"%>
<%@ attribute name="multiSelect"        required="false"     type="java.lang.Boolean"%>

<%-- PASS THROUGH PARAMETERS TO jsTree:inlineTree --%>
<%-- see inlineTree.tag for parameter descriptions --%>
<%@ attribute name="id"                 required="true"     type="java.lang.String"%>
<%@ attribute name="dataJson"           required="true"     type="java.lang.String"%>
<%@ attribute name="width"              required="true"     type="java.lang.Integer"%>
<%@ attribute name="height"             required="true"     type="java.lang.Integer"%>
<%@ attribute name="highlightNodePath"  required="false"    type="java.lang.String"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>

<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

    var multiSelect = ${multiSelect};
    
    function refreshMultiNodeElements() {
    
        // remove all
        var sel = jQuery(document.getElementById("${fieldId}")).html("");
        
        // add selected
        var selected = jQuery("#selectDeviceGRoupNameTreeDiv_${id}").dynatree("getSelectedNodes");
        
        for(var i=0; i<selected.length; i++){
            var newOpt = document.createElement('option');
            newOpt.value = selected[i].data.metadata['${nodeValueName}'];
            newOpt.text = selected[i].data.metadata['${nodeValueName}'];
            newOpt.selected = true;
            jQuery(document.getElementById("${fieldId}")).append(newOpt);
        }
    }

    function recordNameValue_${id}(node) {
    
        var nodeValue = node.data.metadata['${nodeValueName}'];
        
        if (multiSelect) {
            // set field
            refreshMultiNodeElements();
            return false;   //carry over from ext implementation, NOT necessary for jQuery impl as far as I can tell
        }
        else {
            // save group name
            jQuery(document.getElementById("${fieldId}")).val(nodeValue);
        }
    }

</script>

<%-- VALUE HIDDEN FIELD --%>
<%-- the most straightforward way to manage multiple add/remove of DOM elements, use the <select multiple> tag --%>
<c:choose>
    <c:when test="${multiSelect}">
        <select name="${fieldName}" id="${fieldId}" style="display:none;" multiple></select>
    </c:when>
    <c:otherwise>
        <input type="hidden" name="${fieldName}" id="${fieldId}" value="${pageScope.fieldValue}">
    </c:otherwise>
</c:choose>

<%-- INLINE TREE --%>
<%-- Use onActivate here since clicking the node activaties AND selects it.  We pre-select
                    a node on page load.  Doing onSelect would cause the page to loop infinitely (since we redirect onSelect)
                    without some sort of code to handle the 'first' selection of a node.  Not really something I want
                    to deal with. --%>
<jsTree:inlineTree  id="${id}"
                    treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                    treeCallbacks="{onActivate:recordNameValue_${id}}"
                    dataJson="${dataJson}"
                    multiSelect="${pageScope.multiSelect}"
                    divId="selectDeviceGRoupNameTreeDiv_${id}"
                    width="${width}"
                    height="${height}"
                    highlightNodePath="${pageScope.highlightNodePath}"
                    includeControlBar="${pageScope.includeControlBar}" />