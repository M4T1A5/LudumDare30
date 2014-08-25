package com.m4t1a5.ld30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Matias on 25.8.2014.
 */
public class HighScore
{
    static public class Score
    {
        public Score()
        {}

        public Score(String name, int score)
        {
            this.name = name;
            this.score = score;
        }

        public String name;
        public int score;
    }

    public ArrayList<Score> scores;

    private Json json;

    public HighScore()
    {
        json = new Json();
        loadHighScore();
    }

    public void addScore(String name, int score)
    {
        scores.add(new Score(name, score));
        sort();
        saveHighScore();
    }

    private void sort()
    {
        Collections.sort(scores, new Comparator<Score>()
        {
            @Override
            public int compare(Score o1, Score o2)
            {
                if(o1.score > o2.score)
                    return -1;

                return 1;
            }
        });

        if(scores.size() > Settings.SCORE_LIST_LENGTH)
        {
            for (int i = Settings.SCORE_LIST_LENGTH; i < scores.size(); i++)
            {
                scores.remove(i);
            }
        }
    }

    private void saveHighScore()
    {
        System.out.println(Gdx.files.isLocalStorageAvailable());

        FileHandle file = Gdx.files.local(Settings.SCORE_FILE);
        file.writeString(json.toJson(scores, ArrayList.class), false);
    }

    private void loadHighScore()
    {
        FileHandle file = Gdx.files.local(Settings.SCORE_FILE);
        if(file.exists())
            scores = json.fromJson(ArrayList.class, file.readString());
        else
            scores = new ArrayList<Score>();
    }
}
