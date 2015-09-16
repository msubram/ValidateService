# ValidateService
ValidateService 
An application used to store information of the user and validate it based on the country. The Endpoint used to validate the stored information changes often, hence we use UDP Broadcast and receive process to get the Endpoint and sends the stored information in that Endpoint.

###Requirements
i) Android Setup(SDK, Studio)

ii) NFC Reader - ACR-122U NFC MIFARE (http://www.amazon.in/dp/B00CBPIJG4/ref=pe_386221_48200641_TE_item)

iii) Android device with 4.0 and above. NFC enabled device is optional

###Project Workflow

i) Run the server program created using .net in the windows. 
        This server program runs on a port #32233. This program generates the Endpoint and send it back to the request. The Endpoint which is going to change often.
        So  when running this it starts to listen on the port #32233
        
ii) Download this project and execute it on Android device.

iii) First step is the Registration. 

      1) Enter your phone number and select the country code
      2) You will receive the Registration Code through SMS and enter that in the respective fields.
      
      Complete the Registration.
      
iv) User can save the informtion in the update screen. This screen has Home Telephone number and Work Telephone number fields. Enter the details and update it.

v) Now click the Validate button. 

App will perform two actions

  1) Application will listen on the port #39798 to receive the packet.
  2) Broadcast the Country Code, Mobile Number,Name, Endpoint, IP address of the device and listening port of the device in the network.

######Broadcast Data Format : 
{CountryCode:'44', MobileNumber:'07900989998', IP: '192.168.0.98', Name: 'MyDevice', EndPoint: '', UDPListenPort: '38798'}
  
vi) Already the server is listening on the port #32233 and we are broadcasting the data. So the server on the port #32233 will receive the data and parse it and fills the Endpoint and send it back to the UDPListenPort/

vi) The server on the port #39798 will receive the packet which has Endpoint.

vii) Fill the following data in the Endpoint and see the validation is successfull or not.
######Validate Endpoint Data Format:
{
    "Email": "email@domain.com",
    "HomeNumber": "02098999899",
    "WorkNumber": "02011123344",
    "MobileNumber": "07988676543",
    "MobileCountryCode": "44"
}



