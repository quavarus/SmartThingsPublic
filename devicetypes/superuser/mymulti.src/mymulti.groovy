preferences {
	input("adjustment", "number", title: "Temp +/- Correction", description: "Adjust Temperature By X Degrees")
    input "units", "enum", 
			title:"Units?",
			multiple:false,
			metadata: [ 
            	values: [
				       'Fahrenheit',
                       'Celsius'
                ]
			]
}
metadata {
	simulator {
		status "open": "zone report :: type: 19 value: 0031"
		status "closed": "zone report :: type: 19 value: 0030"

		status "acceleration": "acceleration: 1, rssi: 0, lqi: 0"
		status "no acceleration": "acceleration: 0, rssi: 0, lqi: 0"

		for (int i = 10; i <= 50; i += 10) {
			status "temp ${i}C": "contactState: 0, accelerationState: 0, temp: $i C, battery: 100, rssi: 100, lqi: 255"
		}

		// kinda hacky because it depends on how it is installed
		status "x,y,z: 0,0,0": "x: 0, y: 0, z: 0, rssi: 100, lqi: 255"
		status "x,y,z: 1000,0,0": "x: 1000, y: 0, z: 0, rssi: 100, lqi: 255"
		status "x,y,z: 0,1000,0": "x: 0, y: 1000, z: 0, rssi: 100, lqi: 255"
		status "x,y,z: 0,0,1000": "x: 0, y: 0, z: 1000, rssi: 100, lqi: 255"
	}

	tiles {
		standardTile("contact", "device.contact", width: 2, height: 2) {
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e")
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821")
		}
		standardTile("acceleration", "device.acceleration") {
			state("active", label:'${name}', icon:"st.motion.acceleration.active", backgroundColor:"#53a7c0")
			state("inactive", label:'${name}', icon:"st.motion.acceleration.inactive", backgroundColor:"#ffffff")
		}
		valueTile("temperature", "device.temperature") {
			state("temperature", label:'${currentValue}°', unit:"F",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
		valueTile("3axis", "device.threeAxis", decoration: "flat") {
			state("threeAxis", label:'${currentValue}', unit:"", backgroundColor:"#ffffff")
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false) {
			state "battery", label:'${currentValue}% battery', unit:""/*, backgroundColors:[
				[value: 5, color: "#BC2323"],
				[value: 10, color: "#D04E00"],
				[value: 15, color: "#F1D801"],
				[value: 16, color: "#FFFFFF"]
			]*/
		}
		valueTile("lqi", "device.lqi", decoration: "flat", inactiveLabel: false) {
			state "lqi", label:'${currentValue}% signal', unit:""
		}
		

		main(["contact", "acceleration", "temperature"])
		details(["contact", "acceleration", "temperature", "3axis", "battery", "lqi"])
	}
}

def parse(String description) {
	def parameters = parameterValues()
	def results
	log.debug "Parse received $description"
	if (!isSupportedDescription(description) || zigbee.isZoneType19(description)) {
    	log.debug "Parse Zone 19"
		results = parseSingleMessage(description)
	}
	else if (description == 'updated') {
    	log.debug "Parse updated"
		//TODO is there a better way to handle this like the other device types?
		results = parseOtherMessage(description)
	}
	else {
    	log.debug "Parse Multi";
		results = parseMultiSensorMessage(description)
	}
	log.debug "Parse returned $results.descriptionText"
	return results

}

private Map parameterValues(){
	log.debug("Setting Defaults")
	def myUnits = units ?: "Fahrenheit"
	int myAdjustment = (adjustment ?: 0).toInteger()
    log.debug("Units= $myUnits Adjustment=$myAdjustment")
    return [units:myUnits,adjustment:myAdjustment]

}

private Map parseSingleMessage(description) {

	def name = parseName(description)
	def value = parseValue(description)
	def linkText = getLinkText(device)
	def descriptionText = parseDescriptionText(linkText, value, description)
	def handlerName = value == 'open' ? 'opened' : value
	def isStateChange = isStateChange(device, name, value)

	def results = [
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
	log.debug "Parse results for $device: $results"

	results
}

//TODO this just to handle 'updated' for now - investigate better way to do this
private Map parseOtherMessage(description) {
	def name = null
	def value = description
	def linkText = getLinkText(device)
	def descriptionText = description
	def handlerName = description
	def isStateChange = isStateChange(device, name, value)

	def results = [
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
	log.debug "Parse results for $device: $results"

	results
}

private List parseMultiSensorMessage(description) {
	def results = []
	if (isAccelerationMessage(description)) {
		results = parseAccelerationMessage(description)
	}
	else if (isContactMessage(description)) {
		results = parseContactMessage(description)
	}
	else if (isRssiLqiMessage(description)) {
		results = parseRssiLqiMessage(description)
	}
	else if (isOrientationMessage(description)) {
		results = parseOrientationMessage(description)
	}

	results
}

private List parseAccelerationMessage(String description) {
	def results = []
	def parts = description.split(',')
	parts.each { part ->
		part = part.trim()
		if (part.startsWith('acceleration:')) {
			results << getAccelerationResult(part, description)
		}
		else if (part.startsWith('rssi:')) {
			results << getRssiResult(part, description)
		}
		else if (part.startsWith('lqi:')) {
			results << getLqiResult(part, description)
		}
		
	}

	results
}

private List parseContactMessage(String description) {
	def results = []
	def parts = description.split(',')
	parts.each { part ->
		part = part.trim()
		if (part.startsWith('contactState:')) {
			results << getContactResult(part, description)
		}
		else if (part.startsWith('accelerationState:')) {
			results << getAccelerationResult(part, description)
		}
		else if (part.startsWith('temp:')) {
        	results << getOrigTempResult(part, description)
			results << getTempResult(part, description)
		}
		else if (part.startsWith('battery:')) {
			results << getBatteryResult(part, description)
		}
		else if (part.startsWith('rssi:')) {
			results << getRssiResult(part, description)
		}
		else if (part.startsWith('lqi:')) {
			results << getLqiResult(part, description)
		}
	}

	results
}

private List parseOrientationMessage(String description) {
	def results = []
	def xyzResults = [x: 0, y: 0, z: 0]
	def parts = description.split(',')
	parts.each { part ->
		part = part.trim()
		if (part.startsWith('x:')) {
			def unsignedX = part.split(":")[1].trim().toInteger()
			def signedX = unsignedX > 32767 ? unsignedX - 65536 : unsignedX
			xyzResults.x = signedX
		}
		else if (part.startsWith('y:')) {
			def unsignedY = part.split(":")[1].trim().toInteger()
			def signedY = unsignedY > 32767 ? unsignedY - 65536 : unsignedY
			xyzResults.y = signedY
		}
		else if (part.startsWith('z:')) {
			def unsignedZ = part.split(":")[1].trim().toInteger()
			def signedZ = unsignedZ > 32767 ? unsignedZ - 65536 : unsignedZ
			xyzResults.z = signedZ
		}
		else if (part.startsWith('rssi:')) {
			results << getRssiResult(part, description)
		}
		else if (part.startsWith('lqi:')) {
			results << getLqiResult(part, description)
		}
	}

	results << getXyzResult(xyzResults, description)

	results
}

private List parseRssiLqiMessage(String description) {
	def results = []
	// "lastHopRssi: 91, lastHopLqi: 255, rssi: 91, lqi: 255"
	def parts = description.split(',')
	parts.each { part ->
		part = part.trim()
		if (part.startsWith('lastHopRssi:')) {
			results << getRssiResult(part, description, true)
		}
		else if (part.startsWith('lastHopLqi:')) {
			results << getLqiResult(part, description, true)
		}
		else if (part.startsWith('rssi:')) {
			results << getRssiResult(part, description)
		}
		else if (part.startsWith('lqi:')) {
			results << getLqiResult(part, description)
		}
	}

	results
}

private getContactResult(part, description) {
	def name = "contact"
	def value = part.endsWith("1") ? "open" : "closed"
	def handlerName = value == 'open' ? 'opened' : value
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $handlerName"
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
}

private getAccelerationResult(part, description) {
	def name = "acceleration"
	def value = part.endsWith("1") ? "active" : "inactive"
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $value"
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: value,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
}

private getTempResult(part, description) {
	def name = "temperature"
	int value = zigbee.parseSmartThingsTemperatureValue(part, "temp: ").toInteger()
	def tempOffset = adjustment ?: 0	
    value +=tempOffset.toInteger()
    
    log.debug("getTempResult $part, $value")
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $value°F"
	def isStateChange = isTemperatureStateChange(device, name, value.toString())

	[
		name: name,
		value: value,
		unit: "F",
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: name,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
}

private getOrigTempResult(part, description) {
	def name = "originalTemperature"
	int value = zigbee.parseSmartThingsTemperatureValue(part, "temp: ").toInteger()
    
    log.debug("getOrigTempResult $part, $value")
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $value°F"
	def isStateChange = isTemperatureStateChange(device, name, value.toString())

	[
		name: name,
		value: value,
		unit: "F",
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: name,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
}


private getXyzResult(results, description) {
	def name = "threeAxis"
	def value = "${results.x},${results.y},${results.z}"
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $value"
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: name,
		isStateChange: isStateChange,
		displayed: false
	]
}

private getBatteryResult(part, description) {
	def batteryDivisor = description.split(",").find {it.split(":")[0].trim() == "batteryDivisor"} ? description.split(",").find {it.split(":")[0].trim() == "batteryDivisor"}.split(":")[1].trim() : null
	def name = "battery"
	def value = zigbee.parseSmartThingsBatteryValue(part, batteryDivisor)
	def unit = "%"
	def linkText = getLinkText(device)
	def descriptionText = "$linkText Battery was ${value}${unit}"
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: unit,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: name,
		isStateChange: isStateChange,
		displayed: false
	]
}

private getRssiResult(part, description, lastHop=false) {
	def name = lastHop ? "lastHopRssi" : "rssi"
	def valueString = part.split(":")[1].trim()
	def value = (Integer.parseInt(valueString) - 128).toString()
	def linkText = getLinkText(device)
	def descriptionText = "$linkText was $value dBm"
	if (lastHop) {
		descriptionText += " on the last hop"
	}
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: "dBm",
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: null,
		isStateChange: isStateChange,
		displayed: false
	]
}

/**
 * Use LQI (Link Quality Indicator) as a measure of signal strength. The values
 * are 0 to 255 (0x00 to 0xFF) and higher values represent higher signal
 * strength. Return as a percentage of 255.
 *
 * Note: To make the signal strength indicator more accurate, we could combine
 * LQI with RSSI.
 */
private getLqiResult(part, description, lastHop=false) {
	def name = lastHop ? "lastHopLqi" : "lqi"
	def valueString = part.split(":")[1].trim()
	def percentageOf = 255
	def value = Math.round((Integer.parseInt(valueString) / percentageOf * 100)).toString()
	def unit = "%"
	def linkText = getLinkText(device)
	def descriptionText = "$linkText Signal (LQI) was: ${value}${unit}"
	if (lastHop) {
		descriptionText += " on the last hop"
	}
	def isStateChange = isStateChange(device, name, value)

	[
		name: name,
		value: value,
		unit: unit,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: null,
		isStateChange: isStateChange,
		displayed: false
	]
}

private Boolean isAccelerationMessage(String description) {
	// "acceleration: 1, rssi: 91, lqi: 255"
	description ==~ /acceleration:.*rssi:.*lqi:.*/
}

private Boolean isContactMessage(String description) {
	// "contactState: 1, accelerationState: 0, temp: 14.4 C, battery: 28, rssi: 59, lqi: 255"
	description ==~ /contactState:.*accelerationState:.*temp:.*battery:.*rssi:.*lqi:.*/
}

private Boolean isRssiLqiMessage(String description) {
	// "lastHopRssi: 91, lastHopLqi: 255, rssi: 91, lqi: 255"
	description ==~ /lastHopRssi:.*lastHopLqi:.*rssi:.*lqi:.*/
}

private Boolean isOrientationMessage(String description) {
	// "x: 0, y: 33, z: 1017, rssi: 102, lqi: 255"
	description ==~ /x:.*y:.*z:.*rssi:.*lqi:.*/
}

private String parseName(String description) {
	if (isSupportedDescription(description)) {
		return "contact"
	}
	null
}

private String parseValue(String description) {
	if (!isSupportedDescription(description)) {
		return description
	}
	else if (zigbee.translateStatusZoneType19(description)) {
		return "open"
	}
	else {
		return "closed"
	}
}

private parseDescriptionText(String linkText, String value, String description) {
	if (!isSupportedDescription(description)) {
		return value
	}

	value ? "$linkText was ${value == 'open' ? 'opened' : value}" : ""
}
