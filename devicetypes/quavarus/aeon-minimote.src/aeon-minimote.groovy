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
 */
metadata {
	definition (name: "Aeon Minimote", namespace: "quavarus", author: "Joshua Henry") {
		capability "Actuator"
		capability "Button"
		capability "Configuration"
		capability "Sensor"
        
        command "press", ["number"]
        command "hold", ["number"]
        command "hold1"
        command "press1"
        command "hold2"
        command "press2"
        command "hold3"
        command "press3"
        command "hold4"
        command "press4"

		fingerprint deviceId: "0x0101", inClusters: "0x86,0x72,0x70,0x9B", outClusters: "0x26,0x2B"
		fingerprint deviceId: "0x0101", inClusters: "0x86,0x72,0x70,0x9B,0x85,0x84", outClusters: "0x26" // old style with numbered buttons
	}

	simulator {
		status "button 1 pushed":  "command: 2001, payload: 01"
		status "button 1 held":  "command: 2001, payload: 15"
		status "button 2 pushed":  "command: 2001, payload: 29"
		status "button 2 held":  "command: 2001, payload: 3D"
		status "button 3 pushed":  "command: 2001, payload: 51"
		status "button 3 held":  "command: 2001, payload: 65"
		status "button 4 pushed":  "command: 2001, payload: 79"
		status "button 4 held":  "command: 2001, payload: 8D"
		status "wakeup":  "command: 8407, payload: "
	}
	tiles {
		standardTile("button1Pushed", "device.button", width: 1, height: 1) {
			state "pushed", default: true, label: "Push Button 1", action:"press1", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        standardTile("button1Held", "device.button", width: 1, height: 1) {
			state "held",   default: true, label: "Hold Button 1", action:"hold1", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        valueTile("blank1", "device.power", decoration: "flat") {
			state "default", label:''
		}
        standardTile("button2Pushed", "device.button", width: 1, height: 1) {
			state "pushed", default: true, label: "Push Button 2", action: "press2", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        standardTile("button2Held", "device.button", width: 1, height: 1) {
			state "held",   default: true, label: "Hold Button 2", action: "hold2", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        valueTile("blank2", "device.power", decoration: "flat") {
			state "default", label:''
		}
        standardTile("button3Pushed", "device.button", width: 1, height: 1) {
			state "pushed", default: true, label: "Push Button 3", action: "press3", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        standardTile("button3Held", "device.button", width: 1, height: 1) {
			state "held",   default: true, label: "Hold Button 3", action: "hold3", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        valueTile("blank3", "device.power", decoration: "flat") {
			state "default", label:''
		}
        standardTile("button4Pushed", "device.button", width: 1, height: 1) {
			state "pushed", default: true, label: "Push Button 4", action: "press4", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        standardTile("button4Held", "device.button", width: 1, height: 1) {
			state "held",   default: true, label: "Hold Button 4", action: "hold4", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        valueTile("blank4", "device.power", decoration: "flat") {
			state "default", label:''
		}
		main "button1Pushed"
		details(["button1Pushed","button1Held","blank1","button2Pushed","button2Held","blank2","button3Pushed","button3Held","blank3","button4Pushed","button4Held","blank4"])
	}
}

def parse(String description) {
	def results = []
	if (description.startsWith("Err")) {
	    results = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description, [0x2B: 1, 0x80: 1, 0x84: 1])
		if(cmd) results += zwaveEvent(cmd)
		if(!results) results = [ descriptionText: cmd, displayed: false ]
	}
	// log.debug("Parsed '$description' to $results")
	return results
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def results = [createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)]

    results += configurationCmds().collect{ response(it) }
	results << response(zwave.wakeUpV1.wakeUpNoMoreInformation().format())

	return results
}

def press1(){
	press(1)
}

def hold1(){
	hold(1)
}

def press2(){
	press(2)
}

def hold2(){
	hold(2)
}

def press3(){
	press(3)
}

def hold3(){
	hold(3)
}

def press4(){
	press(4)
}

def hold4(){
	hold(4)
}

def press(buttonNumber){
	log.debug "press(${buttonNumber})"
	sendButtonEvent(buttonNumber,false)
}
def hold(buttonNumber){
	log.debug "hold(${buttonNumber})"
	sendButtonEvent(buttonNumber,true)
}

def buttonEvent(button, held) {
	button = button as Integer
	if (held) {
		createEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)
	} else {
		createEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
	}
}

def sendButtonEvent(button, held) {
	button = button as Integer
	if (held) {
		sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)
	} else {
		sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	Integer button = ((cmd.sceneId + 1) / 2) as Integer
	Boolean held = !(cmd.sceneId % 2)
	buttonEvent(button, held)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	Integer button = (cmd.value / 40 + 1) as Integer
	Boolean held = (button * 40 - cmd.value) <= 20
	buttonEvent(button, held)
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[ descriptionText: "$device.displayName: $cmd", linkText:device.displayName, displayed: false ]
}

def configurationCmds() {
	def cmds = []
	def hubId = zwaveHubNodeId
	(1..4).each { button ->
		cmds << zwave.configurationV1.configurationSet(parameterNumber: 240+button, scaledConfigurationValue: 1).format()
	}
	(1..4).each { button ->
		cmds << zwave.configurationV1.configurationSet(parameterNumber: (button-1)*40, configurationValue: [hubId, (button-1)*40 + 1, 0, 0]).format()
		cmds << zwave.configurationV1.configurationSet(parameterNumber: (button-1)*40 + 20, configurationValue: [hubId, (button-1)*40 + 21, 0, 0]).format()
	}
	cmds
}

def configure() {
	def cmds = configurationCmds()
	log.debug("Sending configuration: $cmds")
	return cmds
}
