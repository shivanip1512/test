<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="hrefBase" required="true" type="java.lang.String"%>
<%@ attribute name="otherHrefParameters" required="true" type="java.lang.String"%>
<%@ attribute name="multiSelect"        required="false"     type="java.lang.Boolean"%>

<%-- PASS THROUGH PARAMETERS TO jsTree:inlineTree --%>
<%-- see inlineTree.tag for parameter descriptions --%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="highlightNodePath" required="false" type="java.lang.String"%>
<%@ attribute name="includeControlBar" required="false" type="java.lang.Boolean"%>

<%-- DEVICE GROUP SELECTION HANDLER CODE --%>
<script type="text/javascript">

//Needed to ensure that the initial selection of a node does not trigger the redirectToNameValue_... function
//to be called causing an infinte loop.  The official solution suggests that we include the jQuery cookies
//plugin and use the cookie functionality of jsTree.  I choose not to include it as we use the path in a very
//specific way on the back end. The potential for the the cookie and backend session to get out of sync is
//too risky so I am doing this instead.  Kinda ghetto, I know.
var firstNodeSelect = false;

function redirectToNameValue_${id}(node) {
    
    // build url and redirect
    var url = '${hrefBase}' + '?${name}' + '=' + encodeURIComponent(node.data.metadata["${name}"]);
    
    if ('${otherHrefParameters}' != '') {
        url += '&' + escape('${otherHrefParameters}');
    }
    
    document.location.href = url;
    return false;
}
</script>

<%-- INLINE TREE --%>
<jsTree:inlineTree id="${id}"
                treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                treeParameters="{onActivate: redirectToNameValue_${id}}"
                dataJson="${dataJson}"
                width="${width}"
                height="${height}"
                highlightNodePath="${pageScope.highlightNodePath}"
                includeControlBar="${pageScope.includeControlBar}"
                styleClass="bn" />