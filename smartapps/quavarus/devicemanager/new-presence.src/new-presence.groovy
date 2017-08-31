/**
 *  New Presence
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
    name: "New Presence",
    namespace: "quavarus/DeviceManager",
    author: "Joshua Henry",
    description: "Create a virtual presence device to clone or combine other presence devices",
    category: "Convenience",
    parent: "quavarus/DeviceManager:Device Manager",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "mainPage", title: "", install:true, uninstall: true)   
    page(name: "optionsPage", title: "Options", install:false, uninstall: false)   
}

def mainPage(){
	dynamicPage(name: "mainPage", install:true, uninstall: true){
    	section(){
    		label title: "Name?", required: true
        }
    
    	section(){
        	input "presence", "capability.presenceSensor", required:true, multiple:true, title:"Sensors?"
        }
        
        section{
        	href(name: "toOptions", page: "optionsPage", title: "Options", description: "", state: "complete")
        }
    }
}

def optionsPage(){
	updateSetting("childCount",getChildDevices().size())
	dynamicPage(name:"optionsPage", title:"Other Options"){
    	section("Behaviour"){
        	input "departure", "enum", required:true, multiple:false, title:"Virtual Has Departed When?", defaultValue:"all", options:[any:"First Departs",all:"All Have Departed"]
            input "arrival", "enum", required:true, multiple:false, title:"Virtual Has Arrived When?", defaultValue:"any", options:[any:"First Arrives",all:"All Have Arrived"]
        }
    	section("Duplicates?"){
            input "childCount", "enum", required:true, multiple:false, title:"How Many Duplicates?", options:[1:"1",2:"2",3:"3",4:"4",5:"5"]
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
	def childDevice = addChildDevice("smartthings", "VirtualPresence", "${app.id}-${newUID()}",null,[name:app.label, completedSetup: true])

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
	subscribe(presence, "presence", stateChangeHandler)
    
    updateOnOffState()
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
    
    //monitor the phisical device on/off states and update the virtual device
	if(evt.name=="presence"){
		updateOnOffState(evt.value)
    }
}

def getInfo(){
	def deviceCount = countDevicesWithAttribute("presence")
    def onCount = countDevicesWithAttributeValue("presence","present")
    
    def info = ""
	if(onCount==0)info = "All Not Present"
    else if(onCount==deviceCount)info = "All Present"
    else info = "$onCount of $deviceCount Present"
    info
}

def updateChildrenInfo(){
	
    def info = getInfo();

	getChildDevices().each{
    	it.setInfo(info)
    }
}

def updateChildInfo(child){
	def info = getInfo();
    
    child.setInfo(info)
}

def countDevicesWithAttribute(attribute){
	def count = 0
    def devices = settings.presence
	for(device in devices)
    	if(device.hasAttribute(attribute))count++
    count
}

def countDevicesWithAttributeValue(attribute, value){
	def count = 0
    def devices = settings.presence
	for(device in devices)
    	if(device.currentValue(attribute)==value)count++
    count
}

def updateOnOffState(value){
	log.debug "updateOnOffState(${value})"
    if(!value)value="present"
    def mode = "any"
    if(value=="present")mode=settings.arrival;
    if(value=="not present")mode=settings.departure;
    
    if("all" == mode){
        if(allAre(value)){
            getChildDevices().each{it.sendPresenceEvent(value=="present")}
        }
    }else{
        if(anyAre(value)){
            getChildDevices().each{it.sendPresenceEvent(value=="present")}
        }
    }
}

def allAre(value){
	log.debug "allAre($value)"
    for (device in settings.presence){
        if(device.currentPresence!=value){
            log.debug "device $device is $device.currentPresence"
            return false
        }
    }
    return true;
}

def anyAre(value){
	log.debug "anyAre($value)"
    for (device in settings.presence){
        if(device.currentPresence==value){
            log.debug "device $device is $device.currentPresence"
            return true
        }
    }
    log.debug "no devices are $value"
    return false;
}

def refresh(device){
	log.debug "refresh(${device})"
    //settings.presence.each{it.refresh()}
}

