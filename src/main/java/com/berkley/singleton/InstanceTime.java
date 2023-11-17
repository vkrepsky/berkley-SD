package com.berkley.singleton;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Singleton responsavel por Saver o tempo da instância atual
 */
public class InstanceTime {

    private static LocalDateTime time;
    private static InstanceTime instance;  // Instância estática privada

    // Construtor privado
    private InstanceTime() {
        Random random = new Random();

        // Utiliza aleatoriedade para desincronizar relógios
        int randomSeconds = random.nextInt(60);

        if (random.nextBoolean()) {
            time = LocalDateTime.now().plusSeconds(randomSeconds);
        } else {
            time = LocalDateTime.now().minusSeconds(randomSeconds);
        }
    }

    // Método público estático para obter a instância
    public static InstanceTime getInstance() {
        if (instance == null) {
            instance = new InstanceTime();
        }
        return instance;
    }

    public LocalDateTime randomizeTime(){
        Random random = new Random();

        // Utiliza aleatoriedade para desincronizar relógios
        int randomSeconds = random.nextInt(60);

        if (random.nextBoolean()) {
            time = time.plusSeconds(randomSeconds);
        } else {
            time = time.minusSeconds(randomSeconds);
        }

        return time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public static void setTime(LocalDateTime newTime) {
        time = newTime;
    }
}
