package com.alex.materialdiary.sys.common

import com.alex.materialdiary.sys.common.models.ClassicBody
import com.alex.materialdiary.sys.common.models.PDAnswer
import com.alex.materialdiary.sys.common.models.PDBody
import com.alex.materialdiary.sys.common.models.UserInfoRequest
import com.alex.materialdiary.sys.common.models.all_periods.AllPeriods
import com.alex.materialdiary.sys.common.models.diary_day.DiaryDay
import com.alex.materialdiary.sys.common.models.get_user.UserInfo
import com.alex.materialdiary.sys.common.models.period_marks.PeriodMarks
import com.alex.materialdiary.sys.common.models.periods.Periods
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PskoveduEndpoints {
    @POST("journals/diaryday")
    suspend fun getDiaryDay(@Body body: ClassicBody?): DiaryDay?

    @POST("journals/allmarks")
    suspend fun getAllMarks(@Body body: ClassicBody?): PeriodMarks?

    @POST("journals/marksbyperiod")
    suspend fun getPeriodMarks(@Body body: ClassicBody?): PeriodMarks?

    @POST("journals/allperiods")
    suspend fun getPeriods(@Body body: ClassicBody?): AllPeriods?

    @POST("journals/periodmarks")
    suspend fun getItogMarks(@Body body: ClassicBody?): Periods?

    @POST("pda/setpdakey")
    suspend fun setPda(@Body body: PDBody?): PDAnswer?

    @POST("pda/getpdakey")
    suspend fun getPda(@Body body: PDBody?): PDAnswer?

    @POST("journals/login")
    suspend fun getUserInfo(@Body body: UserInfoRequest?): UserInfo?
}