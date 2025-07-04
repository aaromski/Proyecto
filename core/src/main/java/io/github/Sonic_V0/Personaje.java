package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Personaje {
    protected  boolean izq = false,  der = false, arr = false, abj = false;  //Estados
    protected Vector2 velocidad;  //200
    protected float stateTime = 0f;
    protected TextureRegion frameActual;
    protected Sprite sprite;
    protected Animation<TextureRegion> correr;
    protected Animation<TextureRegion> abajo;
    protected Animation<TextureRegion> arriba;
    protected Animation<TextureRegion> diagonalarr;
    protected Animation<TextureRegion> diagonalabj;
    protected Body body;
    public static final float PPM = 32f; // Pixels Per Meter
    protected TextureAtlas atlas;
    protected Vector2 posicion;
    protected String name;

    public Personaje (Body b) {
        this.body = b;
        this.posicion = body.getPosition();
        velocidad = new Vector2(5f, 2f);
    }


    abstract void inicializarAnimaciones(float x, float y);

    protected void actualizar(float delta) {
        this.posicion = body.getPosition(); // actualiza posición
        boolean diagonalArriba = arr && (izq || der);
        boolean diagonalAbajo = abj && (izq || der);

        // solo sumar stateTime si se anima
        if (diagonalArriba || diagonalAbajo || izq || der || abj || arr) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        // Lógica de animaciones
        if (diagonalArriba) {
            frameActual = diagonalarr.getKeyFrame(stateTime, true);
            if (der && !frameActual.isFlipX()) frameActual.flip(true, false); // invertir para mirar derecha
            if (izq && frameActual.isFlipX()) frameActual.flip(true, false); // restaurar si está invertido
        } else if (diagonalAbajo) {
            frameActual = diagonalabj.getKeyFrame(stateTime, true);
            if (izq && !frameActual.isFlipX()) frameActual.flip(true, false); // invertir para mirar izquierda
            if (der && frameActual.isFlipX()) frameActual.flip(true, false); // restaurar si está invertido
        } else if (abj) {
            frameActual = abajo.getKeyFrame(stateTime, true);
        } else if (arr) {
            frameActual = arriba.getKeyFrame(stateTime, true);
        } else if (izq || der) {
            frameActual = correr.getKeyFrame(stateTime, true);
        } else {
            frameActual = sprite;
        }

        // Flip adicional por si hay inversión en otras direcciones
        if (!diagonalArriba && !diagonalAbajo) {
            if (izq && frameActual != null && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (der && frameActual != null && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }
        }

    }

    public void render(SpriteBatch batch) {
        // Si estás usando solo TextureRegion:
        batch.draw(
            frameActual,
            posicion.x - sprite.getWidth() / 2f,
            posicion.y - sprite.getHeight() / 2f,
            sprite.getWidth(),
            sprite.getHeight()
        );
    }


    public abstract void dispose();
}

