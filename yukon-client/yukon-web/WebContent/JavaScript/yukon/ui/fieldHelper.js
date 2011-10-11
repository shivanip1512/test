/*
 * Requirements: 
 *      - Prototype 1.6+
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
         
         //Initializer
         init: function(){
             if(!this._initialized){
                 var self = Yukon.FieldHelper;
                 
                 //setup select elements
                 $$('.focusableFieldHolder select').invoke('observe', 'blur', self.blurSelect);
                 $$('.focusableFieldHolder select').invoke('observe', 'change', self.blurSelect);
                 $$('.focusableFieldHolder select').invoke('observe', 'focus', self.focusSelect);
                 
                 //setup input elements
                 $$('.focusableFieldHolder input').invoke('observe', 'blur', self.blurInput);
                 $$('.focusableFieldHolder input').invoke('observe', 'change', self.blurInput);
                 $$('.focusableFieldHolder input').invoke('observe', 'focus', self.focusInput);
                 
                 //trigger a blur event on each element -> performs an initial render
                 $$('.focusableFieldHolder select, .focusableFieldHolder input').invoke('trigger', 'blur');
                 this._initialized = true;
             }
         },
         
         blurInput: function(event){
             var inputField = event.currentTarget;
             var defaultField = inputField.up('span').next('input');
             $('descriptionPopup').hide();
             if (typeof(defaultField) == 'undefined') {
                 return;
             }
             if ($F(inputField) == $F(defaultField) || $F(inputField) == "") {
                 inputField.removeClassName('usingNonDefaultValue');
                 inputField.value = $F(defaultField);
             } else {
                 inputField.addClassName('usingNonDefaultValue');
             }
         },
         
         blurSelect: function(event){
             var inputField = event.currentTarget;
             var defaultField = inputField.up('span').next('input');
             $('descriptionPopup').hide();
             if (typeof(defaultField) == 'undefined') {
                 return;
             }
             if ($F(inputField) == $F(defaultField)) {
                 inputField.removeClassName('usingNonDefaultValue');
             } else {
                 inputField.addClassName('usingNonDefaultValue');
             }
         },
         
         focusInput: function(event){
             Yukon.FieldHelper.showPointingPopup(event);
             var inputField = event.currentTarget;
             var defaultField = inputField.up('span').next('input');
             if (typeof(defaultField) == 'undefined') {
                 return;
             }
             if ($F(inputField) == $F(defaultField)) {
                 inputField.removeClassName('usingNonDefaultValue');
                 inputField.value = "";
             } else {
                 inputField.addClassName('usingNonDefaultValue');
             }
         },
         
         //just show a popup and remove the class name
         focusSelect: function(event){
             Yukon.FieldHelper.showPointingPopup(event);
             event.currentTarget.removeClassName('usingNonDefaultValue');
         },
         
         showPointingPopup: function(event){
             var popup = $("descriptionPopup");
             
             if(!popup){
                 var popupString = [];
                 popupString.push('<div class="pointingPopup_container" id="descriptionPopup" style="display:none;">');
                     popupString.push('<div class="pointingPopup_chevron ov pr">');
                     popupString.push('</div>');
                     popupString.push('<div class="pointingPopup_content" id="descriptionPopup_content">');
                     popupString.push('</div>');
                 popupString.push('</div>');
                 
                 document.body.insert(popupString.join(''));
             }
             
             var target = Event.element(event);
             var offsets = target.cumulativeOffset();
             var popupLeft = offsets.left + target.getDimensions().width + 2;
             var left = popupLeft + 'px';
             var top = (offsets.top -20) + 'px';
             
             $('descriptionPopup').setStyle({left:left, top:top});
             $('descriptionPopup_content').innerHTML = target.up().next('span.focusedFieldDescription').innerHTML;
             $('descriptionPopup').show();
         }
    };
}

Event.observe(window, 'load', function() {
    Yukon.FieldHelper.init();
});