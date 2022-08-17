yukon.namespace('yukon.dataUpdater');

/**
 * Module to handle updating dynamic data on all Yukon pages.
 * 
 * Elements updated are expected to have special data attributes:
 * data-updater:       The contents of the element will be replaced by the response.
 * data-class-updater: The response is a class name applied to the element.
 * data-color-updater: The response is an html color value used as the color or background-color of the element.
 * 
 * The value of these attributes is expected to be a multipart identifier parsed on the backend to decided the 
 * service used to render the response data for that particular element.  Elements using the data updater 
 * pattern are typically built by special tags:
 * cti:dataUpdaterValue
 * 
 * This module also handles callback function execution on update instead.  Functions are registered and fired as 
 * callbacks when new data is detected for their multipart identifiers.  Used by tags:
 * cti:dataUpdaterCallback
 * cti:dataUpdaterCallbackEvent
 * 
 * @module yukon.dataUpdater
 * @requires JQUERY
 * @requires yukon
 */
yukon.dataUpdater = (function () {
    
    /** @type {number} - The ms interval to timeout */
    var _updaterTimeout = null,
    
    	/** @type {number} - last update to store the latest date of update . */
        _lastUpdate = 0,
        
        /** @type {number} - failure count to track number of failures before showing message */
        _failureCount = 0,
        
        /** @type {boolean} - flag used to highlight the elements */
        _disableHighlight = false,
        
        /** @type {Object} - Tracks the registration callback calls with identifier */
        _callbackRegistrations = [],
        
        /** @type {string} - URL */
        _url = '',
        
        /** @type {number} - Delay in milliseconds */ 
        _delayMS = 4000,
        
        /**
         * Update the page elements in case anything changed
         * @param {Object} response - Response passed.
         */
        _processResponseCallback = function (response) {
        
            var someValueHasUpdated = false;
            
            // If we got here stuff is working, hide error.
            $('#data-updater-error').hide();
            _failureCount = 0;
            
            // Update all the [data-updater] elements if the data changed.
            $('[data-updater]').each(function (key, val) {
                
                var elem = $(val), 
                    newData,
                    identifier = elem.attr('data-updater');
                
                if (typeof identifier === 'undefined' || typeof response.data === 'undefined') {
                    return;
                }
                
                newData = response.data[identifier];
                
                if (typeof newData !== 'undefined') {
                    if (elem.html() !== newData) {
                        // escape html: creates a div in isolation, sets its text
                        // to whatever's in newData, then effectively escapes it 
                        // via the html function
                        newData = $('<div>').text(newData).html();
                        elem.html(newData);
                        someValueHasUpdated = true;
                        if (!_disableHighlight) {
                            elem.flash(3.5);
                        }
                    }
                }
            });
            
            // Update all the [data-class-updater] elements if the data changed.
            $('[data-class-updater]').each(function (key, val) {
                
                var elem = $(val), 
                    identifier = elem.attr('data-class-updater'),
                    newData;
                
                if (typeof identifier === 'undefined' || typeof response.data === 'undefined') {
                    return;
                }
                
                newData = response.data[identifier];
                
                if (typeof newData !== 'undefined' && newData !== elem.attr('class')) {
                    elem.attr('class', newData);
                }
            });
            
            // Update all the [data-color-updater] elements if the data changed.
            $('[data-color-updater]').each(function (key, val) {
                
                var elem = $(val), 
                    identifier = elem.attr('data-color-updater'),
                    newData,
                    format = elem.attr('data-format'),
                    property = format === 'background' ? 'background-color' : 'color',
                    current_value = format === 'background' ? elem.css('background-color') : elem.css('color');
                    
                if (typeof identifier === 'undefined' || typeof response.data === 'undefined') {
                    return;
                }
                
                newData = response.data[identifier];
                newData = 'undefined' === typeof newData ? newData : newData.toLowerCase();
                // IE represents rgba(0,0,0,0) as transparent, so translate to rgb format here
                // to satisfy rgbToHex, which expects the rgb format.
                if ('transparent' === current_value) {
                    current_value = 'rgba(0,0,0,0)';
                }
                if ('undefined' !== typeof newData && newData !== yukon.ui.util.rgbToHex(current_value)) {
                    // Data was sent and is different than current
                    elem.css(property, newData);
                    elem.children().css(property, newData);
                }
            });

            _callbackRegistrations.forEach(function (it, index, ar) {
                var idMap = it.identifierMap,
                    allIdentifierValues = {},
                    gotNewData = false,
                    isEmpty = function (obj) {
                        for(var prop in obj) {
                            if(obj.hasOwnProperty(prop))
                                return false;
                        }
                        return true;
                    };
                if (('undefined' === typeof idMap || isEmpty(idMap)) && someValueHasUpdated) {
                    (it.callback)();
                    return;
                }
                $.each(idMap, function (key, val) {
                    var newData;
                    if ('undefined' !== typeof response.data) {
                        newData = response.data[idMap[key]];
                        if ('undefined' !== typeof newData) {
                            gotNewData = true;
                            allIdentifierValues[key] = newData;
                        }
                    }
                });
                if (true === gotNewData) {
                    (it.callback)(allIdentifierValues);
                }
            });

            // save latest date
            _lastUpdate = response.toDate;
            // schedule next update
            if (_updaterTimeout) {
                clearTimeout(_updaterTimeout);
            }
            _updaterTimeout = setTimeout(_doUpdate, _delayMS);
        },
        
        /**
         * Manage failure to update the page elements
         */
        _failureCallback = function () {
            // something bad happened, show user that updates are off
            _failureCount++;
            $('#data-updater-error-count').html(_failureCount);
            if(_failureCount > 1) {
                // wait for 2 errors before we show error message
                // to avoid flashing error message on page transitions.
                $('#data-updater-error').show();
            }

            // schedule another update in case the server comes back, but slow it down a bit
            if (_updaterTimeout) {
                clearTimeout(_updaterTimeout);
            }
            _updaterTimeout = setTimeout(_doUpdate, _delayMS * 5);
        },
        
        /**
         * Show warning dialog
         */
        _warnStaleData = function () {
            $('#updatedWarning').dialog('open');
        },
        
        /**
         * Update the page
         */
        _doUpdate = function () {
            // if none exist on this page, get out
            // build up JS object to be used for request
            var reqData = {
                    fromDate : _lastUpdate,
                    requestTokens : []
                },
                getUpdater = function (updateName) {
                    var fullName = 'data' + updateName + 'updater',
                        updateElems = $('[' + fullName + ']'),
                        updateData = $.makeArray(updateElems).map(function (al) {
                            return $(al).attr(fullName);
                        });
                    return updateData;
                };

            $.each([ '-', '-class-', '-color-' ], function (index, updateStr) {
                var addedData = getUpdater(updateStr);
                reqData.requestTokens = reqData.requestTokens.concat(addedData);
            });
            
            _callbackRegistrations.forEach(function (it, index, ar) {
                var idMap = it.identifierMap;
                $.each(idMap, function (key, val) {
                    reqData.requestTokens = reqData.requestTokens.concat(val);
                });
            });

            if (0 === reqData.requestTokens.length) {
                // schedule next update
                if (_updaterTimeout) {
                    clearTimeout(_updaterTimeout);
                }
                _updaterTimeout = setTimeout(_doUpdate, _delayMS);
                return;
            }
            
            $.ajax({
                url: _url,
                type: 'POST',
                data: JSON.stringify(reqData),
                contentType: 'application/json; charset=utf-8',
                dataType: "json"
            }).done(function (data, textStatus, xhr) {
                _processResponseCallback(data);
            }).fail(function (xhr, textStatus, errorThrown) {
                // Since we're asking for json data, if we get a 200 status, but its not json this will 
                // result in a parse error and call fail(), not done()
                if (xhr.status === 409) {
                    _warnStaleData();
                }
                if(!xhr.status == 500){
                    _failureCallback();
                }
                
            });
            reqData.requestTokens = [];
        };
    
    mod = {
        
        /**
         * Register a callback function to fire for the provided identifiers.
         * @param {function} callback - Function to fire on update.
         * @param {Object} identifierMap - Identifier Map
         */
        registerCallback: function (callback, identifierMap) {
            _callbackRegistrations.push({
                'identifierMap': identifierMap,
                'callback': callback
            });
        },
        
        /**
         * Register a callback that will only fire if the response contains a 'boolean' property 
         * whose value is 'true' or true. Once fired the callback will never be fired again.
         * @param {function} callback - Function to fire on update.
         * @param {DOM id} identifier - Identifier Id
         */
        registerEventCallback: function (callback, identifier) {
            var didIt = false,
                callbackWrapper = function (data) {
                    if (!didIt && (data.boolean === true || data.boolean === 'true')) {
                        didIt = true;
                        callback();
                    }
                };
            mod.registerCallback(callbackWrapper, { 'boolean': identifier });
        },
        
        /**
         * Schedules the first update request after waiting 'delay' milliseconds.
         * The first update recursively reschedules consecutive updates from then on.
         * @param {string} url - url
         * @param {number} delay - delay time
         */
        start: function (url, delay) {
            _url = url;
            _delayMS = delay;
            if (_updaterTimeout) {
                clearTimeout(_updaterTimeout);
            }
            _updaterTimeout = setTimeout(_doUpdate, _delayMS);
        }
    };
    
    return mod;
})();