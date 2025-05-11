package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

}
