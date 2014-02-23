
yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.macs');

yukon.ami.macs = (function () {
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
    yukon.ami.macs.init();
});
