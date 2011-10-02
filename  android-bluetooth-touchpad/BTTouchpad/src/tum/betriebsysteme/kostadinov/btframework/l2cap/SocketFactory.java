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


package tum.betriebsysteme.kostadinov.btframework.l2cap;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

/**
 * 
 * @author freeman
 *
 */
class SocketFactory  {
	
	private SocketFactory(){}
	
	protected static BluetoothSocket instantateL2CAPSocket(BluetoothDevice device, int port) throws SecurityException,
	NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException	{
		
		Constructor<BluetoothSocket> constructor = 
			BluetoothSocket.class.getDeclaredConstructor(int.class, int.class, boolean.class, boolean.class,
					BluetoothDevice.class, int.class, ParcelUuid.class);
		
		constructor.setAccessible(true);
		
		return constructor.newInstance(3,-1,false,false,device,port,null);

	}
	
}
