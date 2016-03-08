/**
 *  One App
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
    name: "One App",
    namespace: "quavarus",
    author: "Joshua Henry",
    description: "One App to rule them all.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "mainPage", title: "Conditions and Actions", install:true, uninstall: true)
    page(name: "conditionsPage", title: "When these are true...", install: false, uninstall: false, previousPage: "mainPage")
    page(name: "conditionPage", title: "Condition", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "deleteConditionPage", title: "", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "actionsPage", title: "Do these...", install: false, uninstall: false, previousPage: "mainPage")
    page(name: "actionPage", title: "Action", install: false, uninstall: false, previousPage: "mainPage")
    page(name: "deleteActionPage", title: "", install: false, uninstall: false, previousPage: "actionsPage")
}

def conditionTypes(){[
	"SunriseSunset":"Sunrise and Sunset",
    "Switch":"Switches",
    "Presence Sensor":"People (presence)"
]}

def conditionTypeAttributes(){[
	"Switch":"switch",
    "Presence Sensor":"presence"
]}

def conditionScopes(){[
all:"All",
any:"Any"
]}

def switchConditions(){[
    "state-on":"Are Currently On",
    "state-off":"Are Currently Off",
    "action-on":"Are Turned On",
    "action-off":"Are Turned Off"
]}

def presenceConditions(){[
    "action-present":"Have Arrived",
    "action-not present":"Have Left",
    "state-present":"Are Present",
    "state-not present":"Are Absent"
]}

def sunriseSunsetConditionFilter(){[
    "action":"When It's",
    "state":"If It's"
]}

def sunriseSunsetScopes(){[	
	before:"Before",
    after:"After"
]}

def sunriseSunsetConditions(conditionId){
def scope = settings."condition_${conditionId}_Filter"
if (scope=="action")
return [
    "action-sunrise":"Sunrise",
    "action-sunset":"Sunset"
]
if (scope=="state")
return [
	"state-sunrise":"Sunrise",
    "state-sunset":"Sunset"
]
}

def actionTypes(){[
    "Switch":"Switches",
    "Notify":"Send Notification"
]}

def switchActions(){[
    "on":"Turn On",
    "off":"Turn Off",
    "toggle":"Toggle On/Off"
]}

def notifyActions(){[
	"push":"Send Push Notification",
    "sms":"Send Text",
    "both":"Send Push Notification and SMS"
]}

def buildConditionTitle(conditionId){
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionScope = settings."condition_${conditionId}_Scope"
    
    if(!isConditionComplete(conditionId)) return "Please Configure"
    
    switch(conditionType){
        case "Switch":
        return conditionScopes()[conditionScope]+" "+conditionTypes()[conditionType]+" "+switchConditions()[conditionValue]
        break
        case "Presence Sensor":
        return conditionScopes()[conditionScope]+" "+conditionTypes()[conditionType]+" "+presenceConditions()[conditionValue]
        case "SunriseSunset":
        def conditionFilter = settings."condition_${conditionId}_Filter"
        def conditionOffset = settings."condition_${conditionId}_Offset"
        return sunriseSunsetConditionFilter()[conditionFilter]+" "+conditionOffset+" Mins "+sunriseSunsetScopes()[conditionScope]+" "+sunriseSunsetConditions(conditionId)[conditionValue]
        default:
        return "Missing Condition Label"
        break
    }
}

def buildActionTitle(actionId){
	def actionType = settings."action_${actionId}_Type"
    def actionValue = settings."action_${actionId}_Value"
    
    if(!isActionComplete(actionId)) return "Please Configure"
    
    switch(actionType){
        case "Switch":
        return switchActions()[actionValue]+" "+actionTypes()[actionType]
        break
        case "Notify":
        return notifyActions()[actionValue]
        break
    }
}

def areConditionsDefined(){
	if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(!isConditionComplete(conditionId))return false
     }
     return areTriggerConditionsDefined();
    }
    return false;
}

def areTriggerConditionsDefined(){
	if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(isConditionComplete(conditionId)){
        	def conditionValue = settings."condition_${conditionId}_Value"
            if(conditionValue.startsWith("action"))return true
        }
     }
    }
    return false;
}

def isConditionComplete(conditionId){

	log.debug "isConditionComplete(${conditionId})"
	
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionValue = settings."condition_${conditionId}_Value"
    log.debug "conditionType:${conditionType}"
    log.debug "conditionValue:${conditionValue}"
    if(!conditionType)return false;
    if(!conditionValue)return false;
    
    switch(conditionType){
    	case "Switch":
        case "Presence Sensor":
        	def conditionScope = settings."condition_${conditionId}_Scope"
    		def conditionDevices = settings."condition_${conditionId}_Devices"
    		log.debug "conditionDevices:${conditionDevices}"
    		
            if(!conditionDevices)return false;
            if(!conditionScope)return false;
            if(!conditionDevices[0].hasCapability(conditionType))return false;
        
        break
        case "SunriseSunset":
        	def conditionScope = settings."condition_${conditionId}_Scope"
            def conditionOffset = settings."condition_${conditionId}_Offset"
            def conditionFilter = settings."condition_${conditionId}_Filter"
            if(!conditionOffset)return false;
            if(!conditionFilter)return false;
            if(!conditionScope)return false;
        break
        default:
        return false
    }
    
    log.debug "isConditionComplete(${conditionId})=true"
    
	true
}

def newUID(){
now()+""
}

def areActionsDefined(){
	if(state.actions && state.actions.size()>0){
     for (actionId in state.actions) {
     	if(!isActionComplete(actionId))return false
     }
     return true;
    }
    return false;
}

def isActionComplete(actionId){
log.debug "isActionComplete(${actionId})"
	
	def actionType = settings."action_${actionId}_Type"
    def actionValue = settings."action_${actionId}_Value"
       
    log.debug "actionType:${actionType}"
    log.debug "actionValue:${actionValue}"
   
   	if(!actionType)return false;
    if(!actionValue)return false;
    
    //validate devices
    if(["Switch"].contains(actionType)){
    	def actionDevices = settings."action_${actionId}_Devices"
   		log.debug "actionDevices:${actionDevices}"
    
    	if(!actionDevices)return false;
        if(!actionDevices[0].hasCapability(actionType))return false;
    }
   
    //validate special actions
    if(actionType=="Notify"){
    	if(!notifyActions().containsKey(actionValue))return false;
        def notifyMessage = settings."action_${actionId}_Message"
        if(!notifyMessage) return false
        if(["sms","both"].contains(actionValue)){
        	def notifyPhone = settings."action_${actionId}_Phone"
            if(!notifyPhone)return false;
        }
    }
   	
    
    log.debug "isActionComplete(${actionId})=true"
    
	true
}

def mainPage(params){
	dynamicPage(name: "mainPage", title: "Conditions and Actions", install:true, uninstall: true) {
  
  		section(){
    		label title: "Assign a name", required: false
        }
  
  		section(){
            href(name: "toConditions", page: "conditionsPage", title: "When these are true...", description: "", state: (areConditionsDefined() ? "complete" : "incomplete"))
            href(name: "toActions", page: "actionsPage", title: "Do these...", description: "", state: (areActionsDefined() ? "complete" : "incomplete"))
        }
	}
}

def conditionsPage(params){
		dynamicPage(name: "conditionsPage", title: "Conditions", uninstall: false) {
  
		section() {
        	if(state.conditions){
                for (conditionId in state.conditions) {
                    def conditionParams = [conditionId:conditionId]
                    href(name: "toAddCondition${conditionId}", page: "conditionPage", title: buildConditionTitle(conditionId), params: conditionParams, description: "", state: (isConditionComplete(conditionId) ? "complete" : "incomplete"))
                }
            }
		}
        section(){
        	def newId = newUID()
			href(name: "toAddCondition${newId}", page: "conditionPage", title: "Add Condition", params: [conditionId:newId], description: "", state: (areTriggerConditionsDefined() ? "complete" : "incomplete"))
        }
    }
}

def conditionPage(params) {
	log.debug "conditionPage(${params})"
	def conditionId = params.conditionId as String ?: state.lastDisplayedConditionId
	state.lastDisplayedConditionId = conditionId
    
    if(!conditionId)return mainPage();
    
    if(!state.conditions){
    	state.conditions = [conditionId];
    }else if(!state.conditions.contains(conditionId)){
    	state.conditions << conditionId;
    }
    
    log.debug "conditionId:${conditionId}"
    log.debug "condition_${conditionId}_Type:"+settings."condition_${conditionId}_Type"
    
	dynamicPage(name:"conditionPage", title: "Condition") {
    	section() {
            input "condition_${conditionId}_Type", "enum", required:true, title: "What type of condition?", submitOnChange:true, options: conditionTypes()
        }
        
        switch(settings."condition_${conditionId}_Type"){
        	case "Switch":
            section(){
            	input "condition_${conditionId}_Scope", "enum", required:true, title: "Any or All?", options:conditionScopes()
                input "condition_${conditionId}_Devices", "capability.switch", required:true, multiple:true, title: "Of these Switches?"
                input "condition_${conditionId}_Value", "enum", required:true, title: "Are?", options:switchConditions()
            }
            break
            case "Presence Sensor":
            section(){
            	input "condition_${conditionId}_Scope", "enum", required:true, title: "Any or All?", options:conditionScopes()
                input "condition_${conditionId}_Devices", "capability.presenceSensor", required:true, multiple:true, title:"Of these People?"
                input "condition_${conditionId}_Value", "enum", required:true, title: "Are?", options:presenceConditions()
            }
            break
            case "SunriseSunset":
            section(){
            	input "condition_${conditionId}_Filter", "enum", submitOnChange:true, required:true, title: "When or If?", options:sunriseSunsetConditionFilter()
                input "condition_${conditionId}_Offset", "number", submitOnChange:true, required:true, title: "Minutes?"
                input "condition_${conditionId}_Scope", "enum", submitOnChange:true, required:true, title: "Before or After?", options:sunriseSunsetScopes()
                input "condition_${conditionId}_Value", "enum", required:true, title: "Sunrise or Sunset?", options:sunriseSunsetConditions(conditionId)
            }
            break
        }
        
        section(){
        	  href(name: "toDeleteCondition${conditionId}", page: "deleteConditionPage", title: "Delete", params: [conditionId:conditionId], description: "")
        }
	}
}

def deleteConditionPage(params){
    def conditionId = params.conditionId as String ?: state.lastDisplayedConditionId
    state.lastDisplayedConditionId = conditionId
    state.conditions.remove(conditionId)
    state.lastDisplayedConditionId = null;
    conditionsPage()
}

def actionsPage(params){
    dynamicPage(name: "actionsPage", title: "Actions", uninstall: false) {
    	section() {
        	if(state.actions){
                for (actionId in state.actions) {
                    def actionParams = [actionId:actionId]
                    href(name: "toAddAction${actionId}", page: "actionPage", title: buildActionTitle(actionId), params: actionParams, description: "", state: (isActionComplete(actionId) ? "complete" : "incomplete"))
                }
            }
		}
        section(){
        	def newId = newUID()
        	href(name: "toAddAction", page: "actionPage", title: "Add Action", params: [actionId:newId], description: "", state: (areActionsDefined() ? "complete" : "incomplete"))
        }
    }
}

def actionPage(params) {
	log.debug "actionPage(${params})"
	def actionId = params.actionId as String ?: state.lastDisplayedActionId
	state.lastDisplayedActionId = actionId
    
    if(!actionId)return mainPage();
    
    if(!state.actions){
    	state.actions = [actionId];
    }else if(!state.actions.contains(actionId)){
    	state.actions << actionId;
    }
    
	dynamicPage(name:"actionPage", title: "Action") {
		section() {
            input "action_${actionId}_Type", "enum", required:true, title: "What type of action?", submitOnChange:true, options: actionTypes()
        }
        
        switch(settings."action_${actionId}_Type"){
        	case "Switch":
            section(){
            	input "action_${actionId}_Devices", "capability.switch", required:true, multiple:true, title: "Set which Switches?"
                input "action_${actionId}_Value", "enum", required:true, title: "To?", options:switchActions()
            }
            break
            case "Notify":
            section(){
            	input "action_${actionId}_Value", "enum", required:true, submitOnChange:true, title:"Push? SMS? or Both?", options:notifyActions()
                if(["sms","both"].contains(settings."action_${actionId}_Value")){
                	input "action_${actionId}_Phone", "phone", required:true, title: "Phone Number?"                
                }
                input "action_${actionId}_Message", "text", required:true, title: "Message?"  
            }
            break
        }
        
        section(){
        	  href(name: "toDeleteAction${actionId}", page: "deleteActionPage", title: "Delete", params: [actionId:actionId], description: "")
        }
	}
}

def deleteActionPage(params){
def actionId = params.actionId as String ?: state.lastDisplayedActionId
state.lastDisplayedActionId = actionId
state.actions.remove(actionId)
state.lastDisplayedActionId = null;
actionsPage()
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {
    if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(isConditionComplete(conditionId)){
        	def conditionType = settings."condition_${conditionId}_Type"
        	def conditionValue = settings."condition_${conditionId}_Value"
            if(conditionValue.startsWith("action")){
            	switch(conditionType){
                    case "Switch":
                    case "Presence Sensor":
                        def conditionDevices = settings."condition_${conditionId}_Devices"
                        def conditionTypeAttribute = conditionTypeAttributes()[conditionType]
                        def conditionTypeAttributeState = conditionValue.replace("action-","")
                        def eventKey = conditionTypeAttribute+"."+conditionTypeAttributeState
                        log.debug "subscribing=${eventKey}"
                        subscribe(conditionDevices, eventKey, stateChangeHandler)
                    break
                    case "SunriseSunset":
                    	 def conditionTypeAttributeState = conditionValue.replace("action-","")
                         if(conditionTypeAttributeState=="sunrise"){
                             def eventKey = conditionTypeAttributeState+"Time"
                             subscribe(location, eventKey, sunriseTimeHandler)
                             scheduleTurnOn(conditionTypeAttributeState, location.currentValue(eventKey))
                         }else if (conditionTypeAttributeState=="sunset"){
                         	def eventKey = conditionTypeAttributeState+"Time"
                             subscribe(location, eventKey, sunsetTimeHandler)
                             scheduleTurnOn(conditionTypeAttributeState, location.currentValue(eventKey))
                         }else{
                         	log.error "unknown SunriseSunset action ${conditionTypeAttributeState}"
                         }
                         
                    break
                }
            }
        }
     }
    }
}

def sunsetTimeHandler(evt) {
    scheduleTurnOn("sunset",evt.value)
}

def sunriseTimeHandler(evt) {
    scheduleTurnOn("sunrise",evt.value)
}

def scheduleTurnOn(timeOfDay, sunsetString) {
    //get the Date value for the string
    def actualTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    
    if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(isConditionComplete(conditionId)){
        	def conditionType = settings."condition_${conditionId}_Type"
            if(conditionType=="SunriseSunset"){
            	def conditionValue = settings."condition_${conditionId}_Value"
                if(conditionValue.startsWith("action-")){
                	conditionValue = conditionValue.replace("action-","")
                    if(conditionValue==timeOfDay){
                    	def conditionScope = settings."condition_${conditionId}_Scope"
                        def conditionOffset = settings."condition_${conditionId}_Offset"
                        conditionOffset = conditionOffset*60*1000
                        if(conditionScope=="before"){
                        	conditionOffset = conditionOffset*-1
                        }
                        def offsetTime = new Date(actualTime.time + conditionOffset)
                        log.debug "Scheduling for: $offsetTime (actual is $actualTime)"
                        runOnce(offsetTime, stateChangeHandler)
                    }
                }
            }
        }
     }
   }
    

}

def stateChangeHandler(evt) {
    log.debug "stateChangeHandler(${evt.value})"
    if(checkConditionsPass()){
    	log.debug "conditions pass: executing action"
        executeActions()
    }
}

def checkConditionsPass(){
	if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(isConditionComplete(conditionId)){
        	def conditionType = settings."condition_${conditionId}_Type"
            switch(conditionType){
            	case "Switch":
                case "Presence Sensor":
                	if(!checkDeviceStateConditionPass(conditionId))return false
                break
                case "SunriseSunset":
                	if(!checkSunriseSunsetConditionPass(conditionId))return false
                break
            }
       }
     }
     return true
   }
   return false
}

def checkDeviceStateConditionPass(conditionId){
	log.debug "checkSwitchConditionPass(${conditionId})"
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionScope = settings."condition_${conditionId}_Scope"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionDevices = settings."condition_${conditionId}_Devices"           
    def conditionTypeAttribute = conditionTypeAttributes()[conditionType]
    def conditionTypeAttributeState = conditionValue.replace("action-","")
    conditionTypeAttributeState = conditionTypeAttributeState.replace("state-","")
    switch(conditionScope){
    	case "any":
        	return anyDevicesMatchAttributeState(conditionDevices,conditionTypeAttribute,conditionTypeAttributeState)
        break;
        case "all":
        	return allDevicesMatchAttributeState(conditionDevices,conditionTypeAttribute,conditionTypeAttributeState)
        break;
    }
    return false;

}

def anyDevicesMatchAttributeState(devices,attributeName,attributeValue){
	log.debug "anyDevicesMatchAttributeState(${devices},${attributeName},${attributeValue})"
	for(device in devices){
    	if(device.currentValue(attributeName).equals(attributeValue))return true;
    }
    return false;
}

def allDevicesMatchAttributeState(devices,attributeName,attributeValue){
	log.debug "allDevicesMatchAttributeState(${devices},${attributeName},${attributeValue})"
	for(device in devices){
    	if(!device.currentValue(attributeName).equals(attributeValue))return false;
    }
    return true;
}

def checkSunriseSunsetConditionPass(conditionId){
	log.debug "checkSunriseSunsetConditionPass(${conditionId})"
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionScope = settings."condition_${conditionId}_Scope"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionOffset = settings."condition_${conditionId}_Offset"           
	
    log.debug "checking - "+buildConditionTitle(conditionId)

    def timeOfDay = conditionValue.replace("action-","")
    timeOfDay = timeOfDay.replace("state-","")
    log.debug "timeOfDay $timeOfDay"
    
    //def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", location.currentValue("sunsetTime"))
    //def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", location.currentValue("sunriseTime"))
    def sunriseAndSunset = getSunriseAndSunset();
    def sunsetTime = sunriseAndSunset.sunset
    def sunriseTime = sunriseAndSunset.sunrise
    log.debug "ss $sunriseAndSunset"
    def offset = conditionOffset*60*1000
    if(conditionScope=="before")
    	offset=offset*-1
        
    //log.debug "sunrise: $sunriseTime"
    //log.debug "sunset: $sunsetTime"
        
    def offsetTime;
    if(timeOfDay=="sunrise"){
    	offsetTime = new Date(sunriseTime.time+offset)
        log.debug "offsetTime: $offsetTime"
        log.debug "nowTime: ${new Date(now())}"
        return (now()>offsetTime.time && now() < sunsetTime.time)
    }else{
    	offsetTime = new Date(sunsetTime.time+offset)
        log.debug "offsetTime: $offsetTime"
        log.debug "nowTime: ${new Date(now())}"
        return (now()>offsetTime.time && now() < sunriseTime.time)
    }
    
}

def executeActions(){
	if(state.actions && state.actions.size()>0){
     for (actionId in state.actions) {
     	if(isActionComplete(actionId)){
        	def actionType = settings."action_${actionId}_Type"
    		switch(actionType){
            	case "Switch":
                	def actionValue = settings."action_${actionId}_Value"
            		def actionDevices = settings."action_${actionId}_Devices"
                    executeDevicesCommand(actionDevices,actionValue,null)
                break
                case "Notify":
                	def message = settings."action_${actionId}_Message"
                    def actionValue = settings."action_${actionId}_Value"
                    def phone = settings."action_${actionId}_Phone"
                	executeNotification(actionValue, phone, message)
                break;
            }
        }
     }
   }
}

def executeDevicesCommand(devices,command, arguments){
	devices.each{
    	executeDeviceCommand(it,command,arguments)
    }
}

def isToggleable(device){
	if(device.hasCapability("Switch")) return true
    if(device.hasCapability("Door Control")) return true
    return false
}

def nextToggleCommand(device){
	if(device.hasCapability("Switch")){
		def value = device.currentValue("switch")
        return value=="on"?"off":"on"    
    }else if(device.hasCapability("Door Control")){
		def value = device.currentValue("door")
        if(value=="closed" || value=="closing") return "open"
        else return "close"
    }

}

def executeDeviceCommand(device,command,arguments){
	if(command=="toggle" && !device.hasCommand("toggle") && isToggleable(device)){
       command = nextToggleCommand(device)
    }

    if(arguments==null){
        device."$command"()
    }else{
        device."$command"(*arguments)
    }
}

def executeNotification(type, phone, message){
	sendNotification(message,[method:type,phone:phone])
}