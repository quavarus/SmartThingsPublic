/**
 *  Sample Web Services Application
 *
 *  Author: SmartThings
 */


// Automatically generated. Make future change here.
definition(
    name: "OpenHAB",
    namespace: "smartthings",
    author: "quavarus@gmail.com",
    description: "OpenHAB Integration",
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
    
    path("/debug"){
    	action: [
			GET: "generateChannelTypes"
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

def logStuff(){
	def output="";
	devices.each{device ->
    	device.capabilities.each{capability->
        	capability.attributes.each{attribute ->
            	if(attribute.values){
                	attribute.values.each{value->
                    	output+= capability.name+","+attribute.name+","+attribute.dataType+","+value+"\r\n"
                    }
                }else{
                	output+= capability.name+","+attribute.name+","+attribute.dataType+"\r\n"
                }
            }
            capability.commands.each{command->
            	output+= capability.name+",,,,\""+command.name+"("
                if(command.arguments){
                	command.arguments.each{argument->
                    	output+=(argument as String)+","
                    }
                }
                output+= ")\"\r\n"
            }
        }
    }
    [output:output];
}
def mappings(){
	[
    switchChannels:["Acceleration_Sensor_acceleration","Beacon_presence","Light_switch","Motion_Sensor_motion","Music_Player_mute","Presence_Sensor_presence","Relay_Switch_switch","Samsung_TV_mute","Samsung_TV_switch","Shock_Sensor_shock","Sleep_Sensor_sleeping","Sound_Sensor_sound","Switch_switch","Tamper_Alert_tamper","Valve_valve","Video_Camera_mute","Water_Sensor_water"],
    contactChannels:["Contact_Sensor_contact","Valve_contact"],
    dimmerChannels:["Switch_Level_level"],
    binaryPositives:["active","present","open","on","muted","detected","sleeping","wet"],
    binaryNegatives:["inactive","not present","closed","off","unmuted","unknown","clear","not sleeping","not detected","dry"],
    attributeCommands:[
    	Alarm_alarm_both:"both()",
        Alarm_alarm_off:"off()",
        Alarm_alarm_siren:"siren()",
        Alarm_alarm_strobe:"strobe()",
        Color_Control_color:"setColor(COLOR_MAP)",
        Color_Control_hue:"setHue(NUMBER)",
        Color_Control_saturation:"setSaturation(NUMBER)",
        Color_Temperature_colorTemperature:"setColorTemperature(NUMBER)",
        Consumable_consumableStatus_maintenance_required:"setConsumableStatus(STRING)",
        Consumable_consumableStatus_missing:"setConsumableStatus(STRING)",
        Consumable_consumableStatus_order:"setConsumableStatus(STRING)",
        Consumable_consumableStatus_replace:"setConsumableStatus(STRING)",
        Indicator_indicatorStatus_never:"indicatorNever()",
        Indicator_indicatorStatus_when_off:"indicatorWhenOff()",
        Indicator_indicatorStatus_when_on:"indicatorWhenOn()",
        Light_switch_off:"off()",
        Light_switch_on:"on()",
        Music_Player_mute_muted:"mute()",
        Music_Player_mute_unmuted:"unmute()",
        Relay_Switch_switch_off:"off()",
        Relay_Switch_switch_on:"on()",
        Samsung_TV_mute_muted:"mute()",
        Samsung_TV_mute_unmuted:"unmute()",
        Samsung_TV_pictureMode_dynamic:"setPictureMode(ENUM)",
        Samsung_TV_pictureMode_movie:"setPictureMode(ENUM)",
        Samsung_TV_pictureMode_standard:"setPictureMode(ENUM)",
        Samsung_TV_pictureMode_unknown:"setPictureMode(ENUM)",
        Samsung_TV_soundMode_clear_voice:"setSoundMode(ENUM)",
        Samsung_TV_soundMode_movie:"setSoundMode(ENUM)",
        Samsung_TV_soundMode_music:"setSoundMode(ENUM)",
        Samsung_TV_soundMode_standard:"setSoundMode(ENUM)",
        Samsung_TV_soundMode_unknown:"setSoundMode(ENUM)",
        Samsung_TV_switch_off:"off()",
        Samsung_TV_switch_on:"on()",
        Samsung_TV_volume:"setVolume(NUMBER)",
        Switch_switch_off:"off()",
        Switch_switch_on:"on()",
        Switch_Level_level:"setLevel(NUMBER, NUMBER)",
        Thermostat_coolingSetpoint:"setCoolingSetpoint(NUMBER)",
        Thermostat_heatingSetpoint:"setHeatingSetpoint(NUMBER)",
        Thermostat_thermostatFanMode_auto:"fanAuto()",
        Thermostat_thermostatFanMode_circulate:"fanCirculate()",
        Thermostat_thermostatFanMode_on:"fanOn()",
        Thermostat_thermostatMode_auto:"auto()",
        Thermostat_thermostatMode_cool:"cool()",
        Thermostat_thermostatMode_emergency_heat:"emergencyHeat()",
        Thermostat_thermostatMode_heat:"heat()",
        Thermostat_thermostatMode_off:"off()",
        Thermostat_Cooling_Setpoint_coolingSetpoint:"setCoolingSetpoint(NUMBER)",
        Thermostat_Fan_Mode_thermostatFanMode_auto:"fanAuto()",
        Thermostat_Fan_Mode_thermostatFanMode_circulate:"fanCirculate()",
        Thermostat_Fan_Mode_thermostatFanMode_on:"fanOn()",
        Thermostat_Heating_Setpoint_heatingSetpoint:"setHeatingSetpoint(NUMBER)",
        Thermostat_Mode_thermostatMode_auto:"auto()",
        Thermostat_Mode_thermostatMode_cool:"cool()",
        Thermostat_Mode_thermostatMode_emergency_heat:"emergencyHeat()",
        Thermostat_Mode_thermostatMode_heat:"heat()",
        Thermostat_Mode_thermostatMode_off:"off()",
        Thermostat_Schedule_schedule:"setSchedule(JSON_OBJECT)",
        Timed_Session_sessionStatus_canceled:"cancel()",
        Timed_Session_sessionStatus_paused:"pause()",
        Timed_Session_sessionStatus_running:"start()",
        Timed_Session_sessionStatus_stopped:"stop()",
        Timed_Session_timeRemaining:"setTimeRemaining(NUMBER)",
        Valve_valve_closed:"close()",
        Valve_valve_open:"open()",
        Video_Camera_camera_off:"off()",
        Video_Camera_camera_on:"on()",
        Video_Camera_mute_muted:"mute()",
        Video_Camera_mute_unmuted:"unmute()",
	]
    ]
}
def calculateItemType(capability,attribute){
	def channelId = capability.name.replaceAll(" ","_")+"_"+attribute.name
    def mappings = mappings();
    
	switch(attribute.dataType as String){
    	case "NUMBER":
        if(mappings.dimmerChannels.contains(channelId))return"Dimmer"
        return "Number";
        case "VECTOR3":
        return "String";
        case "ENUM":
        if(mappings.switchChannels.contains(channelId))return"Switch"
        if(mappings.contactChannels.contains(channelId))return"Contact"
        return "String";
        case "STRING":
        return "String";
        case "JSON_OBJECT":
        return "String";
        case "DATE":
        return "DateTime"
        default:
            return "String";
    }
}
def calculateFormat(device, attribute){
	def current = device.currentState(attribute.name)
    def unit = ""
    if(current && current.unit){
    	unit=current.unit;
    }
    if (unit.equals("%")) {
      unit = "%%";
    }
    return "%s" + unit;
}
def calculateOptions(attribute){
	def optionsOut = ""
    if(attribute.values){
    	optionsOut+="<options>"
    	attribute.values.each{value->
        	optionsOut+="<option value=\""+value+"\">"+value+"</option>"
        }
        optionsOut+="</options>"
    }
    optionsOut
}

def calculateCommandOptions(capability){
	def optionsOut = ""
    if(capability.commands){
    	optionsOut+="<options>"
    	capability.commands.each{command->
        	def arguments = ""
        	if(command.arguments.size > 0){
                arguments = command.arguments.collect{it as String};
            }
            arguments = (""+arguments).replaceAll("[\\[\\]]","")
        	optionsOut+="<option value=\""+command.name+"("+arguments+")\">"+command.name+"("+arguments+")</option>"
        }
        optionsOut+="</options>"
    }
    optionsOut
}

def calculateConfig(capability,attribute){
	def configOut=""
    def itemType = calculateItemType(capability,attribute)
    def mappings = mappings();
    if(["Switch","Contact"].contains(itemType)){
    
    	//onOpenValue
    	configOut+="<parameter name=\"onOpenValue\" type=\"text\" required=\"true\">"
		configOut+="<label>On/Open Value</label>"
        configOut+="<description>The SmartThings value corresponding to this switch being \"On\" or a contact being \"Open\".</description>"
        def defaultValue = ""
        for(String value : attribute.values){
        	if(mappings.binaryPositives.contains(value)){
            	defaultValue = value;
                break;
            }
        }
        configOut+="<default>"+defaultValue+"</default>"
        configOut+=calculateOptions(attribute)
        configOut+="</parameter>"
        
        //offClosedValue
    	configOut+="<parameter name=\"offClosedValue\" type=\"text\" required=\"true\">"
		configOut+="<label>Off/Closed Value</label>"
        configOut+="<description>The SmartThings value corresponding to this switch being \"Off\" or a contact being \"Closed\".</description>"
        defaultValue = ""
        for(String value : attribute.values){
        	if(mappings.binaryNegatives.contains(value)){
            	defaultValue = value;
                break;
            }
        }
        configOut+="<default>"+defaultValue+"</default>"
        configOut+=calculateOptions(attribute)
        configOut+="</parameter>"
    }
    
    //changeCommand
    if(capability.commands){
    configOut+="<parameter name=\"changeCommand\" type=\"text\">"
    configOut+="<label>On Change Command</label>"
    configOut+="<description>The SmartThings command to call when this channel changes.</description>"
    def defaultValue = lookupDefaultCommand(capability,attribute,null)
    if(defaultValue)
    configOut+="<default>"+defaultValue+"</default>"
    configOut+=calculateCommandOptions(capability)
    configOut+="</parameter>"
    }
    
    //enum change commands
    if(attribute.values){
    attribute.values.each{value->
    configOut+="<parameter name=\"changeCommand_"+value.replaceAll(" ","_")+"\" type=\"text\">"
    configOut+="<label>On Change Command ("+value+")</label>"
    configOut+="<description>The SmartThings command to call when this channel changes to \""+value+"\".</description>"
    def defaultValue = lookupDefaultCommand(capability,attribute,value)
    if(defaultValue)
    configOut+="<default>"+defaultValue+"</default>"
    configOut+=calculateCommandOptions(capability)
    configOut+="</parameter>"
    }
    }
    
    configOut
}
def lookupDefaultCommand(capability,attribute,value){
	log.debug "lookupDefaultCommand($capability,$attribute,$value)"
	def attributelookup = capability.name+"_"+attribute.name
    attributelookup = attributelookup.replaceAll(" ","_")
    def attributeValueLookup = "";
    def mappings = mappings()
    if(value){
    	attributeValueLookup=attributelookup+"_"+value.replaceAll(" ","_")
    }
    log.debug "lookupDefaultCommand($capability,$attribute,$value) attributeValueLookup=$attributeValueLookup"
    log.debug "lookupDefaultCommand($capability,$attribute,$value) attributelookup=$attributelookup"
    def returnValue = mappings.attributeCommands[attributeValueLookup]
    if(returnValue)return returnValue
    return mappings.attributeCommands[attributelookup]
}
def calculateReadOnly(capability){
	if(capability.commands)return false;
    return true;
}
def generateChannelTypes(){
	def output="<?xml version=\"1.0\" encoding=\"UTF-8\"?><thing:thing-descriptions bindingId=\"smartthings\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:thing=\"http://eclipse.org/smarthome/schemas/thing-description/v1.0.0\" xsi:schemaLocation=\"http://eclipse.org/smarthome/schemas/thing-description/v1.0.0 http://eclipse.org/smarthome/schemas/thing-description-1.0.0.xsd\">"
	devices.each{device ->
    	device.capabilities.each{capability->
        	capability.attributes.each{attribute ->
            	def channelId = capability.name.replaceAll(" ","_")+"_"+attribute.name
            	output+="<channel-type id=\""+channelId+"\">\r\n"
                def itemType = calculateItemType(capability,attribute)
                output+="<item-type>"+itemType+"</item-type>\r\n"
                output+="<label>"+attribute.name+"</label>\r\n"
                output+="<state pattern=\""+calculateFormat(device, attribute)+"\" readOnly=\""+calculateReadOnly(capability)+"\">"
                if(!["Switch","Contact"].contains(itemType))
                output+=calculateOptions(attribute)
                output+="</state>\r\n"
                output+="<config-description uri=\"channel-type:smartthings:"+channelId+"\">"+calculateConfig(capability,attribute)+"</config-description>\r\n"
				output+="</channel-type>\r\n"
            }
//            capability.commands.each{command->
//            	output+= capability.name+",,,,\""+command.name+"("
//                if(command.arguments){
//                	command.arguments.each{argument->
//                    	output+=(argument as String)+","
//                    }
//                }
//                output+= ")\"\r\n"
//            }
        }
    }
    output+="</thing:thing-descriptions>"
    render contentType: "text/xml", data: output, status: 200
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
	log.debug "update, request: ${request.JSON}, params: ${params}, id: $params.id"
	def commands = request.JSON
    if(!commands) commands = [[name: params?.command]]
    commands.each{command ->
    	log.trace command.dump()
       	log.trace params.id+" "+command.name+" "+command?.arguments
        executeDeviceCommand(params.id,command.name,command?.arguments)
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
    typeId: device.device.typeId,
    displayName: device.displayName, 
    name: device.name, 
    label: device.label,
    capabilities: device.capabilities.collect{capabilityToJson(device, it)},
    supportedCommands: device.supportedCommands.collect{commandToJson(it)},
    supportedAttributes: device.supportedAttributes.collect{attributeToJson(device, it)},
    currentValues: device.supportedAttributes.collect{deviceAttributeValuesToMap(device, it)}
    ]
    if(isToggleable(device)){
    	deviceMap.supportedCommands.add([name: "toggle"])
    }
    return deviceMap
}

private isToggleable(device){
	if(device.hasCapability("Switch")) return true
    if(device.hasCapability("Door Control")) return true
    return false
}

private capabilityToJson(device, cap){
	def capMap = [name: cap.name]
    if(cap.attributes){
        capMap.attributes = cap.attributes.collect{attributeToJson(device, it)}
    }
    if(cap.commands){
        capMap.commands = cap.commands.collect{commandToJson(it)}
    }
    return capMap;
}

private attributeToJson(device, attr){
	def attribute = [name: attr.name, dataType: attr.dataType];
    def current = device.currentState(attr.name)
    if(current && current.unit){
    	attribute.unit=current.unit;
    }
    if(attr.values){
    	attribute.values = attr.values;
    }
    return attribute;
}

private commandToJson(com){
	def command = [name: com.name]
    if(com.arguments.size > 0){
    	command.arguments = com.arguments.collect{it as String};
    }
    return command
}

private deviceAttributeValuesToMap(device, attr){
	def values = [name: attr.name, dataType: attr.dataType]
    values.value = device.currentValue(attr.name)
    def state = device.latestState(attr.name)
    if(state){
    	if(state.unit)values.unit = state.unit
        if(state.date)values.date = state.date.getTime();
    }
    return values;
}