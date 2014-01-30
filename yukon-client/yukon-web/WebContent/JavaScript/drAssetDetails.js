var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.DrAssetDetails');

Yukon.DrAssetDetails = (function() {
    var
    _initialized = false, 
    _assetId = "", // Set in intializer
    _itemsPerPage = "", // Set in intializer
    
    _getFilter = function() {
        var filter = [];
        jQuery('[data-filter].on').each(function (idx, item) {
            filter.push(jQuery(item).data('filter'));
        });
        return filter;
    },

    _doFilterTable  = function(event) {
        jQuery(event.currentTarget).toggleClass('on');
        var data = {'assetId': _assetId, 'filter': _getFilter()};
        if ("" != _itemsPerPage) {
            data.itemsPerPage = _itemsPerPage;
        }
        jQuery('.device-detail-table').load('page', data);
        return false;
    },

    _downloadToCsv = function(event) {
        var data = {'assetId': _assetId, 'filter': _getFilter()},
            param = jQuery.param(data);
        window.location = 'downloadToCsv?' + param;
        return false;
    },

    _pingDevices = function(event) {
        jQuery('#pingResults').show();
        jQuery('.progressbar-percent-complete').text("0%");
        jQuery('.progressbar-inner-fail,.progressbar-inner-success').width(0);
        var url = "pingDevices?assetId=" + _assetId;
        jQuery.ajax({
            url: url,
            method: 'POST'
        });
        return true;
    },
    
    drAssetDetailsModule = {
        init: function () {
            if (_initialized) {
                return;
            }

            _assetId = jQuery("#assetId").val();
            _itemsPerPage = jQuery("#itemsPerPage").val();

            jQuery("[data-filter]").click(_doFilterTable);
            jQuery("#dd-download").click(_downloadToCsv);
            jQuery("#pingButton").click(_pingDevices);

            _initialized = true;
        },
        
        unbusyPingButton: function() {
            Yukon.ui.unbusy(jQuery('#pingButton'));
        }

    };
    
    return drAssetDetailsModule;
    
}());

jQuery(function() {
    Yukon.DrAssetDetails.init();
});
