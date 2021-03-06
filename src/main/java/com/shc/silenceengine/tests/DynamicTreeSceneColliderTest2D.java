/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Sri Harsha Chilakapati
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.shc.silenceengine.tests;

import com.shc.silenceengine.collision.broadphase.DynamicTree2D;
import com.shc.silenceengine.collision.colliders.SceneCollider2D;
import com.shc.silenceengine.core.Display;
import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.graphics.opengl.GL3Context;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.math.geom2d.Rectangle;
import com.shc.silenceengine.scene.Scene;
import com.shc.silenceengine.scene.entity.Entity2D;
import com.shc.silenceengine.utils.RenderUtils;

/**
 * @author Sri Harsha Chilakapati
 */
public class DynamicTreeSceneColliderTest2D extends Game
{
    private Scene           scene;
    private SceneCollider2D collider;
    private OrthoCam        cam;

    public static void main(String[] args)
    {
        new DynamicTreeSceneColliderTest2D().start();
    }

    public void init()
    {
        Display.setTitle("DynamicTree Collider Test 2D");

        GL3Context.clearColor(Color.DARK_SLATE_GRAY);

        cam = new OrthoCam().initProjection(Display.getWidth(), Display.getHeight());

        // Create and initialize the scene
        scene = new Scene();
        for (int i = 0; i < 20; i++)
        {
            scene.addChild(new Box(new Vector2(48 * i, 0)));
            scene.addChild(new Box(new Vector2(0, 48 * i)));

            scene.addChild(new Box(new Vector2(48 * i, 48 * 19)));
            scene.addChild(new Box(new Vector2(48 * 19, 48 * i)));
        }
        scene.addChild(new Player(new Vector2(Display.getWidth() / 2 - 24, Display.getHeight() / 2 - 24)));
        scene.init();

        // Create the SceneCollider and set the scene
        collider = new SceneCollider2D(new DynamicTree2D());
        collider.setScene(scene);

        // Register entities for collisions
        collider.register(Player.class, Box.class);
    }

    public void resize()
    {
        cam.initProjection(Display.getWidth(), Display.getHeight());
    }

    public void update(float delta)
    {
        if (Keyboard.isPressed(Keyboard.KEY_ESCAPE))
            end();

        // Update the scene and check for collisions
        scene.update(delta);
        collider.checkCollisions();

        Display.setTitle("Total Memory: " + (getTotalMemory() / 1048576) + "MB / Free Memory: " + (getFreeMemory() / 1048576) + "MB / Used Memory: " + (getUsedMemory() / 1048576) + "MB");
    }

    public void render(float delta, Batcher batcher)
    {
        cam.apply();
        scene.render(delta, batcher);
    }

    public void dispose()
    {
        scene.destroy();
    }

    public class Box extends Entity2D
    {
        public Box(Vector2 position)
        {
            setPolygon(new Rectangle(0, 0, 48, 48));
            setPosition(position);
        }

        public void render(float delta, Batcher batcher)
        {
            RenderUtils.fillPolygon(batcher, getPolygon(), Color.CORN_FLOWER_BLUE);
            RenderUtils.tracePolygon(batcher, getPolygon(), Color.RED);
        }
    }

    public class Player extends Entity2D
    {
        private Color color;
        private Vector2 temp = new Vector2();

        public Player(Vector2 position)
        {
            setPolygon(new Rectangle(0, 0, 48, 48));
            setPosition(position);

            color = Color.random();
        }

        public void update(float delta)
        {
            float speed = 4;

            if (Keyboard.isPressed(Keyboard.KEY_UP))
                getVelocity().y = -speed;

            if (Keyboard.isPressed(Keyboard.KEY_DOWN))
                getVelocity().y = +speed;

            if (Keyboard.isPressed(Keyboard.KEY_LEFT))
                getVelocity().x = -speed;

            if (Keyboard.isPressed(Keyboard.KEY_RIGHT))
                getVelocity().x = +speed;

            cam.center(getPolygon().getCenter());

            rotate(90 * delta);
        }

        public void collision(Entity2D other)
        {
            color = Color.random();

            alignNextTo(other);
            bounce(other);
        }

        public void render(float delta, Batcher batcher)
        {
            RenderUtils.fillPolygon(batcher, getPolygon(), temp.set(getVelocity()), color);
            RenderUtils.tracePolygon(batcher, getPolygon(), temp.set(getVelocity()), Color.GREEN);
        }
    }
}
