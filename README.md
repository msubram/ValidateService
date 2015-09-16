# ValidateService

####Version 1.2

####Minimum SDK 4.0 +

####compileSdkVersion 21
####buildToolsVersion 21.1.2

###Install
Download this project and import in Android Studio.

####Overview 
An application used to store information of the user and validate it. The Endpoint used to validate the stored information changes often, hence we use UDP Broadcast and receive process to get the Endpoint and sends the stored information in that Endpoint.

###Requirements
i) Android Setup(SDK, Studio,windows)

ii) NFC Reader - ACR-122U NFC MIFARE (http://www.amazon.in/dp/B00CBPIJG4/ref=pe_386221_48200641_TE_item)

iii) Android device with 4.0 and above. NFC enabled device(optional)

iv) Mobile application and the server should be connecetd on the same WIFI network.

###Project Workflow

i) Run the server program created using .net in the windows. 
        This server program runs on a port #32233. This program generates the Endpoint and send it back to the request. The Endpoint which changes often is generated in this server program. By running this program make this server to listen on the port #32233.
        
ii) Download this project and execute it on Android device.

iii) First step is the Registration. 

      1) Enter your phone number and select the country code
      2) You will receive the Registration Code through SMS and enter that in the respective fields.
      
      Complete the Registration.
      
iv) Next is Update Screen. User can save the informtion in the update screen. This screen has Home Telephone number and Work Telephone number fields. Enter the details and update it.

v) Now click the Validate button. 

App will perform two actions

  1) Mobile Application will listen on the port  #39798(UDPListenPort) to receive the packet(Endpoint).
  
  2) Broadcast the Country Code, Mobile Number,Name, Endpoint, IP address of the device and listening port of the device in the network.

######Broadcast Data Format : 
{CountryCode:'44', MobileNumber:'07900989998', IP: '192.168.0.98', Name: 'MyDevice', EndPoint: '', UDPListenPort: '38798'}
  
vi) Already the .net server is listening on the port #32233 and we are broadcasting the data. So the server on the port #32233 will receive the data and parse it and fills the Endpoint and send it back to the UDPListenPort.

vi) The mobile application which is listening on the port #39798(UDPListenPort) will receive the packet which has Endpoint in it. (Endpoint sent by server running on windows)

vii) Fill the following data in the received Endpoint and see the validation is successfull or not.
######Validate Endpoint Data Format:
{
    "Email": "email@domain.com",
    "HomeNumber": "02098999899",
    "WorkNumber": "02011123344",
    "MobileNumber": "07988676543",
    "MobileCountryCode": "44"
}



