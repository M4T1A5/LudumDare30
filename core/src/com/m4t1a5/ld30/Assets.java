package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Matias on 24.8.2014.
 */
public class Assets
{
    public static Texture gameBackground, menuBackground;

    public static Texture planetTexture, planetOutlineTexture;
    public static Texture lineTexture, lineErrorTexture;

    public static Texture buttonUp, buttonDown, buttonDisabled;
    public static Texture cursor, textField;

    public static BitmapFont font20, fontSmall;

    public static Sound planetHit;
    public static Music backgroundMusic;

    private static boolean assetsLoaded = false;

    public static void loadAssets()
    {
        if(assetsLoaded)
            return;

        menuBackground = new Texture(Gdx.files.internal("menu_background.png"));
        gameBackground = new Texture(Gdx.files.internal("background.png"));

        planetTexture = new Texture(Gdx.files.internal("planet.png"));
        planetOutlineTexture = new Texture(Gdx.files.internal("planet_outline.png"));
        lineTexture = new Texture(Gdx.files.internal("line_piece.png"));
        lineErrorTexture = new Texture(Gdx.files.internal("line_piece_error.png"));

        buttonDown = new Texture(Gdx.files.internal("buttonUp.png"));
        buttonUp = new Texture(Gdx.files.internal("buttonDown.png"));
        buttonDisabled = new Texture(Gdx.files.internal("buttonDisabled.png"));

        cursor = new Texture(Gdx.files.internal("cursor.png"));
        textField = new Texture(Gdx.files.internal("textField.png"));

        planetHit = Gdx.audio.newSound(Gdx.files.internal("hitPlanet.wav"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("kenpixel_future.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        // No need to generate more characters because i will never use them
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890:;.,+-*/";
        font20 = generator.generateFont(parameter);
        parameter.size = 16;
        fontSmall = generator.generateFont(parameter);
        generator.dispose();

        assetsLoaded = true;
    }

    public static void dispose()
    {
        if(!assetsLoaded)
            return;

        menuBackground.dispose();
        gameBackground.dispose();

        planetOutlineTexture.dispose();
        planetTexture.dispose();

        lineErrorTexture.dispose();
        lineTexture.dispose();

        buttonUp.dispose();
        buttonDown.dispose();
        buttonDisabled.dispose();

        cursor.dispose();
        textField.dispose();

        planetHit.dispose();
        backgroundMusic.dispose();

        font20.dispose();

        assetsLoaded = false;
    }
}
