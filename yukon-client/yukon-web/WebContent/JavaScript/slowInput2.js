
function slowInput2ButtonPress(id, formId, disableOtherButtons, onsubmit){
	
	slowInput2TogglePressedStateOn(true, id, disableOtherButtons);
    
    var doSubmit = true;
    if (onsubmit) {
    	doSubmit = onsubmit();
    }

    if (doSubmit) {
    	$(formId).submit();
    } else {
    	slowInput2TogglePressedStateOn(false, id, disableOtherButtons);
    }
}

function slowInput2TogglePressedStateOn(toggleOn, id, disableOtherButtons) {
	
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
	    $$('.formSubmit').each(function(el) {
			el.disabled = toggleOn;
		});
    }
}