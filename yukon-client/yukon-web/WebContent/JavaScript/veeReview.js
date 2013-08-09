jQuery(function() {

	$$('td.ACTION_TD').each(function(el) {
		
		Event.observe(el, 'click', function() {

			var h = getActionTdHash(el);
        	var action = h.get('action');
        	var valueEl = h.get('valueEl');

        	if (valueEl.value == action) {
        		resetDeleteAccept(action, h.get('deleteImgEl'), h.get('acceptImgEl'));
		        valueEl.value = '';
        	} else {
        		toggleDeleteAccept(action, h.get('deleteImgEl'), h.get('acceptImgEl'));
        		valueEl.value = action;
        	}
        });
	});
});

function checkUncheckAll(action) {
	
	var checkAllState = $('checkAllState');

	$$('td.ACTION_TD').each(function(el) {

		var h = getActionTdHash(el);
    	var tdAction = h.get('action');
    	var valueEl = h.get('valueEl');

		if (checkAllState.value == action) {
			resetDeleteAccept(tdAction, h.get('deleteImgEl'), h.get('acceptImgEl'));
			valueEl.value = '';
		} else {
        	toggleDeleteAccept(action, h.get('deleteImgEl'), h.get('acceptImgEl'));
    		valueEl.value = action;
		}
	});

	if (checkAllState.value == action) {
		checkAllState.value = '';
	} else {
		checkAllState.value = action;
	}
}

function resetDeleteAccept(action, deleteImgEl, acceptImgEl) {

	if (action == 'DELETE') {
	    jQuery(deleteImgEl).attr('disabled', 'disabled');
	}
	if (action == 'ACCEPT') {
	    jQuery(acceptImgEl).attr('disabled', 'disabled');
	}
}

function toggleDeleteAccept(action, deleteImgEl, acceptImgEl) {
	
	if (action == 'DELETE') {
	    jQuery(deleteImgEl).removeAttr('disabled');
	    jQuery(acceptImgEl).attr('disabled', 'disabled');
	}
	if (action == 'ACCEPT') {
	    jQuery(acceptImgEl).removeAttr('disabled');
        jQuery(deleteImgEl).attr('disabled', 'disabled');
	}
}

/**
 * 
 * @param el    {Element}
 * @returns     {Hash} 
 */
function getActionTdHash(el) {

	var descendants = el.descendants();
	var idParts = descendants[0].id.split('_');
	var action = idParts[1];
	var changeId = idParts[3];

	return $H({
	    'action': action,
	    'deleteImgEl': $('ACTION_DELETE_IMG_' + changeId),
        'acceptImgEl': $('ACTION_ACCEPT_IMG_' + changeId),
        'valueEl': $('ACTION_' + changeId)
	});
}