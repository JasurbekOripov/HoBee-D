package uz.glight.hobee.distribuition.services.record

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.ulugbek.ibragimovhelpers.helpers.commons.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.EventBus
import uz.glight.hobee.distribuition.BuildConfig
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.activities.SplashScreen
import java.io.File
import java.util.*


class RecordService : Service() {
    private val AMPLITUDE_UPDATE_MS = 75L
    private var currFilePath = ""
    private var duration = 0
    private var status = RECORDING_STOPPED
    private var durationTimer = Timer()
    private var amplitudeTimer = Timer()
    private var recorder: MediaRecorder? = null
    private var docId: Int? = -1;
    private var job: CoroutineScope? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        job = CoroutineScope(Job() + Dispatchers.IO)
        docId = intent.extras?.getInt("doctor_id")

        when (intent.action) {
            GET_RECORDER_INFO -> broadcastRecorderInfo()
            STOP_AMPLITUDE_UPDATE -> amplitudeTimer.cancel()
            TOGGLE_PAUSE -> togglePause()
            STOP_SERVICE -> {
                stopRecording()
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(RECORDER_RUNNING_NOTIF_ID)
                stopSelf()
            }
            else -> startRecording()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        sendRecord(docId!!)
    }

    // mp4 output format with aac encoding should produce good enough m4a files according to https://stackoverflow.com/a/33054794/1967672
    private fun startRecording() {

        val baseFolder = if (isQPlus()) {
            cacheDir
        } else {
            val defaultFolder = File("${externalCacheDir?.absolutePath}/Audios/")
            if (!defaultFolder.exists()) {
                defaultFolder.mkdir()
            }

            defaultFolder.absolutePath
        }

        currFilePath = "$baseFolder/${getCurrentFormattedDateTime()}.mp3"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)

            try {
//                if (!isQPlus() && isPathOnSD(currFilePath)) {
//                    var document = getDocumentFile(currFilePath.getParentPath())
//                    document = document?.createFile("", currFilePath.getFilenameFromPath())
//
//                    val outputFileDescriptor = contentResolver.openFileDescriptor(document!!.uri, "w")!!.fileDescriptor
//                    setOutputFile(outputFileDescriptor)
//                } else {
//                    setOutputFile(currFilePath)
//                }
                setOutputFile(currFilePath)


                prepare()
                start()
                duration = 0
                status = RECORDING_RUNNING
                broadcastRecorderInfo()
                startForeground(RECORDER_RUNNING_NOTIF_ID, showNotification())

//                durationTimer = Timer()
//                durationTimer.scheduleAtFixedRate(getDurationUpdateTask(), 1000, 1000)

//                startAmplitudeUpdates()
            } catch (e: Exception) {
                showErrorToast(e)
                stopRecording()
            }
        }
    }

    private fun stopRecording() {
        durationTimer.cancel()
        amplitudeTimer.cancel()
        status = RECORDING_STOPPED

        recorder?.apply {
            try {
                stop()
                release()

//                ensureBackgroundThread {
//                    if (isQPlus()) {
//                        addFileInNewMediaStore()
//                    } else {
//                        addFileInLegacyMediaStore()
//                    }
                EventBus.getDefault().post(RecordEvents.RecordingCompleted())
//                }
            } catch (e: Exception) {
                showErrorToast(e)
            }
        }
        recorder = null
    }

    private fun broadcastRecorderInfo() {
//        broadcastDuration()
        broadcastStatus()
//        startAmplitudeUpdates()
    }

//    private fun startAmplitudeUpdates() {
//        amplitudeTimer.cancel()
//        amplitudeTimer = Timer()
////        amplitudeTimer.scheduleAtFixedRate(getAmplitudeUpdateTask(), 0, AMPLITUDE_UPDATE_MS)
//    }

    @SuppressLint("NewApi")
    private fun togglePause() {
        try {
            if (status == RECORDING_RUNNING) {
                recorder?.pause()
                status = RECORDING_PAUSED
            } else if (status == RECORDING_PAUSED) {
                recorder?.resume()
                status = RECORDING_RUNNING
            }
            broadcastStatus()
            startForeground(RECORDER_RUNNING_NOTIF_ID, showNotification())
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    @SuppressLint("InlinedApi")
    private fun addFileInNewMediaStore() {
        val audioCollection =
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val storeFilename = currFilePath.getFilenameFromPath()

        val newSongDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, storeFilename)
            put(MediaStore.Audio.Media.TITLE, storeFilename)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, currFilePath)
        }

        val newUri = contentResolver.insert(audioCollection, newSongDetails)
        if (newUri == null) {
            toast("R.string.unknown_error_occurred")
            return
        }

