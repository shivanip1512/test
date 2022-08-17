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
            
            $('.js-manual-entry').click(function (ev) {
                
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
            
        }
    };
    
    return _mod;
}());

$(function () { yukon.points.init(); });