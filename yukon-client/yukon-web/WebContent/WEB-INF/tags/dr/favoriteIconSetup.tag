<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
var addFavoriteUrl = '<cti:url value="/spring/dr/addFavorite"/>';
var removeFavoriteUrl = '<cti:url value="/spring/dr/removeFavorite"/>';

function updateFavoriteFromTemplate(linkId, templateLink, onclickFunction) {
	var oldLink = $(linkId);
	if (!oldLink) return;

    var newLink = templateLink.cloneNode(true);
    newLink.show();
    newLink.onclick = onclickFunction;

    oldLink.parentNode.replaceChild(newLink, oldLink);
    newLink.id = linkId;
}

function favoriteUpdated(transport, paoId, isAdd) {
    var templateLink = $((isAdd ? 'remove' : 'add') + 'FavoriteTemplate');
    var onclickFunction = isAdd
        ? function() { removeFavorite(paoId); }
        : function() { addFavorite(paoId); };
    updateFavoriteFromTemplate('favoritesIcon' + paoId, templateLink, onclickFunction);
    updateFavoriteFromTemplate('rvfavoritesIcon' + paoId, templateLink, onclickFunction);
}

function favoriteUpdateFailed(transport) {
	alert('update failed');
//    window.location = window.location;
}

function addFavorite(paoId) {
    updateFavorite(addFavoriteUrl, paoId, true);
}

function removeFavorite(paoId) {
    updateFavorite(removeFavoriteUrl, paoId, false);
}

function updateFavorite(requestUrl, paoId, isAdd) {
    new Ajax.Request(requestUrl, {
            'method': 'get',
            'parameters': { 'paoId': paoId },
            'onSuccess': function(transport) {
            	favoriteUpdated(transport, paoId, isAdd);
            },
            'onFailure': favoriteUpdateFailed
        });
}
</script>

<a id="removeFavoriteTemplate" href="javascript:void(0)" class="simpleLink" style="display: none">
    <cti:logo key="yukon.web.modules.dr.actions.removeFavoriteIcon"/>
</a>

<a id="addFavoriteTemplate" href="javascript:void(0)" class="simpleLink" style="display: none">
    <cti:logo key="yukon.web.modules.dr.actions.addFavoriteIcon"/>
</a>
