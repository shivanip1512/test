yukon.namespace('yukon.da.area');

/**
 * Module for the volt/var area page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.area = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** User clicked save on stations assignment dialog. */
            $(document).on('yukon:vv:area:stations:save', function (ev) {
                
                var container = $(ev.target),
                    areaId = container.data('areaId'),
                    stations = [];
                
                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    stations.push($(item).data('id'));
                });
                
                $.ajax({
                    url: yukon.url('/capcontrol/areas/' + areaId + '/stations'),
                    method: 'post',
                    data: { stations: stations }
                }).done(function () {
                    window.location.reload();
                });
                
            });
            
            /** User clicked save on edit info dialog. */
            $(document).on('yukon:vv:area:info:save', function (ev) {
                
                var dialog = $('.js-edit-info-popup');
                
                $('.js-edit-info-popup form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });
            
            _initialized = true;
            debug.debug('yukon.da.area module initialized.');
        }
        
    };
    
    return mod;
})();

$(function () { yukon.da.area.init(); });