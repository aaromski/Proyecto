package io.github.Sonic_V0.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.Main;

/**
 * Clase que representa la pantalla de ayuda y "Acerca de" del juego.
 * <p>
 * Esta pantalla permite al usuario ver los controles del juego y la información
 * sobre los desarrolladores y la versión del juego.
 *
 * @author Miguel Rivas
 * @version 1.0
 * @see BaseMenu
 * @see MenuPrincipal
 */
public class PantallaAyuda extends BaseMenu {
    /**
     * Sección actual mostrada: 0 para Ayuda, 1 para Acerca de.
     */
    private int seccionActual = 0;
    /**
     * Fondo animado de la pantalla de ayuda.
     */
    private final Fondo fondo;

    /**
     * Constructor de la clase PantallaAyuda.
     *
     * @param game Referencia a la clase principal del juego.
     */
    public PantallaAyuda(Main game) {
        super(game);
        fondo = new Fondo();
    }

    /**
     * Método llamado cuando esta pantalla se convierte en la pantalla actual del juego.
     * En esta implementación, no se realiza ninguna inicialización específica.
     */
    @Override
    public void show() {
        // No se requiere inicialización específica aquí, ya que los recursos se cargan en el constructor o son compartidos.
    }

    /**
     * Dibuja un botón con texto y detecta si ha sido clicado.
     *
     * @param batch El SpriteBatch utilizado para dibujar.
     * @param font La fuente BitmapFont para el texto del botón.
     * @param layout El GlyphLayout para calcular el tamaño del texto.
     * @param botonTex La textura del botón.
     * @param texto El texto a mostrar en el botón.
     * @param x La posición X del botón.
     * @param y La posición Y del botón.
     * @param width El ancho del botón.
     * @param height La altura del botón.
     * @return `true` si el botón fue clicado en este frame, `false` en caso contrario.
     */
    private boolean dibujarBotonConTexto(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                         Texture botonTex, String texto,
                                         float x, float y, float width, float height) {
        batch.draw(botonTex, x, y, width, height);
        font.setColor(1, 1, 1, 1); // Color blanco para el texto del botón
        layout.setText(font, texto);
        float textoX = x + width / 2f - layout.width / 2f;
        float textoY = y + height / 2f + layout.height / 2f;
        font.draw(batch, texto, textoX, textoY);

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY(); // Coordenadas Y invertidas para la pantalla

        return Gdx.input.justTouched() // Solo si se acaba de tocar la pantalla
            && mx >= x && mx <= x + width
            && my >= y && my <= y + height;
    }

