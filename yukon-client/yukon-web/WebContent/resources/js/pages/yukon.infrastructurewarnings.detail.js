yukon.namespace('yukon.infrastructurewarnings.detail');

/**
 * Module for the Infrastructure Warnings Detail page
 * @module yukon.infrastructurewarnings.detail
 * @requires JQUERY
 * @requires yukon
 */
yukon.infrastructurewarnings.detail = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _filterResults = function () {
        //select all types if none were selected
        if ($("input[name=types]:checked").length === 0) {
            $("input[name=types").prop("checked", true);
        }
        $('#warnings-form').ajaxSubmit({
            success: function(data, status, xhr, $form) {
                $('#results-table').html(data);
            },
            error: function(xhr, status, error, $form) {
                $('#results-table').html(xhr.responseText);
            }
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _filterResults();
            
            /** Filter the results */
            $(document).on('click', '.js-filter-results', function (ev) {
                _filterResults();
            });
            
            $(document).on('click', '.js-download-warnings', function () {
                var form = $('#warnings-form'),
                    data = form.serialize();
                window.location.href = yukon.url('/stars/infrastructureWarnings/download?' + data);
            });
            
            $(document).on('click', '.js-collection-action', function () {
                var collectionAction = $(this).data('collection-action'),
                    form = $('#warnings-form'),
                    data = form.serialize();
                window.open(yukon.url('/stars/infrastructureWarnings/collectionAction?actionType=' + collectionAction + '&' + data), '_blank');
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.infrastructurewarnings.detail.init(); });