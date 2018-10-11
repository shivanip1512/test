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
    _notesModified = false,
    
    _toggleClasses = function (noteId) {
        var editButton = $('#js-edit-popup-note-btn-' + noteId),
            saveNoteGroup = $('#js-save-popup-note-group-' + noteId),
            saveNoteGroupTd = saveNoteGroup.closest('td'),
            popupNote = $('#js-popup-note-' + noteId),
            popupNoteTd = popupNote.closest('td');
            
        if ($(editButton).is(':disabled')) {
            $(editButton).prop('disabled', false);
        } else {
            $(editButton).prop('disabled', true);
        }
        saveNoteGroup.toggleClass('dn');
        saveNoteGroupTd.toggleClass('dn');
        popupNote.toggleClass('dn');
        popupNoteTd.attr('colspan') ? popupNoteTd.removeAttr('colspan') : popupNoteTd.attr('colspan', '2');
        $('#js-edit-popup-note-' + noteId).toggleClass('dn');
    },
    
    _updatePopup = function (paoId) {
        $.get(yukon.url('/tools/paoNotes/viewAllNotes?paoId=' + paoId), function (data) {
            var popup = $('.ui-dialog-content#js-pao-notes-popup');
            popup.html(data);
            var title = popup.find('#popupTitlePrefix').val() + popup.find('#paoName').val();
            popup.dialog('option', 'title', title).dialog('open');
        });
    },
    
    mod = {

        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            $(document).on('click', 'button[id^="js-edit-popup-note-btn-"]', function (event) {
                var noteId = $(this).data('note-id');
                $('#js-edit-popup-note-textarea-' + noteId).val($('#js-popup-note-content-' + noteId).text().trim());
                _toggleClasses(noteId);
            });
            
            $(document).on('click', 'button[id^="js-popup-note-cancel-btn-"]', function (event) {
                var noteId = $(this).data('note-id');
                $('#js-edit-popup-note-textarea-' + noteId).text($('#js-popup-note-content-' + noteId).val().trim());
                $('#js-edit-popup-note-textarea-' + noteId).removeClass('error');
                $('#popup_error' + noteId).remove();
                _toggleClasses(noteId);
            });
            
            $(document).on('yukon:popup:note:delete', function (event) {
                $(event.target).siblings('#delete-popup-note-form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $(event.target).closest('.ui-dialog-content').html(data);
                        _notesModified = true;
                    }
                });
            });
            
            $(document).on('click', 'button[id^="js-save-popup-note-btn-"]', function (event) {
                var noteId = $(this).data('note-id'),
                    paoId = $(this).data('pao-id'),
                    noteText =  $('#js-edit-popup-note-textarea-' + noteId).val().trim(),
                    parameters = {
                        paoId : paoId,
                        noteId : noteId,
                        noteText : noteText
                    };
                
                $.post(yukon.url('/tools/paoNotes/editPaoNote'), parameters, function (data) {
                    if (data.hasError === true) {
                        if ($("#popup_error" + noteId).length === 0) {
                            var errorSpan = $('<span />').attr({'class':'error' , 'id':'popup_error' + noteId});
                            errorSpan.text(data.errorMessage);
                            $('#js-edit-popup-note-' + noteId).append(errorSpan);
                        } else {
                            $("#popup_error" + noteId).text(data.errorMessage);
                        }
                        $('#js-edit-popup-note-textarea-' + noteId).addClass('error');
                    } else {
                        _updatePopup(paoId);
                        _notesModified = true;
                    }
                });
            });
            
            $(document).on('click', '.js-create-popup-note', function (event) {
                $(event.target).closest('#create-popup-note-form').ajaxSubmit({
                     success: function(data, status, xhr, $form) {
                          $(event.target).closest('.ui-dialog-content').html(data);
                          _notesModified = true;
                     }
                });
             });
            
            $(document).on('click', '.js-popup-note-actions button', function (event) {
                $(event.target).closest("#createPopupNoteTextarea").removeClass('error');
                $(event.target).closest("#noteText\\.errors").remove();
                $(event.target).closest("#create-popup-note-form").siblings("div.user-message").remove();
            });
            
            $(document).on('click','.js-view-all-notes', function (event) {
                var paoId = $(this).data('pao-id'),
                    popup = $('#js-pao-notes-popup');
                if ($('.ui-dialog-content#js-pao-notes-popup').length === 0 ) {
                    popup.load(yukon.url('/tools/paoNotes/viewAllNotes?paoId=' + paoId), function () {
                        popup.dialog({
                            title: popup.find('#popupTitlePrefix').val() + popup.find('#paoName').val(),
                            minWidth: 950,
                            minHeight: 500,
                            modal: true
                        });
                    });
                } else {
                    _updatePopup(paoId);
                }
            });
            
            $(document).on('dialogclose', '#js-pao-notes-popup', function(event) {
                var widgetId, widget;
                
                // Re-render any widgets with notes in them when the notes popup closes, so new or deleted notes will be 
                // picked-up.
                $('.js-notes-containing-widget').each(function(index, obj) {
                    widgetId = $(obj).closest('.widgetWrapper').attr('id');
                    if (widgetId) {
                        widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                        widget = yukon.widgets[widgetId];
                        widget.render();
                    }
                });
                
                // Refresh the page if notes have been modified in the popup on the Notes page.
                if (_notesModified === true && $('#filter-pao-notes-form').exists()) {
                    yukon.ui.blockPage();
                    window.location.reload(true);
                }
                
                // If all notes were deleted for this device, hide all of the note icons for this device
                var paoId = Number($(this).find('#paoId').val());
                if (!$(this).find('tr[id^="js-popup-note-row-"]').exists()) {
                    if ($("#gateways-table").exists()) {
                        $('.js-view-all-notes').filter('[data-pao-id="' + paoId + '"]').addClass('dn');
                    } else {
                        $('.js-view-all-notes').filter('[data-pao-id="' + paoId + '"]').remove();
                    }
                }
            });
            
            _initialized = true;
        },
    
        // Given a device/pao id, determine if the device has notes and either show or hide all of the notes
        // icons on the page for that device.
        hideShowNotesIcons: function (paoId) {
            if (paoId != null) {
                $.getJSON(yukon.url('/tools/paoNotes/hasNotes'), {paoId: paoId})
                    .done(function(data) {
                        if (data.hasNotes) {
                            $('.js-view-all-notes').filter('[data-pao-id="' + paoId + '"]').show();
                        } else {
                            $('.js-view-all-notes').filter('[data-pao-id="' + paoId + '"]').hide();
                        }
                    });
            }
        }
    };
 
    return mod;
})();
 
$(function () { yukon.tools.paonotespopup.init(); });