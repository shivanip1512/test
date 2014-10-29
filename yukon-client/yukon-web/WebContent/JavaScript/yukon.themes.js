yukon.namespace('yukon.themes');

/**
 * Module that manages the Yukon theming feature
 * @module   yukon.themes
 * @requires JQUERY
 * @requires JQUERY_UI
 * @requires JQUERY_UI_WIDGET
 * @requires JQUERY_IFRAME_TRANSPORT
 * @requires JQUERY_FILE_UPLOAD
 * @requires JQUERY_SPECTRUM
 */
yukon.themes = (function () {
    
    var initialized = false,
        _chooseText = '',
        _okText = '',
        _cancelText = '',
        mod;

    mod = {
            
        init: function(cfg) {

            var paletteMapObjects = $('#palette-map').data('paletteMap'),
                paletteObjectId,
                paletteCells,
                popups = $('div[data-image-picker]'),
                colors;

            // Move popup divs up to the body so we don't 
            // end up putting a form inside another form.
            popups.each(function(idx, elem) {
                $('body').prepend(elem); // Prepend with move, not clone the element.
            });

            /** Localized button text */
            _chooseText = cfg.chooseText;
            _okText = cfg.okText;
            _cancelText = cfg.cancelText;

            if (initialized) {
                return;
            }

            /** Initialize each color picker */ 
            $('.js-color-input').each(function (idx, item) {
                var item = $(item),
                    color = item.val();
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

            /** Delete the theme */
            $('#b-delete').click(function(e) {
                $('input[name=_method]').val('DELETE');
                $('#theme-form').submit();
            });

            /** Show an image picker */
            $('a[data-image-picker]').click(function(e) {

                var link = $(e.currentTarget),
                    href = link.attr('href'),
                    popup = $('div[data-image-picker=' + link.attr('data-image-picker') + ']');

                popup.load(href, function() {
                    var buttons = [],
                        okButton = {'text' : _okText, 'click': function() { popup.trigger('yukon.image.selected'); }, 'class': 'primary action'},
                        cancelButton = {'text' : _cancelText, 'click' : function() { $(this).dialog('close'); }},
                        imagePicker,
                        selected,
                        first,
                        category = link.data('imageCategory');

                    mod.initFileUpload(category);
                    buttons.push(cancelButton);
                    buttons.push(okButton);

                    popup.dialog({ autoOpen: false,
                                   height: 'backgrounds' === category ? 650 : 500,
                                   width: 700,
                                   modal : false,
                                   buttons : buttons });

                    popup.dialog('open');

                    // Move selected image to beginning of list
                    imagePicker = popup.find('.image-picker');

                    // Avoid breakage if only one image in the list
                    if (1 < imagePicker.find('.image').length) {
                        selected = imagePicker.find('.selected').parent().remove();
                        first = $(imagePicker.find('.image')[0]).parent();
                        selected.insertBefore(first);
                    }
                });

                return false;
            });

            // If i don't listen for this event it doesn't work, God knows why.
            $(document).on('show.spectrum', 'input', function(e) {
                e.preventDefault();
            });

            /** Set an image as selected */
            $(document).on('click', '.image-picker .image', function(e) {
                var selected = $(e.currentTarget),
                imagePicker = selected.closest('.image-picker');

                e.preventDefault();
                imagePicker.find('.image').removeClass('selected');
                selected.addClass('selected');
            });

            /** New image was choosen */
            $(document).on('yukon.image.selected', '[data-image-picker]', function(e) {
                var imgPicker = $(e.currentTarget),
                    selected = imgPicker.find('.image.selected').data('imageId'),
                    input = $('#' + imgPicker.data('imagePicker')),
                    link = input.next();
                    
                imgPicker.dialog('close');
                input.val(selected);
                link.attr('href', yukon.url('/adminSetup/config/themes/imagePicker?category=logos&selected=' + selected));
                link.find('img').attr('alt', selected).attr('src', yukon.url('/common/images/' + selected + '/thumb'));
            });

            /** Build pallet icon for each theme using its colors */
            for (paletteObjectId in paletteMapObjects) {
                paletteCells = $('table[data-theme="' + paletteObjectId + '"] td');
                colors = paletteMapObjects[paletteObjectId];
                paletteCells.each(function (index, el) {
                    $(el).css('backgroundColor', colors[index]);
                });
            }

            /** Delete image button handler  */
            $(document).on('click', '.delete-image', function (e) {
                var button = $(e.currentTarget),
                    okcancel;

                e.preventDefault();
                okcancel = button.closest('.page-action-area').find('.js-delete-confirm');
                okcancel.removeClass('dn');
            });

            /** Delete image button confirm handler, attempt to delete the image */
            $(document).on('click', '.js-delete-ok', function (e) {
                var button = $(this),
                    imageParent = button.closest('.section'),
                    imageId = button.closest('.image').data('imageId'),
                    imagePicker = button.closest('.image-picker'),
                    originalImageId = imagePicker.data('originalImageId');
                
                e.preventDefault();
                $.ajax({
                  url: yukon.url('/common/images/' + imageId),
                  type: 'post',
                  // _method is needed by the HiddenHttpMethodFilter which allows us to use other methods like DELETE and PUT
                  data: { _method: 'DELETE'}
                }).done(function (data, textStatus, jqXHR) {
                    var deleteConfirm = $('.js-delete-confirm');
                    if (data.success === true) {
                        imageParent.toggle('fade', function() {
                            imageParent.remove();
                        });
                        imagePicker.find('[data-image-id=' + originalImageId + ']').addClass('selected');
                    } else {
                        deleteConfirm.html(data.message);
                        deleteConfirm.find('.button').addClass('dn');
                    }
                });
            });

            /** Delete image button confirm cancel handler */
            $(document).on('click', '.cancel', function (e) {
                var button = $(e.currentTarget);
                e.preventDefault();
                button.closest('.js-delete-confirm').addClass('dn');
            });
            initialized = true;
        },

        /** Initialize the file uploaders for uploading images. Uses a jquery file upload plugin. */
        initFileUpload: function (category) {

            var uploadForm = $('div[data-category="' + category + '"] form'),
                token = $('#csrf-token').val();

            uploadForm.fileupload({
                dataType: 'text',
                start: function (e) {
                    var bar = uploadForm.find('input').next();
                    if (typeof bar !== 'undefined') {
                        $(bar).progressbar({max:100, value: 0});
                    }
                },
                done: function (e, data) {
                    var dataParsed = JSON.parse(data.result),
                        uploadArea,
                        copy;
                    data.result = dataParsed;
                    uploadForm.find('label.uploadLabel').css('display', 'none');
                    if (data.result.status === 'success') {
                        uploadArea = uploadForm.find('input').closest('.section');
                        copy = uploadArea.next().clone();

                        uploadArea.closest('.image-picker').find('.image.selected').removeClass('selected');
                        copy.find('.image').addClass('selected').attr('data-image-id', data.result.image.id);
                        copy.find('.js-name-value').text(data.result.image.name);
                        copy.find('.js-category-value').text(data.result.image.category);
                        copy.find('.js-size-value').text(data.result.image.size);
                        copy.find('.simple-input-image img').attr('alt', data.result.image.name);
                        copy.find('.simple-input-image img').attr('src', yukon.url('/common/images/') + data.result.image.id + '/thumb');

                        copy.insertAfter(uploadArea);
                    } else {/* ignore for now */}
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10),
                        button = uploadForm.find('input'),
                        bar = $(button.next()),
                        percent = $(bar.next());
                    bar.progressbar("value", progress);
                    percent.text(progress + "%");
                }
            });
            uploadForm.fileupload('option', 'formData', {'category': category, 'com.cannontech.yukon.request.csrf.token': token});
        }
    };
    return mod;
})();

$(function() { yukon.themes.init($('#button-keys').data('buttonKeys')); });
