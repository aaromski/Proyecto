package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Clase abstracta base que representa cualquier personaje jugable o no jugable en el juego.
 * Define atributos comunes como posición, cuerpo físico, animaciones, y lógica de colisión.
 * Las subclases deben implementar la lógica específica de comportamiento y animaciones.
 *
 * @author
 */
public abstract class Personaje {

    /** Velocidad de movimiento del personaje en el mundo. */
    protected Vector2 velocidad;

    /** Tiempo transcurrido desde el último cambio de frame. */
    protected float stateTime = 0f;

    /** Frame actual de la animación que se debe renderizar. */
    protected TextureRegion frameActual;

    /** Sprite utilizado para dibujar el personaje en pantalla. */
    protected Sprite sprite;

    /** Animación de correr. Se puede reemplazar o ampliar según personaje. */
    protected Animation<TextureRegion> correr;

    /** Cuerpo físico del personaje en el mundo de Box2D. */
    protected Body body;

    /** Constante de conversión entre píxeles y metros físicos (Pixeles Por Metro). */
    public static final float PPM = 32f;

    /** Atlas de texturas con los sprites utilizados para las animaciones. */
    protected TextureAtlas atlas;

    /** Posición del personaje en el mundo. */
    protected Vector2 posicion;

    /** Nombre identificador del personaje. */
    protected String name;

    /** Estado knockout del personaje. Indica si ha sido derrotado. */
    protected boolean ko = false;

    /** Referencia al mundo físico Box2D donde está integrado el personaje. */
    protected final World world;

    /**
     * Constructor base para inicializar un personaje con su posición inicial y mundo físico.
     *
     * @param posicion posición de inicio en coordenadas físicas
     * @param w instancia del mundo físico Box2D
     */
    public Personaje(Vector2 posicion, World w) {
        this.world = w;
        crearCuerpo(posicion, w);
        this.posicion = body.getPosition();
        velocidad = new Vector2(3f, 3f);
    }

    /**
     * Método abstracto para inicializar las animaciones del personaje en clases derivadas.
     *
     * @param x posición X inicial usada para configurar animaciones
     * @param y posición Y inicial usada para configurar animaciones
     */
    abstract void inicializarAnimaciones(float x, float y);

    /**
     * Método abstracto que actualiza la lógica del personaje en cada ciclo del juego.
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    abstract void actualizar(float delta);

    /**
     * Crea una animación a partir de una secuencia de regiones dentro del atlas.
     *
     * @param baseNombre prefijo común de las texturas (ejemplo: "correr")
     * @param cantidadFrames cantidad total de frames en la animación
     * @param duracion duración total de la animación por ciclo
     * @return objeto Animation listo para ser usado
     */
    protected Animation<TextureRegion> crearAnimacion(String baseNombre, int cantidadFrames, float duracion) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= cantidadFrames; i++) {
            frames.add(atlas.findRegion(baseNombre + i));
        }
        return new Animation<>(duracion, frames);
    }

    /**
     * Renderiza el sprite actual del personaje en pantalla.
     *
     * @param batch sprite batch activo para dibujado
     */
    public void render(SpriteBatch batch) {
        if (frameActual != null && body != null) {
            batch.draw(
                frameActual,
                posicion.x - sprite.getWidth() / 2f,
                posicion.y - sprite.getHeight() / 2f,
                sprite.getWidth(),
                sprite.getHeight()
            );
        }
    }

    /**
     * Configura el filtro de colisión para el personaje. Definido por cada subclase.
     *
     * @param fdef definición del fixture que será filtrado
     */
    abstract void configurarFiltro(FixtureDef fdef);

    /**
     * Crea el cuerpo físico del personaje en el mundo Box2D, incluyendo su fixture y atributos.
     *
     * @param posicion posición de aparición del personaje
     * @param world instancia del mundo físico donde se coloca
     */
    public void crearCuerpo(Vector2 posicion, World world) {
        BodyDef bd = new BodyDef();
        bd.position.set(posicion);
        bd.type = BodyDef.BodyType.DynamicBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;

        body = world.createBody(bd);
        body.setUserData(this);
        body.setLinearDamping(5f);

        configurarFiltro(fixDef);

        body.createFixture(fixDef).setUserData(this);
        circle.dispose();
    }

    /**
     * @return cuerpo físico del personaje
     */
    public Body getCuerpo() { return body; }

    /**
     * @return estado KO del personaje
     */
    public boolean getKO() { return ko; }

    /**
     * Cambia el estado KO y reinicia el tiempo de animación.
     */
    public void setKO() {
        ko = !ko;
        stateTime = 0f;
    }

    /**
     * Destruye el cuerpo físico del personaje si está KO.
     */
    public void destruir() {
        if (ko && body != null) {
            this.world.destroyBody(body);
            body = null;
        }
    }

    /**
     * Método abstracto para liberar recursos del personaje.
     * Las subclases deben implementar esta lógica.
     */
    public abstract void dispose();
}
