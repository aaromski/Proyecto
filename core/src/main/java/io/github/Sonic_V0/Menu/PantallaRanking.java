package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
                                         Texture botonTex) {
        batch.draw(botonTex, (float) 100, (float) 100, botonW, botonH);
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
        fondo.actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();


        fondo.dibujar(batch, screenW, screenH);

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
