$(document).ready(function() {enableDisable();});

function enableDisable () {
    
    var toggle = $('.js-configuration-type'),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');

    if (newConfig) {
        $('.js-new-configuration').toggleClass('dn', false);
        $('.js-selected-config').hide();
    } else {
        $('.js-new-configuration').toggleClass('dn', true);
    }
}

// show/hide information based on New or Existing Configuration selection
$(document).on('click', '.js-configuration-type', function () {
    enableDisable();
});

//display table showing full data streaming configuration
function showSelectedConfiguration(selectedItem) {
    var id = selectedItem.value;
    $('.js-selected-config').hide();
    if (id > 0) {
        $('.js-selected-config').show();
    }
    $('.js-config-table').hide();
    $('#configTable_' + id).show();
}

//Validate user has either selected an existing config or turned at least one attribute on
$(document).on('click', '.js-next-button', function () {
    var validConfig = false;
    
    var selectedConfig = $('#selectedConfiguration').val();
    if (selectedConfig == 0) {
        $('.js-attribute').each(function () {
            var attOn = $(this).find('.switch-btn-checkbox').prop('checked');
            if (attOn) {
                validConfig = true;
            }
        });
    } else {
        validConfig = true;
    }

    if (validConfig) {
        $('#configureForm').submit();
    } else {
        $('.js-none-selected').show();
    }

});