package re.usto.mosaic.engine;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * BackgroundService is a base class for {@link Service}s that contain objects that must remain
 * active even if there is no action being executed. Jobs are scheduled similarly to
 * {@link android.app.IntentService}, but asynchronous calls (that cannot be happen from within
 * IntentServices) are allowed here, since the context will remain active.
 *
 * <p>The goal of this BackgroundService class is not to hold explicit calls of
 * {@link android.os.AsyncTask}s, but to hold objects that are bound to implicit async jobs and
 * belong to the Service context rather then an activity and can't be defined from within the
 * Application class, or that is constantly exchanging information with the BackgroundService.
 *
 * <p>Enqueued jobs are ensured to be ran in a high priority worker thread, but the service logic
 * is ran on only one thread. The BackgroundService will remain running even if the process loses
 * focus and the screen goes off, allowing callbacks for endpoints, for example.
 *
 * <p>When extending BackgroundService, make sure to define a default constructor. The implemented
 * constructor expects the class name to name the worker thread, for debugging purposes.
 */

public abstract class BackgroundService extends Service {

    private String mName;
    private HandlerThread mWorkerThread;
    private BackgroundHandler mHandler;
    private PowerManager.WakeLock mWakeLock;

    private final class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onReceivedIntent((Intent)msg.obj);
        }
    }

    /**
     * Creates the BackgroundService
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundService(String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, mName);

        mWakeLock.acquire();

        mWorkerThread = new HandlerThread(mName, Process.THREAD_PRIORITY_FOREGROUND);
        mWorkerThread.setPriority(Thread.MAX_PRIORITY);
        mWorkerThread.start();
        mHandler = new BackgroundHandler(mWorkerThread.getLooper());
    }

    /**
     * You should not override this method for your BackgroundService. Instead,
     * override {@link #onReceivedIntent(Intent)}, which the system calls when the BackgroundService
     * receives a start request.
     * @see android.app.Service#onStartCommand
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Message msg = mHandler.obtainMessage();
        msg.obj = intent;
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWorkerThread.quitSafely();
        mWakeLock.release();
    }

    /**
     * This is the method that will receive intent messages from
     * {@link android.content.Context#startService(Intent)}.
     * Logic is similar to the one implemented by {@link android.app.IntentService}
     * @see android.app.IntentService#onHandleIntent(Intent)
     */
    @WorkerThread
    protected abstract void onReceivedIntent(@Nullable Intent intent);
}
