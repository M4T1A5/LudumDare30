package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Created by Matias on 24.8.2014.
 */
public class MenuScene implements Scene
{
    private Stage stage;
    private Skin skin;

    private Window highScoreList;

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
        // Add widget styles to skin
        setupStyles();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        root.background(skin.newDrawable("background"));

        highScoreList = new Window("High Score", skin);
        refreshHighScores();
        highScoreList.padRight(20);
        root.add(highScoreList).expandX().right();
        root.row();

        Table nameInput = new Table();
        Label label = new Label("Enter Name:", skin);
        nameInput.add(label);
        final TextField textField = new TextField("", skin);
        nameInput.add(textField).padLeft(5).width(256);
        root.add(nameInput).left().pad(0, 20, 10, 0);
        root.row();


        final TextButton startButton = new TextButton("Start", skin);
        //startButton.setCenterPosition(Settings.WORLD_WIDTH/2, Settings.WORLD_HEIGHT/2);
        startButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                LD30Game game = ((LD30Game) Gdx.app.getApplicationListener());
                game.playerName = textField.getText();
                game.changeState(GameState.Play);
            }
        });
        root.add(startButton).left().padLeft((nameInput.getPrefWidth() - startButton.getPrefWidth())/2);
        startButton.setDisabled(true);

        textField.setTextFieldListener(new TextField.TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char c)
            {
                startButton.setDisabled(false);
            }
        });

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

    public void refreshHighScores()
    {
        highScoreList.clear();
        ArrayList<HighScore.Score> scores = ((LD30Game) Gdx.app.getApplicationListener()).highScore.scores;

        for (int i = 0; i < scores.size(); i++)
        {
            String number = i+1+". ";

            if(i == 0)
            {
                highScoreList.add(number+scores.get(i).name, "small", Color.WHITE).
                        pad(15, 0, 0, 10).left();
                highScoreList.add(String.format("%d", scores.get(i).score), "small", Color.WHITE).padTop(15).right();
            }
            else
            {
                highScoreList.add(number+scores.get(i).name, "small", Color.WHITE).
                        pad(5, 0, 0, 10).left();
                highScoreList.add(String.format("%d", scores.get(i).score), "small", Color.WHITE).padTop(5).right();
            }
            highScoreList.row();
        }
    }

    private void setupStyles()
    {
        skin.add("background", Assets.menuBackground);
        skin.add("buttonUp", Assets.buttonUp);
        skin.add("buttonDown", Assets.buttonDown);
        skin.add("buttonDisabled", Assets.buttonDisabled);
        skin.add("cursor", Assets.cursor);
        skin.add("textField", Assets.textField);
        skin.add("default", Assets.font20);
        skin.add("small", Assets.fontSmall);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        // Up and down were opposite form what i thought
        style.up = skin.newDrawable("buttonDown");
        style.down = skin.newDrawable("buttonUp");
        style.fontColor = Color.WHITE;
        style.disabledFontColor = Color.GRAY;
        style.disabled = skin.newDrawable("buttonDisabled");
        style.font = skin.getFont("default");
        skin.add("default", style);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("small");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = skin.newDrawable("cursor");
        textFieldStyle.background = skin.newDrawable("textField");
        skin.add("default", textFieldStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle(Assets.font20, Color.WHITE);
        skin.add("default", labelStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = Assets.font20;
        windowStyle.titleFontColor = Color.WHITE;
        skin.add("default", windowStyle);
    }
}
