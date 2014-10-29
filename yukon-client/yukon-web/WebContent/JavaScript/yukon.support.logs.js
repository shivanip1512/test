yukon.namespace('yukon.support.logs');

/**
 * Handles the behavior for the log tail page.
 * @module yukon.support.logs
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires yukon
 */
yukon.support.logs = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    /**@type {number} -  task Id*/
    _repeatingTaskId = 0,
    
    /**@type {number} -  file size*/
    _fileLength = 0,
    
    /** 
     * Sets the number of lines displayed.
     * @param {number} num - No. of lines.
     */
    _setNumberOfLines = function (num) {
        $('#numLines').val((isNaN(num) || num <= 10) ? 10 : num);
        _removeExtraLines(true);
    },
    
    /** 
     * Remove the extra lines and reload.
     * @param {boolean} reload - If true, reload the logs.
     */
    _removeExtraLines = function (reload) {
        
        var numLogsCurrent = $('.logLine').size(),
            numLinesNew = $('#numLines').val(),
            linesToSlice = numLogsCurrent - numLinesNew;
        
        if (linesToSlice > 0) {
            $('.logLine').slice(0, linesToSlice).slideUp(200, function () { $(this).remove(); });
        } else if (linesToSlice < 0 && reload) {
            // we requested more lines than we currently have, reload page to obtain
            window.location.href = '?file=' + $('#file').val() + '&numLines=' + numLinesNew;
        }
    },
    
    /** Update the logs after specific interval. */
    _update = function () {
        
        var requestData = {
            fileLength : _fileLength,
            numLines : $('#numLines').val(),
            file : $('#file').val()
        };

        $.ajax({
            url : $('#updateUrl').val(),
            type : 'POST',
            data : JSON.stringify(requestData),
            contentType : 'application/json',
            dataType : 'json'
        }).done(function (response) {
            
            var logLines = response.logLines,
                lastModified = response.lastModified,
                fileSize = response.fileSize;

            if (logLines != null && logLines != '') {
                // This part of the function updates the log last modified field shown on the screen
                if (_fileLength !== fileSize) {
                    _fileLength = fileSize;
                    $('#lastMod').html(lastModified);
                    $('#fileLength').html(response.readableFileSize);
                    $('#lastMod, #fileLength').flash();
                }

                // This part of the function updates the log contents shown on the screen
                $(logLines).each(function (index, newLogLine) {
                    var newDiv = $('<div class="logLine">' + newLogLine +'\n</div>');
                    $('#logOutput').append(newDiv);
                });
                // remove extra lines but do not reload page if numLines is more than we currently have
                _removeExtraLines(false);
            }
        }).fail(function (xhr, status) {
            clearInterval(_repeatingTaskId);
            _repeatingTaskId = setInterval(_update, 1000 * 10);
            var newDiv = $('<div class="logLine">Error: ' + status + '...Trying again in 10 seconds.</div>').hide();
            $('#logOutput').append(newDiv);
            newDiv.slideDown();
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _update();
            _repeatingTaskId = setInterval(_update, 1500);
            $('#startBtn').hide();

            $('#numLines').change(function () {
                _setNumberOfLines($('#numLines').val());
            });
            
            $('#incrementLinesBtn').click(function () {
                var num = parseInt($('#numLines').val());
                _setNumberOfLines(num+10);
            });

            $('#pauseBtn').click(function () {
                clearInterval(_repeatingTaskId);
                $('#pauseBtn').fadeOut(100, function () {
                    $('#startBtn').fadeIn(25);
                });
            });

            $('#startBtn').click(function () {
                _repeatingTaskId = setInterval(_update, 1500);
                $('#startBtn').fadeOut(100, function () {
                    $('#pauseBtn').fadeIn(25);
                });
            });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();
 
$(function () { yukon.support.logs.init(); });