/**
 *  Device Manager
 *
 *  Copyright 2016 Joshua Henry
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
    name: "Device Manager",
    namespace: "quavarus/DeviceManager",
    author: "Joshua Henry",
    description: "Allows for grouping, splitting, duplicating, and mutating devices by creating virtual devices out of physical devices.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "mainPage", title: "Update", install: true, uninstall: true) {
        section("Create") {
            app(name: "switchApps", appName: "New Switch", namespace: "quavarus/DeviceManager", title: "New Switch", multiple: true)
            app(name: "presenceApps", appName: "New Presence", namespace: "quavarus/DeviceManager", title: "New Presence", multiple: true)
        }
    }
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