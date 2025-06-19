package io.github.Sonic_Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Prueba {
    static Sprite duck;
    static Sprite fall;
    static Sprite idle;
    static Sprite jump;

    static Animation<Sprite> walk;
    static TextureAtlas atlas;

    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("NuevoSprite/Sonicb.atlas"));

       /* duck = atlas.createSprite("character_robot_duck");
        fall = atlas.createSprite("character_robot_fall");
        idle = atlas.createSprite("spritesonic0");
        jump = atlas.createSprite("character_robot_jump");
        */


       /* walk = new Animation<>(
            SonicPrueba.WALK_FRAME_DURATION,
            atlas.createSprite("spritesonic0"),
            atlas.createSprite("spritesonic1"),
            atlas.createSprite("spritesonic2"),
            atlas.createSprite("spritesonic3"),
            atlas.createSprite("spritesonic4"),
            atlas.createSprite("spritesonic5"),
            atlas.createSprite("spritesonic6"));*/
    }

    public static void dispose() {
        atlas.dispose();
    }
}
