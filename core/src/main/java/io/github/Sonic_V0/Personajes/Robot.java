package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Mundo;

public class Robot extends Enemigas{
    private float tiempoBasura;
    private Mundo world;
    public Robot(Body b, Vector2 objetivo, Mundo world) {
        super(b);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.name = "Robot";
        this.world = world;
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
    public void actualizar(float delta) {
        super.actualizar(delta); // si el padre tiene lógica

        tiempoBasura += delta;
        if (tiempoBasura >= 20f) { // cada 3 segundos, por ejemplo
            Vector2 posicionActual = body.getPosition().cpy();
            world.generarBasura(posicionActual);
            tiempoBasura = 0f;
        }
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
