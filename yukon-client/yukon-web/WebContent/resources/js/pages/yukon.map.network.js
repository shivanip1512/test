yukon.namespace('yukon.map.network');
 
/**
 * Module to handle the map network page
 * 
 * @module yukon.map.network
 * @requires yukon
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.network = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //grey
    _parentColor = yukon.mapping.getFocusRouteColor(),
    //dark blue
    _routeColor = yukon.mapping.getRouteColor(),
    //grey
    _focusRouteColor = yukon.mapping.getFocusRouteColor(),
    _largerScale = 1.1,
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _primaryRouteLayerIndex = 1,
    _parentLayerIndex = 2,
    _iconLayerIndex = yukon.mapping.getIconLayerZIndex(),
    
    _devicePoints = [],
    _deviceIcon,
    _parentIcon,
    _parentLine,
    _neighborIcons = [],
    _neighborLines = [],
    _primaryRouteIcons = [],
    _primaryRouteLines = [],
    _primaryRoutePreviousPoints,
    _deviceDragInteraction,
    _nearbyIcons = [],
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _destProjection = yukon.mapping.getDestProjection(),
    _srcProjection = yukon.mapping.getSrcProjection(),
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    /** 
     * Gets pao location as geojson format and adds an icon feature to the vector layer for the map.
     */
    _loadIcon = function() {
        var source = yukon.mapping.getIconLayerSource(),
            fc = yukon.fromJson('#geojson'),
            feature = fc.features[0],
            pao = feature.properties.paoIdentifier,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ pao: pao });
            
        icon.setId(feature.id);

        var largerStyle = style.clone(),
            circleStyle = new ol.style.Style({
            image: new ol.style.Circle({
                radius: 8,
                fill: new ol.style.Fill({color: _routeColor}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}) 
            })
        });
        largerStyle.getImage().setScale(_largerScale);
        icon.setStyle([circleStyle, largerStyle]);

        var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
        icon.setGeometry(new ol.geom.Point(coord));
        
        // Drag and drop feature
        _deviceDragInteraction = new ol.interaction.Modify({
            features: new ol.Collection([icon]),
            pixelTolerance: 40
        });
        
        // Add the event to the drag and drop feature
        _deviceDragInteraction.on('modifyend', function(e) {
            yukon.map.location.changeCoordinatesPopup(e, _destProjection, _srcProjection);
        }, icon);
        
        _devicePoints = icon.getGeometry().getCoordinates();
        
        source.addFeature(icon);
        _deviceIcon = icon;
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
    
    _loadParentData = function(parent) {
        var source = yukon.mapping.getIconLayerSource(),
            feature = yukon.mapping.getFeatureFromRouteOrNeighborData(parent);
        
        if (feature != null) {
            var pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                properties = Object.keys(parent).map(function (key) {
                    var parentInfo = parent[key];
                    if (parentInfo != null && parentInfo.properties != null) {
                        return parentInfo.properties;
                    }
                });
            if (properties != null) {
                parent.distance = properties[0].distance;
            }
            
            var icon = new ol.Feature({ parent: parent, pao: pao });
            icon.setStyle(style);
            icon.setId(feature.id);
            
            //check if parent already exists on map
            var parentFound = yukon.mapping.findFocusDevice(pao.paoId, false);
            if (parentFound) {
                icon = parentFound;
                icon.set("parent", parent);
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
                source.addFeature(icon);
            }
            _parentIcon = icon;
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            points.push(_devicePoints);
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),
                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: _parentColor, width: 2, lineDash: [10,10] })
                })
            });
            
            layerLines.setZIndex(_parentLayerIndex);
            _parentLine = layerLines;
            _map.addLayer(layerLines);
            
            yukon.mapping.updateZoom(_map);
        }
    },
    
    _removeDeviceFocusLayers = function() {
        var source = yukon.mapping.getIconLayerSource();
        _deviceFocusIcons.forEach(function (icon) {
            var id = icon.getId(),
                isPrimaryRoute = _isDeviceInArray(_primaryRouteIcons, id),
                isNeighbor = _isDeviceInArray(_neighborIcons, id),
                isNearby = _isDeviceInArray(_nearbyIcons, id),
                isParent = _parentIcon != null && _parentIcon.getId() === id,
                isMainDevice = _deviceIcon.getId() === id;
            if (!isPrimaryRoute && !isNeighbor && !isNearby && !isParent && !isMainDevice && source.getFeatureById(id) != null) {
                source.removeFeature(icon);
            }
        });
        _deviceFocusLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _deviceFocusIcons = [];
        _deviceFocusLines = [];
        //set focus device back to normal style
        if (_deviceFocusCurrentIcon != null && _deviceIcon.getId() != _deviceFocusCurrentIcon.getId()) {
            yukon.mapping.setScaleForDevice(_deviceFocusCurrentIcon);
        }
        yukon.mapping.removeDescendantLayers();
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var source = yukon.mapping.getIconLayerSource();
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = yukon.mapping.findFocusDevice(deviceId, true),
                focusPoints = focusDevice.getGeometry().getCoordinates();
            _removeDeviceFocusLayers();
            _deviceFocusCurrentIcon = focusDevice;
            var deviceFound = yukon.mapping.findFocusDevice(deviceId, false);
            if (deviceFound == null) {
                source.addFeature(focusDevice);
                _deviceFocusIcons.push(focusDevice);
            }
        }

        for (x in neighbors) {
            var device = neighbors[x];
            if (isFocusDevice) {
                yukon.mapping.createNeighborDevice(device, _deviceFocusIcons, _deviceFocusLines, focusPoints, true);
            } else {
                yukon.mapping.createNeighborDevice(device, _neighborIcons, _neighborLines, _devicePoints, true);
            }
        }
        
        yukon.mapping.updateZoom(_map);
    },
    
    _loadNeighborData = function(neighbors) {
        _addNeighborDataToMap(null, neighbors);
    },
    
    _loadDeviceNeighbors = function(deviceId, neighbors) {
        _addNeighborDataToMap(deviceId, neighbors);
    },

    _addPrimaryRouteToMap = function(deviceId, routeInfo, deviceNeighborData) {
        var source = yukon.mapping.getIconLayerSource(),
            routeColor = _routeColor,
            dashedLine = false,
            routeLineWidth = 3.5,
            properties = null;

        if (deviceNeighborData != null) {
            var etxBand = deviceNeighborData.etxBand,
                numSamples = deviceNeighborData.numSamples;
            properties = { etxBand: etxBand, numSamples: numSamples};
            if (etxBand != null && numSamples != null) {
                routeColor = yukon.mapping.getNeighborLineColor(deviceNeighborData.etxBand);
                routeLineWidth = yukon.mapping.getNeighborLineThickness(deviceNeighborData.numSamples);
            }
        }
        
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = yukon.mapping.findFocusDevice(deviceId, true),
                focusPoints = focusDevice.getGeometry().getCoordinates();
            _removeDeviceFocusLayers();
            _deviceFocusCurrentIcon = focusDevice;
            var deviceFound = yukon.mapping.findFocusDevice(deviceId, false);
            //if device is no longer on the map, add it back
            if (deviceFound == null) {
                source.addFeature(focusDevice);
                _deviceFocusIcons.push(focusDevice);
            }
            focusDevice.set("primaryRoute", properties);
            focusDevice.unset("neighbor");
        } else {
            _deviceIcon.set("primaryRoute", properties);
        }
        _primaryRoutePreviousPoints = null;

        for (x in routeInfo) {
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
                
                var device = Object.keys(route).map(function (key) {
                    return route[key];
                });
                if (device != null && device.length > 0) {
                    if (device[0].properties != null) {
                        etxBand = device[0].properties.etxBand,
                        numSamples = device[0].properties.numSamples;
                        properties = { etxBand: etxBand, numSamples: numSamples};
                        icon.set("primaryRoute", properties);
                    }
                }
                
                //check if device already exists on map
                var deviceFound = yukon.mapping.findFocusDevice(pao.paoId, false);
                if (deviceFound) {
                    icon = deviceFound;
                    icon.set("primaryRoute", properties);
                    icon.unset("neighbor");
                } else {
                    icon.setStyle(style);
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                    source.addFeature(icon);
                }
                
                if (isFocusDevice) {
                    _deviceFocusIcons.push(icon);
                } else {
                    _primaryRouteIcons.push(icon);
                }
                
                //draw line
                var points = [];
                points.push(icon.getGeometry().getCoordinates());
                if (_primaryRoutePreviousPoints != null) {
                    points.push(_primaryRoutePreviousPoints);
                } else {
                    if (isFocusDevice) {
                        points.push(focusPoints);
                    } else {
                        points.push(_devicePoints);
                    }
                }
                _primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
                
                var layerLines = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: [new ol.Feature({
                            geometry: new ol.geom.LineString(points),
                            name: 'Line'
                        })]
                    }),
                    style: new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: isFocusDevice ? _focusRouteColor : routeColor,
                            width: isFocusDevice ? 2.5 : routeLineWidth,
                            lineDash: dashedLine ? [10,10] : null
                        })
                    })
                });
                
                layerLines.setZIndex(_primaryRouteLayerIndex);
                if (isFocusDevice) {
                    _deviceFocusLines.push(layerLines);
                } else {
                    _primaryRouteLines.push(layerLines);
                }
                _map.addLayer(layerLines);
                
                dashedLine = false;
            }
            
            routeColor = yukon.mapping.getNeighborLineColor(etxBand);
            routeLineWidth = yukon.mapping.getNeighborLineThickness(numSamples);
        }

        yukon.mapping.updateZoom(_map);
    },
    
    _loadPrimaryRouteData = function(routeInfo, deviceNeighborData) {
        _addPrimaryRouteToMap(null, routeInfo, deviceNeighborData);
    },
    
    _loadDeviceRoute = function(deviceId, routeInfo, deviceNeighborData) {
        _addPrimaryRouteToMap(deviceId, routeInfo, deviceNeighborData);
    },
    
    _loadNearbyDevices = function(nearbyDevices) {
        var source = yukon.mapping.getIconLayerSource();
        for (x in nearbyDevices) {
            var nearby = nearbyDevices[x],
                feature = nearby.location.features[0],
                pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
                icon = new ol.Feature({ nearby: nearby, pao: pao });
            
            icon.setId(pao.paoId);
            icon.setStyle(style);
            
            //check if neighbor already exists on map
            var nearbyFound = yukon.mapping.findFocusDevice(pao.paoId, false);
            if (nearbyFound) {
                icon = nearbyFound;
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
                source.addFeature(icon);
            }                
            _nearbyIcons.push(icon);
        }
        
        yukon.mapping.updateZoom(_map);
    },
    
    _getNearbyDevices = function() {
        var fc = yukon.fromJson('#geojson'),
            feature = fc.features[0],
            paoId = feature.id,
            miles = $('#miles').val();
        yukon.ui.removeAlerts();
        yukon.ui.busy('.js-nearby');
        $.getJSON(yukon.url('/stars/mapNetwork/nearby?') + $.param({ deviceId: paoId, miles: miles }))
        .done(function (json) {
            if (json.nearby) {
                _loadNearbyDevices(json.nearby);
            }
            if (json.errorMsg) {
                yukon.ui.alertError(json.errorMsg);
            }
            yukon.ui.unbusy('.js-nearby');
        });
    },
    
    _removeNearbyDevices = function() {
        yukon.ui.removeAlerts();
        var source = yukon.mapping.getIconLayerSource();
        for (x in _nearbyIcons) {
            var nearby = _nearbyIcons[x],
                id = nearby.getId(),
                isPrimaryRoute = _isDeviceInArray(_primaryRouteIcons, id),
                isNeighbor = _isDeviceInArray(_neighborIcons, id),
                isParent = _parentIcon != null && _parentIcon.getId() === id,
                isMainDevice = _deviceIcon.getId() === id;
            //don't remove if exists in another area
            if (!isPrimaryRoute && !isNeighbor && !isParent && !isMainDevice && source.getFeatureById(id) != null) {
                source.removeFeature(nearby);
            }
        }
        _nearbyIcons = [];
        yukon.mapping.updateZoom(_map);
    },
    
    _addAllPrimaryRoutes = function() {
        var checked = $('.js-all-routes-map-network').find(':checkbox').prop('checked');
        if (checked) {
            //get all device ids on the map
            var deviceIds = [],
                source = yukon.mapping.getIconLayerSource();
            source.getFeatures().forEach(function (feature) {
                deviceIds.push(feature.getId());
            });
            $.ajax({
                type: 'POST',
                data: {
                    deviceIds: deviceIds
                },
                url: yukon.url('/stars/mapNetwork/selectedGateways')
            }).done(function(gatewayIds) {
                yukon.mapping.showHideAllRoutes(gatewayIds);
            });
        } else {
            yukon.mapping.showHideAllRoutes();
        }    
    },
    
    _isDeviceInArray = function(deviceArray, deviceId) {
        var device = deviceArray.filter(function (icon) {
            return icon.getId() === deviceId;
        });
        return device.length > 0;
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
                        
            /** Initialize map if we have a location. */
            if ($('#device-location').data('hasLocation') === true) {
                
                /** Setup the openlayers map. */
                _map = new ol.Map({
                    controls: [
                        new ol.control.Attribution(),
                        new ol.control.FullScreen({source: 'map-network-container'}),
                        new ol.control.Zoom() 
                    ],
                    layers: _tiles,
                    target: 'device-location',
                    view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], _srcProjection, _destProjection), zoom: 4 })
                });
                yukon.mapping.initializeMap(_map);
                _destProjection = _map.getView().getProjection().getCode();
                _map.addLayer(new ol.layer.Vector({ name: 'icons', zIndex: _iconLayerIndex, source: new ol.source.Vector({ projection: _destProjection }) }));
                /** Hide any cog dropdowns on zoom or map move **/
                _map.getView().on('change:resolution', function(ev) {
                    $('.dropdown-menu').css('display', 'none');
                 });
                 _map.on('movestart', function(ev) {
                     $('.dropdown-menu').css('display', 'none');
                  });
                /** Load icon for location */
                _loadIcon();
                
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
                        data: {_method: 'DELETE'},
                        success: function(results) {
                            //if device deleted is the main device, reload page 
                            if (_deviceIcon.get('pao').paoId === paoId) {
                                window.location.reload();
                            } else {
                                $('#confirm-delete').dialog('destroy');
                                yukon.ui.removeAlerts();
                                var source = yukon.mapping.getIconLayerSource();
                                source.removeFeatureById(paoId);

                                var successMsg = $('#coordinatesDeletedMsg').val();
                                yukon.ui.alertSuccess(successMsg);
                                
                                $('#marker-info').hide();
                            }
                        },
                        error: function(xhr, status, error) {
                            var errorMsg = xhr.responseJSON.message;
                            yukon.ui.alertError(errorMsg);
                        }
                    });
                    
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-neighbor-data', function() {
                    var neighborsRow = $(this).closest('.switch-btn'),
                    wasChecked = neighborsRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        yukon.mapping.displayNeighborsLegend();
                        var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                        yukon.ui.busy('.js-neighbor-data');
                        $.getJSON(yukon.url('/stars/mapNetwork/neighbors?') + $.param({ deviceId: paoId }))
                        .done(function (json) {
                            if (json.neighbors) {
                                _loadNeighborData(json.neighbors);
                            }
                            if (json.errorMsg) {
                                yukon.ui.alertError(json.errorMsg);
                            }
                            yukon.ui.unbusy('.js-neighbor-data');
                        });
                    } else {
                        var primaryRouteRow = $('.js-primary-route').closest('.switch-btn'),
                            primaryRouteChecked = primaryRouteRow.find('.switch-btn-checkbox').prop('checked');
                        if (!primaryRouteChecked) {
                            yukon.mapping.hideNeighborsLegend();
                        }
                        var source = yukon.mapping.getIconLayerSource();
                        _neighborIcons.forEach(function (icon) {
                            var id = icon.getId(),
                                isPrimaryRoute = _isDeviceInArray(_primaryRouteIcons, id),
                                isNearby = _isDeviceInArray(_nearbyIcons, id),
                                isParent = _parentIcon != null && _parentIcon.getId() === id,
                                isMainDevice = _deviceIcon.getId() === id;
                            //don't remove if exists in another area
                            if (!isPrimaryRoute && !isNearby && !isParent && !isMainDevice && source.getFeatureById(id) != null) {
                                source.removeFeature(icon);
                            }
                            icon.unset("neighbor");
                        });
                        _neighborLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _removeDeviceFocusLayers();
                        _neighborIcons = [];
                        _neighborLines = [];
                        yukon.mapping.updateZoom(_map);
                    }
                    _addAllPrimaryRoutes();
                });
                
                /** Gets the descendants for the device from the network tree **/
                $(document).on('click', '.js-device-descendants', function() {
                    var deviceId = $(this).data('deviceId');
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
                    yukon.mapping.displayDescendants(deviceId, true);
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-device-neighbors', function() {
                    var deviceId = $(this).data('deviceId'),
                        mapContainer = $('#map-network-container');
                    yukon.ui.block(mapContainer);
                    $.getJSON(yukon.url('/stars/mapNetwork/neighbors?') + $.param({ deviceId: deviceId }))
                    .done(function (json) {
                        if (json.neighbors) {
                            yukon.mapping.displayNeighborsLegend();
                            _loadDeviceNeighbors(deviceId, json.neighbors);
                        }
                        if (json.errorMsg) {
                            yukon.ui.alertError(json.errorMsg);
                        }
                        yukon.ui.unblock(mapContainer);
                        $('#marker-info').hide();
                    });
                    _addAllPrimaryRoutes();
                });
                
                /** Gets the primary route from Network Manager **/
                $(document).on('click', '.js-primary-route', function() {
                    var primaryRouteRow = $(this).closest('.switch-btn'),
                        wasChecked = primaryRouteRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        yukon.mapping.displayNeighborsLegend();
                        var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                        yukon.ui.busy('.js-primary-route');
                        $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute?') + $.param({ deviceId: paoId }))
                        .done(function (json) {
                            if (json.entireRoute) {
                                _loadPrimaryRouteData(json.entireRoute, json.deviceNeighborData);
                            }
                            if (json.errorMsg) {
                                yukon.ui.alertError(json.errorMsg);
                            }
                            yukon.ui.unbusy('.js-primary-route');
                        });
                    } else {
                        var neighborsRow = $('.js-neighbor-data').closest('.switch-btn'),
                            neighborsChecked = neighborsRow.find('.switch-btn-checkbox').prop('checked');
                        if (!neighborsChecked) {
                            yukon.mapping.hideNeighborsLegend();
                        }
                        var source = yukon.mapping.getIconLayerSource();
                        _primaryRouteIcons.forEach(function (icon) {
                            var id = icon.getId(),
                                isNeighbor = _isDeviceInArray(_neighborIcons, id),
                                isNearby = _isDeviceInArray(_nearbyIcons, id),
                                isParent = _parentIcon != null && _parentIcon.getId() === id,
                                isMainDevice = _deviceIcon.getId() === id;
                            if (!isNeighbor && !isNearby && !isParent && !isMainDevice && source.getFeatureById(id) != null) {
                                source.removeFeature(icon);
                            }
                        });
                        _primaryRouteLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _removeDeviceFocusLayers();
                        _primaryRouteIcons = [];
                        _primaryRouteLines = [];
                        yukon.mapping.updateZoom(_map);
                    }
                    _addAllPrimaryRoutes();
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-device-route', function() {
                    var deviceId = $(this).data('deviceId'),
                        mapContainer = $('#map-network-container');
                    yukon.ui.block(mapContainer);
                    $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute?') + $.param({ deviceId: deviceId }))
                    .done(function (json) {
                        if (json.entireRoute) {
                            _loadDeviceRoute(deviceId, json.entireRoute, json.deviceNeighborData);
                        }
                        if (json.errorMsg) {
                            yukon.ui.alertError(json.errorMsg);
                        }
                        yukon.ui.unblock(mapContainer);
                        $('#marker-info').hide();
                    });
                    _addAllPrimaryRoutes();
                });
                
                /** Gets the parent node from Network Manager **/
                $(document).on('click', '.js-parent-node', function() {
                    var parentNodeRow = $(this).closest('.switch-btn'),
                        wasChecked = parentNodeRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                        yukon.ui.busy('.js-parent-node');
                        $.getJSON(yukon.url('/stars/mapNetwork/parentNode?') + $.param({ deviceId: paoId }))
                        .done(function (json) {
                            if (json.parent) {
                                _loadParentData(json.parent);
                            }
                            if (json.errorMsg) {
                                yukon.ui.alertError(json.errorMsg);
                            }
                            yukon.ui.unbusy('.js-parent-node');
                        });
                    } else {
                        //Remove parent and line
                        if (_parentIcon != null) {
                            var source = yukon.mapping.getIconLayerSource(),
                                id = _parentIcon.getId(),
                                isNeighbor = _isDeviceInArray(_neighborIcons, id),
                                isNearby = _isDeviceInArray(_nearbyIcons, id),
                                isPrimaryRoute = _isDeviceInArray(_primaryRouteIcons, id);
                            _parentIcon.unset("parent");
                            if (!isNeighbor && !isNearby && !isPrimaryRoute && source.getFeatureById(id) != null) {
                                source.removeFeature(_parentIcon);
                            }
                        }
                        _map.removeLayer(_parentLine);
                        _parentIcon = null;
                        _parentLine = null;
                        yukon.mapping.updateZoom(_map);
                    }
                    _addAllPrimaryRoutes();
                });
                
                /** Add all primary routes to the map **/
                $(document).on('change', '.js-all-routes-map-network', function() {
                    _addAllPrimaryRoutes();
                });

                /** Add an elevation layer to the map **/
                $(document).on('click', '.js-elevation-layer', function() {
                    yukon.mapping.showHideElevationLayer(_map, $(this));
                });
                
                /** Gets the nearby devices **/
                $(document).on('click', '.js-nearby', function() {
                    var nearbyRow = $(this).closest('.switch-btn'),
                    wasChecked = nearbyRow.find('.switch-btn-checkbox').prop('checked');
                    if (!wasChecked) {
                        _getNearbyDevices();
                    } else {
                        _removeDeviceFocusLayers();
                        _removeNearbyDevices();
                    }
                    _addAllPrimaryRoutes();
                });
                
                /** Gets the nearby devices **/
                $(document).on('change', '.js-miles', function() {
                    var nearbyRow = $('.js-nearby').closest('.switch-btn'),
                    wasChecked = nearbyRow.find('.switch-btn-checkbox').prop('checked');
                    if (wasChecked) {
                        _removeNearbyDevices();
                        _getNearbyDevices();
                    }
                    _addAllPrimaryRoutes();
                });

                
                $(document).on('click', '.js-edit-coordinates', function() {
                    _map.addInteraction(_deviceDragInteraction);
                });
                
                $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                    yukon.mapping.adjustMapForFullScreenModeChange($('#map-network-container'), "10px");
                });
                
                $("body").on("dialogopen", function (event, ui) {
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
                
            }
            
            _initialized = true;
        },
        
        getMap: function () {
            return _map;
        }

    };
 
    return mod;
})();
 
$(function () { yukon.map.network.init(); });