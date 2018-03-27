# SmartThings Device Type - Somfy MyLink Hub

![Preview of device page in SmartThings interface](https://raw.githubusercontent.com/bendews/smartthings-somfy-mylink/master/preview.png)

This Device type allows the integration of Somfy Blinds operated via the Somfy MyLink hub.

It implements the SmartThings "Window Shade" and "Switch" capabilities, which means it will work with most Automations based on switching devices on/off, and will natively work with any Window Shade integrations.

Note that this is a WIP and there is still a lot to do 😊

## Features

### v1.0

- Automatically creates specified devices
- Allows opening and closing of shades

### v1.1
- "My" button functionality (Preset Position)
- "Stop" button functionality
- Emulated level control (I.E Set blind to 60% open). This is not very accurate at small increments due to the SmartThings platform.

## To Do

- Automatically search and add devices
- See if there is a possible way to read current state of device
- Add support for Scenes
- Add support for more device types (if requested)

## To Install

Either copy the device handlers directly in to the IDE, or add the repo:

- **Owner:** bendews
- **Name:** smartthings-somfy-mylink
- **Branch:** master

**Ensure you add both Device Handlers present in the repo**

Once the Device Handlers have been added, we can then add a virtual device via the IDE:

- Login to the IDE @ https://graph.api.smartthings.com/
- Click "My Devices"
- Click the "New Device" Button
- Enter a "Name" for the device, this can be whatever you want. (I.E "Somfy MyLink Hub")
- Enter a "Label" for the device, this is optional and can be whatever you want.
- Enter a "Device Network Id" This can be anything, however it must be different from any other device. This ID will change once the device has been configured.
- "Zigbee" Id should be left blank
- Select a "Type" from the dropdown, this should be "Somfy MyLink Hub"
- "Version" should be published
- "Location" should be your hub location, probably "Home"
- "Hub" should be your hub name
- "Group" leave as is
- Click Create

Once created, we can then open the settings for the device, and fill out the following values:

![Preview of device settings in SmartThings interface](https://raw.githubusercontent.com/bendews/smartthings-somfy-mylink/master/settings.png)

- **Somfy MyLink IP Address:** The IP address of the MyLink hub (Can be found in the integration report in the Somfy App)
- **Somfy Mylink Port:** Ensure this is set to 44100 if you are not sure
- **Somfy Mylink System ID:** The System ID configured in the Somfy App (Can be found in the integration report in the Somfy App)
- **Device 1-5 Name:** The human-readable name of the device you are adding
- **Device 1-5 ID:** The Somfy targetID of the device you are adding (Can be found in the integration report in the Somfy App)

# License

[MIT](https://github.com/bendews/smartthings-daikin-wifi/master/LICENSE)

# Author Information

Created in 2018 by [Ben Dews](https://bendews.com)
