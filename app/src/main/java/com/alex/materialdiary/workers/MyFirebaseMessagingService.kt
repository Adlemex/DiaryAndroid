package com.alex.materialdiary.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alex.materialdiary.MainActivity
import com.alex.materialdiary.R
import com.alex.materialdiary.keywords
import com.alex.materialdiary.sys.ReadWriteJsonFileUtils
import com.alex.materialdiary.sys.net.PskoveduApi
import com.alex.materialdiary.sys.net.models.all_periods.AllPeriods
import com.alex.materialdiary.utils.MarksTranslator
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.joda.time.format.DateTimeFormat
import xdroid.toaster.Toaster.toast
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager
    private var marksJob: Job? = null
    lateinit var api: PskoveduApi
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        api = PskoveduApi.getInstance(baseContext)
        if (remoteMessage.data.get("type") == "kr"){
            val cuurent_date = Date(Calendar.getInstance().time.time + 86400000)
            getDay(cuurent_date.toString())
        }
        if (remoteMessage.data.get("type") == "marks"){
            marks()
        }
    }

    override fun onDestroy() {
        marksJob?.cancel()
        toast("canceled")
        super.onDestroy()
    }
    fun getDay(date: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val lesson = api.getDay(date)?.data ?: return@launch
            val lessns = mutableListOf<String>()
            for (lsn in lesson) {
                val finded = mutableListOf<String>()
                if (lsn.homeworkPrevious?.homework != null) {
                    val c = lsn.homeworkPrevious!!.homework!!
                    finded += check_kr(c)
                }
                if (lsn.topic != null) {
                    finded += check_kr(lsn.topic!!)
                }
                if (finded.size > 0){
                    lsn.subjectName?.let { lessns.add(it) }
                }
            }
            val no_dubls = lessns.distinct()
            if (lessns.size > 0) {
                val intent = Intent(this@MyFirebaseMessagingService, MainActivity::class.java)
                intent.putExtra("navigate", "kr")
                val pendingIntent = PendingIntent.getActivity(
                    this@MyFirebaseMessagingService,
                    123, intent, PendingIntent.FLAG_IMMUTABLE
                )
                val builder: NotificationCompat.Builder = NotificationCompat.Builder(baseContext, "kr")
                    .setSmallIcon(R.drawable.ic_baseline_error_outline_24)
                    .setContentTitle("Контрольные!")
                    .setContentText(
                        "Подготовься, завтра могут быть контрольные по ${
                            baseContext.resources.getQuantityString(
                                R.plurals.kr,
                                no_dubls.size,
                                no_dubls.size
                            )
                        }"
                    )
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(1233, builder.build())
            }
        }
    }
    fun check_kr(str: String): MutableList<String> {
        val finded = mutableListOf<String>()
        keywords.kr_mini.forEach {
            if (str.contains(it)) {
                finded.add(it)
            }
        }
        keywords.kr_maybe.forEach {
            if (str.contains(it)) {
                finded.add(it)
            }
        }
        keywords.kr.forEach {
            if (str.contains(it)) {
                finded.add(it)
            }
        }
        return finded
    }
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigate", "kr")
        val pendingIntent = PendingIntent.getActivity(
            this,
            123, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "adsad"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder  = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_message_24)
            .setContentText(remoteMessage.data.get("kr"))
            .setAutoCancel(true)
            .setContentText("dsdjdhjdjhdhjdhj    " + remoteMessage.data.toString())
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        // Необходим канал уведомлений Android Oreo
        // Необходим канал уведомлений Android Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(123, notificationBuilder.build())
    }
    fun marks(){
        marksJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val utils = ReadWriteJsonFileUtils(baseContext)
                val readed = utils.readJsonFileData("periods.json")
                if (readed == null || readed.length < 75) {
                    return@launch
                }
                val listType = object : TypeToken<AllPeriods?>() {}.type
                val periods =  Gson().fromJson<AllPeriods>(readed, listType)
                val cur_per = MarksTranslator.get_cur_period(periods.data)
                val formatter = DateTimeFormat.forPattern("dd.MM.yyyy")
                val (marks, _) = api.getPeriodMarks(cur_per[0].toString(formatter), cur_per[1].toString(formatter))
                var diffs = 0
                marks?.data?.let { MarksTranslator(it).items }?.let { items ->
                    items.forEach {
                        diffs += MarksTranslator.getSubjectMarksDifferences(
                            baseContext, it.name,
                            items
                        ).size
                    }
                }
                if (diffs <= 0) return@launch
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.putExtra("navigate", "marks")
                val pendingIntent = PendingIntent.getActivity(
                    baseContext,
                    1233, intent, PendingIntent.FLAG_IMMUTABLE
                )
                val builder: NotificationCompat.Builder = NotificationCompat.Builder(baseContext, "marks")
                    .setSmallIcon(R.drawable.ic_baseline_looks_5_24)
                    .setContentTitle("Новые оценки!")
                    .setContentText(
                        baseContext.resources.getQuantityString(R.plurals.marks, diffs, diffs)
                    )
                    .setOnlyAlertOnce(true)
                    .setNumber(diffs)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(122, builder.build())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}