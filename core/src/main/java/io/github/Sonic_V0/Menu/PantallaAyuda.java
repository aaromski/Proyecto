package io.github.Sonic_V0.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.Main;


public class PantallaAyuda extends BaseMenu {
    // Estado: 0 = Ayuda, 1 = Acerca de
    private int seccionActual = 0;
    private final Fondo fondo;
    public PantallaAyuda(Main game) {
        super(game);
        fondo = new Fondo();
    }

    @Override
    public void show() {

    }

    private boolean dibujarBotonConTexto(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                         Texture botonTex, String texto,
                                         float x, float y, float width, float height) {
        batch.draw(botonTex, x, y, width, height);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, texto);
        float textoX = x + width / 2f - layout.width / 2f;
        float textoY = y + height / 2f + layout.height / 2f;
        font.draw(batch, texto, textoX, textoY);

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        return Gdx.input.justTouched()
            && mx >= x && mx <= x + width
            && my >= y && my <= y + height;
    }

    @Override
    public void render(float delta) {
        fondo.actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        fondo.dibujar(batch, screenW, screenH);

        // Botones de pestañas
        int botonPestanaW = 150;
        int botonPestanaH = 40;
        int botonAyudaX = 100;
        int botonAyudaY = 500;
        boolean clicAyuda = dibujarBotonConTexto(batch, font, layout, boton, "AYUDA", botonAyudaX, botonAyudaY, botonPestanaW, botonPestanaH);
        int botonAcercaX = 260;
        int botonAcercaY = 500;
        boolean clicAcerca = dibujarBotonConTexto(batch, font, layout, boton, "ACERCA DE", botonAcercaX, botonAcercaY, botonPestanaW, botonPestanaH);

        // Resaltar sección activa (puedes cambiar color o dibujar un rectángulo debajo)
        if (seccionActual == 0) {
            font.setColor(1, 1, 0, 1); // amarillo para Ayuda activa
            layout.setText(font, "AYUDA");
            font.draw(batch, "AYUDA", botonAyudaX + botonPestanaW / 2f - layout.width / 2f, botonAyudaY + botonPestanaH / 2f + layout.height / 2f);
        } else {
            font.setColor(1, 1, 0, 1); // amarillo para Acerca activa
            layout.setText(font, "ACERCA DE");
            font.draw(batch, "ACERCA DE", botonAcercaX + botonPestanaW / 2f - layout.width / 2f, botonAcercaY + botonPestanaH / 2f + layout.height / 2f);
        }

        font.setColor(1, 1, 1, 1); // Texto negro normal

        // Mostrar contenido según sección
        if (seccionActual == 0) {
            font.draw(batch,
                "Controles:\n" +
                "- A: Izquierda\n" +
                "- D: Derecha\n" +
                "- W/ESPACIO: Saltar\n" +
                "- ESC: Pausar\n\n " +

                "Reglas:\n"+
                "- No caer al vacío\n" +
                "- Completar niveles sin perder vidas",
                100, 400);
        } else {
            font.draw(batch,
                "Lenguaje: Java\n" +
                    "Librerías: LibGDX\n" +
                    "Desarrolladores:\n" +
                    "- Aarom Luces\n" +
                    "- Jesus Guzman\n" +
                    "- Miguel Rivas\n" +
                    "- Yoryelis Ocando\n" +
                    "- Miguel Carreño\n\n" +
                    "Versión: 1.0.3",
                 100, 400);
        }

        // Botón VOLVER
        // Botones
        int botonW = 200;
        int botonH = 60;
        int botonVolverX = 100;
        int botonVolverY = 100;
        boolean clicVolver = dibujarBotonConTexto(batch, font, layout, boton, "VOLVER", botonVolverX, botonVolverY, botonW, botonH);

        batch.end();

        // Manejo clics fuera del batch (para evitar dibujar dentro del batch)

        if (clicAyuda) seccionActual = 0;
        if (clicAcerca) seccionActual = 1;
        if (clicVolver) game.setScreen(new MenuPrincipal(game));
    }


    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo.dispose();
        boton.dispose();
    }
}
