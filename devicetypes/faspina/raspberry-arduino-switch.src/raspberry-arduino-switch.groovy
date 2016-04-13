/**
 *  Raspberry/Arduino project v1.0
 *
 *
 *  Credit goes to JZt and his initial post for inspiring this project 
 https://community.smartthings.com/t/raspberry-pi-to-php-to-gpio-to-relay-to-gate-garage-trigger/43335
*/



import groovy.json.JsonSlurper
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

metadata {
	definition (name: "Raspberry/Arduino Switch", author: "faspina", namespace:"faspina") {
		capability "Switch"
        capability "Polling"
        capability "Refresh"
        
        command "refresh"
        command "send"
        

	}


	preferences {
//		input("DeviceButtonName", "string", title:"Button Name", description: "Please enter button name", required: false, displayDuringSetup: true)
		input("DeviceIP", "string", title:"Device IP Address", description: "Please enter your device's IP Address", required: true, displayDuringSetup: true)
		input("DevicePort", "string", title:"Device Port", description: "Please enter port 80 or your device's Port", required: true, displayDuringSetup: true)
		input("DevicePath", "string", title:"URL Path", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
		input("DeviceNumber", "string", title: "Switch Command", description: "Device Number:",required: true, displayDuringSetup: true)
		section() {
			input("HTTPAuth", "bool", title:"Requires User Auth?", description: "Choose if the HTTP requires basic authentication", defaultValue: false, required: true, displayDuringSetup: true)
			input("HTTPUser", "string", title:"HTTP User", description: "Enter your basic username", required: false, displayDuringSetup: true)
			input("HTTPPassword", "string", title:"HTTP Password", description: "Enter your basic password", required: false, displayDuringSetup: true)
		}

	}
	
	simulator {
	}

	tiles 
    	{
		
		standardTile("switch", "device.switch", width: 3, height: 3 ){
			state ("off", label:'off' , action: "send", icon: "st.Home.home6", backgroundColor:"#ffffff",isStateChange: true,nextState: "send")
			state ("on", label: 'on', action: "off", icon: "st.Home.home6", backgroundColor: "#0022FF",isStateChange: true,nextState: "send")
			state ("send", label: 'sending', action: "none", icon: "st.Home.home6", backgroundColor: "#00FFA6")
		}
           standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
        	state "default", action:"refresh.refresh", icon: "st.secondary.refresh"
        }
		}
		main "switch"
		details(["switch","refresh"])
		
    }


def send(){
    def led = DeviceNumber
    log.debug led + " On"
    runCmd("Led" + DeviceNumber + "on")
}
def off() {
    def led = DeviceNumber
    log.debug led + " Off"
    runCmd("Led" + DeviceNumber + "off")
}



def poll() {
 refresh()
}

def refresh() {
//	sendEvent(name: "switch", value: "off")
	log.debug "Executing 'refresh'"
    runCmd("refresh")
}



def runCmd(String varCommand) {
	def host = DeviceIP 
	def hosthex = convertIPtoHex(host).toUpperCase()
	def porthex = convertPortToHex(DevicePort).toUpperCase()
	device.deviceNetworkId = "$hosthex:$porthex" 
	def userpassascii = "${HTTPUser}:${HTTPPassword}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
	
	log.debug "The device id configured is: $device.deviceNetworkId"
	
	def path = DevicePath
	log.debug "path is: $path"
	log.debug "Uses which method: $DevicePostGet"
	def body = varCommand 
	log.debug "body is: $body"
	
	def headers = [:] 
	headers.put("HOST", "$host:$DevicePort")
	headers.put("Content-Type", "application/x-www-form-urlencoded")
	if (HTTPAuth) {
		headers.put("Authorization", userpass)
	}
	log.debug "The Header is $headers"
	def method = "POST"

	log.debug "The method is $method"

	try {
		def hubAction = new physicalgraph.device.HubAction(
			method: method,
			path: path,
            body: body,
			headers: headers
			)
		hubAction.options = [outputMsgToS3:false]
		//log.debug hubAction
		hubAction
	}
	catch (Exception e) {
		log.debug "Hit Exception $e on $hubAction"
	}

   
}


def parse(String description) {
	log.debug "Parsing '${description}'"
	def whichTile = ''
	def map = [:]
	def retResult = []
	def descMap = parseDescriptionAsMap(description)
	def jsonlist = [:]
	def bodyReturned = new String(descMap["body"].decodeBase64())
	def headersReturned = new String(descMap["headers"].decodeBase64())


	
    def isOn = "("+DeviceNumber + ":1)"
    def isOff = "("+DeviceNumber + ":0)"

	log.debug "Looking for " + isOn + " or " + isOff
    log.debug "In Body " + bodyReturned
    

	if (bodyReturned.contains(isOn)) {
       log.debug "Is On"
       sendEvent(name: "switch", value: "on",isStateChange: true)
	}
	if (bodyReturned.contains(isOff)) {
       sendEvent(name: "switch", value: "off",isStateChange: true)
       log.debug "Is Off"
	}

  	}

    // TODO: subscribe to attributes, devices, locations, etc.

private postAction(uri){
  setDeviceNetworkId(DeviceIP,DevicePort)  
  log.debug DeviceIP
  log.debug DevicePort
  def userpass = encodeCredentials(username, password)
  //log.debug("userpass: " + userpass) 
  
  def headers = getHeader(userpass)
  //log.debug("headders: " + headers) 
  log.debug uri
  def hubAction = new physicalgraph.device.HubAction(
    method: "POST",
    path: uri,
    headers: headers
  )//,delayAction(1000), refresh()]
  log.debug("Executing hubAction on " + getHostAddress())
  //log.debug hubAction
  hubAction    
}



def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
	def nameAndValue = param.split(":")
	map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}
private String convertIPtoHex(ipAddress) { 
	String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	//log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
	return hex
}
private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
	//log.debug hexport
	return hexport
}
private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
	//log.debug("Convert hex to ip: $hex") 
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	//log.debug device.deviceNetworkId
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}
