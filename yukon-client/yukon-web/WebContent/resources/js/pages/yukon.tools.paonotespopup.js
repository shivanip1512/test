yukon.namespace('yukon.tools.paonotespopup');
 
/**
 * Module to handle pao notes popup functionality.
 * 
 * @module yukon.tools.paonotespopup
 * @requires yukon
 * @requires JQUERY
 */
yukon.tools.paonotespopup = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    
    _toggleClasses = function (noteId) {
        var editButton = $('#js-edit-note-btn-' + noteId);
        if ($(editButton).is(':disabled')) {
            $(editButton).prop('disabled', false);
        } else {
            $(editButton).prop('disabled', true);
        }
        $('#js-save-note-group-' + noteId).toggleClass('dn');
        $('#js-note-' + noteId).toggleClass('dn');
        $('#js-edit-note-' + noteId).toggleClass('dn');
    },
    
    _updatePopup = function (paoId) {
        $.get(yukon.url('viewAllNotes?paoId=' + paoId), function (data) {
            $('.ui-dialog-content#js-pao-notes-popup').html(data);
        });
    };
    
    mod = {

        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            $(document).on('click', 'button[id^="js-edit-note-btn-"]', function (event) {
                var noteId = $(this).data('note-id');
                $('#js-edit-note-textarea-' + noteId).val($('#js-note-content-' + noteId).text().trim());
                _toggleClasses(noteId);
            });
            
            $(document).on('click', 'button[id^="js-cancel-btn-"]', function (event) {
                var noteId = $(this).data('note-id');
                $('#js-edit-note-textarea-' + noteId).text($('#js-note-content-' + noteId).val().trim());
                $('#js-edit-note-textarea-' + noteId).removeClass('error');
                $('#error_' + noteId).remove();
                _toggleClasses(noteId);
            });
            
            $(document).on('yukon:note:delete', function (event) {
                $(event.target).siblings('#delete-note-form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $(event.target).closest('.ui-dialog-content').html(data);
                    }
                });
            });
            
            $(document).on('click', 'button[id^="js-save-note-btn-"]', function (event) {
                
                var noteId = $(this).data('note-id'),
                    paoId = $(this).data('pao-id'),
                    noteText =  $('#js-edit-note-textarea-' + noteId).val().trim(),
                    parameters = {
                        paoId : paoId,
                        noteId : noteId,
                        noteText : noteText
                    };
                
                $.post(yukon.url('editPaoNote'), parameters, function (data) {
                    if (data.hasError === true) {
                        if ($("#error_" + noteId).length === 0) {
                            var errorSpan = $('<span />').attr({'class':'error' , 'id':'error_' + noteId});
                            errorSpan.text(data.errorMessage);
                            $('#js-edit-note-' + noteId).append(errorSpan);
                        } else {
                            $("#error_" + noteId).text(data.errorMessage);
                        }
                        $('#js-edit-note-textarea-' + noteId).addClass('error');
                        yukon.ui.initContent();
                    } else {
                        _updatePopup(paoId);
                    }
                });
            });
            
            $(document).on('click', '.js-create-note', function (event) {
                $(this).closest('#create-note-form').ajaxSubmit({
                     success: function(data, status, xhr, $form) {
                          $("#create-note-form").closest('.ui-dialog-content').html(data);
                     }
                });
             });
            
            $(document).on('click', '.js-note-actions button', function (event) {
                $("#createNoteTextarea").removeClass('error');
                $("#noteText\\.errors").remove();
                $("#create-note-form").siblings("div.user-message").remove();
            });
            
            $('.js-view-all-notes').click(function () {
                var paoId = $(this).data('pao-id'),
                    popup = $('#js-pao-notes-popup'),
                    noteId = $(this).data('note-id'),
                    paoName = $(this).data('pao-name');
                    
                
                popup.load(yukon.url('viewAllNotes?paoId=' + paoId), function () {
                    popup.dialog({
                        title: "Notes - " + paoName,
                        minWidth: 1000,
                        minHeight: 500
                    });
                });
            });
                        
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.tools.paonotespopup.init(); });