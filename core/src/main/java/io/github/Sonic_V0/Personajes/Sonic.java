package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;

/**
 * Clase que representa al personaje jugable Sonic en el juego.
 *
 * Hereda de {@link Amigas} e implementa animaciones, controles y una habilidad especial de velocidad temporal.
 *
 * @author Aarom Luces
 */
public class Sonic extends Amigas {

    /** Multiplicador de velocidad (aumenta al usar la habilidad). */
    private int mult = 1;

    /**
     * Constructor de Sonic.
     *
     * @param posicion Vector de posición inicial del personaje.
     * @param world Mundo físico de Box2D donde se integra el personaje.
     */
    public Sonic(Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Sonic";
    }

    /**
     * Inicializa las animaciones del personaje usando su TextureAtlas.
     *
     * @param x Posición horizontal de inicio.
     * @param y Posición vertical de inicio.
     */
    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteSonic/SonicSprite.atlas"));
        sprite = atlas.createSprite("SonicSprite0");
        sprite.setSize(30f / PPM, 39f / PPM);
        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);

        correr      = crearAnimacion("SonicSprite", 8, 0.09f);
        abajo       = crearAnimacion("abajo", 10, 0.1f);
        habilidad   = crearAnimacion("correr", 4, 0.1f);
        arriba      = crearAnimacion("arriba", 6, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 6, 0.1f);
        diagonalabj = crearAnimacion("Diagonalabj", 7, 0.1f);

        frameActual = new TextureRegion();
    }

    /**
     * Actualiza la lógica del personaje Sonic cada frame.
     * Incluye movimiento, control de habilidad de velocidad y animaciones.
     *
     * @param delta Tiempo en segundos desde el último frame.
     */
    @Override
    public void actualizar(float delta) {
        // Control de cooldown de la habilidad
        if (!usandoHabilidad && !puedeUsarHabilidad) {
            tiempoRestante += delta;
            if (tiempoRestante >= MAX_TIEMPO) {
                puedeUsarHabilidad = true;
                tiempoRestante = MAX_TIEMPO - 25;
            }
        }

        if (!ko) {
            izq = der = abj = arr = hab = presionando = false;

            // Movimiento básico
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

            // Activación de habilidad (super velocidad)
            if (Gdx.input.isKeyPressed(Input.Keys.F) && puedeUsarHabilidad) {
                mult = 2;
                hab = true;
                usandoHabilidad = true;
                tiempoRestante -= delta;
                if (tiempoRestante <= 0f) {
                    puedeUsarHabilidad = false;
                    usandoHabilidad = false;
                }
            } else {
                mult = 1;
                hab = false;
            }

            // Si no hay teclas presionadas, se detiene el personaje
            if (!presionando) {
                body.setLinearVelocity(0, 0);
                frameActual = sprite;
                stateTime = 0f;
            }

            super.actualizar(delta);
        }
    }

    /**
     * Configura el filtro de colisión para el cuerpo físico de Sonic.
     *
     * @param fdef Definición de fixture a modificar.
     */
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = -1;  // Colisiona con todo por defecto
    }

    /**
     * Libera recursos gráficos asociados a Sonic.
     */
    @Override
    public void dispose() {
        atlas.dispose();
    }
}

