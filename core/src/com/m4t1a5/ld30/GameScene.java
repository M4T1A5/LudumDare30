package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Matias on 24.8.2014.
 */
public class GameScene implements Scene
{
    private Array<Planet> planets;
    private Array<Line> lines;

    private boolean canDrawLines = true;
    private int planetsLeft = Settings.PLANET_START_COUNT;

    private Camera camera;

    public GameScene(Camera camera)
    {
        this.camera = camera;
    }


    @Override
    public void create()
    {
        planets = new Array<Planet>();
        lines = new Array<Line>();

        createPlanets(Settings.PLANET_START_COUNT);
        setupInput();
    }

    @Override
    public void update()
    {
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        for (int i = 0; i < lines.size; i++)
        {
            lines.get(i).draw(batch);
        }
        for (int i = 0; i < planets.size; i++)
        {
            planets.get(i).draw(batch);
        }
    }

    @Override
    public void dispose()
    {
        planets.clear();
        lines.clear();
    }

    private void createPlanets(int numberOfPlanets)
    {
        final int planetWidth = Assets.planetTexture.getWidth();
        final int planetHeight = Assets.planetTexture.getHeight();
        while (planets.size < numberOfPlanets)
        {
            final int x = MathUtils.random(planetWidth, Settings.WORLD_WIDTH - planetWidth);
            final int y = MathUtils.random(planetHeight, Settings.WORLD_HEIGHT - planetHeight);
            final Vector2 spawnPos = new Vector2(x, y);

            boolean isValidSpawn = true;
            for (int i = 0; i < planets.size; i++)
            {
                final Vector2 planetPos = planets.get(i).getPosition();
                if(planetPos.sub(spawnPos).len2() < Math.pow(Settings.PLANET_SAFEZONE, 2))
                {
                    isValidSpawn = false;
                    break;
                }
            }
            if(!isValidSpawn)
                continue;


            planets.add(new Planet(Assets.planetTexture, Assets.planetOutlineTexture));
            planets.get(planets.size - 1).setPosition(spawnPos);
        }
    }

    private void setupInput()
    {
        // TODO: possibly cleanup input
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button)
            {
                if(pointer != 0 || !canDrawLines)
                    return true;

                Vector3 temp = new Vector3(screenX, screenY, 0);
                temp = camera.unproject(temp);
                Vector2 worldPos = new Vector2(temp.x, temp.y);

                for (int i = 0; i < planets.size; i++)
                {
                    Planet planet = planets.get(i);
                    if(planet.isPointInside(worldPos))
                    {
                        Vector2 startPos = new Vector2(planet.getCenter());
                        lines.add(new Line(Assets.lineTexture, startPos));
                        planet.isVisited = true;
                        planetsLeft--;
                        return true;
                    }
                }

                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer)
            {
                if(pointer != 0 || lines.size == 0 || !canDrawLines)
                    return true;

                Vector3 temp = new Vector3(screenX, screenY, 0);
                temp = camera.unproject(temp);
                Vector2 worldPos = new Vector2(temp.x, temp.y);

                Line newest = lines.get(lines.size-1);
                newest.setEndPos(worldPos);
                newest.setTexture(Assets.lineTexture);

                // Dont check newest line
                for (int i = 0; i < lines.size - 1; i++)
                {
                    if(newest.intersects(lines.get(i)))
                    {
                        newest.setTexture(Assets.lineErrorTexture);
                        break;
                    }
                }

                for (int i = 0; i < planets.size; i++)
                {
                    Planet planet = planets.get(i);
                    if(!planet.isVisited && planet.isPointInside(worldPos))
                    {
                        Vector2 endPos = new Vector2(planet.getCenter());
                        Vector2 startPos = new Vector2(endPos);
                        Vector2 lineNormal = new Vector2(newest.endPos).sub(newest.startPos).nor();
                        lineNormal.scl(2);
                        endPos.sub(lineNormal);
                        startPos.add(lineNormal);
                        newest.setEndPos(endPos);
                        planet.isVisited = true;
                        planetsLeft--;

                        if(planetsLeft == 0)
                        {
                            canDrawLines = false;
                            return true;
                        }
                        else
                        {
                            lines.add(new Line(Assets.lineTexture, startPos));
                            break;
                        }
                    }
                }

                lines.get(lines.size - 1).setEndPos(worldPos);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button)
            {
                if(pointer != 0 || lines.size == 0 || !canDrawLines)
                    return true;

                Vector3 worldPos = new Vector3(screenX, screenY, 0);
                worldPos = camera.unproject(worldPos);
                lines.get(lines.size - 1).setEndPos(new Vector2(worldPos.x, worldPos.y));
                return true;
            }
        });
    }
}