jQuery(document).ready(function(){
    var calculationDiv = jQuery('#calculationFile');
    var importSelect = jQuery('#importTypeSelect');
    calculationDiv.hide();
    
    importSelect.change(function() {
        var selectedValue = importSelect.val();
        if(selectedValue === 'CALC_ANALOG' || selectedValue === 'CALC_STATUS') {
            jQuery('#calculationInstructions').show();
            calculationDiv.fadeIn(1000);
        } else {
            calculationDiv.fadeOut();
            jQuery('#calculationInstructions').hide();
        }
        jQuery('.instructions').hide();
        jQuery('#' + selectedValue + '_instructions').show();
    });
});