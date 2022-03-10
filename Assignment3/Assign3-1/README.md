##### Author: Instructor team SE, ASU Polytechnic, CIDSE, SE
* Version: February 2022


##### Purpose
Each program has a short description as well as the Gradle file
* Please run `Server` and `Client` together.

##### Protocol Description

#### Shuffle two strings
###### Request
    {"type": "shuffle", "data1": <String>, "data2": <String>}

###### Response
Ok case
    {"type": "shuffle", "data": <String>}
Error cases
    {"type": "error", "message": <String>}

    String is:
    - "no String" -- if the "data" field in the request is not a String
    - "Data missing" -- if the request does not have a "data" field


#### Change string to sarcastic case
###### Request
    {"type": "mocking", "data": <String>}

###### Response
Ok case
    {"type": "mocking", "data": <String>}
Error cases
    {"type": "error", "message": <String>}

    String is:
    - "no String" -- if the "data" field in the request is not a String
    - "Data missing" -- if the request does not have a "data" field


#### Exit
###### Request
    {"type": "exit"}

###### Response
Ok case
    {"type": "exit", "data": <String>}
Error cases: 
    no error cases implemented, client will likely not receive an answer -- this is of course not good


#### Request type unknown
Server will respond with:
{"type": "error", "message": "Request type not known"}