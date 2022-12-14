yukon.namespace('yukon.tools.map');

/**
 * Singleton that manages the device collection mapping feature.
 * 
 * @module yukon.tools.map
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.tools.map = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _violationColor = yg.colors.ORANGE,
    
    //grey
    _focusRouteColor = yukon.mapping.getFocusRouteColor(),
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _iconLayerIndex = yukon.mapping.getIconLayerZIndex(),
    
    /** @type {number} - The setTimeout reference for periodic updating of device collection. */
    _updater = -1, 
    
    /** @type {number} - The ms interval to wait before updating the device collection. */
    _updateInterval = 4000,
    
    _destProjection = yukon.mapping.getDestProjection(),
    _srcProjection = yukon.mapping.getSrcProjection(),
    
    /** @type {Object.<number, {ol.Feature}>} - Map of pao id to feature for all device icons. */
    _icons = {}, 
    
    /** @type {Object.<number, boolean>} - Map of pao id to boolean to keep track of device icon visibility. */
    _visibility = {},
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {}, 
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    /** Hashmap of colors and paoIds for tinting the icons */
    _mappingColors,
    
    /** 
     * Creates an {ol.Feature} icon from the geojson feature object.
     * @param {Object} feature - the geojson feature to convert.
     * @param {string} src_projection - The projection system of the tile layer.
     * @returns {ol.Feature} icon - The openlayers feature icon.
     */
    _createFeature = function(feature, src_projection) {
        var pao = feature.properties.paoIdentifier,
            icon = new ol.Feature({pao: pao}),
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
        
        icon.setId(feature.id);
        icon.setStyle(style);
        var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
        icon.setGeometry(new ol.geom.Point(coord));
                
        _icons[pao.paoId] = icon;
        _visibility[pao.paoId] = true;
        
        return icon;
    },
    
    _getLocationUrl = function() {
        var monitorId = $('#monitorId').val(),
            violationsOnly = $('#violationsSelect').val(),
            locationsUrl = $('#locations').val();
        if (monitorId) {
            locationsUrl = $('#monitorLocations').val() + "?violationsOnly=" + violationsOnly;
        }
        return locationsUrl;
    },
    
    _updateStyleIfInViolation = function(data, pao, icon) {
        //check if it's in violation
        var monitorId = $('#monitorId').val();
        if (monitorId) {
            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc(),
                scale = image.getScale();
            var inViolation = data.violationDevices.filter(function (device) {
                return device.deviceId === pao.paoId;
            });
            if (inViolation.length > 0) {
                currentStyle.setImage(new ol.style.Icon({ src: src, color: _violationColor, scale: scale, anchor: yukon.mapping.getAnchor() }));
                icon.setStyle(currentStyle);
            } else {
                //return back to original color
                currentStyle.setImage(new ol.style.Icon({ src: src, scale: scale, anchor: yukon.mapping.getAnchor() }));
                icon.setStyle(currentStyle);
            }
        }
    },
    
    _updateStyleIfFoundInMappingColors = function(paoId, icon) {
        if (_mappingColors) {
            var color;
            Object.keys(_mappingColors).map(function (key) {
                var ids = _mappingColors[key];
                if ($.inArray(parseInt(paoId, 10), ids) > -1) {
                    color = key;
                }
            });
            if (color) {
                var currentStyle = icon.getStyle().clone(),
                    image = currentStyle.getImage(),
                    src = image.getSrc(),
                    scale = image.getScale();
                currentStyle.setImage(new ol.style.Icon({ src: src, color: color, scale: scale, anchor: yukon.mapping.getAnchor() }));
                icon.setStyle(currentStyle);
            }
        }
    },
    
    /** 
     * Gets pao locations for the device collection in geojson format and adds
     * an icon feature for each to a new vector layer for the map.
     */
    _loadIcons = function() {
        $('.js-status-loading').show();
        var monitorId = $('#monitorId').val();
        $.getJSON(decodeURI(_getLocationUrl())).done(function(data) {
            
            var icon, 
                icons = [],
                source = yukon.mapping.getIconLayerSource(),
                fc = monitorId ? data.locations : data,
                mappingData = $('#mappingColorJson');
                        
            if (mappingData.exists()) {
                _mappingColors = yukon.fromJson(mappingData);
            }
            
            debug.time('Loading Icons');
            
            for (var i in fc.features) {
                var feature = fc.features[i],
                    pao = feature.properties.paoIdentifier;
                icon = _createFeature(fc.features[i], fc.crs.properties.name);
                _updateStyleIfInViolation(data, pao, icon);
                _updateStyleIfFoundInMappingColors(pao.paoId, icon);
                icons.push(icon);
            }
            source.addFeatures(icons);
            
            yukon.mapping.updateZoom(_map);

            $('.js-status-loading').hide();
            debug.timeEnd('Loading Icons');
            
            if ($('#map').is('[data-dynamic]')) {
                _update();
            }
        });
    },
    
    /** Method to update device list with recursive setTimeout. */
    _update = function(once) {
        var monitorId = $('#monitorId').val();
        $.getJSON(decodeURI(_getLocationUrl())).done(function(data) {
            
            var toAdd = [], toRemove = [], icons = {}, 
                i, pao, feature, diff,
                source = yukon.mapping.getIconLayerSource(),
                fc = monitorId ? data.locations : data;
                        
            // add any features we don't have
            for (i = 0; i < fc.features.length; i++) {
                feature = fc.features[i],
                pao = feature.properties.paoIdentifier;
                
                icons[pao.paoId] = feature;
                
                if (typeof _icons[pao.paoId] === 'undefined') {
                    var
                    icon = _createFeature(feature, fc.crs.properties.name);
                    _updateStyleIfInViolation(data, pao, icon);
                    toAdd.push(icon);
                } else {
                    var icon = _icons[pao.paoId];
                    _updateStyleIfInViolation(data, pao, icon);
                }
            }
            source.addFeatures(toAdd);
            debug.log('added ' + toAdd.length + ' features');
            
            // remove any features we don't want
            for (i in _icons) {
                feature = _icons[i];
                if (typeof icons[feature.get('pao').paoId] === 'undefined') {
                    toRemove.push(feature);
                }
            }
            for (i = 0; i < toRemove.length; i++) {
                feature = toRemove[i];
                source.removeFeature(feature);
                delete _icons[feature.get('pao').paoId];
                delete _visibility[feature.get('pao').paoId];
            }
            debug.log('removed ' + toRemove.length + ' features');
            
            diff = toAdd.length - toRemove.length;
            if (monitorId) {
                //update violations
                var violationsBadge = $('#violation-collection .js-violations');
                var currentViolations = parseInt(violationsBadge.text(), 10);
                if (currentViolations !== data.violationDevices.length) {
                    violationsBadge.text(data.violationDevices.length);
                    violationsBadge.addClass('animate__animated animate__flash');
                }
            }
            else if (diff !== 0) {
                var count = parseInt($('#device-collection .js-count').text(), 10);
                $('#device-collection .js-count').text(count + diff);
                $('#device-collection .js-count').addClass('animate__animated animate__flash');
            }
            
            //update primary routes if changed
            if (toAdd.length > 0 || toRemove.length > 0 || once) {
                _addAllPrimaryRoutes();  
            }
            
        }).fail(function(xhr, status, error) {
            debug.log('update failed:' + status + ': ' + error);
        }).always(function() {
            if (!once) {
                _updater = setTimeout(_update, _updateInterval);
            }
        });
    },
    
    _removeDeviceFocusLayers = function() {
        var source = yukon.mapping.getIconLayerSource();
        _deviceFocusIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _deviceFocusLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _deviceFocusIcons = [];
        _deviceFocusLines = [];
        //set focus device back to normal style
        if (_deviceFocusCurrentIcon != null) {
            yukon.mapping.setScaleForDevice(_deviceFocusCurrentIcon);
        }
        yukon.mapping.hideNeighborsLegend();
        yukon.mapping.removeDescendantLayers();
    },
    
    //remove neighbors and route information from all icons and set back to initial scale
    _setIconsBack = function() {
        for (var i in _icons) {
            var icon = _icons[i];
            icon.unset("neighbor");
        }
        if (_deviceFocusCurrentIcon != null) {
            yukon.mapping.setScaleForDevice(_deviceFocusCurrentIcon);
        }
    },
    
    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var source = yukon.mapping.getIconLayerSource(),
            focusDevice = yukon.mapping.findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            dashedLine = false,
            routeColor = _focusRouteColor,
            routeLineWidth = 2.5;

        var primaryRoutePreviousPoints = null;
        _removeDeviceFocusLayers();
        _setIconsBack();
        _deviceFocusCurrentIcon = focusDevice;
        
        //if focus device was removed, add it back
        var deviceFound = yukon.mapping.findFocusDevice(deviceId, false);
        if (deviceFound == null) {
            source.addFeature(focusDevice);
            _deviceFocusIcons.push(focusDevice);
        }

        for (var x in routeInfo) {
            var route = routeInfo[x],
                feature = yukon.mapping.getFeatureFromRouteOrNeighborData(route);
            
            if (feature == null) {
                dashedLine = true;
                $('.js-no-location-message').removeClass('dn');
            } else {
                var pao = feature.properties.paoIdentifier,
                    style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                    icon = new ol.Feature({ pao: pao });
            
                icon.setId(feature.id);
                icon.setStyle(style);
                
                //check if device already exists on map
                var deviceFound = yukon.mapping.findFocusDevice(pao.paoId, false);
                if (deviceFound) {
                    icon = deviceFound;
                    icon.unset("neighbor");
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                    _deviceFocusIcons.push(icon);
                    source.addFeature(icon);
                }
                
                //draw line
                var points = [];
                points.push(icon.getGeometry().getCoordinates());
                if (primaryRoutePreviousPoints != null) {
                    points.push(primaryRoutePreviousPoints);
                } else {
                    points.push(focusPoints);
                }
                primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
                
                var layerLines = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: [new ol.Feature({
                            geometry: new ol.geom.LineString(points),
                            name: 'Line'
                        })]
                    }),
                    style: new ol.style.Style({
                        stroke: new ol.style.Stroke({ 
                            color: routeColor, 
                            width: routeLineWidth,
                            lineDash: dashedLine ? [10,10] : null
                        })
                    })
                });
                
                layerLines.setZIndex(_primaryRouteLayerIndex);
                _deviceFocusLines.push(layerLines);
                _map.addLayer(layerLines);
                dashedLine = false;
            }

        }
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var source = yukon.mapping.getIconLayerSource(),
            focusDevice = yukon.mapping.findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            clonedFocusDevice = focusDevice.clone();
            
        clonedFocusDevice.setStyle(focusDevice.getStyle().clone());
        clonedFocusDevice.unset("neighbor");
            
        _removeDeviceFocusLayers();
        _setIconsBack();
        var focusDeviceStillOnMap = yukon.mapping.findFocusDevice(deviceId, true);
        if (!focusDeviceStillOnMap) {
            _deviceFocusIcons.push(clonedFocusDevice);
            source.addFeature(clonedFocusDevice);
        }

        _deviceFocusCurrentIcon = focusDevice;

        for (var x in neighbors) {
            var device = neighbors[x];
            yukon.mapping.createNeighborDevice(device, _deviceFocusIcons, _deviceFocusLines, focusPoints, false);
        }
    },
    
    _addAllPrimaryRoutes = function() {
        var checked = $('.js-all-routes-collection').find(':checkbox').prop('checked');
        if (checked) {
            var getGatewaysUrl = $('#getGatewaysUrl').val();
            $.getJSON(getGatewaysUrl)
            .done(function (gatewayIds) {
                yukon.mapping.showHideAllRoutes(gatewayIds);
            });
        } else {
            yukon.mapping.showHideAllRoutes();
        }  
    },
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen({source: 'map-container'}),
                    new ol.control.ScaleLine({units: 'us', target: 'scale-line'}), 
                    new ol.control.Zoom(), 
                    new ol.control.MousePosition({
                        coordinateFormat: ol.coordinate.createStringXY(6),
                        projection: _srcProjection,
                        target: 'mouse-position',
                        undefinedHTML: '&nbsp;'
                    })
                ],
                layers: _tiles,
                target: 'map',
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], _srcProjection, _destProjection), zoom: 4 })
            });
            yukon.mapping.initializeMap(_map);
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', zIndex: _iconLayerIndex, source: new ol.source.Vector({ projection: _destProjection }) }));
            _loadIcons();
            /** Hide any cog dropdowns on zoom or map move **/
            _map.getView().on('change:resolution', function(ev) {
               $('.dropdown-menu').css('display', 'none');
            });
            _map.on('movestart', function(ev) {
                $('.dropdown-menu').css('display', 'none');
             });
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var paoFeature = _map.forEachFeatureAtPixel(ev.pixel, function(feature) {
                    if (feature && feature.get('pao') != null) {
                        return feature;
                    }
                });
                if (paoFeature) {
                    yukon.mapping.displayMappingPopup(paoFeature, _overlay);
                } else {
                    var target = ev.originalEvent.target;
                    //check if user clicked on the cog, the error hide-reveal, or notes icon
                    var cog = $(target).closest('.js-cog-menu'),
                        error = $(target).closest('.hide-reveal-container'),
                        notesIcon = $(target).closest('.js-view-all-notes');
                    if (!cog.exists() && !error.exists() && !notesIcon.exists()) {
                        $('#marker-info').hide();
                    }
                }
            });
            
            $('#violationsSelect').on('change', function(ev) {
                yukon.mapping.removeAllRoutesLayers();
                _update(true);
            });
            
            $('#devicesFilter').on('change', function(ev) {
                $(this).trigger('yukon.map.filter');
            });
            
            /** Initilize the attribute select and handle change events. */
            $('#attribute-select').chosen({width: '100%'}).on('change', function(ev) {
                $('#filter-states').empty();
                $('#waiting-for-states').show();
                $('#no-states-for-attribute').hide();
                if ($('#attribute-select').val() !== '-1') {
                    $.getJSON(decodeURI($('#state-group-base-url').val()) + '&attribute=' + $('#attribute-select').val())
                    .done(function(groups) {
                        var group, state, row, select;
                        
                        $('#waiting-for-states').hide();
                        $('#no-states-for-attribute').toggle(groups.length === 0);
                        
                        for (var i in groups) {
                            group = groups[i];
                            row = $('#state-group-template').clone().removeAttr('id');
                            row.find('input:hidden').val(group.stateGroupID).attr('name', 'groups[' + i + '].id');
                            select = row.find('select').attr('name', 'groups[' + i + '].state');
                            select.attr('id', 'state-select');
                            for (var ii in group.statesList) {
                                state = group.statesList[ii];
                                select.append('<option value="' + state.liteID + '">' + state.stateText + '</option>');
                            }
                            $('#filter-states').append(row);
                            row.removeClass('dn');
                        }
                    });
                }
            });
            
            /** Submit the filtering form when 'yukon.map.filter' event is fired, then process results. */
            $(document).on('yukon.map.filter', function(ev) {
                
                $('#map-popup').dialog('close');
                $('#no-filter-btn').show();
                $('.js-status-retrieving').show();
                $('#filter-btn').addClass('left');
                $('#filter-btn .b-label').text($('#filtered-msg').val() 
                        + ' ' + $('#attribute-select option:selected').text() + ' - ' + $('#state-select').find('option:selected').text());
                
                var start = new Date().getTime();
                
                $('#filter-form').ajaxSubmit({
                    dataType: 'json',
                    success: function(json) {
                        var results = json.results,
                            filteredCount = json.filteredDeviceCount,
                            filteredDevicesOnly = $('#devicesFilter').val();
                        
                        yukon.mapping.removeAllRoutesLayers();
                        
                        $('.js-status-retrieving').hide();
                        $('.js-status-filtering').show();
                        
                        debug.log('point data request: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        var source = yukon.mapping.getIconLayerSource(),
                            toAdd = [], toRemove = [],
                            visible, show, paoId, icon;
                        
                        for (paoId in results) {
                            icon = _icons[paoId];
                            if (icon) { // Ignore any paos we aren't tracking. i.e. They don't have a location.
                                show = results[paoId];
                                visible = _visibility[paoId];
                                var currentStyle = icon.getStyle().clone(),
                                    image = currentStyle.getImage(),
                                    src = image.getSrc(),
                                    scale = image.getScale();
                                if (show) {
                                    currentStyle.setImage(new ol.style.Icon({ src: src, color: _violationColor, scale: scale, anchor: yukon.mapping.getAnchor() }));
                                    icon.setStyle(currentStyle);
                                } else {
                                    currentStyle.setImage(new ol.style.Icon({ src: src, scale: scale, anchor: yukon.mapping.getAnchor() }));
                                    icon.setStyle(currentStyle);
                                    _updateStyleIfFoundInMappingColors(paoId, icon);
                                }
                                if (show && !visible || (filteredDevicesOnly == "false" && !visible)) {
                                    toAdd.push(icon);
                                    _visibility[paoId] = true;
                                } else if (!show && visible) {
                                    if (filteredDevicesOnly == "true") {
                                        toRemove.push(icon);
                                        _visibility[paoId] = false;
                                    }
                                }
                            }
                        }
                        
                        //update filtered count
                        var filteredBadge = $('#filtered-collection .js-filtered');
                        var currentFiltered = parseInt(filteredBadge.text(), 10);
                        if (currentFiltered !== filteredCount) {
                            filteredBadge.text(filteredCount);
                            filteredBadge.addClass('animate__animated animate__flash');
                        }
                        
                        $('.js-filtered-devices').removeClass('dn');
                        $('.js-color-collections').addClass('dn');
                        
                        debug.log('building add/remove arrays: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        source.addFeatures(toAdd);
                        
                        debug.log('adding icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        var toRemoveLength = toRemove.length;
                        for (var i = 0; i < toRemoveLength; i++) {
                            source.removeFeature(toRemove[i]);
                        }
                        
                        debug.log('removing icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        $('.js-status-filtering').hide();
                        
                        //update primary routes if changed
                        _addAllPrimaryRoutes();
                    }, 
                    error: function(xhr, status, error, $form) {
                        debug.log('error with ajax filter form submission: ' + error);
                        $('.js-status-retrieving').hide();
                    }
                });
                
            });
            
            /** Remove filtering when no filter button clicked. */
            $('#no-filter-btn').click(function(ev) {
                $('#no-filter-btn').hide();
                $('#filter-btn').removeClass('left');
                $('#filter-btn .b-label').text($('#unfiltered-msg').val());
                
                yukon.mapping.removeAllRoutesLayers();
                
                var toAdd = [], 
                    start = new Date().getTime(),
                    source = yukon.mapping.getIconLayerSource();

                for (var paoId in _visibility) {
                    var icon = _icons[paoId],
                        currentStyle = icon.getStyle().clone(),
                        image = currentStyle.getImage(),
                        src = image.getSrc(),
                        scale = image.getScale();
                    currentStyle.setImage(new ol.style.Icon({ src: src, scale: scale, anchor: yukon.mapping.getAnchor() }));
                    icon.setStyle(currentStyle);
                    _updateStyleIfFoundInMappingColors(paoId, icon);
                    if (!_visibility[paoId]) {
                        toAdd.push(icon);
                        _visibility[paoId] = true;
                    }
                }
                source.addFeatures(toAdd);
                
                //update filtered count
                var filteredBadge = $('#filtered-collection .js-filtered');
                filteredBadge.text(0);
                filteredBadge.addClass('animate__animated animate__flash');
                $('.js-filtered-devices').addClass('dn');
                $('.js-color-collections').removeClass('dn');

                debug.log('removing icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                start = new Date().getTime();
                
                //update primary routes if changed
                _addAllPrimaryRoutes();  
            });
            
            /** Change mouse cursor when over marker.  There HAS to be a css way to do this! */
            $(_map.getViewport()).on('mousemove', function(e) {
                var pixel = _map.getEventPixel(e.originalEvent),
                    paoFeature = _map.forEachFeatureAtPixel(pixel, function(feature) {
                        if (feature && feature.get('pao') != null) {
                            return feature;
                        }
                    });
                $('#' + _map.getTarget()).css('cursor', paoFeature ? 'pointer' : 'default');
            });
            
            /** Remove the coordinates for the device when the user clicks OK on the confirmation popup. **/
            $(document).on('yukon:tools:map:delete-coordinates', function(event) {
                var paoId = $('#remove-pin').data("device-id"),
                    removeUrl = yukon.url('/tools/map/device/' + paoId);
                
                $.ajax({
                    url: removeUrl,
                    type: 'POST',
                    data: {_method: 'DELETE'}, //Spring request type hackery
                    success: function(results) {
                        $('#confirm-delete').dialog('destroy');
                        yukon.ui.removeAlerts();
                        _update(true); //refresh map
                    },
                    error: function(xhr, status, error) {
                        var errorMsg = xhr.responseJSON.message;
                        yukon.ui.alertError(errorMsg);
                    }
                });
                
            });
            
            /** Destroy the coordinate deletion confirmation popup when cancelled **/
            $(document).on('click', '.cancel-delete', function(event) {
                $('#confirm-delete').dialog('destroy');
            });
            
            /** Gets the descendants for the device from the network tree **/
            $(document).on('click', '.js-device-descendants', function() {
                var deviceId = $(this).data('deviceId'),
                    focusDevice = yukon.mapping.findFocusDevice(deviceId, true);
                _removeDeviceFocusLayers();
                _deviceFocusCurrentIcon = focusDevice;
                //if focus device was removed, add it back
                var deviceFound = yukon.mapping.findFocusDevice(deviceId, false);
                if (deviceFound == null) {
                    var source = yukon.mapping.getIconLayerSource();
                    source.addFeature(focusDevice);
                    _deviceFocusIcons.push(focusDevice);
                }
                yukon.mapping.displayDescendants(deviceId, false);
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-neighbors', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/neighbors') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.neighbors) {
                        _addNeighborDataToMap(deviceId, json.neighbors);
                        yukon.mapping.displayNeighborsLegend();
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                    _addAllPrimaryRoutes();
                });
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-route', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    _addAllPrimaryRoutes();
                    if (json.entireRoute) {
                        _addPrimaryRouteToMap(deviceId, json.entireRoute);
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
            });
            
            
            /** Add all primary routes to the map **/
            $(document).on('change', '.js-all-routes-collection', function() {
                _addAllPrimaryRoutes();
            });

            /** Add an elevation layer to the map **/
            $(document).on('click', '.js-elevation-layer', function() {
                yukon.mapping.showHideElevationLayer(_map, $(this));
            });
            
            /** Remove animation classes when animation finishes. */
            $('#device-collection .js-count')
                .on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(ev) {
                $('#device-collection .js-count').removeClass('animate__animated animate__flash'); 
            });
            $('#violation-collection .js-violations')
                .on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(ev) {
                    $('#violation-collection .js-violations').removeClass('animate__animated animate__flash'); 
            });
            
            /** Pause/Resume updating on updater button clicks. */
            $('#map-updater .button').on('click', function(ev) {
                var pause = $('#map-updater .yes').is('.on');
                if (pause) {
                    clearTimeout(_updater);
                } else {
                    _updater = setTimeout(_update, _updateInterval);
                }
                $('#map-updater .button').toggleClass('on');
            });
            
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                yukon.mapping.adjustMapForFullScreenModeChange($('#map-container'), "10px");
            });
            
            $("body").on("dialogopen", function (event, ui) {
                // if the user is viewing the map in fullscreen mode, append the dialog to the div that is being displayed in fullscreen mode.
                // So that the dialog displays over the fullscreen div. 
                if (document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement) {
                    $(event.target).closest('.ui-dialog').appendTo("div.ol-viewport");
                    $(event.target).closest('.ui-dialog').find('.ui-dialog-content').scroll( function (event) {
                        if ($(this).hasClass('menu-open')) {
                            $(this).removeClass('menu-open');
                        }
                        $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                    });
                    
                    // If a dialog box is opened while viewing the map in full screen map, disable double click and
                    // mouse wheel zoom interactions since they perform a zoom operation on map.
                    if(!_interactionsBlocked) {
                        _doubleClickZoomInteraction.setActive(false);
                        _mouseWheelZoomInteraction.setActive(false);
                        _interactionsBlocked = true;
                    }
                }
            });
            
            $("body").on("dialogclose", function(event, ui) {
                // If Double click and mouse wheel zoom interactions are disabled AND there is no dialog box open,
                // enable those operations again.
                if(($("body").find(".ui-dialog:visible").length === 0) && _interactionsBlocked) {
                    _doubleClickZoomInteraction.setActive(true);
                    _mouseWheelZoomInteraction.setActive(true);
                    _interactionsBlocked = false;
                }
            });
            
            var interactions = _map.getInteractions();
            for(var i=0; i < interactions.getLength(); i++) {
                var interaction = interactions.item(i);
                if (interaction instanceof ol.interaction.DoubleClickZoom) {
                    _doubleClickZoomInteraction = interaction;
                } else if (interaction instanceof ol.interaction.MouseWheelZoom) {
                    _mouseWheelZoomInteraction = interaction;
                }
            }
            
            _initialized = true;
        },
        
    };
    
    return _mod;
})();

$(function() { yukon.tools.map.init(); });