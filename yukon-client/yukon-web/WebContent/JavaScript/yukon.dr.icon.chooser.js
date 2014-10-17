
yukon.namespace('yukon.dr.iconChooser');

/**
 * Singleton that manages the demand response icons.
 * 
 * @module yukon.tools.map
 * @requires JQUERY
 */
yukon.dr.iconChooser = (function () {
    var mod,
    
	    /**
    	 * Returns file names of the icons.
	     * @param {string} id -  Id.
	     * @param {Object} fileNames - List of file names.
	     */
        _iconFilenames = function (id, fileNames) {
            $('#' + id + 'IconInput').data(id + 'data', fileNames);
        },
 
 	   /**
    	 * Initialize the selected icons.
	     * @param {string} id -  Id.
	     * @param {Object} selected - selected options
	     */
        _initSelected = function (id, selected) {
            var iconInput,
                iconInputVal;
            iconInput = $('#' + id + 'IconInput');
            if (selected === 'OTHER') {
                iconInput.prop('disabled', false);
                iconInputVal = iconInput.val();
                $('#' + id + 'IconPreviewImg').attr('src', $('#' + id + 'IconInput').data(id + 'baseDir') + iconInputVal);
                $('#' + id + 'HiddenIconInput').val(iconInputVal);
                $('#' + id + 'IconPreviewImg').show();
                iconInput.focus();
            } else {
                iconInput.prop('disabled', true);
                if (selected === 'NONE') {
                    $('#' + id + 'IconPreviewImg').hide();
                    $('#' + id + 'HiddenIconInput').val('');
                } else {
                    iconInput.val(iconInput.data(id + 'data')[selected]);
                    $('#' + id + 'IconPreviewImg').attr('src',
                        iconInput.data(id + 'baseDir') +
                        iconInput.data(id + 'data')[selected]);
                    $('#' + id + 'IconPreviewImg').show();
                    $('#' + id + 'HiddenIconInput').val(iconInput.data(id + 'data')[selected]);
                }
            }
        },
   
   		 /**
    	 * Initialize the Event listeners for icons.
	     * @param {string} id -  Id.
	     */
        _initEventListeners = function (id) {
            $('#' + id + 'IconInput').on('keyup blur', function (event) {
                mod.iconInputChanged(id);
            });
            $('#' + id + 'IconSelect').on('change', function (event) {
                var selected = $('#' + id + 'IconSelect').val();
                _initSelected(id, selected);
            });
            $('#' + id + 'IconPreviewImg')
                .load(function() {return _afterImageLoad(id, true);})
                .error(function() {return _afterImageLoad(id, false);});
        },
	
	    /**
    	 * Initialize the selected icons.
	     * @param {string} id -  Id.
	     * @param {boolean} didLoad - Indicator to indicate if the icon is loaded
	     */
        _afterImageLoad = function(id, didLoad) {
            var isCustomImage = $('#' + id + 'IconSelect').val() === 'OTHER',
                iconInputField  = $('#' + id + 'IconInput'),
                imagePreview= $('#' + id + 'IconPreviewImg');

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
    
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init : function (id, baseDir, fileNames) {
            var elemObj = $('#' + id + 'IconInput');
            elemObj.data('timeout', false);
            elemObj.data(id + 'baseDir', baseDir);
            _iconFilenames(id, fileNames);
            elemObj.val($('#' + id + 'HiddenIconInput').val());
            _initEventListeners(id);
            _initSelected(id, $('#' + id + 'IconSelect').val());
        },
    
        /** Check the input of icon if has changed  */
        iconInputChanged : function (id) {
            var iconInput = $('#' + id + 'IconInput'),
                baseDir = iconInput.data(id + 'baseDir'),
                srcString = baseDir + iconInput.val(),
                updatePreview = function () {
                    iconInput.data('timeout', false);
                    $('#' + id + 'HiddenIconInput').val(iconInput.val());
                    $('#' + id + 'IconPreviewImg').attr('src', baseDir + iconInput.val());
                },
                quietDelay,
                previewImgSrcAttr = $('#' + id + 'IconPreviewImg').attr('src'),
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