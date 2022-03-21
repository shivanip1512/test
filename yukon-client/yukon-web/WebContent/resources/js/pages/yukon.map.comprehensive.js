yukon.namespace('yukon.map.comprehensive');
 
/**
 * Module to handle the Comprehensive Map page
 * 
 * @module yukon.map.comprehensive
 * @requires yukon
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.comprehensive = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //dark blue
    _routeColor = yukon.mapping.getRouteColor(),
    //grey
    _focusRouteColor = yukon.mapping.getFocusRouteColor(),
    _largerScale = yukon.mapping.getLargerScale(),
    
    //lines should go beneath icons
    _iconLayerZIndex = yukon.mapping.getIconLayerZIndex(),
    
    _primaryRoutePreviousPoints,
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    
    _highlightedDevices = [],
        
    /** @type {Object.<number, {ol.Feature}>} - Map of pao id to feature for all device icons. */
    _icons = [], 
    
    _destProjection = yukon.mapping.getDestProjection(),
    _srcProjection = yukon.mapping.getSrcProjection(),
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    _loadDevices = function(color, devices) {
        var iconLayer = yukon.mapping.getIconLayer(),
            source = iconLayer.getSource();
        for (x in devices.features) {
            var feature = devices.features[x],
                pao = feature.properties.paoIdentifier,
                src_projection = devices.crs.properties.name,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
                icon = new ol.Feature({ device: feature, pao: pao });
            
            icon.setId(feature.id);
            icon.setStyle(style);

            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc(),
                scale = image.getScale();
            
            currentStyle.setImage(new ol.style.Icon({ src: src, color: color, scale: scale, anchor: yukon.mapping.getAnchor() }));
            
            icon.setStyle(currentStyle);
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
                    
            _icons.push(icon);
            source.addFeature(icon);
        }
        
        yukon.mapping.updateZoom(_map);
        
    },
    
    _removeDeviceFocusLayers = function() {
        var iconLayer = yukon.mapping.getIconLayer(),
            source = iconLayer.getSource();
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
            dashedLine = false;
            routeLineWidth = 2.5;

        _primaryRoutePreviousPoints = null;
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
                
                //check if device already exists on map
                var deviceFound = yukon.mapping.findFocusDevice(pao.paoId, false);
                if (deviceFound) {
                    icon = deviceFound;
                    icon.unset("neighbor");
                } else {
                    icon.setStyle(style);
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                    
                    _deviceFocusIcons.push(icon);
                    source.addFeature(icon);
                }
                
                //draw line
                var points = [];
                points.push(icon.getGeometry().getCoordinates());
                if (_primaryRoutePreviousPoints != null) {
                    points.push(_primaryRoutePreviousPoints);
                } else {
                    points.push(focusPoints);
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
                            color: _focusRouteColor, 
                            width: routeLineWidth,
                            lineDash: dashedLine ? [10,10] : null
                        })
                    })
                });
                
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
        var gatewayIds = $(".js-selected-gateways").chosen().val();
        yukon.mapping.showHideAllRoutes(gatewayIds);
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(".js-selected-gateways").chosen({width: "320px", max_selected_options: 5});
            $(".js-selected-gateways").bind("chosen:maxselected", function () {
                var gatewayError = $('#tooManyGatewaysError').val();
                yukon.ui.alertError(gatewayError);
            }); 
            $(".js-selected-link-qualities").chosen({width: "150px"});
            $(".js-selected-descendant-count").chosen({width: "150px"});
            $(".js-selected-hop-count").chosen({width: "150px"});

            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen({source: 'comprehensive-map-container'}),
                    new ol.control.Zoom() 
                ],
                layers: _tiles,
                target: 'comprehensive-map',
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], _srcProjection, _destProjection), zoom: 4 })
            });
            yukon.mapping.initializeMap(_map);
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', zIndex: _iconLayerZIndex, source: new ol.source.Vector({ projection: _destProjection }) }));
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var popupCoordinates = ev.coordinate;
                var paoFeature = _map.forEachFeatureAtPixel(ev.pixel, function(feature) {
                    if (feature && feature.get('pao') != null) {
                        return feature;
                    } else if (feature && feature.get('geometry') != null) {
                        return feature;
                    }
                });
                if (paoFeature) {
                    if (paoFeature.get('pao') != null) {
                        yukon.mapping.displayMappingPopup(paoFeature, _overlay);
                    } else {
                        var popupContent = '',
                            properties = paoFeature.getProperties();
                        
                        const jsonMap = new Map(Object.entries(properties));
                        for (const [key, value] of jsonMap) {
                            if (value != '' && key != 'geometry') {
                                popupContent = popupContent + key + ": " + value + "<br/>";
                            }
                        }
                        $('#device-info').html(popupContent);
                        $('#device-info').show();
                        $('#marker-info').show();
                        _overlay.setPosition(popupCoordinates);
                    }
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
                var cog = $(target).closest('.js-cog-menu'),
                    noteIcon = $(target).closest('.js-view-all-notes');
                if (cog.exists() && !noteIcon.exists()) {
                    $('#js-popup-note-create').show();
                }
            });
            
            $(document).on('dialogopen', '#js-pao-notes-popup', function() {
                $('#device-info').hide();
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
            
            $('.js-selected-gateways').on('change', function(evt, params) {
                var values = $(".js-selected-gateways").chosen().val();
                $('.js-filter-map').prop('disabled', values.length == 0);
            });
            
            $(document).on('click', '.js-filter-map', function (ev) {
                var form = $('#filter-form');
                yukon.ui.block('#comprehensive-map-container');
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/filter'),
                    type: 'get',
                    data: form.serialize()
                }).done( function(data) {
                    if (data.errorMsg) {
                        yukon.ui.alertError(data.errorMsg);
                    } else {
                        //clear any existing features
                        var iconLayer = yukon.mapping.getIconLayer(),
                            source = iconLayer.getSource();
                        if (_icons.length > 0) {
                            _icons.forEach(function(icon) {
                                if (source.getFeatureById(icon.get('pao').paoId)) {
                                    source.removeFeature(icon); 
                                }
                            });
                            _icons = [];
                        }
                        _removeDeviceFocusLayers();
                        yukon.mapping.removeAllRoutesLayers();
                        yukon.mapping.removeAllGatewayIcons();
                        yukon.mapping.removeAllRelayIcons();
                        var map = data.map.mappedDevices;
                        Object.keys(map).forEach(function(key) {
                            var value = map[key];
                            _loadDevices(key, value);
                        });
                        $('#legend').empty();
                        for (var i = 0; i < data.map.legend.length; i++) {
                            var legendItem = data.map.legend[i];
                            $('#legend').append('<span class=dib><span class=small-circle style=margin-left:5px;margin-bottom:5px;background:' + legendItem.hexColor + '></span><span style=margin-left:5px;>' + legendItem.text + '</span></span>');
                        }
                        $('#legend').removeClass('dn');
                        //display devices cog
                        $('#filtered-devices').removeClass('dn');
                        $('.js-number-devices').html(data.map.totalDevices);
                        $('#collectionActionLink').attr('href', yukon.url(data.collectionActionRedirect));
                        $('#collection-group').val(data.collectionGroup);
                        yukon.mapping.showHideAllGateways();
                        yukon.mapping.showHideAllRelays();
                        var gatewayIds = $(".js-selected-gateways").chosen().val();
                        yukon.mapping.showHideAllRoutes(gatewayIds);                    
                    }
                }).always(function () {
                    yukon.ui.unblock('#comprehensive-map-container');
                });
            });
            
            $(document).on('click', '.js-download', function () {
                var collectionGroup = $('#collection-group').val();
                window.location = yukon.url('/stars/comprehensiveMap/download?groupName=' + collectionGroup);
            });
            
            $("#findDevice").keyup(function(event) {
                if (event.keyCode === 13) {
                    var searchText = $('#findDevice').val();
                    //remove last highlighted devices
                    if (_highlightedDevices.length > 0) {
                        var iconLayer = yukon.mapping.getIconLayer(),
                            source = iconLayer.getSource();
                        _highlightedDevices.forEach(function(device) {
                           source.removeFeature(device);
                        });
                        _highlightedDevices = [];
                    }
                    var searchError = $('.js-no-results-found');
                    searchError.addClass('dn');
                    if (searchText) {
                        $.ajax({
                            url: yukon.url('/stars/comprehensiveMap/search?searchText=' + searchText),
                            type: 'get',
                        }).done( function(data) {
                            if (data.paoIds) {
                                var foundADevice = false,
                                    iconLayer = yukon.mapping.getIconLayer(),
                                    source = iconLayer.getSource();
                                data.paoIds.forEach(function(paoId) {
                                    var feature = source.getFeatureById(paoId);
                                    if (feature) {
                                        foundADevice = true;
                                        var clonedFeature = feature.clone(),
                                            largerStyle = clonedFeature.getStyle().clone(),
                                            circleStyle = new ol.style.Style({
                                            image: new ol.style.Circle({
                                                radius: 8,
                                                fill: new ol.style.Fill({color: _routeColor}),
                                                stroke: new ol.style.Stroke({color: 'black', width: 2}) 
                                            })
                                        });
                                        largerStyle.getImage().setScale(_largerScale);
                                        clonedFeature.setStyle([circleStyle, largerStyle]);
                                        _highlightedDevices.push(clonedFeature);
                                        source.addFeature(clonedFeature);
                                    }
                                });
                                if (foundADevice) { 
                                    var allCoordinates = [];
                                    _highlightedDevices.forEach(function(device) {
                                        var coordinates = device.getGeometry().getCoordinates();
                                        allCoordinates.push(coordinates);
                                    });
                                    _map.getView().fit(ol.extent.boundingExtent(allCoordinates), {'maxZoom': 16});
                                } else {
                                    searchError.removeClass('dn');
                                }
                            } else {
                                searchError.removeClass('dn');
                            }
                        });
                    }
                }
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
                });
                _addAllPrimaryRoutes();
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-route', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.entireRoute) {
                        _addPrimaryRouteToMap(deviceId, json.entireRoute);
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
                _addAllPrimaryRoutes();
            });
            
            /** Add all primary routes to the map **/
            $(document).on('change', '.js-all-routes-comprehensive', function() {
                _addAllPrimaryRoutes();
            });
            
            /** Add an elevation layer to the map **/
            $(document).on('click', '.js-elevation-layer', function() {
                yukon.mapping.showHideElevationLayer(_map, $(this));
            });
            
            /** Add GIS Shapefile layer to the map **/
            $(document).on('change', '.js-upload-shapefile', function(ev) {
                var color = $('#upload-color').val(),
                    fr = new FileReader();
                fr.onload = function () {
                  shp(fr.result).then(function(geojson) {
                    var features = (new ol.format.GeoJSON()).readFeatures(geojson, {featureProjection: _destProjection});
                    var source = new ol.source.Vector({
                        features: features
                    });
                    
                    var styleName = features[0].getGeometry().getType();
                    var style = new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: color,
                                width: 1
                            })
                        });
                    
                    if (styleName === 'Polygon') {
                        var r = parseInt(color.slice(1, 3), 16),
                            g = parseInt(color.slice(3, 5), 16),
                            b = parseInt(color.slice(5, 7), 16);
                        style = new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: color,
                                width: 1
                            }),
                            fill: new ol.style.Fill({
                                color: 'rgba(' + r + ', ' + g + ', ' + b + ', 0.5)'
                            })
                        });
                    } else if (styleName === 'Point') {
                        style = new ol.style.Style({
                            image: new ol.style.Circle({
                                fill: new ol.style.Fill({color: color}),
                                radius: 3
                            })
                        });
                    } 
                    
                    var layerLines = new ol.layer.Vector({
                        source: source,
                        style: style
                    });

                    _map.addLayer(layerLines);
                    
                  })
                };
                fr.readAsArrayBuffer(ev.target.files[0]);
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
                        $('#confirm-delete').dialog('destroy');
                        yukon.ui.removeAlerts();
                        var iconLayer = yukon.mapping.getIconLayer(),
                            source = iconLayer.getSource();
                            feature = source.getFeatureById(paoId);
                        if (feature) {
                            source.removeFeature(feature);
                        }
                        var successMsg = $('#coordinatesDeletedMsg').val();
                        yukon.ui.alertSuccess(successMsg);
                        
                        $('#marker-info').hide();
                    },
                    error: function(xhr, status, error) {
                        var errorMsg = xhr.responseJSON.message;
                        yukon.ui.alertError(errorMsg);
                    }
                });
                
            });
                        
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                yukon.mapping.adjustMapForFullScreenModeChange($('#comprehensive-map-container'), "0px");
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
            
            _initialized = true;
        },
    };
 
    return mod;
})();
 
$(function () { yukon.map.comprehensive.init(); });