<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- submitCallback is optional, can be used for application specific behavior after submit button click is handled (like to submit a form or do nothing) --%>
<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="nodeValueName" required="true" type="java.lang.String"%>
<%@ attribute name="submitButton" required="true" type="java.lang.String"%>
<%@ attribute name="cancelButton" required="true" type="java.lang.String"%>
<%@ attribute name="submitCallback" required="false" type="java.lang.String"%>

<%-- PASS THROUGH PARAMETERS TO ext:popupTree --%>
<%-- see popupTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="treeAttributes" required="false" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="true" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="noSelectionAlert" required="false" type="java.lang.String"%>

<%-- SELECTION HANDLER CODE --%>
<script type="text/javascript">

    // list of node values store in field
    var nodeValues_${id} = $A();
    
    
    
    // tree click callback
    // call during beforeclick and return false to stop the click event for occuring
    // this is done to prevent ext from setting the 'node-selected' class on it, which cannot
    // be removed through the API. We don't want 'node-selected' because that changes the backgroud-color
    // of the node, which we are trying to manage ourself in this case. Notice also that the 
    // 'highlightNode' class has !important so that ext's 'x-tree-node-over' class doesn't overshadow
    // our style 
    function addNodeValue_${id}(node, event) {
    
        // don't allow root to be added
        if (node.getDepth() == 0) {
            return false;
        }
    
        var nodeValue = $H(node.attributes['info']).get('${nodeValueName}');
        
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
        
        return false;
    }
    
    // set the hidden var with our selected node values concatenated
    // then call submit callback that will do whatever application specific function t wants with the values
    function setNodeValues_${id}() {
        
        if (nodeValues_${id}.length == 0 && '${noSelectionAlertMessage}' != '') {
        
            alert('${noSelectionAlertMessage}');
            return false;
        }
        
        $('${fieldId}').value = nodeValues_${id}.join(',');
        ${pageScope.submitCallback}
        closeWindow_${id}();
    }
    
    // the close button handler function
    // remove any selected node values from the nodeValues list bfore closing
    function clearAllNodeValues_${id}() {
    
        nodeValues_${id} = $A();
        $('${fieldId}').value = '';
        
        closeWindow_${id}();
    }
    
    
    function closeWindow_${id}() {
    
        var treeWindow = window['window_${id}'];
        treeWindow.close();
    }

</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="">

<%-- POPUP TREE --%>
<cti:msg2 var="titleText"  key="${title}" javaScriptEscape="true"/>
<cti:msg2 var="submitText" key="${submitButton}" javaScriptEscape="true"/>
<cti:msg2 var="cancelText" key="${cancelButton}" javaScriptEscape="true"/>
<cti:msg2 var="noSelectionAlertMessage" key="${noSelectionAlert}" javaScriptEscape="true"/> 

<ext:popupTree  id="${id}"
                treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css"
                treeAttributes="{}"
                treeCallbacks="{'beforeclick':addNodeValue_${id}}"
                triggerElement="${triggerElement}"
                
                dataJson="${dataJson}"
                
                title="${titleText}"
                width="${width}"
                height="${height}" 
                buttonsList="[{text:'${submitText}', handler:setNodeValues_${id}},{text:'${cancelText}', handler:clearAllNodeValues_${id}}]" />
                
                                
                                