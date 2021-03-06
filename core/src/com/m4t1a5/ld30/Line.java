package com.m4t1a5.ld30;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Matias on 23.8.2014.
 */
public class Line extends Entity
{
    public Vector2 startPos;
    public Vector2 endPos;

    public Planet startPlanet;
    public boolean hasEnded = false;

    public Line(Texture texture, Planet startPlanet)
    {
        super(texture);
        this.startPlanet = startPlanet;
    }

    public Line(Texture tex, Planet startPlanet, Vector2 startPos)
    {
        super(tex);
        this.startPos = new Vector2(startPos);
        endPos = new Vector2(startPos);
        this.startPlanet = startPlanet;
        update();
    }

    public Line(Texture tex, Planet startPlanet, Vector2 startPos, Vector2 endPos)
    {
        super(tex);
        this.startPos = new Vector2(startPos);
        this.endPos = new Vector2(endPos);
        this.startPlanet = startPlanet;
        update();
    }

    public void setStartPos(Vector2 startPos)
    {
        this.startPos = new Vector2(startPos);
        update();
    }

    public void setEndPos(Vector2 endPos)
    {
        this.endPos = new Vector2(endPos);
        update();
    }

    public boolean intersects(Line other)
    {
        // http://stackoverflow.com/a/565282
        Vector2 qsubp = new Vector2(other.startPos).sub(startPos);
        Vector2 s = new Vector2(other.endPos).sub(other.startPos);
        Vector2 r = new Vector2(endPos).sub(startPos);


        if(qsubp.crs(r) == 0 && r.crs(s) == 0)
        {
            float a = qsubp.dot(r);
            float b = new Vector2(other.endPos).sub(startPos).dot(s);

            // colinear, so do they overlap?
            return ((a >= 0 && a <= r.dot(r)) || (b >= 0 && b <= s.dot(s)));

        }
        else if(r.crs(s) == 0)
            return false; // Lines are parallel

        float u = qsubp.crs(r) / (r.crs(s));
        float t = qsubp.crs(s) / (r.crs(s));
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    private void update()
    {
        final Vector2 startToEnd = new Vector2(endPos).sub(startPos);

        setPosition(startPos);
        setRotation(startToEnd.angle());
        setSize(startToEnd.len(), getHeight());
    }
}
