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

        configurarFiltro(fdef); // sigue pasando como argumento

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

    public void destruir(World world) {
        if (!activa) {
            world.destroyBody(cuerpo);
            cuerpo = null;
        }
    }

    public boolean estaActiva() {
        return activa;
    }

    public void setActiva (int op) {
        if (activa) {
            switch (op) {
                case 1:
                    Constantes.SCORE[0] += 5;
                    break;
                case 2:
                    Constantes.SCORE[1] += 5;
                    break;
                case 3:
                    Constantes.SCORE[2] += 5;
                    break;
                default: break;
            }
        }
        activa = false;
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public void dispose() {
        if (textura != null && textura.getTexture() != null) {
            textura.getTexture().dispose();
        }
    }
}
