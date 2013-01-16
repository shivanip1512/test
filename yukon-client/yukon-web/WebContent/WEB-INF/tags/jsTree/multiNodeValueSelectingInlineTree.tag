<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="jsTree"  tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="ct"  tagdir="/WEB-INF/tags"%>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%-- submitCallback is optional, can be used for application specific behavior after submit button click is handled (like to submit a form or do nothing) --%>
<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="nodeValueName" required="true" type="java.lang.String"%>
<%@ attribute name="submitCallback" required="false" type="java.lang.String"%>
<%@ attribute name="treeParameters" required="false" type="java.lang.String"%>

<%-- PASS THROUGH PARAMETERS TO tree:popupTree --%>
<%-- see popupTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="triggerElement" required="false" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="noSelectionAlert" required="false" type="java.lang.String"%>

<%-- POPUP TREE --%>
<cti:msg2 var="titleText"  key="${title}" javaScriptEscape="true"/>
<cti:msg2 var="submitText" key="${submitButton}" javaScriptEscape="true"/>
<cti:msg2 var="cancelText" key="${cancelButton}" javaScriptEscape="true"/>
<cti:msg2 var="noSelectionAlertMessage" key="${noSelectionAlert}" javaScriptEscape="true"/> 

<%-- SELECTION HANDLER CODE --%>
<script type="text/javascript">

    jQuery(document).ready(function() {
        jQuery(".error").hide();
    });
    // list of node values store in field
    var nodeValues_${id} = [];
    
    // set the hidden var with our selected node values concatenated
    // then call submit callback that will do whatever application specific function t wants with the values
    function setNodeValues_${id}() {
        var selectedNodes = jQuery(document.getElementById("${id}")).dynatree("getSelectedNodes");
        var nodeValues = [];
        
        for(var i=0; i<selectedNodes.length; i++){
            nodeValues.push(selectedNodes[i].data.info['${nodeValueName}']);
        }
        
        if (nodeValues.length == 0 && '${noSelectionAlertMessage}' != '') {
            jQuery(".error").show();
            return false;
        }
        
        jQuery(document.getElementById('${fieldId}')).val(nodeValues.join(","));
        ${pageScope.submitCallback}
        closeWindow_${id}();
        return true;
    }
    
    // the close button handler function
    // remove any selected node values from the nodeValues list before closing
    function clearAllNodeValues_${id}() {
        jQuery(document.getElementById('${fieldId}')).val("");
        closeWindow_${id}();
    }
    
    function closeWindow_${id}() {
        jQuery(document.getElementById("window_${id}")).dialog("close");
    }
</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="">

<div class="error">
    ${noSelectionAlertMessage}
</div>

<jsTree:inlineTree id="${id}"
                treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                dataJson="${dataJson}"
                treeParameters="${pageScope.treeParameters}"
                multiSelect="true"
                width="${width}"
                height="${height}" 
                includeControlBar="true" />     
                                