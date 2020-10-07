/*
 Highcharts JS v8.2.0 (2020-08-20)

 Exporting module

 (c) 2010-2019 Torstein Honsi

 License: www.highcharts.com/license
*/
(function(c){"object"===typeof module&&module.exports?(c["default"]=c,module.exports=c):"function"===typeof define&&define.amd?define("highcharts/modules/exporting",["highcharts"],function(p){c(p);c.Highcharts=p;return c}):c("undefined"!==typeof Highcharts?Highcharts:void 0)})(function(c){function p(c,l,h,k){c.hasOwnProperty(l)||(c[l]=k.apply(null,h))}c=c?c._modules:{};p(c,"Extensions/FullScreen.js",[c["Core/Chart/Chart.js"],c["Core/Globals.js"],c["Core/Utilities.js"]],function(c,l,h){var k=h.addEvent;
h=function(){function c(e){this.chart=e;this.isOpen=!1;e=e.renderTo;this.browserProps||("function"===typeof e.requestFullscreen?this.browserProps={fullscreenChange:"fullscreenchange",requestFullscreen:"requestFullscreen",exitFullscreen:"exitFullscreen"}:e.mozRequestFullScreen?this.browserProps={fullscreenChange:"mozfullscreenchange",requestFullscreen:"mozRequestFullScreen",exitFullscreen:"mozCancelFullScreen"}:e.webkitRequestFullScreen?this.browserProps={fullscreenChange:"webkitfullscreenchange",
requestFullscreen:"webkitRequestFullScreen",exitFullscreen:"webkitExitFullscreen"}:e.msRequestFullscreen&&(this.browserProps={fullscreenChange:"MSFullscreenChange",requestFullscreen:"msRequestFullscreen",exitFullscreen:"msExitFullscreen"}))}c.prototype.close=function(){var e=this.chart;if(this.isOpen&&this.browserProps&&e.container.ownerDocument instanceof Document)e.container.ownerDocument[this.browserProps.exitFullscreen]();this.unbindFullscreenEvent&&this.unbindFullscreenEvent();this.isOpen=!1;
this.setButtonText()};c.prototype.open=function(){var e=this,c=e.chart;if(e.browserProps){e.unbindFullscreenEvent=k(c.container.ownerDocument,e.browserProps.fullscreenChange,function(){e.isOpen?(e.isOpen=!1,e.close()):(e.isOpen=!0,e.setButtonText())});var h=c.renderTo[e.browserProps.requestFullscreen]();if(h)h["catch"](function(){alert("Full screen is not supported inside a frame.")});k(c,"destroy",e.unbindFullscreenEvent)}};c.prototype.setButtonText=function(){var e,c=this.chart,h=c.exportDivElements,
k=c.options.exporting,l=null===(e=null===k||void 0===k?void 0:k.buttons)||void 0===e?void 0:e.contextButton.menuItems;e=c.options.lang;(null===k||void 0===k?0:k.menuItemDefinitions)&&(null===e||void 0===e?0:e.exitFullscreen)&&e.viewFullscreen&&l&&h&&h.length&&(h[l.indexOf("viewFullscreen")].innerHTML=this.isOpen?e.exitFullscreen:k.menuItemDefinitions.viewFullscreen.text||e.viewFullscreen)};c.prototype.toggle=function(){this.isOpen?this.close():this.open()};return c}();l.Fullscreen=h;k(c,"beforeRender",
function(){this.fullscreen=new l.Fullscreen(this)});return l.Fullscreen});p(c,"Mixins/Navigation.js",[],function(){return{initUpdate:function(c){c.navigation||(c.navigation={updates:[],update:function(c,h){this.updates.forEach(function(k){k.update.call(k.context,c,h)})}})},addUpdate:function(c,l){l.navigation||this.initUpdate(l);l.navigation.updates.push({update:c,context:l})}}});p(c,"Extensions/Exporting.js",[c["Core/Chart/Chart.js"],c["Mixins/Navigation.js"],c["Core/Globals.js"],c["Core/Options.js"],
c["Core/Renderer/SVG/SVGRenderer.js"],c["Core/Utilities.js"]],function(c,l,h,k,p,e){var x=h.doc,H=h.isTouchDevice,z=h.win;k=k.defaultOptions;var t=e.addEvent,u=e.css,y=e.createElement,D=e.discardElement,w=e.extend,I=e.find,B=e.fireEvent,J=e.isObject,n=e.merge,E=e.objectEach,q=e.pick,K=e.removeEvent,L=e.uniqueKey,F=z.navigator.userAgent,G=h.Renderer.prototype.symbols,M=/Edge\/|Trident\/|MSIE /.test(F),N=/firefox/i.test(F);w(k.lang,{viewFullscreen:"View in full screen",exitFullscreen:"Exit from full screen",
printChart:"Print chart",downloadPNG:"Download PNG image",downloadJPEG:"Download JPEG image",downloadPDF:"Download PDF document",downloadSVG:"Download SVG vector image",contextButtonTitle:"Chart context menu"});k.navigation||(k.navigation={});n(!0,k.navigation,{buttonOptions:{theme:{},symbolSize:14,symbolX:12.5,symbolY:10.5,align:"right",buttonSpacing:3,height:22,verticalAlign:"top",width:24}});n(!0,k.navigation,{menuStyle:{border:"1px solid #999999",background:"#ffffff",padding:"5px 0"},menuItemStyle:{padding:"0.5em 1em",
color:"#333333",background:"none",fontSize:H?"14px":"11px",transition:"background 250ms, color 250ms"},menuItemHoverStyle:{background:"#335cad",color:"#ffffff"},buttonOptions:{symbolFill:"#666666",symbolStroke:"#666666",symbolStrokeWidth:3,theme:{padding:5}}});k.exporting={type:"image/png",url:"https://export.highcharts.com/",printMaxWidth:780,scale:2,buttons:{contextButton:{className:"highcharts-contextbutton",menuClassName:"highcharts-contextmenu",symbol:"menu",titleKey:"contextButtonTitle",menuItems:"viewFullscreen printChart separator downloadPNG downloadJPEG downloadPDF downloadSVG".split(" ")}},
menuItemDefinitions:{viewFullscreen:{textKey:"viewFullscreen",onclick:function(){this.fullscreen.toggle()}},printChart:{textKey:"printChart",onclick:function(){this.print()}},separator:{separator:!0},downloadPNG:{textKey:"downloadPNG",onclick:function(){this.exportChart()}},downloadJPEG:{textKey:"downloadJPEG",onclick:function(){this.exportChart({type:"image/jpeg"})}},downloadPDF:{textKey:"downloadPDF",onclick:function(){this.exportChart({type:"application/pdf"})}},downloadSVG:{textKey:"downloadSVG",
onclick:function(){this.exportChart({type:"image/svg+xml"})}}}};h.post=function(a,b,f){var d=y("form",n({method:"post",action:a,enctype:"multipart/form-data"},f),{display:"none"},x.body);E(b,function(a,b){y("input",{type:"hidden",name:b,value:a},null,d)});d.submit();D(d)};h.isSafari&&h.win.matchMedia("print").addListener(function(a){h.printingChart&&(a.matches?h.printingChart.beforePrint():h.printingChart.afterPrint())});w(c.prototype,{sanitizeSVG:function(a,b){var f=a.indexOf("</svg>")+6,d=a.substr(f);
a=a.substr(0,f);b&&b.exporting&&b.exporting.allowHTML&&d&&(d='<foreignObject x="0" y="0" width="'+b.chart.width+'" height="'+b.chart.height+'"><body xmlns="http://www.w3.org/1999/xhtml">'+d.replace(/(<(?:img|br).*?(?=>))>/g,"$1 />")+"</body></foreignObject>",a=a.replace("</svg>",d+"</svg>"));a=a.replace(/zIndex="[^"]+"/g,"").replace(/symbolName="[^"]+"/g,"").replace(/jQuery[0-9]+="[^"]+"/g,"").replace(/url\(("|&quot;)(.*?)("|&quot;);?\)/g,"url($2)").replace(/url\([^#]+#/g,"url(#").replace(/<svg /,
'<svg xmlns:xlink="http://www.w3.org/1999/xlink" ').replace(/ (|NS[0-9]+:)href=/g," xlink:href=").replace(/\n/," ").replace(/(fill|stroke)="rgba\(([ 0-9]+,[ 0-9]+,[ 0-9]+),([ 0-9\.]+)\)"/g,'$1="rgb($2)" $1-opacity="$3"').replace(/&nbsp;/g,"\u00a0").replace(/&shy;/g,"\u00ad");this.ieSanitizeSVG&&(a=this.ieSanitizeSVG(a));return a},getChartHTML:function(){this.styledMode&&this.inlineStyles();return this.container.innerHTML},getSVG:function(a){var b,f=n(this.options,a);f.plotOptions=n(this.userOptions.plotOptions,
a&&a.plotOptions);f.time=n(this.userOptions.time,a&&a.time);var d=y("div",null,{position:"absolute",top:"-9999em",width:this.chartWidth+"px",height:this.chartHeight+"px"},x.body);var c=this.renderTo.style.width;var e=this.renderTo.style.height;c=f.exporting.sourceWidth||f.chart.width||/px$/.test(c)&&parseInt(c,10)||(f.isGantt?800:600);e=f.exporting.sourceHeight||f.chart.height||/px$/.test(e)&&parseInt(e,10)||400;w(f.chart,{animation:!1,renderTo:d,forExport:!0,renderer:"SVGRenderer",width:c,height:e});
f.exporting.enabled=!1;delete f.data;f.series=[];this.series.forEach(function(a){b=n(a.userOptions,{animation:!1,enableMouseTracking:!1,showCheckbox:!1,visible:a.visible});b.isInternal||f.series.push(b)});this.axes.forEach(function(a){a.userOptions.internalKey||(a.userOptions.internalKey=L())});var k=new h.Chart(f,this.callback);a&&["xAxis","yAxis","series"].forEach(function(b){var d={};a[b]&&(d[b]=a[b],k.update(d))});this.axes.forEach(function(a){var b=I(k.axes,function(b){return b.options.internalKey===
a.userOptions.internalKey}),d=a.getExtremes(),f=d.userMin;d=d.userMax;b&&("undefined"!==typeof f&&f!==b.min||"undefined"!==typeof d&&d!==b.max)&&b.setExtremes(f,d,!0,!1)});c=k.getChartHTML();B(this,"getSVG",{chartCopy:k});c=this.sanitizeSVG(c,f);f=null;k.destroy();D(d);return c},getSVGForExport:function(a,b){var f=this.options.exporting;return this.getSVG(n({chart:{borderRadius:0}},f.chartOptions,b,{exporting:{sourceWidth:a&&a.sourceWidth||f.sourceWidth,sourceHeight:a&&a.sourceHeight||f.sourceHeight}}))},
getFilename:function(){var a=this.userOptions.title&&this.userOptions.title.text,b=this.options.exporting.filename;if(b)return b.replace(/\//g,"-");"string"===typeof a&&(b=a.toLowerCase().replace(/<\/?[^>]+(>|$)/g,"").replace(/[\s_]+/g,"-").replace(/[^a-z0-9\-]/g,"").replace(/^[\-]+/g,"").replace(/[\-]+/g,"-").substr(0,24).replace(/[\-]+$/g,""));if(!b||5>b.length)b="chart";return b},exportChart:function(a,b){b=this.getSVGForExport(a,b);a=n(this.options.exporting,a);h.post(a.url,{filename:a.filename?
a.filename.replace(/\//g,"-"):this.getFilename(),type:a.type,width:a.width||0,scale:a.scale,svg:b},a.formAttributes)},moveContainers:function(a){(this.fixedDiv?[this.fixedDiv,this.scrollingContainer]:[this.container]).forEach(function(b){a.appendChild(b)})},beforePrint:function(){var a=x.body,b=this.options.exporting.printMaxWidth,f={childNodes:a.childNodes,origDisplay:[],resetParams:void 0};this.isPrinting=!0;this.pointer.reset(null,0);B(this,"beforePrint");b&&this.chartWidth>b&&(f.resetParams=[this.options.chart.width,
void 0,!1],this.setSize(b,void 0,!1));[].forEach.call(f.childNodes,function(a,b){1===a.nodeType&&(f.origDisplay[b]=a.style.display,a.style.display="none")});this.moveContainers(a);this.printReverseInfo=f},afterPrint:function(){if(this.printReverseInfo){var a=this.printReverseInfo.childNodes,b=this.printReverseInfo.origDisplay,f=this.printReverseInfo.resetParams;this.moveContainers(this.renderTo);[].forEach.call(a,function(a,f){1===a.nodeType&&(a.style.display=b[f]||"")});this.isPrinting=!1;f&&this.setSize.apply(this,
f);delete this.printReverseInfo;delete h.printingChart;B(this,"afterPrint")}},print:function(){var a=this;a.isPrinting||(h.printingChart=a,h.isSafari||a.beforePrint(),setTimeout(function(){z.focus();z.print();h.isSafari||setTimeout(function(){a.afterPrint()},1E3)},1))},contextMenu:function(a,b,f,d,c,h,k){var g=this,C=g.options.navigation,l=g.chartWidth,A=g.chartHeight,r="cache-"+a,m=g[r],v=Math.max(c,h);if(!m){g.exportContextMenu=g[r]=m=y("div",{className:a},{position:"absolute",zIndex:1E3,padding:v+
"px",pointerEvents:"auto"},g.fixedDiv||g.container);var n=y("ul",{className:"highcharts-menu"},{listStyle:"none",margin:0,padding:0},m);g.styledMode||u(n,w({MozBoxShadow:"3px 3px 10px #888",WebkitBoxShadow:"3px 3px 10px #888",boxShadow:"3px 3px 10px #888"},C.menuStyle));m.hideMenu=function(){u(m,{display:"none"});k&&k.setState(0);g.openMenu=!1;u(g.renderTo,{overflow:"hidden"});e.clearTimeout(m.hideTimer);B(g,"exportMenuHidden")};g.exportEvents.push(t(m,"mouseleave",function(){m.hideTimer=z.setTimeout(m.hideMenu,
500)}),t(m,"mouseenter",function(){e.clearTimeout(m.hideTimer)}),t(x,"mouseup",function(b){g.pointer.inClass(b.target,a)||m.hideMenu()}),t(m,"click",function(){g.openMenu&&m.hideMenu()}));b.forEach(function(a){"string"===typeof a&&(a=g.options.exporting.menuItemDefinitions[a]);if(J(a,!0)){if(a.separator)var b=y("hr",null,null,n);else b=y("li",{className:"highcharts-menu-item",onclick:function(b){b&&b.stopPropagation();m.hideMenu();a.onclick&&a.onclick.apply(g,arguments)},innerHTML:a.text||g.options.lang[a.textKey]},
null,n),g.styledMode||(b.onmouseover=function(){u(this,C.menuItemHoverStyle)},b.onmouseout=function(){u(this,C.menuItemStyle)},u(b,w({cursor:"pointer"},C.menuItemStyle)));g.exportDivElements.push(b)}});g.exportDivElements.push(n,m);g.exportMenuWidth=m.offsetWidth;g.exportMenuHeight=m.offsetHeight}b={display:"block"};f+g.exportMenuWidth>l?b.right=l-f-c-v+"px":b.left=f-v+"px";d+h+g.exportMenuHeight>A&&"top"!==k.alignOptions.verticalAlign?b.bottom=A-d-v+"px":b.top=d+h-v+"px";u(m,b);u(g.renderTo,{overflow:""});
g.openMenu=!0;B(g,"exportMenuShown")},addButton:function(a){var b=this,f=b.renderer,d=n(b.options.navigation.buttonOptions,a),c=d.onclick,e=d.menuItems,h=d.symbolSize||12;b.btnCount||(b.btnCount=0);b.exportDivElements||(b.exportDivElements=[],b.exportSVGElements=[]);if(!1!==d.enabled){var g=d.theme,k=g.states,l=k&&k.hover;k=k&&k.select;var A;b.styledMode||(g.fill=q(g.fill,"#ffffff"),g.stroke=q(g.stroke,"none"));delete g.states;c?A=function(a){a&&a.stopPropagation();c.call(b,a)}:e&&(A=function(a){a&&
a.stopPropagation();b.contextMenu(r.menuClassName,e,r.translateX,r.translateY,r.width,r.height,r);r.setState(2)});d.text&&d.symbol?g.paddingLeft=q(g.paddingLeft,25):d.text||w(g,{width:d.width,height:d.height,padding:0});b.styledMode||(g["stroke-linecap"]="round",g.fill=q(g.fill,"#ffffff"),g.stroke=q(g.stroke,"none"));var r=f.button(d.text,0,0,A,g,l,k).addClass(a.className).attr({title:q(b.options.lang[d._titleKey||d.titleKey],"")});r.menuClassName=a.menuClassName||"highcharts-menu-"+b.btnCount++;
if(d.symbol){var m=f.symbol(d.symbol,d.symbolX-h/2,d.symbolY-h/2,h,h,{width:h,height:h}).addClass("highcharts-button-symbol").attr({zIndex:1}).add(r);b.styledMode||m.attr({stroke:d.symbolStroke,fill:d.symbolFill,"stroke-width":d.symbolStrokeWidth||1})}r.add(b.exportingGroup).align(w(d,{width:r.width,x:q(d.x,b.buttonOffset)}),!0,"spacingBox");b.buttonOffset+=(r.width+d.buttonSpacing)*("right"===d.align?-1:1);b.exportSVGElements.push(r,m)}},destroyExport:function(a){var b=a?a.target:this;a=b.exportSVGElements;
var f=b.exportDivElements,d=b.exportEvents,c;a&&(a.forEach(function(a,d){a&&(a.onclick=a.ontouchstart=null,c="cache-"+a.menuClassName,b[c]&&delete b[c],b.exportSVGElements[d]=a.destroy())}),a.length=0);b.exportingGroup&&(b.exportingGroup.destroy(),delete b.exportingGroup);f&&(f.forEach(function(a,d){e.clearTimeout(a.hideTimer);K(a,"mouseleave");b.exportDivElements[d]=a.onmouseout=a.onmouseover=a.ontouchstart=a.onclick=null;D(a)}),f.length=0);d&&(d.forEach(function(a){a()}),d.length=0)}});p.prototype.inlineToAttributes=
"fill stroke strokeLinecap strokeLinejoin strokeWidth textAnchor x y".split(" ");p.prototype.inlineBlacklist=[/-/,/^(clipPath|cssText|d|height|width)$/,/^font$/,/[lL]ogical(Width|Height)$/,/perspective/,/TapHighlightColor/,/^transition/,/^length$/];p.prototype.unstyledElements=["clipPath","defs","desc"];c.prototype.inlineStyles=function(){function a(a){return a.replace(/([A-Z])/g,function(a,b){return"-"+b.toLowerCase()})}function b(c){function f(b,f){v=u=!1;if(h){for(q=h.length;q--&&!u;)u=h[q].test(f);
v=!u}"transform"===f&&"none"===b&&(v=!0);for(q=e.length;q--&&!v;)v=e[q].test(f)||"function"===typeof b;v||y[f]===b&&"svg"!==c.nodeName||g[c.nodeName][f]===b||(d&&-1===d.indexOf(f)?m+=a(f)+":"+b+";":b&&c.setAttribute(a(f),b))}var m="",v,u,q;if(1===c.nodeType&&-1===k.indexOf(c.nodeName)){var t=z.getComputedStyle(c,null);var y="svg"===c.nodeName?{}:z.getComputedStyle(c.parentNode,null);if(!g[c.nodeName]){l=p.getElementsByTagName("svg")[0];var w=p.createElementNS(c.namespaceURI,c.nodeName);l.appendChild(w);
g[c.nodeName]=n(z.getComputedStyle(w,null));"text"===c.nodeName&&delete g.text.fill;l.removeChild(w)}if(N||M)for(var x in t)f(t[x],x);else E(t,f);m&&(t=c.getAttribute("style"),c.setAttribute("style",(t?t+";":"")+m));"svg"===c.nodeName&&c.setAttribute("stroke-width","1px");"text"!==c.nodeName&&[].forEach.call(c.children||c.childNodes,b)}}var c=this.renderer,d=c.inlineToAttributes,e=c.inlineBlacklist,h=c.inlineWhitelist,k=c.unstyledElements,g={},l;c=x.createElement("iframe");u(c,{width:"1px",height:"1px",
visibility:"hidden"});x.body.appendChild(c);var p=c.contentWindow.document;p.open();p.write('<svg xmlns="http://www.w3.org/2000/svg"></svg>');p.close();b(this.container.querySelector("svg"));l.parentNode.removeChild(l)};G.menu=function(a,b,c,d){return[["M",a,b+2.5],["L",a+c,b+2.5],["M",a,b+d/2+.5],["L",a+c,b+d/2+.5],["M",a,b+d-1.5],["L",a+c,b+d-1.5]]};G.menuball=function(a,b,c,d){a=[];d=d/3-2;return a=a.concat(this.circle(c-d,b,d,d),this.circle(c-d,b+d+4,d,d),this.circle(c-d,b+2*(d+4),d,d))};c.prototype.renderExporting=
function(){var a=this,b=a.options.exporting,c=b.buttons,d=a.isDirtyExporting||!a.exportSVGElements;a.buttonOffset=0;a.isDirtyExporting&&a.destroyExport();d&&!1!==b.enabled&&(a.exportEvents=[],a.exportingGroup=a.exportingGroup||a.renderer.g("exporting-group").attr({zIndex:3}).add(),E(c,function(b){a.addButton(b)}),a.isDirtyExporting=!1);t(a,"destroy",a.destroyExport)};t(c,"init",function(){var a=this;a.exporting={update:function(b,c){a.isDirtyExporting=!0;n(!0,a.options.exporting,b);q(c,!0)&&a.redraw()}};
l.addUpdate(function(b,c){a.isDirtyExporting=!0;n(!0,a.options.navigation,b);q(c,!0)&&a.redraw()},a)});c.prototype.callbacks.push(function(a){a.renderExporting();t(a,"redraw",a.renderExporting)})});p(c,"masters/modules/exporting.src.js",[],function(){})});
//# sourceMappingURL=exporting.js.map