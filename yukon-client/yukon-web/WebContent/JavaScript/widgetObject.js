JsWidgetObject = Class.create();

JsWidgetObject.prototype = { 
  initialize: function (shortName, parameters) {
    // Don't use $() up here because this is called before page is fully loaded
    this.shortName = shortName;
    this.parameters = parameters;
    this.container = "widgetContainer_" + this.parameters.widgetId;
  },

  render: function() {
    var url = "/spring/widget/" + this.shortName + "/render";
    new Ajax.Updater(this.container, url, {'parameters': this.getWidgetParameters(), 'evalScripts': true});
  },
  
  doActionRefresh: function(cmd, actionButton, waitingLabel) {
    $(actionButton).getElementsByClassName('widgetAction_waiting').invoke('show');
    $(actionButton).getElementsBySelector('input').each(function(it) {it.value = waitingLabel});
    $(this.container).getElementsBySelector('input').invoke('disable');
    
//    var restoreIndicators = function() {
//      $(actionButton).getElementsByClassName('widgetAction_waiting').invoke('hide');
//      $(actionButton).getElementsByClassName('widgetAction_normal').invoke('show');
//    };
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(this.container, url, {'parameters': this.getWidgetParameters(), 'evalScripts': true});
  },
  
  getWidgetParameters: function() {
    var container = $(this.container);
    var theseParameters = {};
    
    // now find all inputs under the container
    var widgetInputs = container.getElementsByTagName('input');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
      }
    }
    var widgetInputs = container.getElementsByTagName('select');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
      }
    }
    var widgetInputs = container.getElementsByTagName('textarea');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
      }
    }
    
    var mergedParameters = $H(this.parameters);
    mergedParameters.merge(theseParameters);
    return mergedParameters;
  }

};
