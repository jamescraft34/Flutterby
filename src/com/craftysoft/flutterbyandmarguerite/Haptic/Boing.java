package com.craftysoft.flutterbyandmarguerite.Haptic;
/*
	How to use:

		// Create and open a device to play effects
		// This should be done only once per application

		Device device = Device.newDevice();

		// Create an IVT buffer with the exported effect definitions

		IVTBuffer ivtBuffer = new IVTBuffer(Boing.ivt);

		// Play one of the effect defined in the IVTBuffer

		device.playIVTEffect(ivtBuffer, Boing.Timeline);

		// When the device is not needed anymore,
		// it must be closed to avoid resource leak

		device.close();

		// For more details, refer to the ImmVibe API Reference.

*/

public class Boing
{
	public static final int Timeline = 0;

	public static final int MagSweep = 1;

	public static final int Periodic = 2;

	public static final byte[] ivt = 
	{
		(byte)0x01, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x34, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x1d, (byte)0x00, (byte)0xf1, (byte)0xe0,
		(byte)0x01, (byte)0xe2, (byte)0x00, (byte)0x00, (byte)0xf1, (byte)0xe0, (byte)0x02, (byte)0xe2,
		(byte)0x01, (byte)0x2b, (byte)0xff, (byte)0x30, (byte)0x28, (byte)0x00, (byte)0x67, (byte)0x00,
		(byte)0x16, (byte)0x08, (byte)0x00, (byte)0x20, (byte)0x13, (byte)0x00, (byte)0x00, (byte)0x7b,
		(byte)0x00, (byte)0x00, (byte)0x11, (byte)0x30, (byte)0xc3, (byte)0x00, (byte)0x34, (byte)0x01,
		(byte)0x33, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x68, (byte)0x00, (byte)0x00, (byte)0x28,
		(byte)0xd3, (byte)0x41, (byte)0xf1, (byte)0x00
	};
}
