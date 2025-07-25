package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import io.github.Sonic_V0.Constantes;
import com.badlogic.gdx.utils.Array;

/**
 * Representa al personaje Tails en el juego, con una habilidad especial de atracción magnética para recoger robots KO.
 *
 * Hereda de {@link Amigas} y extiende la funcionalidad con animaciones específicas, control de teclado,
 * y uso de joints físicos para simular el efecto magnético.
 *
 * La habilidad del imán se activa con doble pulsación de NUMPAD_0 y atrae un robot destruido dentro de un área determinada.
 *
 * @author Yoryelis Ocando
 */
public class Tails extends Amigas {
    /** Indica si la habilidad de imán está activa. */
    private boolean imanActivo = false;

    /** Radio de detección para atraer robots KO. */
    private final float radioAtraccionIman = 1.8f;

    /** Sprite animado del imán en uso. */
    protected TextureRegion imanSprite;

    /** Atlas que contiene los fotogramas de la animación del imán. */
    private TextureAtlas imanAtlas;

    /** Animación del efecto de imán. */
    private Animation<TextureRegion> imanAnimation;

    /** Timestamp de la última pulsación para activar el imán. */
    private long lastImanPressTime = 0;

    /** Tiempo límite entre dos pulsaciones para activar el imán (doble tap). */
    private static final long DOUBLE_PRESS_IMAN_TIME = 200;

    /** Ángulo para animar órbita visual del imán. */
    private float imanOrbitAngle = 0f;

    /** Radio visual de órbita del imán. */
    private float imanOrbitRadius = 0.5f;

    /** Distancia de offset entre Tails y el robot atraído. */
    private final float robotOffsetDistance = 0.1f;

    /** Joint físico que conecta el cuerpo de Tails con un robot KO. */
    private MouseJoint robotAtraidoJoint;

    /** Robot actual que está siendo atraído por el imán. */
    private Robot robotSiendoAtraido;

    /** Controla si el imán puede activarse nuevamente tras su uso. */
    private boolean puedeActivarIman = true;

    /** Tiempo acumulado desde la activación del imán. */
    private float tiempoImanActivo = 0f;

    /** Tiempo transcurrido en el cooldown del imán. */
    private float tiempoCooldownIman = 0f;

    /** Duración máxima en segundos del imán activo. */
    private static final float DURACION_IMAN = 10f;

    /** Tiempo necesario para recuperar el imán tras su uso. */
    private static final float COOLDOWN_IMAN = 5f;

    /** Flag para marcar si el MouseJoint debe ser destruido. */
    private boolean destruirJoin = false;

    /**
     * Constructor de Tails.
     *
     * @param posicion Posición inicial en el mundo físico.
     * @param world Mundo Box2D donde el personaje interactúa.
     */
    public Tails(Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Tails";
    }

    /**
     * Inicializa animaciones y efectos visuales, incluyendo el imán.
     *
     * @param x Coordenada horizontal inicial.
     * @param y Coordenada vertical inicial.
     */

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteTails/Tails.atlas"));
        sprite = atlas.createSprite("TailsSprite0");
        sprite.setSize(30 / Constantes.PPM, 39f / Constantes.PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );

        correr = crearAnimacion("TailsSprite", 7, 0.09f);
        abajo = crearAnimacion("abajo", 3, 0.1f);
        arriba = crearAnimacion("arriba", 7, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 9, 0.1f);
        diagonalabj = crearAnimacion("diagonalabj", 4, 0.1f);

        try {
            imanAtlas = new TextureAtlas(Gdx.files.internal("SpriteTails/iman.atlas"));
            Array<TextureRegion> imanFrames = new Array<>();

            for (int i = 1; i <= 4; i++) {
                TextureRegion frame = imanAtlas.findRegion("iman" + i);
                if (frame != null) {
                    imanFrames.add(frame);
                }
            }

            if (imanFrames.size > 0) {
                imanAnimation = new Animation<>(0.15f, imanFrames, Animation.PlayMode.LOOP);
                imanSprite = imanAnimation.getKeyFrame(0);
            } else {
                imanAnimation = null;
                imanSprite = null;
            }
        } catch (Exception e) {
            imanAnimation = null;
            imanSprite = null;
        }

