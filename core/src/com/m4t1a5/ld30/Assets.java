package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Matias on 24.8.2014.
 */
public class Assets
{
    public static Texture planetTexture, planetOutlineTexture;
    public static Texture lineTexture, lineErrorTexture;

    public static BitmapFont font20;

    private static boolean assetsLoaded = false;

    public static void loadAssets()
    {
        if(assetsLoaded)
            return;

        planetTexture = new Texture(Gdx.files.internal("planet.png"));
        planetOutlineTexture = new Texture(Gdx.files.internal("planet_outline.png"));
        lineTexture = new Texture(Gdx.files.internal("line_piece.png"));
        lineErrorTexture = new Texture(Gdx.files.internal("line_piece_error.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("kenpixel_future.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        // No need to generate more characters because i will never use them
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890:;";
        font20 = generator.generateFont(parameter);
        generator.dispose();

        assetsLoaded = true;
    }

    public static void dispose()
    {
        if(!assetsLoaded)
            return;

        planetOutlineTexture.dispose();
        planetTexture.dispose();
        lineErrorTexture.dispose();
        lineTexture.dispose();

        font20.dispose();

        assetsLoaded = false;
    }
}
