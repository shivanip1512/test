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
    _parentColor = "#808080",
    //dark blue
    _routeColor = yukon.mapping.getRouteColor(),
    //grey
    _focusRouteColor = "#808080",
    _largerScale = 1.1,
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _parentLayerIndex = 2,
    _iconLayerIndex = 3,
    
    _devicePoints = [],
    _deviceIcon,
    _deviceStandsOut = false,
    _deviceOriginalStyle,
    _parentIcon,
    _parentLine,
    _parentIconLayer,
    _neighborIcons = [],
    _neighborLines = [],
    _neighborIconLayer,
    _primaryRouteIcons = [],
    _primaryRouteLines = [],
    _primaryRouteIconLayer,
    _primaryRoutePreviousPoints,
    _deviceDragInteraction,
    _nearbyIcons = [],
    _nearbyIconLayer,
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _deviceFocusIconLayer,
    
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

        icon.setStyle(style);
        _deviceOriginalStyle = style;
            
        if (_srcProjection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
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
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: [_deviceIcon]})});
        iconsLayer.setZIndex(_iconLayerIndex);
        _map.addLayer(iconsLayer);
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
    
    _loadParentData = function(parent) {
        var source = yukon.mapping.getIconLayerSource(),
            feature = parent.location.features[0],
            pao = feature.properties.paoIdentifier,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ parent: parent, pao: pao });
        
        icon.setStyle(style);
    
        if (_srcProjection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        _parentIcon = icon;
        source.addFeature(icon);
        
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
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: [_parentIcon]})});
        iconsLayer.setZIndex(_iconLayerIndex);
        _parentIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        yukon.mapping.updateZoom(_map);
        
        _makeCurrentDeviceStandOut();
    },
    
    _removeDeviceFocusLayers = function() {
        var source = yukon.mapping.getIconLayerSource();
        _deviceFocusIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _deviceFocusLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _map.removeLayer(_deviceFocusIconLayer);
        _deviceFocusIcons = [];
        _deviceFocusLines = [];
        _deviceFocusIconLayer = null;
        //set focus device back to normal style
        if (_deviceFocusCurrentIcon != null) {
            yukon.mapping.setScaleForDevice(_deviceFocusCurrentIcon);
        }
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var source = yukon.mapping.getIconLayerSource();
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = yukon.mapping.findFocusDevice(deviceId, true);
                focusPoints = focusDevice.getGeometry().getCoordinates();
        }
            
       if (isFocusDevice) {
           var clonedFocusDevice = focusDevice.clone();
           _removeDeviceFocusLayers();
           _deviceFocusCurrentIcon = clonedFocusDevice;
           _deviceFocusIcons.push(clonedFocusDevice);
           source.addFeature(clonedFocusDevice);
       }
        
        for (x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            pao = feature.properties.paoIdentifier,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ neighbor: neighbor, pao: pao });
            
            icon.setId(feature.id);
            
            //check if neighbor already exists on map
            var neighborFound = yukon.mapping.findFocusDevice(pao.paoId, false);
            if (neighborFound) {
                icon = neighborFound;
                icon.set("neighbor", neighbor);
                icon.unset("routeInfo");
            } else {
                icon.setStyle(style);
                
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                if (isFocusDevice) {
                    _deviceFocusIcons.push(icon);
                } else {
                    _neighborIcons.push(icon);
                }
                source.addFeature(icon);
            }

            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (isFocusDevice) {
                points.push(focusPoints);
            } else {
                points.push(_devicePoints);
            }

            var lineColor = yukon.mapping.getNeighborLineColor(neighbor.data.etxBand),
                lineThickness = yukon.mapping.getNeighborLineThickness(neighbor.data.numSamples);
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),

                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: lineColor, width: lineThickness })
                })
            });
            
            layerLines.setZIndex(_neighborsLayerIndex);
            if (isFocusDevice) {
                _deviceFocusLines.push(layerLines);
            } else {
                _neighborLines.push(layerLines);
            }
            _map.addLayer(layerLines);
        }
        
        var allIcons = [];
        if (isFocusDevice) {
            allIcons.push.apply(allIcons, _deviceFocusIcons);
        } else {
            allIcons.push.apply(allIcons, _neighborIcons);
        }
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        if (isFocusDevice) {
            _deviceFocusIconLayer = iconsLayer;
        } else {
            _neighborIconLayer = iconsLayer;
        }
        _map.addLayer(iconsLayer);
        
        yukon.mapping.updateZoom(_map);
        
        if (!isFocusDevice) {
            _makeCurrentDeviceStandOut();
        }
    },
    
    _loadNeighborData = function(neighbors) {
        _addNeighborDataToMap(null, neighbors);
    },
    
    _loadDeviceNeighbors = function(deviceId, neighbors) {
        _addNeighborDataToMap(deviceId, neighbors);
    },

    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var source = yukon.mapping.getIconLayerSource(),
            routeColor = _routeColor,
            routeLineWidth = 3.5;
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = yukon.mapping.findFocusDevice(deviceId, true),
                focusPoints = focusDevice.getGeometry().getCoordinates(),
                routeColor = _focusRouteColor,
                routeLineWidth = 2.5;
        }
        _primaryRoutePreviousPoints = null;
        if (isFocusDevice) {
            _removeDeviceFocusLayers();
            _deviceFocusCurrentIcon = focusDevice;
        }

        for (x in routeInfo) {
            var route = routeInfo[x],
                feature = route.location.features[0],
                pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ routeInfo: route, pao: pao });
            
            icon.setStyle(style);
            
            //the first device in the route will be the focus device
            if (x == 0) {
                var largerStyle = icon.getStyle().clone();
                largerStyle.getImage().setScale(_largerScale);
                icon.setStyle(largerStyle);
            }
            
            if (_srcProjection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }

            if (isFocusDevice) {
                _deviceFocusIcons.push(icon);
            } else {
                _primaryRouteIcons.push(icon);
            }
            source.addFeature(icon);
            
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
                    stroke: new ol.style.Stroke({ color: routeColor, width: routeLineWidth })
                })
            });
            
            layerLines.setZIndex(_primaryRouteLayerIndex);
            if (isFocusDevice) {
                _deviceFocusLines.push(layerLines);
            } else {
                _primaryRouteLines.push(layerLines);
            }
            _map.addLayer(layerLines);
        }
        
        var allIcons = isFocusDevice ? _deviceFocusIcons : _primaryRouteIcons,
                iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons})});
        iconsLayer.setZIndex(_iconLayerIndex);
        if (isFocusDevice) {
            _deviceFocusIconLayer = iconsLayer;
        } else {
            _primaryRouteIconLayer = iconsLayer;
        }
        _map.addLayer(iconsLayer);
        
        yukon.mapping.updateZoom(_map);
        
        if (!isFocusDevice) {
            _makeCurrentDeviceStandOut();
        }
    }
    
    _loadPrimaryRouteData = function(routeInfo) {
        _addPrimaryRouteToMap(null, routeInfo);
    },
    
    _loadDeviceRoute = function(deviceId, routeInfo) {
        _addPrimaryRouteToMap(deviceId, routeInfo);
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
            } else{
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
            
                _nearbyIcons.push(icon);
                source.addFeature(icon);
            }

        }
        
        var allIcons = [];
        allIcons.push.apply(allIcons, _nearbyIcons);
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        _nearbyIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        yukon.mapping.updateZoom(_map);
        
        _makeCurrentDeviceStandOut();
    },
    
    _makeCurrentDeviceStandOut = function() {
        /*Change device Icon to be larger and have colored circle underneath*/
        if (!_deviceStandsOut) {
            var largerStyle = _deviceIcon.getStyle().clone(),
                circleStyle = new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 8,
                    fill: new ol.style.Fill({color: _routeColor}),
                    stroke: new ol.style.Stroke({color: 'black', width: 2}) 
                })
            });
            largerStyle.getImage().setScale(_largerScale);
            _deviceIcon.setStyle([circleStyle, largerStyle]);
            _deviceStandsOut = true;
        }
    },
    
    _checkToGoBackToDeviceOriginalStyle = function() {
        //map originally has 5 layers (Map, Satellite, Hybrid, Device Feature, Icon Layer)
        if (_map.getLayers().getArray().length < 6) {
            _deviceIcon.setStyle(_deviceOriginalStyle);
            _deviceStandsOut = false;
        }
    }
    
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
            var nearby = _nearbyIcons[x];
            source.removeFeature(nearby);
        }
        _map.removeLayer(_nearbyIconLayer);
        _nearbyIcons = [];
        yukon.mapping.updateZoom(_map);
        _checkToGoBackToDeviceOriginalStyle();
    },
    
    _removeFeatureFromLayer = function(layer, paoId) {
        if (layer) {
            var source = layer.getSource();
            source.getFeatures().forEach(function (feature) {
                if (feature.get('pao').paoId === paoId) {
                    source.removeFeature(feature);
                }
            });
        }
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
                _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
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
                                //remove feature from all layers
                                _removeFeatureFromLayer(_map.getLayers().getArray()[_tiles.length], paoId);
                                _removeFeatureFromLayer(_nearbyIconLayer, paoId);
                                _removeFeatureFromLayer(_neighborIconLayer, paoId);
                                _removeFeatureFromLayer(_primaryRouteIconLayer, paoId);
                                _removeFeatureFromLayer(_parentIconLayer, paoId);

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
                        if (_neighborIcons.length > 0) {
                            var source = yukon.mapping.getIconLayerSource();
                            _neighborLines.forEach(function (line) {
                                _map.addLayer(line);
                            });
                            _neighborIcons.forEach(function (icon) {
                                source.addFeature(icon);
                            });
                            _map.addLayer(_neighborIconLayer);
                            yukon.mapping.updateZoom(_map);
                            _makeCurrentDeviceStandOut();
                        } else {
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
                        }
                    } else {
                        yukon.mapping.hideNeighborsLegend();
                        var source = yukon.mapping.getIconLayerSource();
                        _neighborIcons.forEach(function (icon) {
                            source.removeFeature(icon);
                        });
                        _neighborLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _map.removeLayer(_neighborIconLayer);
                        _removeDeviceFocusLayers();
                        yukon.mapping.updateZoom(_map);
                        _checkToGoBackToDeviceOriginalStyle();
                    }
                    _addAllPrimaryRoutes();
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
                        if (_primaryRouteIcons.length > 0) {
                            var source = yukon.mapping.getIconLayerSource();
                            _primaryRouteLines.forEach(function (line) {
                                _map.addLayer(line);
                            });
                            _primaryRouteIcons.forEach(function (icon) {
                                source.addFeature(icon);
                            });
                            _map.addLayer(_primaryRouteIconLayer);
                            yukon.mapping.updateZoom(_map);
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                                feature = fc.features[0],
                                paoId = feature.id;
                            yukon.ui.busy('.js-primary-route');
                            $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute?') + $.param({ deviceId: paoId }))
                            .done(function (json) {
                                if (json.routeInfo) {
                                    _loadPrimaryRouteData(json.routeInfo);
                                }
                                if (json.errorMsg) {
                                    yukon.ui.alertError(json.errorMsg);
                                }
                                yukon.ui.unbusy('.js-primary-route');
                            });
                        }
                    } else {
                        var source = yukon.mapping.getIconLayerSource();
                        _primaryRouteIcons.forEach(function (icon) {
                            source.removeFeature(icon);
                        });
                        _primaryRouteLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _map.removeLayer(_primaryRouteIconLayer);
                        _removeDeviceFocusLayers();
                        yukon.mapping.updateZoom(_map);
                        _checkToGoBackToDeviceOriginalStyle();
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
                        if (json.routeInfo) {
                            _loadDeviceRoute(deviceId, json.routeInfo);
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
                        if (_parentIcon != null) {
                            var source = yukon.mapping.getIconLayerSource();
                            source.addFeature(_parentIcon);
                            _map.addLayer(_parentLine);
                            _map.addLayer(_parentIconLayer);
                            yukon.mapping.updateZoom(_map);
                            _makeCurrentDeviceStandOut();
                        } else {
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
                        }
                    } else {
                        //Remove parent and line
                        var source = yukon.mapping.getIconLayerSource();
                        source.removeFeature(_parentIcon);
                        _map.removeLayer(_parentLine);
                        _map.removeLayer(_parentIconLayer);
                        yukon.mapping.updateZoom(_map);
                        _checkToGoBackToDeviceOriginalStyle();
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