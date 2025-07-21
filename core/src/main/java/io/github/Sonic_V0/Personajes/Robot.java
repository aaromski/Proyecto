package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Mundo.Mundo;

public class Robot extends Enemigas{
    private float tiempoBasura;
    private final Mundo world;
    private float deathTimer = 0f;

    public Robot(Vector2 posicion, Body objetivo, Mundo world) {
        super(posicion, world.getWorld());
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
        super.actualizar(delta); // si el padre tiene lógica
        if (ko) {return;}
        tiempoBasura += delta;
        if (tiempoBasura >= 30f) { // cada 3 segundos, por ejemplo
            Vector2 posicionActual = body.getPosition().cpy();
            world.generarBasura(posicionActual);
            tiempoBasura = 0f;
        }
    }

    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
        fdef.filter.maskBits = (short) ~(Constantes.CATEGORY_TRASH | Constantes.CATEGORY_NUBE);
    }




    @Override
    public void dispose() {
        atlas.dispose();
    }
}
