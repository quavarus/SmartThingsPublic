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
    page(name: "newConditionPage", title: "Condition", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "conditionPage", title: "Condition", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "testConditionsPage", title: "Test Conditions", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "deleteConditionPage", title: "", install: false, uninstall: false, previousPage: "conditionsPage")
    page(name: "actionsPage", title: "Do these...", install: false, uninstall: false, previousPage: "mainPage")
    page(name: "testActionsPage", title: "Do Actions", install: false, uninstall: false, previousPage: "actionsPage")
    page(name: "actionPage", title: "Action", install: false, uninstall: false, previousPage: "actionsPage")
    page(name: "newActionPage", title: "Action", install: false, uninstall: false, previousPage: "actionsPage")
    page(name: "deleteActionPage", title: "", install: false, uninstall: false, previousPage: "actionsPage")
}

def conditionTypes(){[
	"TimeOfDay":"Time of Day",
    "Switch":"Switches",
    "Presence Sensor":"People (presence)",
    "Mode":"Mode"
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

def modeConditions(){[
    "state-is":"Is Currently",
    "state-isNot":"Is Currently Not",
    "action-is":"Is Changed And",
    "action-isNot":"Is Changed And Not"
]}

def sunriseSunsetConditionFilter(){[
    "action":"When It's",
    "state":"If It's between"
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
        "action-sunset":"Sunset",
        "action-0000":"12:00 AM",
        "action-0015":"12:15 AM",
        "action-0030":"12:30 AM",
        "action-0045":"12:45 AM",
        "action-0100":"1:00 AM",
        "action-0115":"1:15 AM",
        "action-0130":"1:30 AM",
        "action-0145":"1:45 AM",
        "action-0200":"2:00 AM",
        "action-0215":"2:15 AM",
        "action-0230":"2:30 AM",
        "action-0245":"2:45 AM",
        "action-0300":"3:00 AM",
        "action-0315":"3:15 AM",
        "action-0330":"3:30 AM",
        "action-0345":"3:45 AM",
        "action-0400":"4:00 AM",
        "action-0415":"4:15 AM",
        "action-0430":"4:30 AM",
        "action-0445":"4:45 AM",
        "action-0500":"5:00 AM",
        "action-0515":"5:15 AM",
        "action-0530":"5:30 AM",
        "action-0545":"5:45 AM",
        "action-0600":"6:00 AM",
        "action-0615":"6:15 AM",
        "action-0630":"6:30 AM",
        "action-0645":"6:45 AM",
        "action-0700":"7:00 AM",
        "action-0715":"7:15 AM",
        "action-0730":"7:30 AM",
        "action-0745":"7:45 AM",
        "action-0800":"8:00 AM",
        "action-0815":"8:15 AM",
        "action-0830":"8:30 AM",
        "action-0845":"8:45 AM",
        "action-0900":"9:00 AM",
        "action-0915":"9:15 AM",
        "action-0930":"9:30 AM",
        "action-0945":"9:45 AM",
        "action-1000":"10:00 AM",
        "action-1015":"10:15 AM",
        "action-1030":"10:30 AM",
        "action-1045":"10:45 AM",
        "action-1100":"11:00 AM",
        "action-1115":"11:15 AM",
        "action-1130":"11:30 AM",
        "action-1145":"11:45 AM",
        "action-1200":"12:00 PM",
        "action-1215":"12:15 PM",
        "action-1230":"12:30 PM",
        "action-1245":"12:45 PM",
		"action-1300":"1:00 PM",
        "action-1315":"1:15 PM",
        "action-1330":"1:30 PM",
        "action-1345":"1:45 PM",
        "action-1400":"2:00 PM",
        "action-1415":"2:15 PM",
        "action-1430":"2:30 PM",
        "action-1445":"2:45 PM",
        "action-1500":"3:00 PM",
        "action-1515":"3:15 PM",
        "action-1530":"3:30 PM",
        "action-1545":"3:45 PM",
        "action-1600":"4:00 PM",
        "action-1615":"4:15 PM",
        "action-1630":"4:30 PM",
        "action-1645":"4:45 PM",
        "action-1700":"5:00 PM",
        "action-1715":"5:15 PM",
        "action-1730":"5:30 PM",
        "action-1745":"5:45 PM",
        "action-1800":"6:00 PM",
        "action-1815":"6:15 PM",
        "action-1830":"6:30 PM",
        "action-1845":"6:45 PM",
        "action-1900":"7:00 PM",
        "action-1915":"7:15 PM",
        "action-1930":"7:30 PM",
        "action-1945":"7:45 PM",
        "action-2000":"8:00 PM",
        "action-2015":"8:15 PM",
        "action-2030":"8:30 PM",
        "action-2045":"8:45 PM",
        "action-2100":"9:00 PM",
        "action-2115":"9:15 PM",
        "action-2130":"9:30 PM",
        "action-2145":"9:45 PM",
        "action-2200":"10:00 PM",
        "action-2215":"10:15 PM",
        "action-2230":"10:30 PM",
        "action-2245":"10:45 PM",
        "action-2300":"11:00 PM",
        "action-2315":"11:15 PM",
        "action-2330":"11:30 PM",
        "action-2345":"11:45 PM"
    ]
    if (scope=="state")
    return [
        "state-sunrise":"Sunrise",
        "state-sunset":"Sunset",
        "state-0000":"12:00 AM",
        "state-0015":"12:15 AM",
        "state-0030":"12:30 AM",
        "state-0045":"12:45 AM",
        "state-0100":"1:00 AM",
        "state-0115":"1:15 AM",
        "state-0130":"1:30 AM",
        "state-0145":"1:45 AM",
        "state-0200":"2:00 AM",
        "state-0215":"2:15 AM",
        "state-0230":"2:30 AM",
        "state-0245":"2:45 AM",
        "state-0300":"3:00 AM",
        "state-0315":"3:15 AM",
        "state-0330":"3:30 AM",
        "state-0345":"3:45 AM",
        "state-0400":"4:00 AM",
        "state-0415":"4:15 AM",
        "state-0430":"4:30 AM",
        "state-0445":"4:45 AM",
        "state-0500":"5:00 AM",
        "state-0515":"5:15 AM",
        "state-0530":"5:30 AM",
        "state-0545":"5:45 AM",
        "state-0600":"6:00 AM",
        "state-0615":"6:15 AM",
        "state-0630":"6:30 AM",
        "state-0645":"6:45 AM",
        "state-0700":"7:00 AM",
        "state-0715":"7:15 AM",
        "state-0730":"7:30 AM",
        "state-0745":"7:45 AM",
        "state-0800":"8:00 AM",
        "state-0815":"8:15 AM",
        "state-0830":"8:30 AM",
        "state-0845":"8:45 AM",
        "state-0900":"9:00 AM",
        "state-0915":"9:15 AM",
        "state-0930":"9:30 AM",
        "state-0945":"9:45 AM",
        "state-1000":"10:00 AM",
        "state-1015":"10:15 AM",
        "state-1030":"10:30 AM",
        "state-1045":"10:45 AM",
        "state-1100":"11:00 AM",
        "state-1115":"11:15 AM",
        "state-1130":"11:30 AM",
        "state-1145":"11:45 AM",
        "state-1200":"12:00 PM",
        "state-1215":"12:15 PM",
        "state-1230":"12:30 PM",
        "state-1245":"12:45 PM",
		"state-1300":"1:00 PM",
        "state-1315":"1:15 PM",
        "state-1330":"1:30 PM",
        "state-1345":"1:45 PM",
        "state-1400":"2:00 PM",
        "state-1415":"2:15 PM",
        "state-1430":"2:30 PM",
        "state-1445":"2:45 PM",
        "state-1500":"3:00 PM",
        "state-1515":"3:15 PM",
        "state-1530":"3:30 PM",
        "state-1545":"3:45 PM",
        "state-1600":"4:00 PM",
        "state-1615":"4:15 PM",
        "state-1630":"4:30 PM",
        "state-1645":"4:45 PM",
        "state-1700":"5:00 PM",
        "state-1715":"5:15 PM",
        "state-1730":"5:30 PM",
        "state-1745":"5:45 PM",
        "state-1800":"6:00 PM",
        "state-1815":"6:15 PM",
        "state-1830":"6:30 PM",
        "state-1845":"6:45 PM",
        "state-1900":"7:00 PM",
        "state-1915":"7:15 PM",
        "state-1930":"7:30 PM",
        "state-1945":"7:45 PM",
        "state-2000":"8:00 PM",
        "state-2015":"8:15 PM",
        "state-2030":"8:30 PM",
        "state-2045":"8:45 PM",
        "state-2100":"9:00 PM",
        "state-2115":"9:15 PM",
        "state-2130":"9:30 PM",
        "state-2145":"9:45 PM",
        "state-2200":"10:00 PM",
        "state-2215":"10:15 PM",
        "state-2230":"10:30 PM",
        "state-2245":"10:45 PM",
        "state-2300":"11:00 PM",
        "state-2315":"11:15 PM",
        "state-2330":"11:30 PM",
        "state-2345":"11:45 PM"
    ]
}

def actionTypes(){[
    "Switch":"Switches",
    "Notify":"Send Notification",
    "Run":"Run Routine"
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

def runActions(){
	def routines = location.helloHome?.getPhrases()*.label
    if (routines) routines.sort();
    def actions = [:];
    if(routines)
    	routines.each{actions.put(it,it)}
    return actions;
}

def buildConditionTitle(conditionId){
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionScope = settings."condition_${conditionId}_Scope"
    def conditionDevices = settings."condition_${conditionId}_Devices"
    
    if(!isConditionComplete(conditionId)) return "Please Configure"
    
    switch(conditionType){
        case "Switch":
        return conditionScopes()[conditionScope]+" "+conditionTypes()[conditionType]+" "+switchConditions()[conditionValue]
        break
        case "Mode":
        return "Mode "+modeConditions()[conditionValue]+" Set To "+conditionScope
        break
        case "Presence Sensor":
        return conditionScopes()[conditionScope]+" "+conditionTypes()[conditionType]+" "+presenceConditions()[conditionValue]
        case "TimeOfDay":
        def conditionFilter = settings."condition_${conditionId}_Filter"
        def label = sunriseSunsetConditionFilter()[conditionFilter]
        if(conditionFilter=="action"){
        	 def conditionOffset = settings."condition_${conditionId}_Offset"
             if(conditionOffset && conditionOffset.isNumber()){
             	label+=" "+Math.abs(conditionOffset)+" Minutes "+(conditionOffset<0? "Before" : "After")
             }
        }
        label+=" "+sunriseSunsetConditions(conditionId)[conditionValue]
        if(conditionFilter=="state"){
        	label+=" And "+sunriseSunsetConditions(conditionId)[conditionScope]
        }
        return label;
        default:
        return "Missing Condition Label"
        break
    }
}

def buildActionTitle(actionId){
	def actionType = settings."action_${actionId}_Type"
    def actionValue = settings."action_${actionId}_Value"
    def actionDelay = settings."action_${actionId}_Delay"
    
    if(!isActionComplete(actionId)) return "Please Configure"
    
    def delayLabel=""
    if(actionDelay && actionDelay.isNumber()){
    	delayLabel = "After ${actionDelay} Seconds "
    }
    
    switch(actionType){
        case "Switch":
        return delayLabel+switchActions()[actionValue]+" "+actionTypes()[actionType]
        break
        case "Notify":
        return delayLabel+notifyActions()[actionValue]
        break
        case "Run":
        return delayLabel+"${actionType} ${actionValue}"
        break
        default:
        return "Missing Action Label"
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
        case "Mode":
    		def conditionScope = settings."condition_${conditionId}_Scope"	
            if(!conditionScope)return false;
        break
        case "TimeOfDay":
        	def conditionScope = settings."condition_${conditionId}_Scope"
            def conditionFilter = settings."condition_${conditionId}_Filter"
            if(!conditionFilter)return false;
            if(conditionFilter=="state" && !conditionScope)return false;
        break
        default:
        return false
    }
    
    log.debug "isConditionComplete(${conditionId})=true"
    
	true
}

def newConditionId(){
	if(state.unusedConditions && state.unusedConditions.size()>0){
    	return state.unusedConditions.remove(0);
    }
    now()+""
}

def newActionId(){
    if(state.unusedActions && state.unusedActions.size()>0){
    	return state.unusedActions.remove(0);
    }
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
    def actionDelay = settings."action_${actionId}_Delay"
       
    log.debug "actionType:${actionType}"
    log.debug "actionValue:${actionValue}"
    log.debug "actionDelay:${actionDelay}"
   
    if(actionDelay){
    	if(!actionDelay.isNumber()||actionDelay<0) return false;
    }
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
	
//    def id = params?.conditionId as String ?: state.lastDisplayedConditionId
//	state.lastDisplayedConditionId = id
//    if(id){
    	//save condition
//        log.debug "save condition"
//        saveCondition();
//        if(params.conditionId)params.conditionId = null;
//        state.lastDisplayedConditionId = null;
//    }else{
//    	log.debug "no condition to save"
//    }

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
        	href(name: "toAddCondition", page: "newConditionPage", title: "Add Condition", params:[], description: "", state: (areTriggerConditionsDefined() ? "complete" : "incomplete"))
        }
        section("Test"){
        	href(name: "toTestConditions", page: "testConditionsPage", title: "Test Conditions", params: [], description: "", state: "complete")
        }
    }
}

//def saveCondition(){
//	def id = state.lastDisplayedConditionId;
//	if(!state.conditionMap){
//  	state.conditionMap = [:];
//    }
//    def condition = state.conditionMap.get(id);
//    if(!condition){
//    	condition = [:]
//    	state.conditionMap.put(id,condition)
//    }else{
//    	condition.clear();
//    }
//    
//    def variables = ["Type","Value","Devices","Scope","Offset","Filter"];
//    condition.put("id",id);
//    for(variable in variables){
//    	def conditionType = settings."condition_${id}_${variable}"
//        if(conditionType){
//        	condition.put(variable,variable=="Devices"? conditionType*.id :conditionType);
//        }
//    }
//    
//    log.debug "${condition}"
//}

def testConditionsPage(params){
	dynamicPage(name:"testConditionsPage", title:"Test Conditions"){
    	section(){
        	paragraph "Conditions Pass ${checkConditionsPass()}"
        }
    }
}

def newConditionPage(params){
	def newId = newConditionId()
 	conditionPage([conditionId:newId])
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
            case "TimeOfDay":
            section(){
            	input "condition_${conditionId}_Filter", "enum", submitOnChange:true, required:true, title: "When or If?", options:sunriseSunsetConditionFilter()
             //   input "condition_${conditionId}_Scope", "enum", submitOnChange:true, required:true, title: "Before or After?", options:sunriseSunsetScopes()
                if((settings."condition_${conditionId}_Filter")=="action")
                  input "condition_${conditionId}_Offset", "number", submitOnChange:true, required:false, title: "Minutes Before/After?"
                input "condition_${conditionId}_Value", "enum", required:true, title: "Time?", options:sunriseSunsetConditions(conditionId)
                if((settings."condition_${conditionId}_Filter")=="state"){
                	input "condition_${conditionId}_Scope", "enum", required:true, title: "And?", options:sunriseSunsetConditions(conditionId)
                }
            }
            break
            case "Mode":
            section(){
            	input "condition_${conditionId}_Value", "enum", required:true, title: "When Mode", options:modeConditions()
                input "condition_${conditionId}_Scope", "mode", required:true, title: "Set To"
            }
            break
        }
        
        section("Delete"){
        	  href(name: "toDeleteCondition${conditionId}", page: "deleteConditionPage", title: "Delete", params: [conditionId:conditionId], description: "")
        }
	}
}