    /**
     * Método principal de renderizado llamado en cada frame para actualizar y dibujar la pantalla.
     *
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    @Override
    public void render(float delta) {
        fondo.actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1); // Limpiar la pantalla con color negro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin(); // Iniciar el dibujo

        fondo.dibujar(batch, screenW, screenH); // Dibujar el fondo

        // Botones de pestañas para Ayuda y Acerca de
        int botonPestanaW = 150;
        int botonPestanaH = 40;
        int botonAyudaX = 100;
        int botonAyudaY = 500;
        boolean clicAyuda = dibujarBotonConTexto(batch, font, layout, boton, "AYUDA", botonAyudaX, botonAyudaY, botonPestanaW, botonPestanaH);
        int botonAcercaX = 260;
        int botonAcercaY = 500;
        boolean clicAcerca = dibujarBotonConTexto(batch, font, layout, boton, "ACERCA DE", botonAcercaX, botonAcercaY, botonPestanaW, botonPestanaH);

        // Resaltar la sección activa (Ayuda o Acerca de)
        if (seccionActual == 0) {
            font.setColor(1, 1, 0, 1); // Color amarillo para la pestaña activa
            layout.setText(font, "AYUDA");
            font.draw(batch, "AYUDA", botonAyudaX + botonPestanaW / 2f - layout.width / 2f, botonAyudaY + botonPestanaH / 2f + layout.height / 2f);
        } else {
            font.setColor(1, 1, 0, 1); // Color amarillo para la pestaña activa
            layout.setText(font, "ACERCA DE");
            font.draw(batch, "ACERCA DE", botonAcercaX + botonPestanaW / 2f - layout.width / 2f, botonAcercaY + botonPestanaH / 2f + layout.height / 2f);
        }

        font.setColor(0, 0, 1, 1); // Restaurar color de fuente para el contenido

        // Mostrar contenido según la sección activa
        if (seccionActual == 0) {
            font.setColor(0f, 0f, 1f, 1f); // Color azul para el texto de ayuda
            font.draw(batch,
                "Controles:\n" +
                    "-           SONIC:       KNUCKLES:        TAILS:\n" +
                    "- ARRIBA:      W             I                 flecha arriba\n" +
                    "- ABAJO:         S             K                 flecha abajo\n" +
                    "- IZQUIERDA:   A             J                 f. izquierda\n" +
                    "- DERECHA:     D             L                 f. derecha\n" +
                    "- GOLPE:         F             P                 \n" +
                    "- PODERES:\n" +
                    "-                                         IMAN: NUMPAD_0 (DOS VECES ACTIVA)\n" +
                    "- ESC: Pausar\n\n" +
                    "Reglas:\n" +
                    "- No dejarse tocar por los robots\n" +
                    "- Limpiar el mundo de chatarra",
                100, 450);
        } else {
            font.draw(batch,
                "Lenguaje: Java\n" +
                    "El juego se basa en lo siguiente:\n" +
                    "- Sonic recoge basura para ganar puntos\n" +
                    "- Knuckles convierte robots enemigos en chatarra\n" +
                    "- Tails destruye robots llevándolos a la fábrica\n" +
                    "- Los robots se destruyen dentro de la fábrica\n" +
                    "- Si los robots te tocan, pierdes vida\n" +
                    "- Al perder vida, reapareces invencible por unos segundos\n" +
                    "- Si tardas en destruirlos, los robots se reactivan\n" +
                    "- Eggman aparece cada cierto tiempo generando más robots\n" +
                    "- Cada personaje tiene una tarea única\n\n" +
                    "Modo de juego: Sin límite de tiempo\n\n" +
                    "Librerías: LibGDX\n" +
                    "Desarrolladores:\n" +
                    "- Aarom Luces\n" +
                    "- Jesus Guzman\n" +
                    "- Miguel Rivas\n" +
                    "- Yoryelis Ocando\n" +
                    "- Miguel Carreño\n\n" +
                    "Versión: 1.0.3",
                100, 460);
        }

        // Botón VOLVER
        int botonW = 200;
        int botonH = 60;
        float botonVolverX = (Gdx.graphics.getWidth() - botonW) / 2f;
        int botonVolverY = 100;
        boolean clicVolver = dibujarBotonConTexto(batch, font, layout, boton, "VOLVER", botonVolverX, botonVolverY, botonW, botonH);

        batch.end(); // Finalizar el dibujo

        // Manejo de clics (fuera del batch para evitar problemas de estado)
        if (clicAyuda) seccionActual = 0;
        if (clicAcerca) seccionActual = 1;
        if (clicVolver) game.setScreen(new MenuPrincipal(game));
    }

    /**
     * Método llamado cuando la pantalla se pausa.
     * No se implementa ninguna lógica específica en esta clase.
     */
    @Override public void pause() {}
    /**
     * Método llamado cuando la pantalla se reanuda.
     * No se implementa ninguna lógica específica en esta clase.
     */
    @Override public void resume() {}
    /**
     * Método llamado cuando la pantalla deja de ser la pantalla actual.
     * No se implementa ninguna lógica específica en esta clase.
     */
    @Override public void hide() {}

    /**
     * Libera todos los recursos utilizados por esta pantalla de menú.
     * Se encarga de disponer del fondo.
     */
    @Override
    public void dispose() {
        fondo.dispose();
        // Los otros recursos (batch, font, boton) son gestionados por la clase padre BaseMenu.
    }
}
