/* **************************************************
 Copyright (c) 2012, University of Cambridge
 Neal Lathia, neal.lathia@cl.cam.ac.uk
 Kiran Rachuri, kiran.rachuri@cl.cam.ac.uk

This library was developed as part of the EPSRC Ubhave (Ubiquitous and
Social Computing for Positive Behaviour Change) Project. For more
information, please visit http://www.emotionsense.org

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.ubhave.sensormanager.sensors;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.classifier.AccelerometerDataClassifier;
import com.ubhave.sensormanager.classifier.BluetoothDataClassifier;
import com.ubhave.sensormanager.classifier.LocationDataClassifier;
import com.ubhave.sensormanager.classifier.MicrophoneDataClassifier;
import com.ubhave.sensormanager.classifier.SensorDataClassifier;
import com.ubhave.sensormanager.classifier.WifiDataClassifier;
import com.ubhave.sensormanager.config.SensorManagerConstants;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.sensors.pull.AccelerometerSensor;
import com.ubhave.sensormanager.sensors.pull.ApplicationSensor;
import com.ubhave.sensormanager.sensors.pull.BluetoothSensor;
import com.ubhave.sensormanager.sensors.pull.LocationSensor;
import com.ubhave.sensormanager.sensors.pull.MicrophoneSensor;
import com.ubhave.sensormanager.sensors.pull.WifiSensor;
import com.ubhave.sensormanager.sensors.push.BatterySensor;
import com.ubhave.sensormanager.sensors.push.ConnectionStateSensor;
import com.ubhave.sensormanager.sensors.push.PhoneStateSensor;
import com.ubhave.sensormanager.sensors.push.ProximitySensor;
import com.ubhave.sensormanager.sensors.push.ScreenSensor;
import com.ubhave.sensormanager.sensors.push.SmsSensor;

public class SensorUtils
{
	private static String TAG = "SensorUtils";

	public final static int SENSOR_TYPE_ACCELEROMETER = 5001;
	public final static int SENSOR_TYPE_BATTERY = 5002;
	public final static int SENSOR_TYPE_BLUETOOTH = 5003;
	public final static int SENSOR_TYPE_LOCATION = 5004;
	public final static int SENSOR_TYPE_MICROPHONE = 5005;
	public final static int SENSOR_TYPE_PHONE_STATE = 5006;
	public final static int SENSOR_TYPE_PROXIMITY = 5007;
	public final static int SENSOR_TYPE_SCREEN = 5008;
	public final static int SENSOR_TYPE_SMS = 5009;
	public final static int SENSOR_TYPE_WIFI = 5010;
	public final static int SENSOR_TYPE_CONNECTION_STATE = 5011;
	public final static int SENSOR_TYPE_APPLICATION = 5012;

	public final static String SENSOR_NAME_ACCELEROMETER = "Accelerometer";
	public final static String SENSOR_NAME_BATTERY = "Battery";
	public final static String SENSOR_NAME_BLUETOOTH = "Bluetooth";
	public final static String SENSOR_NAME_LOCATION = "Location";
	public final static String SENSOR_NAME_MICROPHONE = "Microphone";
	public final static String SENSOR_NAME_PHONE_STATE = "PhoneState";
	public final static String SENSOR_NAME_PROXIMITY = "Proximity";
	public final static String SENSOR_NAME_SCREEN = "Screen";
	public final static String SENSOR_NAME_SMS = "SMS";
	public final static String SENSOR_NAME_WIFI = "WiFi";
	public final static String SENSOR_NAME_CONNECTION_STATE = "Connection";
	public final static String SENSOR_NAME_APPLICATION = "Application";

	public final static int[] ALL_SENSORS = new int[] { SENSOR_TYPE_ACCELEROMETER, SENSOR_TYPE_BLUETOOTH,
			SENSOR_TYPE_LOCATION, SENSOR_TYPE_MICROPHONE, SENSOR_TYPE_WIFI, SENSOR_TYPE_BATTERY, SENSOR_TYPE_PHONE_STATE,
			SENSOR_TYPE_PROXIMITY, SENSOR_TYPE_SCREEN, SENSOR_TYPE_SMS, SENSOR_TYPE_CONNECTION_STATE,
			SENSOR_TYPE_APPLICATION };

	public static boolean isPullSensor(int sensorType)
	{
		switch (sensorType)
		{
		case SENSOR_TYPE_ACCELEROMETER:
		case SENSOR_TYPE_BLUETOOTH:
		case SENSOR_TYPE_LOCATION:
		case SENSOR_TYPE_MICROPHONE:
		case SENSOR_TYPE_WIFI:
		case SENSOR_TYPE_APPLICATION:
			return true;
		default:
			return false;
		}
	}

	public static ArrayList<SensorInterface> getAllSensors(Context applicationContext)
	{
		return getSensorList(ALL_SENSORS, applicationContext);
	}

	private static ArrayList<SensorInterface> getSensorList(int[] list, Context applicationContext)
	{
		ArrayList<SensorInterface> sensors = new ArrayList<SensorInterface>();
		for (int sensorId : list)
		{
			try
			{
				SensorInterface sensor = getSensor(sensorId, applicationContext);
				sensors.add(sensor);
			}
			catch (ESException e)
			{
				// ESLogger.error(TAG, e);
				Log.d(TAG, "Warning: " + e.getMessage());
			}
		}
		return sensors;
	}

	private static SensorInterface getSensor(int id, Context context) throws ESException
	{
		switch (id)
		{
		case SENSOR_TYPE_ACCELEROMETER:
			return AccelerometerSensor.getAccelerometerSensor(context);
		case SENSOR_TYPE_BLUETOOTH:
			return BluetoothSensor.getBluetoothSensor(context);
		case SENSOR_TYPE_LOCATION:
			return LocationSensor.getLocationSensor(context);
		case SENSOR_TYPE_MICROPHONE:
			return MicrophoneSensor.getMicrophoneSensor(context);
		case SENSOR_TYPE_WIFI:
			return WifiSensor.getWifiSensor(context);
		case SENSOR_TYPE_BATTERY:
			return BatterySensor.getBatterySensor(context);
		case SENSOR_TYPE_PHONE_STATE:
			return PhoneStateSensor.getPhoneStateSensor(context);
		case SENSOR_TYPE_PROXIMITY:
			return ProximitySensor.getProximitySensor(context);
		case SENSOR_TYPE_SCREEN:
			return ScreenSensor.getScreenSensor(context);
		case SENSOR_TYPE_SMS:
			return SmsSensor.getSmsSensor(context);
		case SENSOR_TYPE_CONNECTION_STATE:
			return ConnectionStateSensor.getConnectionStateSensor(context);
		case SENSOR_TYPE_APPLICATION:
			return ApplicationSensor.getApplicationSensor(context);
		default:
			throw new ESException(ESException.UNKNOWN_SENSOR_TYPE, "Unknown sensor id");
		}
	}

	public static SensorConfig getDefaultSensorConfig(int sensorType)
	{
		SensorConfig sensorConfig = new SensorConfig();
		switch (sensorType)
		{
		case SensorUtils.SENSOR_TYPE_ACCELEROMETER:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.ACCELEROMETER_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.ACCELEROMETER_SAMPLING_DELAY, SensorManager.SENSOR_DELAY_GAME);
			sensorConfig.setParameter(SensorConfig.SENSE_WINDOW_LENGTH_MILLIS,
					SensorManagerConstants.ACCELEROMETER_SAMPLING_WINDOW_SIZE_MILLIS);
			break;
		case SensorUtils.SENSOR_TYPE_BLUETOOTH:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.BLUETOOTH_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.NUMBER_OF_SENSE_CYCLES, SensorManagerConstants.BLUETOOTH_SAMPLING_CYCLES);
			sensorConfig.setParameter(SensorConfig.SENSE_WINDOW_LENGTH_PER_CYCLE_MILLIS,
					SensorManagerConstants.BLUETOOTH_SAMPLING_WINDOW_SIZE_PER_CYCLE_MILLIS);
			break;
		case SensorUtils.SENSOR_TYPE_LOCATION:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.LOCATION_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.SENSE_WINDOW_LENGTH_MILLIS,
					SensorManagerConstants.LOCATION_SAMPLING_WINDOW_SIZE_MILLIS);
			sensorConfig.setParameter(SensorConfig.LOCATION_ACCURACY, SensorConfig.LOCATION_ACCURACY_COARSE);
			break;
		case SensorUtils.SENSOR_TYPE_MICROPHONE:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.MICROPHONE_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.SENSE_WINDOW_LENGTH_MILLIS,
					SensorManagerConstants.MICROPHONE_SAMPLING_WINDOW_SIZE_MILLIS);
			break;
		case SensorUtils.SENSOR_TYPE_WIFI:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.WIFI_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.NUMBER_OF_SENSE_CYCLES, SensorManagerConstants.WIFI_SAMPLING_CYCLES);
			sensorConfig.setParameter(SensorConfig.SENSE_WINDOW_LENGTH_PER_CYCLE_MILLIS,
					SensorManagerConstants.WIFI_SAMPLING_WINDOW_SIZE_PER_CYCLE_MILLIS);
			break;
		case SensorUtils.SENSOR_TYPE_APPLICATION:
			sensorConfig.setParameter(SensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, SensorManagerConstants.APPLICATON_SLEEP_INTERVAL);
			sensorConfig.setParameter(SensorConfig.NUMBER_OF_SENSE_CYCLES, SensorManagerConstants.APPLCATION_SAMPLING_CYCLES);
			break;
		}

		sensorConfig.setParameter(SensorConfig.ADAPTIVE_SENSING_ENABLED, false);
		return sensorConfig;
	}

	public static int getSensorType(String sensorName) throws ESException
	{
		if (sensorName.equals(SENSOR_NAME_ACCELEROMETER))
		{
			return SENSOR_TYPE_ACCELEROMETER;
		}
		else if (sensorName.equals(SENSOR_NAME_BATTERY))
		{
			return SENSOR_TYPE_BATTERY;
		}
		else if (sensorName.equals(SENSOR_NAME_BLUETOOTH))
		{
			return SENSOR_TYPE_BLUETOOTH;
		}
		else if (sensorName.equals(SENSOR_NAME_LOCATION))
		{
			return SENSOR_TYPE_LOCATION;
		}
		else if (sensorName.equals(SENSOR_NAME_MICROPHONE))
		{
			return SENSOR_TYPE_MICROPHONE;
		}
		else if (sensorName.equals(SENSOR_NAME_PHONE_STATE))
		{
			return SENSOR_TYPE_PHONE_STATE;
		}
		else if (sensorName.equals(SENSOR_NAME_PROXIMITY))
		{
			return SENSOR_TYPE_PROXIMITY;
		}
		else if (sensorName.equals(SENSOR_NAME_SCREEN))
		{
			return SENSOR_TYPE_SCREEN;
		}
		else if (sensorName.equals(SENSOR_NAME_SMS))
		{
			return SENSOR_TYPE_SMS;
		}
		else if (sensorName.equals(SENSOR_NAME_WIFI))
		{
			return SENSOR_TYPE_WIFI;
		}
		else if (sensorName.equals(SENSOR_NAME_APPLICATION))
		{
			return SENSOR_TYPE_APPLICATION;
		}
		else
		{
			throw new ESException(ESException.UNKNOWN_SENSOR_NAME, "unknown sensor name " + sensorName);
		}
	}

	public static String getSensorName(int sensorType) throws ESException
	{
		switch (sensorType)
		{
		case SensorUtils.SENSOR_TYPE_ACCELEROMETER:
			return SENSOR_NAME_ACCELEROMETER;
		case SensorUtils.SENSOR_TYPE_BATTERY:
			return SENSOR_NAME_BATTERY;
		case SensorUtils.SENSOR_TYPE_BLUETOOTH:
			return SENSOR_NAME_BLUETOOTH;
		case SensorUtils.SENSOR_TYPE_LOCATION:
			return SENSOR_NAME_LOCATION;
		case SensorUtils.SENSOR_TYPE_MICROPHONE:
			return SENSOR_NAME_MICROPHONE;
		case SensorUtils.SENSOR_TYPE_PHONE_STATE:
			return SENSOR_NAME_PHONE_STATE;
		case SensorUtils.SENSOR_TYPE_PROXIMITY:
			return SENSOR_NAME_PROXIMITY;
		case SensorUtils.SENSOR_TYPE_SCREEN:
			return SENSOR_NAME_SCREEN;
		case SensorUtils.SENSOR_TYPE_SMS:
			return SENSOR_NAME_SMS;
		case SensorUtils.SENSOR_TYPE_WIFI:
			return SENSOR_NAME_WIFI;
		case SensorUtils.SENSOR_TYPE_CONNECTION_STATE:
			return SENSOR_NAME_CONNECTION_STATE;
		case SensorUtils.SENSOR_TYPE_APPLICATION:
			return SENSOR_NAME_APPLICATION;
		default:
			throw new ESException(ESException.UNKNOWN_SENSOR_NAME, "unknown sensor type " + sensorType);
		}
	}

	public static SensorDataClassifier getSensorDataClassifier(int sensorType) throws ESException
	{
		switch (sensorType)
		{
		case SensorUtils.SENSOR_TYPE_ACCELEROMETER:
			return new AccelerometerDataClassifier();
		case SensorUtils.SENSOR_TYPE_BLUETOOTH:
			return new BluetoothDataClassifier();
		case SensorUtils.SENSOR_TYPE_LOCATION:
			return new LocationDataClassifier();
		case SensorUtils.SENSOR_TYPE_MICROPHONE:
			return new MicrophoneDataClassifier();
		case SensorUtils.SENSOR_TYPE_WIFI:
			return new WifiDataClassifier();
		default:
			throw new ESException(ESException.UNKNOWN_SENSOR_TYPE, "sensor data classifier not support for the sensor type "
					+ sensorType);
		}
	}

}
