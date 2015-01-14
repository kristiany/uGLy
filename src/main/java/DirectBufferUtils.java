/**
 **   __ __|_  ___________________________________________________________________________  ___|__ __
 **  //    /\                                           _                                  /\    \\
 ** //____/  \__     __ _____ _____ _____ _____ _____  | |     __ _____ _____ __        __/  \____\\
 **  \    \  / /  __|  |     |   __|  _  |     |  _  | | |  __|  |     |   __|  |      /\ \  /    /
 **   \____\/_/  |  |  |  |  |  |  |     | | | |   __| | | |  |  |  |  |  |  |  |__   "  \_\/____/
 **  /\    \     |_____|_____|_____|__|__|_|_|_|__|    | | |_____|_____|_____|_____|  _  /    /\
 ** /  \____\                       http://jogamp.org  |_|                              /____/  \
 ** \  /   "' _________________________________________________________________________ `"   \  /
 **  \/____.                                                                             .____\/
 **
 ** Some simple NIO direct-buffer utilities helping with creation and updating of native data arrays.
 **
 **/

import java.nio.*;
import com.jogamp.common.nio.*;

public class DirectBufferUtils {

    public static FloatBuffer createDirectFloatBuffer(final float[] inFloatArray) {
        final FloatBuffer tDirectFloatBuffer = Buffers.newDirectFloatBuffer(inFloatArray.length);
        tDirectFloatBuffer.put(inFloatArray);
        tDirectFloatBuffer.rewind();
        if (tDirectFloatBuffer.isDirect()) {
            System.out.println("INFO ALLOCATED DIRECT FLOATBUFFER ... LENGHT="+inFloatArray.length);
        } else {
            System.out.println("WARNING ALLOCATED NON-DIRECT FLOATBUFFER ... LENGHT=" + inFloatArray.length);
        }
        return tDirectFloatBuffer;
    }

    public static DoubleBuffer createDirectDoubleBuffer(final double[] inDoubleArray) {
        final DoubleBuffer tDirectDoubleBuffer = Buffers.newDirectDoubleBuffer(inDoubleArray.length);
        tDirectDoubleBuffer.put(inDoubleArray);
        tDirectDoubleBuffer.rewind();
        if (tDirectDoubleBuffer.isDirect()) {
            System.out.println("INFO ALLOCATED DIRECT DOUBLEBUFFER ... LENGHT=" + inDoubleArray.length);
        } else {
            System.out.println("WARNING ALLOCATED NON-DIRECT DOUBLEBUFFER ... LENGHT=" + inDoubleArray.length);
        }
        return tDirectDoubleBuffer;
    }

    public static void updateDirectFloatBuffer(final FloatBuffer inDirectFloatBuffer, final float[] inFloatArray) {
        inDirectFloatBuffer.rewind();
        inDirectFloatBuffer.put(inFloatArray);
        inDirectFloatBuffer.rewind();
        if (inDirectFloatBuffer.isDirect()) {
            //currently disabled ... spams the logs X-)
            //BaseLogging.getInstance().info("UPDATED DIRECT FLOATBUFFER ... LENGHT="+inFloatArray.length);
        } else {
            System.out.println("WARNING UPDATED NON-DIRECT FLOATBUFFER ... LENGHT=" + inFloatArray.length);
        }
    }

    public static FloatBuffer createDirectFloatBuffer(final int inLength) {
        final FloatBuffer tDirectFloatBuffer = Buffers.newDirectFloatBuffer(inLength);
        if (tDirectFloatBuffer.isDirect()) {
            System.out.println("INFO ALLOCATED DIRECT FLOATBUFFER ... LENGHT=" + inLength);
        } else {
            System.out.println("WARNING ALLOCATED NON-DIRECT FLOATBUFFER ... LENGHT=" + inLength);
        }
        return tDirectFloatBuffer;
    }

    public static DoubleBuffer createDirectDoubleBuffer(final int inLength) {
        final DoubleBuffer tDirectDoubleBuffer = Buffers.newDirectDoubleBuffer(inLength);
        if (tDirectDoubleBuffer.isDirect()) {
            System.out.println("INFO ALLOCATED DIRECT DOUBLEBUFFER ... LENGHT=" + inLength);
        } else {
            System.out.println("WARNING ALLOCATED NON-DIRECT DOUBLEBUFFER ... LENGHT=" + inLength);
        }
        return tDirectDoubleBuffer;
    }

}
