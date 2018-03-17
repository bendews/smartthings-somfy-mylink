/**
 *  Somfy MyLink Shade
 *  V 1.0 - 17/03/2018
 *
 *  Copyright 2018 Ben Dews - https://bendews.com
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
 *
 *	Changelog:
 *
 *  1.0 (17/03/2018) - Initial 1.0 Release. Window Shade Open and Close functions working.
 *
 *
 */

metadata {
    definition (name: "Somfy MyLink Shade", namespace: "bendews", author: "contact@bendews.com") {
        capability "Window Shade"
        capability "Switch"
        capability "Actuator"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale:2) {
        multiAttributeTile(name:"shade", type: "lighting", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#CCCCCC")
                attributeState("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", nextState: "opening")
                attributeState("open",    label:'${name}', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#E86D13", nextState: "closing")
                attributeState("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#E86D13")
                attributeState("closing", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC")
                attributeState("opening", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-opening", backgroundColor:"#E86D13")
            }
        }
        standardTile("shadeOpen", "device.windowShade", width:2, height:2) {
            state "default", label:'open', icon:"st.doors.garage.garage-open", backgroundColor:"#FFFFFF", action:"open", nextState: "opening", defaultState:true
            state "open", label:'open', icon:"st.doors.garage.garage-open", backgroundColor:"#E86D13", action:"open"
            state "opening", label:'opening', icon:"st.doors.garage.garage-opening", backgroundColor:"#E86D13", action:"presetPosition"
        }
        standardTile("shadeClose", "device.windowShade", width:2, height:2) {
            state "default", label:'close', icon:"st.doors.garage.garage-closed", backgroundColor:"#FFFFFF", action:"close", nextState: "closing", defaultState:true
            state "close", label:'close', icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", action:"close"
            state "closing", label:'closing', icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC", action:"presetPosition"
        }
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width:2, height:2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
    }
}

def updateState(data){
	log.debug("updateState")
	log.debug(data)
    sendEvent(name: data.name, value: data.value)
}

def installed() {
	log.debug("Installed")
}

def poll() {
    log.debug("Executing poll(), unscheduling existing")
}

def refresh() {
    log.debug("Refreshing")
}

def on(){
    log.debug("On")
    open()
}

def off(){
    log.debug("Off")
    close()
}

def open() {
    log.debug("Open")
    parent.childOpen(device.deviceNetworkId)
    runIn(1, updateState, [overwrite: false, data: [name: "switch", value: "on"]])
    runIn(8, updateState, [overwrite: false, data: [name: "windowShade", value: "open"]])
}

def close() {
    log.debug("Close")
    parent.childClose(device.deviceNetworkId)
    runIn(1, updateState, [overwrite: false, data: [name: "switch", value: "off"]])
    runIn(8, updateState, [overwrite: false, data: [name: "windowShade", value: "closed"]])
}

def presetPosition() {
    log.debug("PresetPosition")
    parent.childPresetPosition(device.deviceNetworkId)
}
