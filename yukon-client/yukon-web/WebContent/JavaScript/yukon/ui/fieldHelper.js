/*
 * Requirements: 
 *      - jQuery 1.6+
 *      - /JavaScript/yukon/ui/general.js [for the trigger()]
 * 
 * Singleton for UI functionality editing focusableFieldHolders
 */

if(typeof(Yukon) === "undefined"){
    Yukon = {};
    
    //@todo: use content loader to load the Yukon singleton defined in general.js
}

if(typeof(Yukon.FieldHelper) === "undefined"){
    Yukon.FieldHelper = {
         _initialized: false,
         _timeout: null,
         _timeoutArgs: null,
         
         //Initializer
         init: function(){
             if(!this._initialized){
                 var self = Yukon.FieldHelper;
                 
                 //setup select elements
                 jQuery('.focusableFieldHolder select').bind('blur', self.blurSelect);
                 jQuery('.focusableFieldHolder select').bind('change', self.focusSelect);
                 jQuery('.focusableFieldHolder select').bind('focus', self.focusSelect);
                 jQuery('.focusableFieldHolder select').bind('active', self.focusSelect);
                 jQuery('.focusableFieldHolder select').bind('mouseenter', self.showTooltip);
                 jQuery('.focusableFieldHolder select').bind('mouseleave', self.blurSelect);
                 
                 //setup input elements
                 jQuery('.focusableFieldHolder input').bind('blur', self.blurInput);
                 jQuery('.focusableFieldHolder input').bind('change', self.blurInput);
                 jQuery('.focusableFieldHolder input').bind('focus', self.focusInput);
                 jQuery('.focusableFieldHolder input').bind('mouseenter', self.showTooltip);
//                 jQuery('.focusableFieldHolder input').bind('mouseleave', self.blurInput);
                 
                 //trigger a blur event on each element -> performs an initial render
                 jQuery('.focusableFieldHolder select, .focusableFieldHolder input').trigger('blur');
                 this._initialized = true;
             }
         },
         
         blurInput: function(event){
             var inputField = jQuery(event.currentTarget);
             var defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
             jQuery('#descriptionPopup').hide();
             if (!defaultField.length) {
                 return;
             }
             if (inputField.val() == defaultField.val() || inputField.val() == "") {
                 inputField.removeClass('usingNonDefaultValue');
                 inputField.val(defaultField.val());
             } else {
                 inputField.addClass('usingNonDefaultValue');
             }
         },
         
         blurSelect: function(event){
             var inputField = jQuery(event.currentTarget);
             var defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
             jQuery('#descriptionPopup').hide();
             if (!defaultField.length) {
                 return;
             }
             if (inputField.val() == defaultField.val()) {
                 inputField.removeClass('usingNonDefaultValue');
             } else {
                 inputField.addClass('usingNonDefaultValue');
             }
         },
         
         focusInput: function(event){
        	 Yukon.FieldHelper.showPointingPopup(event);
             var inputField = jQuery(event.currentTarget);
             var defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
             inputField.removeClass('usingNonDefaultValue');
             if (!defaultField.length) {
                 return;
             }
             if (inputField.val() == defaultField.val()) {
                 inputField.val("");
             }
         },

         showTooltip: function(event){
        	 Yukon.FieldHelper._timeoutArgs = event;
        	 clearTimeout(Yukon.FieldHelper._timeout);
        	 Yukon.FieldHelper._timeout = setTimeout('Yukon.FieldHelper.showPointingPopup(Yukon.FieldHelper._timeoutArgs)', 400);
         },
         
         //just show a popup and remove the class name
         focusSelect: function(event){
             Yukon.FieldHelper.showPointingPopup(event);
             jQuery(event.currentTarget).removeClass('usingNonDefaultValue');
         },
         
         showPointingPopup: function(event){
             var popup = jQuery("#descriptionPopup");
             
             if(!popup.length){
                 var popupString = [];
                 popupString.push('<div class="pointingPopup_container" id="descriptionPopup" style="display:none;">');
                     popupString.push('<div class="pointingPopup_chevron ov pr">');
                     popupString.push('</div>');
                     popupString.push('<div class="pointingPopup_content" id="descriptionPopup_content">');
                     popupString.push('</div>');
                 popupString.push('</div>');
                 
                 document.body.insert(popupString.join(''));
             }
             
             var target = jQuery(event.currentTarget);
             var popupLeft = target.offset().left + target.width() + 4;
             var left = popupLeft + 'px';
             var top = (target.offset().top -20) + 'px';
             
             jQuery('#descriptionPopup').css({left:left, top:top});
             
             var fieldDesc = target.closest('.focusableFieldHolder').nextAll('span.focusedFieldDescription');
             jQuery('#descriptionPopup_content').html(fieldDesc.html());
             jQuery('#descriptionPopup').show();
         }
    };
}

Event.observe(window, 'load', function() {
    Yukon.FieldHelper.init();
});