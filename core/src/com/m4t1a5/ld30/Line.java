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

    public Line(Texture texture)
    {
        super(texture);
    }

    public Line(Texture tex, Vector2 startPos)
    {
        super(tex);
        this.startPos = new Vector2(startPos);
        endPos = new Vector2(startPos);
        update();
    }

    public Line(Texture tex, Vector2 startPos, Vector2 endPos)
    {
        super(tex);
        this.startPos = new Vector2(startPos);
        this.endPos = new Vector2(endPos);
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

        if(r.crs(s) == 0)
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
