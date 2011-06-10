
var CtiMenu = Class.create();
CtiMenu.prototype = {
  initialize: function(wrapperDiv) {
    this.wrapperDiv = $(wrapperDiv);
    //Element.hide(this.wrapperDiv);
  },
  
  show: function(link, subId) {
  
    // Unselect all of the menu links
  	menuItems = $$('.stdhdr_menuLink');
	menuItems.each(function(item) {
	  item.removeClassName('selected');
	});
	
	// Get the div under subMenu and then get the sub menu divs themselves
	subMenus = $('subMenu').immediateDescendants()[0].immediateDescendants();
	subMenus.each(function(item) {
	  // Hide each sub menu
	  item.hide();
	});
	
	// Select the link that was clicked
	Element.addClassName(link, 'selected');
	// Show the link's sub menu
	$(subId).show();

  }
  
};