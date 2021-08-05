yukon.namespace('yukon.points');

/**
 * Module that manages the point data.
 * @module   yukon.points
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.points = (function () {

    _mod = {

        init : function () {
            
            $(document).on('click', '.js-manual-entry', function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/points/manual-entry');
                
                $('#manual-entry-popup').load(url, { pointId : pointId }, function () {
                    $('#manual-entry-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $(document).on('click', '.js-manual-entry-send', function (ev) {
                submitFormViaAjax('manual-entry-popup', 'manual-entry-form');
            });
            
            $(document).on('click', '.js-manual-entry-close', function (ev) {
                $('#manual-entry-popup').dialog('close');
            });
            
            $('.js-manual-control').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    deviceId = option.data('deviceId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/points/manual-control'),
                    data = { deviceId : deviceId, pointId : pointId };
                
                $('#manual-control-popup').load(url, data, function () {
                    $('#manual-control-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $(document).on('click', '.js-manual-control-send', function (ev) {
                submitFormViaAjax('manual-control-popup', 'manual-control-form');
            });
            
            $(document).on('click', '.js-manual-control-close', function (ev) {
                $('#manual-control-popup').dialog('close');
            });
            
        }
    };
    
    return _mod;
}());

$(function () { yukon.points.init(); });