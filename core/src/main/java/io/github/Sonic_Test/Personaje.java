package io.github.Sonic_Test;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;


abstract public class Personaje {
    protected  boolean izq = false,  der = false;
    protected float velocidad = 10;  //200
    protected boolean salta = false;
    protected float stateTime = 0f;
    protected TextureRegion frameActual;
    protected Sprite sprite;
    protected Animation<Sprite> correr;
    protected  Animation<Sprite> saltar;
    protected Body body;
    public static final float PPM = 32f; // Pixels Per Meter (ejemplo)
    protected TextureAtlas atlas;
    protected  Vector2 posicion;

    public Personaje (Body b) {
        this.body = b;
        this.posicion = body.getPosition();
    };


    abstract void inicializarAnimaciones(float x, float y);

    protected void actualizar(float delta) {
        stateTime += delta;

        // SALTO FÍSICO
        if (Gdx.input.isKeyPressed(Input.Keys.W) && !salta) {
            body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
            salta = true;
            stateTime = 0f;
        }

// ANIMACIÓN SEGÚN ESTADO
        if (salta) {
            stateTime += delta;
            frameActual = saltar.getKeyFrame(stateTime, false);

            // Si terminó el salto, volvemos a caminar
            if (saltar.isAnimationFinished(stateTime)) {
                salta = false;
                stateTime = 0f;
            }
        } else if (izq || der) {
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
        if (body.getLinearVelocity().y == 0) {
            salta = false;
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
