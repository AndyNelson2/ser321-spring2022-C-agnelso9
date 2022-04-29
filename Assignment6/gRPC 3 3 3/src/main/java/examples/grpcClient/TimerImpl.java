package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;
import java.util.Date; 

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;
import com.google.protobuf.Empty;


// Implement the timer service. It has four sevices start, check, close, and list. 
class TimerImpl extends TimerGrpc.TimerImplBase {
    
    // having a global set of timers. 
	ArrayList<String> timerNames = new ArrayList<String>(); 
    ArrayList<Long> timers = new ArrayList<Long>();  
    Date date;
    
    public TimerImpl(){
        super();   
    }
    
    @Override
    public void start(TimerRequest req, StreamObserver<TimerResponse> responseObserver) {
        
    	TimerResponse.Builder resp = TimerResponse.newBuilder(); 
    	Time.Builder timer = Time.newBuilder(); 
    	timer.setName(req.getName()); 
    	date = new Date(); 
        if(timerNames.contains(req.getName()))
        {
        	int index = 0; 
        	for(int x = 0; x < timerNames.size(); x++)
        	{
        		if(timerNames.get(x).equals(req.getName()))
        				index = x; 
        	}
        	timer.setSecondsPassed((date.getTime() - timers.get(index))/1000); 
        	resp.setIsSuccess(false); 
        	resp.setTimer(timer.build()); 
        	resp.setError("There is already a timer with that name."); 
        	
        }
        else
        {
        	resp.setIsSuccess(true); 
        	timerNames.add(req.getName()); 
        	timers.add(date.getTime());
        	timer.setSecondsPassed(0); 
        	resp.setTimer(timer.build()); 
        	resp.setError("no error"); 
        }
        
        TimerResponse response = resp.build();  
    	responseObserver.onNext(response); 
    	responseObserver.onCompleted(); 
    }
    
    @Override
    public void check(TimerRequest req, StreamObserver<TimerResponse> responseObserver) {
        
    	TimerResponse.Builder resp = TimerResponse.newBuilder(); 
    	Time.Builder timer = Time.newBuilder(); 
    	timer.setName(req.getName()); 
    	date = new Date(); 
    	
    	 int index = -1; 
         if(timerNames.contains(req.getName()))
         {
         	for(int x = 0; x < timerNames.size(); x++)
         	{
         		if(timerNames.get(x).equals(req.getName()))
         		{
         			index = x; 
         		}
         	}
            timer.setSecondsPassed((date.getTime() - timers.get(index))/1000); 
         	resp.setIsSuccess(true); 
         	resp.setTimer(timer.build()); 
         	resp.setError("No error"); 
         }
         else
         {
        	 resp.setIsSuccess(false); 
        	 timer.setSecondsPassed(0); 
        	 resp.setError("Timer " + req.getName() + " does not exist"); 
         }
        
        TimerResponse response = resp.build();  
    	responseObserver.onNext(response); 
    	responseObserver.onCompleted(); 
    }

    @Override
    public void close(TimerRequest req, StreamObserver<TimerResponse> responseObserver)
    {
    	TimerResponse.Builder resp = TimerResponse.newBuilder(); 
    	Time.Builder timer = Time.newBuilder(); 
    	timer.setName(req.getName()); 
    	date = new Date(); 
    	
    	 int index = -1; 
         if(timerNames.contains(req.getName()))
         {
         	for(int x = 0; x < timerNames.size(); x++)
         	{
         		if(timerNames.get(x).equals(req.getName()))
         		{
         			index = x; 
         		}
         	}
            timer.setSecondsPassed((date.getTime() - timers.get(index))/1000); 
         	resp.setIsSuccess(true); 
         	resp.setTimer(timer.build()); 
         	resp.setError("No error"); 
         	timerNames.remove(index); 
         	timers.remove(index); 
         }
         else
         {
        	 resp.setIsSuccess(false); 
        	 timer.setSecondsPassed(0); 
        	 resp.setError("Timer " + req.getName() + " does not exist"); 
         }
         
        
        TimerResponse response = resp.build();  
    	responseObserver.onNext(response); 
    	responseObserver.onCompleted(); 
    }
    
    @Override
    public void list(Empty empt, StreamObserver<TimerList> responseObserver)
    {
    	date = new Date(); 
    	TimerList.Builder timothy = TimerList.newBuilder(); 
    	for(int x = 0; x < timerNames.size(); x++)
    	{
    		Time.Builder timer = Time.newBuilder(); 
    		timer.setName(timerNames.get(x)); 
    		timer.setSecondsPassed((date.getTime() - timers.get(x))/1000);
    		timothy.addTimers(timer.build()); 
    	}
    	
    	TimerList response = timothy.build(); 
    	responseObserver.onNext(response); 
    	responseObserver.onCompleted(); 
    }
}