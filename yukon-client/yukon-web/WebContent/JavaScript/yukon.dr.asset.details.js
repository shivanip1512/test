
yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.assetDetails');

yukon.dr.assetDetails = (function() {
    var
    _initialized = false, 
    _assetId = "", // Set in intializer
    _itemsPerPage = "", // Set in intializer
    _aaDiv = '', // Set in initializer
    
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
    
    mod = {
        init: function () {
            if (_initialized) {
                return;
            }

            _assetId = jQuery("#assetId").val();
            _itemsPerPage = jQuery("#itemsPerPage").val();
            _aaDiv = jQuery('.f-asset-availability');

            jQuery(document).on('click', '[data-filter]', _doFilterTable);
            jQuery(document).on('click', '#dd-download', _downloadToCsv);
            jQuery(document).on('click', '#pingButton', _pingDevices);
            
            if (_aaDiv.length) {
                yukon.ui.elementGlass.show(_aaDiv);
                jQuery.ajax('assetAvailability', {
                    data: {'paoId': _assetId}
                }).done(function(data) {
                    _aaDiv.html(data);
                    yukon.ui.elementGlass.hide(_aaDiv);
                });
            }
            
            _initialized = true;
        },
        
        unbusyPingButton: function() {
            yukon.ui.unbusy(jQuery('#pingButton'));
        }

    };
    
    return mod;
    
}());

jQuery(function() {
    yukon.dr.assetDetails.init();
});
