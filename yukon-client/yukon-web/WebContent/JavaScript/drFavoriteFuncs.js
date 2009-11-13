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
    window.location = window.location;
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
            'evalScripts': true,
            'onSuccess': function(transport, json) {
            	if (json['favoriteDidUpdate']) {
            		favoriteUpdated(transport, paoId, isAdd);
            	} else {
            		favoriteUpdateFailed();
            	}
            },
            'onFailure': favoriteUpdateFailed
        });
}
