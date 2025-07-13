package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import io.github.Sonic_V0.*;
import io.github.Sonic_V0.Personajes.Etapa;
import io.github.Sonic_V0.Personajes.Sonic;

public class PantallaJuego extends BaseMenu {
    private float botonPixelW;
    private float botonPixelH;
    private final float botonW = 6f;
    private final float botonH = 2f;
    private final Sonic sonic;
    private final Mundo mundo;
    private final CargarMapa map;
    private final Camara camara;
    private ShapeRenderer shape;
    private final Etapa etapa; // <-- NUEVO: clase que genera y maneja los robots

    // Pausa
    private boolean enPausa = false;
    private float alphaPausa = 0f;

    public PantallaJuego(Main game) {
        super(game);
        map = new CargarMapa("Mapa1/mapa.tmx", 0.039f);
        camara = new Camara();
        mundo = new Mundo();
        sonic = new Sonic(mundo.crearCuerpo(new Vector2(20f, 10f))); // Posición inicial
        etapa = new Etapa(mundo, sonic); // <-- Se instancia etapa en lugar de robot único
        mundo.objetosMapa(map.getMap());
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
            sonic.actualizar(delta);
            mundo.actualizar(delta);
            etapa.actualizar(delta); // <-- Actualiza todos los robots generados
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.renderarMapa(camara.getCamara());

        batch.setProjectionMatrix(camara.getCamara().combined);
        batch.begin();
        sonic.render(batch);
        etapa.renderizar(batch); // <-- Dibuja todos los robots generados
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
                float cx = camara.getCamara().position.x - botonW / 2f;
                float cy1 = camara.getCamara().position.y + 3f;  // SEGUIR
                float cy3 = camara.getCamara().position.y;      // MENÚ
                float cy2 = camara.getCamara().position.y - 3f; // SALIR

                Vector3 screenPos1 = camara.getProject(cx, cy1, 0);
                Vector3 screenPos2 = camara.getProject(cx, cy2, 0);
                Vector3 screenPos3 = camara.getProject(cx, cy3, 0);

                batch.setProjectionMatrix(batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                batch.begin();

                batch.draw(boton, screenPos1.x, screenPos1.y, botonPixelW, botonPixelH); // SEGUIR
                batch.draw(boton, screenPos3.x, screenPos3.y, botonPixelW, botonPixelH); // MENÚ
                batch.draw(boton, screenPos2.x, screenPos2.y, botonPixelW, botonPixelH); // SALIR

                font.setColor(1, 1, 1, 1);

                layout.setText(font, "SEGUIR");
                font.draw(batch, "SEGUIR",
                    screenPos1.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos1.y + botonPixelH / 2f + layout.height / 2f);

                layout.setText(font, "MENÚ");
                font.draw(batch, "MENÚ",
                    screenPos3.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos3.y + botonPixelH / 2f + layout.height / 2f);

                layout.setText(font, "SALIR");
                font.draw(batch, "SALIR",
                    screenPos2.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos2.y + botonPixelH / 2f + layout.height / 2f);

                batch.end();

                if (Gdx.input.justTouched()) {
                    Vector3 click = camara.getCamara().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                    float mx = click.x;
                    float my = click.y;

                    if (mx >= cx && mx <= cx + botonW) {
                        if (my >= cy1 && my <= cy1 + botonH) {
                            enPausa = false;
                        } else if (my >= cy3 && my <= cy3 + botonH) {
                            game.setScreen(new MenuPrincipal(game));
                            dispose();
                        } else if (my >= cy2 && my <= cy2 + botonH) {
                            Gdx.app.exit();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        botonPixelW = (int) (botonW * (width / camara.getCamara().viewportWidth));
        botonPixelH = (int)(botonH * (height / camara.getCamara().viewportHeight));
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        font.dispose();
        sonic.dispose();
        map.dispose();
        boton.dispose();
        etapa.dispose(); // <-- Importante: liberar recursos de los robots
    }
}
