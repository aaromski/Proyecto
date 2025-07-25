package io.github.Sonic_V0;

/**
 * Clase que contiene constantes globales para la configuración del mundo físico,
 * categorías de colisión y parámetros generales de juego.
 *
 * Estas constantes son utilizadas en diferentes módulos como físicas (Box2D),
 * lógica de colisión, y manejo de estado del jugador.
 *
 * @author Aarom Luces
 */
public final class Constantes {

    /** Escala utilizada para convertir píxeles a unidades del mundo físico (Box2D). */
    public static final float WORLD_ESCALA = 0.039f;

    // ================== Categorías de colisión para Box2D ==================

    /** Categoría de colisión para entidades tipo Robot. */
    public static final short CATEGORY_ROBOT = 0x0001;

    /** Categoría de colisión para objetos basura. */
    public static final short CATEGORY_TRASH = 0x0002;

    /** Categoría utilizada para sensores de contacto. */
    public static final short CATEGORY_SENSOR = 0x0004;

    /** Categoría de colisión para personajes controlables. */
    public static final short CATEGORY_PERSONAJES = 0x0008;

    /** Categoría de colisión para nubes o elementos atmosféricos. */
    public static final short CATEGORY_NUBE = 0x0010;

    /** Categoría de colisión para objetos del escenario. */
    public static final short CATEGORY_OBJETOS = 0x0020;

    /** Categoría utilizada para golpes provenientes de personaje. */
    public static final short CATEGORY_GOLPE_PERSONAJES = 0x0040;

    // ================== Parámetros gráficos ==================

    /** Píxeles por metro. Se usa para convertir entre unidades físicas y gráficas. */
    public static final float PPM = 32.0f;

    // ================== Estado del juego ==================

    /** Arreglo de vidas por personaje. */
    public static final int[] VIDAS = {3, 3, 3};

    /** Arreglo de puntuaciones individuales por personaje. */
    public static final int[] SCORE = {0, 0, 0};
}
