
yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.iconChooser');

yukon.dr.iconChooser = (function () {
    var mod,
        _iconFilenames = function (id, fileNames) {
            jQuery('#' + id + 'IconInput').data(id + 'data', fileNames);
        },
        _initSelected = function (id, selected) {
            var iconInput,
                iconInputVal;
            iconInput = jQuery('#' + id + 'IconInput');
            if (selected === 'OTHER') {
                iconInput.prop('disabled', false);
                iconInputVal = iconInput.val();
                jQuery('#' + id + 'IconPreviewImg').attr('src', jQuery('#' + id + 'IconInput').data(id + 'baseDir') + iconInputVal);
                jQuery('#' + id + 'HiddenIconInput').val(iconInputVal);
                jQuery('#' + id + 'IconPreviewImg').show();
                iconInput.focus();
            } else {
                iconInput.prop('disabled', true);
                if (selected === 'NONE') {
                    jQuery('#' + id + 'IconPreviewImg').hide();
                    jQuery('#' + id + 'HiddenIconInput').val('');
                } else {
                    iconInput.val(iconInput.data(id + 'data')[selected]);
                    jQuery('#' + id + 'IconPreviewImg').attr('src',
                        iconInput.data(id + 'baseDir') +
                        iconInput.data(id + 'data')[selected]);
                    jQuery('#' + id + 'IconPreviewImg').show();
                    jQuery('#' + id + 'HiddenIconInput').val(iconInput.data(id + 'data')[selected]);
                }
            }
        },
        _initEventListeners = function (id) {
            jQuery('#' + id + 'IconInput').on('keyup blur', function (event) {
                mod.iconInputChanged(id);
            });
            jQuery('#' + id + 'IconSelect').on('change', function (event) {
                var selected = jQuery('#' + id + 'IconSelect').val();
                _initSelected(id, selected);
            });
            jQuery('#' + id + 'IconPreviewImg')
                .load(function() {return _afterImageLoad(id, true);})
                .error(function() {return _afterImageLoad(id, false);});
        },
        _afterImageLoad = function(id, didLoad) {
            var isCustomImage = jQuery('#' + id + 'IconSelect').val() === 'OTHER',
                iconInputField  = jQuery('#' + id + 'IconInput'),
                imagePreview= jQuery('#' + id + 'IconPreviewImg');

            iconInputField.removeClass('success error');
            if (didLoad) {
                imagePreview.show();
                if (isCustomImage) {
                    iconInputField.addClass('success');
                }
            } else {
                imagePreview.hide();
                if (isCustomImage) {
                    iconInputField.addClass('error');
                }
            }
        };
    mod = {
        init : function (id, baseDir, fileNames) {
            var elemObj = jQuery('#' + id + 'IconInput');
            elemObj.data('timeout', false);
            elemObj.data(id + 'baseDir', baseDir);
            _iconFilenames(id, fileNames);
            elemObj.val(jQuery('#' + id + 'HiddenIconInput').val());
            _initEventListeners(id);
            _initSelected(id, jQuery('#' + id + 'IconSelect').val());
        },
        iconInputChanged : function (id) {
            var iconInput = jQuery('#' + id + 'IconInput'),
                baseDir = iconInput.data(id + 'baseDir'),
                srcString = baseDir + iconInput.val(),
                updatePreview = function () {
                    iconInput.data('timeout', false);
                    jQuery('#' + id + 'HiddenIconInput').val(iconInput.val());
                    jQuery('#' + id + 'IconPreviewImg').attr('src', baseDir + iconInput.val());
                },
                quietDelay,
                previewImgSrcAttr = jQuery('#' + id + 'IconPreviewImg').attr('src'),
                lenPreviewImgSrc,
                lenSrcString,
                position;
            lenPreviewImgSrc = ('undefined' === typeof previewImgSrcAttr ? 0 : previewImgSrcAttr.length);
            lenSrcString = srcString.length;
            if ('undefined' !== typeof previewImgSrcAttr) {
                position = previewImgSrcAttr.indexOf(srcString);
                // if previewImgSrcAttr ends with srcString...
                if (-1 !== position && (lenPreviewImgSrc - lenSrcString) === position) {
                    return;
                }
            }
            if (iconInput.data('timeout')) {
                clearTimeout(iconInput.data('timeout'));
            }
            quietDelay = 300;
            iconInput.data('timeout', setTimeout(updatePreview, quietDelay));
        }
    };
    return mod;
})();