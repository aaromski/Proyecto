package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Mundo;
import io.github.Sonic_V0.Nube;

public class Robotnik extends Enemigas {

    private final Mundo world;
    private final Etapa etapa1;
    private enum Fase { FASE1, FASE2, FASE3 }
    private Fase currentPhase = Fase.FASE1;
    private float habilidadTimer = 0;
    private int nubeCounter = 0;

    public Robotnik(Body b, Body objetivo, Mundo world, Etapa2 etapa, Etapa etapa1) {
        super(b);
        this.etapa1 = etapa1;
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.velocidad = new Vector2(2f, 2f);
        this.name = "Robotnik";
        this.world = world;
    }

    @Override
    public void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("Robotnik/Robotnik.atlas"));
        sprite = atlas.createSprite("robotnikSprite0");
        sprite.setSize(73f / PPM, 50f / PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = new Animation<>(0.5f,
            atlas.createSprite("dr-robotnik-46"),
            atlas.createSprite("dr-robotnik-49"),
            atlas.createSprite("dr-robotnik-48"),
            atlas.createSprite("dr-robotnik-44"),
            atlas.createSprite("dr-robotnik-48"),
            atlas.createSprite("dr-robotnik-49"));
    }

    @Override
    public void destruir() {
        if (!destruido) {
            destruido = true;
            stateTime = 0f;
            body.setLinearVelocity(0, 0);
            body.getWorld().destroyBody(body);
        }
    }

    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);

        habilidadTimer += delta;
        if (habilidadTimer >= 1.5f) {
            lanzarNube();
            habilidadTimer = 0;
        }
    }

    private void lanzarNube() {
        Vector2 direccion = objetivo.getPosition().cpy().sub(body.getPosition()).nor();
        world.generarNube(body.getPosition().cpy(), direccion);
        nubeCounter++;

        if (nubeCounter >= 5) {
            cambiarFase();

        }
    }

    private void cambiarFase() {
        if (currentPhase == Fase.FASE1) {
            currentPhase = Fase.FASE2;
            for (int i = 0; i < 5; i++) {
                world.robotEtapa2();
            }
        } else if (currentPhase == Fase.FASE2) {
            currentPhase = Fase.FASE3;
            world.generarCharco(body.getPosition());
        }else if (currentPhase == Fase.FASE3) {
            currentPhase = Fase.FASE1;
        }

        nubeCounter = 0;
        habilidadTimer = 0;
    }


    @Override
    public void dispose() {
        atlas.dispose();
    }
}
