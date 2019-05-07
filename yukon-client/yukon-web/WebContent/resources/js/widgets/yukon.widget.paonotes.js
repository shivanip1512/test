yukon.namespace('yukon.widget.paonotes.js');
 
/**
 * Module to handle pao notes functionality.
 * 
 * @module yukon.widget.paonotes
 * @requires yukon
 * @requires JQUERY
 */
yukon.widget.paonotes = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    
    _toggleClasses = function (noteId) {
        $('#js-edit-note-btn-group-' + noteId).toggleClass('dn');
        $('#js-save-note-group-' + noteId).toggleClass('dn');
        $('#js-note-' + noteId).toggleClass('dn');
        $('#js-edit-note-' + noteId).toggleClass('dn');
    },
    
    _updateNotesWidgets = function () {
        var widgetId, widget;
        $('.js-notes-containing-widget').each(function(index, obj) {
            widgetId = $(obj).closest('.widgetWrapper').attr('id');
            widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
            widget = yukon.widgets[widgetId];
            widget.render();
        });
    },
    
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
                var widgetId = $(event.target).closest('.widgetWrapper').attr('id');
                widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                var widget = yukon.widgets[widgetId],
                    noteId = $(event.target).data('note-id'),
                    extraParameters = {
                        'noteId' : noteId,
                        'com.cannontech.yukon.request.csrf.token' : $("#ajax-csrf-token").val()
                    };
                widget.doActionUpdate({
                    'command' : 'deletePaoNote',
                    'extraParameters' : extraParameters,
                    'containerID' : widget.container
                }, function () {
                    _updateNotesWidgets();
                });
            });
            
            $(document).on('click', 'button[id^="js-save-note-btn-"]', function (event) {
                var widgetId = $(this).closest('.widgetWrapper').attr('id');
                widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                
                var widget = yukon.widgets[widgetId],
                    noteId = $(this).data('note-id'),
                    noteTextarea = $('#js-edit-note-textarea-' + noteId),
                    parameters = {
                        'paoId' : $("#deviceId").val(),
                        'noteId' : noteId,
                        'noteText' : noteTextarea.val().trim()
                    };
                
                $.post(yukon.url('/widget/' + widget.shortName + '/editPaoNote'), parameters, function (data) {
                    if (data.hasError === true) {
                        if ($("#error_" + noteId).length === 0) {
                            var errorSpan = $('<span />').attr({'class':'error' , 'id':'error_' + noteId });
                            errorSpan.text(data.errorMessage);
                            $('#js-edit-note-' + noteId).append(errorSpan);
                        } else {
                            $("#error_" + noteId).text(data.errorMessage);
                        }
                        noteTextarea.addClass('error');
                        yukon.ui.initContent();
                    } else {
                        _updateNotesWidgets();
                    }
                });
            });
            
            $(document).on('click', '.js-create-note', function (event) {
                var widgetId = $('#create-note-form').closest('.widgetWrapper').attr('id');
                widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                var widget = yukon.widgets[widgetId];
                var form = $('#create-note-form').serialize();
                var extraParameters = {'createPaoNote': form, 'com.cannontech.yukon.request.csrf.token' : $("#ajax-csrf-token").val()};
                widget.doActionUpdate({
                    'command' : 'createPaoNote',
                    'extraParameters' : extraParameters,
                    'containerID' : widget.container
                }, function () {
                    var errorSpan = $('#noteText\\.errors'),
                        widgetId, 
                        widget;
                    if (errorSpan && errorSpan.text().length === 0) {
                        $('.js-notes-containing-widget').each(function(index, obj) {
                            widgetId = $(obj).closest('.widgetWrapper').attr('id');
                            widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                            widget = yukon.widgets[widgetId];
                            widget.render();
                        });
                    }
                });
            });
            
            $(document).on('click', '.js-note-actions button', function (event) {
                $("#createNoteTextarea").removeClass('error');
                $("#noteText\\.errors").remove();
                $("#create-note-form").siblings("div.user-message").remove();
            });
            
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.widget.paonotes.init(); });