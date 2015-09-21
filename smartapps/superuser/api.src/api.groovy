/**
 *  Sample Web Services Application
 *
 *  Author: SmartThings
 */


// Automatically generated. Make future change here.
definition(
    name: "API",
    namespace: "",
    author: "quavarus@gmail.com",
    description: "API",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true)

preferences {
	section("Allow a web application to control these things...") {
		input "devices", "capability.sensor", title: "Which Devices?", multiple: true, required: false
	}
}

mappings {
	path("/devices") {
		action: [
			GET: "listDevices",
            PUT: "updateDevices",
            POST: "updateDevices"
		]
	}
    
    path("/device/:id") {
		action: [
			GET: "showDevice",
			PUT: "updateDevice"
		]
	}
    
    path("/device/:id/:command") {
		action: [
			PUT: "updateDevice",
            GET: "updateDevice"
		]
	}
   
	path("/events/:id") {
		action: [
			GET: "showEvents"
		]
	}
    
	path("/devices/subscriptions") {
		action: [
			POST: "addDeviceSubscription"
		]
	}
	path("/devices/subscriptions/:id") {
		action: [
			DELETE: "removeDeviceSubscription"
		]
	}

	path("/state") {
		action: [
			GET: "currentState"
		]
	}

}

def installed() {
	log.trace "Installed"
}

def updated() {
	log.trace "Updated"
}

def listDevices() {
	devices.collect{deviceToJson(it)}
}

void updateDevices() {
    def updateList = request.JSON
     updateList.each{update -> 
    	log.trace update
        update.ids.each{id ->
        	update.commands.each{command ->
            	log.trace id+" "+command.name+" "+command.arguments
                executeDeviceCommand(id,command.name,command.arguments)
            }
        }
    }
}
def showDevice() {
	show(devices)
}
void updateDevice() {
	update(devices)
}
def addDeviceSubscription() {
	addSubscription(devices)
}
def removeDeviceSubscription() {
	removeSubscription(devices)
}

def deviceHandler(evt) {
	def deviceInfo = state[evt.deviceId]
	if (deviceInfo) {
		httpPostJson(uri: deviceInfo.callbackUrl, path: '', body: [evt: [value: evt.value]]) {
			log.debug "Event data successfully posted"
		}
	} else {
		log.debug "No subscribed device found"
	}
}

def currentState() {
	state
}

def showStates() {
	def device = devices.find { it.id == params.id }
	if (!device) {
		httpError(404, "Switch not found")
	}
	else {
		device.events(params)
	}
}
private void update(devices) {
	log.debug "update, request: ${request.JSON}, params: ${params}, devices: $devices.id"
	def commands = request.JSON
    if(!commands) commands = [[name: params?.command]]
    commands.each{command ->
       	log.trace params.id+" "+command.name+" "+command.arguments
        executeDeviceCommand(params.id,command.name,command.arguments)
    }
}

private void executeDeviceCommand(id,command, arguments){
	if (command) {
		def device = devices.find { it.id == id }
        
        if(command=="toggle" && !device.hasCommand("toggle") && isToggleable(device)){
        	command = nextToggleCommand(device)
        }
        
		if (!device) {
			httpError(404, "Device not found")
		} else {
        	if(arguments==null){
            	device."$command"()
            }else{
				device."$command"(*arguments)
            }
		}
    }
}

private nextToggleCommand(device){
	if(device.hasCapability("Switch")){
		def value = device.currentValue("switch")
        return value=="on"?"off":"on"    
    }else if(device.hasCapability("Door Control")){
		def value = device.currentValue("door")
        if(value=="closed" || value=="closing") return "open"
        else return "close"
    }

}

private show(devices) {
	def device = devices.find { it.id == params.id }
	if (!device) {
		httpError(404, "Device not found")
	}
	else {
		deviceToJson(device)
	}
}

private addSubscription(devices, attribute) {
	def deviceId = request.JSON?.deviceId
	def callbackUrl = request.JSON?.callbackUrl
	def myDevice = devices.find { it.id == deviceId }
	if (myDevice) {
		if (state[deviceId]) {
			log.debug "Switch subscription already exists, unsubcribing"
			unsubscribe(myDevice)
		}
		log.debug "Adding switch subscription" + callbackUrl
		state[deviceId] = [callbackUrl: callbackUrl]
		log.debug "Added state: $state"
		subscribe(myDevice, "switch", deviceHandler)
	}
}

private removeSubscription(devices) {
	def deviceId = params.id
	def device = devices.find { it.id == deviceId }
	if (device) {
		log.debug "Removing $device.displayName subscription"
		state.remove(device.id)
		unsubscribe(device)
	}
}

private deviceToJson(device){
	def deviceMap = [
    id: device.id, 
    displayName: device.displayName, 
    name: device.name, 
    capabilities: capabilitiesToList(device.capabilities),
    commands: device.supportedCommands.collect{commandToJson(it)},
    currentValues: device.supportedAttributes.collect{deviceAttributeValuesToMap(device, it)}
    ]
    if(isToggleable(device)){
    	deviceMap.commands.add([name: "toggle"])
    }
    return deviceMap
}

private isToggleable(device){
	if(device.hasCapability("Switch")) return true
    if(device.hasCapability("Door Control")) return true
    return false
}

private capabilitiesToList(caps){
	def list = [] as ArrayList
    caps.each{cap -> 
    	list.add(cap.name)
    }
	return list
}

private commandToJson(com){
	def command = [name: com.name]
    if(com.arguments.size > 0){
    	command.arguments = commandArgumentsToList(com.arguments)
    }
    return command
}

private commandArgumentsToList(args){
	def list = [] as ArrayList
    args.each{arg -> 
    	list.add(arg as String)
    }
	return list
}

private deviceAttributeValuesToMap(device, attr){
	def values = [name: attr.name, dataType: attr.dataType]
    if(attr.dataType=="ENUM"){
    	values.possibleValues = attr.values
    }
    values.value = device.currentValue(attr.name)
    return values;
}