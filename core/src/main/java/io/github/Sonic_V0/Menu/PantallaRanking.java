package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Main;

public class PantallaRanking extends BaseMenu {
    private final Fondo fondo;

    public PantallaRanking(Main game) {
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

        // Título
        font.setColor(1, 1, 0, 1); // Amarillo
        font.draw(batch, "RANKING DE JUGADORES", 100, 450);

        // Puntajes individuales
        font.setColor(0, 0, 1, 1); // Azul
        font.draw(batch, "1. Sonic      - " + Constantes.SCORE[0] + " pts", 100, 390);
        font.draw(batch, "2. Knuckles   - " + Constantes.SCORE[1] + " pts", 100, 360);
        font.draw(batch, "3. Tails      - " + Constantes.SCORE[2] + " pts", 100, 330);

        // Total
        int total = Constantes.SCORE[0] + Constantes.SCORE[1] + Constantes.SCORE[2];
        font.setColor(1, 0.5f, 0, 1); // Naranja
        font.draw(batch, "TOTAL: " + total + " pts", 100, 290);

        // Botón VOLVER centrado
        float botonVolverX = (Gdx.graphics.getWidth() - botonW) / 2f;
        float botonVolverY = 100;
        boolean clicVolver = dibujarBotonConTexto(batch, font, layout, boton, "VOLVER",
            botonVolverX, botonVolverY, botonW, botonH);

        batch.end();

        if (clicVolver) {
            game.setScreen(new MenuPrincipal(game));
        }
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
