package com.shc.silenceengine.tests;

import com.shc.silenceengine.collision.colliders.GridSceneCollider;
import com.shc.silenceengine.core.Display;
import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.entity.Entity2D;
import com.shc.silenceengine.geom2d.Rectangle;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.scene.Scene;
import com.shc.silenceengine.utils.*;

/**
 * @author Sri Harsha Chilakapati
 */
public class GridSceneColliderTest extends Game
{
    private Scene                 scene;
    private GridSceneCollider     collider;
    private OrthoCam              cam;

    public void init()
    {
        Display.setTitle("GridSceneCollider Test");
        Display.setFullScreen(true);

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
        collider = new GridSceneCollider(Display.getWidth(), Display.getHeight(), 48, 48);
        collider.setScene(scene);

        // Register entities for collisions
        collider.register(Player.class, Box.class);
    }

    public void update(float delta)
    {
        if (Keyboard.isPressed(Keyboard.KEY_ESCAPE))
            end();

        // Update the scene and check for collisions
        scene.update(delta);
        collider.checkCollisions();
    }

    public void render(float delta, Batcher batcher)
    {
        cam.apply();
        scene.render(delta, batcher);
    }

    public void resize()
    {
        cam.initProjection(Display.getWidth(), Display.getHeight());
    }

    public void dispose()
    {
        scene.destroy();
    }

    public static void main(String[] args)
    {
        new GridSceneColliderTest().start();
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

        public Player(Vector2 position)
        {
            setPolygon(new Rectangle(0, 0, 48, 48));
            setPosition(position);

            color = Color.random();

            rotate(45);
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
        }

        public void collision(Entity2D other)
        {
            color = Color.random();

            alignNextTo(other);
            bounce(other);
        }

        public void render(float delta, Batcher batcher)
        {
            RenderUtils.fillPolygon(batcher, getPolygon(), getVelocity().scale(delta), color);
            RenderUtils.tracePolygon(batcher, getPolygon(), getVelocity().scale(delta), Color.GREEN);
        }
    }
}
