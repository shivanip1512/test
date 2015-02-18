yukon.namespace('yukon.tools.importUpload');

/**
 * Module for the upload page of bulk upload.
 * @module yukon.tools.importUpload
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.importUpload = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    _updateImportSettings = function () {
        var type = $('.js-import-type').val();
        $('[data-import-type]').hide();
        $('[data-import-type="' + type + '"]').show();
        $('[data-file-type]').hide();
        $('[data-file-type="' + type + '"]').show();
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _updateImportSettings();
            $('.js-import-type').change(_updateImportSettings);
            $('.js-file').change(function (ev) { $('.js-file-validation').hide(); });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.importUpload.init(); });