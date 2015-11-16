/**
 * Tooltips
 * 
 * Elements with attribute [data-tooltip] will have html tooltips.
 * The value of the attribute [data-tooltip] is a selector for the
 * element to serve as the tooltip body.
 * If the tooltip has class js-sticky-tooltip, clicking the item
 * will cause the tooltip to stay visible until the next click.
 * 
 * @requires JQUERY
 */
$(function() {
    
    /** 
     * Move the tooltip to the body of the page, then position it below the 
     *  @param {Object} tooltip - tooltip item to be positioned.
     *  @param {Object} ev - event object with pageX and pageY for positioning.
     */
    var _positionTooltip = function (tooltip, ev) {
        
        tooltip = $(tooltip);
        
        tooltip.appendTo('body')
        .addClass('yukon-tooltip');
        
        var width = tooltip.outerWidth();
        var height = tooltip.outerHeight();
        
        tooltip.css({
            top: ev.pageY - height - 2,
            left: ev.pageX - width / 2
        });

        var windowWidth = $(window).width();

        if (ev.pageX + width / 2 > windowWidth ) {
            // The tooltip is overflowing off the right edge of the screen.
            // Make its right edge touch the edge of the screen.
            tooltip.css({
                left: windowWidth - width
            });
        }
        
        if (ev.pageX - width / 2 < 0 ) {
            // The tooltip is overflowing off the left edge of the screen.
            // Make its left edge touch the edge of the screen
            tooltip.css({
                left: 0
            });
        }
        
        tooltip.show();
    };
    
    /**
     * @type {number}
     * ID of current running hover timer (from setTimeout). Used for clearTimeout.
     */
    var _timeout;

    /** Handle hovers*/
    $(document).on('mouseover', '[data-tooltip]', function (ev) {
        
        var trigger = $(this);
        var tooltip = $(trigger.data('tooltip'));
        
        if (tooltip.is(':visible')) return;
        
        var pageX = ev.pageX;
        var pageY = ev.pageY;
        
        trigger.off('mousemove.yukon.tooltip')
        .on('mousemove.yukon.tooltip', function (ev) {
            pageX = ev.pageX;
            pageY = ev.pageY;
        });
        
        /* If the hover lasts more than 300ms, display the tooltip */
        clearTimeout(_timeout);
        _timeout = setTimeout(function () {
            trigger.off('mousemove.yukon.tooltip');
            _positionTooltip(tooltip, {pageX: pageX, pageY: pageY});
        }, 300);
    });
    
    /** Handle hover stops*/
    $(document).on('mouseleave', '[data-tooltip]', function (ev) {
        var trigger = $(this);
        clearTimeout(_timeout);
        trigger.off('mousemove.yukon.tooltip');
        $('.yukon-tooltip:not(.yukon-tooltip-open)').hide();
    });
        
    
    /** Handle clicks on sticky tooltips */
    $(document).on('click', '[data-tooltip]', function(ev) {
        
        var trigger = $(this);
        var tooltip = $(trigger.data('tooltip'));
        
        if (!tooltip.is('.js-sticky-tooltip')) return true;
        
        $('.yukon-tooltip').hide();
        
        tooltip.addClass('yukon-tooltip-open');
        
        _positionTooltip(tooltip, ev);
    });

    /** Close all tooltips on click (except when clicking a tooltip) */
    $(document).click(function (ev) {
        if ($(ev.target).closest('.yukon-tooltip, [data-tooltip]').length === 0) {
            $('.yukon-tooltip').removeClass('yukon-tooltip-open').hide();
        }
    });
    
    /** Close all tooltips when esc key is hit */
    $(document).keyup(function (ev) {
        if (ev.which == yg.keys.escape) {
            $('.yukon-tooltip').removeClass('yukon-tooltip-open').hide();
        }
    });
});