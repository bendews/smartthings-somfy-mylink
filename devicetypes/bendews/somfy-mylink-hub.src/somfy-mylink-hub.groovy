/**
 *  Somfy MyLink Hub
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
    definition (name: "Somfy MyLink Hub", namespace: "bendews", author: "contact@bendews.com") {
        capability "Actuator"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        command "createChildDevices"
    }


    preferences {
        section("IP Address of MyLink") {
            input("ipAddress", "string", title:"Somfy MyLink IP Address", required:true, displayDuringSetup:true)
            input("ipPort", "string", title:"Somfy MyLink Port (default: 44100)", defaultValue:44100, required:true, displayDuringSetup:true)
        }
        section("System ID") {
            input("systemID", "string", title:"Somfy MyLink System ID", required:true, displayDuringSetup:true)
        }
        section("Devices") {
            input("device1Name", "string", title:"Device 1 Name", displayDuringSetup:true)
            input("device1ID", "string", title:"Device 1 ID", displayDuringSetup:true)
            input("device2Name", "string", title:"Device 2 Name", displayDuringSetup:true)
            input("device2ID", "string", title:"Device 2 ID", displayDuringSetup:true)
            input("device3Name", "string", title:"Device 3 Name", displayDuringSetup:true)
            input("device3ID", "string", title:"Device 3 ID", displayDuringSetup:true)
            input("device4Name", "string", title:"Device 4 Name", displayDuringSetup:true)
            input("device4ID", "string", title:"Device 4 ID", displayDuringSetup:true)
            input("device5Name", "string", title:"Device 5 Name", displayDuringSetup:true)
            input("device5ID", "string", title:"Device 5 ID", displayDuringSetup:true)
        }
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale:2) {
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width:2, height:2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
    }
}

def getDNI(String ipAddress, String port){
    log.debug "Generating DNI"
    String ipHex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    String portHex = String.format( '%04X', port.toInteger() )
    String newDNI = ipHex + ":" + portHex
    return newDNI
}

def setDNI(){
    log.debug "Setting DNI"
    String ip = settings.ipAddress
    String port = settings.ipPort
    String newDNI = getDNI(ip, port)
    device.setDeviceNetworkId("${newDNI}")
}

def getRandomInt(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

def sendCommand(String commandMethod, String deviceID){
    def randomID = getRandomInt(20, 100)
    def commandData = [ id: randomID, method: commandMethod, params: [auth: settings.systemID,targetID: deviceID]]
    def commandJson = groovy.json.JsonOutput.toJson(commandData)
    log.debug(commandJson)
    sendHubCommand(new physicalgraph.device.HubAction(commandJson, physicalgraph.device.Protocol.LAN, device.deviceNetworkId))
}

def createChildDevices() {
	    log.debug("createChildDevices")
        def devices = [:]
        devices[settings.device1ID] = settings.device1Name
        devices[settings.device2ID] = settings.device2Name
        devices[settings.device3ID] = settings.device3Name
        devices[settings.device4ID] = settings.device4Name
        devices[settings.device5ID] = settings.device5Name
        devices.remove(null)
	    log.debug(devices)
        devices.each {deviceID, deviceName ->
            def deviceExists
            if (deviceID) {
                deviceExists = getChildDevices()?.find {
                    it.deviceNetworkId == deviceID
                }
            }
            if (!deviceExists) {
                log.debug("Creating Somfy Device: ${deviceID}")
                addChildDevice("bendews", "Somfy MyLink Shade", deviceID, device.hub.id, [
                        "label": "${deviceName}"
                ])
            }
        }
}

def childOpen(String deviceID) {
    log.debug("childOpen")
    sendCommand("mylink.move.up", deviceID)
}
def childClose(String deviceID) {
    log.debug("childClose")
    sendCommand("mylink.move.down", deviceID)
}
def childPresetPosition(String deviceID) {
    log.debug("childPresetPosition")
}

def installed() {
	log.debug("Installed")
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    // Prevent function from running twice on save
    if (!state.updated || now() >= state.updated + 5000){
        runIn(1, setDNI)
        runIn(3, createChildDevices)
    }
    state.updated = now()
}

def poll() {
    log.debug("Executing poll(), unscheduling existing")
}

def refresh() {
    log.debug("Refreshing")
    setDNI()
}