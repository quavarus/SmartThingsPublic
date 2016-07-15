/**
 *  Solutions Damage & Danger Installer
 *
 *  Copyright 2016 SmartThings
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
    name: "Solutions Damage & Danger Installer",
    namespace: "smartthings",
    author: "smartthings",
    description: "Installs Damage & Danger on the SmartThings Dashboard",
    category: "SmartSolutions",
    singleInstance: true,
    parent:"SmartSolutions/Dangers:Damage & Danger",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Solution/dangers.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Solution/dangers@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Solution/dangers@2x.png")


preferences {
	page(name: "mainPage", title: "Install", install:true, uninstall: true){
    	section{
    		paragraph "Tap 'Done' to install the solution."
            paragraph "Tap 'Back' or 'Remove' to cancel."
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
}
