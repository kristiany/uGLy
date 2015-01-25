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
 ** Utility methods dealing with texture input/output and miscellaneous related topics
 ** like filtering, texture-compression and mipmaps. 
 **
 **/

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.awt.*;
import static javax.media.opengl.GL2.*;

public class TextureUtils {

    public static Texture loadImageAsTexture(final GL2 inGL, final String inFileName) {
        System.out.println("LOADING IMAGE FILE "+inFileName+" AS TEXTURE UNFLIPPED ...");
        try {
            final Texture tTexture = TextureIO.newTexture(new BufferedInputStream(new FileInputStream(inFileName)),true,null);
            System.out.println("TEXTURE " + inFileName + " (" + tTexture.getWidth() + "x" + tTexture.getWidth() + " AUTOMIPMAPS:" + tTexture
                    .isUsingAutoMipmapGeneration() + ") LOADED! ESTIMATED MEMORY SIZE: " + tTexture.getEstimatedMemorySize());
            return tTexture;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Texture loadImageAsTexture_UNMODIFIED(final GL2 inGL, final String inFileName) {
        System.out.println("LOADING IMAGE FILE "+inFileName+" AS TEXTURE UNFLIPPED ...");
        //kinda 'soften' exception-handling ... -:-)
        try {
            final Texture tTexture = TextureIO.newTexture(new BufferedInputStream(new FileInputStream(inFileName)),true,null);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_WRAP_S,GL_REPEAT);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_WRAP_T,GL_REPEAT);
            System.out.println("TEXTURE " + inFileName + " (" + tTexture.getWidth() + "x" + tTexture.getWidth() + " AUTOMIPMAPS:" + tTexture
                    .isUsingAutoMipmapGeneration() + ") LOADED! ESTIMATED MEMORY SIZE: " + tTexture.getEstimatedMemorySize());
            return tTexture;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Texture loadImageAsTexture_FLIPPED(final GL2 inGL, final String inFileName) {
        //kinda 'soften' exception-handling ... -:-)
        System.out.println("LOADING IMAGE FILE " + inFileName + " AS TEXTURE FLIPPED ...");
        try {
            final BufferedImage tBufferedImage = ImageIO.read(new BufferedInputStream(new FileInputStream(inFileName)));
            ImageUtil.flipImageVertically(tBufferedImage);
            final Texture tTexture = AWTTextureIO.newTexture(GLProfile.getDefault(), tBufferedImage, true);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_WRAP_S,GL_REPEAT);
            tTexture.setTexParameterf(inGL,GL_TEXTURE_WRAP_T,GL_REPEAT);
            System.out.println("TEXTURE " + inFileName + " (" + tTexture.getWidth() + "x" + tTexture.getWidth() + " AUTOMIPMAPS:" + tTexture
                    .isUsingAutoMipmapGeneration() + ") LOADED! ESTIMATED MEMORY SIZE: " + tTexture.getEstimatedMemorySize());
            return tTexture;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static int generateTextureID(final GL2 inGL) {
        final int[] result = new int[1];
        inGL.glGenTextures(1, result, 0);
        System.out.println("ALLOCATED NEW JOGL TEXTURE ID=" + result[0]);
        return result[0];
    }

    public static void deleteTextureID(final GL2 inGL, final int inTextureID) {
        System.out.println("DELETING JOGL TEXTURE ID=" + inTextureID);
        inGL.glDeleteTextures(1, new int[] {inTextureID}, 0);
    }

    public static ByteBuffer convertARGBBufferedImageToJOGLRGBADirectByteBuffer(final BufferedImage inBufferedImage) {
        return convertARGBBufferedImageToJOGLDirectByteBuffer(inBufferedImage,true,true,true,true);
    }

    public static ByteBuffer convertARGBBufferedImageToJOGLRDirectByteBuffer(final BufferedImage inBufferedImage) {
        return convertARGBBufferedImageToJOGLDirectByteBuffer(inBufferedImage,true,false,false,false);
    }

    public static ByteBuffer convertARGBBufferedImageToJOGLDirectByteBuffer(final BufferedImage inBufferedImage, final boolean inPutR,
                                                                            final boolean inPutG,
                                                                            final boolean inPutB,
                                                                            final boolean inPutA) {
        System.out.println("CONVERTING ARGB BUFFERED IMAGE TO JOGL RGBA DIRECT BYTE BUFFER " + inBufferedImage.getWidth() + "x" + inBufferedImage
                .getHeight());
        int tSizeMultiplier = 0;
        if (inPutR) {tSizeMultiplier++;}
        if (inPutG) {tSizeMultiplier++;}
        if (inPutB) {tSizeMultiplier++;}
        if (inPutA) {tSizeMultiplier++;}
        final ByteBuffer tBufferedImageByteBuffer = ByteBuffer.allocateDirect(inBufferedImage.getWidth()*inBufferedImage.getHeight()*tSizeMultiplier);
        tBufferedImageByteBuffer.order(ByteOrder.nativeOrder());
        final int[] tBufferedImage_ARGB = ((DataBufferInt)inBufferedImage.getRaster().getDataBuffer()).getData();
        for (int i=0; i<tBufferedImage_ARGB.length; i++) {
            if (inPutR) {
                final byte tRed   = (byte)((tBufferedImage_ARGB[i] >> 16) & 0xFF);
                tBufferedImageByteBuffer.put(tRed);
            }
            if (inPutG) {
                final byte tGreen = (byte)((tBufferedImage_ARGB[i] >>  8) & 0xFF);
                tBufferedImageByteBuffer.put(tGreen);
            }
            if (inPutB) {
                final byte tBlue  = (byte)((tBufferedImage_ARGB[i]      ) & 0xFF);
                tBufferedImageByteBuffer.put(tBlue);
            }
            if (inPutA) {
                final byte tAlpha = (byte)((tBufferedImage_ARGB[i] >> 24) & 0xFF);
                tBufferedImageByteBuffer.put(tAlpha);
            }
        }
        tBufferedImageByteBuffer.rewind();
        return tBufferedImageByteBuffer;
    }

    public static BufferedImage createARGBBufferedImage(final int inWidth, final int inHeight) {
        System.out.println("CREATING NEW BUFFEREDIMAGE ... " + inWidth + "x" + inHeight);
        final BufferedImage tARGBImageIntermediate = new BufferedImage(inWidth,inHeight, BufferedImage.TYPE_INT_ARGB);
        fillImageWithTransparentColor(tARGBImageIntermediate);
        return tARGBImageIntermediate;
    }

    public static void fillImageWithTransparentColor(final Image inImage) {
        final Color TRANSPARENT = new Color(0,0,0,0);
        fillImageWithColor(inImage,TRANSPARENT);
    }

    public static void fillImageWithColor(final Image inImage, final Color inColor) {
        final Graphics2D tGraphics2D = (Graphics2D)inImage.getGraphics();
        tGraphics2D.setColor(inColor);
        tGraphics2D.setComposite(AlphaComposite.Src);
        tGraphics2D.fillRect(0,0,inImage.getWidth(null),inImage.getHeight(null));
        tGraphics2D.dispose();
    }

    public static int generateTexture1DFromBufferedImage(final GL2 inGL,
                                                         final BufferedImage inBufferedImage, final int inBorderMode) {
        System.out.println("GENERATING 1D TEXTURE FROM ARGB BUFFERED IMAGE " + inBufferedImage.getWidth() + "x" + inBufferedImage
                .getHeight());
        inGL.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        final int t1DTextureID = TextureUtils.generateTextureID(inGL);
        inGL.glEnable(GL_TEXTURE_1D);
        inGL.glBindTexture(GL_TEXTURE_1D, t1DTextureID);
        inGL.glTexImage1D(GL_TEXTURE_1D, 0, GL_RGBA, inBufferedImage.getWidth(), 0, GL_RGBA, GL_UNSIGNED_BYTE, TextureUtils.convertARGBBufferedImageToJOGLRGBADirectByteBuffer(inBufferedImage));
        inGL.glTexParameteri(GL_TEXTURE_1D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        inGL.glTexParameteri(GL_TEXTURE_1D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        inGL.glTexParameteri(GL_TEXTURE_1D,GL_TEXTURE_WRAP_S,inBorderMode);
        inGL.glTexParameteri(GL_TEXTURE_1D,GL_TEXTURE_WRAP_T,inBorderMode);
        return t1DTextureID;
    }

    public static BufferedImage[] loadARGBImageSequence(final String inARGBImageSequenceFileName) {
        System.out.println("LOADING IMAGESEQUENCE FROM ARCHIVE: " + inARGBImageSequenceFileName);
        try {
            final ZipInputStream tZipInputStream = new ZipInputStream(new BufferedInputStream((new Object()).getClass().getResourceAsStream(inARGBImageSequenceFileName)));
            final Hashtable<String,BufferedImage> tHashtable = new Hashtable<String,BufferedImage>();
            final ArrayList<String> tZipEntryFileNames = new ArrayList<String>();
            ZipEntry tZipEntry;
            while((tZipEntry = tZipInputStream.getNextEntry())!=null) {
                final String inZipEntryName = tZipEntry.getName();
                System.out.println("EXTRACTING: " + inZipEntryName);
                if (!tZipEntry.isDirectory()) {
                    final BufferedImage tARGBImage = ImageIO.read(tZipInputStream);
                    System.out.println("LOADED IMAGE FROM INPUT STREAM: " + tARGBImage.getWidth() + "x" + tARGBImage.getHeight());
                    final BufferedImage tARGBImageIntermediate = new BufferedImage(tARGBImage.getWidth(),tARGBImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    ((Graphics2D)tARGBImageIntermediate.getGraphics()).drawImage(tARGBImage, 0,0, null);
                    tHashtable.put(inZipEntryName,tARGBImageIntermediate);
                    tZipEntryFileNames.add(inZipEntryName);
                } else {
                    System.out.println("ERROR! ZIP ENTRY IS DIRECTORY! SHOULD BE PLAIN FILE!");
                }
            }
            tZipInputStream.close();
            //sorts the specified list into ascending order, according to the natural ordering of its elements ...
            System.out.println("SORTING FILENAMES ACCORDING TO NATURAL ORDER ...");
            Collections.sort(tZipEntryFileNames);
            final BufferedImage[] tBufferedImages = new BufferedImage[tZipEntryFileNames.size()];
            for (int i=0; i<tZipEntryFileNames.size(); i++) {
                System.out.println("BUFFEREDIMAGE[" + i + "]=" + tZipEntryFileNames.get(i));
                tBufferedImages[i] = tHashtable.get(tZipEntryFileNames.get(i));
            }
            System.out.println("ADDING IMAGESEQUENCE TO BUFFEREDIMAGESEQUENCECACHE ...");
            return tBufferedImages;
        } catch (final Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void loadBufferedImageAs_GL_TEXTURE_2D_WithTextureDXT1Compression(final BufferedImage inBufferedImage, final int[] inTextureID, final GL2 inGL) {
        final int tWidth = inBufferedImage.getWidth();
        final int tHeight = inBufferedImage.getHeight();
        inGL.glGenTextures(1, inTextureID, 0);
        inGL.glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
        inGL.glBindTexture(GL_TEXTURE_2D, inTextureID[0]);
        inGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        inGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        inGL.glTexImage2D(GL_TEXTURE_2D, 0, GL_COMPRESSED_RGBA_S3TC_DXT1_EXT, tWidth, tHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, convertARGBBufferedImageToJOGLRGBADirectByteBuffer(inBufferedImage));
        final int[] tIsCompressed = new int[1];
        inGL.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_COMPRESSED, tIsCompressed, 0);
        System.out.println("TEXTURE DXT1(6:1 FOR RGB) COMPRESSED=" + tIsCompressed[0]);
        final int[] tCompressedTextureSize = new int[1];
        inGL.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_COMPRESSED_IMAGE_SIZE, tCompressedTextureSize, 0);
        System.out.println("UNCOMPRESSED TEXTURE SIZE=" + (tWidth * tHeight * 4));
        System.out.println("DXT1 COMPRESSED TEXTURE SIZE=" + tCompressedTextureSize[0]);
    }

    public static byte[] readRawFileAsByteArray(final String inFilename) {
        try {
            final InputStream tInputStream = new FileInputStream(inFilename);
            final ByteArrayOutputStream tByteArrayOutputStream = new ByteArrayOutputStream(4096);
            final byte [] tBuffer = new byte [1024];
            int tBytesRead;
            while ( (tBytesRead = tInputStream.read (tBuffer)) > 0 ) {
                tByteArrayOutputStream.write(tBuffer,0,tBytesRead);
            }
            tInputStream.close ();
            return tByteArrayOutputStream.toByteArray ();
        } catch (final Exception e) {
            System.out.println(e);
        }
        return null;
    }

}