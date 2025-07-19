package io.github.Sonic_V0;

public final class Constantes {
    // Escala del mundo físico
    public static final float WORLD_ESCALA = 0.039f;

    // Categorías de colisión para Box2D
    public static final short CATEGORY_ROBOT = 0x0001;
    public static final short CATEGORY_TRASH = 0x0002;
    public static final short CATEGORY_PERSONAJES = 0x0004;

    // Tamaños estándar
    public static final float TRASH_WIDTH = 0.8f;
    public static final float TRASH_HEIGHT = 0.8f;

    // Textura de la basura
    public static final String TRASH_TEXTURE_PATH = "Mapa1/trash.png";

    //Vida personajes
    public static final int[] VIDAS = {3,3,3};

    //Score
    public static final int[] SCORE = {0,0,0};
}
