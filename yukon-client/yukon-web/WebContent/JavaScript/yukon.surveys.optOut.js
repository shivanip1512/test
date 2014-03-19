/**
 * Singleton that manages the survey opt out page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.surveys');
yukon.namespace('yukon.surveys.OptOut');

yukon.surveys.OptOut = (function () {

    var mod = {
        init : function () {

            $('.add-survey').click(function () {
                var button = $(this),
                    url = button.attr('data-add-url'),
                    dialogTitle = button.closest('[data-dialog-title]').attr('data-dialog-title');
                openSimpleDialog('ajaxDialog', url, dialogTitle);
            });

            $('.more-programs').click(function () {
                var link = $(this),
                    url = link.attr('data-list-url'),
                    dialogTitle = link.closest('[data-dialog-title]').attr('data-dialog-title'),
                    okText = link.closest('[data-ok-text]').attr('data-ok-text');

                openSimpleDialog(
                    'ajaxDialog',
                    url,
                    dialogTitle,
                    undefined,
                    undefined,
                    {
                        buttons: {
                            ok: {
                                text: okText,
                                click: function () {
                                    $(this).dialog('close');
                                }
                            }
                        }
                    }
                );
            });
        }
    };
    return mod;
}());

$(function () {
    yukon.surveys.OptOut.init();
});