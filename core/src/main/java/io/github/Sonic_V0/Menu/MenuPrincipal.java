package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Main;

import java.util.Arrays;

public class MenuPrincipal extends BaseMenu {
    private Texture logo;
    private final Fondo fondo;
    private final int startX = Gdx.graphics.getWidth() / 2 - (int)botonW / 2;
    private final int startY = 300;
    private final int espacio = 70;
    private float logoAlpha = 0f;
    private float logoYactual;
    private final float logoYfinal = Gdx.graphics.getHeight() - 120 - 30;

    public MenuPrincipal(Main game) {
        super(game);
        fondo = new Fondo();
    }

    @Override
    public void show() {
        logo = new Texture("Menu/sonicmania.png");
        logoYactual = logoYfinal + 100f; // Arranca más arriba
    }

    @Override
    public void render(float delta) {
        fondo.actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

       fondo.dibujar(batch, screenW, screenH);

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
                            reiniciar();
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

    public void reiniciar() {
        // Reiniciar vidas
        Arrays.fill(Constantes.VIDAS, 3);

        // Reiniciar score
        Arrays.fill(Constantes.SCORE, 0);
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
        logo.dispose();
    }
}
