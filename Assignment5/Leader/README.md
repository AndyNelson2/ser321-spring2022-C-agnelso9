# Assignment 5 Activity 2
## Description
Simulated a banking system with nodes and clients who work through a leader. 
screencast: https://youtu.be/byYVbwd-Q5Q
## Protocol

### Requests
request: { "option": <String: "credit", "payback", "quit">, "id": <int>, "amount": <int>}

### Responses

sucess response: {"greetings": <String>}

error response: {"greetings": "response not recognized"}

## How to run the program
### Terminal
Base Code, please use the following commands:
```
    For Server, run "gradle leader"
```
```   
    For Client, run "gradle runClient"
```   
```   
    For Node, run "gradle runNode1" or "gradle runNode2"
```  


