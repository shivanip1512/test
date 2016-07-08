
$(document).on('click', '.js-configuration-type', function () {
    
    var toggle = $(this),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');
    

    if (newConfig) {
        $('.js-new-configuration').toggleClass('dn', false);
        $('.js-selected-config').hide();
    } else {
        $('.js-new-configuration').toggleClass('dn', true);
        $('#selectedConfiguration').val(0);
        
    }

});

function showSelectedConfiguration(selectedItem) {
    var id = selectedItem.value;
    $('.js-selected-config').hide();
    if (id > 0) {
        $('.js-selected-config').show();
    }
    $('.js-config-table').hide();
    $('#configTable_' + id).show();
}