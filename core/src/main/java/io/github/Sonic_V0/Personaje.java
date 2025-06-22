package io.github.Sonic_V0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Personaje {
    protected  boolean izq = false,  der = false, salta = false;  //Estados
    protected float velocidad = 10;  //200
    protected float stateTime = 0f;
    protected TextureRegion frameActual;
    protected Sprite sprite;
    protected Animation<Sprite> correr;
    protected Body body;
    public static final float PPM = 32f; // Pixels Per Meter
    protected TextureAtlas atlas;
    protected Vector2 posicion;
    protected String name;

    public Personaje (Body b) {
        this.body = b;
        this.posicion = body.getPosition();
    };


    abstract void inicializarAnimaciones(float x, float y);

    protected void actualizar(float delta) {
        stateTime += delta;

         if (izq || der) {
            stateTime += delta;
            frameActual = correr.getKeyFrame(stateTime, true);
        } else {
            frameActual = correr.getKeyFrame(0, false);
        }

        // Invertir el sprite si se mueve a la izquierda
        if (frameActual != null && izq && !frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }
        if (frameActual != null && der && frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }

    }

    public void render(SpriteBatch batch) {
        sprite.setRegion(frameActual);
        sprite.setPosition(
            posicion.x - sprite.getWidth() / 2f,
            posicion.y - sprite.getHeight() / 2f
        );
        sprite.draw(batch);
    }


    public abstract void dispose();
}

