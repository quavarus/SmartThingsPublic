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
            standardTile("selectedSwitchNumber", "device.selectedSwitchNumber",canChangeIcon: true) {
                state "default", label:'${currentValue}', action:"nextSwitch", icon:"st.motion.motion.inactive", backgroundColor: "#153591"
                
            }
            standardTile("selectedSwitch", "device.selectedSwitch", canChangeIcon: true) {
				state "on", label: 'Selected ${name}', action: "toggleSelected", icon: "st.switches.switch.on", backgroundColor: "#79b821"
				state "off", label: 'Selected ${name}', action: "toggleSelected", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
       
        //standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
        //                state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        //        }

                main "switch"
                details(["switch","selectedSwitchNumber","selectedSwitch"])
        }
}

import physicalgraph.zwave.commands.*


def installed()
{
	log.debug "installed device $device"
    init()
}

def updated()
{
	log.debug "updated"
    init();
}

def uninstalled()
{
	log.debug "uninstalled device $device"
}

def init(){
	log.debug "init";
    //
    //Integer switchIndex = device.currentValue('selectedSwitchNumber')?.toInteger();
    //if(switchIndex==null){
    	setSelectedSwitchNumber(1);
        setSelectedSwitchState(true);
    //}
    initSwitchState(true)
    //log.debug device.supportedAttributes
    //log.debug device.supportedCommands
}

def initSwitchState(boolean isOn){
	if (isOn){
    	on();
    }else{
    	off();
    }
	def state = isOn? "on" : "off"
    log.debug "setting all switches $state"
    sendEvent([
        name:"switches",value: [state,state,state,state]
    ])
}

int getSelectedSwitchNumber(){
	return device.currentValue("selectedSwitchNumber").toInteger();
}

def setSelectedSwitchNumber(Integer value){
	log.debug "setSelectedSwitch($value)"
	sendEvent([
    	name: "selectedSwitchNumber", value: value
    ])
}

def getSwitches(){
	def switches = device.currentValue("switches");
    switches = switches.substring(1,switches.length()-1)
    def states = switches.split(",")*.trim()
    return states.toList();
}

boolean getSwitchState(int index){
    def states = getSwitches()
    log.debug("|${states[index-1].trim()}|");
    
    return states[index-1]=="on"
}

def setSelectedSwitchState(boolean isOn){
	def state = isOn? "on" : "off"
    log.debug "setting selectedSwitch=$state"
    sendEvent([
    	name:"selectedSwitch", value: state
    ])
}

def nextSwitch(){
	Integer switchIndex = device.currentValue('selectedSwitchNumber')?.toInteger() ?: 0
    switchIndex++;
    if(switchIndex>4) switchIndex=1;
    log.debug "selectedSwitchNumber=$switchIndex"
    setSelectedSwitchNumber(switchIndex);
    boolean isOn = getSwitchState(switchIndex)
    setSelectedSwitchState(isOn)
    
}

def toggleSelected(){
	int index = getSelectedSwitchNumber();
    boolean isOn = getSwitchState(index);
    if(isOn){
    	off(index);
    }else{
    	on(index);
    }
}

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
    def result = null
    def cmd = zwave.parse(description, [0x20:1, 0x25:1, 0x32:0, 0x27: 1, 0x70: 1, 0x85: 1, 0x72: 1, 0x86: 1, 0x60:3, 0xEF:1,  0x82:1])
    if (cmd) {
        result = zwaveEvent(cmd)
    }
    log.debug "Parse description $description"
    log.debug "Parse cmd $cmd"
    log.debug "Parsed result $result"
    //log.debug "Parse returned ${result?.descriptionText}"
    //log.debug "Parse \"$description\" parsed to ${result.inspect()}"
    return result
}

//Reports
def zwaveEvent(switchbinaryv1.SwitchBinaryReport cmd)
{
	log.debug("called SwitchBinaryReport zwaveEvent($cmd)")
    return createAllChangedReport(cmd.value ? "on" : "off", "digital")
}

def zwaveEvent(basicv1.BasicReport cmd)
{
	log.debug("called BasicReport zwaveEvent($cmd)")
    return createAllChangedReport(cmd.value ? "on" : "off", "physical")
}

def zwaveEvent(multichannelv3.MultiChannelCmdEncap cmd) {
    log.debug "$cmd"
    def result = [];
    switch(cmd.commandClass){
    	case 50:
        	log.debug("multi meter message")
        break;
        case 37:
            int index = cmd.destinationEndPoint;
            String state = cmd.parameter == [255] ? "on" : "off"
            result << createEvent(createSelectedSwitchState(state, "digital"))
            result << createEvent(createSwitchesState(index, state, "digital"))
            if(remainingHaveSameState(index, state)){
                result << createEvent(createAllSwitchChange(state, "digital"))
            }
        break;
        default:
        	log.debug("unknown multi message class $cmd.commandClass")
        break;
    }
    return result
}

def zwaveEvent(meterv1.MeterReport cmd) {
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

def zwaveEvent(cmd) {
        log.warn "Captured zwave command $cmd"
}

private boolean remainingHaveSameState(int index, String state){
	def switches = getSwitches()
    index--;
    for(int i=0;i<4;i++){
    	if (index!=i && switches[i]!=state) return false;
    }
    return true;
}


def createAllChangedReport(String allState, String type)
{
    def result = [];
	result << createEvent(createAllSwitchChange(allState, type))
    result << createEvent(createAllSwitchesState(allState,type))
    result << createEvent(createSelectedSwitchState(allState, type))
    return result;
}

def createSelectedSwitchState(String state, String type){
	[
		name: "selectedSwitch", value: state, type: type
	]
}

def createSwitchesState(int index, String state, String type){
	log.debug "createSwitchesState($index, $state, $type)"
    def switchStates = getSwitches();
    switchStates[--index]=state;
	[
    	name: "switches", value: switchStates, type: type
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



//Commands

//test
def test() {
	def cmds = [
    	//zwave.versionV1.versionGet().format(),
        zwave.versionV1.versionCommandClassGet(requestedCommandClass:134).format(),
        41
       // zwave.versionV1.versionCommandClassGet(requestedCommandClass:50).format(),
        //zwave.multiChannelV3.multiChannelEndPointFind(genericDeviceClass:37).format(),
       // zwave.multiChannelV3.multiChannelEndPointGet().format()
    ]
    log.debug cmds
    delayBetween(cmds)
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:1).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:2).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:3).format()
       // cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint:4).format()
       // cmds << zwave.multiChannelV3.multiChannelEndPointGet().format()
       // cmds << zwave.configurationV2.configurationBulkGet().format()

        //cmds << zwave.multiChannelV3.multiInstanceGet(commandClass:37).format()
       // log.debug "Sending ${cmds.inspect()}"
       // delayBetween(cmds, 2300)
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
    log.debug String.class
    //log.debug this.class
    log.debug zwave.class
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