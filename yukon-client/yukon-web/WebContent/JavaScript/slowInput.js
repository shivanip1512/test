/*  This function takes care of resetting the source of the image.  By doing this reset it 
allows animated gifs to continue running even after the submit or link has happened.
*/
function formSubmit(myForm, imgId, imgPath){
    $(myForm).submit();
    $(imgId).src = imgPath;
}

/* This function is used with the slowInput.tag to allow a user to see that a form has been
submitted in cases were a form takes a bit of time to process.
*/
function updateButton(actionButton, waitingLabel, myForm, imgId, disableOtherButtons){
    $(actionButton).getElementsBySelector('.slowInput_waiting').invoke('show');
    var input = $(actionButton).getElementsBySelector('input').first();
    var initialLabel = input.value;
    input.value = waitingLabel;
    
    if (disableOtherButtons) {
	    $$('input[type=button]').each(function(el) {
			el.disable();
		});
    } else {
    	input.disable();
    }
    
    formSubmit(myForm, imgId, '/WebConfig/yukon/Icons/spinner.gif');
}