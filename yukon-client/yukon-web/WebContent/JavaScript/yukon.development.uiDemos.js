
yukon.namespace('yukon.development');
yukon.namespace('yukon.development.uiDemos');

yukon.development.uiDemos = (function () {
    jQuery(function () {
        jQuery('#tabs').tabs({'class': 'section'});
        
        jQuery('#sections').click(function(e) {
            if(e.target.id === "containers") {
                jQuery.post('/support/development/uiDemos/containers').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "buttons") {
                jQuery.post('/support/development/uiDemos/buttons').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "pickers") {
                jQuery.post('/support/development/uiDemos/pickers').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "i18nScopes") {
                jQuery.post('/support/development/uiDemos/i18nScopes').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "i18n") {
                jQuery.post('/support/development/uiDemos/i18n').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "dialogs") {
                jQuery.post('/support/development/uiDemos/dialogs').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "jsTesting") {
                jQuery.post('/support/development/uiDemos/jsTesting').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "sprites") {
                jQuery.post('/support/development/uiDemos/sprites').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            } else if (e.target.id === "more") {
                jQuery.post('/support/development/uiDemos/more').done(function(result) {
                    jQuery('#rightColumn').html(result);
                });
            }
        });
    });
})();