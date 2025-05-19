package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabaseInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Room kurulumu ve temel veri ekleme/okuma işlemini test eder.
 * Şimdilik DAO üzerinden direkt erişim sağlandı.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseInstanceTest {

    /**
     * Room'a kullanıcı eklenip LiveData üzerinden geri alınıyor.
     * runBlocking, suspend DAO çağrıları için kullanıldı.
     */
    @Test
    fun insertAndReadUser() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabaseInstance.getDatabase(context)
        val userDao = db.userDao()

        val user = User(
            id = 1,
            firstName = "Ali",
            lastName = "Kaya",
            age = 22,
            email = "ali@test.com"
        )

        userDao.addUser(user)

        val users = userDao.readAllData().getOrAwaitValue()

        assertTrue(users.any { it.email == "ali@test.com" })
    }
}

/**
 * Test ortamında LiveData'dan veri almayı sağlar.
 * observeForever main thread zorunlu olduğu için Handler kullanıldı.
 */
fun LiveData<List<User>>.getOrAwaitValue(): List<User> {
    var data: List<User>? = null
    val latch = CountDownLatch(1)

    val observer = object : Observer<List<User>> {
        override fun onChanged(value: List<User>) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    Handler(Looper.getMainLooper()).post {
        this.observeForever(observer)
    }

    if (!latch.await(2, TimeUnit.SECONDS)) {
        throw TimeoutException("LiveData değeri zamanında alınamadı.")
    }

    return data ?: emptyList()
}
