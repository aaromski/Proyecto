package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import io.github.Sonic_V0.*;
import io.github.Sonic_V0.Mundo.Mundo;

/**
 * Clase que representa la pantalla principal del juego donde se lleva a cabo la acción.
 * Gestiona la lógica de pausa, renderizado de mapa y sprites, así como el
 * manejo de transición a otras pantallas como el menú o fin de juego.
 *
 *@author Miguel Carreño
 */
public class PantallaJuego extends BaseMenu {

    /** Mundo físico y visual del juego. */
    private final Mundo mundo;

    /** Renderizador de formas utilizado para mostrar la superposición de pausa. */
    private ShapeRenderer shape;

    /** Indica si el juego se encuentra en pausa. */
    private boolean enPausa = false;

    /** Transición gradual de opacidad para la superposición de pausa. */
    private float alphaPausa = 0f;

    /**
     * Constructor que inicializa la pantalla de juego con el contexto principal.
     *
     * @param game instancia principal del juego
     */
    public PantallaJuego(Main game) {
        super(game);
        mundo = new Mundo();
    }

    /**
     * Se llama una vez al mostrar esta pantalla. Inicializa el renderizador de formas.
     */
    @Override
    public void show() {
        shape = new ShapeRenderer();
    }

    /**
     * Método principal de renderizado que se llama en cada fotograma.
     *
     * @param delta tiempo transcurrido desde el último fotograma
     */
    @Override
    public void render(float delta) {
        // Toggle de pausa con la tecla ESCAPE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            enPausa = !enPausa;
            if (enPausa) alphaPausa = 0f;
        }

        // Actualiza el mundo si no está pausado
        if (!enPausa) {
            mundo.actualizar(delta);
        }

        // Verifica condición de fin de juego
        if (Constantes.VIDAS[0] <= 0 && Constantes.VIDAS[1] <= 0 && Constantes.VIDAS[2] <= 0) {
            game.setScreen(new PantallaFin(game));
            dispose();
            return;
        }

        // Limpieza de la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderiza mapa y entidades del mundo
        camara.getCamara().update();
        mundo.renderizarMapa(camara.getCamara());

        batch.setProjectionMatrix(camara.getCamara().combined);
        batch.begin();
        mundo.render(batch);
        batch.end();

        // Si está pausado, renderiza superposición y botones
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

            // Mostrar botones si la transición de opacidad está avanzada
            if (alphaPausa >= 0.8f) {
                // Posiciones de los botones en el mundo
                float cx = camara.getCamara().position.x - 6f / 2f;
                float cy1 = camara.getCamara().position.y + 3f;  // SEGUIR
                float cy3 = camara.getCamara().position.y;      // MENÚ
                float cy2 = camara.getCamara().position.y - 3f; // SALIR

                // Convertir posiciones a coordenadas de pantalla
                Vector3 screenPos1 = camara.getProject(cx, cy1, 0);
                Vector3 screenPos2 = camara.getProject(cx, cy2, 0);
                Vector3 screenPos3 = camara.getProject(cx, cy3, 0);

                batch.setProjectionMatrix(batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                batch.begin();

                // Dibujar botones de pausa
                batch.draw(boton, screenPos1.x, screenPos1.y, 250, 60);
                batch.draw(boton, screenPos3.x, screenPos3.y, 250, 60);
                batch.draw(boton, screenPos2.x, screenPos2.y, 250, 60);

                // Etiquetas de los botones
                font.setColor(1, 1, 1, 1);

                layout.setText(font, "SEGUIR");
                font.draw(batch, "SEGUIR", screenPos1.x + botonW / 2f - layout.width / 2f, screenPos1.y + botonH / 2f + layout.height / 2f);

                layout.setText(font, "MENÚ");
                font.draw(batch, "MENÚ", screenPos3.x + botonW / 2f - layout.width / 2f, screenPos3.y + botonH / 2f + layout.height / 2f);

                layout.setText(font, "SALIR");
                font.draw(batch, "SALIR", screenPos2.x + botonW / 2f - layout.width / 2f, screenPos2.y + botonH / 2f + layout.height / 2f);

                batch.end();

                // Manejo de entrada del usuario (click)
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

    /** Se llama cuando la aplicación se pausa (sin uso en esta clase). */
    @Override public void pause() {}

    /** Se llama cuando se reanuda la aplicación (sin uso en esta clase). */
    @Override public void resume() {}

    /** Se llama cuando esta pantalla ya no está visible (sin uso en esta clase). */
    @Override public void hide() {}

    /**
     * Libera los recursos utilizados por la pantalla.
     * Se llama automáticamente al cambiar de pantalla.
     */
    @Override
    public void dispose() {
        shape.dispose();
        mundo.dispose();
    }
}

