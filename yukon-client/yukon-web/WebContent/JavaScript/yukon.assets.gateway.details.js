yukon.namespace('yukon.assets.gateway.details');
 
/**
 * Module to handle the gateway details page.
 * 
 * @module yukon.assets.gateway.details
 * @requires yukon
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.assets.gateway.details = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
    _tiles = [ 
        new ol.layer.Tile({ name: 'mqosm', source: new ol.source.MapQuest({ layer: 'osm' }) }),
        new ol.layer.Tile({ name: 'mqsat', source: new ol.source.MapQuest({ layer: 'sat' }), visible: false }),
        new ol.layer.Group({
            name: 'hybrid',
            layers: [
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'sat'}) }),
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'hyb'}) })
            ],
            visible: false
        })
    ],
    
    /** 
     * Gets pao location as geojson format and adds an icon feature to the vector layer for the map.
     */
    _loadIcon = function() {
        var source = _map.getLayers().getArray()[3].getSource(),
            fc = yukon.fromJson('#gateway-geojson'),
            feature = fc.features[0],
            src_projection = fc.crs.properties.name,
            pao = feature.properties.paoIdentifier,
            icon = new ol.Feature({ pao: pao });
        
        icon.setStyle(new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png'), 
                anchor: [0.5, 1.0] 
            }) 
        }));
        
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        source.addFeature(icon);
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            /** Initialize map if we have a location. */
            if ($('#gateway-location').data('hasLocation') === 'true') {
                
                /** Setup the openlayers map. */
                _map = new ol.Map({
                    controls: [
                        new ol.control.Attribution(),
                        new ol.control.FullScreen(),
                        new ol.control.Zoom() 
                    ],
                    layers: _tiles,
                    target: 'gateway-location',
                    view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
                });
                _destProjection = _map.getView().getProjection().getCode();
                _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
                
                /** Add class to fix height when fullscreen */
                _map.on('propertychange', function(ev) {
                    var isFullscreen = $('.ol-full-screen-true').length === 1;
                    
                    if (ev.key === 'size') {
                        $('#gateway-location').toggleClass('fullscreen', isFullscreen);
                    }
                });
                
                /** Load icon for location */
                _loadIcon();
                
                /** Change map tiles layer on tile button group clicks. */
                $('#map-tiles button').click(function (ev) {
                    $(this).siblings().removeClass('on');
                    $(this).addClass('on');
                    for (var i in _tiles) {
                        var layer = $(this).data('layer');
                        _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                    }
                    
                });
            }
            
            /** Delete this gateway. */
            $(document).on('yukon:assets:gateways:delete', function (ev) {
                $('#delete-gw-form').submit();
            });
            
            
            /** Edit popup was opened, adjust test connection buttons. */
            $(document).on('yukon:assets:gateway:edit:load', function (ev) {
                yukon.assets.gateway.shared.adjustTestConnectionButtons();
            });
            
            /** Save button clicked on edit popup. */
            $(document).on('yukon:assets:gateway:save', function (ev) {
                
                var btns = $('#gateway-create-popup').closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                $('#gateway-create-popup').find('.user-message').remove();
                
                $('#gateway-settings-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        $('#gateway-edit-popup').dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        $('#gateway-edit-popup').html(xhr.responseText);
                        yukon.assets.gateway.shared.adjustTestConnectionButtons();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            _initialized = true;
        },
        
        getMap: function () {
            return _map;
        }

    };
 
    return mod;
})();
 
$(function () { yukon.assets.gateway.details.init(); });