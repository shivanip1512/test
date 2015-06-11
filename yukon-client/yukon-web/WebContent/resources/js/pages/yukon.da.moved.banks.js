yukon.namespace('yukon.da.movedBanks');

yukon.da.movedBanks = (function () {

    var mod;

    mod = {
        init : function () {
            $(document).on('click', '.js-assign,.js-return', function () {
                var btn = $(this),
                    bankId = btn.closest('[data-bank-id]').data('bankId'),
                    command = btn.is('.js-assign') ? 'assign-here' : 'move-back',
                    url = yukon.url('/capcontrol/command/' + bankId + '/' + command);

                $.getJSON(url).done(function (data) {
                    if (data.success) {
                        btn.closest('tr').fadeOut();
                    } else {
                        window.location.reload();
                    }
                }).error(function () {
                    window.location.reload();
                });
            });
        },
    };
    return mod;
}());

$(function () { yukon.da.movedBanks.init(); });