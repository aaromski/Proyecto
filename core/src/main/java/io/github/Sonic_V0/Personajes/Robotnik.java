package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Mundo.Etapa;
import io.github.Sonic_V0.Mundo.Etapa2;
import io.github.Sonic_V0.Mundo.Mundo;

/**
 * Clase que representa al personaje Robotnik como un enemigo jefe con comportamiento por fases.
 * Genera nubes tóxicas, cambia de fases y realiza acciones según un temporizador interno.
 * Robotnik es un personaje hostil con comportamiento en fases: puede lanzar nubes tóxicas,
 * invocar refuerzos (robots secundarios), y generar charcos que afectan el entorno.
 * Se autodestruye tras cierto tiempo y pasa por ciclos de combate estratégicamente definidos.
 *
 * @author Miguel Carreño
 */
public class Robotnik extends Enemigas {

    /** Referencia al mundo físico del juego. */
    private final Mundo world;

    /** Referencia a la primera etapa del juego. */
    private final Etapa etapa1;

    /**
     * Enum que define las fases del comportamiento del jefe.
     * - FASE1: Genera nubes y prepara cambio.
     * - FASE2: Invoca otros enemigos (robots).
     * - FASE3: Genera charcos en el mapa.
     */
    private enum Fase { FASE1, FASE2, FASE3 }

    /** Fase actual del Robotnik. */
    private Fase currentPhase = Fase.FASE1;

    /** Temporizador para controlar el uso de habilidad especial. */
    private float habilidadTimer = 0;

    /** Contador de nubes generadas en la fase actual. */
    private int nubeCounter = 0;

    /** Temporizador para determinar tiempo total activo del Robotnik. */
    private float activoTimer = 0;

    /** Tiempo máximo permitido antes de que se autodestruya. */
    private final float MAX_ACTIVO_TIME = 30f;

    /** Indica si Robotnik está listo para ser removido del mundo. */
    private boolean listoDespawn = false;

    /**
     * Constructor del Robotnik.
     *
     * @param objetivo Cuerpo Box2D del jugador u objetivo.
     * @param world Mundo físico y lógico donde se encuentra el jefe.
     * @param etapa Instancia de la Etapa2 del juego.
     * @param etapa1 Instancia de la Etapa1, usada para lógica específica.
     * @param posicion Posición inicial del jefe en el mundo.
     */
    public Robotnik(Body objetivo, Mundo world, Etapa2 etapa, Etapa etapa1, Vector2 posicion) {
        super(posicion, world.getWorld());
        this.etapa1 = etapa1;
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.velocidad = new Vector2(2f, 2f);
        this.name = "Robotnik";
        this.world = world;
    }

    /**
     * Inicializa las animaciones del sprite del jefe utilizando un atlas de texturas.
     *
     * @param x Coordenada X en el mundo.
     * @param y Coordenada Y en el mundo.
     */
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

    /**
     * Configura el filtro de colisiones del fixture del cuerpo del jefe.
     * Define las categorías que puede colisionar y cuáles debe ignorar.
     *
     * @param fdef Definición de fixture para configurar colisiones.
     */
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
        fdef.filter.maskBits = (short) ~(Constantes.CATEGORY_TRASH | Constantes.CATEGORY_NUBE);
    }

    /**
     * Marca a Robotnik como destruido y listo para ser eliminado del mundo.
     * Detiene su movimiento y reinicia su estado de animación.
     */
    @Override
    public void destruir() {
        if (!destruido) {
            destruido = true;
            stateTime = 0f;
            body.setLinearVelocity(0, 0);
            listoDespawn = true;
        }
    }

    /**
     * Actualiza el estado del jefe en cada ciclo del juego.
     * Maneja el temporizador de actividad y generación de habilidades.
     *
     * @param delta Tiempo transcurrido desde el último frame (en segundos).
     */
    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);

        activoTimer += delta;

        if (activoTimer >= MAX_ACTIVO_TIME) {
            destruir();
        } else {
            habilidadTimer += delta;
            if (habilidadTimer >= 1.5f) {
                lanzarNube();
                habilidadTimer = 0;
            }
        }
    }

    /**
     * Lanza una nube en dirección al objetivo si el jefe aún está activo.
     * Después de lanzar 5 nubes, inicia el cambio de fase.
     */
    private void lanzarNube() {
        if (!listoDespawn && objetivo != null) {
            Vector2 direccion = objetivo.getPosition().cpy().sub(body.getPosition()).nor();
            world.generarNube(body.getPosition().cpy(), direccion);
            nubeCounter++;

            if (nubeCounter >= 5) {
                cambiarFase();
            }
        }
    }

    /**
     * Realiza el cambio de fase del Robotnik según su estado actual.
     * Cada fase tiene acciones específicas, como invocar enemigos o generar obstáculos.
     */
    private void cambiarFase() {
        if (currentPhase == Fase.FASE1) {
            currentPhase = Fase.FASE2;
            for (int i = 0; i < 5; i++) {
                world.robotEtapa2();
            }
        } else if (currentPhase == Fase.FASE2) {
            currentPhase = Fase.FASE3;
            world.generarCharco(body.getPosition());
        } else if (currentPhase == Fase.FASE3) {
            currentPhase = Fase.FASE1;
        }

        nubeCounter = 0;
        habilidadTimer = 0;
    }

    /**
     * Verifica si el jefe está listo para desaparecer del juego.
     *
     * @return true si debe ser removido, false en caso contrario.
     */
    public boolean isListoDespawn() {
        return listoDespawn;
    }

    /**
     * Libera recursos gráficos asociados al atlas de texturas del jefe.
     */
    @Override
    public void dispose() {
        atlas.dispose();
    }
}
