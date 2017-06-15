yukon.namespace('yukon.favorites');
/**
 * Singleton that manages Yukon favorites feature
 * 
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.favorites = (function () {

    var 
    
    /** 
     * Returns the data for the favorite button specified.
     * @param {Object} button - jquery button object.
     */
    _dataFavoriteButton = function (button) {

        var data = { module:    button.data('module'),
                     name:      button.data('name'),
                     labelArgs: button.data('labelArgs'),
                     path:      button.data('path')
                   };

        if (typeof data.path === 'undefined') {
            data.path = window.location.pathname + window.location.search;
            if (yg.app_context_path.length > 0) {
                data.path = data.path.substring(yg.app_context_path.length);
            }
        }
        return data;
    },

    /** 
     * Returns the data for which the subscribe button is hit.
     * @param {Object} button - jquery button object.
     */
    _dataSubscribeButton = function (button) {

        var data = { subscriptionType: button.data('subscriptionType'),
                     refId:   button.data('refId')
                   };
        return data;
    },

    /** 
     * Initialize the favorite icon for a button specified
     * @param {Object} button - jquery button object.
     */
    _initializeFavoriteIcon = function (button) {

        var data = {};

        if (typeof button === 'undefined') {
            button = $('#favButton');
        }

        data = _dataFavoriteButton(button);

        $.getJSON(yukon.url('/isFavorite'), data).done(function (json) {
            _setIcon(button, json.isFavorite );
        });
    },

    /** 
     * Initialize the subscribed icon for a button specified
     * @param {Object} button - jquery button object.
     */
    _initializeSubscribedIcon = function (button) {

        var data = _dataSubscribeButton(button);

        $.getJSON(yukon.url('/isSubscribed'), data).done(function (json) {
            _setIcon(button, json.isSubscribed, 'icon-feed', 'icon-feed disabled cp');
        });
    },

    /** 
     * Initialize the favorite icon for a button specified
     * @param {Object} jQueryItem - jquery Item object.
     * @param {boolean} isOn - indicator to indicate if the icon is relevant
     * @param {string} iconOn - Name of the icon which is on
     * @param {string} iconOff - Name of the icon which is off
     */
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

    /** 
     * Add the favorite icon to the history
     */
    _addToHistory = function () {

        var button = $('#favButton'),
            data = {};

        if (button.length === 1) {
            data = _dataFavoriteButton(button);
            $.post(yukon.url('/addToHistory'), data);
        }
    },
    
    /** 
     * Toggle the favorite icon
     * @param {Object} button - jquery button object.
     * @param {string} iconOn - Name of the icon which is on
     * @param {string} iconOff - Name of the icon which is off
     */
    _toggleFavorite = function (button, iconOn, iconOff) {

        var data = _dataFavoriteButton(button);

        $.getJSON(yukon.url('/toggleFavorite'), data).done(function (json) {
            _setIcon(button, json.isFavorite, iconOn, iconOff);
        });
    },

    /** 
     * Toggle the subscribed icon
     * @param {Object} button - jquery button object.
     * @param {string} iconOn - Name of the icon which is on
     * @param {string} iconOff - Name of the icon which is off
     */
    _toggleSubscribed = function (button, iconOn, iconOff) {

        var data = _dataSubscribeButton(button);

        if (typeof iconOn === 'undefined') {
            iconOn = 'icon-feed';
            iconOff = 'icon-feed disabled cp';
        }

        $.getJSON(yukon.url('/toggleSubscribed'), data).done(function (json) {
            _setIcon(button, json.isSubscribed, iconOn, iconOff);
        });
    },

    mod = {

        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */   
        init: function() {

            var localUi = yukon.ui;

            _addToHistory();

            $('.b-favorite:not(.remove)').each( function() {
                var button = $(this);

                _initializeFavoriteIcon(button);
                button.off('click');
                button.click( function() {
                    _toggleFavorite(button);
                });
            });

            $('.b-favorite.remove').each( function() {

                var button = $(this);

                button.off('click');
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

        /** Initialize the subscribe Icon */
        initSubscribe: function() {

            var localUi = yukon.ui;

            $('.b-subscribe:not(.remove)').each( function() {
                var button = $(this);

                _initializeSubscribedIcon(button);
                button.off('click');
                button.click(function() {
                    _toggleSubscribed(button);
                });
            });

            $('.b-subscribe.remove').each( function() {
                var button = $(this);

                button.off('click');
                button.click(function() {
                    var row = button.closest('tr'),
                        actionDo = function(){
                            _toggleSubscribed(button, 'icon-feed', 'icon-feed');
                        },
                        actionUndo = actionDo,
                        name = button.data('name');

                    localUi.removeWithUndo(row, actionDo, actionUndo, name) ;
                });
            });
        }
    };
    return mod;
}());

$(function() {
    yukon.favorites.init();
});