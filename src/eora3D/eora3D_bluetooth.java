package eora3D;

import tinyb.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;

public class eora3D_bluetooth {

    private BluetoothManager manager;
    public List<BluetoothDevice> devices = null;

	static void printDevice(BluetoothDevice device) {
        System.out.print("Address = " + device.getAddress());
        System.out.print(" Name = " + device.getName());
        System.out.print(" Connected = " + device.getConnected());
        System.out.println();
    }

    void getDevices() throws InterruptedException {
        BluetoothManager manager = BluetoothManager.getBluetoothManager();
        
        devices = manager.getDevices();

        for (BluetoothDevice device : devices) {
            printDevice(device);
        }
    }

    /*
     * Our device should expose a temperature service, which has a UUID we can find out from the data sheet. The service
     * description of the SensorTag can be found here:
     * http://processors.wiki.ti.com/images/a/a8/BLE_SensorTag_GATT_Server.pdf. The service we are looking for has the
     * short UUID AA00 which we insert into the TI Base UUID: f000XXXX-0451-4000-b000-000000000000
     */
    static BluetoothGattService getService(BluetoothDevice device, String UUID) throws InterruptedException {
        System.out.println("Services exposed by device:");
        BluetoothGattService tempService = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = device.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService service : bluetoothServices) {
                System.out.println("UUID: " + service.getUUID());
                if (service.getUUID().equals(UUID))
                    tempService = service;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty());
        return tempService;
    }

    static BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String UUID) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        if (characteristics == null)
            return null;

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUUID().equals(UUID))
                return characteristic;
        }
        return null;
    }

    /*
     * This program connects to a TI SensorTag 2.0 and reads the temperature characteristic exposed by the device over
     * Bluetooth Low Energy. The parameter provided to the program should be the MAC address of the device.
     *
     * A wiki describing the sensor is found here: http://processors.wiki.ti.com/index.php/CC2650_SensorTag_User's_Guide
     *
     * The API used in this example is based on TinyB v0.3, which only supports polling, but v0.4 will introduce a
     * simplied API for discovering devices and services.
     */
    public void discover() {
    	if(manager==null)
    		manager = BluetoothManager.getBluetoothManager();

        /*
         * The manager will try to initialize a BluetoothAdapter if any adapter is present in the system. To initialize
         * discovery we can call startDiscovery, which will put the default adapter in discovery mode.
         */
        boolean discoveryStarted = manager.startDiscovery();

        System.out.println("The discovery started: " + (discoveryStarted ? "true" : "false"));
        try {
			getDevices();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        
        BluetoothDevice sensor = devices.get(0);

        /*
         * After we find the device we can stop looking for other devices.
         */
        try {
            manager.stopDiscovery();
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped.");
        }

        if(true) return;
        
        System.out.print("Found device: ");
        printDevice(sensor);

        if (sensor.connect())
            System.out.println("Sensor with the provided address connected");
        else {
            System.out.println("Could not connect device.");
            System.exit(-1);
        }

        Lock lock = new ReentrantLock();
        Condition cv = lock.newCondition();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                lock.lock();
                try {
                    cv.signalAll();
                } finally {
                    lock.unlock();
                }

            }
        });


        BluetoothGattService tempService = null;
		try {
			tempService = getService(sensor, "f000aa00-0451-4000-b000-000000000000");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (tempService == null) {
            System.err.println("This device does not have the temperature service we are looking for.");
            sensor.disconnect();
            System.exit(-1);
        }
        System.out.println("Found service " + tempService.getUUID());

        BluetoothGattCharacteristic tempValue = getCharacteristic(tempService, "f000aa01-0451-4000-b000-000000000000");
        BluetoothGattCharacteristic tempConfig = getCharacteristic(tempService, "f000aa02-0451-4000-b000-000000000000");
        BluetoothGattCharacteristic tempPeriod = getCharacteristic(tempService, "f000aa03-0451-4000-b000-000000000000");

        if (tempValue == null || tempConfig == null || tempPeriod == null) {
            System.err.println("Could not find the correct characteristics.");
            sensor.disconnect();
            System.exit(-1);
        }

        System.out.println("Found the temperature characteristics");

        /*
         * Turn on the Temperature Service by writing 1 in the configuration characteristic, as mentioned in the PDF
         * mentioned above. We could also modify the update interval, by writing in the period characteristic, but the
         * default 1s is good enough for our purposes.
         */
        byte[] config = { 0x01 };
        tempConfig.writeValue(config);

        /*
         * Each second read the value characteristic and display it in a human readable format.
         */
        while (true) {
            byte[] tempRaw = tempValue.readValue();
            System.out.print("Temp raw = {");
            for (byte b : tempRaw) {
                System.out.print(String.format("%02x,", b));
            }
            System.out.print("}");

            /*
             * The temperature service returns the data in an encoded format which can be found in the wiki. Convert the
             * raw temperature format to celsius and print it. Conversion for object temperature depends on ambient
             * according to wiki, but assume result is good enough for our purposes without conversion.
             */
            int objectTempRaw = (tempRaw[0] & 0xff) | (tempRaw[1] << 8);
            int ambientTempRaw = (tempRaw[2] & 0xff) | (tempRaw[3] << 8);

/*            float objectTempCelsius = convertCelsius(objectTempRaw);
            float ambientTempCelsius = convertCelsius(ambientTempRaw);

            System.out.println(
                    String.format(" Temp: Object = %fC, Ambient = %fC", objectTempCelsius, ambientTempCelsius));
*/
            lock.lock();
            try {
                try {
					cv.await(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } finally {
                lock.unlock();
            }
        }
 //       sensor.disconnect();
    }
}
