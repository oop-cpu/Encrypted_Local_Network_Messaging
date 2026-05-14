# Encrypted_Local_Network_Messaging

## Quick Description
<p>
This application is for messaging another machine that is running the same program. 
All messages sent are encrypted with a unique key that is generated on start-up of the app.
When you request to chat with another machine, you pass it your key. When they accept, they pass you 
their own key. Messaging has end-to-end encryption, so even if someone was listening on the same network, 
they would only see "random" numbers following an IP. 
Note that machines messaging eachother have to be on the same network whether they are connected to the same router
or on the same virtual private network (VPN).
In my case I use Tailscale.
</p>

## Instructions

### How to Run (In Encrypted_Local_Network_Messaging Directory)
- Run Messaging GUI
```bash
java -cp swing-cli/src startMessaging
```

### Start Messaging!!!
<p>
You can start by either entering an IP and send a request, or waiting for request and accept. 
Once you do either of these, you can start messaging on the chat to the right. 
Keep an eye on the box to the left for anything going wrong while setting up and chatting. 
</p>

## Needed Installs

- Java
```bash
sudo apt install java
```

## Compile Java (only if needed but most likely not)

### In swing-cli/src Directory
```bash
javac encHandler.java
```
```bash
javac decHandler.java
```
```bash
javac commsHandler.java
```
```bash
javac startMessaging.java
```
