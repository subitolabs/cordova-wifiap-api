
function WifiApAPI()
{
    
}

WifiApAPI.prototype.setApEnabled = function(ssid,successCallback, errorCallback) {
    cordova.exec(
        successCallback,
        errorCallback,
        'WifiApAPI',
        'setApEnabled',
        [ssid]
    );
};

WifiApAPI.prototype.setApDisabled = function(successCallback, errorCallback) {
    cordova.exec(
        successCallback,
        errorCallback,
        'WifiApAPI',
        'setApDisabled',
        []
    );
};

module.exports = new WifiApAPI();