def deleteConditionPage(params){
    def conditionId = params.conditionId as String ?: state.lastDisplayedConditionId
    state.lastDisplayedConditionId = conditionId
    deleteCondition(conditionId);
    state.lastDisplayedConditionId = null;
    conditionsPage()
}

def deleteCondition(conditionId){
	state.conditions.remove(conditionId)
    log.debug "${app}"
    
    def unusedConditions = state.unusedConditions
    if(unusedConditions==null){
    	unusedConditions = [];
    }
    unusedConditions << conditionId;
    log.debug "${unusedConditions}"
    state.unusedConditions = unusedConditions;
    
    app.updateSetting("condition_${conditionId}_Type", null);
    app.updateSetting("condition_${conditionId}_Scope", null);
    app.updateSetting("condition_${conditionId}_Devices", null);
    app.updateSetting("condition_${conditionId}_Value", null);
    app.updateSetting("condition_${conditionId}_Filter", null);
    app.updateSetting("condition_${conditionId}_Offset", null);
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
        	href(name: "toAddAction", page: "newActionPage", title: "Add Action", params: [], description: "", state: (areActionsDefined() ? "complete" : "incomplete"))
        }
        section("Test"){
        	href(name: "toTestActions", page: "testActionsPage", title: "Do Actions", params: [], description: "", state: "complete")
        }
    }
}

