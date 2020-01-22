yukon.namespace('yukon.dr.setup.list');

/**
 * Module that handles the behavior on the setup page.
 * @module yukon.dr.setup.list
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.list = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _filterResults = function (isFilterByTypeChanged) {
        yukon.ui.blockPage();
        if (isFilterByTypeChanged) {
            var selectedFilterByType = $("#js-filter-by-type option:selected").val();
            $("#js-name").val('');
            if (!$("#js-load-group-types").is(":visible")) {
                $("#js-load-group-types").val("").trigger("chosen:updated");
            }
            if (!$("#js-load-program-types").is(":visible")) {
                $("#js-load-program-types").val("").trigger("chosen:updated");
            }
            if (!$("#js-gear-types").is(":visible")) {
                $("#js-gear-types").val("").trigger("chosen:updated");
            }
        }
        $('#setupFilter').submit();
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
           if ($("#js-load-group-types").is(":visible")) {
                $("#js-load-group-types").chosen({width: "450px"});
            }
            
            if ($("#js-load-program-types").is(":visible")) {
                $("#js-load-program-types").chosen({width: "305px"});
            }
            
            if ($("#js-gear-types").is(":visible")) {
                $("#js-gear-types").chosen({width: "250px"});
            }
            
            $(document).on('change', '#setupFilter :input', function (event) {
                var isFilterByTypeChanged = event.currentTarget.id === 'js-filter-by-type';
                _filterResults(isFilterByTypeChanged);
            });
            
            $(document).on("yukon:gear:filter:programSelected", function (event) {
                _filterResults(false);
            });
            
            $(document).on("click", ".js-gear-link", function (event) {
                 event.preventDefault();
                 var dialogDivJson = {
                     "data-url" : $(this).attr('href'),
                     "data-load-event" : "yukon:dr:setup:gear:viewMode",
                     "data-width" : "900",
                     "data-height" : "525",
                     "data-title" : $(this).text(),
                     "data-destroy-dialog-on-close" : "",
                 };
               
                dialogDivJson['id'] = $(this).data("gear-id");
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.list.init(); });