yukon.namespace('yukon.tools.paonotessearch.js');
 
/** 
 * Module to handle pao notes search functionality.
 * 
 * @module yukon.tools.paonotessearch 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.tools.paonotessearch = (function () {
 
    'use strict';
 
    var
    _initialized = false,
    _notesModified = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-pao-notes-form');
                var data = form.serialize();
                window.location = yukon.url('/tools/paoNotes/download?' + data);
            });
            
            if ($("#deviceGroups\\.errors").length === 1) {
                $("#deviceGroups\\.errors").prev().remove();
            }
            if ($("#paoIds\\.errors").length === 1) {
                $("#paoIds\\.errors").prev().remove();
            }
            
            // Set "notes modified" flag if a new note is created
            $(document).on('click', '.js-create-popup-note', function (event) {
                _notesModified = true;
            });
            
            // Set "notes modified" flag if a note is edited
            $(document).on('click', 'button[id^="js-save-popup-note-btn-"]', function (event) {
                _notesModified = true;
            });
            
            // Set "notes modified" flag if a note is deleted
            $(document).on('yukon:popup:note:delete', function (event) {
                _notesModified = true;
            });
            
            // When the popup is closed, if the "notes modified" flag is set, refresh the page to update
            $('#js-pao-notes-popup').on('dialogclose', function(event) {
                if (_notesModified) {
                    _notesModified = false;
                    yukon.ui.blockPage()
                    window.location.href = window.location.href;
                }
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.tools.paonotessearch.init(); });