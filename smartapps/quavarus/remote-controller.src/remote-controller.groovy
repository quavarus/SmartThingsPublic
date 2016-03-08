/**
 *  Remote Controller
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
    name: "Remote Controller",
    namespace: "quavarus",
    author: "Joshua Henry",
    description: "A Better MiniMote Controller",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


/**********
 * Setup  *
 **********/
preferences {
	page(name: "mainPage", title: "", nextPage: "buttonsPage", uninstall: true) {
    	
        section(){
    	label title: "Assign a name", required: false
        }
    
		section("Use the buttons on this remote") {
			input "remote", "capability.button", required: true, title: "Remote"
            input "buttonCount", "number", required: true, title: "How many buttons does it have?"
		}
		section("To control these lights") {
			input "lights", "capability.switch", multiple: true, required: false, title: "Lights, switches & dimmers"
		}
	}
	page(name: "buttonsPage", title: "Buttons", install: true, uninstall: true)
	page(name: "buttonPage", title: "Button", install: false, uninstall: false, previousPage: "buttonsPage")
    page(name: "actionPage", install: false, uninstall: false, previousPage: "buttonPage")
	page(name: "saveStatesPage", install: false, uninstall: false, previousPage: "actionPage")
}


def buttonsPage() {
	log.debug "buttonsPage()"
	dynamicPage(name:"buttonsPage") {
		section {
			for (num in 1..buttonCount) {
            	def params = [buttonId:num as Integer]
				href(name: "toButtonPage${num}", page: "buttonPage", title: buttonName(num), params: params, description: "", state: (sceneIsDefined(num) ? "complete" : "incomplete"))
			}
		}
		section {
			href "buttonsPage", title: "Refresh", description: ""
		}
	}
}

def actionPage(params) {
	log.debug "actionPage(${params})"
    
    def buttonId = params.buttonId as Integer ?: state.lastDisplayedButtonId
    def buttonAction = params.buttonAction  ?: state.lastDisplayedButtonAction
	state.lastDisplayedButtonId = buttonId
    state.lastDisplayedButtonAction = buttonAction
    
    getDeviceCapabilities()
    
	dynamicPage(name:"actionPage", title: "When ${buttonName(buttonId)} is ${buttonAction}") {
		section("Lights") {
			lights.each {light ->
				input "onoff_${buttonId}_${buttonAction}_${light.id}", "enum", title: light.displayName, options: ["No Action","Toggle","On","Off"], description:"No Action", required: false
			}
		}

		section("Dimmers") {
			lights.each {light ->
				if (state.lightCapabilities[light.id] in ["level", "color"]) {
					input "level_${buttonId}_${buttonAction}_${light.id}", "enum", title: light.displayName, options: levels, description: "No Action", required: false
				}
			}
		}

		section("Colors (hue/saturation)") {
			lights.each {light ->
				if (state.lightCapabilities[light.id] == "color") {
					input "color_${buttonId}_${buttonAction}_${light.id}", "text", title: light.displayName, description: "No Action", required: false
				}
			}
		}
		section {
			href "saveStatesPage", title: "Record Current Device States", params: [buttonId:buttonId, buttonAction:buttonAction], description: ""
		}
	}
}

def buttonPage(params) {
	log.debug "buttonPage(${params})"
	def buttonId = params.buttonId as Integer ?: state.lastDisplayedButtonId
	state.lastDisplayedButtonId = buttonId
	dynamicPage(name:"buttonPage", title: "Button ${buttonId}") {
		section {
			input "buttonName${buttonId}", "text", title: "Button Name", required: false
		}
		section {
			href "actionPage", title: "Pressed", params: [buttonId:buttonId, buttonAction:"pushed"], description: "", state: sceneIsDefined(buttonId) ? "complete" : "incomplete"
            href "actionPage", title: "Held", params: [buttonId:buttonId, buttonAction:"held"], description: "", state: sceneIsDefined(buttonId) ? "complete" : "incomplete"
		}
	}
}

//def devicePage(params) {
//	log.debug "devicePage($params)"
//    
//	getDeviceCapabilities()
//
//	def buttonId = params.buttonId as Integer ?: state.lastDisplayedButtonId
//    def buttonAction = params.buttonAction  ?: state.lastDisplayedButtonAction
//	state.lastDisplayedButtonId = buttonId
//    state.lastDisplayedButtonAction = buttonAction
//
//	dynamicPage(name:"devicePage", title: "${buttonName(buttonId)} when ${buttonAction} Device States") {
//		section("Lights") {
//			lights.each {light ->
//				input "onoff_${buttonId}_${buttonAction}_${light.id}", "enum", title: light.displayName, options: ["Toggle","On","Off"]
//			}
//		}
//
//		section("Dimmers") {
//			lights.each {light ->
//				if (state.lightCapabilities[light.id] in ["level", "color"]) {
//					input "level_${buttonId}_${buttonAction}_${light.id}", "enum", title: light.displayName, options: levels, description: "", required: false
//				}
//			}
//		}
//
//		section("Colors (hue/saturation)") {
//			lights.each {light ->
//				if (state.lightCapabilities[light.id] == "color") {
//					input "color_${buttonId}_${buttonAction}_${light.id}", "text", title: light.displayName, description: "", required: false
//				}
//			}
//		}
//	}
//}

