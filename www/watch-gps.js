exports.subscribe=function(success, error, options){
    options = options || {};
    cordova.exec(success, error, "WatchGPS", "subscribe", []);
};
