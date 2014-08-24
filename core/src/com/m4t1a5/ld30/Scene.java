package com.m4t1a5.ld30;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Matias on 24.8.2014.
 */
public interface Scene extends Disposable
{
    public void create();

    public void update();
    public void draw(SpriteBatch batch);
}
