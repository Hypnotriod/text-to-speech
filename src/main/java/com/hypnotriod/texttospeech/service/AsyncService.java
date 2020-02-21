package com.hypnotriod.texttospeech.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
