<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="nodeValueName" required="true" type="java.lang.String"%>

<%-- PASS THROUGH PARAMETERS TO ext:inlineTree --%>
<%-- see extInlineTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="treeAttributes" required="false" type="java.lang.String"%>

<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

    var selectedNodeId_${id} = null;
    var selectedNode_${id} = null;

    function recordNameValue_${id}(node, event) {
    
        // save group name
        var nodeValue = $H(node.attributes['info'])['${nodeValueName}'];
        $('${fieldName}').value = nodeValue;
        
        // highlight
        if (selectedNodeId_${id} != node.id) {
        
            if (selectedNode_${id} != null) {
                selectedNode_${id}.getUI().removeClass('highlightNode');
            }
        
            node.getUI().addClass('highlightNode');
            selectedNodeId_${id} = node.id;
            selectedNode_${id} = node;
        }
    }

</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="">

<%-- INLINE TREE --%>
<ext:inlineTree  id="${id}"
                    treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css"
                    treeAttributes="${treeAttributes}"
                    treeCallbacks="{'beforeclick':recordNameValue_${id}}"
                    
                    dataJson="${dataJson}"
                    
                    divId="selectDeviceGRoupNameTreeDiv_${id}"
                    width="${width}"
                    height="${height}" />