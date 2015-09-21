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
    category: "My Apps",
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
    "Switch":"Switches",
    "Presence Sensor":"People Come and Go"
]}

def switchConditions(){[
    "stateOn":"Currently On",
    "stateOff":"Currently Off",
    "actionOn":"Turned On",
    "actionOff":"Turned Off",
    "actionToggled":"Changed"
]}

def presenceConditions(){[
    "actionAnyArrived":"Any have Arrived",
    "actionAnyLeft":"Any have Left",
    "actionAllArrived":"All have Arrived",
    "actionAllLeft":"All have Left",
    "stateAnyPresent":"Any are Present",
    "stateAnyAbsent":"Any are Absent",
    "stateAllPresent":"All are Present",
    "stateAllAbsent":"All are Absent"
]}

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
    
    if(!isConditionComplete(conditionId)) return "Please Configure"
    
    switch(conditionType){
        case "Switch":
        return conditionTypes()[conditionType]+" are "+switchConditions()[conditionValue]
        break
        case "Presence Sensor":
        return presenceConditions()[conditionValue]
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
    def conditionDevices = settings."condition_${conditionId}_Devices"
    
    log.debug "conditionType:${conditionType}"
    log.debug "conditionValue:${conditionValue}"
    log.debug "conditionDevices:${conditionDevices}"
    
    if(!conditionType)return false;
    if(!conditionValue)return false;
    if(!conditionDevices)return false;
   	if(!conditionDevices[0].hasCapability(conditionType))return false;
    
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
            	input "condition_${conditionId}_Devices", "capability.switch", required:true, multiple:true, title: "When which Switches?"
                input "condition_${conditionId}_Value", "enum", required:true, title: "Are?", options:switchConditions()
            }
            break
            case "Presence Sensor":
            section(){
            	input "condition_${conditionId}_Devices", "capability.presenceSensor", required:true, multiple:true, title:"Which People?"
                input "condition_${conditionId}_Value", "enum", required:true, title: "Come and Go How?", options:presenceConditions()
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
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

// TODO: implement event handlers