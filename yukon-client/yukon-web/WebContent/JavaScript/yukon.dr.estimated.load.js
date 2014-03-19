
yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.estimatedLoad');

yukon.dr.estimatedLoad = (function() {
   
    mod = {
            
            displayProgramValue : function (msg) {
                var data = $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                if (status == 'error') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();
                    row.find('.icon-spinner').hide();
                    row.find('.f-connected-load').html(data.value);
                    row.find('.f-diversified-load').html(data.value);
                    row.find('.f-kw-savings').html(data.value);
                } else if (status == 'calc') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').hide();
                    row.find('.icon-spinner').show();
                } else if (status == 'success') {
                    row.removeAttr("title");
                    row.find('.icon-error').hide();
                    row.find('.icon-spinner').hide();
                    row.find('.f-connected-load').html(data.connected);
                    row.find('.f-diversified-load').html(data.diversified);
                    row.find('.f-kw-savings').html(data.kwSavings);
                }
            },
    
            displaySummaryValue : function (msg) {
                var data = msg.value == undefined ? $.parseJSON(msg.identifier) : $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                row.find('.f-connected-load').html(data.connected);
                row.find('.f-diversified-load').html(data.diversified);
                row.find('.f-kw-savings').html(data.kwSavings);
                if (status == 'error') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();
                    row.find('.icon-spinner').hide();
                } else if (status == 'calc') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').hide();
                    row.find('.icon-spinner').show();
                } else if (status == 'errorAndCalc') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();
                    row.find('.icon-spinner').show();
                } else if (status == 'success') {
                    row.removeAttr("title");
                    row.find('.icon-error').hide();
                    row.find('.icon-spinner').hide();
                } 
            }
    };
    
    return mod;
}());