        frameActual = new TextureRegion();
    }

    /**
     * Define el filtro de colisión del personaje según su categoría.
     *
     * @param fdef Objeto FixtureDef para modificar.
     */
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = -1;
    }

    /**
     * Actualiza la lógica de movimiento y habilidad magnética del personaje Tails.
     *
     * @param delta Tiempo desde el último frame.
     */
    @Override
    public void actualizar(float delta) {
        if (body == null) return;

        if (robotSiendoAtraido != null) {
            Robot robot = robotSiendoAtraido;
            Body robotBody = robot.body;

            if (robotBody == null || !robotBody.isActive() || robotBody.getWorld() == null) {
                destruirJoin = true;
            }
        }

        if (imanActivo) {
            tiempoImanActivo += delta;
            if (tiempoImanActivo >= DURACION_IMAN) {
                imanActivo = false;
                tiempoCooldownIman = 0f;
                Gdx.app.log("Tails", "Imán desactivado automáticamente");
            }
        } else if (!puedeActivarIman) {
            tiempoCooldownIman += delta;
            if (tiempoCooldownIman >= COOLDOWN_IMAN) {
                puedeActivarIman = true;
                Gdx.app.log("Tails", "Imán listo para reactivarse");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            long currentTime = System.currentTimeMillis();
            if (puedeActivarIman && (currentTime - lastImanPressTime <= DOUBLE_PRESS_IMAN_TIME)) {
                imanActivo = true;
                puedeActivarIman = false;
                tiempoImanActivo = 0f;
                tiempoCooldownIman = 0f;
                Gdx.app.log("Tails", "Imán ACTIVADO");
            }
            lastImanPressTime = currentTime;
        }

        if (imanActivo) {
            imanOrbitAngle = (imanOrbitAngle + (200f * delta)) % 360f;

            if (robotAtraidoJoint != null && robotSiendoAtraido != null && robotSiendoAtraido.body != null && !world.isLocked()) {
                if (!robotSiendoAtraido.body.isActive()) {
                    destruirJoin = true;
                } else {
                    Vector2 tailsPos = body.getPosition();
                    Vector2 robotPos = robotSiendoAtraido.body.getPosition();
                    Vector2 direction = body.getLinearVelocity().cpy().nor();
                    Vector2 target = tailsPos.cpy();

                    if (direction.len2() > 0.01f) {
                        target.sub(direction.scl(robotOffsetDistance));
                    } else {
                        target.add(robotOffsetDistance * 0.5f, robotOffsetDistance * 0.5f);
                    }

                    float distancia = tailsPos.dst(robotPos);
                    if (distancia < robotOffsetDistance * 0.8f) {
                        Vector2 dir = robotPos.cpy().sub(tailsPos).nor();
                        target = tailsPos.cpy().add(dir.scl(robotOffsetDistance));
                    }

                    robotAtraidoJoint.setTarget(target);
                }
            } else if (robotAtraidoJoint == null) {
                this.world.QueryAABB(new QueryCallback() {
                                         @Override
                                         public boolean reportFixture(Fixture fixture) {
                                             Object userData = fixture.getBody().getUserData();

                                             if (userData instanceof Robot) {
                                                 Robot robot = (Robot) userData;
                                                 if (robot.ko && robot.body != null) {
                                                     float distancia = body.getPosition().dst(robot.body.getPosition());
                                                     if (distancia < radioAtraccionIman) {
                                                         MouseJointDef md = new MouseJointDef();
                                                         md.bodyA = body;
                                                         md.bodyB = robot.body;
                                                         md.collideConnected = false;
                                                         md.target.set(body.getPosition().x, body.getPosition().y + robotOffsetDistance);
                                                         md.maxForce = robot.body.getMass() * 100f;
                                                         md.frequencyHz = 8f;
                                                         md.dampingRatio = 0.8f;

                                                         robotAtraidoJoint = (MouseJoint) world.createJoint(md);
                                                         robotSiendoAtraido = robot;

                                                         Gdx.app.log("Tails", "Robot KO detectado y enganchado con MouseJoint.");
                                                         return false;
                                                     }
                                                 }
                                             }
                                             return true;
                                         }
                                     },
                    body.getPosition().x - radioAtraccionIman,
                    body.getPosition().y - radioAtraccionIman,
                    body.getPosition().x + radioAtraccionIman,
                    body.getPosition().y + radioAtraccionIman);
            }
        } else {
            imanOrbitAngle = 0f;
            destruirJoin = true;
        }

        // Movimiento con teclas
        izq = der = arr = abj = false;
        boolean presionando = false;
        stateTime += delta;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.setLinearVelocity(0, velocidad.y);
            arr = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.setLinearVelocity(0, -velocidad.y);
            abj = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.setLinearVelocity(-velocidad.x, body.getLinearVelocity().y);
            izq = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(velocidad.x, body.getLinearVelocity().y);
            der = true;
            presionando = true;
        }
        if (!presionando) {
            body.setLinearVelocity(0, 0);
            frameActual = sprite;
            stateTime = 0f;
        }

        super.actualizar(delta);
    }

    /**
     * Destruye el MouseJoint utilizado para atraer el robot, de forma segura.
     *
     * Verifica que el mundo no esté bloqueado antes de eliminar el joint,
     * y restablece las variables relacionadas con la atracción.
     */
    private void destruirJointSeguro() {
        if (!world.isLocked() && robotAtraidoJoint != null) {
            try {
                Body b = robotAtraidoJoint.getBodyB();
                if (b != null && b.isActive() && b.getWorld() != null) {
                    world.destroyJoint(robotAtraidoJoint);
                }
            } catch (Exception e) {
                Gdx.app.error("Tails", "Error al destruir el joint", e);
            }
            robotAtraidoJoint = null;
            robotSiendoAtraido = null;
            imanActivo = false;
        }
    }

    /**
     * Renderiza visualmente el efecto del imán orbitando alrededor de Tails.
     *
     * La animación se dibuja si el imán está activo y hay fotogramas disponibles.
     *
     * @param batch Batch de renderizado utilizado para dibujar el sprite.
     */
    public void dibujarIman(SpriteBatch batch) {
        if (imanActivo && imanAnimation != null) {
            TextureRegion currentImanFrame = imanAnimation.getKeyFrame(stateTime, true);

            float imanWidth = currentImanFrame.getRegionWidth() / Constantes.PPM;
            float imanHeight = currentImanFrame.getRegionHeight() / Constantes.PPM;

            float tailsCenterX = body.getPosition().x;
            // Ajustar esta posición de Y para que el imán se vea claramente
            // Aquí lo posicionamos un poco por encima de Tails y ligeramente a la derecha.
            float tailsCenterY = body.getPosition().y + (sprite.getHeight() / 2f / Constantes.PPM) + 0.1f; // Ajuste en Y

            // Si hay un robot atraído, podemos hacer que el imán tenga un ligero desplazamiento fijo
            // o simplemente que orbite como antes, pero siempre visible.
            // Para asegurar visibilidad, vamos a hacer que orbite alrededor de Tails.
            // La lógica para que se dibujara "sobre el robot atraído" la quitamos de aquí.

            float angleRadians = (float) Math.toRadians(imanOrbitAngle);

            float orbitOffsetX = (float) (imanOrbitRadius * Math.cos(angleRadians));
            float orbitOffsetY = (float) (imanOrbitRadius * Math.sin(angleRadians));

            // Dibujamos el imán siempre orbitando alrededor de Tails cuando el imán está activo.
            // La posición del robot atraído no influye en la posición de dibujado del imán.
            float drawX = tailsCenterX + orbitOffsetX - (imanWidth / 2f);
            float drawY = tailsCenterY + orbitOffsetY - (imanHeight / 2f);

            float rotationOffset = 0f; // Puedes añadir una rotación si deseas
            float rotationAngle = imanOrbitAngle + rotationOffset;

            float originX = imanWidth / 2f;
            float originY = imanHeight / 2f;

            batch.draw(currentImanFrame,
                drawX,
                drawY,
                originX,
                originY,
                imanWidth,
                imanHeight,
                1f,
                1f,
                rotationAngle);
        }
    }

    /**
     * Ejecuta destrucción del joint si el flag {@code destruirJoin} está activo.
     */
    public void setDestruirJoin() {
        if (destruirJoin) {
            destruirJointSeguro();
            destruirJoin = false;
        }
    }

    /**
     * Obtiene el estado del flag que indica si se debe destruir el joint.
     *
     * @return {@code true} si se requiere destruir el joint, {@code false} en caso contrario.
     */

    public boolean getDestruirJoin() {
        return destruirJoin;
    }

    /**
     * Desactiva el imán manualmente.
     */
    public void setIman() {
        imanActivo = false;
    }

    /**
     * Consulta si la habilidad magnética está activa actualmente.
     *
     * @return {@code true} si el imán está activo.
     */
    public boolean getIman() {
        return imanActivo;
    }

    /**
     * Libera los recursos gráficos utilizados por Tails y su animación de imán.
     */
    @Override
    public void dispose() {
        atlas.dispose();
        if (imanAtlas != null) {
            imanAtlas.dispose();
        }

    }
}
