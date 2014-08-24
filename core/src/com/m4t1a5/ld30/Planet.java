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

    public boolean intersects(Line line)
    {
        // http://stackoverflow.com/a/1084899

        Vector2 d = new Vector2(line.endPos).sub(line.startPos);
        Vector2 f = new Vector2(line.startPos).sub(getCenter());

        float r = getRadius();
        float a = d.dot(d) ;
        float b = 2*f.dot(d) ;
        float c = f.dot(f) - r*r ;

        float discriminant = b*b-4*a*c;
        if(discriminant >= 0)
        {
            // ray didn't totally miss sphere,
            // so there is a solution to
            // the equation.

            discriminant = (float)Math.sqrt(discriminant);

            // either solution may be on or off the ray so need to test both
            // t1 is always the smaller value, because BOTH discriminant and
            // a are nonnegative.
            float t1 = (-b - discriminant)/(2*a);
            float t2 = (-b + discriminant)/(2*a);

            // 3x HIT cases:
            //          -o->             --|-->  |            |  --|->
            // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

            // 3x MISS cases:
            //       ->  o                     o ->              | -> |
            // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

            if( t1 >= 0 && t1 <= 1 )
            {
                // t1 is the intersection, and it's closer than t2
                // (since t1 uses -b - discriminant)
                // Impale, Poke
                return true;
            }

            // here t1 didn't intersect so we are either started
            // inside the sphere or completely past it
            if( t2 >= 0 && t2 <= 1 )
            {
                // ExitWound
                return true;
            }

            // no intn: FallShort, Past, CompletelyInside
            return false;
        }

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
