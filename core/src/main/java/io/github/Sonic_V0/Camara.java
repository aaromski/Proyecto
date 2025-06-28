package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Camara {
    private final OrthographicCamera camara;
    public Camara() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 50f, 40f); // Ajusta según tu escala y resolución
        camara.update();
    }

    public OrthographicCamera getCamara() {
        return camara;
    }
}
