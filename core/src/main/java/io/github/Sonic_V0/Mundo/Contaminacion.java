package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;

public abstract class Contaminacion {
    protected Body cuerpo;
    protected Sprite textura;
    protected boolean activa = true;
    protected final World world;

    public Contaminacion(World world) {
        this.world = world;
    }

    public void crearCuerpo(Vector2 posicion) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.4f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.3f;

        BodyDef bdef = new BodyDef();
        if (this instanceof Nube) {
            bdef.type = BodyDef.BodyType.DynamicBody;
        } else {
            bdef.type = BodyDef.BodyType.StaticBody;
        }

        bdef.position.set(posicion);

        cuerpo = world.createBody(bdef);

        configurarFiltro(fdef);

        cuerpo.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    abstract void configurarFiltro(FixtureDef fdef);

    public void render(SpriteBatch batch) {
        if (!activa) return;

        Vector2 pos = cuerpo.getPosition();
        textura.setPosition(
            pos.x - textura.getWidth() / 2,
            pos.y - textura.getHeight() / 2
        );

        textura.draw(batch);
    }

    // <-- ¡MODIFICADO! Ya no recibe el parámetro 'World'.
    // Utiliza 'this.world' (el campo de la clase).
    public void destruir() {
        // Solo destruye el cuerpo si está activo y el cuerpo no es nulo
        if (cuerpo != null && activa) {
            this.world.destroyBody(cuerpo); // Usa 'this.world'
            cuerpo = null;
            activa = false; // Asegura que se desactiva después de destruir el cuerpo
        }
    }

    public boolean estaActiva() {
        return activa;
    }

    public void setActiva (int op) {
        if (activa) { // Solo si está activo puede sumar puntos y desactivarse
            if (op == 1) {
                Constantes.SCORE[0] += 5;
            }
            activa = false; // Desactiva la contaminación
        }
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public void dispose() {
        if (textura != null && textura.getTexture() != null) {
            textura.getTexture().dispose();
        }
        // Nota: El cuerpo se destruye en el método destruir(), no aquí.
        // Si necesitas asegurarte de que el cuerpo se destruya si dispose() es llamado
        // sin que destruir() haya sido llamado, podrías añadir:
        // if (cuerpo != null) { world.destroyBody(cuerpo); cuerpo = null; }
        // Pero idealmente, llamar a destruir() antes de dispose() es lo correcto.
    }
}
