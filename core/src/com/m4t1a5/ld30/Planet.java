package com.m4t1a5.ld30;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Matias on 23.8.2014.
 */
public class Planet extends Entity
{
    public boolean isVisited = false;

    private Entity outline;

    public Planet(Texture planetTexture, Texture outlineTexture)
    {
        super(planetTexture);
        outline = new Entity(outlineTexture);
    }

    public float getRadius()
    {
        return getTexture().getWidth()/2;
    }

    public Vector2 getCenter()
    {
        return new Vector2(getPosition()).add(getWidth()/2, getHeight()/2);
    }

    public boolean isPointInside(Vector2 point)
    {
        // Dont modify the original point
        Vector2 testPoint = new Vector2(point);
        if(testPoint.sub(getCenter()).len2() < Math.pow(getRadius(), 2))
            return true;

        return false;
    }

    @Override
    public void setPosition(Vector2 position)
    {
        super.setPosition(position.sub(getWidth() / 2, getHeight() / 2));
        // Get the difference in texture sizes and move the outline so it is centered on the planet
        position.x -= (outline.getTexture().getWidth() - getTexture().getWidth()) / 2;
        position.y -= (outline.getTexture().getHeight() - getTexture().getWidth()) / 2;
        outline.setPosition(position);
    }

    @Override
    public void draw(Batch batch)
    {
        if(isVisited)
        {
            outline.draw(batch);
            super.draw(batch);
        }
        else
            super.draw(batch);
    }
}
