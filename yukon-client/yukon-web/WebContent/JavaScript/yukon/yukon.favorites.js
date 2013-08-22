var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Favorites');
Yukon.Favorites = (function () {

    /*******************
     * Private Methods *
     *******************/

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

        var data = { name:        button.attr('data-name'),
                     monitorType: button.attr('data-monitorType'),
                     monitorId:   button.attr('data-monitorId')
                   };
        return data;
    },

    _initializeFavoriteIcon = function (button) {

        var data = {};

        if (typeof button === 'undefined') {
            button = jQuery('#favButton');
        }

        data = _dataFavoriteButton(button);

        jQuery.getJSON( '/isFavorite', data).done( function (json) {
            _setIcon(button, json.isFavorite );
        });
    },

    _initializeSubscribedIcon = function (button) {

        var data = _dataSubscribeButton(button);

        jQuery.getJSON( '/isSubscribed', data).done( function (json) {
            _setIcon(button, json.isSubscribed, 'icon-feed', 'icon-feed disabled cp');
        });
    },

    _setIcon = function (jQueryItem, isFavorite, iconOn, iconOff) {

        var icon = jQueryItem.find('i');

        if (typeof iconOn === 'undefined') {
            iconOn = 'icon-star';
            iconOff = 'icon-favorite-not';
        }

        if (isFavorite) {
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
            jQuery.getJSON( '/addToHistory', data);
        }
    },

    favoriteMod = {

        /******************
         * Public Methods *
         ******************/

        init: function() {

            var localUi = Yukon.ui;

            _addToHistory();

            jQuery('.b-favorite:not(.remove)').each( function() {
                var button = jQuery(this);

                _initializeFavoriteIcon(button);

                button.unbind('click');
                button.click( function() {
                    favoriteMod.toggleFavorite(button);
                });
            });

            jQuery('.b-favorite.remove').each( function() {
                var button = jQuery(this);

                button.unbind('click');
                button.click(function() {
                    var row = button.closest('li'),
                    actionDo = function(){
                        favoriteMod.toggleFavorite(button, 'icon-star', 'icon-star');
                    },
                    actionUndo = actionDo,
                    header = button.attr('data-header');

                    header = header.replace( /</g, '&lt').replace( />/g, '&gt');

                    localUi.removeWithUndo(row, actionDo, actionUndo, header);
                });
            });
        },

        initSubscribe: function() {
            var localUi = Yukon.ui;

            jQuery('.b-subscribe:not(.remove)').each( function() {
                var button = jQuery(this);

                _initializeSubscribedIcon(button);
                button.unbind('click');
                button.click(function() {
                    favoriteMod.toggleSubscribed(button);
                });
            });

            jQuery('.b-subscribe.remove').each( function() {

                var button = jQuery(this);

                button.unbind('click');
                button.click(function() {
                    var row = button.closest('tr'),
                    actionDo = function(){
                        favoriteMod.toggleSubscribed(button, 'icon-feed', 'icon-feed');
                    },
                    actionUndo = actionDo,
                    name = button.attr('data-name');

                    localUi.removeWithUndo(row, actionDo, actionUndo, name) ;
                });
            });
        },

        toggleFavorite : function (button, iconOn, iconOff) {

            var data = _dataFavoriteButton(button);

            jQuery.getJSON( '/toggleFavorite', data).done( function (json) {
                _setIcon(button, json.isFavorite, iconOn, iconOff);
            });
        },

        toggleSubscribed: function (button, iconOn, iconOff) {

            var data = _dataSubscribeButton(button);

            if (typeof iconOn === 'undefined') {
                iconOn = 'icon-feed';
                iconOff = 'icon-feed disabled cp';
            }

            jQuery.getJSON( '/toggleSubscribed', data).done( function (json) {
                _setIcon(button, json.isSubscribed, iconOn, iconOff);
            });
        }
    };

    return favoriteMod;
}());

jQuery(function() {
    Yukon.Favorites.init();
});