/**
 *
 * This is a custom Device Type for the Aeon Smart Strip
 *
 * Installation
 *
 * Create a new device type (https://graph.api.smartthings.com/ide/devices)
 *    Capabilities:
 *        Configuration
 *        Refresh
 *        Polling
 *        Switch
 *    Custom Attribute
 *        switch1
 *        switch2
 *        switch3
 *        switch4
 *    Custom Command
 *        on1
 *        off1
 *        on2
 *        off2
 *        on3
 *        off3
 *        on4
 *        off4
 */


preferences {
    //input "operationMode1", "enum", title: "Boster Pump",
    //    metadata: [values: ["No",
    //                        "Uses Circuit-1",
    //                        "Variable Speed pump Speed-1",
   //                         "Variable Speed pump Speed-2",
   //                         "Variable Speed pump Speed-3",
   //                         "Variable Speed pump Speed-4"]]
  //  input "operationMode2", "enum", title: "Pump Type", metadata: [values: ["1 Speed Pump","2 Speed Pump"]]
}

metadata {
        // tile definitions
        tiles {
        	standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
				state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
				state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
            standardTile("switch1", "device.switch1",canChangeIcon: true) {
                state "on", label: '1 ${name}', action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                state "off", label: '1 ${name}', action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            }
        standardTile("switch2", "device.switch2",canChangeIcon: true) {
                        state "on", label: '2 ${name}', action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                        state "off", label: '2 ${name}', action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }
        standardTile("switch3", "device.switch3",canChangeIcon: true) {
                        state "on", label: '3 ${name}', action: "off3", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                        state "off", label: '3 ${name}', action:"on3", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }
        standardTile("switch4", "device.switch4",canChangeIcon: true) {
                        state "on", label: '4 ${name}', action: "off4", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                        state "off", label: '4 ${name}', action:"on4", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }
        //standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
        //                state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        //        }

                main "switch"
                details(["switch","switch1","switch2","switch3","switch4"])
        }
}

import physicalgraph.zwave.commands.*

/*
	Parse Capabilities
     0x20 COMMAND_CLASS_BASIC -- turns all on or off
     0x25 COMMAND_CLASS_SWITCH_BINARY -- turns all on or off
     0x32 COMMAND_CLASS_METER
     0x27 COMMAND_CLASS_SWITCH_ALL
     0x70 COMMAND_CLASS_CONFIGURATION
     0x85 COMMAND_CLASS_ASSOCIATION_V2
     0x72 COMMAND_CLASS_MANUFACTURER_SPECIFIC
     0x86 COMMAND_CLASS_VERSION
     0x60 COMMAND_CLASS_MULTI_CHANNEL_V2 COMMAND_CLASS_MULTI_INSTANCE
     0xEF COMMAND_CLASS_MARK
     0x82 COMMAND_CLASS_HAIL
*/
def parse(String description) {
   return parse(0,description);
}

def parse(int endpoint, String description){
  log.debug "parse($endpoint, $description)"
 def result = null
    def cmd = zwave.parse(description, [0x20:1, 0x25:1, 0x32:2, 0x27: 1, 0x70: 1, 0x85: 1, 0x72: 2, 0x86: 1, 0x60:3, 0xEF:1,  0x82:1])
    if (cmd) {
        result = zwaveEvent(endpoint, cmd)
    }
    //log.debug "Parse description $description"
    //log.debug "Parse cmd $cmd"
    //log.debug "Parse cmd payload $cmd.payload"
    log.debug "Parsed result $result"
    //log.debug "Parse returned ${result?.descriptionText}"
    //log.debug "Parse \"$description\" parsed to ${result.inspect()}"
    return result
}

 //zw device: 06, command: 600D, payload: 03 00 32 02 21 64 00 00 07 27 01 2C 00 00 07 24
//MultiChannelCmdEncap(bitAddress: false, command: 2, commandClass: 50, destinationEndPoint: 0, parameter: [33, 100, 0, 0, 7, 39, 1, 44, 0, 0, 7, 36], res01: false, sourceEndPoint: 3)
def parseEncap(multichannelv3.MultiChannelCmdEncap cmd){
	//log.debug cmd;
	def command = toHexByte(cmd.commandClass.toString())+toHexByte(cmd.command.toString())
	def payload = []
    cmd.parameter.each(){payload << toHexByte(it.toString())}
    	
	def description = [
    	device: "00",
        command: command,
        payload: payload.toString().replaceAll(",","")
    ]
    //log.debug description;
    return "zw "+description.toString().replaceAll("[\\[\\]]","").replaceAll(":", ": ");
}

def toHexByte(String base10){
	def hex = toHex(base10);
    if(hex.length()==2) return hex;
    if(hex.length()==1)	return "0"+hex;
    log.error("cannot convert decimal $base10 into byte it is too large")
}

