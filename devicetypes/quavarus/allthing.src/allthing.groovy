/**
 *  AllThing
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
metadata {
	definition (name: "AllThing", namespace: "quavarus", author: "Joshua Henry") {
		capability "Acceleration Sensor"
		capability "Actuator"
		capability "Alarm"
		capability "Audio Notification"
		capability "Battery"
		capability "Beacon"
		capability "Buffered Video Capture"
		capability "Button"
		capability "Carbon Dioxide Measurement"
		capability "Carbon Monoxide Detector"
		capability "Color Control"
		capability "Color Temperature"
		capability "Configuration"
		capability "Consumable"
		capability "Contact Sensor"
		capability "Door Control"
		capability "Energy Meter"
		capability "Estimated Time Of Arrival"
		capability "Garage Door Control"
		capability "Health Check"
		capability "Illuminance Measurement"
		capability "Image Capture"
		capability "Indicator"
		capability "Light"
		capability "Light"
		capability "Location Mode"
		capability "Lock"
		capability "Lock Codes"
		capability "Media Controller"
		capability "Momentary"
		capability "Motion Sensor"
		capability "Music Player"
		capability "Notification"
		capability "pH Measurement"
		capability "Polling"
		capability "Power"
		capability "Power Meter"
		capability "Power Source"
		capability "Presence Sensor"
		capability "Refresh"
		capability "Relative Humidity Measurement"
		capability "Relay Switch"
		capability "Samsung TV"
		capability "Sensor"
		capability "Shock Sensor"
		capability "Signal Strength"
		capability "Sleep Sensor"
		capability "Smoke Detector"
		capability "Sound Pressure Level"
		capability "Sound Sensor"
		capability "Speech Recognition"
		capability "Speech Synthesis"
		capability "Step Sensor"
		capability "Switch"
		capability "Switch Level"
		capability "Tamper Alert"
		capability "Temperature Measurement"
		capability "Test Capability"
		capability "Thermostat"
		capability "Thermostat Cooling Setpoint"
		capability "Thermostat Fan Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		capability "Thermostat Schedule"
		capability "Thermostat Setpoint"
		capability "Three Axis"
		capability "Timed Session"
		capability "Tone"
		capability "Touch Sensor"
		capability "TV"
		capability "Ultraviolet Index"
		capability "Valve"
		capability "Video Camera"
		capability "Video Capture"
		capability "Voltage Measurement"
		capability "Water Sensor"
		capability "Window Shade"
		capability "Zw Multichannel"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}
