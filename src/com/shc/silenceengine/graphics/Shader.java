package com.shc.silenceengine.graphics;

import com.shc.silenceengine.SilenceException;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.math.Vector3;
import com.shc.silenceengine.math.Vector4;
import com.shc.silenceengine.utils.FileUtils;

import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Sri Harsha Chilakapati
 */
public class Shader
{
    private int programID;
    private int vertShaderID;
    private int fragShaderID;

    public static Shader DEFAULT = null;

    public Shader(String vShader, String fShader)
    {
        this(FileUtils.getResource(vShader), FileUtils.getResource(fShader));
    }

    public Shader(InputStream vShaderStream, InputStream fShaderStream)
    {
        programID = glCreateProgram();

        vertShaderID = glCreateShader(GL_VERTEX_SHADER);
        fragShaderID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertShaderID, FileUtils.readLinesToString(vShaderStream));
        glShaderSource(fragShaderID, FileUtils.readLinesToString(fShaderStream));

        glCompileShader(vertShaderID);
        glCompileShader(fragShaderID);

        if (glGetShaderi(vertShaderID, GL_COMPILE_STATUS) != GL_TRUE)
            throw new SilenceException("Error Compiling Vertex shader: " + glGetShaderInfoLog(vertShaderID));

        if (glGetShaderi(fragShaderID, GL_COMPILE_STATUS) != GL_TRUE)
            throw new SilenceException("Error Compiling Fragment Shader: " + glGetShaderInfoLog(fragShaderID));

        glAttachShader(programID, vertShaderID);
        glAttachShader(programID, fragShaderID);

        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) != GL_TRUE)
            throw new SilenceException("Error Linking Program: " + glGetProgramInfoLog(programID));
    }

    public static void loadDefaultShader()
    {
        if (DEFAULT == null)
            DEFAULT = new Shader("com/shc/silenceengine/res/default-shader.vert", "com/shc/silenceengine/res/default-shader.frag");
    }

    public void use()
    {
        glUseProgram(programID);
    }

    public static void unbind()
    {
        DEFAULT.use();
    }

    public void setUniform(String name, float... values)
    {
        int location = glGetUniformLocation(programID, name);

        if (values.length > 4)
            throw new IllegalArgumentException("The max number of uniform values must be 4.");

        switch (values.length)
        {
            case 1:
                glUniform1f(location, values[0]);
                break;
            case 2:
                glUniform2f(location, values[0], values[1]);
                break;
            case 3:
                glUniform3f(location, values[0], values[1], values[2]);
                break;
            case 4:
                glUniform4f(location, values[0], values[1], values[2], values[3]);
                break;
        }
    }

    public void setUniform(String name, Vector2 value)
    {
        setUniform(name, value.getX(), value.getY());
    }

    public void setUniform(String name, Vector3 value)
    {
        setUniform(name, value.getX(), value.getY(), value.getZ());
    }

    public void setUniform(String name, Vector4 value)
    {
        setUniform(name, value.getX(), value.getY(), value.getZ(), value.getW());
    }

    public void dispose()
    {
        glUseProgram(0);
        glDetachShader(programID, vertShaderID);
        glDetachShader(programID, fragShaderID);
        glDeleteShader(vertShaderID);
        glDeleteShader(fragShaderID);
        glDeleteProgram(programID);
    }
}