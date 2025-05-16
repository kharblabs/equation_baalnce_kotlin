package com.kharblabs.equationbalancer2.search_files

import androidx.room.*

@Dao
interface CompoundDao {
    @Query("SELECT * FROM compounds")
     fun getAllCompounds(): List<Compound>

    @Query("SELECT * FROM compounds WHERE name = :name")
     fun getCompoundByName(name: String): Compound?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(compound: Compound)

    @Query("SELECT * FROM compounds ORDER BY occurrence_count DESC LIMIT :limit")
     fun getMostCommonCompounds(limit: Int): List<Compound>
}
@Dao
interface ReactionDao {
    @Query("SELECT * FROM reactions")
     fun getAllReactions(): List<Reaction>

    @Query("SELECT * FROM reactions WHERE id = :id")
     fun getReactionById(id: Int): Reaction?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insert(reaction: Reaction): Long



    @Query("SELECT * FROM reactions WHERE id IN (:ids)")
     fun getReactionsByIds(ids: List<Int>): List<Reaction>

    @Query("SELECT * FROM reactions WHERE LOWER(reaction_string) LIKE '%' || LOWER(:searchQuery) || '%'")
     fun searchReactions(searchQuery: String): List<Reaction>
}
@Dao
interface ProductIndexDao {
    @Query("SELECT * FROM product_index WHERE product_key = :productKey")
     fun getReactionsForProduct(productKey: String): List<ProductIndex>

    @Query("SELECT * FROM product_index WHERE reaction_id = :reactionId")
     fun getProductsForReaction(reactionId: Int): List<ProductIndex>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(productIndex: ProductIndex)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAll(productIndices: List<ProductIndex>)

    @Query("""
        SELECT r.* FROM reactions r
        INNER JOIN product_index pi ON r.id = pi.reaction_id
        WHERE pi.product_key = :productKey
    """)
     fun findReactionsProducingCompound(productKey: String): List<Reaction>
    @Query("SELECT DISTINCT product_key FROM product_index WHERE LOWER(product_key) LIKE '%' || LOWER(:query) || '%'")
     fun findMatchingProductKeys(query: String): List<String>

    @Query("SELECT DISTINCT reaction_id FROM product_index WHERE product_key IN (:keys)")
     fun getReactionIdsForProductKeys(keys: List<String>): List<Int>
}

@Dao
interface ReagentIndexDao {
    @Query("SELECT * FROM reagent_index WHERE reagent_key = :reagentKey")
     fun getReactionsForReagent(reagentKey: String): List<ReagentIndex>

    @Query("SELECT * FROM reagent_index WHERE reaction_id = :reactionId")
     fun getReagentsForReaction(reactionId: Int): List<ReagentIndex>
    @Query("SELECT DISTINCT reaction_id FROM product_index WHERE product_key IN (:keys)")
     fun getReactionIdsForProductKeys(keys: List<String>): List<Int>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(reagentIndex: ReagentIndex)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAll(reagentIndices: List<ReagentIndex>)

    @Query("""
    SELECT r.* FROM reactions r
    INNER JOIN reagent_index ri ON r.id = ri.reaction_id
    WHERE LOWER(ri.reagent_key) LIKE '%' || LOWER(:reagentKey) || '%'
     """)
     fun findReactionsUsingReagent(reagentKey: String): List<Reaction>
    // Step 1: Find matching reagent keys (partial match)
    @Query("SELECT DISTINCT reagent_key FROM reagent_index WHERE LOWER(reagent_key) LIKE '%' || LOWER(:query) || '%'")
     fun findMatchingReagentKeys(query: String): List<String>

    // Step 2: Get reaction IDs for each matched key
    @Query("SELECT DISTINCT reaction_id FROM reagent_index WHERE reagent_key IN (:keys)")
     fun getReactionIdsForReagentKeys(keys: List<String>): List<Int>

}