def toHex(String base10){
    int decimal = base10.toInteger();
    String hex = Integer.toHexString(decimal);
	return hex;
}

//Reports
def zwaveEvent(int endpoint, switchbinaryv1.SwitchBinaryReport cmd)
{
	def result = [];
	log.debug("called SwitchBinaryReport zwaveEvent($endpoint, $cmd)")
    if(endpoint==0)
    result = createAllChangedReport(cmd.value ? "on" : "off", "digital")
    
    if(endpoint>0){
    	String state = cmd.value ? "on" : "off";
    	result << createEvent(createNumberedSwitchChange(endpoint, state, "digital"))
        if(remainingHaveSameState(endpoint, state)){
            result << createEvent(createAllSwitchChange(state, "digital"))
        }
    }
    return result;
}

def zwaveEvent(int endpoint, basicv1.BasicReport cmd)
{
	log.debug("called BasicReport zwaveEvent($cmd)")
    return createAllChangedReport(cmd.value ? "on" : "off", "physical")
}

def zwaveEvent(int endpoint, multichannelv3.MultiChannelCmdEncap cmd) {
    log.debug "zwaveEvent($endpoint, $cmd)"
    def result = [];
    result = parse(cmd.destinationEndPoint, parseEncap(cmd))
    //switch(cmd.commandClass){
    //	case 50:
    //    	log.debug("multi meter message")
    //    break;
    //    case 37:
    //        int index = cmd.destinationEndPoint;
    //        String state = cmd.parameter == [255] ? "on" : "off"
    //        result << createEvent(createNumberedSwitchChange(index, state, "digital"))
    //        //result << createEvent(createSwitchesState(index, state))
    //        if(remainingHaveSameState(index, state)){
    //            result << createEvent(createAllSwitchChange(state, "digital"))
    //        }
    //    break;
    //    default:
    //    	log.debug("unknown multi message class $cmd.commandClass")
    //    break;
    //}
    log.debug "encap result $result"
    return result
}

def zwaveEvent(int endpoint, meterv2.MeterReport cmd) {
	def result;
	if (cmd.scale == 0) {
		result = [name: "energy", value: cmd.scaledMeterValue, unit: "kWh"]
	} else if (cmd.scale == 1) {
		result = [name: "energy", value: cmd.scaledMeterValue, unit: "kVAh"]
	}
	else {
		result = [name: "power", value: Math.round(cmd.scaledMeterValue), unit: "W"]
	}
    return createEvent(result)
}

def zwaveEvent(int endpoint, cmd) {
        log.warn "Captured zwave command $cmd"
}

private boolean remainingHaveSameState(int index, String state){
	if (index!=1 && device.currentValue("switch1")!=state) return false;
    if (index!=2 && device.currentValue("switch2")!=state) return false;
    if (index!=3 && device.currentValue("switch3")!=state) return false;
    if (index!=4 && device.currentValue("switch4")!=state) return false;
    return true;
}


def createAllChangedReport(String allState, String type)
{
    def result = [];
	result << createEvent(createAllSwitchChange(allState, type))
    result << createEvent(createNumberedSwitchChange(1,allState, type))
    result << createEvent(createNumberedSwitchChange(2,allState, type))
    result << createEvent(createNumberedSwitchChange(3,allState, type))
    result << createEvent(createNumberedSwitchChange(4,allState, type))
    //result << createEvent(createAllSwitchesState(allState,type))
    return result;
}

def createSwitchesState(int index, String state){
    def switchStates = [];
    switchStates << (index==1 ? state : device.currentValue("switch1"))
    switchStates << (index==2 ? state : device.currentValue("switch2"))
    switchStates << (index==3 ? state : device.currentValue("switch3"))
    switchStates << (index==4 ? state : device.currentValue("switch4"))
	[
    	name: "switches", value: switchStates, type: "digital"
    ]
}

def createAllSwitchesState(String state, String type){
    def switchStates = [state,state,state,state];
	[
    	name: "switches", value: switchStates, type: type
    ]
}

def createAllSwitchChange(String state, String type){
	[
		name: "switch", value: state, type: type
	]
}



def createNumberedSwitchChange(int index, String state, String type){
	[
    	name: "switch$index", value: state, type: type
    ]
}

//Commands

