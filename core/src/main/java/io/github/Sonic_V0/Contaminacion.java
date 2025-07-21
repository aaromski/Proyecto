package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Contaminacion {
    protected Body cuerpo;
    protected Sprite textura;
    protected boolean activa = true;
    protected final World world;
    protected PolygonShape shape;
    protected FixtureDef fdef = new FixtureDef();

    public Contaminacion(World world) {
        this.world = world;

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(0.4f, 0.4f);
        this.shape = poly;

        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.3f;

    }

    public void crearCuerpo(Vector2 posicion) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(posicion);

        cuerpo = world.createBody(bdef);

        configurarFiltro(fdef); // Clase hija puede personalizar esto

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
