package com.m4t1a5.ld30;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Matias on 23.8.2014.
 */
public class Entity extends Sprite
{
    public Entity(Texture texture)
    {
        super(texture);
    }

    public Vector2 getPosition()
    {
        return new Vector2(getX(), getY());
    }

    public void setPosition(Vector2 position)
    {
        setPosition(position.x, position.y);
    }

    public void move(Vector2 offset)
    {
        translate(offset.x, offset.y);
    }
}