def saveStatesPage(params) {
	saveStates(params)
	actionPage(params)
}


/*************************
 * Installation & update *
 *************************/
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
	subscribe remote, "button", buttonHandler
}


/******************
 * Event handlers *
 ******************/
def buttonHandler(evt) {
	log.trace evt
    def buttonNumber = parseJson(evt.data) // why doesn't jsonData work? always returning [:]
	def value = evt.value
	log.debug "buttonEvent: $evt.name = $evt.value ($evt.data)"
	log.debug "button: $buttonNumber, value: $value"
    
	restoreStates(buttonNumber.buttonNumber, value)

}


/******************
 * Helper methods *
 ******************/
private Boolean sceneIsDefined(sceneId) {
	def tgt = "onoff_${sceneId}".toString()
	settings.find{it.key.startsWith(tgt)} != null
}

private updateSetting(name, value) {
	app.updateSetting(name, value)
	settings[name] = value
}

private closestLevel(level) {
	level ? "${Math.round(level/5) * 5}%" : "0%"
}

private saveStates(params) {
	log.trace "saveStates($params)"
	def buttonId = params.buttonId as Integer
    def buttonAction = params.buttonAction
	getDeviceCapabilities()

	lights.each {light ->
		def type = state.lightCapabilities[light.id]

		updateSetting("onoff_${buttonId}_${buttonAction}_${light.id}", switchStateToEnum(light.currentValue("switch")))

		if (type == "level") {
			updateSetting("level_${buttonId}_${buttonAction}_${light.id}", closestLevel(light.currentValue('level')))
		}
		else if (type == "color") {
			updateSetting("level_${buttonId}_${buttonAction}_${light.id}", closestLevel(light.currentValue('level')))
			updateSetting("color_${buttonId}_${buttonAction}_${light.id}", "${light.currentValue("hue")}/${light.currentValue("saturation")}")
		}
	}
}

private switchStateToEnum(state){
	def enumValue = ""
	if(state=="on")enumValue = "On"
    if(state=="off")enumValue = "Off"
    log.debug("switchStateToEnum ${state} = ${enumValue}")
    return enumValue
}


private restoreStates(buttonId, buttonAction) {
	log.trace "restoreStates($buttonId, ${buttonAction})"
	getDeviceCapabilities()

	lights.each {light ->
		def type = state.lightCapabilities[light.id]
		log.trace "onoff_${buttonId}_${buttonAction}_${light.id}"
		def onOff = settings."onoff_${buttonId}_${buttonAction}_${light.id}"
		log.debug "${light.displayName} is ${onOff}"
		if (onOff=="On") {
			light.on()
		}else if (onOff=="Off") {
			light.off()
		}else if (onOff=="Toggle"){
        	def currentState = light.currentValue("switch")
            if(currentState=="off")light.on()
            if(currentState=="on")light.off()
        }

		if (type != "switch") {
			def level = switchLevel(buttonId, buttonAction, light)

			if (type == "level") {
				log.debug "${light.displayName} level is '$level'"
				if (level != null) {
					light.setLevel(level)
				}
			}
			else if (type == "color") {
				def segs = settings."color_${buttonId}_${buttonAction}_${light.id}"?.split("/")
				if (segs?.size() == 2) {
					def hue = segs[0].toInteger()
					def saturation = segs[1].toInteger()
					log.debug "${light.displayName} color is level: $level, hue: $hue, sat: $saturation"
					if (level != null) {
						light.setColor(level: level, hue: hue, saturation: saturation)
					}
					else {
						light.setColor(hue: hue, saturation: saturation)
					}
				}
				else {
					log.debug "${light.displayName} level is '$level'"
					if (level != null) {
						light.setLevel(level)
					}
				}
			}
			else {
				log.error "Unknown type '$type'"
			}
		}


	}
}

private switchLevel(buttonId,action, light) {
	def percent = settings."level_${buttonId}_${action}_${light.id}"
	if (percent) {
		percent[0..-2].toInteger()
	}
	else {
		null
	}
}

private getDeviceCapabilities() {
	def caps = [:]
	lights.each {
		if (it.hasCapability("Color Control")) {
			caps[it.id] = "color"
		}
		else if (it.hasCapability("Switch Level")) {
			caps[it.id] = "level"
		}
		else {
			caps[it.id] = "switch"
		}
	}
	state.lightCapabilities = caps
}

private getLevels() {
	def levels = []
    levels << "No Action"
	for (int i = 0; i <= 100; i += 5) {
		levels << "$i%"
	}
	levels
}

private buttonName(num) {
	settings."buttonName${num}" ?: "Button ${num}"
}