import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

public class Land {
    private final String heightMapFilename;
    private final int vertexWidth;
    private final int vertexDepth;
    private final float worldWidth;
    private final float worldDepth;
    private final int listId;
    private int program;
    private final Texture texture;
    private int vertexShader;
    private int fragmentShader;

    public Land(final GL2 gl,
                final String heightMapFilename,
                final int vertexWidth,
                final int vertexDepth,
                final float worldWidth,
                final float worldDepth) {
        this.heightMapFilename = heightMapFilename;
        this.vertexWidth = vertexWidth;
        this.vertexDepth = vertexDepth;
        this.worldWidth = worldWidth;
        this.worldDepth = worldDepth;
        this.listId = gl.glGenLists(1);
        gl.glNewList(this.listId, GL2.GL_COMPILE);
            registerDisplayList(gl);
        gl.glEndList();
        gl.glActiveTexture(GL.GL_TEXTURE0);
        this.texture = TextureUtils.loadImageAsTexture_UNMODIFIED(gl, "src/main/resource/textures/land.bmp");
        this.texture.bind(gl);
        setupShaders(gl);
    }

    public void dispose(final GL2 gl) {
        gl.glDeleteLists(this.listId, 1);
        gl.glDeleteShader(this.vertexShader);
        gl.glDeleteShader(this.fragmentShader);
        gl.glDeleteProgram(this.program);
        gl.glDeleteTextures(1, new int[]{ GL.GL_TEXTURE0 }, 0);
    }

    public void draw(final GL2 gl) {
        gl.glUseProgram(this.program);
        this.texture.enable(gl);
        gl.glCallList(this.listId);
        this.texture.disable(gl);
        gl.glUseProgram(0);
    }

    private void setupShaders(final GL2 gl) {
        this.vertexShader = ShaderUtils.loadVertexShaderFromFile(gl, "src/main/resource/shaders/3lights.vs");
        this.fragmentShader = ShaderUtils.loadFragmentShaderFromFile(gl, "src/main/resource/shaders/surface.fs");
        this.program = ShaderUtils.generateSimple_1xVS_1xFS_ShaderProgramm(gl, this.vertexShader, this.fragmentShader);
        gl.glUseProgram(this.program);
        ShaderUtils.setSampler2DUniformOnTextureUnit(gl, this.program, "sampler0", this.texture, GL.GL_TEXTURE0, 0);
        gl.glUseProgram(0);
    }

    private void registerDisplayList(final GL2 gl) {
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
