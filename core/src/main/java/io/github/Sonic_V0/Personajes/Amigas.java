package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Amigas extends Personaje {
    protected  boolean izq = false,  der = false, arr = false, abj = false, hab = false, presionando = false;
    protected Animation<TextureRegion> abajo;
    protected Animation<TextureRegion> arriba;
    protected Animation<TextureRegion> diagonalarr;
    protected Animation<TextureRegion> diagonalabj;
    protected Animation<TextureRegion> habilidad;
    protected boolean TLT = false;
    protected float tiempoRestante = 5f;
    protected boolean usandoHabilidad = false;
    protected boolean puedeUsarHabilidad = true;
    protected final float MAX_TIEMPO = 30f;
    private boolean invulnerable = false;
    private float tiempoInvulnerable = 0f;

    public Amigas(Vector2 p, World w) {
        super(p,w); // Llama al constructor de Personaje que inicializa 'this.world'
    }

    @Override
    protected void actualizar(float delta) {
        this.posicion = body.getPosition(); // actualiza posición
        if (invulnerable) {
            tiempoInvulnerable -= delta;
            if (tiempoInvulnerable <= 0f) {
                invulnerable = false;
            }
        }
        boolean diagonalArriba = arr && (izq || der);
        boolean diagonalAbajo = abj && (izq || der);

        if (diagonalArriba || diagonalAbajo || izq || der || abj || arr || hab) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        if (hab && presionando) {
            frameActual = habilidad.getKeyFrame(stateTime, true);

            if (der && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (izq && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }

        } else if (diagonalArriba) {
            frameActual = diagonalarr.getKeyFrame(stateTime, true);
            if (this instanceof Sonic) {
                if (der && !frameActual.isFlipX()) frameActual.flip(true, false);
                if (izq && frameActual.isFlipX()) frameActual.flip(true, false);
            } else {
                if (der && frameActual.isFlipX()) frameActual.flip(true, false);
                if (izq && !frameActual.isFlipX()) frameActual.flip(true, false);
            }

        } else if (diagonalAbajo) {
            frameActual = diagonalabj.getKeyFrame(stateTime, true);
            if (this instanceof Sonic || this instanceof Tails) {
                if (izq && !frameActual.isFlipX()) frameActual.flip(true, false);
                if (der && frameActual.isFlipX()) frameActual.flip(true, false);
            } else {
                if (izq && frameActual.isFlipX()) frameActual.flip(true, false);
                if (der && !frameActual.isFlipX()) frameActual.flip(true, false);
            }

        } else if (abj) {
            frameActual = abajo.getKeyFrame(stateTime, true);
        } else if (arr) {
            frameActual = arriba.getKeyFrame(stateTime, true);
        } else if (izq || der) {
            frameActual = correr.getKeyFrame(stateTime, true);
        } else {
            frameActual = sprite;
        }

        if (!diagonalArriba && !diagonalAbajo) {
            if (izq && frameActual != null && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (der && frameActual != null && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }
        }
    }


    public void activarInvulnerabilidad(float duracion) {
        invulnerable = true;
        tiempoInvulnerable = duracion;
    }

    public boolean esInvulnerable() {
        return invulnerable;
    }


    public void teletransportar() {
        if(TLT && body != null) {
            body.setTransform(25, 22, body.getAngle());
            activarInvulnerabilidad(3f);
            setTLT();
        }
    }

    public void setTLT() {
        TLT = !TLT;
    }
}
