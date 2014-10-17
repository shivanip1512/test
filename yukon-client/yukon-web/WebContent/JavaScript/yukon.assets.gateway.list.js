yukon.namespace('yukon.assets.gateway.list');

/**
 * Module that handles the behavior on the gatway list page (localhost:8080/yukon/stars/gateways).
 * @module yukon.assets.gateway.list
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.gateway.list = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _update = function (pending) {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            type: 'post',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ pending: pending })
        }).done(function (data) {
            console.log(data);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** Create gateway popup opened. */
            $(document).on('yukon:assets:gateway:load', function (ev) {
                if (!Modernizr.input.placeholder) {
                    $('#gateway-create-popup [placeholder]').placeholder();
                }
            });
            
            /** 'Save' button clicked on the create gateway popup. */
            $(document).on('yukon:assets:gateway:save', function (ev) {
                // TODO
            });
            
            var pending = [];
            $('[data-pending]').each(function (idx, item) {
                pending.push(new Number($(item).data('pending')));
            });
            
            _update(pending);
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });