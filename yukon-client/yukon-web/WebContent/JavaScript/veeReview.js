Event.observe(window, 'load', function() {

	$$('td.ACTION_TD').each(function(el) {
		
		el.onclick = function() {

			var h = getActionTdHash(el);
        	var action = h['action'];
        	var valueEl = h['valueEl'];

        	if (valueEl.value == action) {
        		resetDeleteAccept(action, h['deleteImgEl'], h['acceptImgEl'])
		        valueEl.value = '';
        	} else {
        		toggleDeleteAccept(action, h['deleteImgEl'], h['acceptImgEl']);
        		valueEl.value = action;
        	}
        }
	});
});

function checkUncheckAll(action) {

	var checkAllState = $('checkAllState');

	$$('td.ACTION_TD').each(function(el) {

		var h = getActionTdHash(el);
    	var tdAction = h['action'];
    	var valueEl = h['valueEl'];

		if (checkAllState.value == action) {
			resetDeleteAccept(tdAction, h['deleteImgEl'], h['acceptImgEl']);
			valueEl.value = '';
		} else {
        	toggleDeleteAccept(action, h['deleteImgEl'], h['acceptImgEl']);
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

	if (action == 'DELETE')
		deleteImgEl.src = '/WebConfig/yukon/Icons/delete_disabled_gray.gif';
	if (action == 'ACCEPT')
		acceptImgEl.src = '/WebConfig/yukon/Icons/tick_disabled_gray.gif';
}

function toggleDeleteAccept(action, deleteImgEl, acceptImgEl) {
	
	if (action == 'DELETE') {
		deleteImgEl.src = '/WebConfig/yukon/Icons/delete.gif';
		acceptImgEl.src = '/WebConfig/yukon/Icons/tick_disabled_gray.gif';
	}
	if (action == 'ACCEPT') {
		deleteImgEl.src = '/WebConfig/yukon/Icons/delete_disabled_gray.gif';
		acceptImgEl.src = '/WebConfig/yukon/Icons/accept.gif';
	}
}

function getActionTdHash(el) {

	var descendants = el.descendants();
	var idParts = descendants[0].id.split('_');
	var action = idParts[1];
	var changeId = idParts[3];

	var h = $H();
	h['action'] = action;
	h['deleteImgEl'] = $('ACTION_DELETE_IMG_' + changeId);
	h['acceptImgEl'] = $('ACTION_ACCEPT_IMG_' + changeId);
	h['valueEl'] = $('ACTION_' + changeId);

	return h;
}