def testActionsPage(params){
	executeActions()
	dynamicPage(name:"testActionsPage", title:"Do Actions"){
    	section(){
        	paragraph "Actions Done"
        }
    }
}

def newActionPage(params){
	def newId = newActionId()
 	actionPage([actionId:newId])
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
        	input "action_${actionId}_Delay", "number", submitOnChange:false, required:false, title: "Run after ? Seconds"
        }
        section() {
            input "action_${actionId}_Type", "enum", required:true, title: "What type of action?", submitOnChange:true, options: actionTypes()
        }
        
        switch(settings."action_${actionId}_Type"){
        	case "Switch":
            section(){
            	input "action_${actionId}_Devices", "capability.switch", required:true, multiple:true, title: "Set which Switches?"
                input "action_${actionId}_Value", "enum", required:true, title: "To?", options:switchActions()
            }
            section("Options", hidable:true, hidden:true){
            	input "action_${actionId}_Force", "bool", defaultValue:false, required:true, title:"Force Update?"
            }
            break
            case "Run":
            section(){
            	input "action_${actionId}_Value", "enum", required:true, title: "Which Routine?", options:runActions()
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
        
        section("Delete"){
        	  href(name: "toDeleteAction${actionId}", page: "deleteActionPage", title: "Delete", params: [actionId:actionId], description: "")
        }
	}
}

