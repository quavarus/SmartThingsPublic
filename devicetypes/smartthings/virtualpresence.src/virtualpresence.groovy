/**
 *  Copyright 2015 SmartThings
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
 *  VirtualPresence
 *
 *  Author: Joshua Henry
 *  Date: 2016-07-18
 */
 
metadata {
	definition (name: "VirtualPresence", namespace: "smartthings", author: "SmartThings") {
		capability "Presence Sensor"
		capability "Sensor"
        
        attribute "info", "string"
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}
    
    tiles(scale:2) {
		standardTile("presence", "device.presence", width: 6, height: 6, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.present", backgroundColor:"#53a7c0")
			state("not present", labelIcon:"st.presence.tile.not-present", backgroundColor:"#ebeef2")
		}
        valueTile("info", "device.info", width: 6, height: 1) {
            state "info", label:'${currentValue}', defaultState: true
        }
		main "presence"
		details "presence","info"
	}
}

def sendPresenceEvent(boolean present) {
	log.debug "Here in generatePresenceEvent!"
	def value = formatValue(present)
	def linkText = getLinkText(device)
	def descriptionText = formatDescriptionText(linkText, present)
	def handlerName = getState(present)

	def results = [
		name: "presence",
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName
	]
	log.debug "Generating Event: ${results}"
	sendEvent (results)
}

private String formatValue(boolean present) {
	if (present)
    	return "present"
	else
    	return "not present"
}

private formatDescriptionText(String linkText, boolean present) {
	if (present)
		return "$linkText has arrived"
	else
    	return "$linkText has left"
}

private getState(boolean present) {
	if (present)
		return "arrived"
	else
    	return "left"
}

def setInfo(value){
	log.debug "setInfo($value)"
    sendEvent(name: "info", value: value, displayed:false)
}