//        try {
//            val outputStream = contentResolver.openOutputStream(newUri)
//            val inputStream = getFileInputStreamSync(currFilePath)
//            inputStream!!.copyTo(outputStream!!, DEFAULT_BUFFER_SIZE)
//            recordingSavedSuccessfully(true)
//        } catch (e: Exception) {
//            showErrorToast(e)
//        }
    }

    private fun addFileInLegacyMediaStore() {
        MediaScannerConnection.scanFile(
            this,
            arrayOf(currFilePath),
            arrayOf(currFilePath.getMimeType())
        ) { _, _ -> recordingSavedSuccessfully(false) }
    }

    private fun recordingSavedSuccessfully(showFilenameOnly: Boolean) {
        val title = if (showFilenameOnly) currFilePath.getFilenameFromPath() else currFilePath
//        val msg = String.format(getString(R.string.recording_saved_successfully), title)
        toast(title, Toast.LENGTH_LONG)
    }

//    private fun getDurationUpdateTask() = object : TimerTask() {
//        override fun run() {
//            if (status == RECORDING_RUNNING) {
//                duration++
//                broadcastDuration()
//            }
//        }
//    }

//    private fun getAmplitudeUpdateTask() = object : TimerTask() {
//        override fun run() {
//            if (recorder != null) {
//                try {
//                    EventBus.getDefault()
//                        .post(Events.RecordingAmplitude(recorder!!.maxAmplitude))
//                } catch (ignored: Exception) {
//                }
//            }
//        }
//    }

    private fun showNotification(): Notification {
//        val hideNotification = config.hideNotification
        val channelId = "simple_recorder"
        val label = getString(R.string.app_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
//                if (hideNotification) NotificationManager.IMPORTANCE_MIN else NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelId, label, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

        var priority =
            if (isOreoPlus()) NotificationManager.IMPORTANCE_DEFAULT else NotificationCompat.PRIORITY_DEFAULT
        var icon = R.drawable.circle_white
        var title = label
        var visibility = NotificationCompat.VISIBILITY_PUBLIC
        var text = "Беседа в процессе"
//        if (status == RECORDING_PAUSED) {
//            text += " (${getString(R.string.paused)})"
//        }

//        if (hideNotification) {
//            priority = Notification.PRIORITY_MIN
//            icon = R.drawable.ic_empty
//            title = ""
//            text = ""
//            visibility = NotificationCompat.VISIBILITY_SECRET
//        }

        val stopSelf = Intent(this, RecordService::class.java)
        stopSelf.action = STOP_SERVICE
        stopSelf.putExtra("doctor_id", docId)
        val pStopSelf =
            PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(icon)
            .setContentIntent(getOpenAppIntent())
            .setPriority(priority)
            .setVisibility(visibility)
            .setSound(null)
            .setOngoing(true)
            .setAutoCancel(true)
            .setChannelId(channelId)
            .addAction(R.drawable.circle_stroke, "Завершить", pStopSelf)

        return builder.build()
    }

    private fun getOpenAppIntent(): PendingIntent {
        val intent =
            getLaunchIntent(BuildConfig.APPLICATION_ID) ?: Intent(this, SplashScreen::class.java)
        return PendingIntent.getActivity(
            this,
            RECORDER_RUNNING_NOTIF_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

//    private fun broadcastDuration() {
//        EventBus.getDefault().post(Events.RecordingDuration(duration))
//    }

    private fun broadcastStatus() {
        EventBus.getDefault().post(RecordEvents.RecordingStatus(status))
    }


    private fun sendRecord(id: Int) {
        val file = File(currFilePath)
        if (!file.exists()) {
            return;
        }
        try {
            val map: MutableMap<String, RequestBody> = HashMap<String, RequestBody>();

            val fileBody: RequestBody = file.asRequestBody("audio/mpeg".toMediaTypeOrNull());
            map.put("file\"; filename=\"${currFilePath.getFilenameFromPath()}\"", fileBody);
            map.put(
                "discussion_date",
                RemoteRepository.toRequestBody(getCurrentFormattedDateTime())
            );
            map.put("doctor_id", RemoteRepository.toRequestBody(id.toString()));


            job?.launch {
                val res = RemoteRepository.sendRecord(map)
                if (res.isSuccessful) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
        }
    }
}