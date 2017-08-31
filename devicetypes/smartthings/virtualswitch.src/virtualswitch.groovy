/**
 *  VirtualSwitch
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
metadata {
	definition (name: "VirtualSwitch", namespace: "smartthings", author: "Joshua Henry") {
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        
        attribute "info", "string"

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            tileAttribute ("device.info", key: "SECONDARY_CONTROL") {
				attributeState "info", label:'${currentValue}', action:"getInfo"
			}

		}
		
		standardTile("refresh", "device.switch", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main(["switch"])
		details(["switch","refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	// TODO: handle 'level' attribute
	// TODO: handle 'devices' attribute

}

// handle commands
def on() {
	log.debug "Executing 'on'"
	parent.on(this)
}

def sendOnEvent(){
	sendEvent(name: "switch", value: "on")
}

def off() {
	log.debug "Executing 'off'"
	parent.off(this)
}

def sendOffEvent(){
	sendEvent(name: "switch", value: "off")
}

def poll() {
	log.debug "poll()"
    parent.poll(this)
}

def refresh() {
	log.debug "refresh()"
    parent.refresh(this)
}

def setInfo(value){
	log.debug "setInfo($value)"
    sendEvent(name: "info", value: value, displayed:false)
}



