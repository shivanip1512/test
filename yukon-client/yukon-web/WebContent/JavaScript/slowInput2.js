
function slowInput2ButtonPress(id, formId, disableOtherButtons){
	
	$('slowInput2SpinnerImg_' + id).show();
	$('slowInput2DescriptionSpan_' + id).show();
	
	$('slowInput2Button_' + id).hide();
	$('slowInput2ButtonBusy_' + id).show();
	
    if (disableOtherButtons) {
	    $$('.formSubmit').each(function(el) {
			el.disabled = true;
		});
    }

    $(formId).submit();
}