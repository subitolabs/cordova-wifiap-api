
function WifiApAPI()
{
    
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

module.exports = new WifiApAPI();
