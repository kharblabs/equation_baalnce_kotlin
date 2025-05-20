package com.kharblabs.equationbalancer2.search_files


import androidx.room.*

@Entity(
    tableName = "compounds"
)
data class Compound(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "occurrence_count") val occurrenceCount: Int
)

@Entity(
    tableName = "reactions",
    indices = [Index(value = ["reaction_string"], unique = true)]
)
data class Reaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "reaction_string") val reactionString: String
)

@Entity(
    tableName = "product_index",
    primaryKeys = ["product_key", "reaction_id"],
    foreignKeys = [ForeignKey(
        entity = Reaction::class,
        parentColumns = ["id"],
        childColumns = ["reaction_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["product_key"]),
        Index(value = ["reaction_id"])
    ]
)
data class ProductIndex(
    @ColumnInfo(name = "product_key") val productKey: String,
    @ColumnInfo(name = "reaction_id") val reactionId: Int
)

@Entity(
    tableName = "reagent_index",
    primaryKeys = ["reagent_key", "reaction_id"],
    foreignKeys = [ForeignKey(
        entity = Reaction::class,
        parentColumns = ["id"],
        childColumns = ["reaction_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["reagent_key"]),
        Index(value = ["reaction_id"])
    ]
)
data class ReagentIndex(
    @ColumnInfo(name = "reagent_key") val reagentKey: String,
    @ColumnInfo(name = "reaction_id") val reactionId: Int
)