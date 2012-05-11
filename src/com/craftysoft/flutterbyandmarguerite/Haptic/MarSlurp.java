package com.craftysoft.flutterbyandmarguerite.Haptic;
/*
	How to use:

		// Create and open a device to play effects
		// This should be done only once per application

		Device device = Device.newDevice();

		// Create an IVT buffer with the exported effect definitions

		IVTBuffer ivtBuffer = new IVTBuffer(MarSlurp.ivt);

		// Play one of the effect defined in the IVTBuffer

		device.playIVTEffect(ivtBuffer, MarSlurp.Timeline);

		// When the device is not needed anymore,
		// it must be closed to avoid resource leak

		device.close();

		// For more details, refer to the ImmVibe API Reference.

*/

public class MarSlurp
{
	public static final int Timeline = 0;

	public static final int MagSweep = 1;

	public static final int Periodic = 2;

	public static final int MagSweep__1 = 3;

	public static final byte[] ivt = 
	{
		(byte)0x01, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x46, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x1d, (byte)0x00, (byte)0x2d, (byte)0x00,
		(byte)0xf1, (byte)0xe0, (byte)0x02, (byte)0xe2, (byte)0x00, (byte)0xb9, (byte)0xf1, (byte)0xe0,
		(byte)0x03, (byte)0xe2, (byte)0x01, (byte)0x5d, (byte)0xff, (byte)0x30, (byte)0x83, (byte)0x00,
		(byte)0xf1, (byte)0x00, (byte)0x10, (byte)0x7f, (byte)0x00, (byte)0x20, (byte)0x1e, (byte)0x00,
		(byte)0x00, (byte)0x29, (byte)0x00, (byte)0x00, (byte)0xf1, (byte)0x30, (byte)0x68, (byte)0x00,
		(byte)0x8b, (byte)0x00, (byte)0x0d, (byte)0x3b, (byte)0x00, (byte)0x20, (byte)0x1c, (byte)0x00,
		(byte)0x00, (byte)0x38, (byte)0x91, (byte)0x40, (byte)0xa1, (byte)0x30, (byte)0x0f, (byte)0x00,
		(byte)0x10, (byte)0x00, (byte)0x7f, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x03, (byte)0x00,
		(byte)0x00, (byte)0x7f, (byte)0x00, (byte)0x00, (byte)0xd1, (byte)0x00
	};
}
