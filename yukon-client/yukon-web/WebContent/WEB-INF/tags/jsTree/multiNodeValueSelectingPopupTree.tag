<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="t"  tagdir="/WEB-INF/tags/jsTree" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%@ attribute name="cancelButton" required="true" %>
<%@ attribute name="fieldId" required="true" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="nodeValueName" required="true" %>
<%@ attribute name="submitButton" required="true" %>
<%@ attribute name="submitEvent" description="The name of event that will be triggered when the submit button is clicked." %>

<%-- PASS THROUGH PARAMETERS TO tree:popupTree --%>
<%-- see popupTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="triggerElement" %>
<%@ attribute name="treeParameters" description="This should be a object '{}' with arguments for tree initialization." %>
<%@ attribute name="dataJson" description="A dictionary starting with attributes of the root node. Either dataJson or dataUrl is required."%>
<%@ attribute name="dataUrl" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>
<%@ attribute name="title" required="true" %>
<%@ attribute name="noSelectionAlert" %>

<%-- POPUP TREE --%>
<cti:msg2 var="titleText"  key="${title}" javaScriptEscape="true"/>
<cti:msg2 var="submitText" key="${submitButton}" javaScriptEscape="true"/>
<cti:msg2 var="cancelText" key="${cancelButton}" javaScriptEscape="true"/>
<cti:msg2 var="noSelectionAlertMessage" key="${noSelectionAlert}" javaScriptEscape="true"/> 

<cti:includeScript link="JQUERY_TREE" />

<%-- SELECTION HANDLER CODE --%>
<script type="text/javascript">

    // list of node values store in field
    var nodeValues_${id} = [];
    
    // set the hidden var with our selected node values concatenated
    // then trigger the submit event
    function setNodeValues_${id}() {
        var selectedNodes = $('#${id}').dynatree('getSelectedNodes');
        var nodeValues = [];
        
        for (var i=0; i < selectedNodes.length; i++) {
            nodeValues.push(selectedNodes[i].data.info['${nodeValueName}']);
        }
        
        if (nodeValues.length == 0 && '${noSelectionAlertMessage}' != '') {
            alert('${noSelectionAlertMessage}');
            return false;
        }
        
        $('#${fieldId}').val(nodeValues.join(','));
        closeWindow_${id}();
        <c:if test="${not empty pageScope.submitEvent}">
            $('#${id}').trigger('${submitEvent}');
        </c:if>
    }
    
    // the close button handler function
    // remove any selected node values from the nodeValues list before closing
    function clearAllNodeValues_${id}() {
        $(document.getElementById('${fieldId}')).val('');
        closeWindow_${id}();
    }
    
    function closeWindow_${id}() {
        $('#window_${id}').dialog('close');
    }
</script>

<%-- VALUE HIDDEN FIELD --%>
<input type="hidden" name="${fieldName}" id="${fieldId}" value="">
<t:popupTree buttonsList="[{text:'${cancelText}', click:clearAllNodeValues_${id}},{text:'${submitText}', click:setNodeValues_${id}, 'class': 'action primary'}]"
             dataJson="${dataJson}"
             dataUrl="${dataUrl}"
             id="${id}"
             includeControlBar="true" 
             multiSelect="true"
             title="${titleText}"
             treeCss="/resources/js/lib/dynatree/skin/device.group.css"
             treeParameters="${pageScope.treeParameters}"
             triggerElement="${pageScope.triggerElement}"/>