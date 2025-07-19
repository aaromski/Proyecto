package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Enemigas extends Personaje {
    protected Animation<TextureRegion> KO;
    protected Body objetivo;

    public Enemigas(Body b) {
        super(b);
    }

    public void destruir() {
        ko = true;
        stateTime = 0f; // reiniciar animación KO
        body.setLinearVelocity(0, 0); // detener movimiento si aplica
    }

    public void setObjetivo(Body pos) {
        if(!ko) {
            this.objetivo = pos;
        }
    }

    @Override
    public void actualizar(float delta) {
        posicion = body.getPosition();
        if (ko) {
            body.setLinearVelocity(0, 0);
            objetivo = null;

            if (KO.isAnimationFinished(stateTime)) {
                frameActual = KO.getKeyFrame(KO.getAnimationDuration(), false); // ❄️ Último frame fijo
            } else {
                frameActual = KO.getKeyFrame(stateTime, false); // ▶️ Reproducción normal
                stateTime += delta;
            }
            return;
        }

        if (objetivo == null) {
            // Si no hay objetivo, quedarse quieto
            body.setLinearVelocity(0, 0);
            return;
        }
        stateTime += delta;
        if (objetivo.getPosition() != null && objetivo != body) {
            // Vector hacia el objetivo
            Vector2 direccion = objetivo.getPosition().cpy().sub(posicion).nor().scl(velocidad);
            body.setLinearVelocity(direccion);

            // Voltear sprite si el objetivo está a la izquierda o derecha
            boolean haciaIzq = direccion.x < 0;
            if (frameActual != null) {
                if (haciaIzq && !frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                } else if (!haciaIzq && frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            }
        }
        // Animación de movimiento
        frameActual = correr.getKeyFrame(stateTime, true);
    }

}
