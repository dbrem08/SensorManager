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

package com.ubhave.sensormanager.sensors.push;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.data.pushsensor.SmsData;
import com.ubhave.sensormanager.process.push.SMSProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class SmsSensor extends AbstractPushSensor
{
	private static final String TAG = "SmsSensor";

	private ContentObserver observer;
	private String prevMessageId;
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private static SmsSensor smsSensor;
	private static Object lock = new Object();

	public static SmsSensor getSmsSensor(Context context) throws ESException
	{
		if (smsSensor == null)
		{
			synchronized (lock)
			{
				if (smsSensor == null)
				{
					if (permissionGranted(context, "android.permission.RECEIVE_SMS")
							&& permissionGranted(context, "android.permission.READ_SMS"))
					{
						smsSensor = new SmsSensor(context);
					}
					else throw new ESException(ESException. PERMISSION_DENIED, "SMS Sensor : Permission not Granted");
				}
			}
		}
		return smsSensor;
	}

	private SmsSensor(Context context)
	{
		super(context);
		// Create a content observer for sms
		observer = new ContentObserver(new Handler())
		{

			public void onChange(boolean selfChange)
			{
				if (isSensing)
				{
					// check last sent message
					Uri smsUri = Uri.parse("content://sms");
					Cursor cursor = applicationContext.getContentResolver().query(smsUri, null, null, null, null);
					
					// last sms sent is the fist in the list
					cursor.moveToNext();
					String content = cursor.getString(cursor.getColumnIndex("body"));
					String sentTo = cursor.getString(cursor.getColumnIndex("address"));
					String messageId = cursor.getString(cursor.getColumnIndex("_id"));

					if ((prevMessageId != null) && (prevMessageId.length() > 0) && (prevMessageId.equals(messageId)))
					{
						// ignore, message already logged
					}
					else
					{
						prevMessageId = messageId;
						logDataSensed(System.currentTimeMillis(), content, sentTo, SmsData.SMS_CONTENT_CHANGED);
					}
				}
			}
		};
	}

	private void logDataSensed(long timestamp, String content, String addr, String eventType)
	{
		SMSProcessor processor = (SMSProcessor) getProcessor();
		if (processor != null)
		{
			SmsData data = (SmsData) processor.process(timestamp, sensorConfig.clone(), content, addr, eventType);
			onDataSensed(data);
		}
	}

	public String getLogTag()
	{
		return TAG;
	}

	public int getSensorType()
	{
		return SensorUtils.SENSOR_TYPE_SMS;
	}

	protected void onBroadcastReceived(Context context, Intent intent)
	{
		if (intent.getAction().equals(SMS_RECEIVED))
		{
			Bundle bundle = intent.getExtras();
			SmsMessage[] smsMessagesArray = null;
			if (bundle != null)
			{
				// read the sms received
				try
				{
					Object[] pdusArray = (Object[]) bundle.get("pdus");
					smsMessagesArray = new SmsMessage[pdusArray.length];
					for (int i = 0; i < smsMessagesArray.length; i++)
					{
						smsMessagesArray[i] = SmsMessage.createFromPdu((byte[]) pdusArray[i]);
						String address = smsMessagesArray[i].getOriginatingAddress();
						String content = smsMessagesArray[i].getMessageBody();
						
						logDataSensed(System.currentTimeMillis(), content, address, SmsData.SMS_RECEIVED);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected IntentFilter[] getIntentFilters()
	{
		IntentFilter[] filters = new IntentFilter[1];
		filters[0] = new IntentFilter(SMS_RECEIVED);
		return filters;
	}

	protected boolean startSensing()
	{
		prevMessageId = "";

		// register content observer
		ContentResolver contentResolver = applicationContext.getContentResolver();
		contentResolver.registerContentObserver(Uri.parse("content://sms"), true, observer);
		return true;
	}

	protected void stopSensing()
	{
		applicationContext.getContentResolver().unregisterContentObserver(observer);
	}

}
