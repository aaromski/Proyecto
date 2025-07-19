package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Basura {
    private Body cuerpo;
    private final Sprite textura;
    private boolean activa = true;
    private final World world;

    public Basura(World world) {
        textura = new Sprite(new Texture("Mapa1/trash.png"));
        textura.setSize(0.8f, 0.8f);
        this.world = world;
    }

    public void crearCuerpo(Vector2 posicion) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(posicion);

        cuerpo = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.8f / 2, 0.8f / 2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.3f;
        fdef.filter.categoryBits = Constantes.CATEGORY_TRASH;
        fdef.filter.maskBits = ~(Constantes.CATEGORY_ROBOT); // o una lista explícita sin incluir `TRASH`
        cuerpo.createFixture(fdef).setUserData(this);
        System.out.println("Número de fixtures en cuerpo basura: " + cuerpo.getFixtureList().size);
        shape.dispose();

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
