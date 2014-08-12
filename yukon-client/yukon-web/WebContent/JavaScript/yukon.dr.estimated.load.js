
yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.estimatedLoad');

yukon.dr.estimatedLoad = (function() {
   
    mod = {

            
            displayProgramValue : function (msg) {
                var data = $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                var connectedSpan = row.find('.js-connected-load');
                var diversifiedSpan = row.find('.js-diversified-load');
                var kwSavingsSpan = row.find('.js-kw-savings');
                
                var flashOnUpdate = row.data('flash-on-update') === undefined ? false : row.data('flash-on-update');
                
                if (status == 'error') {
                    row.attr("title", data.tooltip);
                    row.find('.icon-error').show();

                    if (connectedSpan.length > 0 && connectedSpan.html() != data.value) {
                        connectedSpan.html(data.value).flashYellow(1.5);
                    }
                    if (diversifiedSpan.length > 0 && diversifiedSpan.html() != data.value) {
                        diversifiedSpan.html(data.value).flashYellow(1.5);
                    }
                    if (kwSavingsSpan.length > 0 && kwSavingsSpan.html() != data.value) {
                        kwSavingsSpan.html(data.value).flashYellow(1.5);
                    }
                    flashOnUpdate = true;
                    row.data('in-error', true);
                } else if (status == 'success') {
                    row.removeAttr("title");

                    var inError = row.data('in-error');
                    if (inError) {
                        row.find('.icon-error').hide();
                        row.data('in-error', false);
                    }

                    if (connectedSpan.length > 0 && connectedSpan.html() != data.connected) {
                        connectedSpan.html(data.connected);
                        if (flashOnUpdate) {
                            connectedSpan.flashYellow(1.5);
                        }
                    }
                    if (diversifiedSpan.length > 0 && diversifiedSpan.html() != data.diversified) {
                        diversifiedSpan.html(data.diversified);
                        if (flashOnUpdate) {
                            diversifiedSpan.flashYellow(1.5);
                        }
                    }
                    if (kwSavingsSpan.length > 0 && kwSavingsSpan.html() != data.kwSavings) {
                        kwSavingsSpan.html(data.kwSavings);
                        if (flashOnUpdate) {
                            kwSavingsSpan.flashYellow(1.5);
                        }
                    }
                    flashOnUpdate = true;
                }
                row.data('flash-on-update', flashOnUpdate);
            },
    
            displaySummaryValue : function (msg) {
                var data = msg.value == undefined ? $.parseJSON(msg.identifier) : $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                var connectedSpan = row.find('.js-connected-load');
                var diversifiedSpan = row.find('.js-diversified-load');
                var kwSavingsSpan = row.find('.js-kw-savings');
                
                var flashOnUpdate = row.data('flash-on-update') === undefined ? false : row.data('flash-on-update');
                
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
                    if (connectedSpan.length > 0 && connectedSpan.html() != data.connected) {
                        connectedSpan.html(data.connected);
                        if (flashOnUpdate) {
                            connectedSpan.flashYellow(1.5);
                        }
                    }
                    if (diversifiedSpan.length > 0 && diversifiedSpan.html() != data.diversified) {
                        diversifiedSpan.html(data.diversified);
                        if (flashOnUpdate) {
                            diversifiedSpan.flashYellow(1.5);
                        }
                    }
                    if (kwSavingsSpan.length > 0 && kwSavingsSpan.html() != data.kwSavings) {
                        kwSavingsSpan.html(data.kwSavings);
                        if (flashOnUpdate) {
                            kwSavingsSpan.flashYellow(1.5);
                        }
                    }
                }
                flashOnUpdate = true;
                row.data('flash-on-update', flashOnUpdate);
            }
    };
    
    return mod;
}());
