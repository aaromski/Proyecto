package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Nube {
    private Body cuerpo;
    private final Sprite textura;
    private boolean activa = true;
    private final World world;

    public Nube(World world) {
        textura = new Sprite(new Texture("Mapa1/dr-robotnik-130.png"));
        textura.setSize(0.8f, 0.8f);
        this.world = world;
    }

    public void crearCuerpo(Vector2 posicion, Vector2 direccion) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(posicion);

        cuerpo = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.8f / 2, 0.8f / 2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.3f;

        fdef.filter.categoryBits = Constantes.CATEGORY_NUBE;
        fdef.filter.maskBits = (short) ~(Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH);

        cuerpo.createFixture(fdef).setUserData(this);
        shape.dispose();

        float fuerza = 3.0f;
        cuerpo.applyLinearImpulse(direccion.scl(fuerza), cuerpo.getWorldCenter(), true);
    }

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

    public void setActiva () {
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