def deleteActionPage(params){
def actionId = params.actionId as String ?: state.lastDisplayedActionId
state.lastDisplayedActionId = actionId
deleteAction(actionId)
state.lastDisplayedActionId = null;
actionsPage()
}

def deleteAction(actionId){
	state.actions.remove(actionId)
    log.debug "${app}"
    
    def unusedActions = state.unusedActions
    if(unusedActions==null){
    	unusedActions = [];
    }
    unusedActions << actionId;
    log.debug "${unusedActions}"
    state.unusedActions = unusedActions;
    
    app.updateSetting("action_${actionId}_Type", null);
    app.updateSetting("action_${actionId}_Delay", null);
    app.updateSetting("action_${actionId}_Message", null);
    app.updateSetting("action_${actionId}_Value", null);
    app.updateSetting("action_${actionId}_Devices", null);
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
                    case "TimeOfDay":
                    	 def conditionTypeAttributeState = conditionValue.replace("action-","")
                         if(conditionTypeAttributeState=="sunrise"){
                             def eventKey = conditionTypeAttributeState+"Time"
                             subscribe(location, eventKey, sunriseTimeHandler)
                             def offset = getConditionOffset(conditionId)
                             scheduleAction(location.currentValue(eventKey),offset)                             
                         }else if (conditionTypeAttributeState=="sunset"){
                         	def eventKey = conditionTypeAttributeState+"Time"
                             subscribe(location, eventKey, sunsetTimeHandler)
                             def offset = getConditionOffset(conditionId)
                             scheduleAction(location.currentValue(eventKey),offset)         
                         }else if (conditionTypeAttributeState.toInteger()>=0 && conditionTypeAttributeState.toInteger()<2400){
                         	log.debug("actionTime=${conditionTypeAttributeState}")
                            def offset = getConditionOffset(conditionId)
                            def actionTime = calculateHoursMinutes(conditionTypeAttributeState, offset)
                         	def cron = "0 ${actionTime[1]} ${actionTime[0]} 1/1 * ? *"
                         	schedule(cron,scheduledConditionHandler);
                         }else{
                         	log.error "unknown TimeOfDay action ${conditionTypeAttributeState}"
                         }                         
                    break
                    case "Mode":
                    	def conditionTypeAttributeState = conditionValue.replace("action-","")
                        subscribe(location,modeChangeHandler)
                    break;
                }
            }
        }
     }
    }
}

