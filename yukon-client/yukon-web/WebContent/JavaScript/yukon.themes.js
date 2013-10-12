
/**
 * Singleton that manages the javascript for Yukon themes
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 * @requires JQUERY_UI_WIDGET
 * @requires JQUERY_IFRAME_TRANSPORT
 * @requires JQUERY_FILE_UPLOAD
 * @requires JQUERY_SPECTRUM
 */

var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.Themes');

Yukon.Themes = (function () {
    
    var initialized = false,
        _chooseText = '',
        _okText = '',
        _cancelText = '',
        themeMod;

    themeMod = {
            
        init: function(cfg) {
            
            _chooseText = cfg.chooseText;
            _okText = cfg.okText;
            _cancelText = cfg.cancelText;
            
            if (initialized) {
                return;
            }
            
            jQuery('.f-color-input').each(function(idx, item) {
                var item = jQuery(item);
                var color = item.val();
                item.spectrum({
                    color: color,
                    showInput: true,
                    className: "color-picker",
                    chooseText: _chooseText,
                    showInitial: true,
                    showAlpha: true,
                    showPalette: true,
                    showSelectionPalette: true,
                    maxPaletteSize: 10,
                    preferredFormat: "hex",
                    localStorageKey: "yukon.spectrum",
                    change: function() {},
                    palette: [
                        ["rgb(0, 0, 0)", "rgb(67, 67, 67)", "rgb(102, 102, 102)",
                        "rgb(204, 204, 204)", "rgb(217, 217, 217)","rgb(255, 255, 255)"],
                        ["rgb(152, 0, 0)", "rgb(255, 0, 0)", "rgb(255, 153, 0)", "rgb(255, 255, 0)", "rgb(0, 255, 0)",
                        "rgb(0, 255, 255)", "rgb(74, 134, 232)", "rgb(0, 0, 255)", "rgb(153, 0, 255)", "rgb(255, 0, 255)"], 
                        ["rgb(230, 184, 175)", "rgb(244, 204, 204)", "rgb(252, 229, 205)", "rgb(255, 242, 204)", "rgb(217, 234, 211)", 
                        "rgb(208, 224, 227)", "rgb(201, 218, 248)", "rgb(207, 226, 243)", "rgb(217, 210, 233)", "rgb(234, 209, 220)", 
                        "rgb(221, 126, 107)", "rgb(234, 153, 153)", "rgb(249, 203, 156)", "rgb(255, 229, 153)", "rgb(182, 215, 168)", 
                        "rgb(162, 196, 201)", "rgb(164, 194, 244)", "rgb(159, 197, 232)", "rgb(180, 167, 214)", "rgb(213, 166, 189)", 
                        "rgb(204, 65, 37)", "rgb(224, 102, 102)", "rgb(246, 178, 107)", "rgb(255, 217, 102)", "rgb(147, 196, 125)", 
                        "rgb(118, 165, 175)", "rgb(109, 158, 235)", "rgb(111, 168, 220)", "rgb(142, 124, 195)", "rgb(194, 123, 160)",
                        "rgb(166, 28, 0)", "rgb(204, 0, 0)", "rgb(230, 145, 56)", "rgb(241, 194, 50)", "rgb(106, 168, 79)",
                        "rgb(69, 129, 142)", "rgb(60, 120, 216)", "rgb(61, 133, 198)", "rgb(103, 78, 167)", "rgb(166, 77, 121)",
                        "rgb(91, 15, 0)", "rgb(102, 0, 0)", "rgb(120, 63, 4)", "rgb(127, 96, 0)", "rgb(39, 78, 19)", 
                        "rgb(12, 52, 61)", "rgb(28, 69, 135)", "rgb(7, 55, 99)", "rgb(32, 18, 77)", "rgb(76, 17, 48)"]
                    ]
                });
            });
            
            jQuery('#b-delete').click(function(e) {
                jQuery('input[name=_method]').val('DELETE');
                jQuery('#theme-form').submit();
            });
            
            jQuery('a[data-image-picker]').click(function(e) {
                
                var link = jQuery(e.currentTarget),
                    href = link.attr('href'),
                    popup = jQuery('div[data-image-picker=' + link.attr('data-image-picker') + ']');
                
                popup.load(href, function() {
                    var buttons = [],
                        okButton = {'text' : _okText, 'click': function() { popup.trigger('yukon.image.selected'); }, 'class': 'primary action'},
                        cancelButton = {'text' : _cancelText, 'click' : function() { jQuery(this).dialog('close'); }};
                    
                    buttons.push(cancelButton);
                    buttons.push(okButton);
                    popup.dialog({ autoOpen: false,
                                   height: 400, 
                                   width:600,
                                   modal : false,
                                   buttons : buttons });
                    popup.dialog('open');
                });
                
                return false;
            });
            
            jQuery('#file-upload').fileupload({
                dataType: 'json',
                start: function (e) {
                    var bar = jQuery('#file-upload input').data('button').next();
                    jQuery(bar).progressbar({max:100, value: 0});
                },
                done: function (e, data) {
                    if (data.result.status == 'success') {
                        var uploadArea = jQuery(jQuery('#file-upload input').data('button')).parent(),
                            copy = uploadArea.next().clone();
                        
                        uploadArea.closest('.image-picker').find('.image').removeClass('selected');
                        copy.addClass('selected');
                        
                        copy.find('.image').attr('data-image-id', data.result.image.id);
                        copy.find('.f-name-value').text(data.result.image.name);
                        copy.find('.f-category-value').text(data.result.image.category);
                        copy.find('.f-size-value').text(data.result.image.size);
                        copy.find('.simple-input-image img').attr('alt', data.result.image.name);
                        copy.find('.simple-input-image img').attr('src', '/common/images/' + data.result.image.id);
                        
                        copy.insertAfter(uploadArea);
                    } else {
                        console.log('upload fail: ' + data.result.message);
                    }
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10),
                        button = jQuery('#file-upload input').data('button'),
                        bar = jQuery(button.next()),
                        percent = jQuery(bar.next());
                    bar.progressbar("value", progress);
                    percent.text(progress + "%");
                }
            });
            
            jQuery(document).on('click', '.b-upload', function(e) {
                var category = jQuery(e.currentTarget).closest('.image-picker').data('category');
                jQuery('#file-upload input').data('button', e.currentTarget);
                jQuery('#file-upload').fileupload('option', 'formData', {'category': category});
                jQuery('#file-upload input').trigger('click');
            });
            
            // if i don't listen for this event it doesn't work, god knows why
            jQuery(document).on('show.spectrum', 'input', function(e) {
                e.preventDefault();
            });
            
            jQuery(document).on('click', '.image-picker .image', function(e) {
                e.preventDefault();
                var selected = jQuery(e.currentTarget),
                    imagePicker = selected.closest('.image-picker');
                
                imagePicker.find('.image').removeClass('selected');
                selected.addClass('selected');
            });
            
            jQuery(document).on('yukon.image.selected', '[data-image-picker]', function(e) {
                var imgPicker = jQuery(e.currentTarget),
                    selected = imgPicker.find('.image.selected').data('imageId'),
                    input = jQuery('#' + imgPicker.data('imagePicker')),
                    link = input.next();
                    
                imgPicker.dialog('close');
                input.val(selected);
                link.attr('href', '/adminSetup/config/themes/imagePicker?category=logos&selected=' + selected);
                link.find('img').attr('alt', selected).attr('src', '/common/images/' + selected);
            });
            
            initialized = true;
        }

    };
    return themeMod;
}());