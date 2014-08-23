package com.m4t1a5.ld30;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Matias on 23.8.2014.
 */
public class Line extends Entity
{
    private Vector2 startPos;
    private Vector2 endPos;

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


        return false;
    }

    private void update()
    {
        final Vector2 startToEnd = endPos.sub(startPos);

        setPosition(startPos);
        setRotation(startToEnd.angle());
        setSize(startToEnd.len(), getHeight());
    }
}
