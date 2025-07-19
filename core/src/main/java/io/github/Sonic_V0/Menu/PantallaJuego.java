package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import io.github.Sonic_V0.*;

public class PantallaJuego extends BaseMenu {
    //Box2DDebugRenderer debugRenderer;
    private final Mundo mundo;
    private ShapeRenderer shape;

    // Pausa
    private boolean enPausa = false;
    private float alphaPausa = 0f;

    public PantallaJuego(Main game) {
        super(game);
       // debugRenderer = new Box2DDebugRenderer();
        mundo = new Mundo();
    }

    @Override
    public void show() {
        shape = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            enPausa = !enPausa;
            if (enPausa) alphaPausa = 0f;
        }

        if (!enPausa) {
            mundo.actualizar(delta);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camara.getCamara().update();
        mundo.renderizarMapa(camara.getCamara());

        batch.setProjectionMatrix(camara.getCamara().combined);

     //  debugRenderer.render(mundo.getWorld(), camara.getCamara().combined);
        batch.begin();
        mundo.render(batch);
        batch.end();

        if (enPausa) {
            if (alphaPausa < 1f) {
                alphaPausa += delta * 2f;
                if (alphaPausa > 1f) alphaPausa = 1f;
            }

            shape.setProjectionMatrix(camara.getCamara().combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(0, 0, 0, 0.6f * alphaPausa);
            Rectangle vista = camara.getVistaRectangular();
            shape.rect(vista.x, vista.y, vista.width, vista.height);
            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            if (alphaPausa >= 0.8f) {
                float cx = camara.getCamara().position.x - 6f / 2f;
                float cy1 = camara.getCamara().position.y + 3f;  // SEGUIR
                float cy3 = camara.getCamara().position.y;      // MENÚ
                float cy2 = camara.getCamara().position.y - 3f; // SALIR

                Vector3 screenPos1 = camara.getProject(cx, cy1, 0);
                Vector3 screenPos2 = camara.getProject(cx, cy2, 0);
                Vector3 screenPos3 = camara.getProject(cx, cy3, 0);

                batch.setProjectionMatrix(batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                batch.begin();

                // Dibujar botones
                batch.draw(boton, screenPos1.x, screenPos1.y, 250, 60); // SEGUIR
                batch.draw(boton, screenPos3.x, screenPos3.y, 250, 60); // MENÚ
                batch.draw(boton, screenPos2.x, screenPos2.y, 250, 60); // SALIR

                font.setColor(1, 1, 1, 1);

                layout.setText(font, "SEGUIR");
                font.draw(batch, "SEGUIR",
                    screenPos1.x + botonW / 2f - layout.width / 2f,
                    screenPos1.y + botonH / 2f + layout.height / 2f);

                layout.setText(font, "MENÚ");
                font.draw(batch, "MENÚ",
                    screenPos3.x + botonW / 2f - layout.width / 2f,
                    screenPos3.y + botonH / 2f + layout.height / 2f);

                layout.setText(font, "SALIR");
                font.draw(batch, "SALIR",
                    screenPos2.x + botonW / 2f - layout.width / 2f,
                    screenPos2.y + botonH / 2f + layout.height / 2f);

                batch.end();

                if (Gdx.input.justTouched()) {
                    Vector3 click = camara.getCamara().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                    float mx = click.x;
                    float my = click.y;

                    if (mx >= cx && mx <= cx + botonW) {
                        if (my >= cy1 && my <= cy1 + botonH) {
                            enPausa = false;
                        } else if (my >= cy3 && my <= cy3 + botonH) {
                            game.setScreen(new MenuPrincipal(game)); // ← Regresa al menú
                            dispose();
                        } else if (my >= cy2 && my <= cy2 + botonH) {
                            Gdx.app.exit();
                        }
                    }
                }
            }
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        font.dispose();
        boton.dispose();
        mundo.dispose();
    }
}
