package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Matias on 24.8.2014.
 */
public class MenuScene implements Scene
{
    private Stage stage;
    private Skin skin;

    private Viewport viewport;

    public MenuScene(Viewport viewport)
    {
        this.viewport = viewport;
    }

    @Override
    public void create()
    {
        stage = new Stage(viewport);

        skin = new Skin();
        skin.add("buttonUp", Assets.buttonUp);
        skin.add("buttonDown", Assets.buttonDown);
        skin.add("default", Assets.font20);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        // Up and down were opposite form what i thought
        style.up = skin.newDrawable("buttonDown");
        style.down = skin.newDrawable("buttonUp");
        style.font = skin.getFont("default");
        skin.add("default", style);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        final TextButton startButton = new TextButton("Start", skin);
        startButton.setCenterPosition(Settings.WORLD_WIDTH/2, Settings.WORLD_HEIGHT/2);
        startButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                System.out.println("Button is " + startButton.isChecked());
                ((LD30Game) Gdx.app.getApplicationListener()).changeState(GameState.Play);
            }
        });
        table.add(startButton);

        setupInput();
    }

    @Override
    public void update()
    {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        stage.draw();
    }

    @Override
    public void dispose()
    {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void setupInput()
    {
        Gdx.input.setInputProcessor(stage);
    }
}
