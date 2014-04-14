
/**
 * Singleton that manages the Device Mapping feature
 * 
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */

yukon.namespace('yukon.collection.map');

yukon.collection.map = (function () {
    
    var
    mod = {},
    initialized = false,
    map = {},
    
    _buildIconArray = function(dest_projection) {
        var 
        icons = [],
        locations = yukon.fromJson('#locations');
        
        for (loc in locations) {
            var 
            location = locations[loc],
            iconFeature = new ol.Feature({
                geometry: new ol.geom.Point(ol.proj.transform([location.longitude, location.latitude], 'EPSG:4326', dest_projection)),
                paoId: location.paoIdentifier.paoId
            }),
            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
                    anchor: [0.5, 0.5],
                    opacity: 1.0,
                    src: yukon.url('/WebConfig/yukon/Icons/electric_16.png')
                }))
            });

            iconFeature.setStyle(iconStyle);
            
            icons.push(iconFeature);
        }
        
        return icons;
    };

    mod = {
            
        init: function() {

            if (initialized) {
                return;
            }
            
            var vectorSource = new ol.source.Vector({}),
                iconLayer = new ol.layer.Vector({ source: vectorSource }),
                osmLayer = new ol.layer.Tile({ source: new ol.source.OSM({ layer: 'sat' }) });
            
            map = new ol.Map({
                controls: ol.control.defaults().extend([new ol.control.FullScreen()]),
                target: 'map',
                layers: [osmLayer, iconLayer],
                view: new ol.View2D({
                    center: [0, 0],
                    zoom: 9
                })
            });
            
            var dest_projection = mod.getMap().getView().getProjection().getCode();
            map.getLayers().getAt(1).getSource().addFeatures(_buildIconArray(dest_projection));
            map.getView().setCenter(ol.proj.transform([-93.557708, 45.254846], 'EPSG:4326', dest_projection));
            
            initialized = true;
        },

        getMap: function () {
            return map;
        },
        
        changeIcon: function () {
            
            var 
            dest_projection = mod.getMap().getView().getProjection().getCode(),
            iconFeature = new ol.Feature({
                geometry: new ol.geom.Point(ol.proj.transform([-93.557708, 45.254846], 'EPSG:4326', dest_projection))
            }),
            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [0.5, 0.5],
                    opacity: 1.0,
                    src: yukon.url('/WebConfig/yukon/Icons/gas_16.png')
                }))
            });

            iconFeature.setStyle(iconStyle);
            var source = mod.getMap().getLayers().getAt(1).getSource();
            source.removeFeature(source.getFeatures()[0]);
            source.addFeature(iconFeature);
        }
    };
    
    return mod;
})();

$(function() { yukon.collection.map.init(); });