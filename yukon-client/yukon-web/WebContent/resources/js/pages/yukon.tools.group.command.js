yukon.namespace('yukon.tools.group.command');

/**
 * Module that manages the Command Processing page
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.tools.group.command = (function () {

    'use strict';

    var mod = {

        init : function () {

            $('#commandString').on('change', function () {
                var command = $(this).val().trim();
                $('.js-group-commander-btn').prop('disabled', command === '');
            });

            $('#groupName').on('change', function () {
                var input = $(this);
                var groupName = input.val();

                if (groupName === '') {
                    $('#no-group-chosen').removeClass('dn');
                    $('#commands-available').addClass('dn');
                    return;
                }
                $('#no-group-chosen').addClass('dn');

                var url = yukon.url('/group/commander/initCommands?groupName=' + groupName);
                $.getJSON(url, function (commands) {
                    var selectField = $('#select-group-command');
                    var originalValue = selectField.val();
                    selectField.empty();
                    var commandsContainer = $('#commands-available');
                    var noCommandsSpan = commandsContainer.find('.empty-list');

                    $('#commands-available').removeClass('dn');
                    if (commands.length === 0) {
                        $('#commandString').val('').trigger('change');
                        selectField.next('.chosen-container').addClass('dn');
                        noCommandsSpan.removeClass('dn');
                        return;
                    }
                    noCommandsSpan.addClass('dn');

                    var originalValueStillAvailable = false;

                    commands.forEach(function (command) {
                        var htmlOption = $('<option>')
                        .val(command.command)
                        .text(command.label);
                        selectField.append(htmlOption);

                        if (command.command === originalValue) {
                            originalValueStillAvailable = true;
                        }
                    });

                    if (originalValueStillAvailable) {
                        selectField.val(originalValue);
                    } else {
                        selectField.val(commands[0].command).trigger('change');
                    }

                    selectField.next('.chosen-container').removeClass('dn');
                    selectField.trigger('chosen:updated')
                        .siblings('.chosen-container')
                            .css('width', selectField.getHiddenDimensions().innerWidth + 11);
                });
            });
        }
    };

    return mod;
}());

$(function () { yukon.tools.group.command.init(); });
