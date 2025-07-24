package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Main;

import java.util.Arrays;

public class PantallaFin extends BaseMenu {
    private Texture fondo;

    private final float botonW = 250f;
    private final float botonH = 60f;

    public PantallaFin(Main game) {
        super(game);
    }

    @Override
    public void show() {
        fondo = new Texture("Menu/Sunset_Hill.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        batch.begin();

        // Fondo
        batch.draw(fondo, 0, 0, screenW, screenH);

        // Título centrado
        String titulo = "FIN DEL JUEGO";
        font.setColor(1, 1, 1, 1);
        layout.setText(font, titulo);
        float textoX = screenW / 2f - layout.width / 2f;
        float textoY = screenH / 2f + 120;
        font.draw(batch, titulo, textoX, textoY);

        // Coordenadas para botones
        float botonX = screenW / 2f - botonW / 2f;
        float botonY1 = screenH / 2f + 20;   // REINICIAR
        float botonY2 = screenH / 2f - 60;   // VOLVER AL MENÚ
        float botonY3 = screenH / 2f - 140;   // VER PUNTUACIÓN


        // Botón REINICIAR
        batch.draw(boton, botonX, botonY1, botonW, botonH);
        layout.setText(font, "REINICIAR");
        float texto1X = botonX + botonW / 2f - layout.width / 2f;
        float texto1Y = botonY1 + botonH / 2f + layout.height / 2f;
        font.draw(batch, "REINICIAR", texto1X, texto1Y);

        // Botón VOLVER
        batch.draw(boton, botonX, botonY2, botonW, botonH);
        layout.setText(font, "VOLVER AL MENÚ");
        float texto2X = botonX + botonW / 2f - layout.width / 2f;
        float texto2Y = botonY2 + botonH / 2f + layout.height / 2f;
        font.draw(batch, "VOLVER AL MENÚ", texto2X, texto2Y);

        // Botón VER PUNTUACIÓN
        batch.draw(boton, botonX, botonY3, botonW, botonH);
        layout.setText(font, "VER PUNTUACIÓN");
        float texto3X = botonX + botonW / 2f - layout.width / 2f;
        float texto3Y = botonY3 + botonH / 2f + layout.height / 2f;
        font.draw(batch, "VER PUNTUACIÓN", texto3X, texto3Y);

        batch.end();

        // Manejo de clics
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = screenH - Gdx.input.getY();

            if (mx >= botonX && mx <= botonX + botonW) {
                if (my >= botonY1 && my <= botonY1 + botonH) {
                    reiniciarJuego();
                    game.setScreen(new PantallaJuego(game));
                    dispose();
                } else if (my >= botonY2 && my <= botonY2 + botonH) {
                    game.setScreen(new MenuPrincipal(game));
                    dispose();
                }
                if (my >= botonY3 && my <= botonY3 + botonH) {
                    game.setScreen(new PantallaRanking(game)); // Ir a ranking
                    dispose();
                }

            }
        }

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private void reiniciarJuego() {
        Arrays.fill(Constantes.VIDAS, 3);
        Arrays.fill(Constantes.SCORE, 0);
    }

    @Override
    public void dispose() {
        fondo.dispose();
    }
}
