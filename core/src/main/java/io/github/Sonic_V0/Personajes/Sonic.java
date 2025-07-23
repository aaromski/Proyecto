package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;

public class Sonic extends Amigas {
    private int mult = 1;
    public Sonic (Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Sonic";
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteSonic/SonicSprite.atlas"));
        sprite = atlas.createSprite("SonicSprite0");
        sprite.setSize(30f / PPM, 39f / PPM); // ≈ 0.91 x 1.19
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("SonicSprite", 8, 0.09f);       // del 1 al 8
        abajo = crearAnimacion("abajo", 10, 0.1f);
        habilidad = crearAnimacion("correr", 4, 0.1f);
        arriba = crearAnimacion("arriba", 6, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 6, 0.1f);
        diagonalabj = crearAnimacion("Diagonalabj", 7, 0.1f);

        frameActual = new TextureRegion();
    }

    @Override
    public void actualizar(float delta) {
        // Control de recarga
        if (!usandoHabilidad && !puedeUsarHabilidad) {
            tiempoRestante += delta;
            if (tiempoRestante >= MAX_TIEMPO) {
                puedeUsarHabilidad = true;
                tiempoRestante = MAX_TIEMPO - 25;
            }
        }


        if (!ko) {
            izq = der = abj = arr = hab = presionando = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                body.setLinearVelocity(0, velocidad.y * mult);
                arr = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                body.setLinearVelocity(0, -velocidad.y * mult);
                abj = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(-velocidad.x * mult, body.getLinearVelocity().y);
                izq = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(velocidad.x * mult, body.getLinearVelocity().y);
                der = true;
                presionando = true;
            }

            // ↓↓↓ Activar habilidad si presionas F y está lista ↓↓↓
            if (Gdx.input.isKeyPressed(Input.Keys.F) && puedeUsarHabilidad) {
                mult = 2;
                hab = true;
                usandoHabilidad = true;
                tiempoRestante -= delta;
                System.out.println(tiempoRestante);
                if (tiempoRestante <= 0f) {
                    puedeUsarHabilidad = false;
                    usandoHabilidad = false;
                }
            } else {
                mult = 1;
                hab = false;
            }


            if (!presionando) {
                body.setLinearVelocity(0, 0);
                frameActual = sprite;
                stateTime = 0f;
            }
            super.actualizar(delta);
        }
    }

    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = -1;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}

