function heatmapCreate() {
  'use strict';

  function $(id) {
    return document.getElementById(id);
  };

  function repaintAll() {
	  
	Array.from(app.hiddenText).forEach(function(entry, j) {
		
    var data = [];
    var max = 0;
    var temp = {};
    var currentText = entry.innerHTML
    var currentLength = currentText.length;
    var coordinates = app.coordinates;

    for(var i=0; i < currentLength; i++){

      var key = currentText.charAt(i);
      if(/^[A-Za-z]$/.test(key)){
        key = key.toUpperCase();
      }
      if(app.config.exclude && app.EXCLUDES.indexOf(key) == -1){
        var coord;
        coord = coordinates[key] || false;
        if(coord){
          for(var s = 0; s < coord.length; s += 2) {
            var joined = coord.slice(s, s+2).join(";");
            if(!temp[joined])
              temp[joined] = 0;

            temp[joined] += 1;
          }
        }
      }
    }

    for(var k in temp){
      var xy = k.split(";");
      var val = temp[k];

      data.push({x: xy[0], y: xy[1], count: val});

      if(val > max)
        max = val;
    }

		app.heatmap[j].store.setDataSet({max: max, data: data});
	});
  };

  var typeField = $('typefield');
  var currentTypeFieldLen = "typeField.value;".length;


  app.init = function initialize() {
	  
	  
    var cfg = arguments[0] || {};
	
    app.configure(cfg);
    repaintAll();
  };

  app.configure = function configure(cfg) {
	  

	var miau = document.getElementsByClassName("keyboards");
	var hiddenText = document.getElementsByClassName("hiddenText");

	app.hiddenText = hiddenText;
	Array.from(miau).forEach(function(miauEntry, i) {
	  
    var config = {};
    config.element = miauEntry;
    config.radius = cfg.radius || 50;
    config.visible = true;
    config.opacity = 40;
    if(cfg.gradient)
      config.gradient = cfg.gradient;

    app.coordinates = app.LAYOUTS[cfg.layout || "QWERTY"];

    var heatmap = h337.create(config);
    app.heatmap[i] = heatmap;
	
    if(cfg.layout)
      $("keyboard").style.backgroundImage = "url(img/"+cfg.layout+".png)";
  });
  };

  app.init();

  var gradients = [
    { 0.45: "rgb(0,0,255)", 0.55: "rgb(0,255,255)", 0.65: "rgb(0,255,0)", 0.95: "yellow", 1.0: "rgb(255,0,0)"},
    { 0.45: "rgb(255,255,255)", 0.70: "rgb(0,0,0)",0.9: "rgb(2,255,246)", 1.0: "rgb(3,34,66)"},
    { 0.45: "rgb(216,136,211)", 0.55: "rgb(0,255,255)", 0.65: "rgb(233,59,233)", 0.95: "rgb(255,0,240)", 1.0: "yellow"}
  ];
  var lastValue = "";




};
