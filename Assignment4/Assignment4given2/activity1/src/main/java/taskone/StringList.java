package taskone;

import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }
    
    public String pop()
    {
    	if(strings.size() == 0)
    	{
    		return "Cannot pop. List is currently empty"; 
    	}
    	else
    	{
    		String first = strings.get(0); 
    		strings.remove(0); 
    		return first; 
    	}
    }

    public void reverse(int index)
    {
    	String reverse = strings.get(index); 
    	String newString = ""; 
    	for(int x = reverse.length()-1; x > -1; x--)
    	{
    		newString+=reverse.charAt(x); 
    	}
    	strings.add(index, newString);
    	strings.remove(index + 1); 
    }
    
    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }
}