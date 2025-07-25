package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Mundo.Mundo;

import java.util.List;

/**
 * Clase que representa un enemigo Robot en el juego.
 * <p>
 * El Robot es un personaje enemigo que puede moverse, generar basura y ser derrotado.
 * Cuando es derrotado, se convierte en chatarra por un tiempo antes de reactivarse o ser eliminado.
 *
 * @author Jesus
 * @version 1.0
 * @see Enemigas
 * @see Mundo
 * @see Constantes
 */
public class Robot extends Enemigas {
    /** Tiempo acumulado para generar basura */
    private float tiempoBasura;
    /** Referencia al mundo del juego */
    private final Mundo world;

    /** Sprite que representa al robot como chatarra */
    protected TextureRegion chatarraSprite;
    /** Tiempo que el robot ha estado en estado de chatarra */
    private float tiempoEnChatarra = 0f;
    /** Duración máxima del estado de chatarra */
    private final float DURACION_CHATARRA = 30f;

    /**
     * Constructor de la clase Robot.
     *
     * @param posicion Posición inicial del robot en el mundo
     * @param objetivo Cuerpo físico del objetivo al que perseguirá el robot
     * @param world Referencia al mundo del juego
     */
    public Robot(Vector2 posicion, Body objetivo, Mundo world) {
        super(posicion, world.getWorld());
        this.destruido = false;
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.name = "Robot";
        this.world = world;
    }

    /**
     * Inicializa las animaciones y sprites del robot.
     *
     * @param x Posición x inicial para el sprite
     * @param y Posición y inicial para el sprite
     */
    @Override
    public void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("Robot/robot.atlas"));
        sprite = atlas.createSprite("robot");
        sprite.setSize(30f / PPM, 39f / PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("robotmove", 7, 0.09f);
        KO = crearAnimacion("robot", 4, 0.15f);

        chatarraSprite = atlas.createSprite("robotKO");
        if(chatarraSprite == null) {
            chatarraSprite = KO.getKeyFrame(KO.getAnimationDuration(), true);
        }
    }

    /**
     * Selecciona el objetivo más cercano de una lista de objetivos posibles.
     *
     * @param posibles Lista de objetivos potenciales (personajes amigables)
     */
    public void seleccionarObjetivoMasCercano(List<Amigas> posibles) {
        if (posibles == null || posibles.isEmpty()) return;

        if (posibles.size() == 1) {
            setObjetivo(posibles.get(0).getCuerpo());
            return;
        }

        Amigas masCercano = null;
        float distanciaMin = Float.MAX_VALUE;

        for (Amigas objetivo : posibles) {
            float distancia = objetivo.getCuerpo().getPosition().dst2(body.getPosition());
            if (distancia < distanciaMin) {
                distanciaMin = distancia;
                masCercano = objetivo;
            }
        }

        if (masCercano != null) {
            setObjetivo(masCercano.getCuerpo());
        }
    }

    /**
     * Pone al robot en estado KO (derrotado).
     * Cambia sus filtros de colisión para convertirlo en chatarra.
     */
    @Override
    public void setKO() {
        if (!ko) {
            ko = true;
            stateTime = 0f;
            tiempoEnChatarra = 0f;
            if (body != null) {
                body.setLinearVelocity(0, 0);

                for (Fixture fixture : body.getFixtureList()) {
                    Filter filter = fixture.getFilterData();
                    filter.categoryBits = Constantes.CATEGORY_OBJETOS;
                    filter.maskBits = -1;
                    fixture.setFilterData(filter);
                }
            }
        }
    }

    /**
     * Destruye el cuerpo físico del robot.
     * Marca el robot como destruido para su posterior eliminación.
     */
    @Override
    public void destruir() {
        if (!destruido) {
            destruido = true;
            if (body != null) {
                body.getWorld().destroyBody(body);
                body = null;
            }
        }
    }

    /**
     * Verifica si el objeto ha sido marcado como destruido.
     *
     * @return true si el objeto está destruido, false si no.
     */
    public boolean getDestruido() {
        return destruido;
    }

    /**
     * Marca el objeto como destruido.
     * Cambia el estado interno a destruido.
     */
    public void setDestruido() {
        destruido = true;
    }

    /**
     * Reactiva el robot después de estar en estado KO.
     */
    public void reactivacion() {
        if (ko) {
            ko = false;
            tiempoEnChatarra = 0f;
        }
    }

    /**
     * Verifica si el robot está listo para ser eliminado del juego.
     *
     * @return true si el robot está destruido y ha cumplido su tiempo como chatarra,
     *         false en caso contrario
     */
    public boolean estaListoParaEliminar() {
        return destruido && tiempoEnChatarra >= DURACION_CHATARRA;
    }

    /**
     * Actualiza el estado del robot cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame
     */
    @Override
    public void actualizar(float delta) {
        if (ko) {
            tiempoEnChatarra += delta;
            stateTime += delta;

            // Actualiza la posición del sprite a la posición actual del body
            if (body != null) {
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f,
                    body.getPosition().y - sprite.getHeight() / 2f);
            }

            if (tiempoEnChatarra >= DURACION_CHATARRA) {
                if (!destruido) {
                    reactivacion();
                }
            }
            return;
        }

        super.actualizar(delta);
        tiempoBasura += delta;
        if (tiempoBasura >= 30f) {
            Vector2 posicionActual = body.getPosition().cpy();
            world.generarBasura(posicionActual);
            tiempoBasura = 0f;
        }
    }

    /**
     * Dibuja el robot en pantalla.
     *
     * @param batch SpriteBatch utilizado para dibujar
     */
    @Override
    public void render(SpriteBatch batch) {
        if (ko) {
            if (KO != null && !KO.isAnimationFinished(stateTime)) {
                batch.draw(KO.getKeyFrame(stateTime), sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
            } else if (chatarraSprite != null) {
                batch.draw(chatarraSprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
            }
        } else {
            super.render(batch);
        }
    }

    /**
     * Crea el cuerpo físico del robot en el mundo Box2D.
     *
     * @param posicion Posición inicial del cuerpo
     * @param world Mundo físico Box2D
     */
    @Override
    public void crearCuerpo(Vector2 posicion, World world) {
        super.crearCuerpo(posicion, world);

        CircleShape sensorShape = new CircleShape();
        sensorShape.setRadius(0.7f);

        FixtureDef sensorDef = new FixtureDef();
        sensorDef.shape = sensorShape;
        sensorDef.isSensor = true;

        configurarFiltroSensor(sensorDef);
        body.createFixture(sensorDef).setUserData("sensor");
        sensorShape.dispose();
    }

    /**
     * Configura los filtros de colisión para el sensor del robot.
     *
     * @param fdef Definición del fixture del sensor
     */
    public void configurarFiltroSensor(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_SENSOR;
        fdef.filter.maskBits = Constantes.CATEGORY_TRASH | Constantes.CATEGORY_NUBE |
            Constantes.CATEGORY_PERSONAJES | Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_OBJETOS;
    }

    /**
     * Configura los filtros de colisión para el cuerpo principal del robot.
     *
     * @param fdef Definición del fixture principal
     */
    @Override
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
        fdef.filter.maskBits = (short) (Constantes.CATEGORY_PERSONAJES | Constantes.CATEGORY_OBJETOS |
            Constantes.CATEGORY_GOLPE_PERSONAJES);
    }

    /**
     * Libera los recursos utilizados por el robot.
     */
    @Override
    public void dispose() {
        atlas.dispose();
    }
}
