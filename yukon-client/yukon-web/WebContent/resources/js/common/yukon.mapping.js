yukon.namespace('yukon.mapping');
 
/**
 * Module to handle common mapping functionality
 * 
 * @module yukon.mapping
 * @requires yukon
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.mapping = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    _updateNetworkTreeInterval,
    
    _neighborColors = [yg.colors.GREEN, yg.colors.GREEN_LIGHT, yg.colors.YELLOW, yg.colors.ORANGE, yg.colors.RED],  

    _routeColor = yg.colors.BLUE_DARK,

    _focusRouteColor = yg.colors.NAVY,

    _highlightRouteColor = yg.colors.YELLOW,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    _srcProjection = 'EPSG:4326',
    
    //Controls zIndex of Icons (Gateways should always display on top, followed by relays and finally meters)
    _iconZIndex = 2,
    _relayZIndex = 3,
    _gatewayZIndex = 4,
    
    //Controls zIndex of layers (lines should be beneath icons)
    _lineZIndex = 0,
    _deviceLineZIndex = 1,
    _iconLayerZIndex = 3,
    
    //gateways should be the largest icons, then relays, then meters
    _deviceScale = 0.8,
    _relayScale = 0.9,
    _gatewayScale = 1,
    _largerScale = 1.1,
    
    _anchor = [0.5, 1.0],

    _elevationLayer,
    _allGatewayIcons = [],
    _allRelayIcons = [],
    _allRoutesIcons = [],
    _allRoutesLines = [],
    _allRoutesLineFeatures = [],
    _allRoutesDashedLineFeatures = [],
    _devicePrimaryRouteLayer,
    _devicePrimaryRouteDashedLayer,
    _descendantIcons = [],
    _descendantLines = [],
    _descendantLineFeatures = [],
    _descendantDashedLineFeatures = [],
    _missingDescendants = false,
    
    _setRouteLastUpdatedDateTime = function (dateTimeInstant) {
        if (dateTimeInstant == null) {
            $("#js-route-details-container").find(".js-last-update-date-time").text($(".js-loading-text").val());
        } else {
            $("#js-route-details-container").find(".js-last-update-date-time").text(" " + moment(dateTimeInstant).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
        }
    },

    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_PLC_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_CELL_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-cell-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_WIFI_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-wifi-grey.svg'), 
                scale: _relayScale, 
                anchor: _anchor }), 
            zIndex: _relayZIndex }),
        'METER_WATER': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_GAS': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'TRANSMITTER': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter-grey.svg'), 
                scale: _gatewayScale, 
                anchor: _anchor }), 
            zIndex: _gatewayZIndex }),
        'RELAY': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-relay-grey.svg'), 
                scale: _relayScale, 
                anchor: _anchor }), 
            zIndex: _relayZIndex }),
        'RELAY_CELLULAR': new ol.style.Style({
            image: new ol.style.Icon({
                src: yukon.url('/WebConfig/yukon/Icons/marker-relay-cell-grey.svg'),
                scale: _relayScale,
                anchor: _anchor }),
            zIndex: _relayZIndex }),
        'LCR' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-lcr-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'PLC_LCR' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-plc-lcr-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'THERMOSTAT' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-thermostat-grey.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'GENERIC_GREY': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-generic.svg'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
    },
    
    _attributionText = "© <a href='https://www.mapbox.com/about/maps/'>Mapbox</a> © <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a>",
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
    _tiles = [ 
          new ol.layer.Tile({ name: 'mqosm',
              source: new ol.source.XYZ({ name: 'mqosm',
                  url: yg.map_devices_street_url,
                  attributions: _attributionText,
                  tileSize: 512,
                  transition: 0
              })
          }),
          new ol.layer.Tile({ name: 'mqsat', visible: false,
              source: new ol.source.XYZ({ name: 'mqsat', 
                url: yg.map_devices_satellite_url,
                attributions: _attributionText,
                tileSize: 512,
                transition: 0
              })
          }),
          new ol.layer.Tile({ name: 'hybrid', visible: false,
              source: new ol.source.XYZ({ name: 'hybrid', 
                url: yg.map_devices_hybrid_url,
                attributions: _attributionText,
                tileSize: 512,
                transition: 0
              })
          })
    ],
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on("click", ".js-request-route-update", function () {
                $(".js-request-route-update").attr('disabled', true);
                $.post(yukon.url("/stars/comprehensiveMap/requestNetworkTreeUpdate"), function (response) {
                    if (response.isUpdateRequestSent) {
                        yukon.ui.alertSuccess(response.msgText);
                    } else {
                        yukon.ui.alertError(response.msgText);
                    }
                    $(window).scrollTop($('#user-message').offset().top);
                });
            });

            /** Redirects to new device map network page **/
            $(document).on('click', '.js-device-map', function() {
                var deviceId = $(this).data('deviceId');
                window.location.href = yukon.url('/stars/mapNetwork/home') + '?deviceId=' + deviceId;
            });
            
            $(document).on('change', '.js-all-gateways', function () {
                yukon.mapping.showHideAllGateways();
            });
            
            $(document).on('change', '.js-all-relays', function () {
                yukon.mapping.showHideAllRelays();
            });        
            
            $('#map-tiles button').click(function (ev) {
                $(this).siblings().removeClass('on');
                $(this).addClass('on');
                for (var i in _tiles) {
                    var layer = $(this).data('layer');
                    _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                }
                
            });
            
            _initialized = true;
        },
        
        initializeMap: function(map) {
            _map = map;
        },
        
        getStyles: function() {
            return _styles;
        },
        
        getTiles: function() {
            return _tiles;
        },
        
        getAnchor: function() {
            return _anchor;
        },
        
        getDestProjection: function() {
            return _destProjection;
        },
        
        getSrcProjection: function() {
            return _srcProjection;
        },
        
        getRouteColor: function() {
            return _routeColor;
        },
        
        getFocusRouteColor: function() {
            return _focusRouteColor;
        },
        
        getLargerScale: function() {
            return _largerScale;
        },
        
        getIconLayerZIndex: function() {
            return _iconLayerZIndex;
        },
        
        getIconLayer: function() {
            var iconLayer;
            _map.getLayers().forEach(function (layer) {
                if (layer.get('name') === 'icons') {
                    iconLayer = layer;
                }
            });
            return iconLayer;
        },
        
        getIconLayerSource: function() {
            return yukon.mapping.getIconLayer().getSource();
        },
        
        findFocusDevice: function(deviceId, makeLarger) {
            var source = yukon.mapping.getIconLayerSource(),
                feature = source.getFeatureById(deviceId);
            if (feature && makeLarger) {
                yukon.mapping.makeDeviceIconLarger(feature);
            }
            return feature;
        },
        
        makeDeviceIconLarger: function(icon) {
            var style = icon.getStyle();
            if (style instanceof Array) {
                var newStyles = [];
                icon.getStyle().forEach(function (style) {
                    var largerStyle = style.clone();
                    largerStyle.getImage().setScale(_largerScale);
                    newStyles.push(largerStyle);
                });
                icon.setStyle(newStyles);
            } else {
                var largerStyle = icon.getStyle().clone();
                largerStyle.getImage().setScale(_largerScale);
                icon.setStyle(largerStyle);
            }
        },
        
        setScaleForDevice: function(feature) {
            var currentStyle = feature.getStyle(),
                scale = _deviceScale,
                gatewayTypes = $('#gatewayTypes').val(),
                relayTypes = $('#relayTypes').val(),
                wifiTypes = $('#wifiTypes').val(),
                pao = feature.get('pao');
            if (relayTypes.indexOf(pao.paoType) > -1 || wifiTypes.indexOf(pao.paoType) > -1) {
                scale = _relayScale;
            } else if (gatewayTypes.indexOf(pao.paoType) > -1) {
                scale = _gatewayScale;
            }
            currentStyle.getImage().setScale(scale);
            feature.setStyle(currentStyle);
        },
        
        displayNeighborPopupProperties: function(neighbor) {
            var neighborData = neighbor.neighborData;
            $('.js-flags-display').toggleClass('dn', neighbor.commaDelimitedNeighborFlags === null);
            $('.js-flags').text(neighbor.commaDelimitedNeighborFlags);
            $('.js-link-cost-display').toggleClass('dn', neighborData.neighborLinkCost === null);
            $('.js-link-cost').text(neighborData.neighborLinkCost);
            $('.js-num-samples-display').toggleClass('dn', neighborData.numSamples === null);
            $('.js-num-samples').text(neighborData.numSamples);
            $('.js-etx-band-display').toggleClass('dn', neighborData.etxBand === null);
            $('.js-etx-band').text(neighborData.etxBand);
            $('.js-distance-display').toggleClass('dn', neighbor.distance === null);
            $('.js-distance').text(neighbor.distance);
        },
        
        displayParentNodePopupProperties: function(parent) {
            $('.js-distance-display').toggleClass('dn', parent.distance === null);
            $('.js-distance').text(parent.distance);
        },
        
        getNeighborLineColor: function(etxBand) {
            //Line Color is based on ETX Band - see colors above
            var lineColor = _neighborColors[4];
            switch(etxBand) {
                case 1:
                    lineColor = _neighborColors[0];
                    break;
                case 2:
                    lineColor = _neighborColors[1];
                    break;
                case 3:
                    lineColor = _neighborColors[2];
                    break;
                case 4:
                    lineColor = _neighborColors[3];
                    break;
                default:
                    lineColor = _neighborColors[4];
            }
            return lineColor;
        },
        
        getNeighborLineThickness: function(numberSamples) {
            //Line thickness depends on Number of Samples 0-50 - 1px, 51-500 - 2px, 500 and up - 3px
            var lineThickness = 1;
            if (numberSamples > 50 && numberSamples <= 500) {
                lineThickness = 2;
            } else if (numberSamples > 500) {
                lineThickness = 3;
            }
            return lineThickness;
        },
        
        displayNeighborsLegend: function() {
            var borderDetails = "2px solid ";
            $('.js-etx-1').css({backgroundColor: _neighborColors[0], borderTop: borderDetails + _neighborColors[0]});
            $('.js-etx-2').css({backgroundColor: _neighborColors[1], borderTop: borderDetails + _neighborColors[1]});
            $('.js-etx-3').css({backgroundColor: _neighborColors[2], borderTop: borderDetails + _neighborColors[2]});
            $('.js-etx-4').css({backgroundColor: _neighborColors[3], borderTop: borderDetails + _neighborColors[3]});
            $('.js-etx-5').css({backgroundColor: _neighborColors[4], borderTop: borderDetails + _neighborColors[4]});
            $('.js-legend-neighbors').show();
        },
        
        hideNeighborsLegend: function() {
            $('.js-legend-neighbors').hide();
        },
        
        updateDeviceStatusClass: function(deviceStatus) {
            var statusClass;
            $('.js-status').removeClass("success");
            $('.js-status').removeClass("error");
            $('.js-status').removeClass("warning");
            switch(deviceStatus) {
                case 'Ready':
                    statusClass = "success";
                    break;
                case 'Not Ready':
                    statusClass = "error";
                    break;
                case 'Unknown':
                    statusClass = "warning";
                    break;
                default :
                    break;
            }
            $('.js-status').addClass(statusClass);
        },
        
        displayMappingPopup: function(feature, overlay) {
            var geometry = feature.getGeometry(),
                coord = geometry.getCoordinates(),
                properties = feature.getProperties(),
                parent = properties.parent,
                neighbor = properties.neighbor,
                nearby = properties.nearby,
                primaryRoutesExists = $('.js-all-routes').exists(),
                allRoutesChecked = false,
                paoId = feature.get('pao').paoId;
                if (primaryRoutesExists) {
                    allRoutesChecked = $('.js-all-routes').find(':checkbox').prop('checked');
                }

            var includePrimaryRoute = neighbor != null || parent != null ? 'false' : 'true',
                url = yukon.url('/tools/map/device/' + paoId + '/info?includePrimaryRoute=' + includePrimaryRoute);
            $('#device-info').load(url, function() {
                if (nearby != null) {
                    $('.js-distance').text(nearby.distance.distance.toFixed(4) + " ");
                    $('.js-distance-display').show();
                }
                if (neighbor != null) {
                    mod.displayNeighborPopupProperties(neighbor);
                }
                if (parent != null) {
                    mod.displayParentNodePopupProperties(parent);
                }
                $('#device-info').show();
                $('#marker-info').show();
                overlay.setPosition(coord);
                var deviceStatus = $('.js-status').text();
                mod.updateDeviceStatusClass(deviceStatus);
            });
            //close any lingering delete dialogs to simplify handling
            var deleteDialog = $('#confirm-delete');
            if (deleteDialog.hasClass('ui-dialog-content')) {
                deleteDialog.dialog('destroy');
            }

            //highlight specific device primary route if viewing all routes
            if (allRoutesChecked) {
                yukon.mapping.addPrimaryRouteToMap(paoId);
            }
        },
        
        updateZoom: function(map) {
            var source = yukon.mapping.getIconLayerSource(),
                features = source.getFeatures();
            if (features != null && features.length > 0) {
                if (features.length > 1) {
                    map.getView().fit(source.getExtent(), map.getSize());
                    //zoom out just a little to make sure pins display fully
                    var currentZoom = map.getView().getZoom();
                    map.getView().setZoom(currentZoom - 0.2);
                    if (currentZoom > 16){
                        map.getView().setZoom(16);
                    }
                } else {
                    map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
                    map.getView().setZoom(13);
                }
            }
        },

        showHideElevationLayer: function(map, button) {
            var checked = button.hasClass('on');
            if (checked) {
                button.removeClass('on');
                map.removeLayer(_elevationLayer);
                _map.getLayers().forEach(function (layer) {
                    if (layer.get('name') === 'mqsat' || layer.get('name') === 'hybrid') {
                        layer.setOpacity(1);
                    }
                });
            } else {
                _elevationLayer = new ol.layer.Tile({ name: 'ele',
                    source: new ol.source.XYZ({ name: 'ele',
                        url: yg.map_devices_elevation_url,
                        attributions: _attributionText,
                        tileSize: 512,
                        transition: 0
                    })
                });
                _elevationLayer.setZIndex(500);
                button.addClass('on');
                map.addLayer(_elevationLayer);
                _map.getLayers().forEach(function (layer) {
                    if (layer.get('name') === 'mqsat' || layer.get('name') === 'hybrid') {
                        layer.setOpacity(0.4);
                    }
                });
            }
        },
        
        addFeatureToMapAndArray: function(feature, iconArray) {
            var source = yukon.mapping.getIconLayerSource(),
                pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ pao: pao });
            icon.setId(feature.id);
        
            //check if device already exists on map
            var deviceFound = yukon.mapping.findFocusDevice(pao.paoId, false);
            if (deviceFound) {
                icon = deviceFound;
            } else {
                icon.setStyle(style);
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                iconArray.push(icon);
                source.addFeature(icon);
            }
            return icon;
        },
        
        showHideAllGateways: function() {
            var checked = $('.js-all-gateways').find(':checkbox').prop('checked');
            if (!checked) {
                yukon.mapping.removeAllGatewayIcons();
            } else {
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/allGateways'),
                    type: 'get'
                }).done( function(data) {
                    for (var x in data.features) {
                        var feature = data.features[x];
                        yukon.mapping.addFeatureToMapAndArray(feature, _allGatewayIcons);
                    }
                });
            }
        },
        
        showHideAllRelays: function() {
            var checked = $('.js-all-relays').find(':checkbox').prop('checked');
            if (!checked) {
                yukon.mapping.removeAllRelayIcons();
            } else {
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/allRelays'),
                    type: 'get'
                }).done( function(data) {
                    for (var x in data.features) {
                        var feature = data.features[x];
                        yukon.mapping.addFeatureToMapAndArray(feature, _allRelayIcons);
                    }
                });
            }
        },
        
        drawChildren: function(currentNode, atRoot, parentId, gatewayId) {
            var parentFeature = yukon.mapping.getFeatureFromData(currentNode),
                currentParentId = yukon.mapping.getPaoIdFromData(currentNode),
                dashedLine = parentFeature == null,
                parentId = parentFeature ? currentParentId : parentId,
                source = yukon.mapping.getIconLayerSource();
            
            for (var x in currentNode.children) {
                if (atRoot) {
                    dashedLine = false;
                    parentId = gatewayId;
                }
                var child = currentNode.children[x];
      
                if (yukon.mapping.shouldLineBeDrawn(child)) {
                    var feature = yukon.mapping.getFeatureFromData(child);
                    if (feature != null) {
                        var icon = yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons),
                            points = [],
                            parent = source.getFeatureById(parentId);
                        icon.set('parentId', parentId);
                        points.push(icon.getGeometry().getCoordinates());
                        points.push(parent.getGeometry().getCoordinates());
                        var lineFeature = new ol.Feature({
                            geometry: new ol.geom.LineString(points),
                            name: 'Line'
                        });
                        if (dashedLine) {
                            icon.set('dashedLine', true);
                            _allRoutesDashedLineFeatures.push(lineFeature);
                        } else {
                            _allRoutesLineFeatures.push(lineFeature);
                        }
                    } 
                } 
                yukon.mapping.drawChildren(child, false, parentId, gatewayId);
            }
        },
        
        shouldLineBeDrawn: function(currentNode) {
            //check node and all children to see if any of the devices are on map
            var source = yukon.mapping.getIconLayerSource(),
                drawLine = false,
                paoId = yukon.mapping.getPaoIdFromData(currentNode);
            if (paoId != null) {
                //first check self
                if (source.getFeatureById(paoId)) {
                    return true;
                }
            }

            for (var x in currentNode.children) {
                var child = currentNode.children[x],
                    paoId = yukon.mapping.getPaoIdFromData(child);
                if (paoId != null) {
                    if (source.getFeatureById(paoId) != null) {
                        return true;
                    }
                }
                if (yukon.mapping.shouldLineBeDrawn(child)) {
                    return true;
                }
            }
            return drawLine;
        },
        
        getPaoIdFromData: function(node) {
          if (node != null && node.data != null) {
              return Object.keys(node.data)[0];
          }  
        },
        
        getFeatureFromData: function(node) {
            if (node != null && node.data != null) {
                var nodeData = Object.keys(node.data).map(function (key) {
                    return node.data[key];
                });
                if (nodeData[0] != null) {
                    return nodeData[0].features[0];
                }
            }
        },
        
        getFeatureFromRouteOrNeighborData: function(routeData) {
            if (routeData != null) {
                var features = Object.keys(routeData).map(function (key) {
                    var device = routeData[key];
                    if (device != null && device.features != null) {
                        return device.features[0];
                    }
                });
                if (features != null) {
                    return features[0];
                }
            }
        },
        
        createNeighborDevice: function(device, iconArray, lineArray, devicePoints, alwaysAddIcon) {
            var feature = yukon.mapping.getFeatureFromRouteOrNeighborData(device),
                source = yukon.mapping.getIconLayerSource();
            if (feature != null) {
                var pao = feature.properties.paoIdentifier,
                    style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                    properties = Object.keys(device).map(function (key) {
                        var neighborInfo = device[key];
                        if (neighborInfo != null && neighborInfo.properties != null) {
                            return neighborInfo.properties;
                        }
                    });
                if (properties != null) {
                    var neighbor = properties[0].neighborData;
                    neighbor.distance = properties[0].distance;
                    neighbor.commaDelimitedNeighborFlags = properties[0].commaDelimitedNeighborFlags;
                }
                
                var icon = new ol.Feature({ neighbor: neighbor, pao: pao });
                icon.setId(feature.id);
                
                //check if neighbor already exists on map
                var neighborFound = yukon.mapping.findFocusDevice(pao.paoId, false);
                if (neighborFound) {
                    icon = neighborFound;
                    icon.set("neighbor", neighbor);
                } else {
                    icon.setStyle(style);
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                    if (!alwaysAddIcon) {
                        iconArray.push(icon);
                    }
                    source.addFeature(icon);
                }
                
                if (alwaysAddIcon) {
                    iconArray.push(icon);
                }
                
                //draw line
                var points = [];
                points.push(icon.getGeometry().getCoordinates());
                points.push(devicePoints);

                var lineColor = yukon.mapping.getNeighborLineColor(neighbor.neighborData.etxBand),
                    lineThickness = yukon.mapping.getNeighborLineThickness(neighbor.neighborData.numSamples);
                
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
                
                lineArray.push(layerLines);
                _map.addLayer(layerLines);
            }
        },
        
        displayDescendants: function(deviceId, updateZoom) {
            $('.js-no-descendants-message').addClass('dn');
            $('.js-descendants-missing-locations-message').addClass('dn');
            _missingDescendants = false;
            var mapContainer = $('#map-container');
            yukon.ui.block(mapContainer);
            $.getJSON(yukon.url('/stars/comprehensiveMap/networkTree') + '?' + $.param({ deviceId: deviceId }))
            .done(function (json) {
                if (json.tree.length > 0) {
                    var gatewayNode = json.tree[0],
                        paoId = yukon.mapping.getPaoIdFromData(gatewayNode);
                    //first check if device is gateway.
                    //paoId is a String since it is retrieved from a Map and deviceId is an Integer so using == here instead of ===
                    if (paoId == deviceId) {
                        yukon.mapping.findDescendants(gatewayNode, deviceId);
                        yukon.mapping.showDescendantLines();
                    } else {
                        yukon.mapping.drawAllDescendants(deviceId, gatewayNode);
                    }
                } else {
                    $('.js-no-descendants-message').removeClass('dn');
                }
                if (json.errorMsg) {
                    yukon.ui.alertError(json.errorMsg);
                }
                yukon.ui.unblock(mapContainer);
                $('#marker-info').hide();
                if (updateZoom) {
                    yukon.mapping.updateZoom(_map);
                }
            });
        },
        
        drawAllDescendants: function(deviceId, node) {
            for (var i in node.children) {
                var childNode = node.children[i],
                    feature = yukon.mapping.getFeatureFromData(childNode);
                if (feature != null) {
                    var paoId = yukon.mapping.getPaoIdFromData(childNode);
                    //paoId is a String since it is retrieved from a Map and deviceId is an Integer so using == here instead of ===
                    if (paoId == deviceId) {
                        yukon.mapping.findDescendants(childNode, deviceId);
                        yukon.mapping.showDescendantLines();
                        break;
                    }
                }
                yukon.mapping.drawAllDescendants(deviceId, childNode);
            }
        },
        
        findDescendants: function(node, parentId) {
            var parentFeature = yukon.mapping.getFeatureFromData(node),
                currentParentId = yukon.mapping.getPaoIdFromData(node),
                dashedLine = parentFeature == null,
                source = yukon.mapping.getIconLayerSource(),
                parentId = parentFeature ? currentParentId : parentId;
            for (var i in node.children) {
                var childNode = node.children[i],
                    feature = yukon.mapping.getFeatureFromData(childNode);
                if (feature != null) {
                    var icon = yukon.mapping.addFeatureToMapAndArray(feature, _descendantIcons),
                        points = [],
                        parent = source.getFeatureById(parentId);
                    if (parent != null) {
                        points.push(icon.getGeometry().getCoordinates());
                        points.push(parent.getGeometry().getCoordinates());
                        var lineFeature = new ol.Feature({
                            geometry: new ol.geom.LineString(points),
                            name: 'Line'
                        });
                        if (dashedLine) {
                            _descendantDashedLineFeatures.push(lineFeature);
                            _missingDescendants = true;
                        } else {
                            _descendantLineFeatures.push(lineFeature);
                        }
                    }
                } else {
                    _missingDescendants = true;
                }
                yukon.mapping.findDescendants(childNode, parentId);
            }
        },
        
        showDescendantLines: function() {
            if (_descendantLineFeatures.length == 0 && _descendantDashedLineFeatures.length == 0 ){
                $('.js-no-descendants-message').removeClass('dn');
            }
            if (_missingDescendants) {
                $('.js-descendants-missing-locations-message').removeClass('dn');
            }
            
            if (_descendantLineFeatures.length > 0) {
                //draw lines
                var layerLines = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: _descendantLineFeatures
                    }),
                    style: new ol.style.Style({
                        stroke: new ol.style.Stroke({ color: _focusRouteColor, width: 2.5 })
                    })
                });
                
                layerLines.setZIndex(_lineZIndex);
                _descendantLines.push(layerLines);
                _map.addLayer(layerLines);
                _descendantLineFeatures = [];
            }
            
            if (_descendantDashedLineFeatures.length > 0) {
                //draw dashed lines
                var dashedLines = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: _descendantDashedLineFeatures
                    }),
                    style: new ol.style.Style({
                        stroke: new ol.style.Stroke({ color: _focusRouteColor, width: 2.5, lineDash: [10,10] })
                    })
                });
                
                dashedLines.setZIndex(_lineZIndex);
                _descendantLines.push(dashedLines);
                _map.addLayer(dashedLines);
                _descendantDashedLineFeatures = [];
            }
        },
        
        removeDescendantLayers: function() {
            var iconLayer = yukon.mapping.getIconLayer(),
                source = iconLayer.getSource();
            _descendantIcons.forEach(function (icon) {
                if (source.getFeatureById(icon.getId()) != null) {
                    source.removeFeature(icon);
                }
            });
            _descendantLines.forEach(function (line) {
                _map.removeLayer(line);
            });
            _descendantIcons = [];
            _descendantLines = [];
        },
                
        showHideAllRoutes: function(gatewayIds) {
            $('.js-no-location-message').addClass('dn');
            var checked = $('.js-all-routes').find(':checkbox').prop('checked');
            // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
            if ($("#js-route-details-container").exists()) {
                $("#js-route-details-container").toggleClass("vh", !checked);
            }
            if (!checked) {
                yukon.mapping.removeAllRoutesLayers();
                // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                if ($("#js-route-details-container").exists()) {
                    clearInterval(_updateNetworkTreeInterval);
                }
            } else {
                // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                if ($("#js-route-details-container").exists()) {
                    _setRouteLastUpdatedDateTime(null); // It may take few seconds to fetch the RouteLastUpdatedDateTime value. Meanwhile display test "Loading..."
                    _updateNetworkTreeInterval = setInterval(function () {
                        $.getJSON(yukon.url('/stars/comprehensiveMap/getRouteDetails'), function (response) {
                            _setRouteLastUpdatedDateTime(response.routeLastUpdatedDateTime);
                            $("#js-route-details-container").find(".js-request-route-update").attr("disabled", !response.isUpdatePossible);
                            if (response.updateRoutes) {
                                yukon.mapping.showHideAllRoutes(gatewayIds);
                            }
                        });
                    }, yg.rp.updater_delay);
                }

                var mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/comprehensiveMap/allPrimaryRoutes?gatewayIds=' + gatewayIds))
                .done(function (json) {
                    //check for error
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                        // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                        if ($("#js-route-details-container").exists()) {
                            clearInterval(_updateNetworkTreeInterval);
                        }
                    } else if (json.tree) {
                        yukon.mapping.removeAllRoutesLayers();
                        _setRouteLastUpdatedDateTime(json.routeLastUpdatedDateTime);
                        $("#js-route-details-container").find(".js-request-route-update").attr("disabled", !json.isUpdatePossible)

                        //gateway is top node
                        for (var x in json.tree) {
                            var currentNode = json.tree[x],
                                feature = yukon.mapping.getFeatureFromData(currentNode);
                            if (feature != null) {
                                var paoId = yukon.mapping.getPaoIdFromData(currentNode);
                                //if gateway is not on the map add it
                                yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons),
                                yukon.mapping.drawChildren(currentNode, true, paoId, paoId);
                            } else {
                                //this is a virtual gateway so draw children instead
                                for (var i in currentNode.children) {
                                    var childNode = currentNode.children[i],
                                        feature = yukon.mapping.getFeatureFromData(childNode);
                                    if (feature != null) {
                                        var paoId = yukon.mapping.getPaoIdFromData(childNode);
                                        yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons),
                                        yukon.mapping.drawChildren(childNode, true, paoId, paoId);
                                    }
                                }
                            }

                            if (_allRoutesLineFeatures.length > 0) {
                                //draw lines
                                var layerLines = new ol.layer.Vector({
                                    source: new ol.source.Vector({
                                        features: _allRoutesLineFeatures
                                    }),
                                    style: new ol.style.Style({
                                        stroke: new ol.style.Stroke({ color: _routeColor, width: 2.5 })
                                    })
                                });
                                
                                layerLines.setZIndex(_lineZIndex);
                                _allRoutesLines.push(layerLines);
                                _map.addLayer(layerLines);
                                _allRoutesLineFeatures = [];
                            }
                            
                            if (_allRoutesDashedLineFeatures.length > 0) {
                                
                                $('.js-no-location-message').removeClass('dn');
                                //draw dashed lines
                                var dashedLines = new ol.layer.Vector({
                                    source: new ol.source.Vector({
                                        features: _allRoutesDashedLineFeatures
                                    }),
                                    style: new ol.style.Style({
                                        stroke: new ol.style.Stroke({ color: _routeColor, width: 2.5, lineDash: [10,10] })
                                    })
                                });
                                
                                dashedLines.setZIndex(_lineZIndex);
                                _allRoutesLines.push(dashedLines);
                                _map.addLayer(dashedLines);
                                _allRoutesDashedLineFeatures = [];
                            }
                            
                        }
                    }
                }).always(function () {
                    yukon.ui.unblock(mapContainer);
                });
            }
        },
        
        removeAllGatewayIcons: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allGatewayIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allGatewayIcons = [];
        },
        
        removeAllRelayIcons: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allRelayIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allRelayIcons = [];
        },
        
        removeAllRoutesLayers: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allRoutesIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allRoutesLines.forEach(function (line) {
                _map.removeLayer(line);
            });
            _allRoutesIcons = [];
            _allRoutesLines = [];
            
            _map.removeLayer(_devicePrimaryRouteLayer);
            _map.removeLayer(_devicePrimaryRouteDashedLayer);
        },
        
        adjustMapForFullScreenModeChange: function(mapContainer, paddingTop) {
            // we if are doing an exit from the full screen, close any open pop-ups
            if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                $(".ui-dialog-content").dialog("close");
                if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                    $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                }
                //move all dropdowns back to the body
                var menus = $('div.ol-viewport').find('.dropdown-menu');
                $('body').prepend(menus);
                //adjust height back
                mapContainer.css('padding-top', '0px');
            } else {
                //adjust height for mapping buttons
                mapContainer.css('padding-top', paddingTop);
                mapContainer.css('padding-bottom', '0px');
                
                //move any dropdowns from body to viewport
                var menus = $('body').children('.dropdown-menu');
                $('div.ol-viewport').prepend(menus);
            }
            //close any popups
            $('#marker-info').hide();
            mod.updateZoom(_map);
            _map.updateSize();
        },
        
        addPrimaryRouteToMap: function(deviceId) {
            var currentDevice = yukon.mapping.findFocusDevice(deviceId, false),
                currentPoints = currentDevice.getGeometry().getCoordinates(),
                parentId = currentDevice.get('parentId'),
                routeLineWidth = 2.5,
                primaryRoutePreviousPoints = null,
                routeFeatures = [],
                routeDashedFeatures = [];

            _map.removeLayer(_devicePrimaryRouteLayer);
            _map.removeLayer(_devicePrimaryRouteDashedLayer);
            
            while (parentId != null) {
                var parentDevice = yukon.mapping.findFocusDevice(parentId, false),
                    dashedLine = currentDevice.get('dashedLine'),
                    points = [];
                
                points.push(parentDevice.getGeometry().getCoordinates());
                if (primaryRoutePreviousPoints != null) {
                    points.push(primaryRoutePreviousPoints);
                } else {
                    points.push(currentPoints);
                }
                
                var lineFeature = new ol.Feature({
                    geometry: new ol.geom.LineString(points),
                    name: 'Line'
                })
                
                if (dashedLine){
                    routeDashedFeatures.push(lineFeature);
                } else {
                    routeFeatures.push(lineFeature);
                }
                
                primaryRoutePreviousPoints = parentDevice.getGeometry().getCoordinates();
                parentId = parentDevice.get('parentId');
                currentDevice = parentDevice;
            }
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: routeFeatures
                }),
                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: _highlightRouteColor, width: routeLineWidth })
                })
            });
            
            layerLines.setZIndex(_deviceLineZIndex);
            _map.addLayer(layerLines);
            _devicePrimaryRouteLayer = layerLines;
            
            if (routeDashedFeatures) {
                var dashedLines = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: routeDashedFeatures
                    }),
                    style: new ol.style.Style({
                        stroke: new ol.style.Stroke({ color: _highlightRouteColor, width: routeLineWidth, lineDash: [10,10] })
                    })
                });
                dashedLines.setZIndex(_deviceLineZIndex);
                _map.addLayer(dashedLines);
                _devicePrimaryRouteDashedLayer = dashedLines;
            }
            
        },

    };
 
    return mod;
})();
 
$(function () { yukon.mapping.init(); });