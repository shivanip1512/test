/*----------------------------------------------------  
    ActionWhen plugin for jQuery
    Version: 1.0

    Copyright (c) 2012 Cooper Power Systems (Alex Delegard)
    
    May 9, 2012

    Requires: jQuery 1.6.4+
    Last tested with: 1.6.4
------------------------------------------------------*/

;(function($)
{
    $.fn.showWhenChecked = function (group, options)
    {
        var opts = $.extend({}, $.fn.showWhenChecked.defaults, options),
            $to_show = this,
            $checkboxes = $(group),
            onClick = typeof opts.onClick === 'function' ? opts.onClick : null,
            reportTo = typeof opts.reportTo === 'function' ? opts.reportTo : null;

        function _countChecked() {
            return $checkboxes.filter(':checked').length;
        }
            
        function _show_hide() {
            var numChecked = _countChecked();
            if (numChecked > 0) {
                $to_show.show();
            } else {
                $to_show.hide();
            }
            if (reportTo) {
                reportTo(numChecked);
            }
        }

        $to_show.unbind('click.showWhenChecked').bind('click.showWhenChecked', function (e) {
            if (reportTo) {
                reportTo(check_val ? _countChecked() : 0);
            }
        });

        $($checkboxes.selector).die('click.showWhenChecked').live('click.showWhenChecked', function () {
            _show_hide();
            if (onClick) {
                onClick.apply(this);
            }
        });
        
        _show_hide();
        
        return this;
    };
    
    $.fn.showWhenChecked.defaults = {};
}(jQuery));