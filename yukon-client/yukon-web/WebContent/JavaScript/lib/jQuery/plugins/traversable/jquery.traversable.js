/*
 * jQuery Traverse plugin for traversing elements within containers with the keyboard
 *
 * Copyright (c) 2012 Alex Delegard
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *  https://github.com/adelegard/jquery-traverse
 *
 */

;(function($)
{
    $.fn.traverse = function (traverse, options)
    {
        // if there are no elements, then just return
        if (this.length === 0 || typeof traverse === 'undefined' || traverse === null) return;
        var opts = $.extend({}, $.fn.traverse.defaults, options),
            $master = this,

            container_index = 0,
            container_selector = $master.selector,
            traverse_selector = (typeof opts.skip_selector !== 'undefined' ? (traverse + ':not(' + opts.skip_selector + ')') : traverse),
            container_traverse_selector = container_selector + ' ' + traverse_selector,
            cur_highlighted_selector = container_traverse_selector + '.' + opts.highlight_class,

            on_action_before = typeof opts.on_action_before === 'function' ? opts.on_action_before : null,
            on_action_after = typeof opts.on_action_after === 'function' ? opts.on_action_after : null,
            on_action_override = typeof opts.on_action_override === 'function' ? opts.on_action_override : null,

            on_key_down_before = typeof opts.on_key_down_before === 'function' ? opts.on_key_down_before : null,
            on_key_down_after = typeof opts.on_key_down_after === 'function' ? opts.on_key_down_after : null,
            on_key_down_override = typeof opts.on_key_down_override === 'function' ? opts.on_key_down_override : null,

            on_key_up_before = typeof opts.on_key_up_before === 'function' ? opts.on_key_up_before : null,
            on_key_up_after = typeof opts.on_key_up_after === 'function' ? opts.on_key_up_after : null,
            on_key_up_override = typeof opts.on_key_up_override === 'function' ? opts.on_key_up_override : null,
            
            jump_between_up_before = typeof opts.jump_between_up_before === 'function' ? opts.jump_between_up_before : null,
            jump_between_up_after = typeof opts.jump_between_up_after === 'function' ? opts.jump_between_up_after : null,
            jump_between_up_override = typeof opts.jump_between_up_override === 'function' ? opts.jump_between_up_override : null,

            jump_between_down_before = typeof opts.jump_between_down_before === 'function' ? opts.jump_between_down_before : null,
            jump_between_down_after = typeof opts.jump_between_down_after === 'function' ? opts.jump_between_down_after : null,
            jump_between_down_override = typeof opts.jump_between_down_override === 'function' ? opts.jump_between_down_override : null,

            // for compatibility with 1.4.2 through 1.6
            propFn = typeof $.fn.prop === 'function' ? 'prop' : 'attr';

        // add class highlight to the first container traverse element on the page
        var first_container_traverse_element = _getFirstContainerTraverseElement($(container_selector).first());
        if (first_container_traverse_element.nextAll(traverse_selector).length > 0) first_container_traverse_element.addClass(opts.highlight_class);

        $(document)[opts.key_event](function(e) {
            // if user has focus over an input or a button then do nothing
            if ($(opts.key_ignore_when_selector).length > 0) return;

            var cur_traverse_element = $(cur_highlighted_selector),
                prev_traverse_element = cur_traverse_element.prevAll(traverse_selector + ':first'),
                next_traverse_element = cur_traverse_element.nextAll(traverse_selector + ':first');

            // action key
            if (_is_key(opts.key_action, e.which)) {
                if (on_action_before) {
                    on_action_before.call(this, e);
                }
                if (on_action_override) {
                    on_action_override.call(this, e);
                } else {
                    var first_anchor = $('.' + opts.highlight_class + opts.action_selector);
                    // make sure we have an anchor and that the user isn't in an input
                    if (opts.action_window_location_href && first_anchor.length > 0) {
                        window.location.href = first_anchor[propFn]('href');
                    }
                }
                if (on_action_after) {
                    on_action_after.call(this, e);
                }
            }
            
            // key down
            if (_is_key(opts.key_down, e.which)) {
                if (on_key_down_before) {
                    on_key_down_before.call(this, e);
                }
                if (on_key_down_override) {
                    on_key_down_override.call(this, e);
                } else {
                    if (next_traverse_element.length === 0) {
                        if (opts.jump_between && jump_between_down_before) {
                            jump_between_down_before.call(this, e);
                        }
                        if (opts.jump_between && jump_between_down_override) {
                            jump_between_down_override.call(this, e);
                        } else {
                            if (!opts.jump_between && !opts.loop) return;
                            var next_container = null;
                            if (!opts.jump_between && opts.loop) {
                                next_container = $(container_selector + ':eq(0)');
                            } else {
                                next_container = $(container_selector + ':eq(' + (container_index+1) + ')');
                                if (next_container.length === 0) {
                                    if (opts.loop) {
                                        next_container = $(container_selector + ':eq(0)');
                                        container_index = -1; // since we will increment this below (hopefully...)
                                    }
                                }
                            }
                            var next_container_traverse_element = _getFirstContainerTraverseElement(next_container);
                            if (next_container_traverse_element.length > 0) {
                                $(container_traverse_selector).removeClass(opts.highlight_class);
                                next_container_traverse_element.addClass(opts.highlight_class);
                                container_index++;
                            }
                        }
                        if (opts.jump_between && jump_between_down_after) {
                            jump_between_down_after.call(this, e);
                        }
                    } else {
                        $(container_traverse_selector).removeClass(opts.highlight_class);
                        next_traverse_element.addClass(opts.highlight_class);
                    }
                }
                if (on_key_down_after) {
                    on_key_down_after.call(this, e);
                }

            // key up
            } else if (_is_key(opts.key_up, e.which)) {
                if (on_key_up_before) {
                    on_key_up_before.call(this, e);
                }
                if (on_key_up_override) {
                    on_key_up_override.call(this, e);
                } else {
                    if (prev_traverse_element.length === 0) {
                        if (opts.jump_between && jump_between_up_before) {
                            jump_between_up_before.call(this, e);
                        }
                        if (opts.jump_between && jump_between_up_override) {
                            jump_between_up_override.call(this, e);
                        } else {
                            if (!opts.jump_between && !opts.loop) return;
                            var prev_container = null;
                            if (!opts.jump_between && opts.loop) {
                                prev_container = $(container_selector + ':eq(0)');
                            } else {
                                if (container_index === 0) {
                                    if (!opts.loop) return;
                                    prev_container = $(container_selector + ':eq(' + ($master.length-1) + ')');
                                    container_index = $master.length; // since we will decrement this below (hopefully...)
                                } else {
                                    prev_container = $(container_selector + ':eq(' + (container_index-1) + ')');
                                }
                            }
                            var prev_container_traverse_element = _getLastContainerTraverseElement(prev_container);
                            if (prev_container_traverse_element.length > 0) {
                                $(container_traverse_selector).removeClass(opts.highlight_class);
                                prev_container_traverse_element.addClass(opts.highlight_class);
                                container_index--;
                            }
                        }
                        if (opts.jump_between && jump_between_up_after) {
                            jump_between_up_after.call(this, e);
                        }
                    } else {
                        $(container_traverse_selector).removeClass(opts.highlight_class);
                        prev_traverse_element.addClass(opts.highlight_class);
                    }
                }
                if (on_key_up_after) {
                    on_key_up_after.call(this, e);
                }
            }

            // move the scroll bar so our highlight_class is visible
            if (opts.move_scrollbar && (_is_key(opts.key_down, e.which) || _is_key(opts.key_up, e.which))) {
                _keep_element_in_view(cur_highlighted_selector);
            }
        });

        function _keep_element_in_view(element) {
            var the_traverse_element = $(element);
            if (_in_view(the_traverse_element, 0)) return false;
            var offset = the_traverse_element.offset(); // Contains .top and .left
            var window_height = window.innerHeight;

            if (_below_view(the_traverse_element, 0)) {
                offset.top = (offset.top - (window_height - (the_traverse_element.height() * 3)));
                $('html, body').scrollTop(offset.top);
            }
            else if (_above_view(the_traverse_element, 0)) {
                offset.top = (offset.top - (the_traverse_element.height() * 3));
                $('html, body').scrollTop(offset.top);
            }
            return false;
        }

        function _below_view(element, threshold) {
            var fold = window.innerHeight + $(window).scrollTop();
            return fold <= element.offset().top - threshold;
        }
        function _above_view(element, threshold) {
            var top = $(window).scrollTop();
            return top >= element.offset().top + element.height() - threshold;
        }
        function _in_view(element, threshold) {
            return !_below_view(element, threshold) && !_above_view(element, threshold);
        }

        function _is_key(keys, which) {
            if (typeof(keys) === 'number') {
                return keys === which;
            }
            if (typeof(keys) === 'object') {
                return keys.indexOf(which) != -1;
            }
            return false;
        }

        function _getFirstContainerTraverseElement(container) {
            if (opts.table_row_helper) {
                // attempting to account for inconsistent table markup
                // Specifically, tables with their header row as:
                //   - the first row in the tbody
                //   - the first row in the table with no thead or tbody
                var thead_exists = container.find('thead').length > 0;
                var tbody_exists = container.find('tbody').length > 0;
                if (thead_exists && tbody_exists) {
                    return container.find('tbody ' + traverse_selector + ':first');
                } else if (!thead_exists && tbody_exists) {
                    return container.find(traverse_selector + ':nth-child(2)');
                }
            }
            return container.find(traverse_selector + ':first');
        }

        function _getLastContainerTraverseElement(container) {
            var before_selector = '';
            if (opts.table_row_helper) {
                if (container.find('tbody').length > 0) {
                    before_selector = 'tbody ';
                }
            }
            return container.find(before_selector + traverse_selector + ':last');
        }

        return this;
    };
    
    $.fn.traverse.defaults = {
        jump_between: true, //jump between containers at traverse boundaries
        loop: true, // loop to the beginning/end of container boundaries
        skip_selector: null, // skip over these selectors when traversing
        table_row_helper: false, // accounts for an inconsistently placed header row - designed to be used with a selector of 'tr'
        move_scrollbar: true, //move the scroll bar to keep the 'highlight_class' visible
        highlight_class: 'focused',
        action_window_location_href: true, // browser redirect to found anchor's HREF on 'key_action'
        action_selector: ' > td > a',
        key_event: 'keydown',
        key_action: 13, // enter
        key_down: 74, // j
        key_up: 75, // k
        key_ignore_when_selector: 'input:focus, button:focus'
    };
}(jQuery));