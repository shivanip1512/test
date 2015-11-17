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
                var url = yukon.url('/group/commander/initCommands?groupName=' + groupName);

                var selectField = $('#select-group-command');
                var originalValue = selectField.val();

                $.getJSON(url, function (commands) {
                    selectField.empty();


                    if (commands.length === 0) {
                        selectField.next().addClass('dn');
                        $('#commandString').val('');
                        $('#no-command-options').show();
                    } else {
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
                        $('#no-command-options').hide();
                    }
                });
            });

            $('.js-group-commander-btn').on('click', function () {
                var btn = $(this);
                if ($('#groupName').val() === '') {
                    var message = btn.data('noGroupSelectedText');
                    alert(message);
                    return;
                }

                yukon.ui.busy(btn);
                $('#groupCommanderForm').submit();
            });
        }
    };

    return mod;
}());

$(function () { yukon.tools.group.command.init(); });
