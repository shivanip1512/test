/* see the dataUpdateEnabler.tag to see where this is referenced */
var disableHighlight = false;
var cannonDataUpdateRegistrations = [];
var _updaterTimeout = null;

function initiateCannonDataUpdate(url, delayMs) {
    var lastUpdate = 0,
        failureCount = 0;
    
    function processResponseCallback(transport) {
        var someValueHasUpdated = false,
            responseStruc,
            updateElems,
            updateClassElems,
            updateColorElems;

        responseStruc = transport;
        // looks like stuff is working, hide error div
        jQuery('#data-updater-error').hide();
        failureCount = 0;
        
        // find all of the updateable elements
        updateElems = jQuery('[data-updater]');
        jQuery.each (updateElems, function(key, val) {
            var newData,
                attVal = jQuery(val).attr('data-updater');
            if ('undefined' === typeof attVal || 'undefined' === typeof responseStruc.data) {
                return;
            }
            newData = responseStruc.data[attVal];
            if ('undefined' !== typeof newData) {
                if (jQuery(val).html() !== newData) {
                    // escape html: creates a div in isolation, sets its text
                    // to whatever's in newData, then effectively escapes it 
                    // via the html function
                    newData = jQuery('<div>').text(newData).html();
                    jQuery(val).html(newData);
                    someValueHasUpdated = true;
                    if (!disableHighlight) {
                        jQuery(val).flashYellow(3.5);
                    }
                }
            }
        });

        // update the classes
        updateClassElems = jQuery('[data-class-updater]');
        jQuery.each (updateClassElems, function(key, val) {
            var id = jQuery(val).attr('data-class-updater'),
                newData,
                className;
            if ('undefined' === typeof id || 'undefined' === typeof responseStruc.data) {
                return;
            }
            newData = responseStruc.data[id];
            className = jQuery(val).attr('class');
            if ('undefined' !== typeof newData && className !== newData) {
                jQuery(val).attr('class', newData);
            }
        });

        // update the colors
        updateColorElems = jQuery('[data-color-updater]');
        jQuery.each (updateColorElems, function(key, val) {
            var id = jQuery(val).attr('data-color-updater'),
                newData,
                format, // what are typical/legal values for this?
                backgroundColor,
                color,
                current_value,
                // jquery (and plain old javascript) returns color values in rgb format:
                //   rgb(0, 153, 51)
                // but we need:
                //   #009933
                rgb2hex = function(rgb) {
                    var compositeRgb,
                        hex = function(x) {
                            return ('0' + parseInt(x, 10).toString(16)).slice(-2);
                        };
                    if ('undefined' === typeof rgb || '' === rgb || null === rgb) {
                        return '#000000';
                    }
                    
                    // IE8 returns color in hex
                    if (rgb.match(/^#[\da-f]{6}$/)) {
                        return rgb;
                    }
                    
                    rgb = rgb.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+))?\)$/);
                    compositeRgb = hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
                    compositeRgb = compositeRgb.toLowerCase();
                    return '#' + compositeRgb;
                };
            if ('undefined' === typeof id || 'undefined' === typeof responseStruc.data) {
                return;
            }
            newData = responseStruc.data[id];
            newData = 'undefined' === typeof newData ? newData : newData.toLowerCase();
            format = jQuery(val).attr('data-format');
            backgroundColor = jQuery(val).css('background-color');
            color = jQuery(val).css('color');
            current_value = format == 'background' ? backgroundColor : color;
            // IE represents rgb(0,0,0,0) as transparent, so translate to rgb format here
            // to satisfy rgb2hex, which expects the rgb format
            if ('transparent' === current_value) {
                current_value = 'rgb(0,0,0,0)';
            }
            if ('undefined' !== typeof newData && newData !== rgb2hex(current_value)) {
                // data was sent and is different than current
                if (format === 'background') {
                    jQuery(val).css({'background-color': newData});
                    jQuery(val).children().css({'background-color': newData});
                } else {
                    jQuery(val).css({'color': newData});
                    jQuery(val).children().css({'color': newData});
                }
            }
        });

        cannonDataUpdateRegistrations.forEach(function(it, index, ar) {
            var idMap = it.identifierMap,
                allIdentifierValues = {},
                gotNewData = false;
            if ('undefined' === typeof idMap && someValueHasUpdated) {
                (it.callback)();
                return;
            }
            jQuery.each (idMap, function(key, val) {
                var newData;
                if ('undefined' !== typeof responseStruc.data) {
                    newData = responseStruc.data[idMap[key]];
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
        lastUpdate = responseStruc.toDate;
        // schedule next update
        if (_updaterTimeout) {
            clearTimeout(_updaterTimeout);
        }
        _updaterTimeout = setTimeout(doUpdate, delayMs);
    } // end of processResponseCallback
    
    var failureCallback = function() {
        // something bad happened, show user that updates are off
        failureCount++;
        jQuery('#data-updater-error-count').html(failureCount);
        if(failureCount > 1) {
            // wait for 2 errors before we show error message
            // to avoid flashing error message on page transitions.
            jQuery('#data-updater-error').show();
        }

        // schedule another update in case the server comes back, but slow it down a bit
        if (_updaterTimeout) {
            clearTimeout(_updaterTimeout);
        }
        _updaterTimeout = setTimeout(doUpdate, delayMs * 5);
    };
    
    var warnStaleData = function() {
        jQuery('#updatedWarning').dialog('open');
    };
    
    function doUpdate() {
        // if none exist on this page, get out
        // build up JS object to be used for request
        var reqData = {
                'fromDate': lastUpdate,
                'data' : []
            },
            updaters = [
                '-',
                '-class-',
                '-color-'
            ],
            getUpdater = function(updateName) {
                var fullName = 'data' + updateName + 'updater',
                    updateElems = jQuery('[' + fullName + ']'),
                    updateData = jQuery.makeArray(updateElems).map(function(al) {
                        return jQuery(al).attr(fullName);
                    });
                return updateData;
            },
            json_reqData;

        jQuery.each (updaters, function(index, updateStr) {
            var addedData = getUpdater(updateStr);
            reqData.data = reqData.data.concat(addedData);
        });
        cannonDataUpdateRegistrations.forEach(function(it, index, ar) {
            var idMap = it.identifierMap;
            jQuery.each (idMap, function(key, val) {
                reqData.data = reqData.data.concat(val);
            });
        });

        if (0 === reqData.data.length) {
            // schedule next update
            if (_updaterTimeout) {
                clearTimeout(_updaterTimeout);
            }
            _updaterTimeout = setTimeout(doUpdate, delayMs);
            return;
        }
        json_reqData = JSON.stringify(reqData);
        jQuery.ajax({
            url: url,
            type: 'POST',
            data: json_reqData,
            contentType: 'application/json; charset=utf-8',
            dataType: "json"
        }).done(function(data, textStatus, xhr) {
            processResponseCallback(data);
        }).fail(function(xhr, textStatus, errorThrown) {
            // Since we're asking for json data, if we get a 200 status, but its not json this will 
            // result in a parse error and call fail(), not done()
            if (xhr.status === 409) {
                warnStaleData();
            }
            failureCallback();
        });
        reqData.data = [];
    }
    if (_updaterTimeout) {
        clearTimeout(_updaterTimeout);
    }
    _updaterTimeout = setTimeout(doUpdate, delayMs);
}

/**
 * @param    callback        {function}
 * @param    identifierMap    {Object} JSON
 */
function cannonDataUpdateRegistration(callback, identifierMap) {
    // callback will include the formatted string as its one argument

    cannonDataUpdateRegistrations.push({
        'identifierMap': identifierMap,
        'callback': callback
    });
}

/**
 * 
 * @param callback        {function}
 * @param identifier    {DOM id}
 */
function cannonDataUpdateEventRegistration(callback, identifier) {
    var didIt = false,
        callbackWrapper = function(data) {
            // previously, the comparison was data.boolean == true, which
            // worked because of type coercion. With ===, we have to be explicit
            if (!didIt && (data.boolean === true || 'true' === data.boolean)) {
                didIt = true;
                callback();
            }
        };
    cannonDataUpdateRegistration(callbackWrapper, {'boolean': identifier});  
}

