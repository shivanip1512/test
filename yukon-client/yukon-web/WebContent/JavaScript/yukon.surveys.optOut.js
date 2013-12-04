var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Surveys.OptOut');
Yukon.Surveys.OptOut = (function () {

    var mod = {
        init : function () {

            jQuery('.add-survey').click(function () {
                var button = jQuery(this),
                    url = button.attr('data-add-url'),
                    dialogTitle = button.closest('[data-dialog-title]').attr('data-dialog-title');
                openSimpleDialog('ajaxDialog', url, dialogTitle);
            });

            jQuery('.more-programs').click(function () {
                var link = jQuery(this),
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
                                    jQuery(this).dialog('close');
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

jQuery(function () {
    Yukon.Surveys.OptOut.init();
});