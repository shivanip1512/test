jQuery(function() {
    jQuery('.f-has-tooltip').tipsy({
        html: true,
        gravity: jQuery.fn.tipsy.autoNS,
        //className: 'toolTipClass',
        title: function () {
            var elem = jQuery(this),
                tip,
                toolTipped = elem.closest(".f-has-tooltip");
            if ( 0 < toolTipped.length ) {
                tip = toolTipped.next(".f-tooltip");
                console.log('tip.height()=' + tip.height());
                if (0 < tip.length) { // if a .f-tooltip just ahead...
                    return tip.html();
                } else {
                    tip = elem.attr('original-title'); // tipsy stashes it here
                    return tip;
                }
            }
            else {
                // in theory, we should never get here, but you never know
                tip = elem.attr('original-title');
                if ('undefined' === typeof tip || '' === tip) {
                    tip = '';
                }
                return tip;
            }
        }
    });
});
