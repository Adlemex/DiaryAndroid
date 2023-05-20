package com.alex.materialdiary.sys.net

import com.alex.materialdiary.sys.net.models.ClassicBody
import com.alex.materialdiary.sys.net.models.pda.PDAnswer
import com.alex.materialdiary.sys.net.models.pda.PDBody
import com.alex.materialdiary.sys.net.models.get_user.UserInfoRequest
import com.alex.materialdiary.sys.net.models.all_periods.AllPeriods
import com.alex.materialdiary.sys.net.models.diary_day.DiaryDay
import com.alex.materialdiary.sys.net.models.get_user.UserInfo
import com.alex.materialdiary.sys.net.models.period_marks.PeriodMarks
import com.alex.materialdiary.sys.net.models.periods.Periods
import retrofit2.http.Body
import retrofit2.http.POST

interface PskoveduEndpoints {
    @POST("journals/diaryday")
    suspend fun getDiaryDay(@Body body: ClassicBody?): DiaryDay?

    @POST("journals/allmarks") // get all marks, equal: marksbyperiod without from, to args
    suspend fun getAllMarks(@Body body: ClassicBody?): PeriodMarks?

    @POST("journals/marksbyperiod") // get marks in period
    suspend fun getPeriodMarks(@Body body: ClassicBody?): PeriodMarks?

    @POST("journals/allperiods") // get periods
    suspend fun getPeriods(@Body body: ClassicBody?): AllPeriods?

    @POST("journals/periodmarks") // itog
    suspend fun getItogMarks(@Body body: ClassicBody?): Periods?

    @POST("pda/setpdakey")
    suspend fun setPda(@Body body: PDBody?): PDAnswer?

    @POST("pda/getpdakey")
    suspend fun getPda(@Body body: PDBody?): PDAnswer?

    @POST("journals/login")
    suspend fun getUserInfo(@Body body: UserInfoRequest?): UserInfo?
}