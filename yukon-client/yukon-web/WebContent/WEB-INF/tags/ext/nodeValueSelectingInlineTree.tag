<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- multiSelect allows multiple node to be selected, 'fieldId' is set to node values --%>
<%-- values will be returned as a list the same way a <select multiple> element does --%>
<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="nodeValueName" required="true" type="java.lang.String"%>
<%@ attribute name="multiSelect" required="true" type="java.lang.Boolean"%>

<%-- PASS THROUGH PARAMETERS TO ext:inlineTree --%>
<%-- see extInlineTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="treeAttributes" required="false" type="java.lang.String"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>

<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

    var selectedNodeId_${id} = null;
    var selectedNode_${id} = null;
    var nodeValues_${id} = $A();
    
    var multiSelect = ${multiSelect};
    
    function refreshMultiNodeElements() {
    
        // remove all
        var sel = $('${fieldId}');
        $A(sel.options).each(function(opt) {
            sel.remove(opt);
        });
        
        // add selected
        nodeValues_${id}.each(function(n) {
        
                var newOpt = document.createElement('option');
                $('${fieldId}').appendChild(newOpt);
                newOpt.value = n;
                newOpt.text = n;
                newOpt.selected = true;
            }
        );
    }

    function recordNameValue_${id}(node, event) {
    
        var nodeValue = $H(node.attributes['info']).get('${nodeValueName}');
        
        if (multiSelect) {
        
            // don't allow root to be added
            if (node.getDepth() == 0) {
                return false;
            }
            
            var nodeValueIdx = nodeValues_${id}.indexOf(nodeValue);
            var nodeUI = node.getUI();
            
            // not yet selected, add it
            if (nodeValueIdx < 0) {
            
                nodeUI.addClass('highlightNode');
                nodeValues_${id}.push(nodeValue);
                
            // already is selected, remove it
            }
            else {
                nodeUI.removeClass('highlightNode');
                nodeValues_${id}.splice(nodeValueIdx, 1);
            }
            
            // set field
            refreshMultiNodeElements();
            return false;
        
        }
        else {
    
            // save group name
            $('${fieldId}').value = nodeValue;
            
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
        
        
    }

</script>

<%-- VALUE HIDDEN FIELD --%>
<%-- the most straigtforward way to manage multiple add/remove of DOM elements, use the <select multiple> tag --%>
<c:choose>
    <c:when test="${multiSelect}">
        <select name="${fieldName}" id="${fieldId}" style="display:none;" multiple></select>
    </c:when>
    <c:otherwise>
        <input type="hidden" name="${fieldName}" id="${fieldId}" value="${pageScope.fieldValue}">
    </c:otherwise>
</c:choose>

<%-- INLINE TREE --%>
<ext:inlineTree  id="${id}"
                    treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css"
                    treeAttributes="${pageScope.treeAttributes}"
                    treeCallbacks="{'beforeclick':recordNameValue_${id}}"
                    
                    dataJson="${dataJson}"
                    
                    divId="selectDeviceGRoupNameTreeDiv_${id}"
                    width="${width}"
                    height="${height}"
                    highlightNodePath="${pageScope.highlightNodePath}" />