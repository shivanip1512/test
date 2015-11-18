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

            $('#groupName').on('change', function () {
                var input = $(this);
                var groupName = input.val();

                if (groupName === '') {
                    $('#no-group-chosen').removeClass('dn');
                    $('#no-commands-available').addClass('dn');
                    $('#commands-available').addClass('dn');
                    return;
                }
                $('#no-group-chosen').addClass('dn');

                var url = yukon.url('/group/commander/initCommands?groupName=' + groupName);
                $.getJSON(url, function (commands) {
                    var selectField = $('#select-group-command');
                    var originalValue = selectField.val();
                    selectField.empty();

                    if (commands.length === 0) {
                        $('#no-commands-available').removeClass('dn');
                        $('#commands-available').addClass('dn');
                        return;
                    }
                    $('#no-commands-available').addClass('dn');
                    $('#commands-available').removeClass('dn');

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
