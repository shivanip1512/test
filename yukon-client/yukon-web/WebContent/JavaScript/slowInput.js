/*  This function takes care of resetting the source of the image.  By doing this reset it 
allows animated gifs to continue running even after the submit or link has happened.
*/
function formSubmit (myForm, imgId, imgPath) {
    jQuery('#' + myForm).submit();
    jQuery('#' + imgId).attr('src', imgPath);
}

/* This function is used with the slowInput.tag to allow a user to see that a form has been
submitted in cases were a form takes a bit of time to process.
*/
function updateButton (actionButton, waitingLabel, myForm, imgId, disableOtherButtons) {
    var input;
    jQuery('#' + actionButton).find('.slowInput_waiting').show();
    input = jQuery('#' + actionButton).find('input');
    input.val(waitingLabel);
    if (disableOtherButtons) {
        jQuery("input[type='button']").prop('disabled', true);
    } else {
        input.prop('disabled', true);
    }

    formSubmit(myForm, imgId, '/WebConfig/yukon/Icons/spinner.gif');
}