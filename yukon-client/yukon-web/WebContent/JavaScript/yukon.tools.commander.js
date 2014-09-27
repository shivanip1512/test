yukon.namespace('yukon.tools.commander');

/**
 * Module for the commander page (localhost:8080/yukon/tools/commander).
 * @module yukon.tools.commander
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.commander = (function () {
    
    'use strict';
    
    var
    mod = {},
    
    /** {object} - Map of target button id's to CommandType enum entries. */
    _commandTypes = { 
        'target-device-btn': 'DEVICE',
        'target-lm-group-btn': 'LOAD_GROUP',
        'target-expresscom-btn': 'EXPRESSCOM',
        'target-versacom-btn': 'VERSACOM'
    },
    
    /** {object} - Map of regex expressions for finding prompt, update, and queueing command parts. */
    _regex = {
        prompt: /\s\?'([^']*)'/g,
        firstPrompt: /\?'([^']*)'/,
        update: /\supdate/gi,
        noqueue: /\snoqueue/gi
    },
    
    /** {object} - Map of MessageType enum entries to response css class names. */
    _responseTypes = {
        'ERROR': 'cmd-resp-fail', 
        'SUCCESS': 'cmd-resp-success', 
        'INHIBITED': 'cmd-resp-warn'
    },
    
    /** {String} - The IANA timezone name. */
    _tz = jstz.determine().name(),
    
    _timeFormat = 'MMM DD hh:mm:ss A z',
    
    /** {object} - Map of requests that are still waiting for responses.
     *             It is a hash of request id to array of already processed response ids. */
    _pending = {},
    
    _initialized = false,
    
    /** 
     * Prime the pending cache with any pending requests.
     * Should be called on page load before the _update timeout is started. 
     */
    _primePending = function () {
        var reqs = yukon.fromJson('#intial-requests');
        reqs.forEach(function (req, index, array) {
            if (!req.complete) {
                var resps = req.responses.map(function (resp) {
                    return resp.id;
                });
                _pending[req.id] = resps;
            }
        });
    },
    
    /** Remove any prompts, ' update', or ' noqueue' from the command. */
    _cleanCommand = function(command) {
        return command.replace(_regex.prompt, '')
                      .replace(_regex.update, '')
                      .replace(_regex.noqueue, '');
    },
    
    /** 
     * Update the common commands dropdown with for the selected pao.
     * Attempt to keep the current command if we can find it in the list of new commands;
     * not perfect but it's the best we can do without hitting the server.
     */
    _updateCommandsForPao = function (paoId) {
        $.getJSON('commander/commands?' + $.param({ paoId: paoId }))
        .done(function (commands) {
            
            var keep = false,
                currentText = _cleanCommand($('#command-text').val());
            
            _updateCommonCommands(commands);
            
            commands.forEach(function (command, index) {
                if (command.label === currentText || _cleanCommand(command.command) === currentText) keep = true;
            });
            
            if (!keep) $('#command-text').val('');
        });
    },
    
    /**
     * Replace the options on the common commands select with the supplied commands.
     * @param {Object[]} commands - An array of command objects used to build html option elements.
     */
    _updateCommonCommands = function (commands) {
        
        var select = $('#common-commands'),
            first = select.find('option:first-child');
            
        first.siblings().remove();
        commands.forEach(function (command) {
            if (command.label.trim()) {
                select.append($('<option>').text(command.label).val(command.command));
            } else {
                select.append($('<optgroup>')); // Essentially adds a blank space
            }
        });
        
        select.trigger('chosen:updated').siblings('.chosen-container').css('width', select.css('width'));
    },
    
    /** Find any prompts in the command and show dialog for input if need be. */
    _promptForInput = function () {
        
        var field = $('#command-text'),
            command = field.val(),
            dialog = $('#prompt-dialog'),
            at = command.search(_regex.firstPrompt);
            
        if (at != -1) {
            dialog.find('.js-prompt-text').text(_regex.firstPrompt.exec(command)[1]);
            dialog.find('.js-prompt-input').val('');
            yukon.ui.dialog('#prompt-dialog');
        }
    },
    
    /** 
     * Log a new request to the console window.  If not complete, add the message id to the pending bucket.
     * @param {object} req - The JSON object representing the request. 
     * @returns {jqObject} The jquery object of the result div added to the console. 
     */
    _logRequest = function (req) {
        
        var timestamp = moment(req.timezone).tz(_tz).format(_timeFormat),
            result = $('<div>'), 
            resultReq = $('<div>'),
            pending = $('#cmd-templates .cmd-pending').clone();
        
        result.addClass('cmd-req-resp').data('requestId', req.id).attr('data-request-id', req.id);
        resultReq.addClass('cmd-req').text('[' + timestamp + '] - ' + req.requestText).appendTo(result);
        result.append(pending);
        _pending[req.id] = [];
        
        $('#commander-results').append(result).scrollTo(result);
        
        return result;
    },
    
    /** 
     * Log a response for a request in the console window.
     * @param {object} req - The request object for the response. 
     * @param {object} resp - The response object to log. 
     */
    _logResponse = function (req, resp) {
        
        var
        clazz = _responseTypes[resp.type],
        results = $('#commander-results'),
        result = results.find('[data-request-id="' + req.id + '"]'),
        throbber = result.find('.cmd-pending'),
        response = $('<div>').addClass(clazz).data('responseId', resp.id);
        
        for (var i in resp.results) {
            response.append($('<div>').text(resp.results[i]));
        }
        
        if (throbber.length) {
            throbber.before(response);
            if (resp.expectMore === 0) {
                throbber.remove();
            }
        } else {
            result.append(response);
        }
        
        results.scrollTo(response);
        
        return result;
    },
    
    /** 
     * Log an error meaning we weren't able to send the request.
     * @param {Number} reqId - The id of the request. 
     * @param {String} text - The text of the reponse. 
     * @returns {jqObject} The jquery object of the result div the response was added to. 
     */
    _logError = function (reqId, text) {
        
        var 
        results = $('#commander-results'),
        result = results.find('[data-request-id="' + reqId + '"]'),
        response = $('<div>').addClass('cmd-resp-fail').text(text);
        
        result.append(response);
        results.scrollTo(response);
        
        return result;
    },
    
    /**
     * Checks for pending requests awaiting reponses every 200ms as a 
     * recursive timeout.  If pending requests are detected, a request
     * to the server is made for responses.  Any responses processed are
     * recorded and any completed requests are deleted from the pending cache.
     */
    _update = function () {
        if (Object.keys(_pending).length > 0) {
            $.ajax({
                url: 'commander/requests',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(Object.keys(_pending)),
                dataType: 'json'
            }).done(function (requests, status, xhr) {
                
                var req, resp, i = 0, j = 0;
                
                for (i in requests) {
                    req = requests[i];
                    for (j in req.responses) {
                        resp = req.responses[j];
                        if (_pending[req.id].indexOf(resp.id) === -1) {
                            _pending[req.id].push(resp.id);
                            _logResponse(req, resp);
                        } else {
                            // We have already delt with this response
                            continue;
                        }
                    }
                    if (req.complete) delete _pending[req.id];
                }
            }).always(function () {
                setTimeout(_update, 200);
            });
        } else {
            setTimeout(_update, 200);
        }
    },
    
    /** 
     * User clicked the execute button or hit enter in the command textfield.
     * Send the command request to the server.
     */
    _execute = function () {
        var
        picker,
        valid = true,
        type = _commandTypes[$('#target-row .on').attr('id')],
        params = { 
            type: type,
            command: $('#command-text').val().trim()
        },
        btn = $('#cmd-execute-btn'),
        field = $('#command-text');
        
        if (type === 'DEVICE') {
            params.paoId = $('#pao-id').val();
        } else if (type === 'LOAD_GROUP') {
            params.paoId = $('#lm-group-id').val();
        } else {
            params.serialNumber = $('#serial-number').val();
            params.routeId = $('#route-id').val();
        }
        
        // Do some validation before we fire a request
        if (!params.command) {
            valid = false;
            field.addClass('animated shake-subtle error')
            .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
        }
        if (!params.paoId && (type === 'DEVICE' || type === 'LOAD_GROUP')) {
            valid = false;
            picker = type === 'DEVICE' ? $('.js-device-picker') : $('.js-lm-group-picker');
            picker.addClass('animated shake-subtle')
            .one(yg.events.animationend, function() { 
                $(this).removeClass('animated shake-subtle error').find('.b-label').removeClass('error'); 
            }).find('.b-label').addClass('error');
        } else if ((type === 'EXPRESSCOM' || type === 'VERSACOM') && !params.serialNumber) {
            valid = false;
            $('#serial-number').addClass('animated shake-subtle error')
            .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
        }
        
        if (valid) {
            
            yukon.ui.busy(btn);
            field.prop('disabled', true);
            $.ajax({
                url: 'commander/execute',
                data: params,
                dataType: 'json'
            }).done(function (result, status, xhr) {
                for (var i in result.requests) {
                    _logRequest(result.requests[i].request);
                }
            }).fail(function (xhr, status, errorThrown) {
                var 
                requests = xhr.responseJSON.requests,
                reason = xhr.responseJSON.reason;
                
                for (var i in requests) {
                    _logRequest(requests[i].request);
                    _logError(requests[i].request.id, reason);
                }
            }).always(function () {
                yukon.ui.unbusy(btn);
                field.prop('disabled', false);
            });
        }
    };
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** 
             * Init the the common commands select with chosen. 
             * On change, autofill the command text field ; prompt for any user input needed. 
             */
            $('#common-commands').chosen().change(function (ev) {
                $('#command-text').val($(this).val());
                _promptForInput();
            });
            
            /** User clicked the expresscom or versacom target buttons, update the common commands. */
            $('#target-expresscom-btn, #target-versacom-btn').click(function (ev) {
                var url = 'commander/type-commands?' + $.param({ type: $(this).data('type') });
                $.getJSON(url).done(function (commands) {
                    _updateCommonCommands(commands);
                });
                $('#command-text').val('');
            });
            
            /** User clicked the device target buttons, update the common commands. */
            $('#target-device-btn').click(function (ev) {
                var paoId = $('#device-row input[type="hidden"]').first().val();
                if (paoId) {
                    // We have picked a device previously.
                    _updateCommandsForPao(paoId);
                } else {
                    // No device selected yet, just nuke any commands in there.
                    $('#common-commands option:first-child').siblings().remove();
                }
                $('#command-text').val('');
            });
            
            /** User clicked the lm group target buttons, update the common commands. */
            $('#target-lm-group-btn').click(function (ev) {
                var paoId = $('#load-group-row input[type="hidden"]').first().val();
                if (paoId) {
                    // We have picked an lm group previously.
                    _updateCommandsForPao(paoId);
                } else {
                    // No device selected yet, just nuke any commands in there.
                    $('#common-commands option:first-child').siblings().remove();
                }
                $('#command-text').val('');
            });
            
            /** User clicked the execute button. */
            $('#cmd-execute-btn').click(function (ev) {
                _execute();
            });
            
            /** User hit enter in the command textfield. */
            $('#command-text').on('keyup', function (ev) {
                if (ev.which === yg.keys.enter) _execute();
            });
            
            /** User clicked the clear button in the console. */
            $('#clear-console-btn').click(function (ev) {
                $.ajax({ url: 'commander/clear' });
                // Append a dummy div after empty cuz ie9 is stupid
                $('#commander-results').empty().append('<div>');
            });
            
            /** User clicked the select all button in the console. */
            $('#select-console-btn').click(function (ev) {
                $('#commander-results').selectText();
            });
            
            /** User clicked the change route button, get the popup. */
            $('#change-route-btn').click(function (ev) {
                var routeId = $('.js-on-route').data('routeId');
                $.ajax({url: 'commander/route/' + routeId + '/change'})
                .done(function(view, status, xhr) {
                    yukon.ui.dialog($('#change-route-dialog').html(view));
                });
            });
            
            /** User chose a new route for the device.  Updated the pao. */
            $(document).on('yukon.tools.commander.routeChange', function (ev) {
                var paoId = $('#pao-id').val(),
                    routeId = $('#new-route').val();
                $.ajax({
                    url: 'commander/' + paoId + '/route/' + routeId,
                    type: 'post'
                }).done(function (route, status, xhr) {
                    $('.js-on-route').data('routeId', route.liteID).find('.value').text(route.paoName).flash();
                    $('#change-route-dialog').dialog('close');
                }).fail(function () {
                    $('#change-route-dialog').dialog('close');
                });
            });
            
            /** User has supplied input for command, use it and check if more is needed. */
            $('#prompt-dialog').on('yukon.tools.commander.user.input keyup', function (ev) {
                
                // Ignore keydown events that are not the enter key.
                if (ev.type === 'keyup') {
                    if (ev.which !== yg.keys.enter) return true;
                }
                
                var field = $('#command-text'),
                    current = field.val(),
                    input = $(this).find('.js-prompt-input').val();
                
                field.val(current.replace(_regex.firstPrompt, input));
                $('#prompt-dialog').dialog('close');
                field.focus();
                _promptForInput();
            });
            
            // Prime the pending cache with any pending requests found in the console.
            _primePending();
            
            // Start the recursive updating
            setTimeout(_update, 200);
            
            _initialized = true;
        },
        
        /** A device was chosen from the lm group picker, update the commands. */
        lmGroupChosen: function (paos) { _updateCommandsForPao(paos[0].paoId); },
        
        /** A device was chosen from the device picker, update the commands and set the route if need be. */
        deviceChosen: function (paos) {
            
            var row = $('#device-row'),
                paoId = paos[0].paoId,
                type = paos[0].type,
                prevType = row.data('type');
            
            if (prevType !== type) {
                row.data('type', type);
                _updateCommandsForPao(paoId);
            }
            
            $.ajax({ url: 'commander/route/' + paoId, dataType: 'json' })
            .done(function (route, status, xhr) {
                $('.js-on-route').data('routeId', route.liteID).show().find('.value').text(route.paoName);
            }).fail(function () {
                $('.js-on-route').hide();
            });
        },
        
        getPending: function () { return _pending; }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.commander.init(); });