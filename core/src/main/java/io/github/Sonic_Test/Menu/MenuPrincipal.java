package io.github.Sonic_Test.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.github.Sonic_Test.Main;

public class MenuPrincipal implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fondo, boton, logo;
    private GlyphLayout layout;

    private final int botonW = 250, botonH = 60;
    private final int startX = Gdx.graphics.getWidth() / 2 - botonW / 2;
    private final int startY = 300;
    private final int espacio = 70;

    private final float fondoX = 0f;

    private float logoAlpha = 0f;
    private float logoYactual;
    private final float logoYfinal = Gdx.graphics.getHeight() - 120 - 30;
    private float scrollX;


    public MenuPrincipal(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        fondo = new Texture("Menu/Sunset_Hill.png");
        boton = new Texture("Menu/button.png");
        logo = new Texture("Menu/sonicmania.png");
        layout = new GlyphLayout();
        logoYactual = logoYfinal + 100f; // Arranca más arriba
    }

    @Override
    public void render(float delta) {
        // Desplazamiento del fondo
        scrollX -= delta * 50f; // velocidad de desplazamiento
        if (scrollX <= -fondo.getWidth()) scrollX += fondo.getWidth();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int fondoW = fondo.getWidth();

        // Dibujar copias encadenadas del fondo
        for (int x = (int)scrollX; x < screenW; x += fondoW) {
            batch.draw(fondo, x, 0, fondoW, screenH);
        }

        // Animación del logo
        if (logoAlpha < 1f) logoAlpha += delta;
        if (logoYactual > logoYfinal) logoYactual -= delta * 80f;
        if (logoYactual < logoYfinal) logoYactual = logoYfinal;

        batch.setColor(1, 1, 1, logoAlpha);
        int logoW = 300, logoH = 120;
        int logoX = screenW / 2 - logoW / 2;
        batch.draw(logo, logoX, logoYactual, logoW, logoH);
        batch.setColor(Color.WHITE);

        // Botones
        for (int i = 0; i < 4; i++) drawBoton(getTextoBoton(i), i);

        batch.end();

        // Lógica de clics
        if (Gdx.input.justTouched()) {
            int mx = Gdx.input.getX();
            int my = screenH - Gdx.input.getY();

            for (int i = 0; i < 4; i++) {
                int by = startY - i * espacio;
                if (mx >= startX && mx <= startX + botonW && my >= by && my <= by + botonH) {
                    switch (i) {
                        case 0:
                            game.setScreen(new PantallaJuego(game));
                            break;
                        case 1:
                            game.setScreen(new PantallaAyuda(game));
                            break;
                        case 2:
                            game.setScreen(new PantallaRanking(game));
                            break;
                        case 3:
                            Gdx.app.exit();
                            break;
                    }
                }
            }
        }
    }

    private String getTextoBoton(int i) {
        switch (i) {
            case 0: return "PLAY";
            case 1: return "AYUDA";
            case 2: return "RANKING";
            default: return "SALIR";
        }
    }

    private void drawBoton(String texto, int index) {
        int y = startY - index * espacio;
        batch.draw(boton, startX, y, botonW, botonH);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, texto);
        float textoX = startX + botonW / 2f - layout.width / 2f;
        float textoY = y + botonH / 2f + layout.height / 2f;
        font.draw(batch, texto, textoX, textoY);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo.dispose();
        boton.dispose();
        logo.dispose();
    }
}
