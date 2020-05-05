yukon.namespace('yukon.widget.commChannel.info.js');
 
/** 
 * TODO 
 * @module yukon.widget.commChannel.info.js 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.widget.commChannel.info = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:assets:commChannel:save", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('#commChannel-info-form'),
                    popup = $('#js-edit-comm-channel-popup');
                $.ajax({
                    type: "POST",
                    url: yukon.url("/widget/commChannelInfoWidget/save"),
                    data: form.serialize()
                }).done(function (data) {
                    window.location.href = window.location.href;
                    dialog.dialog('close');
                    dialog.empty();
                }).fail(function (xhr, status, error){
                    popup.html(xhr.responseText);
                    yukon.ui.initContent(popup);
                });
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });