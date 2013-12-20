// JavaScript associated with popup page

document.addEventListener('DOMContentLoaded', function () {
    chrome.tabs.getSelected(null, function(tab) {
        displayQrCode(tab.url);
    });
    
    var button = document.getElementById("enrollbutton");
	button.addEventListener('click', function() {
        var passwordform = document.getElementById("passwordform");
        var password = passwordform.value;
		chrome.tabs.getSelected(null, function(tab) {
            displayQrCode(tab.url, password);
        });
	});
    
});

function displayQrCode(url, password) 
{
    // Enrolling a new password?
    if (password) {
        var str = cleanUrl(url) + ":glasspasswordmanager:" + password;
        document.getElementById('qr').innerHTML = create_qrcode(str);
    }
    // Or looking up a password for this site? 
    else 
    {
        document.getElementById('qr').innerHTML = create_qrcode(cleanUrl(url));
    }
    
}

// Drop path from URL
function cleanUrl(url) {
    var protocol;
    
    if (url.indexOf("://") != -1) {
        protocol = url.split("://")[0];
        url = url.split("://")[1];
    }
    url = url.split('/')[0];
    
    return protocol + "://" + url;  
}

// Call library (qrcode.js) to create QR code
function create_qrcode(text, typeNumber, errorCorrectLevel, table) {

	var qr = qrcode(typeNumber || 4, errorCorrectLevel || 'M');
	qr.addData(text);
	qr.make();

	return qr.createImgTag();
};