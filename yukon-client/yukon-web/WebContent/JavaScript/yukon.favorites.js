/**
 * Singleton that manages Yukon favorites feature
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.favorites');

yukon.favorites = (function () {

    var _dataFavoriteButton = function (button) {

        var data = { module:    button.attr('data-module'),
                     name:      button.attr('data-name'),
                     labelArgs: button.attr('data-label-args'),
                     path:      button.attr('data-path')
                   };

        if (typeof data.path === 'undefined') {
            data.path = window.location.pathname + window.location.search;
        }
        return data;
    },

    _dataSubscribeButton = function (button) {

        var data = { subscriptionType: button.attr('data-subscription-type'),
                     refId:   button.attr('data-ref-id')
                   };
        return data;
    },

    _initializeFavoriteIcon = function (button) {

        var data = {};

        if (typeof button === 'undefined') {
            button = jQuery('#favButton');
        }

        data = _dataFavoriteButton(button);

        jQuery.getJSON(YG.APP_NAME + '/isFavorite', data).done(function (json) {
            _setIcon(button, json.isFavorite );
        });
    },

    _initializeSubscribedIcon = function (button) {

        var data = _dataSubscribeButton(button);

        jQuery.getJSON(YG.APP_NAME + '/isSubscribed', data).done(function (json) {
            _setIcon(button, json.isSubscribed, 'icon-feed', 'icon-feed disabled cp');
        });
    },

    _setIcon = function (jQueryItem, isOn, iconOn, iconOff) {

        var icon = jQueryItem.find('i');

        if (typeof iconOn === 'undefined') {
            iconOn = 'icon-star';
            iconOff = 'icon-favorite-not';
        }

        if (isOn) {
            icon.removeClass(iconOff).addClass(iconOn);
        } else {
            icon.removeClass(iconOn).addClass(iconOff);
        }
    },

    _addToHistory = function () {

        var button = jQuery('#favButton'),
            data = {};

        if (button.length === 1) {
            data = _dataFavoriteButton(button);
            jQuery.getJSON(YG.APP_NAME + '/addToHistory', data);
        }
    },
    
    _toggleFavorite = function (button, iconOn, iconOff) {

        var data = _dataFavoriteButton(button);

        jQuery.getJSON(YG.APP_NAME + '/toggleFavorite', data).done(function (json) {
            _setIcon(button, json.isFavorite, iconOn, iconOff);
        });
    },

    _toggleSubscribed = function (button, iconOn, iconOff) {

        var data = _dataSubscribeButton(button);

        if (typeof iconOn === 'undefined') {
            iconOn = 'icon-feed';
            iconOff = 'icon-feed disabled cp';
        }

        jQuery.getJSON(YG.APP_NAME + '/toggleSubscribed', data).done(function (json) {
            _setIcon(button, json.isSubscribed, iconOn, iconOff);
        });
    },

    mod = {

        /******************
         * Public Methods *
         ******************/

        init: function() {

            var localUi = yukon.ui;

            _addToHistory();

            jQuery('.b-favorite:not(.remove)').each( function() {
                var button = jQuery(this);

                _initializeFavoriteIcon(button);
                button.unbind('click');
                button.click( function() {
                    _toggleFavorite(button);
                });
            });

            jQuery('.b-favorite.remove').each( function() {

                var button = jQuery(this);

                button.unbind('click');
                button.click(function() {
                    var actionDo = function(){
                            _toggleFavorite(button, 'icon-star', 'icon-star');
                        },
                        actionUndo = actionDo,
                        row = button.closest('li');

                    if (row.length === 0) {
                        row = button.closest('tr');
                    }
                    localUi.removeWithUndo(row, actionDo, actionUndo);
                });
            });
        },

        initSubscribe: function() {

            var localUi = yukon.ui;

            jQuery('.b-subscribe:not(.remove)').each( function() {
                var button = jQuery(this);

                _initializeSubscribedIcon(button);
                button.unbind('click');
                button.click(function() {
                    _toggleSubscribed(button);
                });
            });

            jQuery('.b-subscribe.remove').each( function() {
                var button = jQuery(this);

                button.unbind('click');
                button.click(function() {
                    var row = button.closest('tr'),
                        actionDo = function(){
                            _toggleSubscribed(button, 'icon-feed', 'icon-feed');
                        },
                        actionUndo = actionDo,
                        name = button.attr('data-name');

                    localUi.removeWithUndo(row, actionDo, actionUndo, name) ;
                });
            });
        }
    };
    return mod;
}());

jQuery(function() {
    yukon.favorites.init();
});