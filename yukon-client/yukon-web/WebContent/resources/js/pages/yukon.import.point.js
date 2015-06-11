/**
 * To Hide/show Calculation Instructions based on importSelect type 
 */
$(function(){
    var calculationDiv = $('#calculationFile');
    var importSelect = $('#importTypeSelect');
    calculationDiv.hide();
    
    importSelect.change(function() {
        var selectedValue = importSelect.val();
        if(selectedValue === 'CALC_ANALOG' || selectedValue === 'CALC_STATUS') {
            $('#calculationInstructions').show();
            calculationDiv.fadeIn(1000);
        } else {
            calculationDiv.fadeOut();
            $('#calculationInstructions').hide();
        }
        $('.instructions').hide();
        $('#' + selectedValue + '_instructions').show();
    });
});