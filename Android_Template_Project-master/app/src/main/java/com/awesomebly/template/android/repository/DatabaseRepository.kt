package com.awesomebly.template.android.repository

import android.content.Context
import android.provider.SyncStateContract.Helpers.insert
import com.awesomebly.template.android.database.dao.TpDao
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.database.handler.DatabaseHandler
import com.awesomebly.template.android.database.handler.DatabaseResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : DatabaseRepository
 * Date : 2021-05-03, 오후 3:03
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */
class DatabaseRepository @Inject constructor( //@Inject -> 의존성 주입을 받겠다는 의미, Hilt 컴포넌트에 의해 데이터베이스 객체를 전달받게 됨.
    private val appContext: Context,
    private val databaseHandler: DatabaseHandler,
    private val tpDao: TpDao
) {

    /**
     * Insert tp
     * TpEntity 하나를 DB에 저장
     * @param tp
     * @return
     */
    suspend fun insertTp(tp: TpEntity): DatabaseResult<Long> {
        return try {
            databaseHandler.handleSuccess(tpDao.insert(tp))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    /**
     * Insert all tp
     * 가변인자를 사용하여 원하는 개수의 TpEntity를 DB에 저장
     * @param tps
     * @return
     */
    suspend fun insertAllTp(vararg tps: TpEntity): DatabaseResult<Unit> {
        return try {
            databaseHandler.handleSuccess(tpDao.insertAll(*tps))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    /**
     * Select tp by name
     * 지정한 이름과 매칭되는 TpEntity를 리스트로 가져온다
     * @param name
     * @return
     */
    suspend fun selectTpByName(name: String): DatabaseResult<List<TpEntity?>> {
        return try {
            databaseHandler.handleSuccess(tpDao.selectListByName(name))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    /**
     * Select tp all
     * DB에 저장되어 있는 모든 TpEntity를 조회한다
     * @return
     */
    suspend fun selectTpAll(): DatabaseResult<List<TpEntity?>> {
        return try {
            databaseHandler.handleSuccess(tpDao.selectAll())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    /**
     * Delete all tp
     * TpEntity 테이블의 모든 내용 삭제한다
     * @return
     */
    suspend fun deleteAllTp(): DatabaseResult<Unit> {
        return try {
            databaseHandler.handleSuccess(tpDao.deleteAll())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

}