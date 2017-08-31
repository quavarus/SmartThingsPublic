/**
 *  New Switch
 *
 *  Copyright 2015 Joshua Henry
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "New Switch",
    namespace: "quavarus/DeviceManager",
    author: "Joshua Henry",
    description: "Create a virtual light or switch that controls many physical lights or switches",
    category: "Convenience",
    parent: "quavarus/DeviceManager:Device Manager",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "mainPage", title: "", install:true, uninstall: true)   
    page(name: "optionsPage", title: "Options", install:false, uninstall: false)   
    page(name: "masterControlPage", title: "Master Control", install:false, uninstall: false)   
}

def mainPage(){
	dynamicPage(name: "mainPage", install:true, uninstall: true){
    	section(){
    		label title: "Name?", required: true
        }
    
    	section(){
        	input "switches", "capability.switch", required:true, multiple:true, title:"Switches?", submitOnChange:true
            if(switches){
            	input(name: "deviceType", type: "enum", title: "Switch Type?", defaultValue:selectBestDeviceType(), options: ["VirtualDimmer":"Dimmer Switch","VirtualSwitch":"On/Off Switch"])
            }
        }
        
        section{
        	href(name: "toOptions", page: "optionsPage", title: "Options", description: "", state: "complete")
        }
    }
}

def optionsPage(){
	updateSetting("childCount",getChildDevices().size())
	dynamicPage(name:"optionsPage", title:"Other Options"){
    	section{
        	href(name: "toMasterControl", page: "masterControlPage", title: "Master Control", description: "", state: "complete")
            input "childCount", "enum", required:true, multiple:false, title:"How Many Duplicates?", options:[1:"1",2:"2",3:"3",4:"4",5:"5"]
        }
    }
}

def masterControlPage(){
	dynamicPage(name:"masterControlPage", title:"Master Control"){
    	section(){
        	input "masters", "capability.switch", required:false, multiple:true, title:"Pick one to control the rest?"
            input "masterUpdates", "enum", required:false, multiple:false, title:"Master Update Direction?", description:"Both", options:[virtualToMaster:"Virtual Updates Master",masterToVirtual:"Master Updates Virtual",both:"Both"]
            input "stateChange", "enum", required:false, multiple:false, title:"Change to On/Off When?", description:"Any are turned On", options:[anyOn:"Any are turned On",anyOff:"Any are turned Off",all:"All are turned On/Off"]
        }
    }
}

def newUID(){
now()+""
}

private updateSetting(name, value) {
	app.updateSetting(name, value)
	settings[name] = value
}


def createVirtualDevice(){
	log.debug "createVirtualDevice()"
    log.debug "hubs ${location.hubs}"
  //  log.debug location.hubs.first()
	def childDevice = addChildDevice("smartthings", deviceType, "${app.id}-${newUID()}",null,[name:app.label, completedSetup: true])

    if (childDevice)
    {

        try {
            childDevice.save()
        }
        catch (e) { // do nothing
            log.debug "Error = ${e}"
        } 
    }
}

def updateVirtualDevices(){
	for(int i=getChildDevices().size(); i<settings.childCount.toInteger();i++){
    	createVirtualDevice()
    }
}

def selectBestDeviceType(){
	
    def deviceType="VirtualSwitch"
    if(switches){
        for (int i=0; i<switches.size();i++){
            if(switches[i].hasCapability("Switch Level")){
                deviceType = "VirtualDimmer"
            }
        }
    }
    deviceType
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	//create device on install it has no state to update
	createVirtualDevice()
    updateSetting("childCount",1)
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	updateVirtualDevices()
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(switches, "switch", stateChangeHandler)
    subscribe(switches, "level", stateChangeHandler)
    subscribe(masters, "switch", masterChangeHandler)
    subscribe(masters, "level", masterChangeHandler)
    
    updateOnOffState()
}

def masterChangeHandler(evt){
	if(updateMasterToVirtual() && evt.isStateChange()){
    	log.debug "master device changed $evt.name"
        //def remainingDevices = allDevices()
        //remainingDevices.remove(evt.device.id)
        log.debug "remainging Devices= $remainingDevices"
        //def changed = false
        
        switch(evt.name){
        	case "switch":
            	if(evt.value=="on")
            	//changed = turnPhysicalsOn(remainingDevices.values())
                on()
            	if(evt.value=="off")
            	//changed = turnPhysicalsOff(remainingDevices.values())
                off()
                break
            case "level":
            	//getChildDevice(app.id).sendLevelEvent(evt.value)
            	//changed = setPhysicalsLevel(remainingDevices.values(),evt.value)
                setLevel(evt.integerValue)
                break
        }
        //if(changed)return;
        return
    }else{
    	if(evt.name=="switch")refresh();
    }
	
}

def stateChangeHandler(evt){
//	log.debug "event source = $evt.source"
//    log.debug "eventAppid = $evt.installedSmartAppId"
//    log.debug "device.id = $evt.device.id"
//    log.debug "deviceId = $evt.deviceId"
//    log.debug "appId $app.id"
//    log.debug "childDeviceId = ${getChildDevice(app.id).id}"
	
    log.debug "incoming event from $evt.device.label $evt.name $evt.value"
	updateChildrenInfo()
    
	//if the master has changed update the others
    if(isMasterDevice(evt.device)){
        return
    }
    
    //monitor the phisical device on/off states and update the virtual device
	if(evt.name=="switch"){
		updateOnOffState(evt.value)
    }


}

def updateChildrenInfo(){
	
    def deviceCount = countDevicesWithAttribute("switch")
    def onCount = countDevicesWithAttributeValue("switch","on")
    
    def info = ""
	if(onCount==0)info = "All Off"
    else if(onCount==deviceCount)info = "All On"
    else info = "$onCount of $deviceCount On"

	getChildDevices().each{
    	it.setInfo(info)
    }
}

def updateChildInfo(child){
	def deviceCount = countDevicesWithAttribute("switch")
    def onCount = countDevicesWithAttributeValue("switch","on")
    
    def info = ""
	if(onCount==0)info = "All Off"
    else if(onCount==deviceCount)info = "All On"
    else info = "$onCount of $deviceCount On"
    
    child.setInfo(info)
}

def countDevicesWithAttribute(attribute){
	def count = 0
    def devices = settings.switches
	for(device in devices)
    	if(device.hasAttribute(attribute))count++
    count
}

def countDevicesWithAttributeValue(attribute, value){
	def count = 0
    def devices = settings.switches
	for(device in devices)
    	if(device.currentValue(attribute)==value)count++
    count
}

def updateVirtualToMaster(){
	def direction = settings?.masterUpdates
    if(!direction)direction = "both"
    (settings.masters &&(direction=="both"||direction=="virtualToMaster"))
}

def updateMasterToVirtual(){
	def direction = settings?.masterUpdates
    if(!direction)direction = "both"
    (settings.masters &&(direction=="both"||direction=="masterToVirtual"))
}

def updateOnOffState(value){
	log.debug "updateOnOffState(${value})"
	def mode = settings?.stateChange
    if(!mode)mode = "anyOn"

    switch(mode){
    	case "anyOn":
        if(anyAre("on")){
        	if(value == "on" || !value){
            	getChildDevices().each{it.sendOnEvent()}
            	if(updateVirtualToMaster())settings.masters.on()
            }
        }else{
        	if(value == "off" || !value){
            	getChildDevices().each{it.sendOffEvent()}
            	if(updateVirtualToMaster())settings.masters.off()
            }
        }
        break
        case "anyOff":
        if(anyAre("off")){
        	if(value == "off" || !value){
            	getChildDevices().each{it.sendOffEvent()}
            	if(updateVirtualToMaster())settings.masters.off()
            }
        }else{
        	if(value == "on" || !value){
            	getChildDevices().each{it.sendOnEvent()}
            	if(updateVirtualToMaster())settings.masters.on()
            }
        }
        break
        case "all":
        if(value && allAre(value)){
            if(value=="on"){
            	getChildDevices().each{it.sendOnEvent()}
                if(updateVirtualToMaster())settings.masters.on()
            }else{
                getChildDevices().each{it.sendOffEvent()}
                if(updateVirtualToMaster())settings.masters.off()
            }
        }
        break
    }
}

def isMasterDevice(device){
	if(settings.masters){
    	def isMaster = settings.masters.contains(device)
        log.debug "$device.label isMaster=$isMaster"
        return isMaster
    }
    false
}

def allAre(value){
	log.debug "allAre($value)"
    for (device in settings.switches){
        if(device.currentSwitch!=value){
            log.debug "device $device is $device.currentSwitch"
            return false
        }
    }
    return true;
}

def anyAre(value){
	log.debug "anyAre($value)"
    for (device in settings.switches){
        if(device.currentSwitch==value){
            log.debug "device $device is $device.currentSwitch"
            return true
        }
    }
    log.debug "no devices are $value"
    return false;
}

def on (){
    def changed = turnPhysicalsOn(settings.switches)
	if (!changed)getChildDevices().each{it.sendOnEvent()}
}

def on(device){
	log.debug "on(${device})"
	on()
}

def turnPhysicalsOn(devices){
	def changed = false;
    for (it in devices){
    	if(it.currentSwitch!="on"){
        	 log.debug "turning on $it.label"
        	it.on()
            changed = true;
        }
    }
    changed
}

def off(){
	def changed = turnPhysicalsOff(settings.switches)
	if(!changed)getChildDevices().each{it.sendOffEvent()}
}

def off(device){
	log.debug "off(${device})"
    off()
}

def turnPhysicalsOff(devices){
	def changed = false
    for (it in devices){
    	if(it.currentSwitch!="off"){
        	log.debug "turning off $it.label"
        	it.off()
            changed = true
        }
    }
    changed
}

def setLevel(Integer value){
	def changed = false;
    changed = setPhysicalsLevel(settings.switches,value)	
    if(changed)
	getChildDevices().each{it.sendLevelEvent(value)}
}

def setLevel(device,Integer value){
	log.debug "setLevel(${device},${value})"
   setLevel(value)
}

def setPhysicalsLevel(devices,Integer value){
	def changed = false
	for(dimmer in devices){
    	if(dimmer.hasCapability("Switch Level")){
            log.debug "dimmer level=$dimmer.currentLevel value=$value equal? ${value == dimmer.currentLevel}"
            if(value != dimmer.currentLevel){
                log.debug "setting level of $dimmer.label to $value"
                dimmer.setLevel(value)
                changed = true
            }
        }
    }
    changed
}

def poll(device){
	log.debug "poll(${device})"
}

def refresh(device){
	log.debug "refresh(${device})"
    settings.switches.each{it.refresh()}
}

//def allDevices(){
//def devices = [:]
//if(settings.switches)
//settings.switches.each{
//	devices[it.id]=it;
//}
//return devices
//}

def setIndicatorStatus(device,value){
	log.debug "setIndicatorStatus(${device}, ${value})"
    switch(value){
    	case "when on":
        settings.switches.each{
        	if(it.hasCommand("indicatorWhenOn"))
        	it.indicatorWhenOn()
        }
        break
        case "when off":
        settings.switches.each{
        	if(it.hasCommand("indicatorWhenOff"))
        	it.indicatorWhenOff()
        }
        break
        case "never":
        settings.switches.each{
        	if(it.hasCommand("indicatorNever"))
        	it.indicatorNever()
        }
        break
    }
	device.sendIndicatorStatusEvent(value)
}