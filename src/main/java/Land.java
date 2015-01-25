import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Land {
    private final String heightMapFilename;
    private final int vertexWidth;
    private final int vertexDepth;
    private final float worldWidth;
    private final float worldDepth;

    public Land(final String heightMapFilename,
                final int vertexWidth,
                final int vertexDepth,
                final float worldWidth,
                final float worldDepth) {
        this.heightMapFilename = heightMapFilename;
        this.vertexWidth = vertexWidth;
        this.vertexDepth = vertexDepth;
        this.worldWidth = worldWidth;
        this.worldDepth = worldDepth;
    }

    public void registerDisplayList(final GL2 gl) {
        final float widthVertexStep = this.worldWidth / this.vertexWidth;
        final float depthVertexStep = this.worldDepth / this.vertexDepth;
        final float widthTextureStep = 1.f / this.vertexWidth;
        final float depthTextureStep = 1.f / this.vertexDepth;
        final byte[] heightMap = TextureUtils.readRawFileAsByteArray(this.heightMapFilename);
        for(int z = 0; z < this.vertexDepth - 1; z++) {
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for(int x = 0; x < this.vertexWidth; x++) {
                vertex(gl, heightMap, widthVertexStep, depthVertexStep, widthTextureStep, depthTextureStep, x, z + 1);
                vertex(gl, heightMap, widthVertexStep, depthVertexStep, widthTextureStep, depthTextureStep, x, z);
            }
            gl.glEnd();
        }
    }

    private void vertex(final GL2 gl,
                        final byte[] heightMap,
                        final float widthVertexStep,
                        final float depthVertexStep,
                        final float widthTextureStep,
                        final float depthTextureStep,
                        final int x,
                        final int z) {
        gl.glVertex3f(x * widthVertexStep, landHeight(x, z, heightMap) * 10.f, z * depthVertexStep);
        gl.glTexCoord2f(x * widthTextureStep, z * depthTextureStep);
    }

    private float landHeight(final int x, final int z, final byte[] heightMap) {
        return normalize(heightMap[index(x, z) * 3] & 0xFF);
    }

    private int index(final int x, final int z) {
        return flipz(z) * this.vertexDepth + x;
    }

    private int flipz(final int z) {
        return this.vertexDepth - 1 - z;
    }

    private static float normalize(final int byteValue) {
        return byteValue / 255.f;
    }

}
