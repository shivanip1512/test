var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.EstimatedLoad');

Yukon.EstimatedLoad = (function() {
   
    drEstimatedLoadModule = {
            
            createToolTip : function (msg) {
                console.log(msg);
                var data = null == msg.value ? jQuery.parseJSON(msg.identifier) : jQuery.parseJSON(msg.value);
                
                if (typeof data.errorMessage != 'undefined') {
                    var errorMessage = data.errorMessage;
                    var row = jQuery('[data-pao=' + data.paoId +']');
                    row.attr("title", errorMessage);
                    row.find('i').show();
                    
                } else {
                    var row = jQuery('[data-pao=' + data.paoId +']');
                    row.removeAttr("title");
                    row.find('i').hide();
                }
            }
    };
    
    return drEstimatedLoadModule;
}());
