package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Robot extends Enemigas{
    public Robot(Body b, Vector2 objetivo) {
        super(b);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.name = "Robot";
    }

    @Override
    public void inicializarAnimaciones(float x, float y){
        atlas = new TextureAtlas(Gdx.files.internal("Robot/robot.atlas"));
        sprite = atlas.createSprite("robot");
        sprite.setSize(30f / PPM, 39f / PPM); // ≈ 0.91 x 1.19
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("robotmove", 7, 0.09f);       // del 1 al 8
        KO = crearAnimacion("robot", 4, 0.1f);


    }
    @Override
    public void destruir() {
        if (!destruido) {
            destruido = true;
            stateTime = 0f;
            body.setLinearVelocity(0, 0);
            body.getWorld().destroyBody(body); // destruye físicamente
        }
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
