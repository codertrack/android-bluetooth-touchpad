/**
    Copyright (C) 2011 Nikolay Kostadinov
   
    This file is part of BTTouchpad.

    BTTouchpad is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BTTouchpad is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BTTouchpad.  If not, see <http://www.gnu.org/licenses/>. 
    
 */

package tum.betriebsysteme.kostadinov.btframework.sdp;

/*package*/ class SDPCommands {
	
	//Warning the executable file name should be the same as the LOCAL_MODULE
	//variable specified in the <project>/jni/Android.mk
	/*package*/ static final String EXECUTABLE_FILE_NAME = "sdp";
		
	/*package*/ static final String COMMAND_VALUE_REGISTER = "register";
	
	/*package*/ static final String COMMAND_VALUE_UNREGISTER = "unregister";
	
	/*package*/ static final String PARAM_VALUE_RELATIVE = "-relative";
	
	/*package*/ static final String PARAM_VALUE_ABSOULTE = "-absolute";
	
	/*package*/ static final String COMMAND_EXIT = "exit\n";
	 
	/*package*/ static final String COMMAND_EXEC_SU = "su"; 

	/*package*/ static String navigateToExecutableFolder(String applicationPackageName){
		return "cd /data/data/" + applicationPackageName+ "/files/ "+"\n"; 
	}
	
	/*package*/ static String changeModeToExecutable(String executableName){
		return "chmod 777 ./"+executableName+"\n";
	}
	 
	/*package*/ static String runExecutable(String executableName, String command, String param){
		
		return "./"+executableName+" "+command+((param != null ) ? " " + param : "") +"\n";

	}
	
	/*package*/ static String deleteExecutableFile(String executableName){
		return "rm "+executableName +"\n";
	}
	
}
