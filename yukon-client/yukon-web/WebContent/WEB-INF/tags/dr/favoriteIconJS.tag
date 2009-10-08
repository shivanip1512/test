<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<script type="text/javascript">
function addFavorite(paoId) {
    <cti:url var="addFavoriteUrl" value="/spring/dr/addFavorite"/>
    new Ajax.Request('${addFavoriteUrl}', {
        'method': 'get',
        'parameters': 'paoId=' + paoId,
        <%-- reload without rerequesting every image/css file/etc. --%>
        'onComplete': function(transport) { window.location = window.location; }
    });
}

function removeFavorite(paoId) {
    <cti:url var="removeFavoriteUrl" value="/spring/dr/removeFavorite"/>
    new Ajax.Request('${removeFavoriteUrl}', {
        'method': 'get',
        'parameters': 'paoId=' + paoId,
        <%-- reload without rerequesting every image/css file/etc. --%>
        'onComplete': function(transport) { window.location = window.location; }
    });
}
</script>
