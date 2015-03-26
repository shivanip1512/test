yukon.namespace('yukon.dr.estimatedLoad');

/**
 * Module for the estimated load section of dr pages.
 * 
 * @module yukon.dr.estimatedLoad
 * @requires JQUERY
 */
yukon.dr.estimatedLoad = (function () {
    
    var mod = {

            /** Display the estimated load values for the paoId requested by the data updater. */
            displayValue : function (msg) {
                var data = $.parseJSON(msg.value);
                var status = data.status;
                var row = $('[data-pao=' + data.paoId +']');
                
                var connected = row.find('.js-connected-load');
                var diversified = row.find('.js-diversified-load');
                var kwSavings = row.find('.js-kw-savings');
                
                if (status === 'error') {
                    
                    row.find('.js-est-load-error-btn .icon').removeClass().addClass('icon ' + data.icon);
                    row.find('.js-est-load-error-btn').removeClass('dn');
                    row.find('.js-est-load-calculating').addClass('dn');
                    connected.text(data.na);
                    diversified.text(data.na);
                    kwSavings.text(data.buttonText);
                    
                } else if (status === 'calc') {
                    
                    // Do nothing here, waiting for an error or success state.
                    
                } else if (status === 'success') {
                    
                    row.find('.js-est-load-error-btn').addClass('dn');
                    row.find('.js-est-load-calculating').addClass('dn');
                    if (connected.length > 0 && connected.text() !== data.connected) {
                        connected.text(data.connected).flash();
                    }
                    if (diversified.length > 0 && diversified.text() !== data.diversified) {
                        diversified.text(data.diversified).flash();
                    }
                    if (kwSavings.length > 0 && kwSavings.text() !== data.kwSavings) {
                        kwSavings.text(data.kwSavings).flash();
                    }
                }
            }
    };
    
    return mod;
}());