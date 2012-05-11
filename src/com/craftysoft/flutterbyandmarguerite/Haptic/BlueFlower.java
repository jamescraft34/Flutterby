package com.craftysoft.flutterbyandmarguerite.Haptic;
/*
	How to use:

		// Create and open a device to play effects
		// This should be done only once per application

		Device device = Device.newDevice();

		// Create an IVT buffer with the exported effect definitions

		IVTBuffer ivtBuffer = new IVTBuffer(BlueFlower.ivt);

		// Play one of the effect defined in the IVTBuffer

		device.playIVTEffect(ivtBuffer, BlueFlower.Timeline);

		// When the device is not needed anymore,
		// it must be closed to avoid resource leak

		device.close();

		// For more details, refer to the ImmVibe API Reference.

*/

public class BlueFlower
{
	public static final int Timeline = 0;

	public static final int MagSweep = 1;

	public static final int Periodic = 2;

	public static final int MagSweep__1 = 3;

	public static final byte[] ivt = 
	{
		(byte)0x01, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x17, (byte)0x00, (byte)0x27, (byte)0x00,
		(byte)0xf1, (byte)0xe0, (byte)0x03, (byte)0xe2, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x30,
		(byte)0x2a, (byte)0x00, (byte)0x6c, (byte)0x00, (byte)0x36, (byte)0x11, (byte)0x00, (byte)0x20,
		(byte)0x0b, (byte)0x00, (byte)0x00, (byte)0x25, (byte)0x00, (byte)0x00, (byte)0xb1, (byte)0x30,
		(byte)0xc8, (byte)0x00, (byte)0xc8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20,
		(byte)0x3e, (byte)0x00, (byte)0x00, (byte)0x5f, (byte)0x81, (byte)0x3e, (byte)0x81, (byte)0x30,
		(byte)0x80, (byte)0x00, (byte)0x98, (byte)0x00, (byte)0x2b, (byte)0x30, (byte)0x00, (byte)0x20,
		(byte)0x19, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x40, (byte)0x00
	};
}
