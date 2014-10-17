yukon.namespace('yukon.dr.estimatedLoad');

/**
 * Singleton that manages the Demand Response Load estimation feature.
 * 
 * @module yukon.dr.estimatedLoad
 * @requires JQUERY
 */
yukon.dr.estimatedLoad = (function() {
   
    mod = {

            /** Display the program value as per the message passed */
            displayProgramValue : function (msg) {
                var data = $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                var connected = row.find('.js-connected-load');
                var diversified = row.find('.js-diversified-load');
                var kwSavings = row.find('.js-kw-savings');
                
                var flash = row.is('[data-flash]');
                
                if (status == 'error') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();
                    
                    if (connected.length > 0 && connected.text() != data.value) {
                        connected.text(data.value).flash();
                    }
                    if (diversified.length > 0 && diversified.text() != data.value) {
                        diversified.text(data.value).flash();
                    }
                    if (kwSavings.length > 0 && kwSavings.text() != data.value) {
                        kwSavings.text(data.value).flash();
                    }
                } else if (status == 'success') {
                    row.removeAttr("title");
                    row.find('.icon-error').hide();
                    
                    if (connected.length > 0 && connected.text() != data.connected) {
                        connected.text(data.connected);
                        if (flash) {
                            connected.flash();
                        }
                    }
                    if (diversified.length > 0 && diversified.text() != data.diversified) {
                        diversified.text(data.diversified);
                        if (flash) {
                            diversified.flash();
                        }
                    }
                    if (kwSavings.length > 0 && kwSavings.text() != data.kwSavings) {
                        kwSavings.text(data.kwSavings);
                        if (flash) {
                            kwSavings.flash();
                        }
                    }
                }
                row.attr('data-flash', '');
            },
            
            displaySummaryValue : function (msg) {
                var data = msg.value == undefined ? $.parseJSON(msg.identifier) : $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                var connected = row.find('.js-connected-load');
                var diversified = row.find('.js-diversified-load');
                var kwSavings = row.find('.js-kw-savings');
                
                var flash = row.is('[data-flash]');
                
                // Display/hide icons and tooltips based on status.
                if (status == 'error' || status == 'errorAndCalc') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();
                } else if (status == 'calc') {
                    row.attr("title", data.tooltip);
                } else if (status == 'success') {
                    row.removeAttr("title");
                    row.find('.icon-error').hide();
                }
                
                // Update and flash amount fields only when in error or success states. 
                if (status == 'error' || status == 'success') {
                    if (connected.length > 0 && connected.text() != data.connected) {
                        connected.text(data.connected);
                        if (flash) {
                            connected.flash();
                        }
                    }
                    if (diversified.length > 0 && diversified.text() != data.diversified) {
                        diversified.text(data.diversified);
                        if (flash) {
                            diversified.flash();
                        }
                    }
                    if (kwSavings.length > 0 && kwSavings.text() != data.kwSavings) {
                        kwSavings.text(data.kwSavings);
                        if (flash) {
                            kwSavings.flash();
                        }
                    }
                }
                row.attr('data-flash', '');
            }
    };
    
    return mod;
}());