//test
def test() {
	//toHex("5")
     //zw device: 06, command: 600D, payload: 03 00 32 02 21 64 00 00 07 27 01 2C 00 00 07 24
	//MultiChannelCmdEncap(bitAddress: false, command: 2, commandClass: 50, destinationEndPoint: 0, parameter: [33, 100, 0, 0, 7, 39, 1, 44, 0, 0, 7, 36], res01: false, sourceEndPoint: 3)
    //multichannelv3.MultiChannelCmdEncap encap = zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, command: 2, commandClass: 50, destinationEndPoint: 0, parameter: [33, 100, 0, 0, 7, 39, 1, 44, 0, 0, 7, 36], res01: false, sourceEndPoint: 3);
    //def description = parseEncap(encap);
    //log.debug description
    //log.debug zwave.parse(description, [0x20:1, 0x25:1, 0x32:2, 0x27: 1, 0x70: 1, 0x85: 1, 0x72: 2, 0x86: 1, 0x60:3, 0xEF:1,  0x82:1])
    log.debug zwave.inspect()
	//def cmds = [
    //	zwave.basicV1.basicSet(value:00).format(),
    //	zwave.versionV1.versionGet().format()//,
        //zwave.versionV1.versionCommandClassGet(requestedCommandClass:134).format(),
        //41
       // zwave.versionV1.versionCommandClassGet(requestedCommandClass:50).format(),
        //zwave.multiChannelV3.multiChannelEndPointFind(genericDeviceClass:37).format(),
       // zwave.multiChannelV3.multiChannelEndPointGet().format()
    //]
    //log.debug cmds
    //delayBetween(cmds)
    def cmds = []
       cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:1).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:2).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:3).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:4).format()
       // cmds << zwave.multiChannelV3.multiChannelEndPointGet().format()
       // cmds << zwave.configurationV2.configurationBulkGet().format()

        //cmds << zwave.multiChannelV3.multiInstanceGet(commandClass:37).format()
        log.debug "Sending ${cmds.inspect()}"
        delayBetween(cmds, 2300)
}

//test2
def test2() {
    //log.debug "HubID: $zwaveHubNodeId"
    //log.debug "Device name: $device.displayName"
    //log.debug "Device: $device.id"
    //log.debug "Device: $device.name"
    //log.debug "Device: $device.label"
    //log.debug "$device.data"
    log.debug "$device.rawDescription"
    //log.debug String.class
    //log.debug this.class
    //log.debug zwave.class
    Class<physicalgraph.zwave.Zwave> z = new Class<physicalgraph.zwave.Zwave>()
    zwave.metaClass.methods.each { method ->
      log.debug "${method.returnType.name} ${method.name}( ${method.parameterTypes*.name.join( ', ' )} )"
    }
}

//switch instance
def on(value) {
log.debug "value $value"
        delayBetween([
                zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: value, destinationEndPoint: value, commandClass:37, command:1, parameter:[255]).format(),
                zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: value, destinationEndPoint: value, commandClass:37, command:2).format()
        ], 2300)
}

def off(value) {
log.debug "value $value"
        delayBetween([
                zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: value, destinationEndPoint: value, commandClass:37, command:1, parameter:[0]).format(),
                zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: value, destinationEndPoint: value, commandClass:37, command:2).format()
        ], 2300)
}

//switch1
def on1() {
        on(1)
}

def off1() {
        off(1)
}

//switch2
def on2() {
        on(2)
}

def off2() {
        off(2)
}

//switch3
def on3() {
        on(3)
}

def off3() {
        off(3)
}

//switch4
def on4() {
        on(4)
}

def off4() {
        off(4)
}

//switch5
def on5() {
        on(5)
}

def off5() {
        off(5)
}

def on() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0xFF).format(),
			zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

def off() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0x00).format(),
			zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

/* 
def poll() {
    refresh()
}

def refresh() {
	delayBetween([
		zwave.switchBinaryV1.switchBinaryGet().format(),
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
}

def reset() {
	return [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format()
	]
}

def configure() {
	log.debug("configure")
	delayBetween([
		zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 4).format(),   // combined power in watts
		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: 300).format(), // every 5 min
		zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 8).format(),   // combined energy in kWh
		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: 300).format(), // every 5 min
		zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 0).format(),    // no third report
		zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 300).format() // every 5 min
	])
}


def poll() {
    refresh()
}

def refresh() {
 //       delayBetween([
 //   zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format(),
 //   zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:2).format(),
 //   zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:3, destinationEndPoint:3, commandClass:37, command:2).format(),
 //   zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:4, destinationEndPoint:4, commandClass:37, command:2).format(),
 //   zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:5, destinationEndPoint:5, commandClass:37, command:2).format()
 //   ], 2300)
}
           
          
def configure() {
    def operationMode = [
        value1: 7,
        value2: "two"
    ]

    // Set Operation Mode variables based on the user preferences
    if (operationMode1 == "No") {
        switch ( operationMode2 ) {
            case "1 Speed Pump":
                operationMode['value1'] = 0x00
                break
            case "2 Speed Pump":
                operationMode['value1'] = 0x02
                break
         }
    } else {
        switch ( operationMode2 ) {
            case "1 Speed Pump":
                operationMode['value1'] = 0x01
                break
            case "2 Speed Pump":
                operationMode['value1'] = 0x03
                break
        }
    }
log.debug operationMode
log.debug "$operationMode.value1"
}*/