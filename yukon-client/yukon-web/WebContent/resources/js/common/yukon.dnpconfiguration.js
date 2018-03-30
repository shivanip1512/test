yukon.namespace('yukon.dnpconfiguration');
 
/** 
 * Module that makes an AJAX call and sets the value of dnp configuration fields on UI.
 * 
 * @module yukon.dnpconfiguration 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.dnpconfiguration = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on("change", "#dnp-config", function(event) {
                var configId = $(this).val();
                if (configId) {
                    var url = yukon.url('/deviceConfiguration/' + configId),
                        dnpFields = $('.js-dnp-fields');
                    yukon.ui.block(dnpFields, 200);
                    $.get(url)
                     .done(function (data) {
                        if (data.dnpConfiguration.deviceConfiguration.dnpCategory != null) {
                            $('.js-dnp-category').removeClass('dn');
                            $('.js-dnp-category-alert').addClass('dn');
                            data.dnpConfiguration.deviceConfiguration.dnpCategory.deviceConfigurationItems.forEach(function (field) {
                                var fieldName = field.fieldName,
                                    value = field.value;
                                if (fieldName == 'timeOffset') {
                                    value = data.dnpConfiguration.timeOffsetValue;
                                }
                                dnpFields.find('.js-dnp-' + fieldName).text(value);
                            });
                        } else { 
                            $('.js-dnp-category').addClass('dn');
                            var errorMessage = $('#dnpCategoryError').val().replace('{0}', data.dnpConfiguration.deviceConfiguration.name);
                            $('.js-dnp-category-alert').text(errorMessage);
                            $('.js-dnp-category-alert').removeClass('dn');
                        }
                    }).always(function () {
                        yukon.ui.unblock(dnpFields);
                    });
                }
            });
 
            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.dnpconfiguration.init(); });