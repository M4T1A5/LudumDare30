package com.m4t1a5.ld30;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LD30Game extends ApplicationAdapter
{
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;

    private Array<Planet> planets;
    private Array<Line> lines;

    private boolean canDrawLines = true;
    private int planetsLeft = Settings.PLANET_START;

    BitmapFont font;

    // Assets
    Texture planetTexture, planetOutlineTexture;
    Texture lineTexture, lineErrorTexture;

    @Override
    public void create()
    {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        // Move camera to the lower right corner
        camera.position.set(Settings.WORLD_WIDTH / 2, Settings.WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, camera);

        planets = new Array<Planet>();
        lines = new Array<Line>();

        loadAssets();
        setupInput();
        createPlanets(Settings.PLANET_START);
    }

    @Override
    public void render()
    {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        batch.dispose();
        planetTexture.dispose();
        planetOutlineTexture.dispose();
        lineTexture.dispose();
    }

    private void update()
    {
    }

    private void draw()
    {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < lines.size; i++)
        {
            lines.get(i).draw(batch);
        }
        for (int i = 0; i < planets.size; i++)
        {
            planets.get(i).draw(batch);
        }

        String text = String.format("Derps\nDurps");
        BitmapFont.TextBounds bounds = font.getMultiLineBounds(text);
        font.drawMultiLine(batch, text, Settings.WORLD_WIDTH - bounds.width, Settings.WORLD_HEIGHT - bounds.height);
        batch.end();
    }

    private void createPlanets(int numberOfPlanets)
    {
        final int planetWidth = planetTexture.getWidth();
        final int planetHeight = planetTexture.getHeight();
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


            planets.add(new Planet(planetTexture, planetOutlineTexture));
            planets.get(planets.size - 1).setPosition(spawnPos);
        }
    }

    private void loadAssets()
    {
        planetTexture = new Texture(Gdx.files.internal("planet.png"));
        planetOutlineTexture = new Texture(Gdx.files.internal("planet_outline.png"));
        lineTexture = new Texture(Gdx.files.internal("line_piece.png"));
        lineErrorTexture = new Texture(Gdx.files.internal("line_piece_error.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("kenpixel_future.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        // No need to generate more characters because i will never use them
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890:;";
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private void setupInput()
    {
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
                        lines.add(new Line(lineTexture, startPos));
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
                newest.setTexture(lineTexture);

                // Dont check newest line
                for (int i = 0; i < lines.size - 1; i++)
                {
                    if(newest.intersects(lines.get(i)))
                    {
                        newest.setTexture(lineErrorTexture);
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
                            lines.add(new Line(lineTexture, startPos));
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
