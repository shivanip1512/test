(function (jq) {
    jq.fn.selectText = function() {
        var text = this[0],
            range,
            selection;
        if (document.body.createTextRange) {
            range = document.body.createTextRange();
            range.moveToElementText(text);
            range.select();
        } else if (window.getSelection) {
            selection = window.getSelection();        
            range = document.createRange();
            range.selectNodeContents(text);
            selection.removeAllRanges();
            selection.addRange(range);
        }
    };

    jq.fn.toggleDisabled = function() {
        return this.each(function() {
            if (jQuery(this).is(":input")) {
                this.disabled = !this.disabled;
            }
        });
    };

    jq.fn.flashColor = function(args) {
        return this.each(function() {
            var _self = jQuery(this),
                prevColor = _self.data('previous_color') ? _self.data('previous_color') : _self.css('background-color');
            _self.data('previous_color', prevColor);
    
            if (typeof(args) === 'string') {
                _self.stop(true);
                _self.css({backgroundColor: args}).animate({backgroundColor: prevColor, duration: 1000});
            } else if (typeof(args) === 'object' && typeof(args.color) === 'string') {
                _self.stop(true);
                _self.css({backgroundColor: args.color}).animate({backgroundColor: prevColor}, args);
            }
        });
    };

    jq.fn.flashYellow = function (duration) {
        return this.each(function() {
            if (typeof(duration) != 'number') {
                duration = 0.8;
            }
            jQuery(this).flashColor({color: "#FF0", duration: duration*1000});
        });
    };

})(jQuery);

