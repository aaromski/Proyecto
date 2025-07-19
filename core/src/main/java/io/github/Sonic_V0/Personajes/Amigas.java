package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Amigas extends Personaje {
    protected  boolean izq = false,  der = false, arr = false, abj = false;
    protected boolean golpeando = false;
    protected Animation<TextureRegion> abajo;
    protected Animation<TextureRegion> arriba;
    protected Animation<TextureRegion> diagonalarr;
    protected Animation<TextureRegion> diagonalabj;
    protected Animation<TextureRegion> golpe;
    protected boolean TLT = false;
    protected boolean ko = false;

    public Amigas(Body b) {
        super(b);
    }

    public void golpear() {
        if (!golpeando) {
            golpeando = true;
            stateTime = 0;
        }
    }

    protected void actualizar(float delta) {
        this.posicion = body.getPosition();

        if (golpeando || arr || abj || izq || der || (arr && (izq || der)) || (abj && (izq || der))) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        boolean diagonalArribaActiva = arr && (izq || der);
        boolean diagonalAbajoActiva = abj && (izq || der);

        if (golpeando) {
            frameActual = golpe.getKeyFrame(stateTime);
            if (golpe.isAnimationFinished(stateTime)) {
                golpeando = false;
            }
        } else if (diagonalArribaActiva) {
            frameActual = diagonalarr.getKeyFrame(stateTime, true);
        } else if (diagonalAbajoActiva) {
            frameActual = diagonalabj.getKeyFrame(stateTime, true);
        } else if (abj) {
            frameActual = abajo.getKeyFrame(stateTime, true);
        } else if (arr) {
            frameActual = arriba.getKeyFrame(stateTime, true);
        } else if (izq || der) {
            frameActual = correr.getKeyFrame(stateTime, true);
        } else {
            frameActual = sprite;
        }

        if (frameActual != null) {
            if (diagonalArribaActiva) {
                if (izq && !frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                } else if (der && frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            } else if (diagonalAbajoActiva) {
                if (der && !frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                } else if (izq && frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            } else {
                if (izq && !frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                } else if (der && frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            }
        }
    }

    public void destruir(World world) {
            if (!ko) {
                world.destroyBody(body);
                body = null;
            }
    }

    public boolean getKO() {
        return ko;
    }

    public void setKO() {
        ko = !ko;
    }

    public void teletransportar() {
        if(TLT) {
            body.setTransform(25, 22, body.getAngle());
            setTLT();
        }
    }

    public void setTLT() {
        TLT = !TLT;
    }
}
