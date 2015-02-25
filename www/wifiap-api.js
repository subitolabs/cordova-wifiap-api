
function WifiApAPI()
{
    this.bindings = [];
}

WifiApAPI.prototype.setApEnabled = function(ssid,errorCallback) {
    cordova.exec(
        function(){},
        errorCallback,
        'WifiApAPI',
        'setApEnabled',
        [ssid]
    );
};

WifiApAPI.prototype.setApDisabled = function(errorCallback) {
    cordova.exec(
        function(){},
        errorCallback,
        'WifiApAPI',
        'setApDisabled',
        []
    );
};

WifiApAPI.prototype.on = function(event,callable) {

    var tmpItem = {};
    tmpItem[event]= callable;
    this.bindings.push(tmpItem);
    
};

WifiApAPI.prototype.__plugin_async = function(event,data) {
   
    for ( var i in this.bindings){
        if(this.bindings[i].hasOwnProperty(event)){
            this.bindings[i][event].apply(this,[data]);
        }
    }
};

module.exports = new WifiApAPI();
