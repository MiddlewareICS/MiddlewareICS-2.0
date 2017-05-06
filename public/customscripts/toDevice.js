

console.log('-- Here is the toDevice.js  --from Service');
console.log('x+y= '+(x+y));
console.log(Plus(2,3));
function Plus(a,b){
	return 10*a+b;
}

function scriptFunc(topic,deviceData){
	console.log("here extensional Script")
	
	var dataJson = JSON.parse(deviceData);
	if(topic === "rawEnvironment" && dataJson["temperature"]>25)
		return deviceData;
	else
		return '';
}