def calculateHoursMinutes(time, offset){
	log.debug "calculateHoursMinutes(time=${time}, offset=${offset})"
	def hours = time.take(2).toInteger()
    def minutes = time.drop(2).toInteger()
    minutes = minutes+(hours*60)
    minutes += offset
    if(minutes<0) minutes+=(24*60)
    hours = (int)minutes/60
    minutes = minutes-(hours*60)
    def value = [hours,minutes]
    log.debug "calculateHoursMinutes(time=${time}, offset=${offset})=${value}"
    return value
}

def sunsetTimeHandler(evt) {
    scheduleTurnOn("sunset",evt.value)
}

def sunriseTimeHandler(evt) {
    scheduleTurnOn("sunrise",evt.value)
}

def getConditionOffset(conditionId){
	def conditionOffset = settings."condition_${conditionId}_Offset"
    if(conditionOffset && conditionOffset.isNumber()){
        return conditionOffset
    }
    return 0
}

def scheduleAction(timeString,offset){
	def actualTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timeString)
    if(actualTime.time>now()){
        def offsetTime = new Date(actualTime.time + (offset*60*1000))
        log.debug "Scheduling actions for: $offsetTime (actual is $actualTime)"
        runOnce(offsetTime, scheduledConditionHandler)
        return true
    }
    return false
}

