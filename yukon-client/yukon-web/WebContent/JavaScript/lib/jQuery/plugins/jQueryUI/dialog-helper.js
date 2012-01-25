(function( $ ) {
  $.fn.addMessage = function(args) {
      //maintain the chain!
      return this.each(function() {
          var messages = [];
          var create = !$(this).children('[data-ui-add-message=]').length;
          
          switch(typeof(args.message)){
          case 'string':
              messages.push('<li>'+ args.message +'</li>');
              break;
          case 'object':
              //array
              if(typeof(args.message.length) != 'undefined'){
                  for(var i=0; i<args.message.length; i++){
                      messages.push('<li>'+ args.message[i] +'</li>');
                  }
              }else{
                  for(var key in args.message){
                      switch(typeof(args.message[key])){
                      case 'number':
                      case 'string':
                          messages.push('<li>'+ args.message[key] +'</li>');
                          break;
                      default:
                          break;
                      }
                  }
              }
              break;
          default:
              break;
          }
          
          //
          if(messages.length > 0){
              if(create){
                  $(this).prepend('<div class="userMessage '+ args.messageClass +' box" data-ui-add-message=""><ul>'+ messages.join('') +'</ul></div>');
              }else{
                  $(this).children('[data-ui-add-message=]').addClass(args.messageClass).find('ul').html(messages.join(''));
              }
          }
      });
  };
  
  $.fn.removeMessage = function() {
      return this.each(function() {
          $(this).children('[data-ui-add-message=]').remove();
      });
  }
})( jQuery );