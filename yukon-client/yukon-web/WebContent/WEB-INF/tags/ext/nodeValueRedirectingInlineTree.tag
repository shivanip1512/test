<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<%-- name of key in the selected node's info attribute --%>
<%-- also will be the name of the hidden field on which the value is set --%>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="hrefBase" required="true" type="java.lang.String"%>
<%@ attribute name="otherHrefParameters" required="true" type="java.lang.String"%>

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

    function redirectToNameValue_${id}(node, event) {
    
        // build url and redirect
        var nodeValue = $H(node.attributes['info']).get('${name}');
        
        var url = '${hrefBase}' + '?${name}' + '=' + encodeURIComponent(nodeValue);
        
        if ('${otherHrefParameters}' != '') {
            url += '&' + escape('${otherHrefParameters}');
        }
        
        window.location = url;
        return false;
    }

</script>

<%-- INLINE TREE --%>
<ext:inlineTree id="${id}"
                treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css"
                treeAttributes="${pageScope.treeAttributes}"
                treeCallbacks="{'beforeclick':redirectToNameValue_${id}}"
                
                dataJson="${dataJson}"
                
                divId="selectDeviceGRoupNameTreeDiv_${id}"
                width="${width}"
                height="${height}"
                highlightNodePath="${pageScope.highlightNodePath}" />