def scheduleTurnOn(timeOfDay, sunsetString) {
    //get the Date value for the string
    def actualTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    
    if(state.conditions && state.conditions.size()>0){
     for (conditionId in state.conditions) {
     	if(isConditionComplete(conditionId)){
        	def conditionType = settings."condition_${conditionId}_Type"
            if(conditionType=="TimeOfDay"){
            	def conditionValue = settings."condition_${conditionId}_Value"
                if(conditionValue.startsWith("action-")){
                	conditionValue = conditionValue.replace("action-","")
                    if(conditionValue==timeOfDay){
                        def conditionOffset = getConditionOffset(conditionId);
                        scheduleAction(sunsetString,conditionOffset)
                    }
                }
            }
        }
     }
   }
    

}

def scheduledConditionHandler(){
	log.debug "scheduled condition triggered";
	stateChangeHandler({});
}

def modeChangeHandler(evt){
    if(evt.name=="mode"){
    	stateChangeHandler(evt);
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
                case "TimeOfDay":
                	if(!checkTimeOfDayConditionPass(conditionId))return false
                break
                case "Mode":
                	if(!checkModeConditionPass(conditionId))return false;
                break;
            }
       }
     }
     return true
   }
   return false
}

def checkModeConditionPass(conditionId){
	log.debug "checkModeConditionPass(${conditionId})"
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionDevices = settings."condition_${conditionId}_Devices"           

    def conditionTypeAttributeState = conditionValue.replace("action-","")
    conditionTypeAttributeState = conditionTypeAttributeState.replace("state-","")
    if(conditionTypeAttributeState=="is"){
    	if(conditionDevices == location.mode)return true;
    }else if(conditionTypeAttributeState=="isNot"){
    	if(conditionDevices != location.mode)return true;
    }
    return false;
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

def checkTimeOfDayConditionPass(conditionId){
	log.debug "checkTimeOfDayConditionPass(${conditionId})"
	def conditionType = settings."condition_${conditionId}_Type"
    def conditionScope = settings."condition_${conditionId}_Scope"
    def conditionValue = settings."condition_${conditionId}_Value"
    def conditionFilter = settings."condition_${conditionId}_Filter"
    def conditionOffset = 0; //settings."condition_${conditionId}_Offset"           
	
    log.debug "checking - "+buildConditionTitle(conditionId)
    
    if(conditionFilter=="action")return true;

    def startTimeOfDay = conditionValue.replace("state-","")
    log.debug "startTimeOfDay $startTimeOfDay"
    
    def endTimeOfDay = conditionScope.replace("state-","")
    log.debug "endTimeOfDay $endTimeOfDay"
    
    def start = timeOfDayToInteger(startTimeOfDay);
    def end = timeOfDayToInteger(endTimeOfDay);
    def current = new Date().format("HHmm",location.timeZone).toInteger();
    if(end<=start){
    	end+=2400;
         if(current<start){
            current+=2400;
        }
    }
   
    log.debug "start:${start} end:${end} current:${current}"
    
    return (current>=start && current < end)
}

def timeOfDayToInteger(timeOfDay){
	if(timeOfDay=="sunrise"){
    	def sunriseAndSunset = getSunriseAndSunset();
        return sunriseAndSunset.sunrise.format("HHmm",location.timeZone).toInteger();
    }else if (timeOfDay=="sunset"){
    	def sunriseAndSunset = getSunriseAndSunset();
        return sunriseAndSunset.sunset.format("HHmm",location.timeZone).toInteger();
    }else{
    	return timeOfDay.toInteger();
    }
}

def executeActions(){
	if(state.actions && state.actions.size()>0){
     for (actionId in state.actions) {
     	def delay = getActionDelay(actionId)
        if(delay==0){
        	executeAction(actionId)
        }else{
        	runIn(delay,handleScheduledAction,[overwrite:false,data:[actionId:actionId]])
        }
     }
   }
}

def handleScheduledAction(data){
	executeAction(data.actionId)
}

def getActionDelay(actionId){
	def delay = settings."action_${actionId}_Delay"
    if(delay && delay.isNumber() && delay>0){
        return delay
    }
    return 0
}

def executeAction(actionId){
	if(isActionComplete(actionId)){
        def actionType = settings."action_${actionId}_Type"
        switch(actionType){
            case "Switch":
            def actionValue = settings."action_${actionId}_Value"
            def actionDevices = settings."action_${actionId}_Devices"
            def actionForce = settings."action_${actionId}_Force"
            if(actionForce){
            	executeDevicesCommand(actionDevices,null,actionValue,null)
            }else{
            	executeDevicesCommand(actionDevices,switchStateChangeCheck,actionValue,null)
            }
            break
            case "Run":
            def actionValue = settings."action_${actionId}_Value"
            executeRunRoutine(actionValue);
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

def switchStateChangeCheck(device, command, arguments){
	def currentState = device.currentSwitch
	return currentState!=command;
}

def executeDevicesCommand(devices,stateChangeCheckFunction,command, arguments){
	devices.each{
    	executeDeviceCommand(it,stateChangeCheckFunction,command,arguments)
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

def lastIndexOfEvent(device, attribute){
	for(int i=0;i<device.events().size();i++){
    	def event = device.events()[i]
        if (event.name && event.name==attribute)
        	return i
    }
    return -1
}

/**
* Returns PHYSICAL if device state was changed by a physical button press
* Returns DIGITAL if a device state was changed by a digital button press
* Returns the appid if a device state was changed by an smart app
**/
def findSource(device,eventIndex){
	def allowableTimespan = 500
	def event = device.events()[eventIndex]
    if (event.isPhysical())return "PHYSICAL"
	for (int i=(eventIndex+1);i<device.events().size();i++){
    	def compareEvent = device.events()[i]
        def timespan = event.date.getTime()-compareEvent.date.getTime()
        log.debug "timespan=${timespan}"
        if(timespan<allowableTimespan){
        	log.debug "source=${compareEvent.source}"
            def compareSource = compareEvent.source as String
            log.debug "compare=${compareSource=='APP_COMMAND'}"
        	if(compareSource=="APP_COMMAND"){
            	log.debug "found near app command"
            	if(event.value==compareEvent.value)return compareEvent.installedSmartAppId
            }
        }else{
        	return "DIGITAL"
        }
    }
}

def isLastActor(device, attribute){
    def lastAttributeChangeIndex = lastIndexOfEvent(device,attribute)
    def source = findSource(device,lastAttributeChangeIndex)
	app.id==source
}

def executeDeviceCommand(device,stateChangeCheckFunction,command,arguments){
	isLastActor(device,"switch")
	if(command=="toggle" && !device.hasCommand("toggle") && isToggleable(device)){
       command = nextToggleCommand(device)
    }
	def doCommand = true;
    if(stateChangeCheckFunction){
    	doCommand = "$stateChangeCheckFunction"(device,command,arguments)
    }
    if(doCommand){
        if(arguments==null){
            device."$command"()
        }else{
            device."$command"(*arguments)
        }
    }else{
    	log.debug "${device} is already ${command} so skipping "
    }
}

def executeNotification(type, phone, message){
	sendNotification(message,[method:type,phone:phone])
}

def executeRunRoutine(routine){
	location.helloHome?.execute(routine)
}