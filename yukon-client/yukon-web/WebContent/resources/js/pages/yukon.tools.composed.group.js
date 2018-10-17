yukon.namespace('yukon.tools.composedGroup');

/**
 * This module handles behavior on the device configuration pages.
 * @module yukon.tools.composedGroup
 *
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.composedGroup = (function () {

    var
        /**
         * Sets the square bracket indexing notation for Spring binding.
         */
        _indexForSpring = function () {
            $('.js-rules-list').children('.js-rule').each(function (index) {
                $(this).find(':input[name]').each(function () {
                    var input = $(this),
                        name = input.attr('name');

                    name = name.replace(/\[(\d+|\?)\]/, '[' + index + ']');
                    input.attr('name', name);
                });
            });
        },

        mod = {

            /** Initialize the module. Depends on DOM elements so call after page load. */
            init : function () {
                $(document).on('click', '.js-add-rule', function () {
                    var template = $('.js-template').clone().removeClass('js-template');
                    $('.js-rules-list').append(template);
                    template.show();
                    _indexForSpring();
                });

                $(document).on('click', '.js-remove-rule', function () {
                    $(this).closest('.js-rule').remove();
                    _indexForSpring();
                });
            },

        };

    return mod;
}());

$(function () {yukon.tools.composedGroup.init(); });