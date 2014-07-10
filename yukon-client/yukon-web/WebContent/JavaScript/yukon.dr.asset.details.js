
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
        $('[data-filter].on').each(function (idx, item) {
            filter.push($(item).data('filter'));
        });
        return filter;
    },

    _doFilterTable  = function(event) {
        $(event.currentTarget).toggleClass('on');
        var data = {'assetId': _assetId, 'filter': _getFilter()};
        if ("" != _itemsPerPage) {
            data.itemsPerPage = _itemsPerPage;
        }
        $('.device-detail-table').load('page', data);
        return false;
    },

    _downloadToCsv = function(event) {
        var data = {'assetId': _assetId, 'filter': _getFilter()},
            param = $.param(data);
        window.location = 'downloadToCsv?' + param;
        return false;
    },

    _pingDevices = function(event) {
        $('#pingResults').show();
        $('.progressbar-percent-complete').text("0%");
        $('.progress-bar').width(0);
        var url = "pingDevices?assetId=" + _assetId;
        $.ajax({
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

            _assetId = $("#assetId").val();
            _itemsPerPage = $("#itemsPerPage").val();
            _aaDiv = $('.js-asset-availability');

            $(document).on('click', '[data-filter]', _doFilterTable);
            $(document).on('click', '#dd-download', _downloadToCsv);
            $(document).on('click', '#pingButton', _pingDevices);
            
            if (_aaDiv.length) {
                yukon.ui.elementGlass.show(_aaDiv);
                $.ajax('assetAvailability', {
                    data: {'paoId': _assetId}
                }).done(function(data) {
                    _aaDiv.html(data);
                    yukon.ui.elementGlass.hide(_aaDiv);
                });
            }
            
            _initialized = true;
        },
        
        unbusyPingButton: function() {
            yukon.ui.unbusy($('#pingButton'));
        }

    };
    
    return mod;
    
}());

$(function() {
    yukon.dr.assetDetails.init();
});
