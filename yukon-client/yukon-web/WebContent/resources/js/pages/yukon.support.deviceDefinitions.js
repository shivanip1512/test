yukon.namespace('yukon.support.deviceDefinitions');

/**
 * Module that manages the device definitions functionality
 * @module   yukon.support.deviceDefinitions
 * @requires JQUERY
 * @requires JQUERYUI
 */
yukon.support.deviceDefinitions = (function () {
    
    var _initialized = false,

    mod = {
            
        init: function () {
            if (_initialized) return;
            $('.deviceDefinition-select').on('change', function () {
                var currentId = $(this).attr('id');
                $('.deviceDefinition-select').each(function() {
                    if($(this).attr('id') != currentId){
                        $(this).val('');
                    }
                });
            	$('#filterForm').submit();
            });

            _initialized = true;
        }
        
    };

    return mod;

})();

$(function () { yukon.support.deviceDefinitions.init(); });