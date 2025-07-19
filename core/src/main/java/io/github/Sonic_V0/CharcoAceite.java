package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class CharcoAceite {
    private Body cuerpo;
    private final Sprite textura;
    private boolean activa = true;

    // El constructor recibe el cuerpo, que ya fue creado en la clase Mundo.
    public CharcoAceite(Body body) {
        this.cuerpo = body;
        textura = new Sprite(new Texture("Mapa1/oil.png"));
        textura.setSize(1.5f, 1.5f);
    }


    public void render(SpriteBatch batch) {
        Vector2 pos = cuerpo.getPosition();
        textura.setPosition(
            pos.x - textura.getWidth() / 2,
            pos.y - textura.getHeight() / 2
        );
        textura.draw(batch);
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void setActiva () {
        activa = false;
    }

    public void destruir(World world) {
        if (!activa) {
            world.destroyBody(cuerpo);
            cuerpo = null;
        }
    }

    public void dispose() {
        if (textura != null && textura.getTexture() != null) {
            textura.getTexture().dispose();
        }
    }
}
