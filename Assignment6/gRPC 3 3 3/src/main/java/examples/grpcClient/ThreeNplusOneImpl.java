package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;
import com.google.protobuf.Empty;


// Implement the ThreeNplusOne service. It has two sevices getResultand getPath. 

// The '3n + 1' problem is a famous math problem for being so simple but unproven. 
// Also known as the Collatz conjecture. 
// Basically if the number is odd you multiply by 3 and add 1. If it's even, you divide by 2. 
// Every number tested so far (not just me but by mathematicians) eventually goes back 
// down to 1 but it hasn't been proven that every number will behave the same way I believe.  

class ThreeNplusOneImpl extends ThreeNplusOneGrpc.ThreeNplusOneImplBase {
    
    public ThreeNplusOneImpl(){
        super();   
    }
    
    public void getResult(ThreeReq req, StreamObserver<ThreeRes> responseObserver) {
        
    	ThreeRes.Builder response = ThreeRes.newBuilder(); 
    	int counter = 0; 
    	int start = req.getNumber(); 
    	
    	while(start != 1)
    	{
    		if(start%2 == 1)
    			start = start * 3 + 1; 
    		else
    			start = start/2; 
    		counter++; 
    	}
    	
    	response.setAnswer(counter); 
    	
    	ThreeRes resp = response.build(); 
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
   
    }
    
    public void getPath(ThreeReq req, StreamObserver<ThreePathRes> responseObserver)
    {
    	ThreePathRes.Builder response = ThreePathRes.newBuilder(); 
    	int counter = 0; 
    	int start = req.getNumber(); 
    	
    	while(start != 1)
    	{
    		if(start%2 == 1)
    			start = start * 3 + 1; 
    		else
    			start = start/2; 
    		response.addResult(start); 
    		counter++; 
    	}
    	
    	ThreePathRes resp = response.build(); 
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

}