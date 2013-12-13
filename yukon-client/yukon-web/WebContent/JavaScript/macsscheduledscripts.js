var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Macs');
Yukon.Macs = (function () {
    var _autoUpdatePageContent = function () {
        var tableContainer = jQuery('[data-reloadable]'),
            reloadUrl = tableContainer.attr('data-url');
        tableContainer.load(reloadUrl, function () {
            setTimeout(_autoUpdatePageContent, 5000);
        });

    },
        mod = {
            init: function () {
                var tableContainer = jQuery('[data-reloadable]');
                if (tableContainer.length === 1) {
                    _autoUpdatePageContent();
                }
            }
    };
    return mod;
}());

jQuery(function () {
    Yukon.Macs.init();
});
