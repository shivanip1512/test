
function slowInput2ButtonPress(id, formId, disableOtherButtons, onsubmit){
	
	slowInput2TogglePressedStateOn(true, id, formId, disableOtherButtons);

    var doSubmit = true;
    if (onsubmit) {
    	doSubmit = onsubmit();
    }

    if (doSubmit) {
    	if (!$(formId).onsubmit || $(formId).onsubmit && $(formId).onsubmit()) {
        	$(formId).submit();
    	}
    } else {
    	slowInput2TogglePressedStateOn(false, id, disableOtherButtons);
    }
}

function slowInput2TogglePressedStateOn(toggleOn, id, formId, disableOtherButtons) {
	
	if (toggleOn) {
		$('slowInput2SpinnerImg_' + id).show();
		$('slowInput2DescriptionSpan_' + id).show();
		$('slowInput2Button_' + id).hide();
		$('slowInput2ButtonBusy_' + id).show();
	} else {
		$('slowInput2SpinnerImg_' + id).hide();
		$('slowInput2DescriptionSpan_' + id).hide();
		$('slowInput2Button_' + id).show();
		$('slowInput2ButtonBusy_' + id).hide();
	}
	
	if (disableOtherButtons) {
	    $$('#' + formId + ' .formSubmit').each(function(el) {
			el.disabled = toggleOn;
		});
    }
}