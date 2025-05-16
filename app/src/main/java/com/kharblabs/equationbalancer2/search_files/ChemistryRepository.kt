package com.kharblabs.equationbalancer2.search_files

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
/**
 * Repository class that provides clean APIs for accessing chemistry database data
 */class ChemistryRepository(private val database: AppDatabase) {
    private val compoundDao = database.compoundDao()
    private val reactionDao = database.reactionDao()
    private val productIndexDao = database.productIndexDao()
    private val reagentIndexDao = database.reagentIndexDao()

    // Compound operations
    fun getAllCompounds() = flow {
        emit(compoundDao.getAllCompounds())
    }.flowOn(Dispatchers.IO)

    fun getCompoundByName(name: String) = flow {
        emit(compoundDao.getCompoundByName(name))
    }.flowOn(Dispatchers.IO)

    fun getMostCommonCompounds(limit: Int) = flow {
        emit(compoundDao.getMostCommonCompounds(limit))
    }.flowOn(Dispatchers.IO)

    // Reaction operations
    fun getAllReactions() = flow {
        emit(reactionDao.getAllReactions())
    }.flowOn(Dispatchers.IO)

    fun getReactionById(id: Int) = flow {
        emit(reactionDao.getReactionById(id))
    }.flowOn(Dispatchers.IO)

    fun searchReactions(query: String) = flow {
        emit(reactionDao.searchReactions("%$query%"))
    }.flowOn(Dispatchers.IO)

    // Find reactions that produce a specific compound
    fun findReactionsByReagent(query: String): Flow<List<Reaction>> = flow {
        val keys = reagentIndexDao.findMatchingReagentKeys(query)
        if (keys.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val ids = reagentIndexDao.getReactionIdsForReagentKeys(keys)
        val reactions = reactionDao.getReactionsByIds(ids)
        emit(reactions)
    }.flowOn(Dispatchers.IO)

    fun findReactionsByProduct(query: String): Flow<List<Reaction>> = flow {
        val keys = productIndexDao.findMatchingProductKeys(query)
        if (keys.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val ids = productIndexDao.getReactionIdsForProductKeys(keys)
        val reactions = reactionDao.getReactionsByIds(ids)
        emit(reactions)
    }.flowOn(Dispatchers.IO)


    // Find reactions that use a specific reagent
    fun findReactionsUsingReagent(reagentName: String) = flow {
        emit(reagentIndexDao.findReactionsUsingReagent(reagentName))
    }.flowOn(Dispatchers.IO)

    // Find all products for a reaction
    suspend fun getProductsForReaction(reactionId: Int): List<String> {
        return productIndexDao.getProductsForReaction(reactionId).map { it.productKey }
    }

    // Find all reagents for a reaction
    suspend fun getReagentsForReaction(reactionId: Int): List<String> {
        return reagentIndexDao.getReagentsForReaction(reactionId).map { it.reagentKey }
    }
}