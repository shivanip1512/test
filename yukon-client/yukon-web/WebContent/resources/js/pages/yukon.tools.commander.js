yukon.namespace('yukon.tools.commander');

/**
 * Module for the commander page (localhost:8080/yukon/tools/commander).
 * @module yukon.tools.commander
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */
yukon.tools.commander = (function () {
    
    'use strict';
    
    var
    mod = {},
    
    _scrollLock = false,
    _customCommandsDirty = false,
    
    /** {object} - Map of target button id's to CommandType enum entries. */
    _targetTypes = {
        device: 'DEVICE',
        lmGroup: 'LOAD_GROUP',
        ecom: 'EXPRESSCOM',
        vcom: 'VERSACOM'
    },
    
    /** {object} - Map of target button id's to CommandType enum entries. */
    _targetButtons = { 
        'target-device-btn': _targetTypes.device,
        'target-lm-group-btn': _targetTypes.lmGroup,
        'target-expresscom-btn': _targetTypes.ecom,
        'target-versacom-btn': _targetTypes.vcom
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
    
    /** {object} - Map of requests that are still waiting for responses.
     *             It's a hash of request id to array of already processed response ids. */
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
        } else {
            // For some reason setting focus immediately doesn't work.
            setTimeout(function() { $('#command-text').focus(); }, 100);
        }
    },
    
    /** Load a menu with devices close to the selected one. */
    _loadNearby = function (paoId) {
        $('.js-nearby-btn').remove();
        $('.js-nearby-menu').remove();
        $.ajax({ url: yukon.url('/tools/commander/' + paoId + '/nearby') })
        .done(function (nearby, status, xhr) {
            if (status !== 'nocontent') {
                $('.page-actions').prepend(nearby);
            }
        });
    },
    
    /** 
     * Log a new request to the console window.  If not complete, add the message id to the pending bucket.
     * @param {object} req - The JSON object representing the request. 
     * @returns {jqObject} The jquery object of the result div added to the console. 
     */
    _logRequest = function (req) {
        
        var timestamp = moment(req.timestamp).tz(yg.timezone).format(yg.formats.date.long_date_time_hms),
            result = $('<div>'), 
            resultReq = $('<div>'),
            console = $('#commander-results');
        
        result.addClass('cmd-req-resp').data('requestId', req.id).attr('data-request-id', req.id);
        resultReq.addClass('cmd-req').text('[' + timestamp + '] - ' + req.requestText).appendTo(result);
        
        _pending[req.id] = [];
        if (!req.complete) {
            result.append($('#cmdr-templates .cmd-pending').clone());
        }
        
        console.append(result);
        if (!_scrollLock) console.scrollTo(result); 
        
        return result;
    },
    _logRequestUnAuthorized = function (req, command) {
        
        var timestamp = moment().tz(yg.timezone).format(yg.formats.date.long_date_time_hms),
            result = $('<div>'), 
            resultReq = $('<div>'),
            console = $('#commander-results');
        
            result.addClass('cmd-req-resp').data('requestId', req).attr('data-request-id', req);
            resultReq.addClass('cmd-req').text('[' + timestamp + '] - ' + command).appendTo(result);
        
        console.append(result);
        if (!_scrollLock) console.scrollTo(result); 
        
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
        console = $('#commander-results'),
        result = console.find('[data-request-id="' + req.id + '"]'),
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
        
        if (!_scrollLock) console.scrollTo(response);
        
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
        console = $('#commander-results'),
        result = console.find('[data-request-id="' + reqId + '"]'),
        response = $('<div>').addClass('cmd-resp-fail').text(text);
        
        result.append(response);
        
        if (!_scrollLock) console.scrollTo(response);
        
        return result;
    },
    
    _handleRequests = function(requests, logRequest) {
        var req, resp, i = 0, j = 0;
        
        for (i in requests) {
            req = requests[i];
            if(logRequest) _logRequest(req);
            for (j in req.responses) {
                resp = req.responses[j];
                if (_pending[req.id].indexOf(resp.id) === -1) {
                    _pending[req.id].push(resp.id);
                    _logResponse(req, resp);
                } else {
                    // We have already dealt with this response
                    continue;
                }
            }
            if (req.complete) delete _pending[req.id];
        }
    },
    
    /**
     * Checks for pending requests awaiting reponses every 200ms as a 
     * recursive timeout.  If pending requests are detected, a request
     * to the server is made for responses.  Any responses processed are
     * recorded and any completed requests are deleted from the pending cache.
     */
    _update = function () {
    	
        if (Object.keys(_pending).length > 0) {
        	var data = {
                    requestIds : Object.keys(_pending)
                };

            $.ajax({
                url: 'commander/requests',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                dataType: 'json'
            }).done(function (requests, status, xhr) {
                _handleRequests(requests, false);
            }).always(function () {
                setTimeout(_update, 200);
            });
        } else {
            setTimeout(_update, 200);
        }
    },
    
    /**
     * Refresh the commander targets as per the latest recent targets list
     */
    _updateTarget = function (target, recent, params) {

        var option, targetStore;
        
        $('.js-recent-menu').empty();
        recent.forEach(function (element) {
            targetStore = element.target;
            option = $('#cmdr-templates .dropdown-option').clone()
                .removeClass('js-template-menu-option')
                .data('type', targetStore.target);
            
            if (targetStore.target === _targetTypes.device || targetStore.target === _targetTypes.lmGroup) {
                option.data('paoId', targetStore.paoId);
            } else {
                $('#route-id').val = targetStore.routeId;
                option.data('routeId', targetStore.routeId);
                option.data('serialNumber', targetStore.serialNumber);
                option.find('.icon').toggleClass('icon-database-add icon-textfield');
            }
            option.find('.dropdown-option-label').text(element.label);
            $('.js-recent-menu').append(option);
            $('.js-recent-btn').removeClass('dn');
        });
    },
    
    _persistFields = function () {
        _saveFields();
    },
    
    _saveFields = function () {
        var enabled = $('.js-queueCmd').is(':checked');
        $('#queueCommand').val(enabled);
    },
    
    /** 
     * User clicked the execute button or hit enter in the command textfield.
     * Send the command request to the server.
     */
    _execute = function () {
    	_saveFields();
        var
        picker,
        valid = true,
        target = _targetButtons[$('#target-row .on').attr('id')],
        params = {
            target: target,
            command: $('#command-text').val().trim()
        },
        btn = $('#cmd-execute-btn'),
        field = $('#command-text');
        
        if (target === _targetTypes.device) {
            params.paoId = $('#pao-id').val();
        } else if (target === _targetTypes.lmGroup) {
            params.paoId = $('#lm-group-id').val();
        } else {
            params.serialNumber = $('#serial-number').val();
            params.routeId = $('#route-id').val();
        }
        params.priority = $('#commandPriority').val();
        params.queueCommand = $('#queueCommand').val();
        
        // Do some validation before we fire a request
        if (!params.command) {
            valid = false;
            field.addClass('animated shake-subtle error')
            .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
            
            if (!Modernizr.cssanimations) {
                setTimeout(function () { field.removeClass('animated shake-subtle error'); }, 1500);
            }
        }
        if (!params.paoId && (target === _targetTypes.device || target === _targetTypes.lmGroup)) {
            valid = false;
            picker = target === _targetTypes.device ? $('.js-device-picker') : $('.js-lm-group-picker');
            picker.addClass('animated shake-subtle')
            .one(yg.events.animationend, function() { 
                $(this).removeClass('animated shake-subtle error').find('.b-label').removeClass('error'); 
            }).find('.b-label').addClass('error');
            
            if (!Modernizr.cssanimations) {
                setTimeout(function () { picker.removeClass('animated shake-subtle').find('.b-label').removeClass('error'); }, 1500);
            }
        } else if ((target === _targetTypes.ecom || target === _targetTypes.vcom) && !params.serialNumber) {
            valid = false;
            $('#serial-number').addClass('animated shake-subtle error')
            .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
            
            if (!Modernizr.cssanimations) {
                setTimeout(function () { $('#serial-number').removeClass('animated shake-subtle error'); }, 1500);
            }
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
                for (var i in result.unAuthorizedCommand) {
                    _logRequestUnAuthorized(i, result.unAuthorizedCommand[i]);
                    _logError(i,result.unAuthorizedErrorMsg);
                }
                // Update the recent targets
                _updateTarget(target, result.recentTargets, params);
                if (result.showLoopCountMessage == true) {
                    $('#commander-results').append(result.maxLoopCountExceededMsg);
                }
            }).fail(function (xhr, status, errorThrown) {
                var 
                requests = xhr.responseJSON.requests,
                reason = xhr.responseJSON.reason,
                recentTargets = xhr.responseJSON.recentTargets;
                // Update the recent targets
                _updateTarget(target, recentTargets, params);
                for (var i in requests) {
                    _logRequest(requests[i].request);
                    _logError(requests[i].request.id, reason);
                }
                var
                unAuthorizedCommand = xhr.responseJSON.unAuthorizedCommand,
                unAuthorizedErrorMsg = xhr.responseJSON.unAuthorizedErrorMsg;
                for (var i in unAuthorizedCommand) {
                    _logRequestUnAuthorized(i, unAuthorizedCommand[i]);
                    _logError(i,unAuthorizedErrorMsg);
                }
            }).always(function () {
                yukon.ui.unbusy(btn);
                field.prop('disabled', false);
            });
        }
    },
    
    /** Setup the 'on route' section and device actions menu. */
    _setupFieldsForDevice = function (data) {
    	$('#other-actions-btn a').attr('href', yukon.url('/bulk/collectionActions?collectionType=idList&idList.ids=' + data.pao.liteID));
        $('#change-route-btn').toggleClass('dn', !data.isRoutable);
        $('#view-meter-detail-btn').toggleClass('dn', !data.isMeter);
        if (data.isRoutable) {
            $('.js-on-route').data('routeId', data.route.liteID).show().find('.value').text(data.route.paoName);
        } else {
            $('.js-on-route').hide();
        }
        if (data.isMeter) {
            $('#view-meter-detail-btn a').attr('href', yukon.url('/meter/home?deviceId=' + data.pao.liteID));
        }
        
        _loadNearby(data.pao.liteID);
    },
    
    _reOrderCustomCommands = function () {
        yukon.ui.reindexInputs('#commands');
        //update display order
        var table = $('#commands'),
            rows = table.find('tbody tr');
        rows.each(function (idx, elem) {
            var row = $(elem);
            row.find('.js-display-order').val(idx + 1);
        });
    },
    
    _switchCategory = function (category) {
        $.ajax({ url: yukon.url('/tools/commander/customCommandsByCategory?category=' + category) })
        .done(function (html) {
            $('#device-commands-table').html(html);
            _customCommandsDirty = false;
            $("#commands tbody").sortable({
                stop : function(event, ui) {
                    ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                    _reOrderCustomCommands();
                    _customCommandsDirty = true;
                }
            });
        });
    }

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            var target, paoId, category, url, lastReq;
            
            /** Setup form components based on inputs.  Use input values for target instead of 
             * retreiving from cookie to support the browser 'back' button behavior at least for devices. */
            target = _targetButtons[$('#target-row .on').attr('id')];
            paoId = target === _targetTypes.device ? $('#pao-id').val() : $('#lm-group-id').val();
            
            if (target === _targetTypes.device || target === _targetTypes.lmGroup) {
                if (paoId) {
                    _updateCommandsForPao(paoId);
                    if (target === _targetTypes.device) {
                        $.ajax({ url: 'commander/' + paoId + '/data' })
                        .done(function (data) { _setupFieldsForDevice(data); });
                    }
                }
            } else {
                category = target === _targetTypes.ecom ? 'EXPRESSCOM_SERIAL' : 'VERSACOM_SERIAL';
                url = 'commander/type-commands?' + $.param({ type: category });
                $.getJSON(url).done(function (commands) { _updateCommonCommands(commands); });
            }
            
            /** Scroll the console to the bottom incase there are previous commands */
            lastReq = $('#commander-results .cmd-req-resp:last-child');
            if (lastReq.length) $('#commander-results').scrollTo(lastReq);
            
            _scrollLock = yukon.cookie.get('commander', 'scrollLock', false);
            $('#scroll-lock-btn').toggleClass('on', _scrollLock);
            
            /**                **/
            /** EVENT HANDLERS **/
            /**                **/
            
            /** User clicked scroll lock button on console. */
            $('#scroll-lock-btn').on('click', function (ev) {
                var on = $(this).toggleClass('on').is('.on');
                yukon.cookie.set('commander', 'scrollLock', on);
                _scrollLock = on;
            });

            /** User clicked the custom commands button, display the custom commands popup. */
            $(document).on('click', '#custom-commands', function() {
                var target = _targetButtons[$('#target-row .on').attr('id')],
                    data = {};
                if (target === _targetTypes.device || target === _targetTypes.lmGroup) {
                    var paoId = target === _targetTypes.device ? $('#pao-id').val() : $('#lm-group-id').val();
                    if (paoId != "") {
                        data.paoId = paoId;
                    }
                } else {
                    var category = target === _targetTypes.ecom ? 'EXPRESSCOM_SERIAL' : 'VERSACOM_SERIAL';
                    data.category = category;
                }
                $.ajax({ 
                    url: yukon.url('/tools/commander/customCommands'),
                    data: data
                }).done(function (html) {
                    var buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon:tools:commander:commands:save'}),
                        popup = $('#custom-commands-popup'),
                        title = popup.data('title');
                    popup.html(html);
                    popup.dialog({title: title, width: '985px', buttons: buttons});
                    _customCommandsDirty = false;
                    $("#commands tbody").sortable({
                        stop : function(event, ui) {
                            ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                            _reOrderCustomCommands();
                            _customCommandsDirty = true;
                        }
                    });
                });
            });
            
            $(document).on('click', '.js-with-movables .js-move-up, .js-with-movables .js-move-down', function () {
                _reOrderCustomCommands();
                _customCommandsDirty = true;
            });
            
            $(document).on('click', '.js-add-command', function () {
                var clonedRow = $('.js-template-row').clone();
                clonedRow.find(':input').removeAttr('disabled');
                clonedRow.removeClass('dn js-template-row');
                clonedRow.appendTo($('#commands'));
                $('.js-empty-commands').remove();
                var firstField = clonedRow.find('.js-command-fields').first();
                firstField.focus();
                $('#commands').trigger('yukon:ordered-selection:added-removed');
                _reOrderCustomCommands();
                _customCommandsDirty = true;
            });
            
            $(document).on('click', '.js-remove', function () {
                $(this).closest('tr').remove();
                $('#commands').trigger('yukon:ordered-selection:added-removed');
                _reOrderCustomCommands();
                _customCommandsDirty = true;
            });
            
            $(document).on('yukon:tools:commander:commands:save', function (ev) {
                var popup = $('#custom-commands-popup');
                $('#custom-commands-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        $('#device-commands-table').html(xhr.responseText).scrollTop(0);
                        _customCommandsDirty = false;
                        var saveChangesPopup = $('#save-changes-popup');
                        if (saveChangesPopup && saveChangesPopup.is(':visible')) {
                            //close dialog and change selected back to previous
                            var selectedCategory = $('#selectedCategory').val();
                            saveChangesPopup.dialog('close');
                            $('.js-selected-category').val(selectedCategory);
                        }
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        $('#save-changes-popup').dialog('close');
                    }
                });
            });
            
            $(document).on('click', '.js-switch-category', function () {
                var category = $('#save-changes-popup').attr('data-category');
                _switchCategory(category);
            });
            
            $(document).on('change', '.js-command-fields', function (event) {
                _customCommandsDirty = true;
            });

            $(document).on('change', '.js-selected-category', function() {
                var category = $(this).val();
                $('.js-add-command').removeAttr('disabled');
                if (_customCommandsDirty) {
                    var buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon:tools:commander:commands:save', cancelClass: 'js-switch-category'}),
                    popup = $('#save-changes-popup'),
                    title = popup.data('title');
                    popup.attr('data-category', category);
                    popup.dialog({title: title, width: '400px', buttons: buttons});
                } else {
                    _switchCategory(category);
                }
            });
            
            /** User clicked the device readings menu option, show points popup. */
            $('#readings-btn').on('click', function (ev) {
                var paoId = $('#pao-id').val();
                $.ajax({ url: yukon.url('/common/pao/' + paoId + '/points-simple') })
                .done(function (html) {
                    var popup = $('#device-readings-popup').html(html);
                    popup.dialog({ title: popup.find('.js-popup-title').val(), width: 'auto', minHeight: 120, maxHeight: 400 });
                });
            });
            
            /** User clicked a recent target from the recent targets menu. */
            $(document).on('click', '.js-recent-menu a', function (ev) {
                
                var option = $(this).parent(),
                    type = option.data('type'), 
                    paoId = option.data('paoId'),
                    routeId = option.data('routeId'),
                    serialNumber = option.data('serialNumber'),
                    category = type === _targetTypes.ecom ? 'EXPRESSCOM_SERIAL' : 'VERSACOM_SERIAL',
                    url = 'commander/type-commands?' + $.param({ type: category });
                
                if (type === _targetTypes.device) {
                    $.ajax({ url: yukon.url('/common/pao/' + paoId) }).done(function (pao) {
                        commanderDevicePicker.select({ type: pao.paoType, paoId: paoId, paoName: pao.paoName });
                        _updateCommandsForPao(paoId);
                        $('#target-device-btn').addClass('on').siblings().removeClass('on');
                        $('#device-row').show();
                        $('#serial-number-row').hide();
                        $('#route-row').hide();
                        $('#load-group-row').hide();
                    });
                } else if (type === _targetTypes.lmGroup) {
                    $.ajax({ url: yukon.url('/common/pao/' + paoId) }).done(function (pao) {
                        lmGroupPicker.select({ type: pao.paoType, paoId: paoId, paoName: pao.paoName });
                        _updateCommandsForPao(paoId);
                        $('#target-lm-group-btn').addClass('on').siblings().removeClass('on');
                        $('#load-group-row').show();
                        $('#device-row').hide();
                        $('#serial-number-row').hide();
                        $('#route-row').hide();
                        $('.js-nearby-btn').addClass('dn');
                    });
                } else if (type === _targetTypes.ecom) {
                    $('#serial-number').val(serialNumber);
                    $('#route-id').val(routeId);
                    $('#target-expresscom-btn').addClass('on').siblings().removeClass('on');
                    $('#load-group-row').hide();
                    $('#device-row').hide();
                    $('#serial-number-row').show();
                    $('#route-row').show();
                    $.getJSON(url).done(function (commands) { _updateCommonCommands(commands); });
                    $('.js-nearby-btn').addClass('dn');
                    $('#command-text').val('');
                } else if (type === _targetTypes.vcom) {
                    $('#serial-number').val(serialNumber);
                    $('#route-id').val(routeId);
                    $('#target-versacom-btn').addClass('on').siblings().removeClass('on');
                    $('#load-group-row').hide();
                    $('#device-row').hide();
                    $('#serial-number-row').show();
                    $('#route-row').show();
                    $.getJSON(url).done(function (commands) { _updateCommonCommands(commands); });
                    $('.js-nearby-btn').addClass('dn');
                    $('#command-text').val('');
                }
            });
            
            /** User clicked a target from the nearby targets menu. */
            $(document).on('click', '.js-nearby-menu a', function (ev) {
                
                var option = $(this).parent(),
                paoId = option.data('paoId');
                
                $.ajax({ url: yukon.url('/common/pao/' + paoId) }).done(function (pao) {
                    commanderDevicePicker.select({ type: pao.paoType, paoId: paoId, paoName: pao.paoName });
                });
            });
            
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
                $('.js-nearby-btn').addClass('dn');
            });
            
            /** User clicked the device target buttons, update the common commands. */
            $('#target-device-btn').click(function (ev) {
                var paoId = $('#pao-id').val(),
                    select = $('#common-commands');
                if (paoId) {
                    // We have picked a device previously.
                    _updateCommandsForPao(paoId);
                    $.ajax({ url: 'commander/' + paoId + '/data' })
                    .done(function (data) { _setupFieldsForDevice(data); });
                } else {
                    // No device selected yet, just nuke any commands in there.
                    select.find('option:first-child').siblings().remove();
                    select.trigger('chosen:updated').siblings('.chosen-container').css('width', select.css('width'));
                }
                $('#command-text').val('');
            });
            
            /** User clicked the lm group target buttons, update the common commands. */
            $('#target-lm-group-btn').click(function (ev) {
                var paoId = $('#lm-group-id').val(),
                    select = $('#common-commands');
                if (paoId) {
                    // We have picked an lm group previously.
                    _updateCommandsForPao(paoId);
                } else {
                    // No device selected yet, just nuke any commands in there.
                    select.find('option:first-child').siblings().remove();
                    select.trigger('chosen:updated').siblings('.chosen-container').css('width', select.css('width'));
                }
                $('#command-text').val('');
                $('.js-nearby-btn').addClass('dn');
            });
            
            /** User clicked the execute button. */
            $('#cmd-execute-btn').click(function (ev) { _execute(); });
            
            $('#saveFieldsBtn').click(function (ev) { _persistFields(); });
            
            /** User hit enter in the command textfield. */
            $('#command-text').on('keyup', function (ev) {
                if (ev.which === yg.keys.enter) _execute();
            });
            
            /** Stop IE from somehow opening a dropdown menu. */
            $('#command-text').on('keydown', function (ev) {
                if (ev.which === yg.keys.enter) return false; 
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
            
            /** User clicked the refresh button in the console. */
            $('#refresh-console-btn').click(function (ev) {
                $.ajax({
                    url: 'commander/refresh',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json'
                }).done(function (requests, status, xhr) {
                    $('#commander-results').empty().append('<div>');
                    _handleRequests(requests, true);
                });            
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
            
            /** User clicked ok on commander settings dialog. */
            $(document).on('yukon:tools:commander:popup', function (ev) {
                var value = Number($('#commandPriority').val()),
                    minPriority = Number($('#commandPriority').attr('min')),
                    maxPriority = Number($('#commandPriority').attr('max'));
                
                if (isNaN(value) || ((value % 1) !== 0) || value < minPriority || value > maxPriority) {
                    $('#commandPriority').addClass('error');
                    $('#invalidCommanderPriority').removeClass('dn');
                } else {
                    $('#commandPriority').removeClass('error');
                    $('#invalidCommanderPriority').addClass('dn');
                    _saveFields();
                    var dialog = $('.js-settings-popup'),
                        params = {
                            priority : $('#commandPriority').val(),
                            queueCommand : $('#queueCommand').val()
                        };
                    $.ajax({
                        type: 'post',
                        url: 'commander/updateCommanderPreferences',
                        data: params,
                    });
                    dialog.dialog('close');
                }
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
            
            $(document).on('keyup mouseup', '#commandPriority', function() {
                var value = Number($('#commandPriority').val()),
                    minPriority = Number($('#commandPriority').attr('min')),
                    maxPriority = Number($('#commandPriority').attr('max'));
                
                if (isNaN(value) || value < minPriority || value > maxPriority) {
                    $('#commandPriority').val(maxPriority);
                }
            });
            
            /** Prime the pending cache with any pending requests found in the console. */
            _primePending();
            
            /** Start the recursive updating. */
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
            
            $.ajax({ url: 'commander/' + paoId + '/data', dataType: 'json' })
            .done(function (data) { _setupFieldsForDevice(data); });
        },
        
        getPending: function () { return _pending; }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.commander.init(); });