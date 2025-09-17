package com.stepunlock.app.services

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GoogleFitService(private val context: Context) {
    
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .build()
    
    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    
    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    
    suspend fun isConnected(): Boolean = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                true
            } else {
                // Need to request permissions
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getTodaySteps(): Int = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: return@withContext 0
            
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                return@withContext 0
            }
            
            val endTime = Calendar.getInstance()
            val startTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime.timeInMillis, endTime.timeInMillis, TimeUnit.MILLISECONDS)
                .build()
            
            val response = Tasks.await(
                Fitness.getHistoryClient(context, account)
                    .readData(readRequest)
            )
            
            var totalSteps = 0
            for (dataSet in response.dataSets) {
                for (dataPoint in dataSet.dataPoints) {
                    for (field in dataPoint.dataType.fields) {
                        if (field.name == Field.FIELD_STEPS.name) {
                            totalSteps += dataPoint.getValue(field).asInt()
                        }
                    }
                }
            }
            
            totalSteps
        } catch (e: Exception) {
            0
        }
    }
    
    suspend fun getStepsForDate(date: Date): Int = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: return@withContext 0
            
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                return@withContext 0
            }
            
            val calendar = Calendar.getInstance().apply { time = date }
            val startTime = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endTime = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            
            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime.timeInMillis, endTime.timeInMillis, TimeUnit.MILLISECONDS)
                .build()
            
            val response = Tasks.await(
                Fitness.getHistoryClient(context, account)
                    .readData(readRequest)
            )
            
            var totalSteps = 0
            for (dataSet in response.dataSets) {
                for (dataPoint in dataSet.dataPoints) {
                    for (field in dataPoint.dataType.fields) {
                        if (field.name == Field.FIELD_STEPS.name) {
                            totalSteps += dataPoint.getValue(field).asInt()
                        }
                    }
                }
            }
            
            totalSteps
        } catch (e: Exception) {
            0
        }
    }
    
    fun getSignInIntent() = googleSignInClient.signInIntent
    
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            Tasks.await(googleSignInClient.signOut())
        } catch (e: Exception) {
            // Handle error
        }
    }
}
