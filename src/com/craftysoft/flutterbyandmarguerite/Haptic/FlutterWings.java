package com.craftysoft.flutterbyandmarguerite.Haptic;
/*
	How to use:

		// Create and open a device to play effects
		// This should be done only once per application

		Device device = Device.newDevice();

		// Create an IVT buffer with the exported effect definitions

		IVTBuffer ivtBuffer = new IVTBuffer(FlutterWings.ivt);

		// Play one of the effect defined in the IVTBuffer

		device.playIVTEffect(ivtBuffer, FlutterWings.Timeline);

		// When the device is not needed anymore,
		// it must be closed to avoid resource leak

		device.close();

		// For more details, refer to the ImmVibe API Reference.

*/

public class FlutterWings
{
	public static final int Timeline = 0;

	public static final int Periodic = 1;

	public static final byte[] ivt = 
	{
		(byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x1c, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0xf1, (byte)0xe0, (byte)0x01, (byte)0xe2,
		(byte)0x00, (byte)0x00, (byte)0xff, (byte)0x30, (byte)0xd9, (byte)0x00, (byte)0x30, (byte)0x02,
		(byte)0x36, (byte)0x23, (byte)0x00, (byte)0x20, (byte)0x12, (byte)0x01, (byte)0x00, (byte)0x50,
		(byte)0x51, (byte)0x29, (byte)0x11, (byte)0x00
	};
}
