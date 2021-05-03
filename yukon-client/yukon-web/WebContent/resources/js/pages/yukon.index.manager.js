yukon.namespace('yukon.support.indexes');

/**
 * Module for the index manager page.
 * @module yukon.support.indexes
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.indexes = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _update = function () {
        $.getJSON(yukon.url('/index/status'))
        .done(function (indexes) {
            Object.keys(indexes).forEach(function (name) {
                
                var index = indexes[name];
                var row = $('#index-table').find('[data-index="' + name + '"]');
                var date = row.find('.js-date');
                var at = row.find('.js-date').text();
                
                date.text(index.date);
                if (index.date !== at) {
                    date.flash();
                }
                
                row.find('.js-build-btn').toggle(!index.building);
                row.find('.progress').toggleClass('dn', !index.building);
                row.find('.progress').toggleClass('df', index.building);
                if (index.building) {
                    row.find('.progress-bar').css({ width: index.percentDone + '%' });
                }
            });
            setTimeout(_update, 1000);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Send a build request when a build button in clicked. */
            $(document).on('click', '.js-build-btn', function (ev) {
                $(this).toggle();
                $(this).prev().removeClass('dn').addClass('df');
                var index = $(this).closest('tr').data('index');
                $.ajax(yukon.url('/index/' + index + '/build'));
            });
            
            _update();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.support.indexes.init(); });
