console.log('start reqConf.js');

var require = {
    baseUrl: 'assets/js',
    paths: {
        "main": "main",
        "jquery" : "lib/jquery.min",
        "jquery-ui" : "lib/jquery-ui.min",
        "kendo" :"lib/kendo.web.min",
        "domReady" : "lib/requirePlugins/domReady"
    },
    shim : {
        "jquery-ui" : [ 'jquery' ],
        "kendo" : {
            deps: [ 'jquery' ],
            exports: 'kendo'
        }
    },
    waitSeconds: 15
};

console.log('end reqConf.js');