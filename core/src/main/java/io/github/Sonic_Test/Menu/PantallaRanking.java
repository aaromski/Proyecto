package io.github.Sonic_Test.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_Test.Main;

public class PantallaRanking implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fondo;
    private Texture boton;
    private GlyphLayout layout;

    private float scrollX = 0f;          // Para el fondo en movimiento

    public PantallaRanking(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        fondo = new Texture("Menu/Sunset_Hill.png");
        boton = new Texture("Menu/button.png");
        layout = new GlyphLayout();
    }

    private boolean dibujarBotonConTexto(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                         Texture botonTex) {
        batch.draw(botonTex, (float) 100, (float) 100, (float) 200, (float) 60);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, "VOLVER");
        float textoX = (float) 100 + (float) 200 / 2f - layout.width / 2f;
        float textoY = (float) 100 + (float) 60 / 2f + layout.height / 2f;
        font.draw(batch, "VOLVER", textoX, textoY);

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        return Gdx.input.justTouched()
            && mx >= (float) 100 && mx <= (float) 100 + (float) 200
            && my >= (float) 100 && my <= (float) 100 + (float) 60;
    }

    @Override
    public void render(float delta) {
        // Actualizar desplazamiento del fondo
        // Velocidad de desplazamiento en píxeles por segundo
        float scrollSpeed = 50f;
        scrollX -= scrollSpeed * delta;
        if (scrollX <= -fondo.getWidth()) {
            scrollX += fondo.getWidth();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int fondoW = fondo.getWidth();

        // Dibujar copias encadenadas del fondo para efecto scrolling
        for (int x = (int)scrollX; x < screenW; x += fondoW) {
            batch.draw(fondo, x, 0, fondoW, screenH);
        }

        // Texto ranking
        font.setColor(1, 1, 1, 1); // Texto negro
        font.draw(batch, "RANKING DE JUGADORES", 100, 450);
        font.draw(batch, "1. SonicMaster   - 1500 pts", 100, 390);
        font.draw(batch, "2. ShadowPlayer  - 1200 pts", 100, 360);
        font.draw(batch, "3. AmyRocker     - 900 pts", 100, 330);

        // Botón VOLVER
        boolean clicVolver = dibujarBotonConTexto(batch, font, layout, boton);

        batch.end();

        if (clicVolver) {
            game.setScreen(new MenuPrincipal(game));
        }
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
    }
}
