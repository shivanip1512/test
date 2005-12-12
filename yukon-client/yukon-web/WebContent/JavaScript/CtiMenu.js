
var CtiMenu = Class.create();
CtiMenu.prototype = {
  initialize: function(wrapperDiv) {
    this.wrapperDiv = $(wrapperDiv);
    //Element.hide(this.wrapperDiv);
  },
  
  show: function(link, subId) {
    if ($(subId) == this.lastShownMenu) {
      Element.hide($(subId));
      //Element.hide(this.wrapperDiv);
      this.lastShownMenu = null;
      Element.removeClassName(link, 'selected');
      this.lastLink = null;
    } else {
      //Element.show(this.wrapperDiv);
      if (this.lastShownMenu) {
        Element.hide(this.lastShownMenu);
        Element.removeClassName(this.lastLink, 'selected');
      }
      this.lastShownMenu = $(subId);
      this.lastLink = link;
      Element.show($(subId));
      Element.addClassName(link, 'selected');
    }
  }
  
};