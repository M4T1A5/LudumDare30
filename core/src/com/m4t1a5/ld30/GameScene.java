package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private Camera camera;

    private Array<Planet> planets;
    private Array<Line> lines;

    private boolean canDrawLines;
    private boolean linesCrossed;
    private float lineResetTimer;
    private float roundTimer;
    private float nextRoundTimer;
    private int currentRound;
    private float nextRoundTransitionTimer;
    private int planetsLeft;
    private boolean wonRound;
    private boolean lostGame;

    private float lineCrossTime;

    private int score;


    public GameScene(Camera camera)
    {
        this.camera = camera;
    }


    @Override
    public void create()
    {
        planets = new Array<Planet>();
        lines = new Array<Line>();

        canDrawLines = true;
        linesCrossed = false;
        lineResetTimer = 0;
        roundTimer = Settings.BASE_ROUND_TIME;
        nextRoundTimer = Settings.BASE_ROUND_TIME;
        currentRound = 0;
        nextRoundTransitionTimer = 0;
        wonRound = false;
        lostGame = false;
        lineCrossTime = 0;
        score = 0;

        createPlanets(Settings.PLANET_START_COUNT);
        //setupInput();
    }

    @Override
    public void update()
    {
        if(!wonRound && !lostGame)
            roundTimer -= Gdx.graphics.getDeltaTime();

        if(roundTimer <= 0 && !lostGame)
        {
            // Lost the game
            canDrawLines = false;
            lostGame = true;
            roundTimer = 0;
            nextRoundTransitionTimer = Settings.ROUND_TRANSITION_TIME;
        }

        if(lines.size > 0)
        {
            Line newest = lines.get(lines.size - 1);

            // Dont check newest line
            for (int i = 0; i < lines.size - 1; i++)
            {
                if (newest.intersects(lines.get(i)) && !linesCrossed)
                {
                    lineCrossTime += Gdx.graphics.getDeltaTime();
                    break;
                }
            }

            if(lineCrossTime > 0.1f)
            {
                newest.setTexture(Assets.lineErrorTexture);
                canDrawLines = false;
                linesCrossed = true;
                lineResetTimer = Settings.LINE_RESET_TIME;
                lineCrossTime = 0;
            }

            for (int i = 0; i < planets.size; i++)
            {
                Planet planet = planets.get(i);
                if (!planet.isVisited && planet != newest.startPlanet &&
                        (planet.isPointInside(newest.endPos) || planet.intersects(newest)))
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

                    if (planetsLeft == 0)
                    {
                        canDrawLines = false;
                        wonRound = true;
                        nextRoundTransitionTimer = Settings.ROUND_TRANSITION_TIME;
                        score += calculateScore();
                        break;
                    }
                    else
                    {
                        lines.add(new Line(Assets.lineTexture, planet, startPos));
                        break;
                    }
                }
            }
        }

        if(linesCrossed)
        {
            lineResetTimer -= Gdx.graphics.getDeltaTime();
            if(lineResetTimer <= 0)
            {
                resetLinesAndPlanets();
                canDrawLines = true;
                linesCrossed = false;
            }
        }

        // Round is over
        if(wonRound || lostGame)
        {
            if (nextRoundTransitionTimer > 0)
                nextRoundTransitionTimer -= Gdx.graphics.getDeltaTime();
            else if (wonRound)
            {

                nextRound();
            }
            else
            {
                LD30Game game = ((LD30Game) Gdx.app.getApplicationListener());
                game.addScore(score);
                game.changeState(GameState.Menu);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        batch.draw(Assets.gameBackground, 0, 0);

        for (int i = 0; i < lines.size; i++)
        {
            lines.get(i).draw(batch);
        }
        for (int i = 0; i < planets.size; i++)
        {
            planets.get(i).draw(batch);
        }

        final int possibleScore = calculateScore();
        String scoreTime = String.format("Time remaining: %.2f\nRound score: %d\nTotal score: %d",
                roundTimer, possibleScore, score);
        BitmapFont.TextBounds bounds = Assets.font20.getMultiLineBounds(scoreTime);
        Assets.font20.drawMultiLine(batch, scoreTime, Settings.WORLD_WIDTH - bounds.width - 20,
                Settings.WORLD_HEIGHT - 20);

        if(linesCrossed)
        {
            String lineCrossText = "Lines Crossed";
            bounds = Assets.font20.getBounds(lineCrossText);
            Assets.font20.draw(batch, lineCrossText, Settings.WORLD_WIDTH/2 - bounds.width/2,
                    Settings.WORLD_HEIGHT - bounds.height);
        }

        if(lostGame)
        {
            String lostGameText = "Time ran out\nand you lost";
            bounds = Assets.font20.getMultiLineBounds(lostGameText);
            Assets.font20.drawMultiLine(batch, lostGameText, Settings.WORLD_WIDTH / 2 - bounds.width / 2,
                    Settings.WORLD_HEIGHT / 2 + bounds.height / 2);
        }
    }

    @Override
    public void dispose()
    {
        planets.clear();
        lines.clear();
    }

    private void nextRound()
    {
        currentRound++;
        dispose();
        int nextRoundPlanetCount = Settings.PLANET_START_COUNT + currentRound;
        if(nextRoundPlanetCount > Settings.PLANET_MAX_COUNT)
            roundTimer = Math.max(--nextRoundTimer, 1);
        else
            roundTimer = Settings.BASE_ROUND_TIME;

        createPlanets(nextRoundPlanetCount);
        canDrawLines = true;
        wonRound = false;
        linesCrossed = false;
    }

    private int calculateScore()
    {
        return (int)Math.floor((roundTimer / nextRoundTimer)*Settings.SCORE_MULTIPLIER);
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

        planetsLeft = numberOfPlanets;
    }

    private void resetLinesAndPlanets()
    {
        lines.clear();
        for (int i = 0; i < planets.size; i++)
        {
            planets.get(i).isVisited = false;
        }

        planetsLeft = Settings.PLANET_START_COUNT + currentRound;
    }

    private void startNewLine(Vector2 pos)
    {
        for (int i = 0; i < planets.size; i++)
        {
            Planet planet = planets.get(i);
            if(planet.isPointInside(pos))
            {
                Vector2 startPos = new Vector2(planet.getCenter());
                lines.add(new Line(Assets.lineTexture, planet, startPos));
                planet.isVisited = true;
                planetsLeft--;
            }
        }
    }

    @Override
    public void setupInput()
    {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button)
            {
                if(pointer != 0 || !canDrawLines)
                    return true;

                if(lines.size > 0 && !lines.get(lines.size -1).hasEnded)
                    return true;

                Vector3 temp = new Vector3(screenX, screenY, 0);
                temp = camera.unproject(temp);
                Vector2 worldPos = new Vector2(temp.x, temp.y);
                startNewLine(worldPos);

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
