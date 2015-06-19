cordova.define("cordova/plugin/GladiusBarcodeScanner", 
  function(require, exports, module) {
    var exec = require("cordova/exec");
    var GladiusBarcodeScanner = function() {};
    

	GladiusBarcodeScanner.prototype.startBarcodeListener = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner', 'startBarcodeListener', []);
	};

	GladiusBarcodeScanner.prototype.stopBarcodeListener = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner', 'stopBarcodeListener', []);
	};

	GladiusBarcodeScanner.prototype.startScanning = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner', 'startScanning', []);
	};
	
	var GladiusBarcodeScanner = new GladiusBarcodeScanner();
	module.exports = GladiusBarcodeScanner;

});


if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.GladiusBarcodeScanner) {
    window.plugins.GladiusBarcodeScanner = cordova.require("cordova/plugin/GladiusBarcodeScanner");
}

var gladius = cordova.require("cordova/plugin/GladiusBarcodeScanner");
gladius.startBarcodeListener(function (msg) {
        }, function () {
            alert("Error while receiving scan GLADIUS");
        });