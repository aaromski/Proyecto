package io.github.Sonic_V0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class Sonic extends Personaje {

    public Sonic (Body body) {
        super(body);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Sonic";

    }

    private Animation<TextureRegion> crearAnimacion(String baseNombre, int cantidadFrames, float duracion) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= cantidadFrames; i++) {
            frames.add(atlas.findRegion(baseNombre + i));
        }
        return new Animation<>(duracion, frames);
    }



    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteSonic/SonicSprite.atlas"));
        sprite = atlas.createSprite("SonicSprite0");
        sprite.setSize(30f / PPM, 39f / PPM); // ≈ 0.91 x 1.19
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );

        correr = crearAnimacion("SonicSprite", 8, 0.1f);       // del 1 al 8
        abajo = crearAnimacion("abajo", 10, 0.1f);
        arriba = crearAnimacion("arriba", 6, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 6, 0.1f);
        diagonalabj = crearAnimacion("Diagonalabj", 7, 0.1f);

        frameActual = new TextureRegion();
    }

    @Override
    protected void actualizar(float delta) {
        izq = der = abj = arr = false;
        boolean presionando = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(0, velocidad.y);
            arr = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.setLinearVelocity(0, -velocidad.y);
            abj = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-velocidad.x, body.getLinearVelocity().y);
            izq = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(velocidad.x, body.getLinearVelocity().y);
            der = true;
            presionando = true;
        }
        if (!presionando) {
            body.setLinearVelocity(0, 0);
            frameActual = sprite;
            stateTime = 0f;
        }
        super.actualizar(delta);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}

