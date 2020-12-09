omfy MyLink Shade
 *  V 1.2 - 08/12/2020
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
 *  1.1 (28/03/2018) - Emulated partial opening (not very accurate due to ST), and also emulated Stop and PresetPosition buttons.
 *  1.2 (08/12/2020) - Updated to work on Hubitat, also added a command button to expose presetPosition.
 */

metadata {
    definition (name: "Somfy MyLink Shade", namespace: "bendews", author: "contact@bendews.com") {
        capability "Window Shade"
        capability "Switch"
        capability "Switch Level"
        capability "Actuator"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        command "stop"
        command "levelOpenClose"
        command "presetPosition"
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
                attributeState("partially open", label:'${name}', action:"close", icon:"st.Transportation.transportation13", backgroundColor:"#E86D13")
                attributeState("closing", label:'${name}', action:"stop", icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC")
                attributeState("opening", label:'${name}', action:"stop", icon:"st.doors.garage.garage-opening", backgroundColor:"#E86D13")
            }
        }
        standardTile("shadeOpen", "device.windowShade", width:2, height:2) {
            state "default", label:'open', icon:"st.doors.garage.garage-open", backgroundColor:"#FFFFFF", action:"open", nextState: "opening", defaultState:true
            state "open", label:'open', icon:"st.doors.garage.garage-open", backgroundColor:"#E86D13", action:"open"
            state "opening", label:'opening', icon:"st.doors.garage.garage-opening", backgroundColor:"#E86D13", action:"stop"
        }
        standardTile("shadeClose", "device.windowShade", width:2, height:2) {
            state "default", label:'close', icon:"st.doors.garage.garage-closed", backgroundColor:"#FFFFFF", action:"close", nextState: "closing", defaultState:true
            state "close", label:'close', icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", action:"close"
            state "closing", label:'closing', icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC", action:"stop"
        }
		standardTile("shadePreset", "device.refresh", width:2, height:2) {
            state "default", label:'preset', icon:"st.Health & Wellness.health9", backgroundColor:"#FFFFFF", action:"presetPosition"
        }
		standardTile("shadeStop", "device.refresh", width:2, height:2) {
            state "default", label:'stop', icon:"st.Electronics.electronics13", backgroundColor:"#FFFFFF", action:"stop"
        }
        controlTile("levelSlider", "device.level", "slider", height: 2, width: 2) {
            state "level", action:"setLevel"
        }
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width:2, height:2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
    }

    preferences {
        section("Settings") {
            input("timeToOpen", "string", title:"Time (in seconds) for blind to open", defaultValue: "8")
        }
    }
}

private timeToLevel(targetLevel){
    def currLevel = device.currentState("level")?.value.toFloat()
    def timeToOpen = getTimeToOpen()
    def percTime = timeToOpen / 100
    def levelDiff = currLevel - targetLevel
    def moveTime = levelDiff > 0 ? (percTime * levelDiff) : (percTime * -levelDiff)
    return moveTime
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
    Integer timeToOpen = getTimeToOpen()
    parent.childOpen(device.deviceNetworkId)
    sendEvent(name: "windowShade", value: "opening")
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: 100)
    runIn(timeToOpen, updateState, [overwrite: false, data: [name: "windowShade", value: "open"]])
}

def close() {
    log.debug("Close")
    Integer timeToOpen = getTimeToOpen()
    parent.childClose(device.deviceNetworkId)
    sendEvent(name: "windowShade", value: "closing")
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: 0)
    runIn(timeToOpen, updateState, [overwrite: false, data: [name: "windowShade", value: "closed"]])
}

def presetPosition() {
    log.debug("PresetPosition")
    def shadeState = device.currentState("windowShade")?.value
    log.debug(shadeState)
    if (!shadeState.equalsIgnoreCase("opening") && !shadeState.equalsIgnoreCase("closing")){
        parent.childPresetPosition(device.deviceNetworkId)
        sendEvent(name: "windowShade", value: "partially open")
    } else {
        log.debug("Blind moving. Preset Position ignored.")
    }
}

def stop(){
    log.debug("Stop")
    def shadeState = device.currentState("windowShade")?.value
    log.debug(shadeState)
    if (shadeState.equalsIgnoreCase("opening") || shadeState.equalsIgnoreCase("closing")){
        parent.childPresetPosition(device.deviceNetworkId)
        sendEvent(name: "windowShade", value: "partially open")
    } else {
        log.debug("Blind not moving. Stop ignored.")
    }
}

def getTimeToOpen(){
    if (settings.timeToOpen == null){
        return 8
    }
    return settings.timeToOpen.toFloat().toInteger()
}

def levelOpenClose(level) {
    log.debug("levelOpenClose (${level})")
    if (value) {   
	    setLevel(level)
    }
}

def setLevel() {
    log.debug("SetLevel()")
	setLevel(0)
}

def setLevel(level) {
    log.debug("Set Level (${level})")
    if (level <  10){
        log.debug("Less than 10")
        close()
    } else if (level > 90){
        log.debug("More than 90")
        open()
    } else {
        def moveTime = timeToLevel(level)
        def currLevel = device.currentState("level")?.value.toInteger()
        if (moveTime > 2){
            // reduce time by 1 second to account for delay
            moveTime = moveTime - 1
            log.debug("Scheduling move for: ${moveTime}")
            if (currLevel > level){
                parent.childClose(device.deviceNetworkId)
                runIn(moveTime, stop)
            } else {
                parent.childOpen(device.deviceNetworkId)
                runIn(moveTime, stop)
            }
            sendEvent(name: "windowShade", value: "opening")
            sendEvent(name: "switch", value: "on")
            sendEvent(name: "level", value: level, unit: "%")
            runIn(moveTime, updateState, [overwrite: false, data: [name: "windowShade", value: "partially open"]])
        }
    }
}

def setLevel(level, rate) {
    log.debug("Set Level (${level}, ${rate})")
	setLevel(level)
}
