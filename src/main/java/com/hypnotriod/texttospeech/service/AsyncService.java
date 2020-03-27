package com.hypnotriod.texttospeech.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
public class AsyncService {

    public void startAsyncProcess(Runnable asyncProcess, Runnable runLaterProcess) {
        startAsyncProcess(asyncProcess, runLaterProcess, true);
    }

    public void startAsyncProcess(Runnable asyncProcess, Runnable runLaterProcess, boolean deamon) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(asyncProcess);
        executorService.execute(() -> {
            Platform.runLater(runLaterProcess);
        });
        executorService.shutdown();
    }
}
