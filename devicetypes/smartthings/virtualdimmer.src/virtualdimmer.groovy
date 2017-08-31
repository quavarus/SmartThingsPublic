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
	definition (name: "VirtualDimmer", namespace: "smartthings", author: "Joshua Henry") {
		capability "Switch Level"
		capability "Actuator"
		capability "Indicator"
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
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
            tileAttribute ("device.info", key: "SECONDARY_CONTROL") {
				attributeState "info", label:'${currentValue}', action:"getInfo"
			}

		}
		standardTile("indicator", "device.indicatorStatus", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
		state "when off", action:"indicator.indicatorWhenOn", icon:"st.indicators.lit-when-off"
			state "when on", action:"indicator.indicatorNever", icon:"st.indicators.lit-when-on"
			state "never", action:"indicator.indicatorWhenOff", icon:"st.indicators.never-lit"
		}
		standardTile("refresh", "device.switch", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main(["switch"])
		details(["switch","indicator","refresh"])
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

def setLevel(value) {
	log.debug "setLevel(${value})"
    parent.setLevel(this,value)
}

def sendLevelEvent(value){
    sendEvent(name: "level", value: value, unit:"%")
}

def setLevel(value, duration) {
	log.debug "setLevel(${value},${duration})"
    parent.setLevel(this,value)
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

def indicatorWhenOn() {
	log.debug "indicatorWhenOn()"
	parent.setIndicatorStatus(this,"when on")
}

def sendIndicatorStatusEvent(value){
	sendEvent(name: "indicatorStatus", value: value, display: false)
}

def indicatorWhenOff() {
	log.debug "indicatorWhenOff()"
	parent.setIndicatorStatus(this,"when off")
}

def indicatorNever() {
	log.debug "indicatorNever()"
	parent.setIndicatorStatus(this,"never")
}


