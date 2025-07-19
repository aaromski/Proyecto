package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Mundo;

public class Robot extends Enemigas{
    private float tiempoBasura;
    private final Mundo world;
    private float deathTimer = 0f;

    public Robot(Body b, Vector2 objetivo, Mundo world) {
    public Robot(Body b, Body objetivo, Mundo world) {
        super(b);
        this.destruido = false;
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
        KO = crearAnimacion("robot", 4, 0.15f);


    }
    @Override
    public void destruir() {
        if (!ko) {
            ko = true;
            stateTime = 0f;
            body.setLinearVelocity(0, 0);
            body.getWorld().destroyBody(body); // destruye físicamente
        }
    }

    public boolean estaListoParaEliminar() {
        return destruido && deathTimer >= 1.0f;
    }

    @Override
    public void actualizar(float delta) {
        stateTime += delta;
        if(destruido){
            deathTimer += delta;
        } else {
            super.actualizar(delta);
            tiempoBasura += delta;
            if (tiempoBasura >= 10f) {
                Vector2 posicionActual = body.getPosition().cpy();
                world.generarBasura(posicionActual);
                tiempoBasura = 0f;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (body == null) {
            return;
        }

        if (destruido) {
            if (KO != null) {
                frameActual = KO.getKeyFrame(stateTime);
            }
        } else {
            if (correr != null) {
                frameActual = correr.getKeyFrame(stateTime, true);
            }
        }

        if (frameActual != null) {
            sprite.setRegion(frameActual);

            sprite.setPosition(
                body.getPosition().x - sprite.getWidth() / 2f,
                body.getPosition().y - sprite.getHeight() / 2f
            );

            sprite.draw(batch);
        }
    }


    @Override
    public void dispose() {
        atlas.dispose();
    }
}
