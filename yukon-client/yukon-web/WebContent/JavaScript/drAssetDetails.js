var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.DrAssetDetails');

Yukon.DrAssetDetails = (function() {
    var
    _initialized = false, 
    _assetId = "", // Set in intializer
    _assetType = "", // Set in intializer
    _itemsPerPage = "", // Set in intializer
    
    _getFilter = function() {
        var filter = [];
        jQuery('[data-filter].on').each(function (idx, item) {
            filter.push(jQuery(item).data('filter'));
        });
        if (filter.length > 0) {
            return JSON.stringify(filter);
        } else {
            return "";
        }
    },
    
    _doFilterTable  = function(event) {
        event.stopPropagation();
        jQuery(event.currentTarget).toggleClass('on');
        var data = {'assetId': _assetId, 'type': _assetType, 'filter': _getFilter()};
        if ("" != _itemsPerPage) {
            data.itemsPerPage = _itemsPerPage;
        }
        jQuery('.device-detail-table').load('page', data);
        return false;
    },

    _doPagingWithFilter = function(event) {
        var url = jQuery(event.currentTarget).data('load'),
            data = {'filter': _getFilter()};
        jQuery.ajax({
            url: url,
            method: 'get',
            data: data
        }).done(function(data) {
            var parent = jQuery(event.currentTarget).closest(".device-detail-table");
            parent.html(data);
        });
        return false;
    },
    
    _doSortingWithFilter = function(event) {
        event.stopPropagation();
        var url = this.href,
            data = {'filter': _getFilter()};
        jQuery.ajax({
            url: url,
            method: 'get',
            data: data
        }).done(function(data) {
            var parent = jQuery(event.currentTarget).closest(".device-detail-table");
            parent.html(data);
        });
        return false;
    },

    _downloadToCsv = function(event) {
        var url = "downloadToCsv?filter=" + _getFilter() + "&assetId=" + _assetId + "&type=" + _assetType;
        window.location.href = url;
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
            _assetType = jQuery("#assetType").val();
            _itemsPerPage = jQuery("#itemsPerPage").val();

            jQuery("[data-filter]")
                .click(_doFilterTable);

            jQuery(".device-detail-table")
                .on('click', '.f-sort-link', _doSortingWithFilter)
                .on('click', '[data-load]', _doPagingWithFilter);
            
            jQuery("#dd-download")
                .click(_downloadToCsv);
            
            jQuery("#pingButton")
                .click(_pingDevices);

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
