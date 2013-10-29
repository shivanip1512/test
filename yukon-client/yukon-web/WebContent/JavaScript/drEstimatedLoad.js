var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.EstimatedLoad');

Yukon.EstimatedLoad = (function() {
   
    drEstimatedLoadModule = {
            
            createToolTip : function (msg) {
                var data = null == msg.value ? jQuery.parseJSON(msg.identifier) : jQuery.parseJSON(msg.value);
                if (data != null) {
                    if (typeof data.errorMessage != 'undefined') {
                        var errorMessage = data.errorMessage;
                        var row = jQuery('[data-pao=' + data.paoId +']');
                        row.attr("title", errorMessage);
                        row.find('i.f-error').show();
                        row.find('i.f-spinner').hide();
                        row.find('span').html(data.na);
                    } else if (typeof data.calculating != 'undefined') {
                        var row = jQuery('[data-pao=' + data.paoId + ']');
                        row.attr("title", data.calculating);
                        row.find('i.f-error').hide();
                        row.find('i.f-spinner').show();
                    } else {
                        var row = jQuery('[data-pao=' + data.paoId +']');
                        row.removeAttr("title");
                        row.find('i.f-error').hide();
                        row.find('i.f-spinner').hide();
                        row.find('span.f-connectedLoad').html(data.connected);
                        row.find('span.f-diversifiedLoad').html(data.diversified);
                        row.find('span.f-kwSavings').html(data.kwSavings);
                    }
                }
            }
    };
    
    return drEstimatedLoadModule;
}());
