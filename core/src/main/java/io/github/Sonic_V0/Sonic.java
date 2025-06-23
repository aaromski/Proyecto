package io.github.Sonic_V0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Sonic extends Personaje {

    public Sonic (Body body) {
        super(body);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Sonic";

    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteSonic/Sonicb.atlas"));
        sprite = atlas.createSprite("spritesonic0");
        sprite.setSize(40f / PPM, 49f / PPM); // ≈ 0.91 x 1.19
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );

        correr = new Animation<>(
            0.2f,
            atlas.createSprite("spritesonic0"),
            atlas.createSprite("spritesonic1"),
            atlas.createSprite("spritesonic2"),
            atlas.createSprite("spritesonic3"),
            atlas.createSprite("spritesonic4"),
            atlas.createSprite("spritesonic5"),
            atlas.createSprite("spritesonic6"));

        frameActual = new TextureRegion();
    }

    @Override
    protected void actualizar(float delta) {
        izq = false;
        der = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-velocidad, body.getLinearVelocity().y);
            izq = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(velocidad, body.getLinearVelocity().y);
            der = true;
        }
        super.actualizar(delta);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}

