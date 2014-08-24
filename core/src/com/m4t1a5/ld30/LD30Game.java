package com.m4t1a5.ld30;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LD30Game extends ApplicationAdapter
{
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;

    private GameState gameState = GameState.Menu;

    private MenuScene menuScene;
    private GameScene gameScene;

    @Override
    public void create()
    {
        Assets.loadAssets();

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        // Move camera to the lower right corner
        camera.position.set(Settings.WORLD_WIDTH / 2, Settings.WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, camera);

        menuScene = new MenuScene(viewport);
        menuScene.create();

        gameScene = new GameScene(camera);
        gameScene.create();
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
        gameScene.dispose();
        Assets.dispose();
    }

    public void changeState(GameState newState)
    {
        gameState = newState;
        switch (gameState)
        {
            case Menu:
                menuScene.setupInput();
                break;
            case Play:
                gameScene.dispose();
                gameScene.create();
                gameScene.setupInput();
                break;
        }
    }

    private void update()
    {
        switch (gameState)
        {
            case Menu:
                menuScene.update();
                break;
            case Play:
                gameScene.update();
                break;
        }
    }

    private void draw()
    {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        switch (gameState)
        {
            case Menu:
                menuScene.draw(batch);
                break;
            case Play:
                gameScene.draw(batch);
                break;
        }
        batch.end();
    }
}
