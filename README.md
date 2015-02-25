# cordova-wifiap-api

This plugin defines a global `WifiApAPI` object, which offer methods to on/off Wifi Access-Point Mode:

- setApEnabled
- setApDisabled
- on

### Installation

    cordova plugin add https://github.com/subitolabs/cordova-wifiap-api.git

### Quick example

```js
document.addEventListener('deviceready', function()
{
    WifiApAPI.setApEnabled("Qomodo",function(e){ console.log(e) });

    
    WifiApAPI.on('status',function(status){
         console.log(status);
    });
	
}, false);
```
